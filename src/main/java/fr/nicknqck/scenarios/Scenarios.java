package fr.nicknqck.scenarios;

import lombok.Getter;

@Getter
public enum Scenarios {

    Abso(new Anti_Abso()),
    Hastey_Boys(new Hastey_Boys()),
    Hastey_Babys(new Hastey_Babys()),
    FFA(new FFA()),
    Drop(new AntiDrop()),
    CutClean(new CutClean());

    private final BasicScenarios scenarios;
    Scenarios(BasicScenarios sc){
        scenarios = sc;
    }
}
