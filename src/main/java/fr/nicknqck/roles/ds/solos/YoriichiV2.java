package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class YoriichiV2 extends DemonsSlayersRoles {

    public YoriichiV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.SOLEIL;
    }

    @Override
    public String getName() {
        return "Yoriichi§7 (§6V2§7)";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Yoriichi;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        addPower(new KillPower(this));
        setCanuseblade(true);
        setLameincassable(true);
        addPower(new DSSoleil(this));
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }
    private static class KillPower extends Power implements Listener {

        private final YoriichiV2 yoriichiV2;
        private int demonKills = 0;
        private int slayerKills = 0;

        public KillPower(@NonNull YoriichiV2 role) {
            super("Amélioration", null, role,
                    "§7Lorsque vous tuez un joueur vous obtiendrez un §cbonus§7 en fonction de son§c camp§7: ",
                    "",
                    "§aSlayer§7: ",
                    "",
                    "§7     →§c 1 kill§7: Vous obtiendrez l'effet§c résistance I§7 le§e jour",
                    "§7     →§c 2 kills§7: Vous obtiendrez§c régénération III§7 en mangeant une§e pomme d'or§7 (Vous ne vous soignerez quand même que de§c 2❤§7)",
                    "§7     →§c 3 kills§7: En mangeant une§e pomme d'or§7 les secondes de§c régénérations III§7 seront§c cumulé§7.",
                    "",
                    "§cDémons§7: ",
                    "",
                    "§7     →§c 1 kill§7: Vous obtiendrez une§c épée en diamant§7 enchantée§c tranchant IV",
                    "§7     →§c 2 kills§7: Vous obtiendrez§c 10% de force§7 supplémentaire la§c nuit",
                    "§7     →§c 3 kills§7: Vous obtiendrez§c 10 de force§7 supplémentaire le§e jour",
                    "",
                    "§eRole Solitaire§7: A chaque§c kill§7 vous obtiendrez§c 5%§7 de§c speed§7 supplémentaire",
                    "",
                    "§cTips:§7 Si vous arrivez à tué 3§a Slayers§7 et§c 3 Démons§7 vous obtiendrez l'effet§c résistance I§7 la §cnuit"
            );
            EventUtils.registerRoleEvent(this);
            this.yoriichiV2 = role;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void onKill(@NonNull final UHCPlayerKillEvent event) {
            if (event.getGamePlayerKiller() == null)return;
            if (event.getGamePlayerKiller().getRole() == null)return;
            if (!event.getKiller().getUniqueId().equals(this.getRole().getPlayer()))return;
            if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;
            final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();
            if (role instanceof DemonsRoles) {
                if (this.demonKills == 0) {
                    this.yoriichiV2.giveItem(event.getPlayerKiller(), false, new ItemBuilder(Material.DIAMOND_SWORD).setName("§bÉpée de§e Yoriichi").addEnchant(Enchantment.DAMAGE_ALL, 4).toItemStack());
                    event.getKiller().sendMessage("§7Vous avez obtenue votre§c épée en diamant");
                }
                this.demonKills++;
            } else if (role instanceof SlayerRoles) {
                if (this.slayerKills == 0) {
                    this.yoriichiV2.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), EffectWhen.DAY);
                event.getKiller().sendMessage("§7Vous avez obtenue l'effet§c résistance 1§7 le§e jour");
                }
                this.slayerKills++;
            } else if (role.getOriginTeam().equals(TeamList.Solo) || role.getOriginTeam().equals(TeamList.Jubi) || role.getOriginTeam().equals(TeamList.Zabuza_et_Haku)) {
                this.yoriichiV2.addSpeedAtInt(event.getPlayerKiller(), 5.0f);
                event.getKiller().sendMessage("§7Vous avez obtenue§c 5% de speed");
            }
            if (this.demonKills == 3 && this.slayerKills == 3) {
                this.yoriichiV2.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), EffectWhen.NIGHT);
                event.getKiller().sendMessage("§7Vous avez maintenant§c résistance 1 permanent");
            }
        }
        @EventHandler
        private void onConsume(@NonNull final PlayerItemConsumeEvent event) {
            if (event.getPlayer().getUniqueId().equals(this.yoriichiV2.getPlayer())) {
                if (this.slayerKills >= 2) {
                    final ItemStack item = event.getItem();
                    if (!item.getType().equals(Material.GOLDEN_APPLE))return;
                    if (!checkUse(event.getPlayer(), new HashMap<>()))return;
                    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                        int toAdd = 0;
                        if (this.slayerKills >= 3) {
                            for (final PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()){
                                if (potionEffect.getType().equals(PotionEffectType.REGENERATION)) {
                                    toAdd += potionEffect.getDuration();
                                    break;
                                }
                            }
                        }
                        this.yoriichiV2.givePotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 50+toAdd, 2, false, false), EffectWhen.NOW);
                        this.yoriichiV2.givePotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20*60*2, 0, false, false), EffectWhen.NOW);
                    },1);
                }
            }
        }
        @EventHandler
        private void UHCBattleEvent(@NonNull final UHCPlayerBattleEvent event) {
            if (!event.isPatch())return;
            if (!event.getDamager().getUuid().equals(this.yoriichiV2.getPlayer()))return;
            if (this.demonKills < 2)return;
            if (this.yoriichiV2.getGameState().isNightTime()) {
                event.setDamage(event.getDamage()*1.1);//+10%
            } else {
                if (this.demonKills < 3)return;
                event.setDamage(event.getDamage()*1.1);//+10%
            }
        }
    }
    private static class DSSoleil extends CommandPower implements Listener {

        private final Map<UUID, Long> inSoleil = new HashMap<>();
        private boolean active = false;

        public DSSoleil(@NonNull RoleBase role) {
            super("/ds souffle", "souffle", null, role, CommandType.DS,
                    "§7Vous permet d'§aactiver§7/§cdésactiver§7 votre§e souffle du Soleil§7",
                    "",
                    "§7Lorsqu'il est§a activer§7, les joueurs que vous frappez se§c régénèreront§7 plus§c lentement§7 et ne recevrons qu'§e1❤ d'absorption§7",
                    "§7pendant§c 10 secondes§7.");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (!active) {
                active = true;
                player.sendMessage("§7Vous avez§a activé§7 votre§e souffle du Soleil§7.");
            } else {
                active = false;
                player.sendMessage("§7Vous avez§c désactiver§7 voter§e souffle du Soleil§7.");
            }
            return true;
        }
        @EventHandler
        private void onDamage(final EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Player))return;
            if (!(event.getEntity() instanceof Player))return;
            if (!event.getDamager().getUniqueId().equals(getRole().getPlayer()))return;
            if (!active)return;
            final Long currentTime = System.currentTimeMillis();
            inSoleil.put(event.getEntity().getUniqueId(), currentTime);
            event.getEntity().sendMessage("§eYoriichi§7 a§c limité§7 votre§c capacité§7 à vous§c régénérer§7 avec des§e pommes d'or§7.");
            event.getDamager().sendMessage("§7Vous avez§c limité§7 la§c capacité§7 à se§c régénérer§7 avec des§e pommes d'or§7 à§c "+((Player) event.getEntity()).getDisplayName()+"§7.");
        }
        @EventHandler
        private void onEat(final PlayerItemConsumeEvent event) {
            if (this.inSoleil.containsKey(event.getPlayer().getUniqueId())) {
                final Long currentTime = System.currentTimeMillis();
                if (inSoleil.get(event.getPlayer().getUniqueId()) - currentTime <= 10000) {
                    final Player player = event.getPlayer();
                    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*6, 0, false, false), true);
                        ((CraftPlayer) player).getHandle().setAbsorptionHearts(2f);
                    }, 1);
                }
            }
        }
    }
}