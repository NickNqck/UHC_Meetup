package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.utils.RandomUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class NSRoles extends RoleBase {
    public NSRoles(Player player) {
        super(player);
    }
    public abstract Intelligence getIntelligence();
    public void setChakraType(Chakras chakras) {
        if (chakras == null) {
            this.chakras = null;
            return;
        }
        chakras.getChakra().getList().add(owner.getUniqueId());
        this.chakras = chakras;
    }
    private Chakras chakras = null;
    public Chakras getChakras() {
        return chakras;
    }
    public boolean hasChakras() {
        return chakras != null;
    }
    public Chakras getRandomChakras() {
        Chakras toReturn = null;
        int rdm = RandomUtils.getRandomInt(1, 5);
        if (rdm == 1) {
            toReturn = Chakras.DOTON;
        }
        if (rdm == 2) {
            toReturn = Chakras.FUTON;
        }
        if (rdm == 3) {
            toReturn = Chakras.KATON;
        }
        if (rdm == 4) {
            toReturn = Chakras.RAITON;
        }
        if (rdm == 5) {
            toReturn = Chakras.SUITON;
        }

        return toReturn;
    }
    public Chakras getRandomChakrasBetween(Chakras... c) {
        Chakras tr = null;
        HashMap<Integer, Chakras> canReturn = new HashMap<>();
        int i = 0;
        for (Chakras ch : c) {
            i++;
            canReturn.put(i, ch);
        }
        int max = canReturn.size()+1;
        int rdm = RandomUtils.getRandomInt(1, max);
        for (Chakras r : canReturn.values()) {
            if (canReturn.get(rdm).equals(r)) {
                tr = r;
                break;
            }
        }
        return tr;
    }
}
