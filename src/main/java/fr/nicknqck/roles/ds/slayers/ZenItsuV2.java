package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.NightEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.demons.lune.Kaigaku;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class ZenItsuV2 extends SlayerRoles implements Listener {

    private TextComponent textComponent;
    private boolean killKaigaku = false;

    public ZenItsuV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.FOUDRE;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Zen'Itsu";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.ZenItsu;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return textComponent;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new EcouterCommande(this));
        addPower(new DieuFoudrePower(this), true);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        EventUtils.registerRoleEvent(this);
        AutomaticDesc automaticDesc = new AutomaticDesc(this).setPowers(getPowers());
        this.textComponent = automaticDesc.getText();
    }
    @EventHandler
    private void onNight(NightEvent event) {
        final int middleOfTheNight = event.getTimeNight()/2;
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 1, false, false), true);
            owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60, 0, false, false), true);
        }, 20L *middleOfTheNight);
    }
    @EventHandler
    private void onKill(UHCPlayerKillEvent event) {
        if (event.getKiller().getUniqueId().equals(getPlayer())) {
            GamePlayer gamePlayer = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId());
            if (gamePlayer == null)return;
            if (gamePlayer.getRole() == null)return;
            if (gamePlayer.getRole() instanceof Kaigaku) {
                this.killKaigaku = true;
            }
        }
    }
    private static class EcouterCommande extends CommandPower {

        public EcouterCommande(@NonNull RoleBase role) {
            super("§c/ds ecouter <pseudo>", "ecouter", new Cooldown(60*10), role, CommandType.DS);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> strings) {
            String[] args = (String[]) strings.get("args");
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    new EcouterTargeter(this, target);
                }
            }
            return false;
        }
        private void callDie(Player target, Player player, RoleBase role) {
            Player owner = Bukkit.getPlayer(getRole().getPlayer());
            if (owner == null)return;
            owner.sendMessage("§aAu loin vous entendez ce qui se§c trame§a proche de§c "+target.getName()+"§a, vous avez entendu les crie d'agonie de§c "+player.getName()+"§a qui était "+role.getOriginTeam().getColor()+role.getName());
        }

        private void call(Player player) {
            Player owner = Bukkit.getPlayer(getRole().getPlayer());
            if (owner == null)return;
            owner.sendMessage("§aAu loin vous entendez ce qui se passe avec§c "+player.getName()+"§a, on dirait qu'il a§c subit§a/§cinfliger§a des coups");
        }
        private static class EcouterTargeter implements Listener {

            private final EcouterCommande ecouterCommande;
            private final UUID tUUID;

            public EcouterTargeter(EcouterCommande ecouterCommande, Player target) {
                this.ecouterCommande = ecouterCommande;
                this.tUUID = target.getUniqueId();
                EventUtils.registerEvents(this);
                Bukkit.getScheduler().runTaskLaterAsynchronously(ecouterCommande.getPlugin(), () -> {
                    EventUtils.unregisterEvents(this);
                    this.ecouterCommande.getCooldown().addSeconds(60*5);
                    Player p = Bukkit.getPlayer(ecouterCommande.getRole().getPlayer());
                    if (p == null)return;
                    p.sendMessage("§aVotre écoute se termine.");
                }, 20*60*5);
            }

            @EventHandler
            private void onEntityDamage(EntityDamageByEntityEvent event) {
                if (event.getDamager().getUniqueId().equals(tUUID) && event.getDamager() instanceof Player) {
                    ecouterCommande.call((Player) event.getDamager());
                } else if (event.getEntity().getUniqueId().equals(tUUID) && event.getEntity() instanceof Player) {
                    ecouterCommande.call((Player) event.getEntity());
                }
            }
            @EventHandler
            private void onDie(UHCDeathEvent event) {
                Player target = Bukkit.getPlayer(tUUID);
                if (target == null)return;
                if (event.getPlayer().getWorld().equals(target.getWorld())) {
                    if (event.getPlayer().getLocation().distance(target.getLocation()) <= 10.0) {
                        this.ecouterCommande.callDie(target, event.getPlayer(), event.getRole());
                    }
                }
            }
        }
    }
    private static class DieuFoudrePower extends ItemPower {

        private final ZenItsuV2 zenItsuV2;

        protected DieuFoudrePower(@NonNull ZenItsuV2 role) {
            super("§eDieu de la foudre", new Cooldown(60*10), new ItemBuilder(Material.GLOWSTONE_DUST).setName("§eDieu de la foudre"), role);
            this.zenItsuV2 = role;
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60+(zenItsuV2.killKaigaku ? 20*30 : 0), 2, false, false), true);
                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*60*3, 1, false, false), true);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*60*3, 0, false, false), true);
                }, 20*60+(zenItsuV2.killKaigaku ? 20*30 : 0));
                return true;
            }
            return false;
        }
    }
}