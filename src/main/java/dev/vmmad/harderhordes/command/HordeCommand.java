package dev.vmmad.harderhordes.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import dev.vmmad.harderhordes.config.HordeConfigHolder;
import dev.vmmad.harderhordes.horde.HordeManager;
import dev.vmmad.harderhordes.horde.difficulty.ProgressionScore;
import dev.vmmad.harderhordes.horde.difficulty.ProgressionScorer;
import dev.vmmad.harderhordes.horde.spawn.SpawnContext;
import dev.vmmad.harderhordes.horde.spawn.SpawnPositionFinder;
import dev.vmmad.harderhordes.horde.type.HordeDefinition;
import dev.vmmad.harderhordes.horde.type.HordeDefinitions;
import dev.vmmad.harderhordes.horde.type.HordeTypeRegistry;
import dev.vmmad.harderhordes.horde.type.HordeTypeSelector;
import dev.vmmad.harderhordes.horde.ward.WardSuppression;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Debug/testing command tree: {@code /harderhordes spawn <type>|hybrid},
 * {@code score}, and {@code list}. Built loader-agnostically; the loader adapter
 * registers it from its RegisterCommands hook.
 */
public final class HordeCommand {

    private static final SuggestionProvider<CommandSourceStack> TYPE_SUGGESTIONS = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(
                    HordeTypeRegistry.all().stream().map(HordeDefinition::id), builder);

    private HordeCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("harderhordes")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("spawn")
                        .then(Commands.literal("hybrid").executes(HordeCommand::spawnHybrid))
                        .then(Commands.argument("type", ResourceLocationArgument.id())
                                .suggests(TYPE_SUGGESTIONS)
                                .executes(HordeCommand::spawnType)))
                .then(Commands.literal("score").executes(HordeCommand::showScore))
                .then(Commands.literal("list").executes(HordeCommand::listDefinitions))
                .then(Commands.literal("recent").executes(HordeCommand::recent))
                .then(Commands.literal("ward").executes(HordeCommand::ward)));
    }

    private static int spawnType(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ResourceLocation id = ResourceLocationArgument.getId(ctx, "type");
        HordeDefinition def = HordeTypeRegistry.get(id);
        if (def == null) {
            ctx.getSource().sendFailure(Component.literal("Unknown horde type: " + id));
            return 0;
        }
        int spawned = HordeManager.forceSpawn(player.serverLevel(), player, def, false);
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Spawned horde '" + id + "' (" + spawned + " mobs)"), true);
        return spawned;
    }

    private static int spawnHybrid(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        HordeDefinition base = HordeTypeRegistry.get(HordeDefinitions.ZOMBIE_ID);
        if (base == null) {
            ctx.getSource().sendFailure(Component.literal("No base horde type registered."));
            return 0;
        }
        int spawned = HordeManager.forceSpawn(player.serverLevel(), player, base, true);
        ctx.getSource().sendSuccess(() -> Component.literal(
                "Spawned hybrid horde (" + spawned + " mobs)"), true);
        return spawned;
    }

    private static int showScore(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ProgressionScore score = ProgressionScorer.score(player.serverLevel(), player, HordeConfigHolder.get());
        ctx.getSource().sendSuccess(() -> Component.literal(String.format(
                "Progression score: %.2f  (days %.2f, difficulty %.2f, gear %.2f)",
                score.total(), score.dayScore(), score.difficultyScore(), score.gearScore())), false);
        return (int) Math.round(score.total());
    }

    private static int ward(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        boolean safe = WardSuppression.isProtected(player.serverLevel(), player.blockPosition(), HordeConfigHolder.get());
        ctx.getSource().sendSuccess(() -> Component.literal(safe
                ? "You are inside a bell-totem safe zone: hordes will not spawn on you here."
                : "You are NOT in a safe zone. Build a bell on a 3x3 platform of metal blocks (iron/gold/diamond/netherite) to make one."), false);
        return safe ? 1 : 0;
    }

    private static int recent(CommandContext<CommandSourceStack> ctx) {
        var history = HordeManager.recent();
        if (history.isEmpty()) {
            ctx.getSource().sendSuccess(() -> Component.literal("No hordes have spawned recently."), false);
            return 0;
        }
        long now = ctx.getSource().getLevel().getGameTime();
        StringBuilder sb = new StringBuilder("Recent hordes (newest first):\n");
        for (HordeManager.RecentHorde h : history) {
            sb.append(" - day ").append(h.worldDay()).append(' ').append(clock(h.dayTimeTicks()))
                    .append(" (").append(ago(now - h.gameTime())).append(")")
                    .append(": ").append(h.type()).append(h.hybrid() ? " (hybrid)" : "")
                    .append(" x").append(h.size())
                    .append(" near ").append(h.player())
                    .append(" @ ").append(h.x()).append(' ').append(h.y()).append(' ').append(h.z())
                    .append('\n');
        }
        ctx.getSource().sendSuccess(() -> Component.literal(sb.toString().stripTrailing()), false);
        return history.size();
    }

    /** Vanilla day-time ticks (0 = 06:00) → a HH:MM in-game clock. */
    private static String clock(long dayTimeTicks) {
        long minutesOfDay = (Math.floorMod(dayTimeTicks, 24000L) * 1440L / 24000L + 360L) % 1440L;
        return String.format("%02d:%02d", minutesOfDay / 60, minutesOfDay % 60);
    }

    /** Elapsed game ticks → a short "Xm ago" / "Xh Ym ago" real-time string (20 ticks = 1s). */
    private static String ago(long ticks) {
        if (ticks < 0) {
            ticks = 0;
        }
        long totalSeconds = ticks / 20L;
        long minutes = totalSeconds / 60L;
        if (minutes < 1) {
            return totalSeconds + "s ago";
        }
        if (minutes < 60) {
            return minutes + "m ago";
        }
        return (minutes / 60) + "h " + (minutes % 60) + "m ago";
    }

    private static int listDefinitions(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ServerLevel level = player.serverLevel();
        SpawnContext spawnCtx = SpawnPositionFinder.find(level, player.blockPosition(), HordeConfigHolder.get(),
                level.getRandom());
        boolean aquatic = spawnCtx != null && spawnCtx.aquatic();

        var matching = (spawnCtx != null)
                ? HordeTypeSelector.candidates(spawnCtx, HordeConfigHolder.get())
                : java.util.List.<HordeDefinition>of();

        StringBuilder sb = new StringBuilder("Horde definitions (context: ")
                .append(aquatic ? "water" : "land").append(")\n");
        for (HordeDefinition def : HordeTypeRegistry.all()) {
            sb.append(" - ").append(def.id())
                    .append(" [").append(def.dimension()).append(", ").append(def.environment())
                    .append(def.elite() ? ", elite" : "").append("]")
                    .append(matching.contains(def) ? "  <- matches now" : "")
                    .append('\n');
        }
        ctx.getSource().sendSuccess(() -> Component.literal(sb.toString().stripTrailing()), false);
        return HordeTypeRegistry.all().size();
    }
}
