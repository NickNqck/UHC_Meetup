package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.ArrowTargetUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
        addPower(new SentationItem(this), true);
        this.textComponent = new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }
    private static class SentimentCommand extends CommandPower {

        private boolean traque = false;

        public SentimentCommand(@NonNull RoleBase role) {
            super("/ds sentiments", "sentiments", new Cooldown(60*10), role, CommandType.DS);
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
    private static class SentationItem extends ItemPower {

        protected SentationItem(@NonNull RoleBase role) {
            super("§aPrésentiment Bestial", new Cooldown(60*5), new ItemBuilder(Material.PORK).setName("§aPrésentiment Bestial"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                Player target = getRole().getTargetPlayer(player, 5.0);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur");
                    return false;
                }
                final int rdm = Main.RANDOM.nextInt(4);
                player.sendMessage(getInformations(target, rdm));
            }
            return false;
        }
        private String getInformations(final Player target, final int random) {
            String toReturn= "§CBUUUUUUUUUUUUUUUG";
            switch (random) {
                case 0:
                    toReturn = "§aVous sentez la force vitale de§c "+target.getDisplayName()+"§a il à l'air d'avoir §c"+
                    new DecimalFormat("0").format(target.getMaxHealth())+"§a, on dirait même qu'il possède§c "+getHealthPercentage(target)+"§a.";
                    break;
                case 1:
                    toReturn = "§aVous sentez la force de§c "+
                            target.getDisplayName()+
                            "§a il à l'air "+
                            (target.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE) ? "d'avoir l'effet " : "de ne pas avoir l'effet ")+
                            "§c force§a et "+
                            (target.hasPotionEffect(PotionEffectType.WEAKNESS) ? "d'avoir l'effet " : "de ne pas avoir l'effet")+
                            "§7faiblesse";
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
