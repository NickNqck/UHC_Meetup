package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.UpdatablePowerLore;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.FinalDeathEvent;
import fr.nicknqck.events.custom.GamePlayerEatGappleEvent;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GyutaroV2 extends DemonsRoles implements Listener {

    private DakiV2 daki;
    private PassifCommandPower passifCommandPower;

    public GyutaroV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.SUPERIEUR;
    }

    @Override
    public String getName() {
        return "Gyutaro";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Gyutaro;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new RappelPower(this), true);
        PassifCommandPower passifCommandPower = new PassifCommandPower(this);
        addPower(passifCommandPower);
        this.passifCommandPower = passifCommandPower;
        EventUtils.registerRoleEvent(this);
        getGamePlayer().addItems(passifCommandPower.FauxItem);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
        addKnowedRole(DakiV2.class);
        addKnowedRole(MuzanV2.class);
        super.RoleGiven(gameState);
    }
    @EventHandler
    private void onEndGiveRole(final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        final List<GamePlayer> dakiPlayers = new ArrayList<>(getListGamePlayerFromRoles(Roles.Daki));
        if (!dakiPlayers.isEmpty()) {
            Collections.shuffle(dakiPlayers, Main.RANDOM);
            this.daki = (DakiV2) dakiPlayers.get(0).getRole();
        }
        if (this.daki == null && this.passifCommandPower != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> this.passifCommandPower.onDakiDeath(), 20);
        }
    }
    @EventHandler
    private void onDeath(final FinalDeathEvent event) {
        if (event.getRole() == null)return;
        if (event.getRole() instanceof DakiV2) {
            if (this.daki == null && this.passifCommandPower != null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> this.passifCommandPower.onDakiDeath(), 20);
            }
        }
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .getText();
    }

    private static class RappelPower extends ItemPower {

        private final GyutaroV2 gyutaroV2;

        protected RappelPower(@NonNull GyutaroV2 role) {
            super("Rappel", new Cooldown(60 * 10), new ItemBuilder(Material.NETHER_STAR).setName("§cRappel"), role,
                    "§7Vous permez de vous téléporter à §cDaki §7si elle est présente dans un rayon de §c50 blocs§7 autour de vous et qu'elle possède moins de§c 7❤§7.",
                    "",
                    "§7A la mort de§c Daki§7, le§c Rappel§7 deviendra le§c Troisième Oeil§7 (donne§e Speed I§7 pendant§c 3 minutes§7).");
            this.gyutaroV2 = role;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            if (gyutaroV2.daki == null) {
                player.sendMessage("§cDaki§7 n'est pas dans la partie...");
                return false;
            }
            Player daki = Bukkit.getPlayer(gyutaroV2.daki.getPlayer());
            if (daki == null) {
                player.sendMessage("§cDaki n'est pas connecter");
                return false;
            }
            if (!player.getWorld().equals(daki.getWorld())) {
                player.sendMessage("§cDaki§7 ne peut pas être téléportée pour l'instant.");
                return false;
            }
            final double distance = player.getLocation().distance(daki.getLocation());
            if (distance <= 50.0) {
                Location loc = Loc.getRandomLocationAroundPlayer(player, 5);
                daki.teleport(loc);
                player.sendMessage("§7Vous venez de Téléportez §cDaki §7à vous.");
                daki.sendMessage("§cGyutaro §7vous téléportez sur lui.");
            }
            return true;
        }
    }
    private static class PassifCommandPower extends CommandPower implements Listener, UpdatablePowerLore {

        private boolean activate = false;
        private int pourcentage = 5;
        private ItemStack FauxItem = new ItemBuilder(Material.DIAMOND_SWORD).setName("§cFaux Démoniaques")
                .addEnchant(Enchantment.DAMAGE_ALL, 3)
                .setUnbreakable(true)
                .setLore("§7Le§c pourcentage§7 de votre§c passif§7 est actuellement de§c "+pourcentage+"%")
                .setDroppable(false)
                .toItemStack();
        private int amountEateds = 0;

        public PassifCommandPower(@NonNull RoleBase role) {
            super("/ds faux", "faux", null, role, CommandType.DS,
                    "§7Vous permet d'§aactiver§7/§cdésactiver§7 votre§c passif§7 (§cDésactiver par défauts§7)",
                    "§7Il vous permettra d'avoir§c 5%§7 de§c chance§7 d'infliger l'effet§c Wither I§7 à la personne que vous",
                    "§7frappez avec votre épée \"§cFaux Démoniaques§7\", l'effet aura une durée de§c 5 secondes§7.",
                    "",
                    "§7Pour chaque§c 10§e pommes d'or§c mangé§7, le§c pourcentage§7 de chance d'infliger l'effet§c Wither I§7 sera augmenter de§c 1%§7 (§cmaximum 15%§7)");
            EventUtils.registerRoleEvent(this);
        }
        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            if (!activate) {
                activate = true;
                player.sendMessage("§7Vous venez d'§aactiver§7 votre§c passif§7.");
            } else {
                if (args.containsKey("passif")) {
                    if (args.get("passif") instanceof RoleBase) {
                        return RandomUtils.getOwnRandomProbability(this.pourcentage);
                    }
                }
                activate = false;
                player.sendMessage("§7Vous venez de§c désactiver§7 votre§c passif§7.");
            }
            return true;
        }
        @EventHandler
        private void onBaston(final EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Player))return;
            if (event.getDamager().getUniqueId().equals(getRole().getPlayer())) {
                final Player owner = (Player) event.getDamager();
                if (owner.getItemInHand() == null)return;
                if (owner.getItemInHand().getType().equals(Material.AIR))return;
                if (!owner.getItemInHand().isSimilar(this.FauxItem))return;
                if (!activate)return;
                final HashMap<String, Object> map = new HashMap<>();
                map.put("passif", getRole());
                if (checkUse((Player) event.getDamager(), map)) {
                    final GamePlayer gamePlayer = GamePlayer.of(event.getEntity().getUniqueId());
                    boolean give = false;
                    if (gamePlayer != null) {
                        if (gamePlayer.getRole() != null) {
                            gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.WITHER, 20*5, 0, false, false), EffectWhen.NOW);
                            give = true;
                        }
                    }
                    if (!give && event.getEntity() instanceof LivingEntity) {
                        ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20*5, 0, false, false), true);
                    }
                    event.getEntity().sendMessage("§7Vous avez été§c empoisonné§7 par§c Gyutaro§7.");
                    event.getDamager().sendMessage("§7Vous avez§c empoisonné§7 le joueur§c "+event.getEntity().getName());
                }
            }
        }
        @EventHandler
        private void onEat(final GamePlayerEatGappleEvent event) {
            if (!event.getGamePlayer().getUuid().equals(getRole().getPlayer())) return;
            this.amountEateds++;
            if (this.amountEateds >= 10) {
                this.amountEateds = 0;
                if (this.pourcentage < 15) {
                    event.getPlayer().sendMessage("§7Vous sentez que votre§c passif§7 c'est§c renforcé§7.");
                    this.pourcentage++;
                    reCreateFaux(event.getPlayer());
                }
            }
        }
        private void reCreateFaux(final Player player) {
            int slot = -1;
            ItemStack[] contents = player.getInventory().getContents();

            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (item == null || item.getType() == Material.AIR) continue;
                if (item.isSimilar(this.FauxItem)) {
                    slot = i;
                    break;
                }
            }

            if (slot == -1) return; // Aucun item trouvé, on ne fait rien

            this.FauxItem = new ItemBuilder(Material.DIAMOND_SWORD)
                    .setName("§cFaux Démoniaques")
                    .addEnchant(Enchantment.DAMAGE_ALL, 3)
                    .setUnbreakable(true)
                    .setLore("§7Le§c pourcentage§7 de votre§c passif§7 est actuellement de§c "+pourcentage+"%")
                    .setDroppable(false)
                    .toItemStack();

            player.getInventory().setItem(slot, this.FauxItem);
        }
        private void onDakiDeath() {
            getRole().getGamePlayer().sendMessage("§cDaki§7 est§c morte§7, malgré cette perte, vous vous sentez plus§c fort§7, votre§c passif§7 c'est§c renforcé§7.");
            this.pourcentage+=5;
            reCreateFaux(Bukkit.getPlayer(getRole().getPlayer()));
            final List<Power> powerList = new ArrayList<>(this.getRole().getPowers());
            for (Power power : powerList) {
                if (!(power instanceof RappelPower))continue;
                this.getRole().getPowers().remove(power);
                getRole().getGamePlayer().removeItem(((RappelPower) power).getItem());
                break;
            }
            getRole().addPower(new TroisiemeOeil(this.getRole()), true);
        }

        @Override
        public String[] getCustomPowerLore() {
            return new String[] {
                    "§7Vous permet d'§aactiver§7/§cdésactiver§7 votre§c passif§7 (§cDésactiver par défauts§7)",
                    "§7Il vous permettra d'avoir§c "+this.pourcentage+"%§7 de§c chance§7 d'infliger l'effet§c Wither I§7 à la personne que vous",
                    "§7frappez avec votre épée \"§cFaux Démoniaques§7\", l'effet aura une durée de§c 5 secondes§7.",
                    "",
                    "§7Pour chaque§c 10§e pommes d'or§c mangé§7, le§c pourcentage§7 de chance d'infliger l'effet§c Wither I§7 sera augmenter de§c 1%§7 (§cmaximum 15%§7)"
            };
        }
        private static class TroisiemeOeil extends ItemPower {

            public TroisiemeOeil(@NonNull RoleBase role) {
                super("Troisième Oeil", new Cooldown(60*8), new ItemBuilder(Material.NETHER_STAR).setName("§aTroisième Oeil"), role,
                        "§7A l'activation, durant§c 3 minutes§7 vous obtenez l'effet§e Speed I§7.");
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                if (getInteractType().equals(InteractType.INTERACT)) {
                    player.sendMessage("§7Votre§a Troisième Oeil§7 s'éveille...");
                    new TroisiemeOeil.SpeedRunnable(this).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                    return true;
                }
                return false;
            }

            private static class SpeedRunnable extends BukkitRunnable {

                private final TroisiemeOeil troisiemeOeil;
                private int timeLeft = 60*3;

                private SpeedRunnable(TroisiemeOeil troisiemeOeil) {
                    this.troisiemeOeil = troisiemeOeil;
                }

                @Override
                public void run() {
                    if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                        cancel();
                        return;
                    }
                    if (this.timeLeft <= 0) {
                        this.troisiemeOeil.getRole().getGamePlayer().getActionBarManager().removeInActionBar("dakiv2.oeil");
                        this.troisiemeOeil.getRole().getGamePlayer().sendMessage("§7Les effets de votre§a Troisième Oeil§7 s'estompent...");
                        cancel();
                        return;
                    }
                    this.troisiemeOeil.getRole().getGamePlayer().getActionBarManager().updateActionBar("dakiv2.oeil", "§bTemps restant (§aTroisième Oeil§b):§c "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                    this.timeLeft--;
                    final Player owner = Bukkit.getPlayer(this.troisiemeOeil.getRole().getPlayer());
                    if (owner == null)return;
                    Bukkit.getScheduler().runTask(this.troisiemeOeil.getPlugin(), () -> this.troisiemeOeil.getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.NOW));
                }
            }
        }
    }
}