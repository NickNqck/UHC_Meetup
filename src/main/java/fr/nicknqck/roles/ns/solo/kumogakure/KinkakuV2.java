package fr.nicknqck.roles.ns.solo.kumogakure;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.events.custom.roles.PowerActivateEvent;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.KumogakureRole;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KinkakuV2 extends KumogakureRole {

    private boolean ginkakuDeath = false;

    public KinkakuV2(UUID player) {
        super(player);
    }

    @Override
    public void onEndKyubi() {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false , false), EffectWhen.DAY);
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return EChakras.values();
    }

    @Override
    public String getName() {
        return "Kinkaku";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.Kinkaku;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine(this.ginkakuDeath ? "" : "§7Vous aurez l'effet§9 Résistance I§7 en étant proche de§6 Ginkaku§7.")
                .addCustomLine(this.ginkakuDeath ? "" : "§7A la mort de§6 Ginkaku§7, vous obtiendrez§9 Résistance I§7 de manière§c permanente§7.")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        addKnowedRole(GinkakuV2.class);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false , false), EffectWhen.DAY);
        new EffectGiver(getGameState(), this);
        addPower(new KyubiPower(this), true);
        addPower(new EventailBananePower(this), true);
        addPower(new VasePower(this), true);
    }
    private static class EffectGiver extends BukkitRunnable {

        private final GameState gameState;
        private final KinkakuV2 kinkakuV2;

        private EffectGiver(@NonNull final GameState gameState,@NonNull final KinkakuV2 kinkakuV2) {
            this.gameState = gameState;
            this.kinkakuV2 = kinkakuV2;
            runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.gameState.getDeadRoles().contains(Roles.Ginkaku)) {
                this.kinkakuV2.getGamePlayer().sendMessage("§6Ginkaku§7 est mort, vous obtenez l'effet§c Résistance I§7 de manière§c permanente");
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    this.kinkakuV2.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 0, false, false), EffectWhen.PERMANENT);
                    this.kinkakuV2.ginkakuDeath = true;
                });
                cancel();
                return;
            }
            @NonNull final Player owner = Bukkit.getPlayer(this.kinkakuV2.getPlayer());
            if (owner == null)return;
            @NonNull final List<GamePlayer> gamePlayerList = new ArrayList<>(Loc.getNearbyGamePlayers(owner.getLocation(), 30));
            if (gamePlayerList.isEmpty())return;
            for (@NonNull final GamePlayer gamePlayer : gamePlayerList) {
                if (gamePlayer.getRole() == null)continue;
                if (gamePlayer.getRole() instanceof GinkakuV2) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.kinkakuV2.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), EffectWhen.NOW));
                    break;
                }
            }
        }
    }
    private static final class EventailBananePower extends ItemPower implements Listener {

        public EventailBananePower(@NonNull RoleBase role) {
            super("§aEventail de bananier", null, new ItemBuilder(Material.DIAMOND_SWORD).setName("§aÉventail de bananier").addEnchant(Enchantment.DAMAGE_ALL, 3), role);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return map.containsKey("rien");
        }
        @EventHandler
        private void onKill(@NonNull final UHCPlayerKillEvent event) {
            if (event.getGamePlayerKiller() != null) {
                final GamePlayer gamePlayer = GamePlayer.of(event.getVictim().getUniqueId());
                if (gamePlayer != null && gamePlayer.check()) {
                    if (gamePlayer.getRole() instanceof NSRoles) {
                        final EChakras eChakras = ((NSRoles) gamePlayer.getRole()).getChakras();
                        if (eChakras != null) {
                            if (!eChakras.getChakra().getList().contains(getRole().getPlayer()) && checkUse(event.getPlayerKiller(), Collections.singletonMap("rien", "rien"))) {
                                eChakras.getChakra().getList().add(getRole().getPlayer());
                                getRole().getGamePlayer().sendMessage(getPlugin().getPLUGIN_NAME()+"§7Vous utilisez maintenant la§a nature de chakra§7 \""+eChakras.getShowedName()+"§7\" en plus de celles que vous aviez déjà.");
                            }
                        }
                    }
                }
            }
        }
    }
    private static final class VasePower extends ItemPower {

        public VasePower(@NonNull RoleBase role) {
            super("§eVase d'ambre§r", new Cooldown(60*8), new ItemBuilder(Material.FLOWER_POT_ITEM).setName("§eVase d'ambre"), role,
                    "§7En fonction du clique, effectue une action différente:",
                    "",
                    "§8 -§f Clique droit§7: En visant un joueur, enclenche la§a purification§7.",
                    "",
                    "§8 -§f Clique gauche§7: Ouvre un menu, en choisissant un joueur, enclenche la§a purification§7.",
                    "",
                    "§aPurification§7: Démarre un§c compteur§7 de§c 1 minute§7, si la§c cible§7 utilise un§c pouvoir§7,",
                    "§cl'annule§7, puis, déclenche un autre§c compteur§7 de§c 10 minutes§7 durant lequel la§c cible§7 ne",
                    "§7peut utiliser§c aucun pouvoir§7."
            );
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (map.containsKey("rien") && map.get("rien").equals("rien") && map.containsKey("uuid")) {
                final UUID uuid = UUID.fromString((String) map.get("uuid"));
                final Player target = Bukkit.getPlayer(uuid);
                if (target != null) {
                    final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                    if (gamePlayer != null && gamePlayer.check()) {
                        player.sendMessage("§7Les effets du§e vase d'ambre§7 vont essayer de toucher§c "+target.getName()+"§7.");
                        target.sendMessage("§6Kinkaku§7 essaie de vous§a purifier§7 avec son§e Vase d'ambre§7, si vous utilisez un§c pouvoir§7 dans le prochaine§c minute§7, vous ne pourrez plus en utiliser pendant§c 10 minutes§7.");
                        new VaseRunnable(this, gamePlayer).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                        return true;
                    } else {
                        player.sendMessage("§cImpossible de viser§4 "+target.getName());
                    }
                } else {
                    player.sendMessage("§cImpossible de viser ce joueur !");
                }
                return false;
            }
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("RIGHT")) {
                    final Player target = RayTrace.getTargetPlayer(player, 30, null);
                    if (target != null) {
                        final  GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                        if (gamePlayer != null && gamePlayer.check()) {
                            final Map<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uuid", gamePlayer.getUuid().toString());
                            hashMap.put("rien", "rien");
                            checkUse(player, hashMap);
                            return false;
                        }
                    } else {
                        player.sendMessage("§cIl faut viser un joueur !");
                        return false;
                    }
                } else {
                    final List<GamePlayer> gamePlayerList = new ArrayList<>(Loc.getNearbyGamePlayers(player.getLocation(), 30));
                    gamePlayerList.remove(getRole().getGamePlayer());
                    if (gamePlayerList.isEmpty()) {
                        player.sendMessage("§cImpossible, personne n'est autours de vous !");
                        return false;
                    }
                    createInventory(player, gamePlayerList );
                    return false;
                }
            }
            return false;
        }
        private void createInventory(@NonNull final Player player, @NonNull final List<GamePlayer> gamePlayerList) {
            final PaginatedFastInv paginatedFastInv = new PaginatedFastInv(27, "§eVase d'ambre");
            paginatedFastInv.setItems(paginatedFastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
            final List<Integer> list = new ArrayList<>();
            for (int i = 10; i <= 16; i++) {
                list.add(i);
            }
            paginatedFastInv.setContentSlots(list);
            paginatedFastInv.previousPageItem(3, new ItemBuilder(Material.WOOD_BUTTON)
                    .setName("§7◄ Page précédente").toItemStack());
            paginatedFastInv.nextPageItem(5, new ItemBuilder(Material.WOOD_BUTTON)
                    .setName("§7Page suivante ►").toItemStack());
            for (GamePlayer gamePlayer : gamePlayerList) {
                final Player target = Bukkit.getPlayer(gamePlayer.getUuid());
                if (target== null)continue;
                if (!gamePlayer.check())continue;
                if (target.hasPotionEffect(PotionEffectType.INVISIBILITY))continue;
                paginatedFastInv.addContent(new ItemBuilder(GlobalUtils.getAsyncPlayerHead(target.getUniqueId())).setName("§a"+gamePlayer.getPlayerName()).toItemStack(), inventoryClickEvent -> {
                    final Map<String, Object> hashMap = new HashMap<>();
                    hashMap.put("uuid", gamePlayer.getUuid().toString());
                    hashMap.put("rien", "rien");
                    checkUse(player, hashMap);
                });
            }
            paginatedFastInv.open(player);
        }
        private static final class VaseRunnable extends BukkitRunnable implements Listener{

            private final VasePower power;
            private final GamePlayer gameTarget;
            private int timeBeforePass = 60;
            private boolean activate = false;
            private int timeCancel = 0;

            private VaseRunnable(VasePower power, GamePlayer gameTarget) {
                this.power = power;
                this.gameTarget = gameTarget;
                EventUtils.registerRoleEvent(this);
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                if (this.activate) {
                    this.power.getRole().getGamePlayer().getActionBarManager().removeInActionBar("kinkaku.vase");
                    if (this.timeCancel <= 0) {
                        cancel();
                    }
                    this.timeCancel--;
                    return;
                }
                final Player target = Bukkit.getPlayer(gameTarget.getUuid());
                if (target == null) {return;}
                this.power.getRole().getGamePlayer().getActionBarManager().updateActionBar("kinkaku.vase" ,"§bTemps avant fin de§c purification§b: "+ StringUtils.secondsTowardsBeautiful(this.timeBeforePass));
                this.timeBeforePass--;
                if (this.timeBeforePass <= 0) {
                    final Player owner = Bukkit.getPlayer(this.power.getRole().getGamePlayer().getUuid());
                    if (owner != null) {
                        this.power.getCooldown().setActualCooldown(60*3);
                        Bukkit.getScheduler().runTask(this.power.getPlugin(), () -> {
                            float absorptionHearts = ((CraftPlayer) owner).getHandle().getAbsorptionHearts();
                            absorptionHearts = absorptionHearts + 8f;
                            ((CraftPlayer) owner).getHandle().setAbsorptionHearts(absorptionHearts);
                        });
                        owner.sendMessage(this.power.getPlugin().getNAME()+"§7Comme§c "+target.getName()+"§7 n'a utiliser§4 aucun pouvoir§7 pendant la dernière§c minute§7, le§c cooldown actuel§7 deviant§c 3 minutes§7, vous recevez également§e +4❤ d'absorption§7.");
                        cancel();
                        this.activate = true;
                    }
                }
            }
            @EventHandler(priority = EventPriority.HIGH)
            private void onPowerActivate(@NonNull final PowerActivateEvent event) {
                if (this.gameTarget.getUuid().equals(event.getPlayer().getUniqueId())) {
                    if (!this.activate && this.timeBeforePass > 0) {
                        event.setCancel(true);
                        event.setCancelMessage(this.power.getPlugin().getNAME()+"§6Kinkaku§7 vous empêche d'utiliser vos pouvoirs pendant§c 10 minutes !");
                        this.timeCancel = 60*10;
                        this.activate = true;
                        return;
                    }
                    if (this.timeCancel > 0 && this.activate) {
                        event.setCancel(true);
                        event.setCancelMessage(this.power.getPlugin().getNAME()+"§6Kinkaku§7 vous empêche d'utiliser vos pouvoirs pendant encore§c "+StringUtils.secondsTowardsBeautiful(this.timeCancel)+" !");
                    }
                }
            }
        }
    }
}
