package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.Main;
import fr.nicknqck.interfaces.IChakraV2;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.Intelligence;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.*;

public abstract class NSRoles extends RoleBase {
    @Setter
    @Getter
    private boolean canBeHokage = false;
    private EChakras chakras = null;
    public NSRoles(UUID player) {
        super(player);
    }

    public abstract @NonNull Intelligence getIntelligence();

    public boolean hasChakras() {
        return chakras != null;
    }
    public void onNsCommand(String[] args) {}
    public abstract EChakras[] getChakrasCanHave();

    @NonNull
    public EChakras getChakras() {
        if (this.chakras == null) {
            final List<EChakras> chakrasList = new ArrayList<>(Arrays.asList(getChakrasCanHave()));
            Collections.shuffle(chakrasList);
            this.setChakras(chakrasList.get(0));
            Main.getInstance().debug(getPlayer()+" ("+(Main.getInstance().getServer().getPlayer(getPlayer()) == null ? "null" : Main.getInstance().getServer().getPlayer(getPlayer()).getName())+") chakra is now "+this.chakras);
        }
        return chakras;
    }
    public void setChakras(final EChakras chakras) {
        if (this.chakras != null) {
            for (IChakraV2 iChakraV2 : Main.getInstance().getBijuManager().getChakraManager().getLoadedChakra()) {
                if (iChakraV2.getMap().containsKey(getPlayer())) {
                    iChakraV2.setActivateFor(getPlayer(), false);
                }
            }
        }
        this.chakras = chakras;
        for (IChakraV2 iChakraV2 : Main.getInstance().getBijuManager().getChakraManager().getLoadedChakra()) {
            if (iChakraV2.getChakraType().equals(this.chakras)) {
                iChakraV2.setActivateFor(getPlayer(), !iChakraV2.isActivate(getPlayer()));
                break;
            }
        }
    }
}