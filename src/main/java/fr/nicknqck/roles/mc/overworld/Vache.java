package fr.nicknqck.roles.mc.overworld;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.mc.builders.OverWorldRoles;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.packets.NMSPacket;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Vache extends OverWorldRoles {

    private MilkPower milkPower;
    private TextComponent desc;

    public Vache(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        this.milkPower = new MilkPower(this);
        addPower(this.milkPower);
        AutomaticDesc automaticDesc = new AutomaticDesc(this).setCommands(new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous permet de retirer les effets d'un §cjoueur§7 pendant§c 5 secondes§7. ")}),
                "§6/mc milk <joueur>",
                60*20
        ));
        this.desc = automaticDesc.getText();
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Vache";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Vache;
    }

    @Override
    public void resetCooldown() {
        assert milkPower != null;
        this.milkPower.getCooldown().resetCooldown();
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return desc;
    }

    @Override
    public void onMcCommand(String[] args) {
        if (args[0].equalsIgnoreCase("milk")) {
            if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    owner.sendMessage("§c"+args[1]+" n'est pas connectée");
                } else {
                    Map<String, Object> uwu = new HashMap<>();
                    uwu.put("target", target);
                    if (this.milkPower.checkUse(owner, uwu)) {
                        owner.sendMessage("§b"+target.getDisplayName()+"§7§7 n'aura plus d'effet pendant les§c 5 prochaines secondes§7."+gameState.getInGameTime());
                    }
                }
            } else {
                owner.sendMessage("§cCette commande prend un joueur en compte");
            }
        }
    }
    private static class MilkPower extends Power {

        private final MilkRunnable runnable;

        public MilkPower(@NonNull RoleBase role) {
            super("/mc milk <joueur>", new Cooldown(60*20), role);
            this.runnable = new MilkRunnable(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            this.runnable.start((Player) args.get("target"));
            return true;
        }
        private static class MilkRunnable extends BukkitRunnable implements Listener {

            private final MilkPower power;
            private int timeLeft;
            private final GameState gameState;
            private String tName;
            private UUID tUUID;

            private MilkRunnable(MilkPower power) {
                this.power = power;
                this.gameState = this.power.getRole().getGameState();
            }

            @Override
            public void run() {
                if (gameState.getServerState() != GameState.ServerStates.InGame) {
                    cancel();
                    return;
                }

                Player owner = Bukkit.getPlayer(this.power.getRole().getPlayer());
                if (owner != null) {
                    NMSPacket.sendActionBar(owner, "§bTemp restant de perte d'effet (§c"+tName+"§b): §c"+ StringUtils.secondsTowardsBeautiful(timeLeft/2));
                }
                Player target = Bukkit.getPlayer(tUUID);
                if (target != null) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        for (PotionEffect effect : target.getActivePotionEffects()) {
                            target.removePotionEffect(effect.getType());
                        }
                    });
                }
                if (timeLeft <= 0) {
                    cancel();
                    assert Bukkit.getPlayer(this.power.getRole().getPlayer()) != null;
                    Bukkit.getPlayer(this.power.getRole().getPlayer()).sendMessage("§7L'annulation des effets de§c "+tName+"§7 est terminer." + gameState.getInGameTime());
                }
                timeLeft--;
            }

            public void start(Player target) {
                this.timeLeft = 10;
                this.tName = target.getName();
                this.tUUID = target.getUniqueId();
                runTaskTimerAsynchronously(Main.getInstance(), 0, 10);
            }
        }
    }
}
