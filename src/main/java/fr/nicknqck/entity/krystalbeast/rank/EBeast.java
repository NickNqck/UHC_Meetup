package fr.nicknqck.entity.krystalbeast.rank;

import fr.nicknqck.entity.krystalbeast.creator.BeastCreator;

public abstract class EBeast extends BeastCreator {

    @Override
    public BeastRank getBeastRank() {
        return BeastRank.E;
    }


}