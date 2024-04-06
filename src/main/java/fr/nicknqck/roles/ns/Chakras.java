package fr.nicknqck.roles.ns;

import fr.nicknqck.roles.ns.chakratype.Doton;
import fr.nicknqck.roles.ns.chakratype.Futon;
import fr.nicknqck.roles.ns.chakratype.Katon;
import fr.nicknqck.roles.ns.chakratype.Raiton;
import fr.nicknqck.roles.ns.chakratype.Suiton;

public enum Chakras {

	KATON("§cKaton", new Katon(), (short)1),
	SUITON("§bSuiton", new Suiton(), (short) 4),
	FUTON("§aFûton", new Futon(), (short) 10),
	DOTON("§6Doton", new Doton(), (short) 14),
	RAITON("§eRaiton", new Raiton(), (short) 11);
	private String showed;
	private Chakra ch;
	private short color;
	private Chakras(String a, Chakra ch, short colorCode) {
		this.showed = a;
		this.ch = ch;
		this.color = colorCode;
	}
	public String getShowedName() {
		return showed;
	}
	public Chakra getChakra() {
		return ch;
	}
	public short getColorCode() {
		return color;
	}
}
