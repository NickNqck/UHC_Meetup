package fr.nicknqck.bijus;

import fr.nicknqck.Main;
import lombok.Getter;
import org.bukkit.Material;

import fr.nicknqck.GameState;
import fr.nicknqck.bijus.biju.Chomei;
import fr.nicknqck.bijus.biju.Isobu;
import fr.nicknqck.bijus.biju.Kokuo;
import fr.nicknqck.bijus.biju.Matatabi;
import fr.nicknqck.bijus.biju.Saiken;
import fr.nicknqck.bijus.biju.SonGoku;

public enum Bijus {
	
	Isobu(new Isobu(), Material.NETHER_STAR),
	Kokuo(new Kokuo(), Material.NETHER_STAR),
	Chomei(new Chomei(), Material.NETHER_STAR),
	SonGoku(new SonGoku(), Material.NETHER_STAR),
	Matatabi(new Matatabi(), Material.NETHER_STAR),
	Saiken(new Saiken(), Material.NETHER_STAR);
	@Getter
    private final Biju biju;
	private final Material mat;

	Bijus(Biju biju, Material material) {
		this.biju = biju;
		this.mat = material;
	}

    public Material getMaterial() {
		return mat;
	}

    public static void initBiju(GameState state) {
		if (Main.getInstance().getGameConfig().isBijusEnable()) {
			for (Bijus value : values()) {
				if (value.getBiju().isEnable()) {
					value.getBiju().setupBiju(state);
				}
			}
		}
	}
}