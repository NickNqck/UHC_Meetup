package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.betteritem.BetterItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Mitsuri extends PillierRoles {

    public Mitsuri(UUID player) {
        super(player);
        setCanuseblade(true);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.FLAMME;
    }

    @Override
    public Roles getRoles() {
        return Roles.Mitsuri;
    }
    @Override
    public String[] Desc() {return AllDesc.Mitsuri;}
    @Override
    public ItemStack[] getItems() {
    	return new ItemStack[] {
    			BetterItem.of(Items.getCharm(), event-> true).setPosable(false).setDroppable(false).getItemStack()
    	};
    }

    @Override
    public void GiveItems() {
        owner.getInventory().addItem(Items.getCharm());
        owner.getInventory().addItem(Items.getLamedenichirin());        
        super.GiveItems();
    }
    private int cooldown = 0;
    @Override
    public void resetCooldown() {
    	cooldown = 0;
    }
    @Override
    public void Update(GameState gameState) {
    	if (owner.getItemInHand().isSimilar(Items.getCharm())) {
			sendActionBarCooldown(owner, cooldown);
		}
        if (gameState.nightTime){
            givePotionEffet(owner, PotionEffectType.INCREASE_DAMAGE, 20*3, 1, true);
        }
        if (cooldown >= 1) cooldown--;
        if (cooldown == (60*5)-10){
            for (Player p : Bukkit.getOnlinePlayers()){
                if (gameState.getCharmed().contains(p)){
                	gameState.delCharmed(p);
                    owner.sendMessage(ChatColor.GOLD+p.getName()+"§r n'est plus sous votre charme");
                    p.sendMessage("Vous n'êtes plus sous l'effet du charme de Mitsuri");
                }
            }
        }
        super.Update(gameState);
    }
    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(Items.getCharm())){
            if (cooldown <= 0){
                for (UUID u : gameState.getInGamePlayers()){
                    Player p = Bukkit.getPlayer(u);
                    if (p == null)continue;
                    if (gameState.getInGamePlayers().contains(p.getUniqueId())){
                        for (RoleBase r : gameState.getPlayerRoles().values()){
                            if (r.getRoles() != Roles.Mitsuri && r.getRoles() != null && p != owner){
                                if (!gameState.getCharmed().contains(p)){
                                    if (r.owner == p){
                                        if (p.getLocation().distance(owner.getLocation()) <= 10 && owner.canSee(p)){
                                        	Player pl = getRightClicked(10, 5);
                                        	if (pl != p) {
                                        		owner.sendMessage("Veuiller visée un joueur");
                                        		return super.ItemUse(item, gameState);
                                        	}
                                            cooldown = 60*5;
                                            gameState.addCharmed(p);//GameState 132
                                            p.sendMessage("Vous avez été charmer par"+ChatColor.GOLD+" Mitsuri");
                                            owner.sendMessage("Vous venez de charmer le joueur: "+ChatColor.GOLD+p.getName());
                                        }else {owner.sendMessage(ChatColor.RED+"Veuiller viser un joueur");}
                                    }
                                }else {owner.sendMessage(ChatColor.RED+"Veuiller viser un joueur qui n'est pas déjà charmer");}
                            }
                        }
                    } 
                }
            } else {
            	sendCooldown(owner, cooldown);
            }
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public String getName() {
        return "Mitsuri";
    }
}