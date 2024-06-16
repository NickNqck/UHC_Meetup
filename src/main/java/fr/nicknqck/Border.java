package fr.nicknqck;

import lombok.Getter;
import lombok.Setter;


public class Border {

    @Getter
    @Setter
    private static int maxBorderSize = 250;
    @Getter
    @Setter
    private static int minBorderSize = 50;
    @Getter
    @Setter
    private static float borderSpeed = 1f;
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
