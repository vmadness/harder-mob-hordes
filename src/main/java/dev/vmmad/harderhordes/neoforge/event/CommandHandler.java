package dev.vmmad.harderhordes.neoforge.event;

import dev.vmmad.harderhordes.command.HordeCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** Registers the {@code /harderhordes} debug command. */
public final class CommandHandler {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        HordeCommand.register(event.getDispatcher());
    }
}
