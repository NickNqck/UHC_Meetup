package fr.nicknqck.roles.krystal;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class BonusKrystalBase extends KrystalBase implements IKrystalRoleBooster {

    private final List<String> customBonusString;
    private final List<Bonus> bonusList;

    public BonusKrystalBase(UUID player) {
        super(player);
        this.customBonusString = new ArrayList<>();
        this.bonusList = new ArrayList<>();
    }

    @Override
    public @NonNull List<Bonus> getBonus() {
        return this.bonusList;
    }

    public void addBonus(@NonNull final Bonus bonus) {
        getBonus().add(bonus);
    }
}