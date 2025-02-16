package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.events.custom.assassin.ProcAssassinEvent;
import fr.nicknqck.items.Items;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
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
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class Tanjiro extends SlayerRoles implements Listener {

    private TextComponent automaticDesc;
    private GamePlayer gameAssassin;

    public Tanjiro(UUID player) {
        super(player);

    }

    @Override
    public void GiveItems() {
        giveItem(owner, false, getItems());
        giveItem(owner, false, Items.getLamedenichirin());
    }

    @Override
    public void RoleGiven(GameState gameState) {
        getEffects().put(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.DAY);
        EventUtils.registerRoleEvent(this);
        setCanuseblade(true);
        addPower(new DsAssassinCommand(this));
        addPower(new DsSentirCommand(this));
        addPower(new DanseItemPower(this), true);
        Lames.FireResistance.getUsers().put(getPlayer(), Integer.MAX_VALUE);
        getCantHave().add(Lames.FireResistance);
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.addEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0, false, false), EffectWhen.DAY)
                .setItems(new TripleMap<>(getDanseText().getHoverEvent(), getDanseText().getText(), 60*12))
                .setCommands(new TripleMap<>(getSentir().getHoverEvent(), getSentir().getText(), 60*5), new TripleMap<>(getSentirJoueur().getHoverEvent(), getSentirJoueur().getText(), -500), new TripleMap<>(getAssassin().getHoverEvent(), getAssassin().getText(), -500))
                .addParticularites(getKillAssassin().getHoverEvent());
        this.automaticDesc = desc.getText();
    }

    @Override
    public String getName() {
        return "Tanjiro";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Tanjiro;
    }

    @Override
    public void resetCooldown(){}

    @Override
    public String[] Desc() {
        return new String[0];
    }
    @Override
    public TextComponent getComponent() {
        return automaticDesc;
    }
    private TextComponent getSentir() {
        TextComponent dsSentir = new TextComponent("§c/ds sentir");
        dsSentir.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                "§7Vous permet de savoir combien il y a de§c démon§7 dans un rayon de§c 30 blocs§7 autours de vous (§aNezuko§7 est compter dedans). (1x/5m)")}));
        dsSentir.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ds sentir"));
        return dsSentir;
    }
    private TextComponent getSentirJoueur() {
        TextComponent sentir = new TextComponent("§c/ds §csentir <joueur>");
        sentir.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                "§7Vous permet de savoir si un joueur est un§c démons§7 ou non (§aNezuko§7 est compter comme§c démon§7). (1x/partie)")}));
        sentir.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ds sentir "));
        return sentir;
    }
    private TextComponent getAssassin() {
        TextComponent assassin = new TextComponent("§c/ds assassin <joueur>");
        assassin.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                "§7Vous permet de savoir si un joueur est l'§4Assassin§7 ou non, " +
                        "\n§7S'il l'est vous obtiendrez un §ctraqueur§7 vers lui pendant§c 5 minutes§7, " +
                        "\n§7Sinon, vous perdrez§c 2"+AllDesc.coeur+"§7 permanent. (1x/partie)")}));
        assassin.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ds assassin "));
        return assassin;
    }
    private TextComponent getDanseText() {
        TextComponent danseItem = new TextComponent(" §7\"§6Danse §6du dieu §6du Feu§7\"");
        danseItem.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                "§r§7Vous obtenez pendant§l§c 5 minutes§r§7 l'Effet §9Resistance 1 §r§7et vous devenez §6invulnerable aux degâts de feu, §r§7également,\n"+
                "§r§7pendant§l§c 1 minute§r§7 vos coups §6embrasent§r§7 les joueurs touchés, de plus le joueur possédant le role de §aNezuko§r§7\n"+
                "§r§7obtiendra l'effet §espeed 2§r§7 pendant§l§c 5 minutes§r§7, à la dissipation des effets vous perdez  §c2"+AllDesc.coeur+"§r§7 permanent§7. (1x/12m)."
        )}));
        return danseItem;
    }
    private TextComponent getKillAssassin() {
        TextComponent texte = new TextComponent("§6§lParticularité");
        texte.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                "§7Aléatoirement, un membre du camp des§c démons§7 est choisis pour devenir l'§4Assassin§7,\n§7Si vous parvenez à le tuer vous obtiendrez l'effet§c§l Force I§7 permanent")}));
        return texte;
    }
    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @EventHandler
    private void onAssassinProc(final ProcAssassinEvent event) {
        this.gameAssassin = event.getAssassin();
    }
    @Override
    public Soufle getSoufle() {
        return Soufle.EAU;
    }

    @EventHandler
    private void onUHCPlayerKill(UHCPlayerKillEvent event){
        if (event.getPlayerKiller() != null) {
            if (event.getPlayerKiller().getUniqueId().equals(getPlayer())) {
                if (this.gameAssassin != null) {
                    if (this.gameAssassin.getUuid().equals(event.getVictim().getUniqueId())) {
                        getEffects().put(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
                        event.getPlayerKiller().sendMessage("§7Vous avez venger votre famille, vous recevez l'effet§c Force I§7 de manière§c permanente");
                    }
                }
            }
        }
    }

    private static class DsAssassinCommand extends CommandPower {

        private final Tanjiro tanjiro;

        public DsAssassinCommand(@NonNull Tanjiro role) {
            super("/ds assassin <joueur>", "assassin", new Cooldown(-500), role, CommandType.DS,
                    "§7Vous permet de savoir si un joueur est l'§4Assassin§7 ou non, ",
                            "§7S'il l'est vous obtiendrez un §ctraqueur§7 vers lui pendant§c 5 minutes§7, ",
                            "§7Sinon, vous perdrez§c 2❤§7 permanent.");
            setMaxUse(1);
            this.tanjiro = role;
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length != 2) {
                player.sendMessage("§cLa commande est§6 /ds assassin <joueur>");
                return false;
            }
            if (this.tanjiro.gameAssassin != null) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    boolean assa = target.getUniqueId().equals(this.tanjiro.gameAssassin.getUuid());
                    player.sendMessage("§c"+target.getName()+"§f "+(assa ? "§7est l'§4Assassin" : "§7n'est§c pas§7 l'§4Assassin"));
                    if (!assa) {
                        this.tanjiro.setMaxHealth(this.tanjiro.getMaxHealth()-4);
                    } else {
                        this.tanjiro.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*5, 0, false, false), EffectWhen.NOW);
                        this.tanjiro.giveItem(player, true, new ItemBuilder(Material.COMPASS).setName("§4§lTraqueur").toItemStack());
                        new CompassRunnable(target.getUniqueId(), player.getUniqueId());
                    }
                    return true;
                }
            }
            player.sendMessage("§cL'assassin n'a pas été désigné");
            return false;
        }
        private static class CompassRunnable extends BukkitRunnable {

            private int timeRemaining = 60*5;
            private final UUID targetUUID;
            private final UUID ownerUUID;

            private CompassRunnable(UUID targetUUID, UUID ownerUUID) {
                this.targetUUID = targetUUID;
                this.ownerUUID = ownerUUID;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                Player target = Bukkit.getPlayer(targetUUID);
                Player owner = Bukkit.getPlayer(ownerUUID);
                if (target !=null && owner != null) {
                    owner.setCompassTarget(target.getLocation());
                    this.timeRemaining--;
                }
                if (this.timeRemaining == 0 || !GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                }
            }
        }
    }
    private static class DsSentirCommand extends CommandPower {

        private final SentirSimplePower sentirPower;
        private final SentirJoueurPower sentirJoueurPower;

        public DsSentirCommand(@NonNull Tanjiro role) {
            super("/ds sentir", "sentir", null, role, CommandType.DS);
            this.sentirPower = new SentirSimplePower(role);
            role.addPower(this.sentirPower);
            this.sentirJoueurPower = new SentirJoueurPower(role);
            role.addPower(this.sentirJoueurPower);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 1) {
                return this.sentirPower.checkUse(player, map);
            } else if (args.length == 2){
                return this.sentirJoueurPower.checkUse(player, map);
            }
            return false;
        }
        private static class SentirSimplePower extends Power {

            public SentirSimplePower(@NonNull Tanjiro role) {
                super("/ds sentir", new Cooldown(60*5), role);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                int amountDemon = 0;
                for (Player around : Loc.getNearbyPlayersExcept(player, 30)) {
                    if (!getRole().getGameState().hasRoleNull(around.getUniqueId())) {
                        final RoleBase role = getRole().getGameState().getGamePlayer().get(around.getUniqueId()).getRole();
                        boolean demon = role.getTeam().equals(TeamList.Demon) || role instanceof NezukoV2 || role.getOriginTeam().equals(TeamList.Demon);
                        if (demon) {
                            amountDemon++;
                        }
                    }
                }
                player.sendMessage("§7Il y a§c "+amountDemon+" démon§7(§cs§7) autours de vous");
                return true;
            }
        }
        private static class SentirJoueurPower extends Power {

            public SentirJoueurPower(@NonNull Tanjiro role) {
                super("/ds sentir <joueur>", null, role);
                setMaxUse(1);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final String[] args = (String[]) map.get("args");
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (!getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                        final RoleBase role = getRole().getGameState().getGamePlayer().get(target.getUniqueId()).getRole();
                        boolean demon = role.getTeam().equals(TeamList.Demon) || role instanceof NezukoV2 || role.getOriginTeam().equals(TeamList.Demon);
                        player.sendMessage("§c"+target.getName()+(demon ? "§7 est un§c démon" : "§7 n'est pas un§c démon"));
                        return true;
                    }
                } else {
                    player.sendMessage("§c"+args[1]+" n'est pas connectée !");
                }
                return false;
            }
        }
    }
    private static class DanseItemPower extends ItemPower implements Listener{

        protected DanseItemPower(@NonNull Tanjiro role) {
            super("§6Danse du dieu du feu", new Cooldown(60*12), new ItemBuilder(Material.BLAZE_ROD).setName("§6Danse du dieu du feu"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                player.sendMessage("§7Vous utilisez votre§6 Danse du dieu du Feu");
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), EffectWhen.NOW);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*60*5, 0, false, false), EffectWhen.NOW);
                List<Player> liste = getRole().getListPlayerFromRole(NezukoV2.class);
                if (!liste.isEmpty()) {
                    final Location loc = event.getPlayer().getLocation();
                    final DecimalFormat df = new DecimalFormat("0");
                    for (final Player p : liste) {
                        p.sendMessage("§aTanjiro§7 a utiliser sa§6 danse du dieu du feu§7 en§c x: "+
                                df.format(loc.getBlockX())+", y: "+
                                df.format(loc.getBlockY())+", z: "+
                                df.format(loc.getBlockZ()));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 1, false, false), true);
                    }
                }
                EventUtils.registerRoleEvent(this);
                event.setCancelled(true);
                new DanseRunnable(this, this.getRole().getGameState());
                return true;
            }
            return false;
        }
        @EventHandler
        private void onBattle(final EntityDamageByEntityEvent event) {
            if (event.getDamager().getWorld().getName().equals("enmuv2_duel"))return;
            if (event.getDamager().getUniqueId().equals(getRole().getPlayer())) {
                event.getEntity().setFireTicks(event.getEntity().getFireTicks()+150);
            }
        }
        private static class DanseRunnable extends BukkitRunnable {

            private final DanseItemPower power;
            private int timeRemaining = 60*5;
            private final GameState gameState;

            private DanseRunnable(DanseItemPower power, GameState gameState) {
                this.power = power;
                this.gameState = gameState;
                power.getRole().getGamePlayer().getActionBarManager().addToActionBar("tanjiro.danse", "§bTemp restant:§c "+StringUtils.secondsTowardsBeautiful(this.timeRemaining));
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (!this.power.getRole().getGamePlayer().isAlive()) {
                    EventUtils.unregisterEvents(this.power);
                    cancel();
                    return;
                }
                timeRemaining--;
                this.power.getRole().getGamePlayer().getActionBarManager().updateActionBar("tanjiro.danse", "§bTemp restant:§c "+StringUtils.secondsTowardsBeautiful(this.timeRemaining));
                final Player player = Bukkit.getPlayer(this.power.getRole().getPlayer());
                if (player != null) {
                    MathUtil.sendCircleParticle(EnumParticle.FLAME, player.getLocation(), 1, 15);
                }
                if (this.timeRemaining == 60*4) {
                    EventUtils.unregisterEvents(this.power);
                    this.power.getRole().getGamePlayer().sendMessage("§7Vous ne mettrez plus les joueurs en§c feu§7.");
                }
                if (this.timeRemaining <= 0) {
                    this.power.getRole().getGamePlayer().sendMessage("§7Vous devez arrêter de danser suite à votre fatigue...");
                    this.power.getRole().setMaxHealth(this.power.getRole().getMaxHealth()-4);
                    cancel();
                }
            }
        }
    }
}