package fr.nicknqck.roles.ns.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.EffectWhen;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.enums.TeamList;
import fr.nicknqck.events.custom.EffectGiveEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.interfaces.ITeam;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.EChakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.NSSoloRoles;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KabutoSolo extends NSSoloRoles {

    public KabutoSolo(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return EChakras.values();
    }

    @Override
    public String getName() {
        return "Kabuto§7 (§eSolo§7)§r";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.KabutoSolo;
    }

    @Override
    public @NonNull ITeam getOriginTeam() {
        return TeamList.Kabuto;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        addPower(new ErmitePower(this), true);
        addPower(new EdoTenseiPower(this), true);
        addPower(new MueDuSerpent(this));
        giveHealedHeartatInt(2.0);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    private static final class ErmitePower extends ItemPower {

        public ErmitePower(@NonNull RoleBase role) {
            super("§aMode Ermite§r", new Cooldown(60*10), new ItemBuilder(Material.FERMENTED_SPIDER_EYE).setName("§aMode Ermite"), role,
                    "§7Pendant§c 3 minutes§7, vous aurez l'effet§c Force I§7, également, des§f particules blanche§7",
                    "§7vous relieront aux§c joueurs§7 étant à§c moins§7 de§c 45 blocs§7.",
                    "",
                    "§7La§a couleur§7 des§c particules§7 changera en fonction du§c camp§7 de la personne",
                    "§7si vous êtes à moins de§c 10 blocs§7 d'elle.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            player.sendMessage("§7Vous avez§a activer§7 votre§a Mode Ermite§7.");
            new ErmiteRunnable(this).runTaskTimerAsynchronously(getPlugin(), 0, 20);
            return true;
        }
        private static final class ErmiteRunnable extends BukkitRunnable {

            private final ErmitePower ermitePower;
            private int timeLeft = 60*3;

            private ErmiteRunnable(ErmitePower ermitePower) {
                this.ermitePower = ermitePower;
            }

            @Override
            public void run() {
                if (!GameState.inGame()) {
                    cancel();
                    return;
                }
                final Player player = Bukkit.getPlayer(this.ermitePower.getRole().getPlayer());
                if (player == null)return;
                if (this.timeLeft <= 0) {
                    this.ermitePower.getRole().getGamePlayer().getActionBarManager().removeInActionBar("kabuto.ermite");
                    cancel();
                    return;
                }
                final List<GamePlayer> gamePlayerList = new ArrayList<>(Loc.getNearbyGamePlayers(this.ermitePower.getRole().getGamePlayer().getLastLocation(), 45.0));
                for (GamePlayer gamePlayer : gamePlayerList) {
                    if (!gamePlayer.check())continue;
                    if (this.ermitePower.getRole().getPlayer().equals(gamePlayer.getUuid()))continue;
                    final double distance = player.getLocation().distance(gamePlayer.getLastLocation());
                    if (distance > 10) {
                        for (Location location : MathUtil.getLine(player.getLocation(), gamePlayer.getLastLocation(), 45)) {
                            MathUtil.spawnColoredParticle(player, location, EnumParticle.REDSTONE, 255, 255,255);
                        }
                    } else {
                        for (Location location : MathUtil.getLine(player.getLocation(), gamePlayer.getLastLocation(), (int) distance)) {
                            final Color color = getRGBFromMinecraftColor(gamePlayer.getRole().getTeam().getColor());
                            MathUtil.spawnColoredParticle(player, location, EnumParticle.REDSTONE, color.getRed(), color.getGreen(), color.getBlue());
                        }
                    }
                }

                this.ermitePower.getRole().getGamePlayer().getActionBarManager().updateActionBar("kabuto.ermite", "§bTemps restant (§aMode Ermite§b):§c "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                this.timeLeft--;
                Bukkit.getScheduler().runTask(this.ermitePower.getPlugin(), () -> this.ermitePower.getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NOW));
            }
            public static Color getRGBFromMinecraftColor(String code) {
                if (code == null || code.length() < 2) {
                    return Color.fromRGB(255, 255, 255);
                }

                // On cherche le caractère après le §
                int index = code.indexOf('§');
                if (index == -1 || index + 1 >= code.length()) {
                    return Color.fromRGB(255, 255, 255);
                }

                char c = Character.toLowerCase(code.charAt(index + 1));

                switch (c) {
                    case '0': return Color.fromRGB(0, 0, 0);
                    case '1': return Color.fromRGB(0, 0, 170);
                    case '2': return Color.fromRGB(0, 170, 0);
                    case '3': return Color.fromRGB(0, 170, 170);
                    case '4': return Color.fromRGB(170, 0, 0);
                    case '5': return Color.fromRGB(170, 0, 170);
                    case '6': return Color.fromRGB(255, 170, 0);
                    case '7': return Color.fromRGB(170, 170, 170);
                    case '8': return Color.fromRGB(85, 85, 85);
                    case '9': return Color.fromRGB(85, 85, 255);
                    case 'a': return Color.fromRGB(85, 255, 85);
                    case 'b': return Color.fromRGB(85, 255, 255);
                    case 'c': return Color.fromRGB(255, 85, 85);
                    case 'd': return Color.fromRGB(255, 85, 255);
                    case 'e': return Color.fromRGB(255, 255, 85);
                    default:  return Color.fromRGB(255, 255, 255);
                }
            }


        }
    }
    private static final class EdoTenseiPower extends ItemPower implements Listener {

        private final Map<UUID, RoleBase> edoTenseis;
        private final Map<UUID, Location> killLocation;
        @Setter
        private boolean canEdoTensei = true;

        public EdoTenseiPower(@NonNull RoleBase role) {
            super("Edo Tensei", null, new ItemBuilder(Material.NETHER_STAR).setName("§5Edo Tensei"), role,
                    "§7Quand vous tuez un joueur, vous pourrez le ressusciter en échange de§c "+ (MathUtil.get34(Main.getInstance().getGameConfig().getNarutoConfig().getEdoHealthRemove()))+"1/2❤");
            EventUtils.registerRoleEvent(this);
            this.edoTenseis = new LinkedHashMap<>();
            this.killLocation = new LinkedHashMap<>();
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (!this.edoTenseis.isEmpty()) {
                    player.sendMessage("§7Vous avez déjà un§5 Edo Tensei§7 en§a vie");
                    event.setCancelled(true);
                    return false;
                }
                if (this.killLocation.isEmpty()) {
                    player.sendMessage("§7Il faut avoir tuer un joueur pour utiliser cette technique.");
                    event.setCancelled(true);
                    return false;
                }
                if (!canEdoTensei) {
                    player.sendMessage("§cVous ne pouvez pas utiliser ce pouvoir !");
                    event.setCancelled(true);
                    return false;
                }
                final PaginatedFastInv fastInv = new PaginatedFastInv(27, "§6Edo Tensei");
                fastInv.setItems(fastInv.getBorders(), new ItemBuilder(Material.ANVIL).toItemStack());
                for (UUID uuid : this.killLocation.keySet()) {
                    final Player target = Bukkit.getPlayer(uuid);
                    if (target == null)continue;
                    if (!target.getWorld().equals(player.getWorld()))continue;
                    final double distance = target.getLocation().distance(player.getLocation());
                    if (distance <= 65) {
                        fastInv.addContent(new ItemBuilder(GlobalUtils.getPlayerHead(uuid))
                                .setName("§a"+target.getName())
                                .setLore("§7Cliquez ici pour réssusciter ce joueur !\n\n/7Coût: §c"+
                                (MathUtil.get34(Main.getInstance().getGameConfig().getNarutoConfig().getEdoHealthRemove()))
                                +"❤ permanent")
                                .toItemStack(),
                                event1 -> {
                            event1.getWhoClicked().closeInventory();
                            this.revive(getRole().getGameState(), target, player);
                        });
                    }
                }
                fastInv.setItems(fastInv.getBorders(), new ItemBuilder(Material.AIR).toItemStack());
                fastInv.setItems(fastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setName(" ").toItemStack());
                fastInv.open(player);
                event.setCancelled(true);
            }
            return false;
        }

        @EventHandler
        private void onUHCDeath(UHCDeathEvent event) {
            if (edoTenseis.containsKey(event.getPlayer().getUniqueId())) {
                edoTenseis.remove(event.getPlayer().getUniqueId(), event.getRole());
            }
        }
        @EventHandler
        private void onUHCKill(UHCPlayerKillEvent event){
            if (event.getPlayerKiller() != null) {
                if (event.getPlayerKiller().getUniqueId().equals(this.getRole().getPlayer())) {
                    this.killLocation.put(event.getVictim().getUniqueId(), event.getVictim().getLocation());
                }
            }
        }

        private void revive(final GameState gameState, final Player clicked, final Player owner) {
            List<Double> cercles = Arrays.asList(1.0, 2.5, 5.0, 7.5);
            new InwardCircleAnimation(clicked.getLocation(), cercles, 70, 139, 0, 139)
                    .runTaskTimer(getPlugin(), 0, 1);
            RoleBase role = gameState.getGamePlayer().get(clicked.getUniqueId()).getRole();
            edoTenseis.put(clicked.getUniqueId(), role);
            owner.closeInventory();
            clicked.sendMessage("§7Vous avez été invoquée par l'§5Edo Tensei");
            owner.sendMessage("§5Edo Tensei !");
            role.setTeam(this.getRole().getTeam());
            role.setMaxHealth(20.0);
            clicked.getInventory().setContents(role.getGamePlayer().getLastInventoryContent());
            clicked.getInventory().setArmorContents(role.getGamePlayer().getLastArmorContent());
            gameState.RevivePlayer(clicked);
            this.getRole().setMaxHealth(this.getRole().getMaxHealth()-Main.getInstance().getGameConfig().getNarutoConfig().getEdoHealthRemove());
            owner.setMaxHealth(this.getRole().getMaxHealth());
            clicked.teleport(owner);
            final List<Power> copyPower = new ArrayList<>(role.getPowers());
            if (!copyPower.isEmpty()) {
                for (Power power : copyPower) {
                    if (power instanceof ItemPower) {
                        clicked.getInventory().removeItem(((ItemPower) power).getItem());
                    }
                    role.removePower(power);
                }
            }
            role.GiveItems();
            role.RoleGiven(this.getRole().gameState);
            this.killLocation.remove(clicked.getUniqueId());
            clicked.resetTitle();
            clicked.sendTitle("§5Edo Tensei !", "Vous êtes maintenant dans le camp "+this.getRole().getTeam().getName());
        }
        private static class InwardCircleAnimation extends BukkitRunnable {

            private final Location center;
            private final List<Double> radii;
            private final double speedPerTick; // La vitesse de réduction par tick

            // Configuration des particules (supporte EnumParticle NMS ou RGB)
            private final EnumParticle particle;
            private final boolean isRgb;
            private final float r, g, b;

            private int currentIndex;

            /**
             * Constructeur pour une particule NMS classique (ex: FLAME, REDSTONE)
             */
            public InwardCircleAnimation(Location center, List<Double> startingRadii, int durationInTicks, EnumParticle particle) {
                this.center = center;
                this.particle = particle;
                this.isRgb = false;
                this.r = 0; this.g = 0; this.b = 0;

                this.radii = setupRadii(startingRadii);
                this.currentIndex = this.radii.size() - 1;
                this.speedPerTick = calculateSpeed(durationInTicks);
            }

            /**
             * Constructeur pour des particules RGB (COLOURED_DUST)
             */
            public InwardCircleAnimation(Location center, List<Double> startingRadii, int durationInTicks, float r, float g, float b) {
                this.center = center;
                this.particle = null;
                this.isRgb = true;
                this.r = r; this.g = g; this.b = b;

                this.radii = setupRadii(startingRadii);
                this.currentIndex = this.radii.size() - 1;
                this.speedPerTick = calculateSpeed(durationInTicks);
            }

            // Prépare la liste des rayons en ajoutant "0.0" au centre pour que le dernier cercle disparaisse aussi
            private List<Double> setupRadii(List<Double> startingRadii) {
                List<Double> list = new ArrayList<>();
                list.add(0.0); // Le centre absolu
                list.addAll(startingRadii);
                Collections.sort(list); // On s'assure que c'est bien trié du plus petit au plus grand
                return list;
            }

            // Calcule de combien de blocs le cercle doit se réduire par tick pour respecter la durée
            private double calculateSpeed(int durationInTicks) {
                double totalDistance = 0;
                for (int i = 1; i < radii.size(); i++) {
                    totalDistance += (radii.get(i) - radii.get(i - 1));
                }
                return durationInTicks > 0 ? totalDistance / durationInTicks : totalDistance;
            }

            @Override
            public void run() {
                if (currentIndex <= 0) {
                    // L'animation est terminée, tout est arrivé au centre
                    this.cancel();
                    return;
                }

                double currentRadius = radii.get(currentIndex);
                double targetRadius = radii.get(currentIndex - 1);

                // On réduit le rayon du cercle extérieur
                currentRadius -= speedPerTick;

                if (currentRadius <= targetRadius) {
                    // Le cercle extérieur a touché le cercle intérieur : il fusionne/disparaît
                    radii.remove(currentIndex);
                    currentIndex--;
                } else {
                    // Sinon, on met à jour sa nouvelle taille
                    radii.set(currentIndex, currentRadius);
                }

                // On redessine tous les cercles restants à leur position actuelle
                for (int i = 1; i < radii.size(); i++) {
                    drawSmoothCircle(radii.get(i));
                }
            }

            private void drawSmoothCircle(double radius) {
                // Densité de particules : 15 particules par bloc de rayon (comme dans ta méthode getCircle)
                int amount = (int) (radius * 15);
                if (amount < 5) amount = 5; // Sécurité pour les tout petits cercles

                double increment = (2 * Math.PI) / amount;

                for (int i = 0; i < amount; i++) {
                    double angle = i * increment;
                    double x = center.getX() + radius * Math.cos(angle);
                    double z = center.getZ() + radius * Math.sin(angle);

                    if (isRgb) {
                        // Utilisation de ta méthode MathUtil pour le RGB
                        Location loc = new Location(center.getWorld(), x, center.getY() + 0.1, z);
                        MathUtil.spawnParticle(loc, r, g, b);
                    } else {
                        // Utilisation de ta méthode MathUtil pour les particules NMS
                        MathUtil.sendParticle(particle, x, center.getY() + 0.1, z, center.getWorld());
                    }
                }
            }
        }
    }
    private static final class MueDuSerpent extends CommandPower implements Listener{

        private long lastTimeCurrent = 0;

        public MueDuSerpent(@NonNull RoleBase role) {
            super("/ns mue", "mue", new Cooldown(60*10), role, CommandType.NS,
                    "§7Vous permet de§a muer§7 votre corp vers un autre, autrement dit, cela vous téléportera à§c 50 blocs§7.",
                    "",
                    "§7Après la§c téléportation§7 vous obtiendrez§c 30 secondes§7 de§e Speed II§7."
            );
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            player.sendMessage("§7La§a Mue du serpent§7 vous a donnez un§c nouveau corp§7.");
            final Location loc = Loc.getRandomLocationAroundPlayer(player, 25);
            player.teleport(loc);
            getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*30, 1, false, false), EffectWhen.NOW);
            lastTimeCurrent = System.currentTimeMillis();
            return true;
        }
        @EventHandler
        private void onEffect(@NonNull final EffectGiveEvent event) {
            if (event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                if (event.getEffectWhen().equals(EffectWhen.PERMANENT) && event.getPotionEffect().getType().equals(PotionEffectType.SPEED)) {
                    if (System.currentTimeMillis() - lastTimeCurrent <= 30_000) {//30s
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}