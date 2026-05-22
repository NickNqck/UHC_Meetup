package fr.nicknqck.interfaces;

import fr.nicknqck.enums.CrystalFaction;
import lombok.NonNull;

public interface IGotMultpleFaction {

    @NonNull
    CrystalFaction getOneFaction();
    @NonNull
    CrystalFaction[] getPossiblesFaction();

}
