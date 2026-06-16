package dev.vmmad.harderhordes.horde.difficulty;

/**
 * The blended difficulty result plus its normalized components (each ~0..1),
 * kept around so {@code /harderhordes score} can show the breakdown for tuning.
 */
public record ProgressionScore(double total, double dayScore, double difficultyScore, double gearScore) {

    public static final ProgressionScore ZERO = new ProgressionScore(0, 0, 0, 0);
}
