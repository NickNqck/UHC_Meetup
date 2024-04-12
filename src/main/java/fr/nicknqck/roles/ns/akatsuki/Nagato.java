package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class Nagato extends RoleBase {
    private final ItemStack ShuradoItem = new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 4).setName("§7Shuradô").setLore("§7").toItemStack();
    private int useJikogudo = 0;
    private final ItemStack ShikushodoItem = new ItemBuilder(Material.NETHER_STAR).setName("§fShikushodo").setLore("§7").toItemStack();
    private Location ShikushodoLoc;
    private int cdShikushodo = 0;
    public Nagato(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
        setChakraType(Chakras.SUITON);
        player.sendMessage(Desc());
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        super.giveItem(owner, false, getItems());
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§cNagato",
                AllDesc.objectifteam+"§cAkatsuki",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§7Shuradô§f: Juste une épée en diamant§7 Tranchant IV§f.",
                "",
                AllDesc.point+"Shikushodo: Permet via un clique droit de ce téléporter à un emplacement au préalable prédéfinie via un shift + clique droit",
                "",
                AllDesc.commande,
                "",
                AllDesc.point+"§6/ns jigokudo <joueur>§f: Vous permet (si vous êtes à moins de§c 15 blocs§f du joueur) d'obtenir précisément le rôle du joueur viser.",
                "",
                AllDesc.particularite,
                "",
                AllDesc.point+"Lorsque que vous subissez des dégats vous aurrez§c 15%§f de chance de réduire les dégats de§c 25%",
                "",
                AllDesc.chakra+getChakras().getShowedName(),
                AllDesc.bar
        };
    }

    @Override
    public void onNsCommand(String[] args) {
        super.onNsCommand(args);
        if (args[0].equalsIgnoreCase("jigokudo")){
            if (args.length == 2){
                if (useJikogudo > 2){
                    owner.sendMessage("§cVous avez utiliser le nombre maximum d'utilisation de Jigokudo (2)");
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null){
                    if (Loc.getNearbyPlayersExcept(owner, 15).contains(target)){
                        if (!gameState.hasRoleNull(target)){
                            owner.sendMessage(getTeamColor(target)+"§f possède le rôle: "+getPlayerRoles(target).type.getItem().getItemMeta().getDisplayName());
                            useJikogudo++;
                        }
                    }
                }
            }
        }
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                ShuradoItem,
                ShikushodoItem
        };
    }

    @Override
    public void resetCooldown() {
        useJikogudo = 0;
        cdShikushodo = 0;
        ShikushodoLoc = null;
    }

    @Override
    public void Update(GameState gameState) {
        super.Update(gameState);
        if (cdShikushodo >= 0){
            cdShikushodo--;
            if (cdShikushodo == 0){
                owner.sendMessage("§7Vous pouvez à nouveau vous téléportez à l'emplacement de§f Shikushodo§7.");
            }
        }
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(ShikushodoItem)) {
            if (cdShikushodo <= 0){
                if (owner.isSneaking()){
                    ShikushodoLoc = owner.getLocation().clone();
                    owner.sendMessage("§7Vous avez définie la position de§f Shikushodo§7 en:§c x: "+ShikushodoLoc.getBlockX()+", y: "+ShikushodoLoc.getBlockY()+", z: "+ShikushodoLoc.getBlockZ());
                } else {
                    if (ShikushodoLoc != null){
                        owner.teleport(ShikushodoLoc);
                        cdShikushodo = 60*5;
                        owner.sendMessage("§7Vous avez été téléporter à l'emplacement de§f Shikushodo");
                        ShikushodoLoc.getWorld().getBlockAt(ShikushodoLoc).setType(Material.AIR);
                    }
                }
            }
            return  true;
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public void onALLPlayerDamage(EntityDamageEvent e, Player victim) {
        super.onALLPlayerDamage(e, victim);
        if (victim.getUniqueId().equals(owner.getUniqueId())){
            if (RandomUtils.getOwnRandomProbability(15)){
                e.setDamage(e.getDamage()*0.75);//(donc -25% de dégat subit)
                victim.sendMessage("§aGakido§7 vous à protégez des dégats, vous avez donc subit§c 25% de dégat en moins");
            }
        }
    }
}
