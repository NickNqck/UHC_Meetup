package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.UUID;

public class InosukeV2 extends SlayerRoles {

    private TextComponent textComponent;

    public InosukeV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.VENT;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Inosuke";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Inosuke;
    }

    @Override
    public void resetCooldown() {}

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return this.textComponent;
    }
    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new SentimentCommand(this));
        addPower(new SentationCommand(this));
        this.textComponent = new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }
    private static class SentimentCommand extends CommandPower {

        private boolean traque = false;

        public SentimentCommand(@NonNull RoleBase role) {
            super("/ds sentiments", "sentiments", new Cooldown(60*10), role, CommandType.DS,
                    "§7Vous permet d'obtenir la liste de tout les joueurs autours de vous (§c50 blocs§7)",
                    "§7en cliquant sur l'un de ces joueurs vous pourrez le traquer pendant§c 20 secondes§7.",
                    "",
                    "§4! ATTENTION ! §cVous n'aurez que§4 10 secondes§c pour choisir un joueur à traquer");
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> strings) {
            String[] args = (String[]) strings.get("args");
            if (args.length == 1) {
                TextComponent component = new TextComponent("§7Voici la liste de tout les§c joueurs§7 autours de vous:\n\n");
                for (final Player aroundPlayer : Loc.getNearbyPlayers(player, 50)) {
                    TextComponent toAdd = new TextComponent("§7 -§c "+aroundPlayer.getDisplayName()+"§7 (§c"+ new DecimalFormat("0").format(aroundPlayer.getLocation().distance(player.getLocation()))+"m§7)\n");
                    toAdd.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                            new TextComponent("§a§lCLIQUEZ ICI POUR TRAQUER")
                    }));
                    toAdd.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ds sentiments "+aroundPlayer.getName()));
                    component.addExtra(toAdd);
                }
                player.spigot().sendMessage(component);
                traque = true;
                Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                    if (traque) {
                        player.sendMessage("§cVous avez mis trop de temp à vous décidez, vous ne pouvez plus traquer personne.");
                        traque = false;
                        setWorkWhenInCooldown(false);
                    }
                }, 20*10);
                setWorkWhenInCooldown(true);
                return true;
            } else if (args.length == 2) {
                if (!traque) {
                    player.sendMessage("§cIl est trop tard pour démarrer la traque");
                    return false;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    player.sendMessage("§7Commencement de la§c traque§7 contre§c "+target.getDisplayName());
                    setWorkWhenInCooldown(false);
                    traque = false;
                    new TraqueRunnable(target.getUniqueId(), player.getUniqueId()).runTaskTimerAsynchronously(getPlugin(), 0, 1);
                    return true;
                } else {
                    player.sendMessage("§c"+args[1]+" n'existe pas ou n'est pas connecter");
                    return false;
                }
            }
            return false;
        }
        private static class TraqueRunnable extends BukkitRunnable {

            private final UUID uuidTarget;
            private final UUID uuidUser;
            private int timeRemaining = 20*20;//20 secondes

            private TraqueRunnable(UUID uuidTarget, UUID uuidUser) {
                this.uuidTarget = uuidTarget;
                this.uuidUser = uuidUser;
            }

            @Override
            public void run() {
                if (timeRemaining == 0) {
                    cancel();
                    return;
                }
                timeRemaining--;
                Player owner = Bukkit.getPlayer(uuidUser);
                Player target = Bukkit.getPlayer(uuidTarget);
                if (owner == null)return;
                if (target == null)return;
                NMSPacket.sendActionBar(owner, "§bTraque sur§c "+
                        target.getDisplayName()+
                        "§b:§c "+
                        ArrowTargetUtils.calculateArrow(owner, target.getLocation())+
                        new DecimalFormat("0").format(target.getLocation().distance(owner.getLocation()))+
                        "m");
            }
        }
    }
    private static class SentationCommand extends CommandPower {

        public SentationCommand(@NonNull RoleBase role) {
            super("§a/ds sentation <joueur>", "sentation", new Cooldown(60*6), role, CommandType.DS,
                    "§7En visant un joueur (à moins de§c 5 blocs§7), permet d'obtenir§c aléatoirement§7 l'une de ses informations: ",
                    "",
                    "§8 • §7Vous obtiendrez le§c nombre§7 de§c point de vie§7 du joueur viser,",
                    "",
                    "§8 • §7Vous saurrez si le joueur possède l'effet§c force§7 et/ou l'effet§c faiblesse",
                    "",
                    "§8 • §7Vous saurrez si le joueur possède l'effet§c vitesse§7 et/ou l'effet§c lenteur",
                    "",
                    "§8 • §7Vous saurrez si le joueur possède l'effet§c résistance§7 ou non");
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> stringObjectMap) {
            String[] args = (String[]) stringObjectMap.get("args");
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (Loc.getNearbyPlayers(player, 5).contains(target)) {
                        player.sendMessage(getInformations(target));
                        return true;
                    } else {
                        player.sendMessage("§c"+target.getDisplayName()+"§7 n'est pas asser proche de vous");
                        return false;
                    }
                } else {
                    player.sendMessage("§b"+args[1]+"§c n'est pas connecter");
                    return false;}
            }
            player.sendMessage("§cCette commande prend un joueur en entrer");
            return false;
        }
        private String getInformations(final Player target) {
            final int random = Main.RANDOM.nextInt(4);
            String toReturn= "§cSi vous voyez ce message c'est qu'il y a un bug au niveau du random";
            switch (random) {
                case 0:
                    toReturn = "§aVous sentez la force vitale de§c "+target.getDisplayName()+"§a il à l'air d'avoir §c"+
                            new DecimalFormat("0").format(target.getMaxHealth())+"❤ permanents§a, on dirait même qu'il possède§c "+
                            getHealthPercentage(target)+"§a de sa vie maximum.";
                    break;
                case 1:
                    toReturn = "§aVous sentez la force de§c "+
                            target.getDisplayName()+
                            "§a il à l'air "+
                            (target.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE) ? "d'avoir l'effet" : "de ne pas avoir l'effet")+
                            "§c force§a et "+
                            (target.hasPotionEffect(PotionEffectType.WEAKNESS) ? "d'avoir l'effet " : "de ne pas avoir l'effet")+
                            "§7 faiblesse";
                    break;
                case 2:
                    toReturn = "§aVous sentez la puissance se trouvant dans les jambes de§c "+target.getDisplayName()+
                            "§a il à l'air "+(target.hasPotionEffect(PotionEffectType.SPEED) ? "d'avoir l'effet" : "de ne pas avoir l'effet")+
                            " §evitesse§a et "+(target.hasPotionEffect(PotionEffectType.SLOW) ? "d'avoir l'effet" : "de ne pas avoir l'effet")+"§7 lenteur";
                    break;
                case 3:
                    toReturn = "§aVous sentez la musculature de§c "+target.getDisplayName()+"§a il à l'air "+(target.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE) ? "d'avoir l'effet" : "de ne pas avoir l'effet")+
                            "§9 résistance";
                    break;
            }
            return toReturn;
        }
        private String getHealthPercentage(Player target) {
            double currentHealth = target.getHealth();
            double maxHealth = target.getMaxHealth();
            return new DecimalFormat("0").format((currentHealth / maxHealth) * 100)+"%";
        }

    }
}