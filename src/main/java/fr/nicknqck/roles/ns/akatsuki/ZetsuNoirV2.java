package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.EChakras;
import fr.nicknqck.enums.Intelligence;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.enums.TeamList;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.death.FinalDeathEvent;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.akatsuki.blancv2.ZetsuBlancV2;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.roles.ns.solo.jubi.ObitoV2;
import fr.nicknqck.utils.AttackUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZetsuNoirV2 extends AkatsukiRoles implements Listener {

    private boolean nagatoDeath = false;

    public ZetsuNoirV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.GENIE;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return new EChakras[]{
                EChakras.DOTON
        };
    }

    @Override
    public String getName() {
        return "Zetsu Noir";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.ZetsuNoir;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this).addCustomLine(this.nagatoDeath ? "" :"§7A la mort de§c Nagato§7, vous obtiendrez une liste de tout les membres encore en§a vie§7 de l'§cAkatsuki§7 (dont§d Obito§7)").getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new InvisibilitePower(this), true);
        addPower(new RegenPower(this));
        EventUtils.registerRoleEvent(this);
        getGamePlayer().startChatWith("§cZetsu Noir:", "!", ZetsuBlancV2.class);
    }

    @EventHandler
    private void onEndGiveRole(RoleGiveEvent event) {
        if (!event.isEndGive())return;
        boolean b = false;
        for (GamePlayer gamePlayer : event.getGameState().getGamePlayer().values()) {
            if (gamePlayer == null)continue;
            if (!gamePlayer.check())continue;
            if (gamePlayer.getRole() instanceof NagatoV2) {
                b = true;
                break;
            }
        }
        if (!b) {
            onNagatoDeath();
        }
    }

    private void onNagatoDeath() {
        if (this.nagatoDeath) return;
        for (GamePlayer gamePlayer : getGameState().getGamePlayer().values()) {
            if (gamePlayer == null)continue;
            if (!gamePlayer.check())continue;
            if (gamePlayer.getRole().getTeam().equals(TeamList.Akatsuki) || gamePlayer.getRole().getOriginTeam().equals(TeamList.Akatsuki) || gamePlayer.getRole() instanceof ObitoV2) {
                getGamePlayer().sendMessage("§c"+gamePlayer.getPlayerName()+"§7 semble faire ou avoir fait partie de l'§cAkatsuki§7.");
            }
        }
        this.nagatoDeath = true;
    }
    @EventHandler
    private void onDeath(@NonNull final FinalDeathEvent event) {
        if (event.getRole() == null)return;
        if (event.getRole() instanceof NagatoV2) {
            onNagatoDeath();
        }
    }

    private static class InvisibilitePower extends ItemPower {

        private InvisibilitePower.InvisibiliteRunnable runnable;
        private final HashMap<Integer, ItemStack> armorContents = new HashMap<>();
        private boolean invisible = false;
        private final Cooldown cooldown;

        protected InvisibilitePower(RoleBase role) {
            super("Invisibilité", null, new ItemBuilder(Material.NETHER_STAR).setName("§aInvisibilité").setLore("§7Vous permez de devenir invisible"), role,
                    "§7Vous permet de vous rendre§a invisible§7 et d'avoir§c Speed II§7 pendant§c 5 minutes§7.",
                    "§7",
                    "§7En réutilisant ce§c pouvoir§7, vous pourrez vous retirer l'§ainvisibilité§7.",
                    "§7Tant que vous êtes§a Invisible§7 vous ne pourrez n'y§c recevoir de coup§7, n'y en§c infliger§7.",
                    "§7Tant que vous êtes§a invisible§7, tous les§c zetsu étant dans votre camp pourront voir des§c particules§7 à vos pieds."
            );
            this.cooldown = new Cooldown(60*5);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (cooldown.isInCooldown()) {
                    getRole().sendCooldown(player, cooldown.getCooldownRemaining());
                    return false;
                }
                if (this.invisible) {
                    this.runnable.timeLeft = 0;
                    return false;
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*60*5, 0, false, false), true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 1, false, false), true);

                if (player.getInventory().getHelmet() != null) {
                    armorContents.put(1, player.getInventory().getHelmet());
                    player.getInventory().setHelmet(null);
                }
                if (player.getInventory().getChestplate() != null) {
                    armorContents.put(2, player.getInventory().getChestplate());
                    player.getInventory().setChestplate(null);
                }
                if (player.getInventory().getLeggings() != null) {
                    armorContents.put(3, player.getInventory().getLeggings());
                    player.getInventory().setLeggings(null);
                }
                if (player.getInventory().getBoots() != null) {
                    armorContents.put(4, player.getInventory().getBoots());
                    player.getInventory().setBoots(null);
                }
                player.sendMessage("§aVous êtes maintenant invisible.");
                AttackUtils.CantAttack.add(player.getUniqueId());
                AttackUtils.CantReceveAttack.add(player.getUniqueId());
                runnable = new InvisibilitePower.InvisibiliteRunnable(this);
                return true;
            }
            return false;
        }

        private void removeInvisibility() {
            Player owner = Bukkit.getPlayer(getRole().getPlayer());
            if (owner == null)return;
            AttackUtils.CantAttack.remove(owner.getUniqueId());
            AttackUtils.CantReceveAttack.remove(owner.getUniqueId());
            owner.sendMessage("§cVous n'êtes plus invisible.");
            owner.removePotionEffect(PotionEffectType.INVISIBILITY);
            owner.removePotionEffect(PotionEffectType.SPEED);
            this.runnable.timeLeft = 0;
            if (armorContents.get(1) != null) {
                owner.getInventory().setHelmet(armorContents.get(1));
            }
            if (armorContents.get(2) != null) {
                owner.getInventory().setChestplate(armorContents.get(2));
            }
            if (armorContents.get(3) != null) {
                owner.getInventory().setLeggings(armorContents.get(3));
            }
            if (armorContents.get(4) != null) {
                owner.getInventory().setBoots(armorContents.get(4));
            }
            this.runnable.cancel();
            this.runnable = null;
            cooldown.use();
            getRole().getGamePlayer().getActionBarManager().removeInActionBar("zabuza.invisibilite");
        }

        private static class InvisibiliteRunnable extends BukkitRunnable {

            private final RoleBase zabuza;
            private int timeLeft = 60*5*20;
            private final GameState gameState;
            private final InvisibilitePower power;
            public InvisibiliteRunnable(InvisibilitePower power) {
                this.zabuza = power.getRole();
                this.gameState = this.zabuza.getGameState();
                this.power = power;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
                this.power.invisible = true;
                this.power.getRole().getGamePlayer().getActionBarManager().addToActionBar("zetsu.invisibilite", "§bTemp d'invisibilité:§c 60s");
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (timeLeft <= 0) {
                    this.power.removeInvisibility();
                    this.power.invisible = false;
                    return;
                }
                timeLeft--;
                int toshow = timeLeft/20;
                this.power.getRole().getGamePlayer().getActionBarManager().updateActionBar("zetsu.invisibilite", "§bTemp d'invisibilité:§c "+(StringUtils.secondsTowardsBeautiful((60*5)-toshow)));
                for (Player p : zabuza.getGamePlayer().getLastLocation().getWorld().getPlayers()) {
                    final GamePlayer gamePlayer = GamePlayer.of(p.getUniqueId());
                    if (gamePlayer == null)continue;
                    if (!gamePlayer.check())continue;
                    if (gamePlayer.getRole().getTeam().equals(this.zabuza.getTeam())) {
                        if (gamePlayer.getRole() instanceof ZetsuNoirV2 ||gamePlayer.getRole() instanceof ZetsuBlancV2) {
                            MathUtil.sendParticleTo(p, EnumParticle.CLOUD, zabuza.owner.getLocation().clone());
                        }
                    }
                }
            }
        }
    }
    private static class RegenPower extends Power {

        public RegenPower(@NonNull RoleBase role) {
            super("Pouvoir régénérant", null, role,
                    "§7Vous possédez une§c régénération naturel§7 à hauteur de§c 1/2❤§7 toute les§c 15 secondes");
            new RegenPower.RegenerationRunnable(role.getGameState(), role.getPlayer(), this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        private static class RegenerationRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final UUID uuid;
            private final RegenPower regenPower;
            private int timeLeft;

            private RegenerationRunnable(GameState gameState, UUID uuid, RegenPower regenPower) {
                this.gameState = gameState;
                this.uuid = uuid;
                this.regenPower = regenPower;
                runTaskTimerAsynchronously(regenPower.getPlugin(), 0, 20);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft == 0) {
                    final Player owner = Bukkit.getPlayer(this.uuid);
                    if (owner != null) {
                        if (this.regenPower.checkUse(owner, new HashMap<>())){
                            Bukkit.getScheduler().runTask(this.regenPower.getPlugin(), () -> owner.setHealth(Math.min(owner.getMaxHealth(), owner.getHealth()+1.0)));
                        }
                    }
                    this.timeLeft = 15;
                    return;
                }
                timeLeft--;
            }
        }
    }
}