package fr.nicknqck.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameConfig {
    private static GameConfig instance;
    public GameConfig() {
        instance = this;
    }
    private int WaterEmptyTiming = 30;
    private int LavaEmptyTiming = 30;
}
