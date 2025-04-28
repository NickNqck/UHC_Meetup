package fr.nicknqck.roles.ns.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.power.CooldownFinishEvent;
import fr.nicknqck.events.custom.roles.ns.IzanamiFinishEvent;
import fr.nicknqck.events.custom.roles.ns.IzanamiStartEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.akatsuki.ItachiV2;
import fr.nicknqck.roles.ns.builders.EUchiwaType;
import fr.nicknqck.roles.ns.builders.IUchiwa;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.ns.power.Genjutsu;
import fr.nicknqck.roles.ns.power.Izanagi;
import fr.nicknqck.roles.ns.power.IzanamiV2;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ShisuiSolo extends NSRoles implements Listener, IUchiwa {

    private RoleBase infected = null;

    public ShisuiSolo(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.GENIE;
    }

    @Override
    public String getName() {
        return "Shisui";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Shisui;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Shisui;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine(this.infected == null ?
                        "§7Votre§e Izanami§7 ne peut pas avoir les missions: \"§fRester loin de vous (§c30 blocs§f) pendant§c 1 minutes§7\"§7 et \"§fRester proche de la cible (§c20 blocs§f) pendant§c 5 minutes§7\""
                        : "")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addKnowedRole(ItachiV2.class);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, false, false), EffectWhen.PERMANENT);
        setChakraType(Chakras.KATON);
        addPower(new Genjutsu(this), true);
        addPower(new KotoAmatsukamiPower(this), true);
        addPower(new SusanoPower(this), true);
        addPower(new Izanagi(this));
        addPower(new ShurikenjutsuCommand(this));
        EventUtils.registerRoleEvent(this);
        super.RoleGiven(gameState);
    }
    @EventHandler
    private void onEndIzanami(@NonNull final IzanamiFinishEvent event) {
        if (event.getInfecteur().getPlayer().equals(this.getPlayer())) {//donc si notre Shisui est l'infecteur
            this.infected = event.getInfected();
            event.getInfected().getGamePlayer().startChatWith("§e"+event.getInfected().getName(), "!", this.getClass());
            event.getTarget().sendMessage("§7Vous possédez maintenant un chat commun avec§e Shisui§7, il suffira de commencer votre message par un§c !");
            getGamePlayer().startChatWith("§e"+getName(), "!", event.getInfected().getClass());
            event.getOwner().sendMessage("§7Vous possédez maintenant un chat commmun avec §c"+event.getTarget().getName()+"§7, il suffira de commencer votre message par un§c !");
        }
    }
    @EventHandler
    private void onStartIzanami(@NonNull final IzanamiStartEvent event) {//Toute ces choses pour ne pas avoir certaines missions
        @NonNull final List<IzanamiV2.MissionUser> muLIST = new ArrayList<>();
        @NonNull final List<IzanamiV2.MissionTarget> mtLIST = new ArrayList<>();
        for (@NonNull final IzanamiV2.MissionUser mu : event.getMissionUserList()) {
            if (mu.equals(IzanamiV2.MissionUser.Rester)) {
                continue;
            }
            muLIST.add(mu);
        }
        if (muLIST.size() < 2) {
            for (IzanamiV2.MissionUser mu : IzanamiV2.MissionUser.values()) {
                if (muLIST.size() == 2)break;
                if (mu.equals(IzanamiV2.MissionUser.Rester))continue;
                if (!muLIST.contains(mu)) {
                    muLIST.add(mu);
                }
            }
        }
        for (@NonNull final IzanamiV2.MissionTarget mt : event.getMissionTargetList()) {
            if (mt.equals(IzanamiV2.MissionTarget.Distance)) {
                continue;
            }
            mtLIST.add(mt);
        }
        if (mtLIST.isEmpty()) {
            for (IzanamiV2.MissionTarget mt : IzanamiV2.MissionTarget.values()) {
                if (mtLIST.size() == 1)break;
                if (mt.equals(IzanamiV2.MissionTarget.Distance))continue;
                mtLIST.add(mt);
            }
        }
        event.getMissionTargetList().clear();
        event.getMissionTargetList().addAll(mtLIST);
        event.getMissionUserList().clear();
        event.getMissionUserList().addAll(muLIST);
    }

    @Override
    public @NonNull EUchiwaType getUchiwaType() {
        return EUchiwaType.INUTILE;
    }

    private static class KotoAmatsukamiPower extends ItemPower implements Listener {

        private final ShisuiSolo shisuiSolo;

        private KotoAmatsukamiPower(@NonNull ShisuiSolo role) {
            super("Kotoamatsukami", null, new ItemBuilder(Material.NETHER_STAR).setName("§cKotoamatsukami"), role,
                    "§7Ouvre un menu vous permettant de sélectionner un joueur proche de vous à moins de§c 50 blocs§7",
                    "",
                    "§7Une fois choisis celà démarrera une infection fonctionnant avec un système de§c points§7: ",
                    "",
                    "§8 -§7 5 blocs§2 + 25 points",
                    "§8 -§7 10 blocs§a + 15 points",
                    "§8 -§7 20 blocs§6 + 10 points",
                    "§8 -§7 40 blocs§c + 5 points",
                    "",
                    "§7Une fois arriver à§6 2500 points§7 le joueur visé rejoindra votre camp et devra donc gagnez avec vous.");
            this.shisuiSolo = role;
            setMaxUse(1);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final List<Player> playerList = new ArrayList<>(Loc.getNearbyPlayersExcept(player, 50));
            if (playerList.isEmpty()) {
                player.sendMessage("§cIl n'y a pas asser de personnes autours de vous pour utiliser cette technique.");
                return false;
            }
            final Inventory inv = Bukkit.createInventory(player, 54, "§7(§c!§7)§c Kotoamatsukami");
            for (@NonNull final Player target : playerList) {
                inv.addItem(new ItemBuilder(GlobalUtils.getPlayerHead(target.getName())).setName("§a"+target.getName()).toItemStack());
            }
            player.openInventory(inv);
            return false;
        }
        @EventHandler
        private void onInventoryClick(@NonNull final InventoryClickEvent event) {
            if (event.getCurrentItem() == null)return;
            if (event.getCurrentItem().getItemMeta() == null)return;
            if (!event.getCurrentItem().getItemMeta().hasDisplayName())return;
            if (event.getInventory() == null)return;
            if (event.getInventory().getTitle() == null)return;
            if (event.getWhoClicked() == null)return;
            if (!event.getWhoClicked().getUniqueId().equals(getRole().getPlayer()))return;
            if (event.getInventory().getTitle().equalsIgnoreCase("§7(§c!§7)§c Kotoamatsukami")) {
                event.setCancelled(true);
                String name = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
                final Player target = Bukkit.getPlayer(name);
                System.out.println(name);
                if (target != null) {
                    event.getWhoClicked().closeInventory();
                    if (getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                        event.getWhoClicked().sendMessage("§cImpossible de démarrer le§a Kotoamatsukami§c, §b"+target.getName()+"§c n'a pas de rôle.");
                        return;
                    }
                    new KotoRunnable(this.getRole().getGameState(), this, getRole().getGameState().getGamePlayer().get(target.getUniqueId())).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                    setUse(getUse()+1);
                    event.getWhoClicked().sendMessage("§7Démarrage du§c Kotoamatsukami");
                }
            }
        }
        private static class KotoRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final KotoAmatsukamiPower power;
            private final GamePlayer gameTarget;
            private int points = 0;

            private KotoRunnable(GameState gameState, KotoAmatsukamiPower power, GamePlayer uuidTarget) {
                this.gameState = gameState;
                this.power = power;
                this.gameTarget = uuidTarget;
                power.getRole().getGamePlayer().getActionBarManager().addToActionBar("shisui.kotoamatsukami", "§bNombre de points: §c"+this.points+"§b/§62500");
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.points >= 2500) {
                    this.power.getRole().getGamePlayer().sendMessage("§7Votre§c Kotoamatsukami§7 à réussis,§c "+this.gameTarget.getPlayerName()+"§7 rejoint votre équipe.");
                    this.power.getRole().getGamePlayer().getActionBarManager().removeInActionBar("shisui.kotoamatsukami");
                    cancel();
                    return;
                } else
                {
                    final Location loc = this.power.getRole().getGamePlayer().getLastLocation();
                    final List<Player> playerList = new ArrayList<>(Loc.getNearbyPlayers(loc, 40));
                    if (playerList.isEmpty())return;
                    for (@NonNull final Player player : playerList) {
                        if (!player.getUniqueId().equals(this.gameTarget.getUuid()))continue;
                        if (loc.distance(player.getLocation()) <= 40) {
                            this.points+=5;//Moins de 40 blocs +5 points
                        }
                        if (loc.distance(player.getLocation()) <= 20) {
                            this.points+=5;//Moins de 20 blocs +10 points
                        }
                        if (loc.distance(player.getLocation()) <= 10) {
                            this.points+=5;//Moins de 10 blocs +15 points
                        }
                        if (loc.distance(player.getLocation()) <= 5) {
                            this.points+=10;//Moins de 5 blocs + 25 points
                        }
                    }
                }
                if (this.power.shisuiSolo.infected != null) {
                    final Location loc = this.power.shisuiSolo.infected.getGamePlayer().getLastLocation();
                    final List<Player> playerList = new ArrayList<>(Loc.getNearbyPlayers(loc, 20));
                    if (playerList.isEmpty())return;
                    for (@NonNull final Player player : playerList) {
                        if (!player.getUniqueId().equals(this.gameTarget.getUuid()))continue;
                        if (loc.distance(player.getLocation()) <= 40) {
                            this.points+=5;//Moins de 40 blocs +5 points
                        }
                        if (loc.distance(player.getLocation()) <= 20) {
                            this.points+=5;//Moins de 20 blocs +10 points
                        }
                        if (loc.distance(player.getLocation()) <= 10) {
                            this.points+=5;//Moins de 10 blocs +15 points
                        }
                        if (loc.distance(player.getLocation()) <= 5) {
                            this.points+=10;//Moins de 5 blocs + 25 points
                        }
                    }
                }
                power.getRole().getGamePlayer().getActionBarManager().updateActionBar("shisui.kotoamatsukami", "§bNombre de points: §c"+this.points+"§b/§62500");
            }
        }
    }
    private static class ShurikenjutsuCommand extends CommandPower implements Listener{

        private boolean activate = false;
        private final Cooldown cooldown;

        public ShurikenjutsuCommand(@NonNull RoleBase role) {
            super("/ns shurikenjutsu", "shurikenjutsu", null, role, CommandType.NS,
                    "§7Vous permet d'§aactiver§7 ou de§c désactiver§7 votre§c Shuriken Jutsu§7",
                    "",
                    "§7Quand il est§a activé§7 en faisant§c clique droit§7 avec une§c épée§7 vous pourrez lancé une§c flèche§7 qui,",
                    "§7S'il elle touche un joueur lui infligera un effet de§c stun§7 pendant§c 0,5s",
                    "",
                    "§cAttention, par défaut ce pouvoir est§4 désactiver");
            EventUtils.registerRoleEvent(this);
            this.cooldown = new Cooldown(120);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (this.activate) {
                player.sendMessage("§7Vous avez§c désactivé§7 votre§6 Shuriken Jutsu");
                this.activate = false;
            } else {
                player.sendMessage("§7Vous avez§a activé§7 votre§6 Shuriken Jutsu");
                this.activate = true;
            }
            return true;
        }
        @EventHandler
        private void onInteract(@NonNull final PlayerInteractEvent event) {
            if (!event.getPlayer().getUniqueId().equals(getRole().getPlayer()))return;
            if (event.getPlayer().getItemInHand() != null) {
                if (!this.activate)return;
                if (event.getPlayer().getItemInHand().getType().name().contains("SWORD")) {
                    if (!event.getAction().name().contains("RIGHT"))return;
                    if (this.cooldown.isInCooldown()) {
                        event.getPlayer().sendMessage("§cVous êtes en cooldown:§b "+ StringUtils.secondsTowardsBeautiful(cooldown.getCooldownRemaining()));
                        return;
                    }
                    @NonNull final Arrow arrow = event.getPlayer().launchProjectile(Arrow.class);
                    arrow.setBounce(false);
                    arrow.setVelocity(event.getPlayer().getLocation().getDirection().normalize().multiply(5));
                    event.getPlayer().playSound(event.getPlayer().getEyeLocation(), Sound.SHOOT_ARROW, 8, 1);
                    this.cooldown.use();
                    event.getPlayer().sendMessage("§7Vous lancez un§c Shuriken§7.");
                    arrow.setMetadata("shisui.stunarrow", new FixedMetadataValue(Main.getInstance(), arrow.getUniqueId()));
                    event.setCancelled(true);
                }
            }
        }
        @EventHandler
        private void CooldownEndEvent(@NonNull final CooldownFinishEvent event) {
            if (event.getCooldown().getUniqueId().equals(this.cooldown.getUniqueId())) {
                getRole().getGamePlayer().sendMessage("§7Vous pouvez à nouveau lancer un§c Shuriken§7.");
            }
        }
        @EventHandler
        private void EntityDamageByEntityEvent(@NonNull final EntityDamageByEntityEvent event) {
            if (event.getDamager() instanceof Arrow) {
                if (!(((Arrow) event.getDamager()).getShooter() instanceof Player))return;
                if (!(event.getEntity() instanceof Player))return;
                if (!((Player) ((Arrow) event.getDamager()).getShooter()).getUniqueId().equals(getRole().getPlayer()))return;
                if (event.getDamager().hasMetadata("shisui.stunarrow")) {
                    if (getRole().getGameState().getGamePlayer().containsKey(event.getEntity().getUniqueId())) {
                        getRole().getGameState().getGamePlayer().get(event.getEntity().getUniqueId()).stun(10, false, false);
                        ((Player) event.getEntity()).setHealth(Math.max(1.0, ((Player) event.getEntity()).getMaxHealth()-1.0));
                        event.getDamager().removeMetadata("shisui.stunarrow", Main.getInstance());
                        ((Player) ((Arrow) event.getDamager()).getShooter()).sendMessage("§7Votre§c Shuriken§7 a§a stun§c "+((Player) event.getEntity()).getDisplayName());
                    }
                }
            }
        }
    }
    private static class SusanoPower extends ItemPower {

        protected SusanoPower(@NonNull RoleBase role) {
            super("Susano (Obito)", new Cooldown(60*20), new ItemBuilder(Material.NETHER_STAR).setName("§c§lSusanô"), role,
                    "§7Vous permet d'obtenir l'effet§c Résistance I§7 pendant§c 5 minutes§7. (1x/20m)");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                new SusanoRunnable(this.getRole().getGameState(), this.getRole().getGamePlayer());
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), EffectWhen.NOW);
                player.sendMessage("§cActivation du§l Susanô§c.");
                return true;
            }
            return false;
        }
        private static class SusanoRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final GamePlayer gamePlayer;
            private int timeLeft = 60*5;

            private SusanoRunnable(GameState gameState, GamePlayer gamePlayer) {
                this.gameState = gameState;
                this.gamePlayer = gamePlayer;
                this.gamePlayer.getActionBarManager().addToActionBar("shisui.susano", "§bTemp restant du§c§l Susanô§b: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    this.gamePlayer.getActionBarManager().removeInActionBar("shisui.susano");
                    this.gamePlayer.sendMessage("§cVotre§l Susanô§c s'arrête");
                    cancel();
                    return;
                }
                this.timeLeft--;
                this.gamePlayer.getActionBarManager().updateActionBar("shisui.susano", "§bTemp restant du§c§l Susanô§b: "+StringUtils.secondsTowardsBeautiful(this.timeLeft));
            }
        }
    }
}
