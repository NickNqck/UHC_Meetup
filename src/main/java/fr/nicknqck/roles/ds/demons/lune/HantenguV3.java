package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.FormHantengu;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.power.CooldownFinishEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.*;
import org.bukkit.util.Vector;

import static fr.nicknqck.utils.particles.MathUtil.spawnParticle;

public class HantenguV3 extends DemonsRoles implements Listener {

    private boolean invisible = false;

    public HantenguV3(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.SUPERIEUR;
    }

    @Override
    public String getName() {
        return "Hantengu§7 (§6V2§7)";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.HantenguV2;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine("§7Lorsque vous retirez votre§c armure§7 vous devenez§a invisible§7, lorsque vous l'êtes vous êtes insensible au§c dégâts§7 de§a chute§7 et vous avez les effets§9 Résistance II§7 et§e Speed I§7 (Ne fonctionne pas si vous êtes sous une autre§c forme§7 qu'§cHantengu§7).")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0, false, false), EffectWhen.PERMANENT);
        final ChoicePower choicePower = new ChoicePower(this);
        addPower(choicePower, true);
        new InvisibilityRunnable(this, choicePower).runTaskTimerAsynchronously(Main.getInstance(), 100, 20);
        EventUtils.registerRoleEvent(this);
        super.RoleGiven(gameState);
    }
    @EventHandler
    private void onDamage(final EntityDamageEvent event) {
        if (!event.getEntity().getUniqueId().equals(getPlayer()))return;
        if (!event.getEntity().getUniqueId().equals(getPlayer()))return;
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL))return;
        if (!invisible)return;
        event.setDamage(0.0);
        event.setCancelled(true);
    }
    private static class InvisibilityRunnable extends BukkitRunnable {

        private final HantenguV3 hantenguV3;
        private final ChoicePower choicePower;
        private boolean message = false;

        private InvisibilityRunnable(HantenguV3 hantenguV3, ChoicePower choicePower) {
            this.hantenguV3 = hantenguV3;
            this.choicePower = choicePower;
        }

        @Override
        public void run() {
            if (!hantenguV3.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            final Player owner = Bukkit.getPlayer(this.hantenguV3.getPlayer());
            if (owner == null)return;
            boolean nude = this.hantenguV3.getGameState().isApoil(owner);
            if (choicePower.using) {
                nude = false;
            }
            if (nude) {
                if (!message) {
                    owner.sendMessage("§7Vous êtes devenue§a invisible§7.");
                    this.message = true;
                    this.hantenguV3.invisible = true;
                }
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    this.hantenguV3.givePotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false), EffectWhen.NOW);
                    this.hantenguV3.givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.NOW);
                    this.hantenguV3.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, false, false), EffectWhen.NOW);
                });
            } else {
                if (message) {
                    owner.sendMessage("§7Vous n'êtes plus§a invisible§7.");
                    this.message = false;
                    this.hantenguV3.invisible = false;
                }
            }
        }
    }
    private static class ChoicePower extends ItemPower implements Listener {

        private int amountChoices = 0;
        private Power leftClickPower;
        private final KhakkharaPower khakkharaPower;
        private final UchiwaPower uchiwaPower;
        private boolean using = false;
        private final List<FormHantengu> formesTakes = new ArrayList<>();
        private final AffaiblissementPower affaiblissementPower;
        private final CriSoniquePower criSoniquePower;
        private Power shiftClickPower;

        public ChoicePower(@NonNull RoleBase role) {
            super("Émotions", null, new ItemBuilder(Material.NETHER_STAR).setName("§cÉmotions"), role,
                    "§7Vous ouvre un menu vous permettant de choisir une§c forme§7, chacune d'entre elles ont leurs propres§c particularités§7 et",
                    "§7leurs propres pouvoir utilisable via un§c clique gauche§7 sur cette objet, chacun d'entre eux à un§c cooldown§7 de§c 2 minutes§7.",
                    "",
                    "§8 -§c Sekido§7: ",
                    "",
                    AllDesc.tab+"§7Vous donne l'effet§c Force I§7 pendant le temps de la§c transformation§7, également pendant la transformation vous pouvez utiliser le \"§cKhakkhara§7\".",
                    "",
                    AllDesc.tab+"§cKhakkhara§7: Tout les joueurs étant à moins de§c 50 blocs§7 et qui ne sont pas des§c démons§7 seront touché par un§e éclair§7,",
                    "§7ce qui leurs donnera§c Slowness IV§7 et§c Blindness IV§7 pendant§c 5 secondes§7, ils perdront aussi§c 1,5❤§7 de§c dégâts§7.",
                    "",
                    "§8 -§a Karaku§7: ",
                    "",
                    AllDesc.tab+"§7Vous donne l'effet§e Vitesse I§7 pendant le temps de la§c transformation§7 ainsi que l'insensibilité au§c dégâts§7 de§a chute§7,",
                    "§7également, pendant la transformation vous utiliser le \"§aUchiwa§7\".",
                    "",
                    AllDesc.tab+"§aUchiwa§7: Tout les joueurs étant à moins de§c 50 blocs§7 et qui ne sont pas§c Muzan§7 seront téléporter§c 50 blocs§7 pour haut.",
                    "",
                    "§8 -§c Aizetsu§7: ",
                    "§7§oCette§c§o forme§7§o nécessite d'avoir d'abord utiliser§c Sekido§7.",
                    "",
                    AllDesc.tab+"§7Vous donne l'effet§c Force I§7 pendant le temps de la§c transformation§7, également pendant la transformation vous pouvez utiliser \"§cAffaiblissement§7\".",
                    "",
                    AllDesc.tab+"§cAffaiblissement§7: Tout les joueurs étant dans un rayon de§c 50 blocs§7 et qui ne sont pas§c démons§7 obtiendront§c 30 secondes§7 de§c Weakness I§7.",
                    "",
                    "§8 -§9 Urami§7: ",
                    "§7§oCette§c§o forme§7§o nécessite d'avoir d'abord utiliser§c Sekido§7§o ou§a Karaku§7.",
                    "",
                    AllDesc.tab+"§7Vous donne les effets§c Weakness I§7 et§ç Résistance II§7 pendant le temps de la§c transformation§7, également, si vous§c mourrez§7 pendant la§c transformation§7",
                    "§7vous§c ressusciterez§7 en perdant§c 2❤ permanents§7.",
                    "",
                    "§8 -§a Urogi§7: ",
                    "§7§oCette§c§o forme§7§o nécessite d'avoir d'abord utiliser§a Karaku§7.",
                    "",
                    AllDesc.tab+"§7Vous donne l'effet§e Speed I§7 pendant le temps de la§c transformation§7, également pendant la transformation vous pouvez utiliser \"§aCri Sonique§7\".",
                    "",
                    AllDesc.tab+"§aCri Sonique§7: Tire des§c cercles§7 de§c particule§7 devant vous, tout les joueurs touchés obtiennent pendant§c 20 secondes§7 les effets:§c Slowness I§7,",
                    "§cWeakness I§7 et§c Nausée I§7.",
                    "",
                    "§8 -§e Zohakuten§7: ",
                    "§7§oCette§c§o forme§7§o vous permettra d'utiliser les pouvoirs des§c§o formes§7§o que vous aviez choisis jusqu'ici, les pouvoirs de§c Sekido§7§o ou de§a Karaku§7§o seront",
                    "§7§oplacer sur le§c clique gauche§7§o, pour utiliser les pouvoirs de§c Aizetsu§7§o ou de§a Urogi§7§o il faudra faire§c shift + clique gauche§7.",
                    "",
                    AllDesc.tab+"§7Vous donne les effets§c Force I§7 et§9 Résistance I§7 pendant le temps de la§c transformation§7."
            );
            this.khakkharaPower = new KhakkharaPower(role);
            role.addPower(khakkharaPower);
            this.uchiwaPower = new UchiwaPower(role);
            role.addPower(uchiwaPower);
            this.affaiblissementPower = new AffaiblissementPower(role);
            role.addPower(affaiblissementPower);
            this.criSoniquePower = new CriSoniquePower(role);
            role.addPower(criSoniquePower);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) map.get("event");
                if (playerInteractEvent.getAction().name().contains("RIGHT")) {
                    if (using) {
                        player.sendMessage("§7Vous ne pouvez pas encore changer d'§cÉmotions§7.");
                        return false;
                    }
                    final FastInv fastInv = new FastInv(27, "§cÉmotions");
                    if (amountChoices == 0) {
                        fastInv.setItem(12, new ItemBuilder(Material.BLAZE_ROD).setName("§cSekido").toItemStack(), event -> {
                            event.getWhoClicked().sendMessage("§7Vous avez choisis la forme \"§c Sekido§7\"");
                            this.amountChoices++;
                            event.getWhoClicked().closeInventory();
                            this.leftClickPower = khakkharaPower;
                            new FormeRunnable(this, "§cSekido");
                            getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*5, 0, false, false), EffectWhen.NOW);
                            formesTakes.add(FormHantengu.SEKIDO);
                        });
                        fastInv.setItem(14, new ItemBuilder(Material.FEATHER).setName("§aKaraku").toItemStack(), event -> {
                            event.getWhoClicked().sendMessage("§7Vous avez choisis la forme \"§aKaraku§7\"");
                            this.amountChoices++;
                            event.getWhoClicked().closeInventory();
                            new FormeRunnable(this, "§aKaraku");
                            this.leftClickPower = uchiwaPower;
                            getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 0, false, false), EffectWhen.NOW);
                            formesTakes.add(FormHantengu.KARAKU);
                        });
                    }
                    if (amountChoices == 1) {
                        if (formesTakes.contains(FormHantengu.SEKIDO)) {
                            fastInv.setItem(12, new ItemBuilder(Material.DIAMOND_SWORD).setName("§cAizetsu").toItemStack(), event -> {
                                this.amountChoices++;
                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§7Vous avez choisis la forme \"§cAizetsu§7\"");
                                new FormeRunnable(this, "§cAizetsu");
                                formesTakes.add(FormHantengu.AIZETSU);
                                this.leftClickPower = affaiblissementPower;
                                getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*5, 0, false, false), EffectWhen.NOW);
                            });
                            if (getRole().getMaxHealth() > 10.0) {
                                fastInv.setItem(14, new ItemBuilder(Material.IRON_CHESTPLATE).setName("§9Urami").toItemStack(), event -> {
                                    this.amountChoices++;
                                    event.getWhoClicked().closeInventory();
                                    event.getWhoClicked().sendMessage("§7Vous avez choisis la forme \"§9Urami§7\"");
                                    formesTakes.add(FormHantengu.URAMI);
                                    new FormeRunnable(this, "§9Urami");
                                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 1, false, false), EffectWhen.NOW);
                                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*60*5, 0, false, false), EffectWhen.NOW);
                                });
                            }
                        } else if (formesTakes.contains(FormHantengu.KARAKU)) {
                            if (getRole().getMaxHealth() > 10.0) {
                                fastInv.setItem(12, new ItemBuilder(Material.IRON_CHESTPLATE).setName("§9Urami").toItemStack(), event -> {
                                    this.amountChoices++;
                                    event.getWhoClicked().closeInventory();
                                    event.getWhoClicked().sendMessage("§7Vous avez choisis la forme \"§9Urami§7\"");
                                    formesTakes.add(FormHantengu.URAMI);
                                    new FormeRunnable(this, "§9Urami");
                                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 1, false, false), EffectWhen.NOW);
                                    getRole().givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*60*5, 0, false, false), EffectWhen.NOW);
                                });
                            }
                            fastInv.setItem(14, new ItemBuilder(Material.YELLOW_FLOWER).setName("§aUrogi").toItemStack(), event -> {
                                this.amountChoices++;
                                event.getWhoClicked().closeInventory();
                                event.getWhoClicked().sendMessage("§7Vous avez choisis la forme \"§aUrogi§7\"");
                                formesTakes.add(FormHantengu.UROGI);
                                new FormeRunnable(this, "§aUrogi");
                                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 0, false, false), EffectWhen.NOW);
                                this.leftClickPower = criSoniquePower;
                            });
                        }
                    }
                    if (amountChoices == 2) {
                        fastInv.setItem(13, new ItemBuilder(Material.NETHER_STAR).setName("§eZohakuten").toItemStack(), event -> {
                            this.amountChoices = 0;
                            event.getWhoClicked().closeInventory();
                            new FormeRunnable(this, "§eZohakuten");
                            event.getWhoClicked().sendMessage("§7Vous avez choisis la forme \"§eZohakuten§7\"");
                            for (final FormHantengu string : this.formesTakes) {
                                switch (string) {
                                    case SEKIDO:
                                        this.leftClickPower = khakkharaPower;
                                        continue;
                                    case KARAKU:
                                        this.leftClickPower = uchiwaPower;
                                        continue;
                                    case AIZETSU:
                                        this.shiftClickPower = affaiblissementPower;
                                        continue;
                                    case UROGI:
                                        this.shiftClickPower = criSoniquePower;
                                        break;
                                }
                            }
                            this.formesTakes.add(FormHantengu.ZOHAKUTEN);
                            getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*5, 0, false, false), EffectWhen.NOW);
                            getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), EffectWhen.NOW);
                        });
                    }
                    fastInv.open(player);
                } else {
                    if (this.leftClickPower == null || !using) {
                        player.sendMessage("§7Vous n'avez aucun pouvoir équiper sur votre§c clique gauche§7 pour l'instant...");
                        return false;
                    }
                    if (player.isSneaking() && this.shiftClickPower != null) {
                        return this.shiftClickPower.checkUse(player, map);
                    }
                    return this.leftClickPower.checkUse(player, map);
                }
            }
            return false;
        }

        @EventHandler
        private void onDamage(final EntityDamageEvent event) {
            if (!(event.getEntity() instanceof Player))return;
            Bukkit.getLogger().info("Cause = "+event.getCause());
            if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL))return;
            Bukkit.getLogger().info("using = "+using);
            if (!using)return;
            if (!event.getEntity().getUniqueId().equals(getRole().getPlayer()))return;
            for (FormHantengu formesTake : this.formesTakes) {
                Bukkit.getLogger().info("Form: "+formesTake);
            }
            if (this.formesTakes.contains(FormHantengu.KARAKU) || this.formesTakes.contains(FormHantengu.UROGI)) {
                event.setDamage(0.0);
                event.setCancelled(true);
            }
        }
        @EventHandler
        private void onDeath(final UHCDeathEvent event) {
            if (!event.getPlayer().getUniqueId().equals(getRole().getPlayer()))return;
            if (this.amountChoices < 2)return;
            if (!using)return;
            if (!this.formesTakes.contains(FormHantengu.URAMI))return;
            final ItemStack[] itemStacks = event.getPlayer().getInventory().getContents();
            final ItemStack[] armors = event.getPlayer().getInventory().getArmorContents();
            event.setCancelled(true);
            final Location location = Loc.getRandomLocationAroundPlayer(event.getPlayer(), 50);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                final Player player = Bukkit.getPlayer(getRole().getPlayer());
                if (player != null) {
                    player.spigot().respawn();
                    player.getInventory().setArmorContents(armors);
                    player.getInventory().setContents(itemStacks);
                    player.teleport(location);
                    player.sendMessage("§7Vous avez été§a ressuscité§7 par votre pouvoir d'§9Urami§7.");
                    getRole().setMaxHealth(getRole().getMaxHealth()-4.0);
                    player.setMaxHealth(getRole().getMaxHealth());
                    player.setHealth(player.getMaxHealth());
                }
            },20);
        }

        private synchronized void removeWeakness() {
            for (PotionEffect potionEffect : new HashSet<>(this.getRole().getEffects().keySet())) {
                if (!potionEffect.getType().equals(PotionEffectType.WEAKNESS))continue;
                if (!this.getRole().getEffects().get(potionEffect).equals(EffectWhen.PERMANENT))continue;
                this.getRole().getEffects().remove(potionEffect);
                Bukkit.getScheduler().runTask(getPlugin(), () -> {
                    final Player player = Bukkit.getPlayer(getRole().getPlayer());
                    if (player != null) {
                        player.removePotionEffect(PotionEffectType.WEAKNESS);
                    }
                });
                break;
            }
        }
        private synchronized void giveWeakness() {
            if (this.formesTakes.contains(FormHantengu.ZOHAKUTEN)) {
                this.formesTakes.clear();
            }
            Bukkit.getScheduler().runTask(getPlugin(), () -> getRole().givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0, false, false), EffectWhen.PERMANENT));
        }
        private static class FormeRunnable extends BukkitRunnable {

            private final ChoicePower choicePower;
            private final String forme;
            private int timeLeft = 60*5;

            private FormeRunnable(ChoicePower choicePower, String forme) {
                this.choicePower = choicePower;
                this.forme = forme;
                choicePower.removeWeakness();
                this.choicePower.using = true;
                runTaskTimerAsynchronously(this.choicePower.getPlugin(), 0, 20);
            }

            @Override
            public void run() {
                if (!this.choicePower.getRole().getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    this.choicePower.getRole().getGamePlayer().getActionBarManager().removeInActionBar("hantengu."+forme);
                    this.choicePower.getRole().getGamePlayer().sendMessage("§7Vous ne ressentez plus les effets de§c "+forme+"§7...");
                    choicePower.giveWeakness();
                    this.choicePower.leftClickPower = null;
                    this.choicePower.using = false;
                    this.choicePower.shiftClickPower = null;
                    cancel();
                    return;
                }
                this.timeLeft--;
                this.choicePower.getRole().getGamePlayer().getActionBarManager().updateActionBar("hantengu."+forme, "§bTemps restant (§c"+forme+"§b): §c"+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
            }
        }
        private static class KhakkharaPower extends Power {

            public KhakkharaPower(@NonNull RoleBase role) {
                super("§cKhakkhara§r", new Cooldown(60*2), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                for (Player target : player.getWorld().getPlayers()) {
                    if (target.getUniqueId().equals(player.getUniqueId()))continue;
                    if (getRole().getGameState().hasRoleNull(target.getUniqueId()))continue;
                    if (target.getLocation().distance(player.getLocation()) > 50)continue;
                    final RoleBase role = getRole().getGameState().getGamePlayer().get(target.getUniqueId()).getRole();
                    if (role instanceof DemonsRoles)continue;
                    if (role.getOriginTeam().equals(TeamList.Demon))continue;
                    if (role.getTeam().equals(TeamList.Demon))continue;
                    role.givePotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*5, 3, false, false), EffectWhen.NOW);
                    role.givePotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*5, 3, false, false), EffectWhen.NOW);
                    target.getWorld().strikeLightningEffect(target.getEyeLocation());
                    target.setHealth(Math.max(target.getHealth()-4.0, 1.0));
                    target.sendMessage("§7Vous avez été toucher par le§c Khakkhara§7 de§c Sekido§7.");
                    player.sendMessage("§c"+target.getDisplayName()+"§7 a été toucher par votre§c Khakkhara§7.");
                }
                return true;
            }
        }
        private static class UchiwaPower extends Power {

            public UchiwaPower(@NonNull RoleBase role) {
                super("§aUchiwa§7", new Cooldown(60*2), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                for (Player target : player.getWorld().getPlayers()) {
                    if (target.getUniqueId().equals(player.getUniqueId()))continue;
                    if (getRole().getGameState().hasRoleNull(target.getUniqueId()))continue;
                    if (target.getLocation().distance(player.getLocation()) > 50)continue;
                    final RoleBase role = getRole().getGameState().getGamePlayer().get(target.getUniqueId()).getRole();
                    if (role instanceof MuzanV2)continue;
                    target.sendMessage("§7Vous avez été toucher par l'§a Uchiwa§7 de§a Karaku§7.");
                    player.sendMessage("§c"+target.getDisplayName()+"§7 a été toucher par votre§a Uchiwa§7.");
                    target.teleport(target.getEyeLocation().add(0, 50, 0));
                }
                return true;
            }
        }
        private static class AffaiblissementPower extends Power implements Listener {

            private final List<UUID> uuidList = new ArrayList<>();

            public AffaiblissementPower(@NonNull RoleBase role) {
                super("§cAffaiblissement§7", new Cooldown(60*2), role);
                setShowInDesc(false);
                EventUtils.registerRoleEvent(this);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                for (Player target : player.getWorld().getPlayers()) {
                    if (target.getUniqueId().equals(player.getUniqueId()))continue;
                    if (target.getLocation().distance(player.getLocation()) > 50)continue;
                    final RoleBase role = getRole().getGameState().getGamePlayer().get(target.getUniqueId()).getRole();
                    if (role instanceof DemonsRoles)continue;
                    if (role.getOriginTeam().equals(TeamList.Demon))continue;
                    if (role.getTeam().equals(TeamList.Demon))continue;
                    target.sendMessage("§7Vous avez été toucher par l'§c Affaiblissement§7 d'§c Aizetsu§7.");
                    player.sendMessage("§c"+target.getDisplayName()+"§7 a été toucher par votre§c Affaiblissement§7.");
                    role.givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*30, 0, false, false), EffectWhen.NOW);
                    uuidList.add(target.getUniqueId());
                }
                return true;
            }
            @EventHandler(priority = EventPriority.LOW)
            private void onBattle(final EntityDamageByEntityEvent event) {
                if (!(event.getEntity() instanceof Player))return;
                if (!event.getDamager().getUniqueId().equals(getRole().getPlayer()))return;
                if (!uuidList.contains(event.getEntity().getUniqueId()))return;
                event.setDamage(event.getDamage()*1.1);
            }
            @EventHandler
            private void onEndCooldown(final CooldownFinishEvent event) {
                if (event.getCooldown() == null)return;
                if (!event.getCooldown().getUniqueId().equals(getCooldown().getUniqueId()))return;
                this.uuidList.clear();
                getRole().getGamePlayer().sendMessage("§7Vous n'infligez plus de dégâts supplémentaire...");
            }
        }
        private static class CriSoniquePower extends Power {

            public CriSoniquePower(@NonNull RoleBase role) {
                super("Cri Sonique", new Cooldown(60*2), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                player.sendMessage("§7Votre§e Cri Sonique§7 s'apprête à retentir...");
                startDirectionalWifi(player);
                return true;
            }
            //Fait par ChatGPT5
            private void startDirectionalWifi(final Player caster) {
                final Location origin = caster.getLocation().clone().add(0, 1.0, 0);
                final Vector dir = caster.getEyeLocation().getDirection().clone().normalize();

                Vector approxUp = new Vector(0, 1, 0);
                if (Math.abs(dir.dot(approxUp)) > 0.99) {
                    approxUp = new Vector(1, 0, 0);
                }
                final Vector side = dir.clone().crossProduct(approxUp).normalize();
                final Vector upVec = side.clone().crossProduct(dir).normalize();

                final Map<UUID, Long> lastDamaged = new HashMap<>();
                final long DAMAGE_COOLDOWN_MS = 500L;

                new BukkitRunnable() {
                    int ticks = 0;
                    int arcStep = 0; // nombre de cercles visibles (1 → 5)

                    @Override
                    public void run() {
                        if (!caster.isOnline() || ticks > 40) {
                            cancel();
                            return;
                        }

                        // Débloque progressivement les cercles : 1 cercle de plus toutes les 10 ticks
                        if (ticks % 10 == 0 && arcStep < 5) {
                            arcStep++;
                        }

                        for (int arcIndex = 1; arcIndex <= arcStep; arcIndex++) {
                            double distanceAlong = arcIndex * 1.2;
                            double radius = 0.6 * arcIndex;
                            Location arcCenter = origin.clone().add(dir.clone().multiply(distanceAlong));

                            // --- Particules (visuel du cercle complet) ---
                            for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 30) {
                                double x = Math.cos(angle) * radius;
                                double y = Math.sin(angle) * radius;

                                Location particleLoc = arcCenter.clone()
                                        .add(side.clone().multiply(x))
                                        .add(upVec.clone().multiply(y));

                                spawnParticle(particleLoc, (float) 227, (float) 172, (float) 0);
                            }

                            // --- Dégâts pour tous les joueurs dans le cercle ---
                            for (Player target : arcCenter.getWorld().getPlayers()) {
                                if (target.equals(caster)) continue;

                                // distance au centre projetée dans le plan du cercle
                                double dx = target.getLocation().getX() - arcCenter.getX();
                                double dy = target.getLocation().getY() - arcCenter.getY();
                                double dz = target.getLocation().getZ() - arcCenter.getZ();

                                // norme projetée dans le plan (ignore l’axe "dir")
                                double dist2D = Math.sqrt(
                                        dx * dx + dy * dy + dz * dz
                                );

                                if (dist2D <= radius) {
                                    UUID id = target.getUniqueId();
                                    long now = System.currentTimeMillis();
                                    Long last = lastDamaged.get(id);
                                    if (last != null && (now - last) < DAMAGE_COOLDOWN_MS) continue;

                                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*20, 0, false, false), true);
                                    target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*20, 0, false, false), true);
                                    target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*20, 0, false, false), true);
                                    target.sendMessage("§7Vous avez été toucher par le§e Cri Sonique§7 d'§aUrogi");
                                    getRole().getGamePlayer().sendMessage("§c"+target.getDisplayName()+"§7 a été toucher par votre§e Cri Sonique§7.");
                                    lastDamaged.put(id, now);
                                }
                            }
                        }


                        ticks += 5;
                    }
                }.runTaskTimer(Main.getInstance(), 0, 2);
            }

        }
    }
}