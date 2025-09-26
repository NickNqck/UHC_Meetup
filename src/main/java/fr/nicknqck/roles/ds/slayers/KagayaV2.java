package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.IRole;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.slayers.pillier.PilierRoles;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.packets.ArmorStandUtils;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class KagayaV2 extends SlayerRoles {

    private MaladieRunnable maladieRunnable;

    public KagayaV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.AUCUN;
    }

    @Override
    public String getName() {
        return "Kagaya";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Kagaya;
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .addCustomLine("§7Vous possédez§c 1❤ permanent§7 supplémentaire")
                .setPowers(getPowers())
                .addCustomLines(new String[]{
                        "§7Toute les§c "+(this.maladieRunnable == null ? "5 minutes" : StringUtils.secondsTowardsBeautiful(this.maladieRunnable.maxTime)+"inutes")+"§7 vous passez un§c stade§7, à chaque fois que vous en passez un vous perdez§c 1/2❤ permanent§7.",
                        "",
                        AllDesc.point+"§7Lorsque que vous atteignez le§c sixième stade§7 vous obtenez la capacité de voir la§a vie§7 des autres§c joueurs§7, aussi quand vous parviendrez au§c huitième stade§7 vous obtiendrez la commande§6 /ds pilier <joueur>§7, elle vous permettra de "
                        +"§7savoir si§a oui§7 ou§c non§7 la personne est un§a pilier§7.",
                        "",
                        AllDesc.point+"§7Votre§2 maladie§7 s'arrêtera au§c quatorzième stade§7, après cela vous garderez vos pouvoirs."
                })
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        this.maladieRunnable = new MaladieRunnable(this);
       // addPower(new MaladieItem(this), true);
        addPower(new PredictionCommand(this));
        setMaxHealth(getMaxHealth()+2);
        owner.setMaxHealth(getMaxHealth());
        owner.setHealth(owner.getMaxHealth());
    }

    private static class PredictionCommand extends CommandPower implements Listener{

        private final KagayaV2 kagaya;
        private final Map<UUID, String> predictings;

        public PredictionCommand(@NonNull KagayaV2 role) {
            super("/ds prediction <joueur> <role>", "prediction", new Cooldown(5), role, CommandType.DS,
                    "§7Vous permet d'essayer de savoir si un joueur est un rôle ou pas",
                    "",
                    "§7A sa mort, s'il était réellement le rôle que vous pensiez vous pourrez choisir un bonus ci-dessous:",
                    "",
                    "§8 -§a Gagner§c 1/2❤ permanent§7.",
                    "",
                    "§8 -§a Réinitialiser le timer de votre§c perte de coeur");
            this.kagaya = role;
            this.predictings = new HashMap<>();
            EventUtils.registerRoleEvent(this);
            setWorkWhenInCooldown(true);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            String[] args = (String[]) map.get("args");
            if (args.length == 3) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (getCooldown().isInCooldown()) {
                        getRole().sendCooldown(player, getCooldown().getCooldownRemaining());
                        return false;
                    }
                    if (this.predictings.containsKey(target.getUniqueId())) {
                        player.sendMessage("§7Vous essayez déjà de savoir si cette personne est:§c "+this.predictings.get(target.getUniqueId()));
                        return false;
                    }
                    final List<String> test = new ArrayList<>();
                    for (IRole iRole : getPlugin().getRoleManager().getRolesRegistery().values()) {
                        test.add(iRole.getName());
                    }
                    for (String string : test) {
                        if (string.equalsIgnoreCase(args[2])) {
                            this.predictings.put(target.getUniqueId(), string);
                            player.sendMessage("§7Essayons de voir si§c "+target.getName()+"§7 est§c "+string);
                            break;
                        }
                    }
                    return true;
                } else {
                    if (args[1].length() == UUID.randomUUID().toString().length()) {
                        final UUID uuid = UUID.fromString(args[1]);
                        if (this.predictings.containsKey(uuid)) {
                            if (args[2].equals("coeur")) {
                                getRole().setMaxHealth(getRole().getMaxHealth()+1);
                                player.setMaxHealth(getRole().getMaxHealth());
                                player.sendMessage("§7Vous avez récupérer un petit peut d'§aénergie vitale§7.");
                                this.predictings.remove(UUID.fromString(args[1]));
                                return true;
                            } else if (args[2].equals("time")) {
                                this.predictings.remove(UUID.fromString(args[1]));
                                this.kagaya.maladieRunnable.actualTime = 0;
                                player.sendMessage("§7Le temp avant l'expension de votre maladie à été réduit.");
                                return true;
                            }
                        }

                    }
                    player.sendMessage("§b"+args[1]+"§c n'est pas connecté(e) !");
                    return false;
                }
            }
            player.sendMessage("§cCette commande prend un§b joueur§c et un§b rôle§c en compte");
            return false;
        }
        @EventHandler
        private void onDeath(UHCDeathEvent event) {
            if (event.getRole() == null)return;
            if (predictings.containsKey(event.getPlayer().getUniqueId())) {
                if (event.getRole().getName().contains(this.predictings.get(event.getPlayer().getUniqueId()))) {
                    final TextComponent toSend = new TextComponent("§c"+event.getPlayer().getName()+"§7 était belle et bien§c "+event.getRole().getName()+"§7, vous avez maintenant le choix entre:\n\n");
                    HoverEvent hoverEvent = new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new BaseComponent[]{
                                    new TextComponent("§a§lCLIQUEZ ICI POUR FAIRE VOTER CHOIX")
                            }
                    );
                    final TextComponent choice1 = getChoice1(event, hoverEvent);
                    choice1.addExtra("\n\n");
                    final TextComponent choice2 = getChoice2(event, hoverEvent);
                    toSend.addExtra(choice1);
                    toSend.addExtra(choice2);
                    Player owner = Bukkit.getPlayer(getRole().getPlayer());
                    if (owner != null) {
                        owner.spigot().sendMessage(toSend);
                    }
                }
            }
        }

        private TextComponent getChoice1(UHCDeathEvent event, HoverEvent hoverEvent) {
            final TextComponent choice1 = new TextComponent("§aRegagner un brain de§c vie§7 (§a+§c1/2❤ permanent§7)");

            choice1.setHoverEvent(hoverEvent);
            choice1.setClickEvent(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/ds prediction "+ event.getPlayer().getUniqueId()+" coeur"
            ));
            return choice1;
        }
        private TextComponent getChoice2(UHCDeathEvent event, HoverEvent hoverEvent) {
            final TextComponent choice2 = new TextComponent("§aRetarder l'inévitable§7 (Remet le timer de perde de§c coeur§7 à §c0§7)");
            choice2.setHoverEvent(hoverEvent);
            choice2.setClickEvent(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/ds prediction "+event.getPlayer().getUniqueId()+" time"
            ));
            return choice2;
        }
    }
    private static class MaladieRunnable extends BukkitRunnable {

        private final KagayaV2 kagaya;
        private final GameState gameState;
        private final int maxTime;
        private int actualTime = 0;
        private int stade = 0;

        private MaladieRunnable(KagayaV2 kagaya) {
            this.kagaya = kagaya;
            this.gameState = kagaya.getGameState();
            this.maxTime = Main.getInstance().getGameConfig().isMinage() ? 60*10 : 60*5;
            runTaskTimerAsynchronously(Main.getInstance(), 40, 20);
        }

        private void augmentationStade() {
            this.stade++;
            this.kagaya.setMaxHealth(kagaya.getMaxHealth()-1);
            if (this.stade == 6) {
                this.kagaya.addPower(new ShowHealthPower(kagaya));
                this.kagaya.getGamePlayer().sendMessage("§7Vous avez gagner le pouvoir de§a voir la vie des joueurs");
            } else if (this.stade == 8) {
                this.kagaya.addPower(new PilierCommand(this.kagaya));
                this.kagaya.getGamePlayer().sendMessage("§7Vous avez obtenu la capacité de savoir si un joueur est un§a pilier§7 ou§c non§6 /ds me§7 pour plus§c d'information");
            }
            if (this.stade == 14) {
                this.kagaya.getGamePlayer().sendMessage("§7Votre§2 maladie§7 à atteind son§c stade maximum§7.");
                cancel();
            }
        }
        @Override
        public void run() {
            if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!kagaya.getGamePlayer().isAlive())return;
            if (this.actualTime == maxTime) {
                this.augmentationStade();
                this.kagaya.getGamePlayer().sendMessage("§7Votre§2 maladie§7 s'aggrave, elle à atteind le §cniveau "+stade);
                this.actualTime = 0;
                return;
            }
            actualTime++;
            Player owner = Bukkit.getPlayer(this.kagaya.getPlayer());
            if (owner != null) {
                NMSPacket.sendActionBar(owner, "§bTemp avant accentuation de la§2 maladie§b: §c"+ StringUtils.secondsTowardsBeautiful(this.maxTime-actualTime));
            }
        }
        private static class PilierCommand extends CommandPower {

            public PilierCommand(@NonNull RoleBase role) {
                super("/ds pilier <joueur>", "pilier", null, role, CommandType.DS);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                String[] args = (String[]) map.get("args");
                if (args.length == 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        final GamePlayer gameTarget = getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                        if (gameTarget != null) {
                            if (gameTarget.isAlive()) {
                                if (gameTarget.getRole() != null) {
                                    if (gameTarget.getRole() instanceof PilierRoles){
                                        player.sendMessage("§b"+gameTarget.getPlayerName()+"§a est bien un pilier");
                                    }
                                    return true;
                                } else {
                                    player.sendMessage("§b"+args[1]+"§c n'a pas de rôle.");
                                    return false;
                                }
                            }
                        }
                    }
                    player.sendMessage("§b"+args[1]+"§c n'est pas connecter ou n'est pas en vie.");
                }
                return false;
            }
        }

    }
  private static class ShowHealthPower extends Power implements Listener {

        private final Map<UUID, ArmorStandUtils> armorStands = new HashMap<>();

        public ShowHealthPower(@NonNull RoleBase role) {
            super("§aVoir la vie des joueurs", null, role);
            EventUtils.registerRoleEvent(this);
            for (final UUID uuid : role.gameState.getGamePlayer().keySet()) {
                if (uuid.equals(role.getPlayer()))continue;
                ArmorStandUtils armorStand = new ArmorStandUtils(role.gameState.getGamePlayer().get(uuid).getLastLocation(), "TEST");
                armorStand.display(role.owner);
                this.armorStands.put(uuid, armorStand);
            }
            setShowInDesc(false);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            return false;
        }
        @EventHandler
        private void PlayerMooveEvent(PlayerMoveEvent event) {
            if (armorStands.containsKey(event.getPlayer().getUniqueId())) {
                Player owner = Bukkit.getPlayer(getRole().getPlayer());
                if (owner == null)return;
                armorStands.get(event.getPlayer().getUniqueId()).rename("§c"+new DecimalFormat("0").format(event.getPlayer().getHealth())+"❤", owner);
                final Location to = new Location(event.getTo().getWorld(), event.getTo().getX(), event.getTo().getY()+0.5, event.getTo().getZ());
                armorStands.get(event.getPlayer().getUniqueId()).teleport(to, owner);
            }
        }
    }
}