package fr.nicknqck.roles.mc.nether;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.mc.builders.NetherRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class MagmaCube extends NetherRoles implements Listener {

    private int reviveRestant = 2;
    private boolean JumpBoost = false;


    public MagmaCube(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setMaxHealth(24.0);
        owner.setMaxHealth(getMaxHealth());
        owner.setHealth(owner.getMaxHealth());
        setNoFall(true);
        EventUtils.registerEvents(this);
        getGamePlayer().setCanRevive(true);
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public String getName() {
        return "Magma Cube";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.MagmaCube;
    }

    @Override
    public void resetCooldown() {
        JumpBoost = false;
        reviveRestant = 2;
    }

    @EventHandler
    private void onUHCPlayerDie(UHCPlayerKillEvent e) {
        if (reviveRestant > 0) {
            if (e.getVictim().getUniqueId().equals(owner.getUniqueId())) {
                Location loc = Loc.getRandomLocationAroundPlayer(e.getPlayerKiller(), 5);
                ItemStack[] contents = e.getVictim().getInventory().getContents();
                e.getGameState().addInSpecPlayers(e.getVictim());
                e.getGameState().RevivePlayer(e.getVictim());
                e.getVictim().setMaxHealth(getMaxHealth() -4.0);
                e.getVictim().sendMessage("§7Vous venez de réssusciter en perdant §c2" + AllDesc.coeur + " §7permanent");
                reviveRestant--;
                if(reviveRestant == 0){
                    getGamePlayer().setCanRevive(false);
                }
                if (e.getVictim().isDead()) {
                    e.getVictim().spigot().respawn();
                }
                if (loc.getWorld() != Main.getInstance().getWorldManager().getGameWorld()) {

                } else {
                    e.getVictim().teleport(loc);
                }
            }
        }
    }

    @Override
    public void onMcCommand(String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("MagmaCube")) {
                if (!JumpBoost) {
                    owner.sendMessage("§7Vous venez d'activer votre §2Jump Boost 2§7.");
                    getEffects().put(new PotionEffect(PotionEffectType.JUMP, 20, 1, false, false), EffectWhen.PERMANENT);
                    JumpBoost = true;
                } else {
                    owner.sendMessage("§7Vous venez de désactiver votre §2Jump Boost§7.");
                    getEffects().remove(new PotionEffect(PotionEffectType.JUMP, 20, 1, false, false));
                    JumpBoost = false;
                }
            }
        }
        super.onMcCommand(args);
    }


    @EventHandler
    private void EndGameEvent(EndGameEvent event) {
        EventUtils.unregisterEvents(this);
    }

}
