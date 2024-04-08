package fr.nicknqck.scenarios;

import fr.nicknqck.scenarios.impl.*;
import lombok.Getter;

@Getter
public enum Scenarios {

    Abso(new Anti_Abso()),
    Hastey_Boys(new Hastey_Boys()),
    Hastey_Babys(new Hastey_Babys()),
    FFA(new FFA()),
    Drop(new AntiDrop()),
    CutClean(new CutClean()),
    BowSwap(new BowSwap()),
    DiamondLimit(new DiamondLimit());

    private final BasicScenarios scenarios;
    Scenarios(BasicScenarios sc){
        scenarios = sc;
    }
}
