package fr.nicknqck.roles.mc.overworld;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.mc.builders.UHCMcRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class AraigneeVenimeuse extends UHCMcRoles {

    private final ItemStack ToileItem = new ItemBuilder(Material.WEB).setName("§aToile d'araignée").setLore("§7Vous permez de poser une toile d'araigée sous un joueur").addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).hideAllAttributes().toItemStack();
    private boolean poison = false;
    private int cdToile = 0;

    public AraigneeVenimeuse(UUID player) {
        super(player);
        giveItem(owner, false , getItems());
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§aAraignée Venimeuse",
                AllDesc.objectifteam+"§aOverworld",
                "",
                AllDesc.items,
                AllDesc.point+"§aToile d'araignée §r: Vous permez de poser une toile d'araignée sur le joueur visé. (1x/15mins)",
                "",
                AllDesc.commande,
                AllDesc.point+"/mc poison : Vous permez d'activé votre passif",
                "",
                AllDesc.particularite,
                "Si votre passif est actif tout vos coups d'épée donnerons l'effet §2poison 1 pendant 2 secondes.",
        };
    }



    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
            ToileItem,
        };
    }

    @Override
    public String getName() {
        return "Araignée venimeuse";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.AraigneeVenimeuse;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.OverWorld;
    }

    @Override
    public void resetCooldown() {
        cdToile = 0;
        poison = false;
    }

    @Override
    public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
        if (entity.getUniqueId() == owner.getUniqueId()){
            if(owner.getItemInHand().getType().equals(Material.DIAMOND_SWORD)){
                if (poison){
                    givePotionEffet(victim, PotionEffectType.POISON, 20*2,1,true);
                }
            }
        }
        super.onALLPlayerDamageByEntity(event, victim, entity);
    }

    @Override
    public void onMcCommand(String[] args) {
        if (args[0].equalsIgnoreCase("poison")) {
            if (poison){
                owner.sendMessage("Vous venez de désactiver votre passif §aPoison§r.");
                poison = false;
            } else {
                owner.sendMessage("Vous venez d'activer votre passif §aPoison§r.");
                poison = true;
            }
        }
        super.onMcCommand(args);
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(ToileItem)){
            if (cdToile <= 0){
                Player target = getTargetPlayer(owner, 30);
                if (target == null) {
                    owner.sendMessage("§cIl faut viser un joueur !");
                    return true;
                }
                owner.sendMessage("Vous venez de placez une §atoile d'araignée §rsous les pieds de "+target.getName()+".");
                target.sendMessage("§cVous venez de marcher dans une toile d'araignée!");
                target.getLocation().getBlock().setType(Material.WEB);
                cdToile = 60*15;
            } else {
                sendCooldown(owner, cdToile);
            }
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public void Update(GameState gameState) {
        if (cdToile >= 0){
            cdToile --;
            if (cdToile == 0){
                owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§a Toile d'araignée");
            }
        }
        super.Update(gameState);
    }
}
