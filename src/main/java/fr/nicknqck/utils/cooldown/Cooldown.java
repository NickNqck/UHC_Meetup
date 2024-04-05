package fr.nicknqck.utils.cooldown;

public final class Cooldown {
	
    private final int cooldown;
    private long lastUse;

    public Cooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public long getCooldownRemaining() {
        return (long) this.cooldown - (System.currentTimeMillis() - this.lastUse) / 1000L;
    }

    public boolean isInCooldown() {
        return System.currentTimeMillis() - this.lastUse < (long) this.cooldown * 1000L;
    }

    public void use() {
        this.lastUse = System.currentTimeMillis();
    }

    public void addSeconds(int seconds) {
        this.lastUse -= (long) seconds * 1000L;
    }

    public void resetCooldown() {
        this.lastUse = 0L;
    }
}
