package fr.nicknqck.roles.ns.builders;

import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.EChakras;
import fr.nicknqck.roles.ns.Intelligence;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

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

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    public boolean hasChakras() {
        return chakras != null;
    }
    public void onNsCommand(String[] args) {}
    public abstract EChakras[] getChakrasCanHave();

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
        this.chakras = chakras;
        chakras.getChakra().getList().add(getPlayer());
    }
}