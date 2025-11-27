package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.events.custom.power.CooldownUpdateEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.HShinobiRoles;
import fr.nicknqck.roles.ns.power.Rasengan;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.PotionUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MinatoV2 extends HShinobiRoles {

    private boolean knowNaruto = false;

    public MinatoV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.GENIE;
    }

    @Override
    public String getName() {
        return "Minato";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Minato;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine(knowNaruto ? "§7Vous possédez l'effet§c Force I§7 proche de§a Naruto§7." : "§7Au bout de§c 5 minutes§7 passé proche de§a Naruto§7 vous connaitrez son pseudonyme.")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        addPower(new ArcHiraishin(this), true);
        addPower(new Rasengan(this), true);
        addPower(new BalisesPermanentes(this), true);
        new NarutoRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 1, 20);
        setChakraType(Chakras.KATON);
    }
    private static class NarutoRunnable extends BukkitRunnable {

        private final MinatoV2 minatoV2;
        private int time = 0;

        private NarutoRunnable(MinatoV2 minatoV2) {
            this.minatoV2 = minatoV2;
        }

        @Override
        public void run() {
            if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!minatoV2.getGamePlayer().isAlive()) {
                return;
            }
            @NonNull final List<GamePlayer> gamePlayerList = new ArrayList<>(Loc.getNearbyGamePlayers(this.minatoV2.getGamePlayer().getLastLocation(), 30.0));
            if (this.minatoV2.knowNaruto) {
                for (GamePlayer gamePlayer : gamePlayerList) {
                    if (gamePlayer.getRole() == null)continue;
                    if (gamePlayer.getRole() instanceof NarutoV2) {
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.minatoV2.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NOW));
                    }
                }
                return;
            }
            if (!GameState.getInstance().getAttributedRole().contains(Roles.Naruto)) {
                this.minatoV2.knowNaruto = true;
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    this.minatoV2.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.DAY);
                    this.minatoV2.getGamePlayer().sendMessage("§aNaruto§7 n'est pas dans la§c composition de la partie§7, vous obtenez donc l'effet§c Force I§7 le §e jour§7.");
                });
                cancel();
                return;
            }
            for (GamePlayer gamePlayer : gamePlayerList) {
                if (gamePlayer.getUuid().equals(this.minatoV2.getPlayer()))continue;
                if (gamePlayer.getRole() == null)continue;
                if (gamePlayer.getRole() instanceof NarutoV2) {
                    time++;
                    break;
                }
            }
            if (this.time >= 60*5) {
                this.minatoV2.knowNaruto = true;
                this.minatoV2.addKnowedRole(NarutoV2.class);
                this.minatoV2.getGamePlayer().sendMessage("§7Vous connaissez maintenant§a Naruto§7.");
            }
        }
    }
    private static class ArcHiraishin extends ItemPower implements Listener {

        private AroundArrowRunnable aroundArrowRunnable;
        private boolean own = false;

        public ArcHiraishin(@NonNull RoleBase role) {
            super("§aArc Hiraishin§r", new Cooldown(60*6), new ItemBuilder(Material.BOW)
                    .setName("§aArc Hiraishin")
                    .addEnchant(Enchantment.ARROW_INFINITE, 1)
                    .hideEnchantAttributes(), role,
                    "§7Lorsque vous lancez une§c flèche§7 avec l'§aArc Hiraishin§7, elle devient une§c balise§7.",
                    "",
                    "§7Vous pouvez§c vous téléportez§7 à la dernière§a balise§7 poser en faisant un§f shift + clique gauche§7,",
                    "§7vous pouvez également téléporter un autre joueur en le frappant.",
                    "",
                    "§7A chaque fois que quelqu'un se§a téléporte§7 à votre§a balise§7, elle sera supprimé.",
                    "§7Si vous utilisez votre§a Hiraishin§7 pour vous§a téléportez§c vous même§7, votre§c cooldown§7 s'écoulera§c 2x plus vite§7.",
                    "§cAucun joueur ne peut prendre de dégâts de cette arc."
            );
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.ATTACK_ENTITY)) {
                @NonNull final UHCPlayerBattleEvent uhcEvent = (UHCPlayerBattleEvent) map.get("event");
                @NonNull final EntityDamageByEntityEvent event = uhcEvent.getOriginEvent();
                if (!(event.getEntity() instanceof Player))return false;
                if (aroundArrowRunnable == null) {
                    player.sendMessage("§7Vous devez d'abord lancer un§c Kunai§7.");
                    return false;
                }
                @NonNull final Location location = this.aroundArrowRunnable.arrow.getLocation();
                @NonNull final Player target = (Player) event.getEntity();
                this.aroundArrowRunnable.arrow.remove();
                target.teleport(location);
                player.sendMessage("§7Vous avez utilisez l'§aHiraishin§7 sur§c "+uhcEvent.getVictim().getPlayerName()+"§7.");
                target.sendMessage("§7Vous avez été toucher par l'§aHiraishin§7.");
                PotionUtils.addTempNoFall(target.getUniqueId(), 1);
                event.setCancelled(true);
                this.own = false;
                return true;
            }
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (((PlayerInteractEvent) map.get("event")).getAction().name().contains("LEFT")){
                    if (!player.isSneaking()) {
                        player.sendMessage("§7Pour vous téléportez à votre§a Kunai§7 il vous faudra être§c accroupi§7.");
                        return false;
                    } else {
                        if (this.aroundArrowRunnable != null) {
                            @NonNull final Location location = this.aroundArrowRunnable.arrow.getLocation();
                            this.aroundArrowRunnable.arrow.remove();
                            player.teleport(location);
                            player.sendMessage("§7Vous avez utilisez l'§aHiraishin§7 sur§c vous même§7.");
                            PotionUtils.addTempNoFall(player.getUniqueId(), 1);
                            this.own = true;
                            return true;
                        }
                    }
                }
                return false;
            }
            return false;
        }
        @EventHandler
        private void onShoot(@NonNull final EntityShootBowEvent event) {
            if (!(event.getEntity() instanceof Player))return;
            if (!event.getBow().isSimilar(getItem()))return;
            if (!(event.getProjectile() instanceof Arrow))return;
            event.getProjectile().setMetadata("minato.hiraishin", new FixedMetadataValue(getPlugin(), getName()));
            this.aroundArrowRunnable = new AroundArrowRunnable(this, (Arrow) event.getProjectile());
            event.getEntity().sendMessage("§7Vous avez lancer un§a Hiraishin No Kunai§7.");
            ((Arrow) event.getProjectile()).setBounce(false);
        }
        @EventHandler
        private void onDamage(@NonNull final EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Arrow))return;
            final Arrow arrow = (Arrow) event.getDamager();
            if (arrow.hasMetadata("minato.hiraishin")) {
                event.setDamage(0.0);
                event.setCancelled(true);
            }
        }
        @EventHandler
        private void onCooldownUpdate(@NonNull final CooldownUpdateEvent event) {
            if (!event.getCooldown().getUniqueId().equals(getCooldown().getUniqueId()))return;
            if (own) {
                event.getCooldown().addSeconds(-1);
            }
        }
        private static class AroundArrowRunnable extends BukkitRunnable {

            private final ArcHiraishin arcHiraishin;
            private final Arrow arrow;
            private final UUID uuid;

            private AroundArrowRunnable(ArcHiraishin arcHiraishin, Arrow arrow) {
                this.arcHiraishin = arcHiraishin;
                this.arrow = arrow;
                this.uuid = UUID.randomUUID();
                runTaskTimerAsynchronously(arcHiraishin.getPlugin(), 1, 1);
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (arrow == null) {
                    this.arcHiraishin.getRole().getGamePlayer().sendMessage("§7Vous ne pouvez plus vous téléportez.");
                    cancel();
                    return;
                }
                if (arrow.isDead()) {
                    this.arcHiraishin.getRole().getGamePlayer().sendMessage("§7Vous ne pouvez plus vous téléportez.");
                    cancel();
                    return;
                }
                if (!arrow.hasMetadata("minato.hiraishin")) {
                    this.arcHiraishin.getRole().getGamePlayer().sendMessage("§7Vous ne pouvez plus vous téléportez.");
                    cancel();
                    return;
                }
                if (this.arcHiraishin.aroundArrowRunnable == null) {
                    cancel();
                    return;
                }
                if (!this.uuid.equals(this.arcHiraishin.aroundArrowRunnable.uuid)) {
                    this.arcHiraishin.getRole().getGamePlayer().sendMessage("§7L'emplacement de votre balise a été modifier.");
                    cancel();
                    return;
                }
                final Location location = arrow.getLocation();
                final Player player = Bukkit.getPlayer(this.arcHiraishin.getRole().getPlayer());
                if (player != null) {
                    MathUtil.sendParticleTo(player, EnumParticle.FLAME, location);
                }
            }
        }
    }
    private static class BalisesPermanentes extends ItemPower {

        private final LinkedHashMap<Location, String> balises;

        public BalisesPermanentes(@NonNull RoleBase role) {
            super("§aBalises permanentes§r", new Cooldown(60*10), new ItemBuilder(Material.NETHER_STAR).setName("§aBalises permanentes"), role,
                    "§7Vous pouvez poser une§a balise permanente§7 en faisant un§f shift + clique§7. (maximum 3)",
                    "",
                    "§7En faisant un§a clique§7 vous ouvre un menu contenant vos§a balise permanente§7,",
                    "§7vous pourrez vous y téléportez en faisant un§c clique gauche§7 et la renommé avec§f shift + clique§7.",
                    "",
                    "§7§oVous pouvez poser des§a balises permanentes§7§o même si vous êtes en§c cooldown§7.");
            this.balises = new LinkedHashMap<>();
            setWorkWhenInCooldown(true);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (player.isSneaking()) {
                    if (balises.size() >= 3) {
                        player.sendMessage("§cVous avez déjà poser toute vos réserves de§a balises permanentes§c.");
                        return false;
                    }
                    if (!player.getWorld().getName().equalsIgnoreCase("arena")) {
                        player.sendMessage("§cImpossible de poser une§a balise permanente§c ici§c.");
                        return false;
                    }
                    @NonNull final String name = "§bBalise n°"+(balises.size()+1);
                    balises.put(player.getLocation(), name);
                    player.sendMessage("§7Vous avez enregistré votre position sous le nom de "+name);
                } else {
                    if (getCooldown().isInCooldown()) {
                        sendCooldown(player);
                        return false;
                    }
                    if (this.balises.isEmpty()) {
                        player.sendMessage("§cIl faut d'abord poser une balise.§7 (§fShift + clique§7)");
                        return false;
                    }
                    openFastInv(player);
                }
                return false;
            }
            return false;
        }
        private void openFastInv(@NonNull final Player player) {
            @NonNull final FastInv fastInv = new FastInv(27, "§aBalises permanentes");
            fastInv.setItems(fastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setDurability(7).toItemStack());
            int i = 11;
            for (@NonNull final Location location : new LinkedList<>(this.balises.keySet())) {
                @NonNull final String name = this.balises.get(location);
                fastInv.setItem(i, new ItemBuilder(Material.BEACON)
                        .setName(name)
                                .setLore(
                                        "§cx§7:§c "+location.getBlockX(),
                                        "§cy§7:§c "+location.getBlockY(),
                                        "§cz§7:§c "+location.getBlockZ()
                                )
                        .toItemStack(), event -> {
                    event.getWhoClicked().closeInventory();
                    if (getCooldown().isInCooldown()) {
                        sendCooldown((Player) event.getWhoClicked());
                        return;
                    }
                    event.getWhoClicked().teleport(location);
                    event.getWhoClicked().sendMessage("§cVous vous êtes téléportez a la§a balise permanente§c: "+name);
                    getCooldown().use();
                });
                i+=2;
            }
            fastInv.open(player);
        }
    }
}