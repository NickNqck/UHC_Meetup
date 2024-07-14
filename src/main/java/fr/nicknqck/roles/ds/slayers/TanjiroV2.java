package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.particles.MathUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TanjiroV2 extends SlayerRoles implements Listener {
    private final ItemStack danseItem = new ItemBuilder(Material.BLAZE_ROD).setName("§6Danse du dieu du Feu").setUnbreakable(true).setDroppable(false).toItemStack();
    private int cdDanse;
    private boolean sentirUse;
    private int cdSentir;
    private boolean useAssassin;
    public TanjiroV2(Player player) {
        super(player);
        player.spigot().sendMessage(desc());
        getEffects().put(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.DAY);
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        new TanjiroRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        giveItem(player, false, getItems());
        giveItem(player, false, Items.getLamedenichirin());
        setCanuseblade(true);
        Lames.FireResistance.getUsers().put(player.getUniqueId(), Integer.MAX_VALUE);
    }

    @Override
    public String getName() {
        return "Tanjiro";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Tanjiro;
    }

    @Override
    public void resetCooldown(){
        cdDanse = 0;
        cdSentir = 0;
    }

    @Override
    public String[] Desc() {
        return new String[] {

        };
    }
    private TextComponent desc() {
        TextComponent texte = new TextComponent(AllDesc.bar);
        texte.addExtra("\n");
        texte.addExtra("§7Role: §aTanjiro\n");
        texte.addExtra("§7Votre objectif est de gagner avec le camp: §aSlayers\n");

        TextComponent danseItem = getDanseText();
        texte.addExtra("\n"+AllDesc.point+"§7Vous possédez l'effet§c Speed I§7 le§c jour§7\n\n");
        texte.addExtra(AllDesc.point+"§7Vous possédez l'item");
        texte.addExtra(danseItem);
        texte.addExtra("§7 (1x/12m).\n\n");

        TextComponent dsSentir = new TextComponent("§c/ds sentir");
        dsSentir.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                "§7Vous permet de savoir combien il y a de§c démons§7 dans un rayon de§c 30 blocs§7 autours de vous (§aNezuko§7 est compter dedans). (1x/5m)")}));
        dsSentir.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ds sentir"));
        texte.addExtra(AllDesc.point+"§7Vous avez accès aux commandes ");
        texte.addExtra(dsSentir);
        texte.addExtra("§7 (1x/5m), ");
        TextComponent sentir = new TextComponent("§c/ds §csentir <joueur>");
        sentir.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                "§7Vous permet de savoir si un joueur est un§c démons§7 ou non (§aNezuko§7 est compter comme§c démon§7). (1x/partie)")}));
        sentir.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ds sentir "));
        texte.addExtra(sentir);
        texte.addExtra("§7, ");
        TextComponent assassin = getAssassin();
        texte.addExtra(assassin);
        return texte;
    }

    private TextComponent getAssassin() {
        TextComponent assassin = new TextComponent("§c/ds assassin <joueur>");
        assassin.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                "§7Vous permet de savoir si un joueur est l'§4Assassin§7 ou non, " +
                "\n§7S'il l'est vous obtiendrez un §ctraqueur§7 vers lui pendant§c 3 minutes§7, " +
                "\n§7Sinon, vous perdrez§c 2"+AllDesc.coeur+"§7 permanent. (1x/partie)")}));
        assassin.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ds assassin "));
        return assassin;
    }

    private TextComponent getDanseText() {
        TextComponent danseItem = new TextComponent(" §7\"§6Danse §6du dieu §6du Feu§7\"");
        danseItem.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                "§7Pendant§c 5 minutes§7 vous obtiendrez les effets§c résistance 1§7 et§c résistance au feu 1§7, également,\n" +
                "§7pendant§c 1 minutes§7 vos coups mettront en §cfeu§7 les joueurs frappés, de plus,\n§7" +
                "le joueur possédant le rôle de§a Nezuko§7 obtiendra l'effet§c speed 2§7 pendant§c 5 minutes§7,\n§7" +
                " après ce temp là vous perdrez§c 2"+AllDesc.coeur+"§c permanent§7.§7 (1x/12m)")}));
        return danseItem;
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                danseItem
        };
    }
    @EventHandler
    private void onUse(PlayerInteractEvent event) {
        if (event.getItem() == null)return;
        if (event.getItem().isSimilar(danseItem)) {
            if (event.getPlayer().getUniqueId().equals(getPlayer())) {
                if (cdDanse <= 0) {
                    event.getPlayer().sendMessage("§7Vous utilisez votre§6 Danse du dieu du Feu");
                    cdDanse = 60*12;
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), true);
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*60*5, 0, false, false), true);
                    List<Player> liste = getListPlayerFromRole(Nezuko.class);
                    if (!liste.isEmpty()) {
                        Location loc = event.getPlayer().getLocation();
                        for (Player p : liste) {
                            p.sendMessage("§aTanjiro§7 a utiliser sa§6 danse du dieu du feu§7 en§c x: "+loc.getBlockX()+", y: "+loc.getBlockY()+", z: "+loc.getBlockZ());
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 1, false, false), true);
                        }
                    }
                    event.setCancelled(true);
                } else {
                    sendCooldown(event.getPlayer(), cdDanse);
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    private void onBattle(EntityDamageByEntityEvent event) {
        if (event.getDamager().getUniqueId().equals(getPlayer())) {
            if (cdDanse >= 60*11) {
                event.getEntity().setFireTicks(20*15);
            }
        }
    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void onDSCommandSend(String[] args, GameState gameState) {
        if (args[0].equalsIgnoreCase("assassin")) {
            if (args.length == 2) {
                Player owner = Bukkit.getPlayer(getPlayer());
                if (useAssassin){
                    owner.sendMessage("§cVous avez atteint le nombre maximum d'utilisation de ce pouvoir.");
                    return;
                }
                if (gameState.Assassin != null) {
                    Player target = Bukkit.getPlayer(getPlayer());
                    if (target != null) {
                        boolean assa = target.getUniqueId().equals(gameState.Assassin.getUniqueId());
                        owner.sendMessage("§c"+target.getName()+"§f "+(assa ? "§7est l'§4Assassin" : "§7n'est§c pas§7 l'§4Assassin"));
                        useAssassin = true;
                    }
                } else {
                    owner.sendMessage("§cL'§4Assassin§c n'a pas encore été désigner ou n'est pas présent dans la partie.");
                }
            }
        }
        if (args[0].equalsIgnoreCase("sentir")) {
            Player owner = Bukkit.getPlayer(getPlayer());
            if (args.length == 2) {
                if (sentirUse) {
                    owner.sendMessage("§cVous avez atteint le nombre maximum d'utilisation de ce pouvoir.");
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (!gameState.hasRoleNull(target)) {
                        RoleBase role = gameState.getPlayerRoles().get(target);
                        boolean demon = role.getTeam().equals(TeamList.Demon) || role instanceof Nezuko || role.getOriginTeam().equals(TeamList.Demon);
                        owner.sendMessage("§c"+target.getName()+(demon ? "§7 est un§c démon" : "§7 n'est pas un§c démon"));
                        sentirUse = true;
                    }
                } else {
                    owner.sendMessage("§c"+args[1]+" n'est pas connectée !");
                }
            } else {
                if (cdSentir > 0) {
                    sendCooldown(owner, cdSentir);
                    return;
                }
                int amountDemon = 0;
                for (Player player : Loc.getNearbyPlayersExcept(owner, 30)) {
                    if (!gameState.hasRoleNull(player)) {
                        RoleBase role = gameState.getPlayerRoles().get(player);
                        boolean demon = role.getTeam().equals(TeamList.Demon) || role instanceof Nezuko || role.getOriginTeam().equals(TeamList.Demon);
                        if (demon) {
                            amountDemon++;
                        }
                    }
                }
                owner.sendMessage("§7Il y a§c "+amountDemon+" démon§7(§cs§7) autours de vous");
                cdSentir = 60*5;
            }
        }
        super.onDSCommandSend(args, gameState);
    }

    private static class TanjiroRunnable extends BukkitRunnable {

        private final TanjiroV2 tanjiro;
        private TanjiroRunnable(TanjiroV2 tanjiro){
            this.tanjiro = tanjiro;
        }
        @Override
        public void run() {
            if (tanjiro.getGameState().getServerState() != GameState.ServerStates.InGame) {
                cancel();
                return;
            }
            Player owner = Bukkit.getPlayer(tanjiro.getPlayer());
            if (tanjiro.cdDanse >= 0) {
                tanjiro.cdDanse--;
            }
            if (tanjiro.cdSentir >= 0){
                tanjiro.cdSentir--;
            }
            if (owner != null) {
                if (tanjiro.cdSentir == 0) {
                    owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§c /ds sentir");
                }
                if (tanjiro.cdDanse == 0) {
                    owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§6 Danse du dieu du feu§7.");
                }
                if (owner.getItemInHand() != null) {
                    if (owner.getItemInHand().isSimilar(tanjiro.danseItem)) {
                        NMSPacket.sendActionBar(owner, (tanjiro.cdDanse <= 0 ? "§e<§f Pouvoir utilisable§e >" : "§bCooldown: "+ StringUtils.secondsTowardsBeautiful(tanjiro.cdDanse)));
                    }
                }
                if (tanjiro.cdDanse >= 60*7) {
                    NMSPacket.sendActionBar(owner, "§bTemp restant: "+StringUtils.secondsTowardsBeautiful(tanjiro.cdDanse-60*7));
                    MathUtil.sendCircleParticle(EnumParticle.FLAME, owner.getLocation(), 1, 15);
                    if (tanjiro.cdDanse == 60*7) {
                        owner.sendMessage("§7Vous devez arrêter de danser suite à votre fatigue...");
                        tanjiro.setMaxHealth(tanjiro.getMaxHealth()-4);
                    }
                    if (tanjiro.cdDanse == 60*11) {
                        owner.sendMessage("§7Vous ne mettrez plus les joueurs en§c feu§7.");
                    }
                }
            }
        }
    }
}