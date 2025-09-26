package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Ino extends ShinobiRoles {
    private final ItemStack transpositionItem = new ItemBuilder(Material.NETHER_STAR).setName("§aTransposition").setLore("§7Vous permet de suivre très discrètement un joueur").toItemStack();
    private int cdTransposition = 0;
    public Ino(UUID player) {
        super(player);
        setChakraType(getRandomChakras());
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        giveItem(owner, false, getItems());
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Ino;
    }
    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.CONNUE;
    }

    @Override
    public String[] Desc() {
        KnowRole(owner, Roles.Shikamaru, 15);
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§aIno",
                AllDesc.objectifteam+"§aShinobi",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§aTransposition§f: En visant un joueur, vous permet de vous mettre en§c Spectateur§f autours du joueur visée pendant§c 1 minute§f, après ce temp vous retournerez à votre ancienne position.§7 (1x/5m)",
                "",
                AllDesc.particularite,
                "",
                "Vous connaissez les§c joueurs§f possédent le rôle de§a Shikamaru§f et§a Choji",
                "",
                "",
                AllDesc.bar

        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                transpositionItem
        };
    }
    @Override
    public void resetCooldown() {
        cdTransposition = 0;
    }

    @Override
    public void Update(GameState gameState) {
        super.Update(gameState);
        if (cdTransposition >= 0){
            cdTransposition--;
            if (cdTransposition == 0) {
                if (Bukkit.getPlayer(getPlayer()) != null){
                    Bukkit.getPlayer(getPlayer()).sendMessage("§7Vous pouvez à nouveau utiliser votre§a Transposition§7 sur un joueur.");
                }
            }
        }
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(transpositionItem)){
            if (cdTransposition > 1){
                sendCooldown(owner, cdTransposition);
                return true;
            }
            Player target = RayTrace.getTargetPlayer(owner, 30.0, null);
            if (target != null){
                new TranspositionRunnable(this, target).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                cdTransposition = 60*6;
            } else {
                owner.sendMessage("§cIl faut viser un joueur !");
            }
            return true;
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public String getName() {
        return "Ino";
    }

    private static class TranspositionRunnable extends BukkitRunnable {
        private final Ino ino;
        private final UUID uuidTarget;
        public TranspositionRunnable(Ino ino, Player target) {
            this.ino = ino;
            this.uuidTarget = target.getUniqueId();
        }

        @Override
        public void run() {
            if (GameState.getInstance().getServerState() != GameState.ServerStates.InGame) {
                cancel();
                return;
            }
            Player target = Bukkit.getPlayer(uuidTarget);
            Player owner = Bukkit.getPlayer(ino.getPlayer());
            if (target != null && owner != null){
                if (target.getGameMode() == GameMode.SPECTATOR ||owner.getGameMode() != GameMode.SPECTATOR){
                    cancel();
                    return;
                }
                if (ino.cdTransposition <= 60*5){
                cancel();
                return;
                }
                if (!Loc.getNearbyPlayers(target, 15).contains(owner)){
                    owner.teleport(target);
                    owner.sendMessage("§cVous ne pouvez pas vous éloignez de votre cible");
                }
            }
        }
    }
}