package fr.nicknqck.events;

import fr.nicknqck.GameState;
import fr.nicknqck.events.ds.AkazaVSKyojuro;
import fr.nicknqck.events.ds.Alliance;
import fr.nicknqck.events.ds.dkt.DemonKing;

public enum Events {
	DemonKingTanjiro(new DemonKing(),"§cDemon King Tanjiro", GameState.getInstance().DKTProba),
	Alliance(new Alliance(), "§fAlliance §aKyojuro§f -§e Shinjuro", GameState.getInstance().AllianceProba),
	AkazaVSKyojuro(new AkazaVSKyojuro(), "§cAkaza§6 vs§a Kyojuro", GameState.getInstance().AkazaVSKyojuroProba);
	
	private EventBase base;
	private String name;
	private double probalite;
	
	Events(EventBase base, String string, double proba) {
		this.base = base;
		this.name = string;
		this.probalite = proba;
	}
	public String getName() {
		return this.name;
	}
	public EventBase getEvent() {
		return this.base;
	}
	public void setName(String n) {
		this.name = n;
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