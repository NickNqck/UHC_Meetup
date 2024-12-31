package fr.nicknqck.roles.aot.builders.titans;

import lombok.Getter;

@Getter
public enum Titans {
	
	Colossal(new Colossal()),
	Cuirasse(new Cuirasse()),
	WarHammer(new WarHammer()),
	Machoire(new Machoire()),
	Charette(new Charette()),
	Assaillant(new Assaillant()),
	Bestial(new Bestial());
	
	final Titan titan;
	Titans(Titan titan) {
		this.titan = titan;
	}

}