package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.roles.ns.builders.EUchiwaType;
import fr.nicknqck.roles.ns.builders.IUchiwa;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.roles.ns.solo.DanzoV2;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Fugaku extends ShinobiRoles implements IUchiwa {

    public Fugaku(UUID player) {
        super(player);
    }

    @Override
    public @NonNull EUchiwaType getUchiwaType() {
        return EUchiwaType.INUTILE;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0), EffectWhen.PERMANENT);
        new CroiserRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        addPower(new OeilPower(this), true);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[] {
                EChakras.KATON
        };
    }

    @Override
    public String getName() {
        return "Fugaku";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Fugaku;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    private static final class OeilPower extends ItemPower implements Listener{

        private final AffaiblissementPower affaiblissementPower;
        private final AttaquePower attaquePower;
        private final PlaceAuCombat placeAuCombat;

        public OeilPower(@NonNull RoleBase role) {
            super("§cOeil maléfique§r", new Cooldown(1), new ItemBuilder(Material.EYE_OF_ENDER).setName("§cOeil maléfique"), role,
                    "§7Ouvre un menu donnant accès à§c 3 pouvoirs§7:",
                            "",
                            AllDesc.point+"§fAffaiblissement§7: Donne à la cible l'effet§8 Weakness I§7 pendant§c 18 secondes§7. (1x/2min)",
                            "",
                            AllDesc.point+"§cAttaque§7: Vous téléporte dans un rayon de§c 10 blocs§7 autours de la cible. (1x/5min)",
                            "",
                            AllDesc.point+"§6§lPlace au combat !§7: Pendant§c 1 minutes§7 vous obtenez des §cbonus§7: (1x/10min)",
                            "",
                            AllDesc.tab+"§7Vous serez insensible à tout les projectiles",
                            AllDesc.tab+"§7Vous infligerez §c+25%§7 de dégat au joueur viser",
                            AllDesc.tab+"§7Vous subirez §c-25%§7 de dégat de sa part"
            );
            setSendCooldown(false);
            setShowCdInDesc(false);
            this.affaiblissementPower = new AffaiblissementPower(role);
            this.attaquePower = new AttaquePower(role);
            this.placeAuCombat = new PlaceAuCombat(role);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (map.containsKey("event")) {
                    openFirstInventory(player);
                    return true;
                }
            }
            return false;
        }
        @EventHandler
        public void onEyeThrow(PlayerInteractEvent event) {
            if (event.getAction().name().contains("RIGHT") && event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                if (event.getItem() != null && event.getItem().getType() == Material.EYE_OF_ENDER) {
                    event.setCancelled(true);
                }
            }
        }
        private void openFirstInventory(Player player) {
            final FastInv fastInv = new FastInv(27, "§cOeil Maléfique");
            fastInv.setItems(fastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setDurability(7).toItemStack());
            fastInv.setItem(12, new ItemBuilder(Material.FERMENTED_SPIDER_EYE)
                    .setName("§r§fAffaiblissement")
                    .setLore("§7Inflige l'effet§8 Weakness I§7 pendant §c18 secondes§7 a un joueur visée",
                            "",
                            (this.affaiblissementPower.getCooldown().getCooldownRemaining() <= 0 ? "§cPouvoir Utilisable" : "§7Cooldown: §c"+ StringUtils.secondsTowardsBeautiful(this.affaiblissementPower.getCooldown().getCooldownRemaining())))
                    .toItemStack(), event -> {
                event.setCancelled(true);
                this.affaiblissementPower.checkUse(player, new HashMap<>());
            });
            fastInv.setItem(14, new ItemBuilder(Material.IRON_SWORD)
                    .hideAllAttributes()
                    .setName("§cAttaque")
                    .setLore("§7Vous permet de vous téléporter dans un rayon de§c 10 blocs§7 autours d'un joueur visée",
                            "",
                            (this.attaquePower.getCooldown().getCooldownRemaining() <= 0 ? "§cPouvoir Utilisable":"§7Cooldown:§c "+StringUtils.secondsTowardsBeautiful(this.attaquePower.getCooldown().getCooldownRemaining())))
                    .toItemStack(), event -> {
                event.setCancelled(true);
                this.attaquePower.checkUse(player, new HashMap<>());
            });
            fastInv.setItem(16, new ItemBuilder(Material.BOW)
                    .setName("§6§lPlace au combat !")
                    .setLore("§7En visant un joueur vous obtiendrez des§c bonus§7 pendant§c 1 minutes§7: ",
                            "",
                            AllDesc.tab+"§7Vous serez insensible à tout les projectiles",
                            AllDesc.tab+"§7Vous infligerez §c+25%§7 de dégât au joueur viser",
                            AllDesc.tab+"§7Vous subirez §c-25%§7 de dégât de sa part",
                            "",
                            (this.placeAuCombat.getCooldown().getCooldownRemaining() <= 0 ? "§cPouvoir Utilisable" : "§7Cooldown:§c "+StringUtils.secondsTowardsBeautiful(this.placeAuCombat.getCooldown().getCooldownRemaining())))
                    .toItemStack(), event -> {
                event.setCancelled(true);
                this.placeAuCombat.checkUse(player, new HashMap<>());
            });
            fastInv.open(player);
        }
        private static final class AffaiblissementPower extends Power {

            public AffaiblissementPower(@NonNull RoleBase role) {
                super("§fAffaiblissement§r", new Cooldown(120), role);
                setShowInDesc(false);
                role.addPower(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (map.isEmpty()) {
                    final PaginatedFastInv paginatedFastInv = new PaginatedFastInv(9*5, "§cOeil Maléfique§7 ->§f Affaiblissement");
                    paginatedFastInv.setItems(paginatedFastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
                    final List<Integer> integerList = new ArrayList<>();
                    for (int line = 0; line <= 2; line++ ) {
                        for (int slot = 10+(line*9); slot <= (10+(line*9))+6; slot++) {
                            integerList.add(slot);
                        }
                    }
                    paginatedFastInv.setContentSlots(integerList);
                    paginatedFastInv.previousPageItem(3, p -> new ItemBuilder(Material.ARROW).setName("§fPage " + p + "/" + paginatedFastInv.lastPage()).toItemStack());

                    paginatedFastInv.nextPageItem(5, p -> new ItemBuilder(Material.ARROW).setName("§fPage " + p + "/" + paginatedFastInv.lastPage()).toItemStack());

                    final List<GamePlayer> gamePlayerList = Loc.getNearbyGamePlayers(player.getLocation(), 30);
                    for (GamePlayer gamePlayer : gamePlayerList) {
                        if (gamePlayer == null)continue;
                        if (!gamePlayer.check())continue;
                        final Player target = Bukkit.getPlayer(gamePlayer.getUuid());
                        if (target == null)continue;
                        if (target.hasPotionEffect(PotionEffectType.INVISIBILITY))continue;
                        if (target.getUniqueId().equals(getRole().getPlayer()))continue;
                        //Le pouvoir ne touche pas les joueurs invisibles
                        paginatedFastInv.addContent(new ItemBuilder(GlobalUtils.getAsyncPlayerHead(gamePlayer.getUuid()))
                                .setName("§a"+target.getName())
                                .toItemStack(), event -> {
                            final Map<String, Object> test = new HashMap<>();
                            test.put("target", target);
                            test.put("gameplayer", gamePlayer);
                            checkUse(player, test);
                        });
                    }
                    paginatedFastInv.open(player);
                } else {
                    if (map.containsKey("target") && map.containsKey("gameplayer") && map.get("target") instanceof Player && map.get("gameplayer") instanceof GamePlayer) {
                        final Player target = (Player) map.get("target");
                        final GamePlayer gamePlayer = (GamePlayer) map.get("gameplayer");
                        gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*18, 0, false, false), EffectWhen.NOW);
                        target.sendMessage("§aFugaku§7 vous fait sentir impuissant");
                        player.sendMessage("§7Vous avez donner à §c"+target.getName()+"§7 l'effet§8 Weakness I§7 pendant§c 18 secondes");
                        player.closeInventory();
                        return true;
                    }
                }
                return false;
            }
        }
        private static final class AttaquePower extends Power {

            public AttaquePower(@NonNull RoleBase role) {
                super("§cAttaque§r", new Cooldown(60*5), role);
                setShowInDesc(false);
                role.addPower(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (map.isEmpty()) {
                    final PaginatedFastInv paginatedFastInv = new PaginatedFastInv(9*5, "§cOeil Maléfique§7 ->§c Attaque");
                    paginatedFastInv.setItems(paginatedFastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
                    final List<Integer> integerList = new ArrayList<>();
                    for (int line = 0; line <= 2; line++ ) {
                        for (int slot = 10+(line*9); slot <= (10+(line*9))+6; slot++) {
                            integerList.add(slot);
                        }
                    }
                    paginatedFastInv.setContentSlots(integerList);
                    paginatedFastInv.previousPageItem(3, p -> new ItemBuilder(Material.ARROW).setName("§fPage " + p + "/" + paginatedFastInv.lastPage()).toItemStack());

                    paginatedFastInv.nextPageItem(5, p -> new ItemBuilder(Material.ARROW).setName("§fPage " + p + "/" + paginatedFastInv.lastPage()).toItemStack());

                    final List<GamePlayer> gamePlayerList = Loc.getNearbyGamePlayers(player.getLocation(), 30);
                    for (GamePlayer gamePlayer : gamePlayerList) {
                        if (gamePlayer == null)continue;
                        if (!gamePlayer.check())continue;
                        final Player target = Bukkit.getPlayer(gamePlayer.getUuid());
                        if (target == null)continue;
                        if (target.hasPotionEffect(PotionEffectType.INVISIBILITY))continue;
                        if (target.getUniqueId().equals(getRole().getPlayer()))continue;
                        //Le pouvoir ne touche pas les joueurs invisibles
                        paginatedFastInv.addContent(new ItemBuilder(GlobalUtils.getAsyncPlayerHead(gamePlayer.getUuid()))
                                .setName("§a"+target.getName())
                                .toItemStack(), event -> {
                            final Map<String, Object> test = new HashMap<>();
                            test.put("target", target);
                            test.put("gameplayer", gamePlayer);
                            checkUse(player, test);
                        });
                    }
                    paginatedFastInv.open(player);
                } else {
                    if (map.containsKey("target") && map.containsKey("gameplayer") && map.get("target") instanceof Player && map.get("gameplayer") instanceof GamePlayer) {
                        final Player target = (Player) map.get("target");
                        Location loc = Loc.getRandomLocationAroundPlayer(target, 10);
                        player.teleport(loc);
                        target.sendMessage("§7Vous sentez quelque chose de nouveau autours de vous.");
                        player.sendMessage("§7Vous vous êtes téléportez autours de§c "+target.getName());
                        player.closeInventory();
                        return true;
                    }
                }
                return false;
            }
        }
        private static final class PlaceAuCombat extends Power {

            public PlaceAuCombat(@NonNull RoleBase role) {
                super("§6Place au combat !§r", new Cooldown(60*10), role);
                setShowInDesc(false);
                role.addPower(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (map.isEmpty()) {
                    final PaginatedFastInv paginatedFastInv = new PaginatedFastInv(9*5, "§cOeil Maléfique§7 ->§6 Place au combat !");
                    paginatedFastInv.setItems(paginatedFastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
                    final List<Integer> integerList = new ArrayList<>();
                    for (int line = 0; line <= 2; line++ ) {
                        for (int slot = 10+(line*9); slot <= (10+(line*9))+6; slot++) {
                            integerList.add(slot);
                        }
                    }
                    paginatedFastInv.setContentSlots(integerList);
                    paginatedFastInv.previousPageItem(3, p -> new ItemBuilder(Material.ARROW).setName("§fPage " + p + "/" + paginatedFastInv.lastPage()).toItemStack());

                    paginatedFastInv.nextPageItem(5, p -> new ItemBuilder(Material.ARROW).setName("§fPage " + p + "/" + paginatedFastInv.lastPage()).toItemStack());

                    final List<GamePlayer> gamePlayerList = Loc.getNearbyGamePlayers(player.getLocation(), 30);
                    for (GamePlayer gamePlayer : gamePlayerList) {
                        if (gamePlayer == null)continue;
                        if (!gamePlayer.check())continue;
                        final Player target = Bukkit.getPlayer(gamePlayer.getUuid());
                        if (target == null)continue;
                        if (target.hasPotionEffect(PotionEffectType.INVISIBILITY))continue;
                        if (target.getUniqueId().equals(getRole().getPlayer()))continue;
                        //Le pouvoir ne touche pas les joueurs invisibles
                        paginatedFastInv.addContent(new ItemBuilder(GlobalUtils.getAsyncPlayerHead(gamePlayer.getUuid()))
                                .setName("§a"+target.getName())
                                .toItemStack(), event -> {
                            final Map<String, Object> test = new HashMap<>();
                            test.put("target", target);
                            test.put("gameplayer", gamePlayer);
                            checkUse(player, test);
                        });
                    }
                    paginatedFastInv.open(player);
                } else {
                    if (map.containsKey("target") && map.containsKey("gameplayer") && map.get("target") instanceof Player && map.get("gameplayer") instanceof GamePlayer) {
                        final Player target = (Player) map.get("target");
                        new CombatManager(getRole(), target);
                        player.closeInventory();
                        return true;
                    }
                }
                return false;
            }
            private static class CombatManager implements Listener {

                private final RoleBase fugaku;
                private final UUID uTarget;

                public CombatManager(RoleBase fugaku, Player target) {
                    this.fugaku = fugaku;
                    this.uTarget = target.getUniqueId();
                    EventUtils.registerEvents(this);
                    new CombatRunnable(this).runTaskTimer(Main.getInstance(), 0, 20);
                }
                @EventHandler
                private void onDamage(UHCPlayerBattleEvent event) {
                    if (event.isPatch()) {
                        if (event.getDamager().getUuid().equals(fugaku.getPlayer())) {
                            if (event.getVictim().getUuid().equals(uTarget)) {
                                event.setDamage(event.getDamage()*1.25);
                            }
                        } else if (event.getDamager().getUuid().equals(uTarget)) {
                            if (event.getVictim().getUuid().equals(fugaku.getPlayer())) {
                                event.setDamage(event.getDamage()*0.75);
                            }
                        }
                    }
                }
                @EventHandler
                private void onBowDamage(EntityDamageEvent event) {
                    if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                        if (event.getEntity().getUniqueId().equals(fugaku.getPlayer())) {
                            event.setCancelled(true);
                        }
                    }
                }
                private static class CombatRunnable extends BukkitRunnable {
                    private final CombatManager manager;
                    private int time = 60;
                    public CombatRunnable(CombatManager combatManager) {
                        this.manager = combatManager;
                    }

                    @Override
                    public void run() {
                        if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                            cancelAll();
                            return;
                        }
                        time--;
                        if (time == 0) {
                            cancelAll();
                            return;
                        }
                        Player fugaku = Bukkit.getPlayer(manager.fugaku.getPlayer());
                        if (fugaku != null) {
                            NMSPacket.sendActionBar(fugaku, "§bTemp avant fin du§c combat§b: §c"+StringUtils.secondsTowardsBeautiful(time));
                        }
                    }
                    private void cancelAll() {
                        EventUtils.unregisterEvents(manager);
                        cancel();
                    }
                }
            }
        }
    }
    private static class CroiserRunnable extends BukkitRunnable {
        private final Fugaku fugaku;
        private int timeRemaining = 60*5;
        private boolean croised = false;

        public CroiserRunnable(Fugaku fugaku) {
            this.fugaku = fugaku;
        }

        @Override
        public void run() {
            if (!fugaku.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!fugaku.getGamePlayer().isAlive()) {
                cancel();
                return;
            }
            Player owner = Bukkit.getPlayer(fugaku.getPlayer());
            if (owner != null) {
                timeRemaining--;
                if (!croised) {//donc je fais le code juste en dessous que s'il n'a croiser personne
                    for (Player p : Loc.getNearbyPlayersExcept(owner, 10)) {
                        if (fugaku.getGameState().hasRoleNull(p.getUniqueId()))continue;
                        RoleBase role = fugaku.getGameState().getGamePlayer().get(p.getUniqueId()).getRole();
                        if (!role.getGamePlayer().isAlive())continue;
                        if (role instanceof IUchiwa || role instanceof DanzoV2 ||role instanceof KakashiV2) {
                            this.croised = true;
                        }
                    }
                }
                if (timeRemaining == 0) {
                    if (croised) {
                        owner.sendMessage("§7Vous avez croiser un porteur du§c Sharingan§7 durant ces§c 5 dernières minutes§7.");
                    } else {
                        owner.sendMessage("§7Vous n'avez croiser aucun porteur du §cSharingan§7 ces dernières§c 5 minutes§7.");
                    }
                    timeRemaining = 60*5;
                }
            }
        }
    }
}