package fr.nicknqck;

import lombok.Getter;
import lombok.Setter;


public class Border {

    @Getter
    @Setter
    private static int maxBorderSize = 300;
    @Getter
    @Setter
    private static int minBorderSize = 150;
    @Getter
    @Setter
    private static long borderSpeed = 1;
    @Getter
    @Setter
    private static int tempReduction = 120;
    @Getter
    @Setter
    private static float actualBorderSize = getMaxBorderSize();
    @Getter
    @Setter
    private static int maxBijuSpawn = 200;
    @Getter
    @Setter
    private static int minBijuSpawn = 100;
}