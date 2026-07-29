package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.*;
import fr.nicknqck.events.custom.death.FinalDeathEvent;
import fr.nicknqck.events.custom.death.UHCDeathEvent;
import fr.nicknqck.events.custom.roles.PowerActivateEvent;
import fr.nicknqck.events.ns.GamePlayerBecomeHokageEvent;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.interfaces.UpdatablePowerLore;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.akatsuki.ItachiV2;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.roles.ns.orochimaru.SasukeV2;
import fr.nicknqck.roles.ns.solo.DanzoV2;
import fr.nicknqck.roles.ns.solo.jubi.JubiSasuke;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.*;

public class Sai extends ShinobiRoles implements Listener {

    private boolean unlocked = false;

    public Sai(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[] {
                EChakras.DOTON, EChakras.FUTON, EChakras.SUITON
        };
    }

    @Override
    public String getName() {
        return "Saï";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.Sai;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this).addCustomLine(this.unlocked ? "" : "§7Si vous parvenez à tué§5 Sasuke§7 ou que§e Danzo§7 réussi l'un de§c ses objectifs§7,§e il§7 obtiendra l'accès à la commande§c /ns sai§7, ce qui lui permettra de vous faire rejoindre son camp").getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new ToileAuMonstreFantomatique(this), true);
        addPower(new EvaluationPower(this));
        EventUtils.registerRoleEvent(this);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDeath(@NonNull final UHCDeathEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (event.isCancelled())return;
        if (event.getGamePlayerKiller().getUuid().equals(getPlayer())) {
            if (event.getRole() instanceof SasukeV2 || event.getRole() instanceof JubiSasuke) {
                this.unlocked = true;
                for (@NonNull final GamePlayer gamePlayer : event.getGameState().getGamePlayer().values()) {
                    if (!gamePlayer.check())continue;
                    if (gamePlayer.getRole() instanceof DanzoV2) {
                        gamePlayer.sendMessage("§aSaï§7 (§a"+getGamePlayer().getPlayerName()+"§7) a enfin accompli sa mission, vous l'avez peut être§c manipuler§7 mais vous pourriez bien§c sacrifier 2❤ permanents§7 pour§a gagner avec lui§7.");
                    }
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onCommand(@NonNull final PlayerCommandPreprocessEvent event) {
        if (!this.unlocked) return;
        String message = event.getMessage();
        String[] args = message.split(" ");
        if (args[0].equalsIgnoreCase("/ns") && args.length == 2) {
            if (args[1].equalsIgnoreCase("sai")) {
                final Player player = event.getPlayer();
                final GamePlayer gamePlayer = GamePlayer.of(player.getUniqueId());
                if (gamePlayer != null) {
                    if (gamePlayer.check()) {
                        if (gamePlayer.getRole() instanceof DanzoV2) {
                            //Si Danzo est infecté par Shisui alors, il ne peut plus changer de camp, donc on fait rejoindre à Sai ce camp-là
                            gamePlayer.getRole().setTeam(TeamList.Racine);
                            this.setTeam(gamePlayer.getRole().getTeam());
                            gamePlayer.sendMessage("§aSai§7 et vous faite maintenant la pair, la§a victoire§7 semble assuré !");
                            this.getGamePlayer().sendMessage("§eDanzo§7 a reconnu votre valeur, vous pouvez enfin§a gagner§7 avec lui, il ne devrait pas vous trahir...");
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onBecomeHokage(@NonNull final GamePlayerBecomeHokageEvent event) {
        if (!event.getHokage().check())return;
        if (event.getHokage().getRole() instanceof DanzoV2 && !this.unlocked) {
            event.getHokage().sendMessage("§7Vous êtes devenue le nouvel§a Hokage§7, si vous souhaitez que§a Saï§7 rejoigne votre§a camp§7, faite la commande§6 /ns sai§7.");
            this.unlocked = true;
        }
    }
    @EventHandler(priority = EventPriority.NORMAL)
    private void onDeath(@NonNull final FinalDeathEvent event) {
        if (event.getEntityKiller() == null)return;
        @Nullable
        final GamePlayer gamePlayer = GamePlayer.of(event.getEntityKiller().getUniqueId());
        if (gamePlayer != null) {
            if (gamePlayer.check()) {
                if (gamePlayer.getRole() instanceof DanzoV2 && !this.unlocked) {
                    final Map<PotionEffect, EffectWhen> danzoEffects = new HashMap<>(gamePlayer.getRole().getEffects());
                    for (@NonNull PotionEffect potionEffect : danzoEffects.keySet()) {
                        if (potionEffect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE) && danzoEffects.get(potionEffect).equals(EffectWhen.PERMANENT)) {
                            this.unlocked = true;
                            gamePlayer.getRole().getGamePlayer().sendMessage("§7En tuant ces maudits démon du clan§4§l Uchiwa§r§7, vous vous êtes souvenue que§a Sai§7 existe, faite la commande§6 /ns sai§7 pour le rallier à votre cause.");
                            break;
                        }
                    }
                }
            }
        }
    }

    private static final class ToileAuMonstreFantomatique extends ItemPower {

        @NonNull
        private final SourisMessagere sourisMessagere;
        @NonNull
        private final Sangsue sangsue;
        @NonNull
        private final ScellementDePapier scellementDePapier;
        private Power equipedPower;

        public ToileAuMonstreFantomatique(@NonNull RoleBase role) {
            super("§aToile au monstres fantomatiques", new Cooldown(1), new ItemBuilder(Material.NETHER_STAR).setName("§aToile au monstres fantomatiques"), role,
                    "§fClique gauche§7: Permet d'utiliser le§c pouvoir séléctionner§7.",
                    "",
                    "§fClique droit§7: Ouvre un inventaire, avec à l'intérieur les§c pouvoirs§7 ci-dessous.",
                    "",
                    "§8 -§a Souris Messagères§7: Avec une rapidité de§c 10b/s§7, se dirige§a discrètement§7 vers la§c cible§7",
                    "§7une fois atteint vous aurez§c 1 minute§7 pour converser avec la personne en mettant \"§cs§7\" devant votre message.",
                    "",
                    "§8 -§c Sangsues§7: En visant un joueur, vous lui volerez ses§a points de vie§7 petit-à-petit.",
                    "",
                    "§8 -§a Scellement de papier§7: En visant un joueur, puis, en restant proche de lui pendant§c 10 secondes§7",
                    "§7vous§a scellerez§7 ses§c pouvoirs§7, il ne pourra donc plus les utiliser tant que vous êtes à moins de§c 150 blocs§7",
                    "§7de lui, aussi, lors de votre§c mort§7 il pourra à nouveau les utilisés."
            );
            setShowCdInDesc(false);
            setSendCooldown(false);
            this.sourisMessagere = new SourisMessagere(role);
            this.sangsue = new Sangsue(this);
            this.scellementDePapier = new ScellementDePapier(this);
            getShowCdRunnable().setCustomText(true);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("RIGHT")){
                    this.openInventory(player);
                    return true;
                } else if (event.getAction().name().contains("LEFT")) {
                    if (this.equipedPower == null) {
                        player.sendMessage("§7Il faut faire§f clique droit§7 pour sélectionner un§c pouvoir§7.");
                        return false;
                    }
                    return this.equipedPower.checkUse(player, map);
                }
            }
            return false;
        }

        @Override
        public void tryUpdateActionBar() {
            if (this.equipedPower == null) {
                getShowCdRunnable().setCustomTexte(
                        this.sourisMessagere.getName()+"§7: "+(this.sourisMessagere.getCooldown().isInCooldown() ? "§c"+StringUtils.secondsTowardsBeautiful(this.sourisMessagere.getCooldown().getCooldownRemaining()) : "§2✔")+"§7 | "+
                                this.sangsue.getName()+"§7: "+(this.sangsue.getCooldown().isInCooldown() ? "§c"+StringUtils.secondsTowardsBeautiful(this.sangsue.getCooldown().getCooldownRemaining()) : "§2✔")+"§7 | "+
                                this.scellementDePapier.getName()+"§7:§a "+this.scellementDePapier.getUse()+"§7/§c"+this.scellementDePapier.getMaxUse()
                );
            } else {
                getShowCdRunnable().setCustomTexte(
                        this.sourisMessagere.getName()+"§7: "+(this.sourisMessagere.getCooldown().isInCooldown() ? "§c"+StringUtils.secondsTowardsBeautiful(this.sourisMessagere.getCooldown().getCooldownRemaining()) : this.sourisMessagere.equals(this.equipedPower) ? "§2Utilisable" : "§2✔")+"§7 | "+
                                this.sangsue.getName()+"§7: "+(this.sangsue.getCooldown().isInCooldown() ? "§c"+StringUtils.secondsTowardsBeautiful(this.sangsue.getCooldown().getCooldownRemaining()) : this.sangsue.equals(this.equipedPower) ? "§2Utilisable" : "§2✔")+"§7 | "+
                                this.scellementDePapier.getName()+"§7:§a "+this.scellementDePapier.getUse()+"§7/§c"+this.scellementDePapier.getMaxUse()
                );
            }
        }

        private void openInventory(@NonNull final Player player) {
            final FastInv fastInv = new FastInv(27, "§aToile au monstres fantomatiques");
            fastInv.setItems(fastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
            fastInv.setItem(11, new ItemBuilder(Material.INK_SACK).setDurability(8).setName("§aSouris").toItemStack(), event -> {
                this.equipedPower = sourisMessagere;
                player.sendMessage("§7La§a Toile équiper§7 est maintenant \""+this.equipedPower.getName()+"§7\".");
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                setTargetDistance(0);
            });
            fastInv.setItem(13, new ItemBuilder(Material.REDSTONE).setName("§cSangsue").toItemStack(), event -> {
                this.equipedPower = sangsue;
                player.sendMessage("§7La§a Toile équiper§7 est maintenant \""+this.equipedPower.getName()+"§7\".");
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                setTargetDistance(30);
            });
            fastInv.setItem(15, new ItemBuilder(Material.PAPER).setName("§aScellement dans le papier").toItemStack(), event -> {
                this.equipedPower = scellementDePapier;
                player.sendMessage("§7La§a Toile équiper§7 est maintenant \""+this.equipedPower.getName()+"§7\".");
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                setTargetDistance(20);
            });
            fastInv.open(player);
        }
        private static final class SourisMessagere extends Power implements Listener{

            private UUID targetUUID;

            public SourisMessagere(@NonNull RoleBase role) {
                super("§aSouris messagères", new Cooldown(60*5), role);
                EventUtils.registerRoleEvent(this);
                role.addPower(this);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (map.containsKey("event")) {
                    Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> openInventory(player));
                    return false;
                }
                if (map.containsKey("target")) {
                    if (map.get("target") instanceof GamePlayer) {
                        player.sendMessage("§7Vos "+getName()+"§7 partent à la recherche de§a "+((GamePlayer) map.get("target")).getPlayerName());
                        new SourisRunnable(this, (GamePlayer) map.get("target"));
                        player.closeInventory();
                        return true;
                    }
                }
                return false;
            }
            @EventHandler
            private void onChat(@NonNull AsyncPlayerChatEvent event) {
                if (this.targetUUID == null)return;
                if (event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                    if (!event.getMessage().startsWith("s"))return;
                    if (!getRole().getGamePlayer().check())return;
                    final GamePlayer gamePlayer = GamePlayer.of(this.targetUUID);
                    if (gamePlayer == null)return;
                    if (!gamePlayer.check())return;
                    gamePlayer.sendMessage("§aSai§7:"+" §f"+ ChatColor.translateAlternateColorCodes('&', event.getMessage().substring("s".length())));
                    getRole().getGamePlayer().sendMessage("§aSai§7:"+" §f"+ ChatColor.translateAlternateColorCodes('&', event.getMessage().substring("s".length())));
                } else if (event.getPlayer().getUniqueId().equals(this.targetUUID)) {
                    if (!event.getMessage().startsWith("s"))return;
                    final GamePlayer gamePlayer = GamePlayer.of(event.getPlayer().getUniqueId());
                    if (gamePlayer == null)return;
                    if (!gamePlayer.check())return;
                    gamePlayer.sendMessage("§a"+gamePlayer.getPlayerName()+"§7:"+" §f"+ ChatColor.translateAlternateColorCodes('&', event.getMessage().substring("s".length())));
                    getRole().getGamePlayer().sendMessage("§a"+gamePlayer.getPlayerName()+"§7:"+" §f"+ ChatColor.translateAlternateColorCodes('&', event.getMessage().substring("s".length())));
                }
            }
            private void openInventory(@NonNull Player player) {
                final PaginatedFastInv paginatedFastInv = new PaginatedFastInv(9*5, "§aSouris Messagères");
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

                final List<GamePlayer> gamePlayerList = Loc.getNearbyGamePlayers(player.getLocation(), 9999);
                for (GamePlayer gamePlayer : gamePlayerList) {
                    if (gamePlayer == null)continue;
                    if (!gamePlayer.check())continue;
                    final Player target = Bukkit.getPlayer(gamePlayer.getUuid());
                    if (target == null)continue;
                    if (target.hasPotionEffect(PotionEffectType.INVISIBILITY))continue;
                    if (target.getUniqueId().equals(getRole().getPlayer()))continue;
                    //Le pouvoir ne touche pas les joueurs invisibles
                    paginatedFastInv.addContent(new ItemBuilder(gamePlayer.getHeadItem() == null ? GlobalUtils.getAsyncPlayerHead(gamePlayer.getUuid()) : gamePlayer.getHeadItem())
                            .setName("§a"+target.getName())
                            .toItemStack(), event -> {
                        final Map<String, Object> test = new HashMap<>();
                        test.put("target", gamePlayer);
                        this.checkUse(player, test);
                    });
                }
                paginatedFastInv.open(player);
            }
            private static final class SourisRunnable extends BukkitRunnable {

                private final SourisMessagere sourisMessager;
                private final GamePlayer gameCible;
                private double distanceMax;
                private double distanceParcouru;
                private int timeLeft = 60;
                private boolean activate = false;

                private SourisRunnable(SourisMessagere sourisMessager, GamePlayer gameCible) {
                    this.sourisMessager = sourisMessager;
                    this.gameCible = gameCible;
                    this.distanceMax = this.gameCible.getLastLocation().distance(this.sourisMessager.getRole().getGamePlayer().getLastLocation());
                    this.distanceParcouru = 0.0;
                    runTaskTimerAsynchronously(this.sourisMessager.getPlugin(), 0, 20);
                }

                @Override
                public void run() {
                    if (!GameState.inGame()) {
                        cancel();
                        return;
                    }
                    if (!this.gameCible.check()) {
                        this.sourisMessager.getRole().getGamePlayer().sendMessage("§7Vos§a Souris messagères§7 ne peuvent plus trouver§b "+this.gameCible.getPlayerName()+"§7, il/elle est§c mort(e)§7.");
                        this.sourisMessager.getRole().getGamePlayer().getActionBarManager().removeInActionBar("sai.souris");
                        this.sourisMessager.targetUUID = null;
                        cancel();
                        return;
                    }
                    if (!this.sourisMessager.getRole().getGamePlayer().check()) {
                        this.sourisMessager.getRole().getGamePlayer().getActionBarManager().removeInActionBar("sai.souris");
                        this.sourisMessager.targetUUID = null;
                        cancel();
                        return;
                    }
                    @NonNull
                    final Player target = this.gameCible.getPlayer();
                    @NonNull
                    final Player owner = this.sourisMessager.getRole().getGamePlayer().getPlayer();
                    if (!owner.getWorld().equals(target.getWorld()))return;
                    if (!this.activate) {
                        this.distanceMax = owner.getLocation().distance(target.getLocation());
                        //DPS pour Distance Par Seconde de base sur 10 blocs
                        double dps = 10.0;
                        this.distanceParcouru = this.distanceParcouru + dps;
                        if (this.distanceParcouru >= this.distanceMax) {
                            this.activate = true;
                            this.timeLeft =60;
                            owner.sendMessage("§7Vos§a Souris messagères§7 ont enfin atteint§c "+target.getName()+"§7, vous aurez§c 1 minute§7 pour§c communiquer§7 avec lui, pour ce faire vous devrez commencer vos messages par un \"§cs§7\".");
                            target.sendMessage("§7Les§a Souris messagères§7 de§a Sai§7 vous ont enfin atteint, vous pouvez des maintenant et ce pendant§c 1 minute§7 communiquer avec lui en commençant vos messages par un \"§cs§7\".");
                            this.sourisMessager.targetUUID = target.getUniqueId();
                        }
                    } else {
                        if (this.timeLeft <= 0) {
                            this.sourisMessager.getRole().getGamePlayer().getActionBarManager().removeInActionBar("sai.souris");
                            this.sourisMessager.targetUUID = null;
                            cancel();
                            return;
                        }
                        this.timeLeft--;
                        this.sourisMessager.getRole().getGamePlayer().getActionBarManager().updateActionBar("sai.souris", "§7Temps de§a vie§7 des§a Souris Messagères§7:§c "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                    }
                }
            }
        }
        private static final class Sangsue extends Power {

            private final ToileAuMonstreFantomatique toileAuMonstreFantomatique;

            public Sangsue(@NonNull ToileAuMonstreFantomatique toileAuMonstreFantomatique) {
                super("§cSangsue", new Cooldown(60*5), toileAuMonstreFantomatique.getRole());
                setShowInDesc(false);
                getRole().addPower(this);
                this.toileAuMonstreFantomatique = toileAuMonstreFantomatique;
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                final Player target = this.toileAuMonstreFantomatique.getShowGlowingRunnable().getTarget();
                if (target != null) {
                    final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                    if (gamePlayer != null) {
                        if (gamePlayer.check()) {
                            if (gamePlayer.getRole() instanceof DanzoV2 && getRole().getTeam().equals(gamePlayer.getRole().getTeam())) {
                                player.sendMessage("§cIl vous est impossible de viser maitre§e Danzo§7.");
                                return false;
                            }
                            new SangsueRunnable(this, gamePlayer).runTaskTimer(getPlugin(), 1, 80);
                            player.sendMessage("§b"+gamePlayer.getPlayerName()+"§7 a été toucher par vos§c "+getName());
                            gamePlayer.sendMessage("§aSai§7 vous a toucher avec ses§c Sangsue§7.");
                            return true;
                        }
                    }
                }
                player.sendMessage("§cIl faut viser un joueur !");
                return false;
            }
            private static final class SangsueRunnable extends BukkitRunnable {

                private final Sangsue sangsue;
                private final GamePlayer gameTarget;
                private double healObtained = 0.0;
                private final double maxPerCycle;
                private final double maxPerHeal;

                private SangsueRunnable(Sangsue sangsue, GamePlayer gameTarget) {
                    this.sangsue = sangsue;
                    this.gameTarget = gameTarget;
                    this.maxPerCycle = 30.0;
                    this.maxPerHeal = 2.0;
                }

                @Override
                public void run() {
                    if (!GameState.inGame()) {
                        cancel();
                        return;
                    }
                    if (!this.gameTarget.check()) {
                        cancel();
                        return;
                    }
                    if (!this.sangsue.getRole().getGamePlayer().check())return;
                    if (this.healObtained >= this.maxPerCycle) {
                        cancel();
                        return;
                    }
                    final Player target = this.gameTarget.getPlayer();
                    final Player owner = this.sangsue.getRole().getGamePlayer().getPlayer();
                    double missingHealth = owner.getMaxHealth() - owner.getHealth();
                    if (missingHealth > 0.0) {
                        double toHeal = Math.min(this.maxPerHeal, this.maxPerCycle - this.healObtained);
                        toHeal = Math.min(toHeal, missingHealth);
                        if (owner.getHealth() + toHeal > owner.getMaxHealth()) {return;}
                        target.damage(0.0);
                        target.setHealth(Math.max(0.1, target.getHealth()-toHeal));
                        target.sendMessage("§7Votre§a vie§7 a été absorbé par les§a Sangsues§7 de§a Sai§7.");
                        owner.sendMessage("§7Vous avez été§a soigné§7 par vos§a Sangsues§7.");
                        owner.setHealth(owner.getHealth()+toHeal);
                        this.healObtained += toHeal;
                    }
                }
            }
        }
        private static final class ScellementDePapier extends Power implements Listener {

            private final ToileAuMonstreFantomatique toileAuMonstreFantomatique;
            private UUID targetUUID = null;

            public ScellementDePapier(@NonNull ToileAuMonstreFantomatique toileAuMonstreFantomatique) {
                super("§aScellement de papier", null, toileAuMonstreFantomatique.getRole());
                setShowInDesc(false);
                setMaxUse(1);
                getRole().addPower(this);
                this.toileAuMonstreFantomatique = toileAuMonstreFantomatique;
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                final Player target = this.toileAuMonstreFantomatique.getShowGlowingRunnable().getTarget();
                if (target != null) {
                    final GamePlayer gamePlayer = GamePlayer.of(target.getUniqueId());
                    if (gamePlayer != null) {
                        if (gamePlayer.check()) {
                            if (gamePlayer.getRole() instanceof DanzoV2 && getRole().getTeam().equals(gamePlayer.getRole().getTeam())) {
                                player.sendMessage("§cIl vous est impossible de viser maitre§e Danzo§7.");
                                return false;
                            }
                            player.sendMessage("§7Le "+getName()+"§7 a commencé, restez§c 10 secondes§7 proche de§a "+gamePlayer.getPlayerName()+"§7 pour que ce sois définitif§7.");
                            new ScellementRunnable(this, gamePlayer).runTaskTimerAsynchronously(getPlugin(), 1, 20);
                            return true;
                        }
                    }
                }
                player.sendMessage("§cIl faut viser un joueur !");
                return false;
            }
            @EventHandler(priority = EventPriority.LOWEST)
            private void onPowerUse(@NonNull final PowerActivateEvent event) {
                if (this.targetUUID == null)return;
                if (event.getPlayer().getUniqueId().equals(this.targetUUID)) {
                    if (!this.getRole().getGamePlayer().check())return;
                    if (this.getRole().getGamePlayer().getLastLocation().getWorld().equals(event.getPlayer().getWorld())) {
                        if (this.getRole().getGamePlayer().getLastLocation().distance(event.getPlayer().getLocation()) <= 120.0) {
                            event.setCancel(true);
                            getRole().getGamePlayer().sendMessage("§a"+event.getPlayer().getName()+"§7 a essayé d'utiliser un pouvoir, heureusement qu'ils sont§c scellés§7.");
                            event.setCancelMessage("§aSaï§c vous empêche d'utiliser ce pouvoir !");
                        }
                    }
                }
            }

            private static final class ScellementRunnable extends BukkitRunnable {

                private final ScellementDePapier scellement;
                private final GamePlayer gameTarget;
                private int timeBeforeScellement = 10;

                private ScellementRunnable(ScellementDePapier scellement, GamePlayer gameTarget) {
                    this.scellement = scellement;
                    this.gameTarget = gameTarget;
                }

                @Override
                public void run() {
                    if (!GameState.inGame()) {
                        cancel();
                        return;
                    }
                    if (!this.gameTarget.check()) {
                        this.scellement.getRole().getGamePlayer().getActionBarManager().removeInActionBar("sai.scellement");
                        cancel();
                        return;
                    }
                    if (!this.scellement.getRole().getGamePlayer().check()) {
                        this.scellement.getRole().getGamePlayer().getActionBarManager().removeInActionBar("sai.scellement");
                        cancel();
                        return;
                    }
                    @NonNull
                    final Player target = gameTarget.getPlayer();
                    @NonNull
                    final Player owner = this.scellement.getRole().getGamePlayer().getPlayer();
                    if (!owner.getWorld().equals(target.getWorld())) return;
                    if (Loc.getNearbyPlayersExcept(owner, 10).contains(target)){
                        if (this.timeBeforeScellement <= 0) {
                            this.scellement.targetUUID = target.getUniqueId();
                            this.scellement.getRole().getGamePlayer().getActionBarManager().removeInActionBar("sai.scellement");
                            EventUtils.registerRoleEvent(this.scellement);
                            cancel();
                            return;
                        }
                        this.timeBeforeScellement--;
                        this.scellement.getRole().getGamePlayer().getActionBarManager().updateActionBar("sai.scellement", "§bTemps avant scellement:§c "+StringUtils.secondsTowardsBeautiful(this.timeBeforeScellement));
                    }
                }
            }
        }
    }
    private static final class EvaluationPower extends Power implements UpdatablePowerLore {

        public EvaluationPower(@NonNull RoleBase role) {
            super("§aÉvaluation§r", null, role,
                    "§7En restant proche des autres joueurs pendant un temps définie en fonction de leurs§a intelligences§7",
                    "§7vous saurez s'ils sont§5 Sasuke§7 ou§c non§7.",
                    "",
                    "§aFugaku§7,§c Itachi§7 et§d Sasuke§7 (§dcamp Jubi§7) seront détécter comme étant§5 Sasuke§7.",
                    "§7Si vous§c tuez§5 Sasuke§7 alors ce pouvoirs permettra d'obtenir l'information du§c camp§7 de la personne"
            );
            new EvaluationRunnable(this).runTaskTimerAsynchronously(getPlugin(), 100, 20);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }

        @Override
        public String[] getCustomPowerLore() {
            if (getRole() instanceof Sai) {
                if (((Sai) getRole()).unlocked) {
                    return new String[] {
                            "§7En restant proche des autres joueurs pendant un temps définie en fonction de leurs§a intelligences§7",
                            "§7vous saurez dans quel camps ils sont parmi un§a vrai§7 et un§c faux§7."
                    };
                }
            }
            return getDescriptions();
        }

        private static final class EvaluationRunnable extends BukkitRunnable {

            private final EvaluationPower evaluationPower;
            private final Map<Intelligence, Integer> evalMap;
            private final Map<UUID, Integer> playerMap;
            private boolean killSasuke = false;

            public EvaluationRunnable(EvaluationPower evaluationPower) {
                this.evaluationPower = evaluationPower;
                this.evalMap = new HashMap<>();
                this.evalMap.put(Intelligence.GENIE, 60*5);
                this.evalMap.put(Intelligence.INTELLIGENT, 60*4+30);
                this.evalMap.put(Intelligence.CONNUE, 60*4);
                this.evalMap.put(Intelligence.MOYENNE, 60*3+30);
                this.evalMap.put(Intelligence.PEUINTELLIGENT, 60*3);
                this.playerMap = new HashMap<>();
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                final GamePlayer gamePlayer = evaluationPower.getRole().getGamePlayer();
                if (gamePlayer == null)return;
                if (!gamePlayer.check())return;
                if (gamePlayer.getRole() instanceof Sai) {
                    this.killSasuke = ((Sai) gamePlayer.getRole()).unlocked;
                }
                final Location location = gamePlayer.getLastLocation();
                for (Player player : location.getWorld().getPlayers()) {
                    if (player.getLocation().distance(location) >= 20.0) {
                        continue;
                    }
                    if (player.getUniqueId().equals(gamePlayer.getUuid()))continue;
                    final GamePlayer target = GamePlayer.of(player.getUniqueId());
                    if (target == null)continue;
                    if (!target.check())continue;
                    if (!(target.getRole() instanceof NSRoles))continue;
                    int time = this.playerMap.getOrDefault(player.getUniqueId(), 0);
                    time++;
                    this.playerMap.put(player.getUniqueId(), time);
                    int percent = Math.min(100, (time * 100) / (this.evalMap.get(((NSRoles) target.getRole()).getIntelligence())));
                    updatePointsNametag(gamePlayer.getPlayer(), player, percent);
                    if (time == this.evalMap.get(((NSRoles) target.getRole()).getIntelligence()) && this.evaluationPower.checkUse(gamePlayer.getPlayer(), new HashMap<>())) {
                        if (this.killSasuke) {
                            String team1 = "";
                            String team2= "";
                            if (RandomUtils.getOwnRandomProbability(50.0)) {
                                team1 = target.getRole().getTeam().getName();
                                final List<TeamList> teamListList = new ArrayList<>(Arrays.asList(TeamList.values()));
                                Collections.shuffle(teamListList, Main.RANDOM);
                                for (TeamList team : teamListList) {
                                    if (team.getName().equals(team1)) {continue;}
                                    if (!team.getMdj().equalsIgnoreCase("ns"))continue;
                                    team2 = team.getName();
                                    break;
                                }
                            } else {
                                team2 = target.getRole().getTeam().getName();
                                final List<TeamList> teamListList = new ArrayList<>(Arrays.asList(TeamList.values()));
                                Collections.shuffle(teamListList, Main.RANDOM);
                                for (TeamList team : teamListList) {
                                    if (team.getName().equals(team2)) {continue;}
                                    if (!team.getMdj().equalsIgnoreCase("ns"))continue;
                                    team1 = team.getName();
                                    break;
                                }
                            }
                            gamePlayer.sendMessage("§a"+player.getName()+"§7 fait probablement soit partie de l'équipe: "+team1+"§7, soit: "+team2);
                            this.playerMap.remove(player.getUniqueId());
                        } else {
                            if (target.getRole() instanceof SasukeV2 || target.getRole() instanceof Fugaku || target.getRole() instanceof ItachiV2 || target.getRole() instanceof JubiSasuke) {
                                gamePlayer.sendMessage("§a"+target.getPlayerName()+"§7 semble décrire à la description de§5 Sasuke Uchiwa§7.");
                            }
                        }
                    }
                }
            }
            private void updatePointsNametag(Player viewer, Player target, int percent) {
                if (viewer == null || target == null) return;
                if (!viewer.isOnline() || !target.isOnline()) return;
                if (viewer.equals(target)) return;
                String suffix;
                if (percent < 25){
                    suffix = ("§c " + percent + "%");
                } else if (percent < 75) {
                    suffix = ("§6 " + percent + "%");
                } else if (percent < 95) {
                    suffix =("§a " + percent + "%");
                } else {
                    suffix = ("§2 " + percent + "%");
                }
                if (percent == 100) {
                    suffix = ("§2 ✔");
                }
                Main.getInstance().getCustomTabManager().setSuffix(viewer.getUniqueId(), target.getUniqueId(), suffix);
            }
        }
    }
}