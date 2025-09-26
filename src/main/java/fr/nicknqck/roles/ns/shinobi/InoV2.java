package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

public class InoV2 extends ShinobiRoles {

    public InoV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.CONNUE;
    }

    @Override
    public String getName() {
        return "Ino";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Ino;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new SphereDeCaptation(this), true);
        addPower(new TelepatiCommand(this));
        setChakraType(getRandomChakrasBetween(Chakras.KATON, Chakras.DOTON, Chakras.SUITON));
        addKnowedRole(Shikamaru.class);
        addPower(new ControleCommand(this));
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }
    private static class SphereDeCaptation extends ItemPower {

        public SphereDeCaptation(@NonNull RoleBase role) {
            super("Sphere de Captation", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§aSphere de Captation"), role,
                    "§7Vous permet d'obtenir§e Speed I§7 pendant§c 3 minutes§7 et d'obtenir les§c coordonnées§7 de toute les créatures possédant du chakra (§dBijuus§7 et§a humains§7)");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final StringBuilder sb = new StringBuilder("§7Voici la position de toute les créatures possédant du chakra (§c100 blocs§7):\n\n");
                for (final Entity entity : player.getWorld().getEntities()) {
                    if (entity.getLocation().distance(player.getLocation()) > 100.0)continue;
                    CraftEntity craftEntity = ((CraftEntity) entity);
                    if (craftEntity.getHandle().hasCustomName() || entity instanceof Player) {
                        String name = (craftEntity.getHandle().hasCustomName() ? craftEntity.getCustomName() : entity.getName());
                        sb.append("§8 - §c")
                                .append(name)
                                .append(": x: ")
                                .append(craftEntity.getLocation().getBlockX())
                                .append(", y: ")
                                .append(craftEntity.getLocation().getBlockY())
                                .append(", z: ")
                                .append(craftEntity.getLocation().getBlockZ())
                                .append("\n");
                    }
                }
                player.sendMessage(sb.toString());
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*3, 0, false, false), true);
                return true;
            }
            return false;
        }
    }
    private static class TelepatiCommand extends CommandPower implements Listener {

        private final UUID myUUID;
        private GamePlayer toTalk = null;

        public TelepatiCommand(@NonNull RoleBase role) {
            super("/ns telepathie <joueur>", "telepathie", new Cooldown(60*3), role, CommandType.NS,
                    "§7Vous permet de démarrer un chat commun avec le joueur visé pendant§c 60 secondes§7.");
            EventUtils.registerRoleEvent(this);
            this.myUUID = role.getPlayer();
            setMaxUse(5);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§b"+args[1]+"§c n'existe pas ou n'est pas connecté.");
                    return false;
                }
                if (getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                    player.sendMessage("§cImpossible de démarrer une§a télépathie§c avec§a " + target.getDisplayName());
                    return false;
                }
                final GamePlayer gamePlayer = getRole().getGamePlayer();
                final GamePlayer gameTarget = getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                new TelepathieRunnable(gamePlayer, gameTarget, this);
                target.sendMessage("§aIno§7 a crée un§a chat§7 avec vous, vous pouvez parler avec elle en commencent a écrire dans le chat avec§c :");
                player.sendMessage("§7Vous avez commencer une§a télépathie avec§a "+target.getName()+"§7, vous pouvez lui parler en commencent un message avec§c :");
                return true;
            }
            return false;
        }
        @EventHandler(priority = EventPriority.HIGHEST)
        private void onChat(AsyncPlayerChatEvent event) {
            if (this.toTalk != null) {
                if (!event.getMessage().startsWith(":"))return;
                if (event.getPlayer().getUniqueId().equals(this.myUUID)) {
                    event.getPlayer().sendMessage("§aIno: §7"+ChatColor.translateAlternateColorCodes('&', event.getMessage().substring(1)));
                    this.toTalk.sendMessage("§aIno: §7"+ChatColor.translateAlternateColorCodes('&', event.getMessage().substring(1)));
                    event.setCancelled(true);
                } else if (event.getPlayer().getUniqueId().equals(this.toTalk.getUuid())){
                    event.getPlayer().sendMessage("§a"+event.getPlayer().getName()+": §7"+ChatColor.translateAlternateColorCodes('&', event.getMessage().substring(1)));
                    this.getRole().getGamePlayer().sendMessage("§a"+event.getPlayer().getName()+": §7"+ChatColor.translateAlternateColorCodes('&', event.getMessage().substring(1)));
                }
            }
        }
        private static class TelepathieRunnable extends BukkitRunnable {

            private final GamePlayer gamePlayer;
            private final GamePlayer gameTarget;
            private final TelepatiCommand telepatiCommand;
            private int timeLeft = 60;

            private TelepathieRunnable(GamePlayer gamePlayer, GamePlayer gameTarget, TelepatiCommand telepatiCommand) {
                this.gamePlayer = gamePlayer;
                this.gameTarget = gameTarget;
                this.telepatiCommand = telepatiCommand;
                this.gamePlayer.getActionBarManager().addToActionBar("ino.telepathie", "§bTemp restant§a Télépathie§b: §c"+this.timeLeft+"s");
                this.gameTarget.getActionBarManager().addToActionBar("ino.telepathie", "§bTemp restant§a Télépathie§b: §c"+this.timeLeft+"s");
                this.telepatiCommand.toTalk = gameTarget;
                runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.timeLeft <= 0) {
                    this.gamePlayer.getActionBarManager().removeInActionBar("ino.telepathie");
                    this.gameTarget.getActionBarManager().removeInActionBar("ino.telepathie");
                    this.gamePlayer.sendMessage("§cVotre communication par§a télépathie§c s'arrête...");
                    this.gameTarget.sendMessage("§cVotre communication par§a télépathie§c s'arrête...");
                    this.telepatiCommand.toTalk = null;
                    cancel();
                    return;
                }
                this.timeLeft--;
                this.gamePlayer.getActionBarManager().addToActionBar("ino.telepathie", "§bTemp restant§a Télépathie§b: §c"+this.timeLeft+"s");
                this.gameTarget.getActionBarManager().addToActionBar("ino.telepathie", "§bTemp restant§a Télépathie§b: §c"+this.timeLeft+"s");
            }
        }
    }
    private static class ControleCommand extends CommandPower {

        private GamePlayer gameTarget = null;

        public ControleCommand(@NonNull RoleBase role) {
            super("/ns controle <joueur>", "controle", new Cooldown(60*6), role, CommandType.NS,
                    "§7Vous permet d'insuffler du§a chakra§7 dans un joueur,",
                    "§7Une fois fait vous pourrez faire la commande \"§6/ns controle§7\"",
                    "§7Elle vous mettra en§a spectateur§7 autours de la dernière personne à qui vous avez donner du§a chakra§7,",
                    "",
                    "§aAprès§c 15 secondes§7 vous reviendrez à votre position de départ avec§c 2 minutes§7 de§9 Résistance§7 et votre§a vie§7 régénérer§c entièrement§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§b"+args[1]+"§c n'existe pas ou n'est pas connecté.");
                    return false;
                }
                if (getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                    player.sendMessage("§cImpossible de placer votre§a chakra§c dans l'esprit de§a " + target.getDisplayName());
                    return false;
                }
                this.gameTarget = getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                player.sendMessage("§c"+target.getName()+"§a a bien reçus votre§c chakra");
                return false;
            } else {
                if (this.gameTarget == null) {
                    player.sendMessage("§cIl faut d'abord mettre du§a chakra§c dans un joueur via la commande \"§6/ns controle <joueur>§c\"");
                    return false;
                }
                player.setGameMode(GameMode.SPECTATOR);
                new ControleRunnable(this.gameTarget, getRole().getGamePlayer(), getRole().getGameState());
                player.sendMessage("§7Vous commencez à voir grâce au chakra que vous aviez infusé dans§a "+this.gameTarget.getPlayerName());
                return true;
            }
        }
        private static class ControleRunnable extends BukkitRunnable {

            private final GamePlayer gameTarget;
            private final GamePlayer gamePlayer;
            private final GameState gameState;
            private final Location initLoc;
            private int timeLeft = 15*20;

            private ControleRunnable(GamePlayer gameTarget, GamePlayer gamePlayer, GameState gameState) {
                this.gameTarget = gameTarget;
                this.gamePlayer = gamePlayer;
                this.gameState = gameState;
                this.initLoc = gamePlayer.getLastLocation();
                gamePlayer.getActionBarManager().addToActionBar("ino.controlemental."+gameTarget.getPlayerName(), "§bTemp restant (§aControle mental§b): §c"+ StringUtils.secondsTowardsBeautiful(this.timeLeft/20));
                runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                gamePlayer.getActionBarManager().updateActionBar("ino.controlemental."+this.gameTarget.getPlayerName(), "§bTemp restant (§aControle mental§b): §c"+ StringUtils.secondsTowardsBeautiful(this.timeLeft/20));
                if (this.timeLeft <= 0) {
                    gamePlayer.getActionBarManager().removeInActionBar("ino.controlemental."+this.gameTarget.getPlayerName());
                    final Player player = Bukkit.getPlayer(this.gamePlayer.getUuid());
                    if (player != null) {
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                            player.setGameMode(GameMode.SURVIVAL);
                            player.teleport(this.initLoc);
                            player.setHealth(player.getMaxHealth());
                            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*2, 0, false, false), true);
                        });
                    }
                    cancel();
                    return;
                }
                if (!Loc.getNearbyGamePlayers(this.gameTarget.getLastLocation(), 15).contains(this.gamePlayer)) {
                    this.gamePlayer.teleport(this.gameTarget.getLastLocation());
                    gamePlayer.sendMessage("§cImpossible de s'éloigner de votre cible.");
                }
                this.timeLeft--;
            }
        }
    }
}