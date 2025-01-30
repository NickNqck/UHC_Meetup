package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class RuiV2 extends DemonsRoles {

    public RuiV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.INFERIEUR;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Rui§7 (§6V2§7)";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Rui;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void resetCooldown() {}

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
        this.addPower(new FilPower(this), true);
    }
    private static class FilPower extends ItemPower {

        private Power equipedPower;
        private final Map<Integer, Power> powerMap;

        protected FilPower(@NonNull RuiV2 role) {
            super("Fils de Rui", new Cooldown(5), new ItemBuilder(Material.NETHER_STAR).setName("§cFils de Rui"), role,
                    "§7Vous permet d'utiliser vos fils, pour les changées il vous faudra effectuer un§n clique gauche§r§7:",
                    "",
                    "Attaque longue porté§7: En visant un §cjoueur§7, vous permet de lui infliger §c 2,5❤§7 ainsi que§c 15%§7 de chance de lui donner en plus§c 15 secondes§7 de§2 poison 1§7.",
                    "",
                    "Fil attractif§7: En visant un§c joueur§7, vous permet de l'attirer très fortement vers vous");
            this.powerMap = new LinkedHashMap<>();
            final LongAttackFilPower longAttackFilPower = new LongAttackFilPower(role);
            this.equipedPower = longAttackFilPower;
            final GrabPower grabPower = new GrabPower(role);
            role.addPower(grabPower);
            role.addPower(longAttackFilPower);
            this.powerMap.put(0, longAttackFilPower);
            this.powerMap.put(1, grabPower);
            setShowCdInHand(false);
            getShowCdRunnable().setCustomText(true);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("LEFT")) {//Pour changer de pouvoir
                    int power = getIntFromEquipedPower();
                    getShowCdRunnable().setCustomTexte("");
                    power++;
                    if (!this.powerMap.containsKey(power)) {
                        power = 0;
                    }
                    final Power futurePower = this.powerMap.get(power);
                    getShowCdRunnable().setCustomTexte((!futurePower.getCooldown().isInCooldown() ?
                                    "§c"+futurePower.getName()+" est§6 utilisable" :
                                    "§c"+futurePower.getName()+" est en cooldown (§b"+StringUtils.secondsTowardsBeautiful(futurePower.getCooldown().getCooldownRemaining())+"§c)"));
                    this.equipedPower = futurePower;
                } else if (event.getAction().name().contains("RIGHT")){
                    if (this.equipedPower == null) {
                        player.sendMessage("§cAucun pouvoir n'a été équiper.");
                        return false;
                    }
                    if (this.equipedPower.getCooldown().isInCooldown()) {
                        player.sendMessage("§c"+this.equipedPower.getName()+" est en cooldown:§b "+StringUtils.secondsTowardsBeautiful(this.equipedPower.getCooldown().getCooldownRemaining()));
                        return false;
                    }
                    return this.equipedPower.checkUse(player, map);
                }
            }
            return false;
        }

        @Override
        public void tryUpdateActionBar() {
            getShowCdRunnable().setCustomTexte((!this.equipedPower.getCooldown().isInCooldown() ?
                            "§c"+this.equipedPower.getName()+" est§6 utilisable" :
                            "§c"+this.equipedPower.getName()+" est en cooldown (§b"+StringUtils.secondsTowardsBeautiful(this.equipedPower.getCooldown().getCooldownRemaining())+"§c)"));
        }

        private Integer getIntFromEquipedPower() {
            if (this.equipedPower == null) {
                return 0;
            }
            for (final Integer integer : powerMap.keySet()) {
                if (powerMap.get(integer).equals(this.equipedPower)) {
                    return integer;
                }
            }
            return 0;
        }
        private static final class LongAttackFilPower extends Power {

            public LongAttackFilPower(@NonNull RuiV2 role) {
                super("Attaque longue porté", new Cooldown(60*7), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final Player target = RayTrace.getTargetPlayer(player, 25, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                if (target.getHealth() - 5.0 <= 0.0) {
                    target.setHealth(0.1);
                } else {
                    target.setHealth(target.getHealth()-5.0);
                }
                target.damage(0.0);
                if (Main.RANDOM.nextInt(100) <= 15) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*10, 0, false, false), true);
                    target.sendMessage("§7Vous avez été atteint par le§2 poison§7 de§c Rui§7 (§6V2§7)");
                    player.sendMessage("§7Votre§2 poison§7 à atteint§c "+target.getDisplayName());
                }
                player.sendMessage("§7Vous avez utiliser votre§c "+getName()+"§7 sur§c "+target.getDisplayName());
                target.sendMessage("§cRui§7 (§6V2§7)§c a utilisé son "+getName()+" sur vous");
                return true;
            }
        }
        private static final class GrabPower extends Power {

            public GrabPower(@NonNull RuiV2 role) {
                super("Fil attractif", new Cooldown(60*5), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(Player player, Map<String, Object> map) {
                final Player target = RayTrace.getTargetPlayer(player, 25, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                pullPlayerTowards(player, target);
                return false;
            }
            private void pullPlayerTowards(final @NonNull Player owner,final @NonNull Player target) {
                final Location locOwner = owner.getLocation();
                final Location locTarget = target.getLocation();

                final Vector direction = locOwner.toVector().subtract(locTarget.toVector()).normalize();
                direction.multiply(8.0);

                target.setVelocity(direction);
            }

        }
    }
}