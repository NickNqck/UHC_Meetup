package fr.nicknqck.bijus;

import org.bukkit.Material;

import fr.nicknqck.GameState;
import fr.nicknqck.bijus.biju.Chomei;
import fr.nicknqck.bijus.biju.Isobu;
import fr.nicknqck.bijus.biju.Kokuo;
import fr.nicknqck.bijus.biju.Matatabi;
import fr.nicknqck.bijus.biju.Saiken;
import fr.nicknqck.bijus.biju.SonGoku;

public enum Bijus {
	
	Isobu(new Isobu(), Material.NETHER_STAR, true),
	Kokuo(new Kokuo(), Material.NETHER_STAR, true),
	Chomei(new Chomei(), Material.NETHER_STAR, true),
	SonGoku(new SonGoku(), Material.NETHER_STAR, true),
	Matatabi(new Matatabi(), Material.NETHER_STAR, true),
	Saiken(new Saiken(), Material.NETHER_STAR, true);
	//KyubiYang(new KyubiYang(), Material.NETHER_STAR, false);
	private final Biju biju;
	private final Material mat;
	private boolean enable;
	private Bijus(Biju biju, Material material, boolean enable) {
		this.biju = biju;
		this.mat = material;
		this.enable = enable;
	}
	public Biju getBiju() {
        return biju;
    }
	public Material getMaterial() {
		return mat;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean e) {
		enable = e;
	}
	public static void initBiju(GameState state) {
		if (GameState.getInstance().BijusEnable) {
			for (Bijus value : values()) {
				if (value.isEnable()) {
					value.getBiju().setupBiju(state);
				}
			}
		}
	}
}