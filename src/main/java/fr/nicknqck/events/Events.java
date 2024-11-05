package fr.nicknqck.events;

import fr.nicknqck.GameState;
import fr.nicknqck.events.ds.AkazaVSKyojuro;
import fr.nicknqck.events.ds.dkt.DemonKing;
import lombok.Getter;

public enum Events {
	DemonKingTanjiro(new DemonKing(),"§cDemon King Tanjiro", GameState.getInstance().DKTProba),
	AkazaVSKyojuro(new AkazaVSKyojuro(), "§cAkaza§6 vs§a Kyojuro", GameState.getInstance().AkazaVSKyojuroProba);
	
	private final EventBase base;
	@Getter
	private final String name;
	private double probalite;
	
	Events(EventBase base, String string, double proba) {
		this.base = base;
		this.name = string;
		this.probalite = proba;
	}

	public EventBase getEvent() {
		return this.base;
	}

	public double getProba() {
		return probalite;
	}
	public void setProba(double d) {
		probalite = d;
	}
	public static void initEvents() {
		for (Events value : values()) {
			value.getEvent().setupEvent();
		}
	}
}