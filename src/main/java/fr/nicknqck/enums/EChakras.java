package fr.nicknqck.enums;

import fr.nicknqck.interfaces.IChakra;
import fr.nicknqck.roles.ns.chakratype.*;

public enum EChakras {

	KATON("§cKaton", new Katon(), (short)1),
	SUITON("§bSuiton", new Suiton(), (short) 4),
	FUTON("§aFûton", new Futon(), (short) 10),
	DOTON("§6Doton", new Doton(), (short) 14),
	RAITON("§eRaiton", new Raiton(), (short) 11);
	private final String showed;
	private final IChakra ch;
	private final short color;
	EChakras(String a, IChakra ch, short colorCode) {
		this.showed = a;
		this.ch = ch;
		this.color = colorCode;
	}
	public String getShowedName() {
		return showed;
	}
	public IChakra getChakra() {
		return ch;
	}
	public short getColorCode() {
		return color;
	}
}
