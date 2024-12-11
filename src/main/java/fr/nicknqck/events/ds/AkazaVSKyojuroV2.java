package fr.nicknqck.events.ds;

import fr.nicknqck.GameListener;
import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.ds.demons.lune.Akaza;
import fr.nicknqck.roles.ds.slayers.pillier.KyojuroV2;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class AkazaVSKyojuroV2 extends Event implements Listener {

    private Akaza akaza;
    private KyojuroV2 kyojuro;
    private Location originalAkazaLocation;
    private Location originalKyojuroLocation;
    private boolean AkazaWin = false;
    private boolean KyojuroWin = false;
    private boolean endEvent = false;
    private boolean realEnd = false;
    private boolean activated = false;

    @Override
    public String getName() {
        return "§cAkaza§6 vs§a Kyojuro";
    }

    @Override
    public void onProc(GameState gameState) {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.dispatchCommand(console, "nakime E4jL5cOzv0sI2XqY7wNpD3Ab");
        if (Bukkit.getWorld("AkazaVSKyojuro") != null) {
            Player pkyojuro = gameState.getOwner(GameState.Roles.Kyojuro);
            Player pAkaza = gameState.getOwner(GameState.Roles.Akaza);
            if (pAkaza != null && pkyojuro != null) {
                Akaza akaza = (Akaza) gameState.getPlayerRoles().get(pAkaza);
                KyojuroV2 kyojuro = (KyojuroV2) gameState.getPlayerRoles().get(pkyojuro);
                this.akaza = akaza;
                this.kyojuro = kyojuro;
                this.originalAkazaLocation = pAkaza.getLocation();
                pAkaza.teleport(new Location(Bukkit.getWorld("AkazaVSKyojuro"), -40, 6, -24.5, -90, 0));
                pAkaza.playSound(pAkaza.getEyeLocation(), "dsmtp.avsk", 10, 1);
                this.originalKyojuroLocation = pkyojuro.getLocation();
                pkyojuro.teleport(new Location(Bukkit.getWorld("AkazaVSKyojuro"), -3.5, 7, -23.5, 90, 0));
                pkyojuro.playSound(pkyojuro.getLocation(), "dsmtp.avsk", 10, 1);
                Bukkit.getWorld("AkazaVSKyojuro").setGameRuleValue("randomTickSpeed", "3");
                Bukkit.broadcastMessage(getName()+"§r vient de ce déclancher !");
                EventUtils.registerRoleEvent(this);
                new CombatRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                this.activated = true;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void detectWhoWin() {
        if (realEnd)return;
        boolean end = AkazaWin;
        if (KyojuroWin) {
            end = true;
        }
        String title = "§cLe combat est fini";
        String subTitle = "";
        if (AkazaWin) {
            subTitle = "§7Victoire de§c Akaza";
        }
        if (KyojuroWin) {
            subTitle = "§7Victoire de§a Kyojuro";
        }
        if (originalAkazaLocation != null && akaza != null && end && AkazaWin) {
            Player akaza = Bukkit.getPlayer(this.akaza.getPlayer());
            if (akaza == null)return;
            akaza.teleport(originalAkazaLocation);
            originalAkazaLocation = null;
            akaza.sendTitle(title, subTitle);
            this.akaza.addPower(new CompaCommandPower(this.akaza));
        }
        if (originalKyojuroLocation != null && kyojuro != null && end && KyojuroWin) {
            Player kyojuro = Bukkit.getPlayer(this.kyojuro.getPlayer());
            if (kyojuro == null) return;
            kyojuro.teleport(originalKyojuroLocation);
            kyojuro.sendTitle(title, subTitle);
            originalKyojuroLocation = null;
            this.kyojuro.addPower(new VagueItemPower(this.kyojuro), true);
        }
        if (end && !isLocationNull() && !realEnd) {
            this.endEvent = true;
            Bukkit.broadcastMessage("Fin de l'évènement "+getName());
            realEnd = true;
        }
    }
    private boolean isLocationNull() {
        return originalAkazaLocation == null && originalKyojuroLocation == null;
    }
    @Override
    public ItemStack getMenuItem() {
        return new ItemBuilder(Material.DIAMOND_SWORD).setName(getName()).setLore(getLore()).toItemStack();
    }

    @Override
    public boolean canProc(GameState gameState) {
        return containsRoles(gameState);
    }

    private boolean containsRoles(final GameState gameState) {
        return gameState.attributedRole.contains(GameState.Roles.Kyojuro) && !gameState.DeadRole.contains(GameState.Roles.Kyojuro) && gameState.attributedRole.contains(GameState.Roles.Akaza) && !gameState.DeadRole.contains(GameState.Roles.Akaza);
    }

    @EventHandler
    private void onKill(UHCPlayerKillEvent event) {
        if (!endEvent) {
            if (akaza != null && kyojuro != null) {
                if (event.getKiller().getUniqueId() == kyojuro.getPlayer()) {//donc if victim == kyojuro donc winner == Akaza
                    akaza.givePotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
                    AkazaWin = true;
                    akaza.getGamePlayer().sendMessage("§7Vous avez gagné votre§c 1v1§7 contre§a§l Kyojuro§r§7 !");
                }
                if (event.getKiller().getUniqueId() == akaza.getPlayer()) {//donc if victim == akaza, donc winner == Kyojuro
                    KyojuroWin = true;
                    this.kyojuro.givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
                    kyojuro.getGamePlayer().sendMessage("§7Vous avez gagné votre 1v1 contre§c§l Akaza§r§7 !");

                }
            }
        }
    }

    @Override
    public boolean isActivated() {
        return this.activated;
    }

    private static class VagueItemPower extends ItemPower implements Listener {

        protected VagueItemPower(@NonNull KyojuroV2 role) {
            super("§6Vague Flamboyante", new Cooldown(60*8), new ItemBuilder(Material.NETHER_STAR)
                    .setName("§6Vague Flamboyante"), role, "§r§7Pendant§b 10s§7 quand un joueur vous frappe il y aura 10% de chance qu'il§6 brûle§7 et subisse§c 1/2❤");
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                EventUtils.registerEvents(this);
                player.sendMessage("§7Maintenant vous§c enflammez§7 les joueurs qui vous frappe pendant§b 10s");
                Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> EventUtils.unregisterEvents(this), 20*10);
            }
            return false;
        }
        @EventHandler
        private void onDamage(EntityDamageByEntityEvent event) {
            if (event.getEntity().getUniqueId().equals(getRole().getPlayer())) {
                event.getDamager().setFireTicks(event.getDamager().getFireTicks()+160);
                event.getDamager().sendMessage("§aKyojuro§7 vous à§6§l enflammé");
                event.getEntity().sendMessage("§7Vous avez§6 brulé§7§l "+event.getDamager().getName());
                if (!(event.getDamager() instanceof Player))return;
                final Player attacker = (Player) event.getDamager();
                if (attacker.getHealth() > 1.0) {
                    attacker.setHealth(attacker.getHealth()-1.0);
                } else {
                    attacker.setHealth(1.0);
                }
            }
        }
    }
    private static class CompaCommandPower extends CommandPower implements Listener {

        private boolean use = false;

        public CompaCommandPower(@NonNull Akaza role) {
            super("§c/ds compa", "compa", new Cooldown(60*8), role, CommandType.DS);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            player.sendMessage("§7Activation du§c compa");
            this.use = true;
            getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> this.use = false, 20*120);
            return true;
        }
        @EventHandler
        private void onBattle(EntityDamageByEntityEvent event) {
            if (event.getEntity().getUniqueId().equals(getRole().getPlayer())) {
                if (!use)return;
                if (Main.RANDOM.nextInt(100) <= 10) {
                    event.setDamage(0.0);
                    event.getEntity().sendMessage("§7Vous avez esquivé les dégats d'une attaque !");
                    ((Player) event.getEntity()).setNoDamageTicks(20);
                }
            }
        }
    }
    private static class CombatRunnable extends BukkitRunnable {

        private final Akaza akaza;
        private final KyojuroV2 kyojuro;
        private final AkazaVSKyojuroV2 akazaVSKyojuroV2;
        private final GameState gameState;
        private int battleTime = 0;

        private CombatRunnable(AkazaVSKyojuroV2 akazaVSKyojuroV2) {
            this.akazaVSKyojuroV2 = akazaVSKyojuroV2;
            this.akaza = akazaVSKyojuroV2.akaza;
            this.kyojuro = akazaVSKyojuroV2.kyojuro;
            this.gameState = this.akaza.getGameState();
        }

        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (this.akazaVSKyojuroV2.realEnd && !this.akazaVSKyojuroV2.isLocationNull() && this.akaza != null && this.kyojuro != null) {
                Player akaza = Bukkit.getPlayer(this.akaza.getPlayer());
                Player kyojuro = Bukkit.getPlayer(this.kyojuro.getPlayer());
                if (kyojuro != null) {
                    Location loc = GameListener.generateRandomLocation(Main.getInstance().getWorldManager().getGameWorld());
                    kyojuro.teleport(loc);
                    this.akazaVSKyojuroV2.originalKyojuroLocation = null;
                }
                if (akaza != null) {
                    Location loc = GameListener.generateRandomLocation(Main.getInstance().getWorldManager().getGameWorld());
                    akaza.teleport(loc);
                    this.akazaVSKyojuroV2.originalAkazaLocation = null;
                }
                cancel();
                return;
            }
            assert this.akaza != null;
            final Player akaza = Bukkit.getPlayer(this.akaza.getPlayer());
            if (akaza != null) {
                NMSPacket.sendActionBar(akaza, "§bTemp de§c combat§b restant: §c"+ StringUtils.secondsTowardsBeautiful(battleTime));
            }
            assert this.kyojuro != null;
            Player kyojuro = Bukkit.getPlayer(this.kyojuro.getPlayer());
            if (kyojuro != null) {
                NMSPacket.sendActionBar(kyojuro, "§bTemp de§c combat§b restant: §c"+ StringUtils.secondsTowardsBeautiful(battleTime));
            }
            battleTime++;
            if (this.akazaVSKyojuroV2.realEnd) {
                cancel();
                return;
            }
            this.akazaVSKyojuroV2.detectWhoWin();

        }
    }
}