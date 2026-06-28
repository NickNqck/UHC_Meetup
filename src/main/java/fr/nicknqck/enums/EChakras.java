package fr.nicknqck.enums;

public enum EChakras {

	KATON("§cKaton", (short)1),
	SUITON("§bSuiton", (short) 4),
	FUTON("§aFûton", (short) 10),
	DOTON("§6Doton", (short) 14),
	RAITON("§eRaiton", (short) 11);
	private final String showed;
	private final short color;
	EChakras(String a, short colorCode) {
		this.showed = a;
		this.color = colorCode;
	}
	public String getShowedName() {
		return showed;
	}
	public short getColorCode() {
		return color;
	}
}
