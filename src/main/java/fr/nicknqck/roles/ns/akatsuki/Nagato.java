package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.roles.ns.solo.jubi.Obito;
import fr.nicknqck.utils.*;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Nagato extends AkatsukiRoles {
    private final ItemStack ShuradoItem = new ItemBuilder(Material.DIAMOND_SWORD).hideEnchantAttributes().addEnchant(Enchantment.DAMAGE_ALL, 4).setName("§7Shuradô").setLore("§7Sharpness IV").toItemStack();
    private int useJikogudo = 0;
    private final ItemStack ShikushodoItem = new ItemBuilder(Material.NETHER_STAR).setName("§fShikushodo").setLore(
            "§aShift + Clique droit§f: Vous permet de placer un point de téléportation à votre position.",
            "",
            "§cClique droit§f: Vous permet de vous téléportez à la dernière position poser.").toItemStack();
    private Location ShikushodoLoc;
    private int cdShikushodo = 0;
    private int useNingendo = 0;
    private final ItemStack BenshoItem = new ItemBuilder(Material.NETHER_STAR).setName("§cBenshô Ten'in").setLore("§aClique gauche§f: Vous permet de repousser toute entité étant à moins de§c 20 blocs§f de vous.","","§cClique droit§f: Vous permet de téléporter le joueur viser à votre position.").toItemStack();
    private int cdTpMe = 0;
    private int cdRepousser = 0;
    private final List<UUID> NF = new ArrayList<>();
    public Nagato(Player player) {
        super(player);
        setChakraType(Chakras.SUITON);
    }
    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Nagato;
    }
    @Override
    public void GiveItems() {
        super.GiveItems();
        super.giveItem(owner, false, getItems());
        super.giveHealedHeartatInt(owner, 3.0);
    }

    @Override
    public String[] Desc() {
        List<Player> mates = new ArrayList<>();
        for (Player p : gameState.getInGamePlayers()) {
            if (!gameState.hasRoleNull(p)) {
                if (getOldTeam(p) != null && p.getUniqueId() != owner.getUniqueId()) {
                    if (getOldTeam(p) == TeamList.Akatsuki || getPlayerRoles(p) instanceof Obito) {
                        mates.add(p);
                    }
                }
            }
        }
        if (!mates.isEmpty()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                owner.sendMessage("Voici la liste de vos coéquipier: ");
                mates.forEach(p -> owner.sendMessage("§7 - §c"+p.getName()));}, 1);
        }
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
                AllDesc.point+"§cBenshô Ten'In§f: Effectue différente action en fonction du clique utiliser: ",
                AllDesc.tab+"§aClique gauche§f: Vous permet de repousser toute entité étant à moins de§c 20 blocs§f de vous.",
                AllDesc.tab+"§cClique droit§f: Vous permet de téléporter le joueur viser à votre position.",
                "",
                AllDesc.commande,
                "",
                AllDesc.point+"§6/ns jigokudo <joueur>§f: Vous permet (si vous êtes à moins de§c 15 blocs§f du joueur) d'obtenir précisément le rôle du joueur viser.",
                "",
                AllDesc.point+"§6/ns ningendo <joueur>§f: Vous permet (si vous êtes proche à moins de§c 15 blocs du joueur§f) d'obtenir précisément le camp du joueur ainsi que son nombre de§e pomme d'or§f.",
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
                            owner.sendMessage(getPlayerRoles(target).getRoles().getTeam().getColor()+target.getDisplayName()+"§f possède le rôle: "+getPlayerRoles(target).getRoles().getItem().getItemMeta().getDisplayName());
                            useJikogudo++;
                        }
                    } else {
                        owner.sendMessage("§cVous n'êtes pas asser proche du joueur viser");
                    }
                } else {
                    owner.sendMessage("§c"+args[1]+" n'est pas connectée !");
                }
            }
        }
        if (args[0].equalsIgnoreCase("ningendo")){
            if (args.length == 2){
                if (useNingendo > 2){
                    owner.sendMessage("§cVous avez utiliser le nombre maximum d'utilisation de Ningendo (2)");
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null){
                    if (Loc.getNearbyPlayersExcept(owner, 15).contains(target)){
                        if (!gameState.hasRoleNull(target)){
                            owner.sendMessage(getTeamColor(target)+target.getDisplayName()+"§7 est dans le camp: "+getTeamColor(target)+getTeam(target).name()+"§7, et possède exactement "+ GlobalUtils.getItemAmount(target, Material.GOLDEN_APPLE)+"§e pommes d'or");
                            useNingendo++;
                        } else {
                            owner.sendMessage(target.getDisplayName()+" ne possède pas de rôle, et donc de team.");
                        }
                    } else {
                        owner.sendMessage("§cVous n'êtes pas asser proche du joueur viser");
                    }
                } else {
                    owner.sendMessage("§c"+args[1]+" n'est pas connectée !");
                }
            }
        }
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                ShuradoItem,
                ShikushodoItem,
                BenshoItem
        };
    }

    @Override
    public void resetCooldown() {
        useJikogudo = 0;
        cdShikushodo = 0;
        ShikushodoLoc = null;
        useNingendo = 0;
        cdTpMe = 0;
        cdRepousser = 0;
    }

    @Override
    public void Update(GameState gameState) {
        super.Update(gameState);
        givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
        if (cdShikushodo >= 0){
            cdShikushodo--;
            if (cdShikushodo == 0){
                owner.sendMessage("§7Vous pouvez à nouveau vous téléportez à l'emplacement de§f Shikushodo§7.");
            }
        }
        if (cdTpMe >= 0){
            cdTpMe--;
            if (cdTpMe == 0){
                owner.sendMessage("§7Vous pouvez à nouveau téléporter un joueur à votre position.");
            }
        }
        if (cdRepousser >= 0){
            cdRepousser--;
            if (cdRepousser == 0){
                owner.sendMessage("§7Vous pouvez à nouveau éjecter les joueurs proches de vous.");
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
                    } else {
                        owner.sendMessage("§cIl faut d'abord définir un lieu de téléportation !");
                    }
                }
            } else {
                sendCooldown(owner, cdShikushodo);
            }
            return  true;
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public void onALLPlayerInteract(PlayerInteractEvent event, Player player) {
        super.onALLPlayerInteract(event, player);
        if (player.getUniqueId().equals(owner.getUniqueId()) && event.getItem().isSimilar(BenshoItem)){
            if (event.getAction().name().contains("RIGHT")){
                if (cdTpMe > 0){
                    sendCooldown(owner, cdTpMe);
                    return;
                }
                Player target = getTargetPlayer(owner, 50);
                if (target != null){
                    target.teleport(owner);
                    owner.sendMessage("§7Vous avez téléporter§c "+target.getDisplayName()+"§7 à votre position.");
                    cdTpMe = 60*5;
                } else {
                    owner.sendMessage("§cIl faut viser un joueur !");
                }
            } else {
                if (cdRepousser > 0){
                    sendCooldown(owner, cdRepousser);
                    return;
                }
                PropulserUtils pu = new PropulserUtils(owner, 20).soundToPlay("nsmtp.shinratensei");
                NF.addAll(pu.getPropulsedUUID());
                pu.applyPropulsion();
                owner.sendMessage("§7Vous avez utiliser votre§c Shinra Tensei");
                cdRepousser = 60*3;
            }
        }
    }

    @Override
    public Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public void onALLPlayerDamage(EntityDamageEvent e, Player victim) {
        super.onALLPlayerDamage(e, victim);
        if (!NF.isEmpty()){
            if (NF.contains(victim.getUniqueId())){
                e.setDamage(0.0);
                victim.setFallDistance(0f);
                NF.remove(victim.getUniqueId());
            }
        }
        if (victim.getUniqueId().equals(owner.getUniqueId())){
            if (RandomUtils.getOwnRandomProbability(15)){
                e.setDamage(e.getDamage()*0.75);//(donc -25% de dégat subit)
                victim.sendMessage("§aGakido§7 vous à protégez des dégats, vous avez donc subit§c 25% de dégat en moins");
            }
        }
    }

    @Override
    public String getName() {
        return "§cNagato";
    }
}
