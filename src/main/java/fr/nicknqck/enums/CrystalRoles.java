package fr.nicknqck.enums;

import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum CrystalRoles implements IRoles<CrystalRoles> {

    //ROYAUME
    Leolio(CrystalTeam.Royaume, "cr", 0, new ItemBuilder(Material.DIAMOND_PICKAXE).setName("§aLeolio").toItemStack(), "§bNickNqck"),
    //GUILDE
    Bartholome(CrystalTeam.Guilde, "cr", 0, new ItemBuilder(Material.DIAMOND_PICKAXE).setName("§5Bartholome").toItemStack(), "§bNickNqck")

    ;

    private final ITeam team;
    private final String mdj;
    private final int nmb;
    private final ItemStack item;
    private final String gDesign;

    CrystalRoles(ITeam team, String mdj, int nmb, ItemStack item, String gDesign) {
        this.team = team;
        this.mdj = mdj;
        this.nmb = nmb;
        this.item = item;
        this.gDesign = gDesign;
    }

    @Override
    public ITeam getTeam() {
        return this.team;
    }

    @Override
    public String getMdj() {
        return this.mdj;
    }

    @Override
    public int getNmb() {
        return this.nmb;
    }

    @Override
    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public String getGDesign() {
        return this.gDesign;
    }
}
