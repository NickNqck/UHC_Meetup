package fr.nicknqck.roles.aot.builders.titans;

public enum Titans {
	
	Colossal(new Colossal()),
	Cuirasse(new Cuirasse()),
	WarHammer(new WarHammer()),
	Machoire(new Machoire()),
	Charette(new Charette()),
	Assaillant(new Assaillant()),
	Bestial(new Bestial());
	
	Titan titan;
	private Titans(Titan titan) {
		this.titan = titan;
	}
	
	public Titan getTitan() {
		return titan;
	}
}