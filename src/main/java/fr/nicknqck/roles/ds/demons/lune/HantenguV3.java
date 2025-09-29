package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class HantenguV3 extends DemonsRoles {

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
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0, false, false), EffectWhen.PERMANENT);
        addPower(new ChoicePower(this), true);
        super.RoleGiven(gameState);
    }

    private static class ChoicePower extends ItemPower {

        private int amountChoices = 0;
        private Power leftClickPower;
        private final KhakkharaPower khakkharaPower;
        private final UchiwaPower uchiwaPower;
        private boolean using = false;


        public ChoicePower(@NonNull RoleBase role) {
            super("Émotions", null, new ItemBuilder(Material.NETHER_STAR).setName("§cÉmotions"), role);
            this.khakkharaPower = new KhakkharaPower(role);
            role.addPower(khakkharaPower);
            this.uchiwaPower = new UchiwaPower(role);
            role.addPower(uchiwaPower);
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
                        });
                        fastInv.setItem(14, new ItemBuilder(Material.FEATHER).setName("§aKaraku").toItemStack(), event -> {
                            event.getWhoClicked().sendMessage("§7Vous avez choisis la forme \"§aKaraku§7\"");
                            this.amountChoices++;
                            event.getWhoClicked().closeInventory();
                            new FormeRunnable(this, "§aKaraku");
                            this.leftClickPower = uchiwaPower;
                            getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 0, false, false), EffectWhen.NOW);
                        });
                    }
                    if (amountChoices == 1) {
                    }
                    fastInv.open(player);
                } else {
                    if (this.leftClickPower == null || !using) {
                        player.sendMessage("§7Vous n'avez aucun pouvoir équiper sur votre§c clique gauche§7 pour l'instant...");
                        return false;
                    }
                    return this.leftClickPower.checkUse(player, map);
                }
            }
            return false;
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
            Bukkit.getScheduler().runTask(getPlugin(), () -> {
               getRole().givePotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0, false, false), EffectWhen.PERMANENT);
            });
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
    }
}