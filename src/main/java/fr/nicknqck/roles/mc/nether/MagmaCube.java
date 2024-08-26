package fr.nicknqck.roles.mc.nether;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.mc.builders.NetherRoles;
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
        super.RoleGiven(gameState);
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


    @Override
    public void Update(GameState gameState) {
        getGamePlayer().setCanRevive(reviveRestant != 0);
        super.Update(gameState);
    }

    @EventHandler
    private void onUHCPlayerDie(UHCPlayerKillEvent e) {
        if (reviveRestant > 0) {
            if (e.getVictim().getUniqueId().equals(owner.getUniqueId())) {
                e.getGameState().addInSpecPlayers(e.getVictim());
                e.getGameState().RevivePlayer(e.getVictim());
                e.getVictim().setMaxHealth(getMaxHealth()-4.0);
                e.getVictim().sendMessage("§7Vous venez de réssusciter en perdant §c2"+AllDesc.coeur+" §7permanent");
                if (e.getVictim().isDead()) {
                    e.getVictim().spigot().respawn();
                }
            }
        }
    }

    @Override
    public void onMcCommand(String[] args) {
        if(args.length == 2){
            if (args[0].equalsIgnoreCase("MagmaCube")) {
                if (!JumpBoost){
                    owner.sendMessage("§7Vous venez d'activer votre §2Jump Boost 2§7.");
                    getEffects().put(new PotionEffect(PotionEffectType.JUMP, 20,1,false,false), EffectWhen.PERMANENT);
                    JumpBoost = true;
                } else {
                    owner.sendMessage("§7Vous venez de désactiver votre §2Jump Boost§7.");
                    getEffects().remove(new PotionEffect(PotionEffectType.JUMP,20,1,false,false));
                    JumpBoost = false;
                }
            }
        }
        super.onMcCommand(args);
    }
}
