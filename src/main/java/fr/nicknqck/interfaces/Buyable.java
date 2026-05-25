package fr.nicknqck.interfaces;

import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

public interface Buyable {

    @NonNull
    Class<? extends RoleBase> getAssossiatedClass();

    @NonNull
    ItemStack getMenuItem();

    @NonNull
    Power createPower(@NonNull final RoleBase role);

    int getCrystalCost();

}