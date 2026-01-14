package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.ds.UrokodakiDsWaterEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class MakomoV2 extends SlayerRoles implements Listener{

    private boolean targetByUrokodaki = false;

    public MakomoV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.EAU;
    }

    @Override
    public String getName() {
        return "Makomo";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Makomo;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new LiquefactionAccelere(this), true);
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.NIGHT);
        EventUtils.registerRoleEvent(this);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createAutomaticDesc(this)
                .addCustomLine(targetByUrokodaki ? "" : "§7Si§a Urokodaki§7 vous§c cible§7 avec son§b /ds water§7 vous obtiendrez l'effet§e Speed II§7 le§e jour§7.")
                .getText();
    }
    public void targetedByUrokodaki() {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 1, false, false), EffectWhen.DAY);
        getGamePlayer().sendMessage("§7Vous avez gagnez l'effet§e Speed II§7 le§c jour§7.");
        this.targetByUrokodaki = true;
    }
    @EventHandler
    private void onUrokodakiTarget(final UrokodakiDsWaterEvent event) {
        if (event.getGameTarget().getUuid().equals(getPlayer())) {
            targetedByUrokodaki();
        }
    }
    @EventHandler
    private void onEndGiveRole(final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        if (!event.getGameState().getAttributedRole().contains(Roles.Urokodaki)) {
            targetedByUrokodaki();
        }
    }
    private static class LiquefactionAccelere extends ItemPower implements Listener {

        private final LinkedList<Block> blocks;

        public LiquefactionAccelere(@NonNull RoleBase role) {
            super("Liquéfaction Accéléré", new Cooldown(60*6), new ItemBuilder(Material.NETHER_STAR).setName("§bLiquéfaction Accéléré"), role,
                    "§7Vous entours d'§beau§7 pendant§c 2 secondes§7 (vous empêche de bouger), puis, vous§c téléporte§7 sur la§c cible.",
                    "",
                    "§7Une fois§c téléporter§7 la§c cible§7 obtient§c 30 secondes§7 de§c Slowness I§7.");
            this.blocks = new LinkedList<>();
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Player target = RayTrace.getTargetPlayer(player, 30, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                final GamePlayer gameTarget = GameState.getInstance().getGamePlayer().get(target.getUniqueId());
                if (gameTarget == null) {
                    player.sendMessage("§cImpossible de viser ce joueur !");
                    return false;
                }
                if (!gameTarget.isAlive() || !gameTarget.isOnline()) {
                    player.sendMessage("§cIl faut viser un joueur  !");
                    return false;
                }
                getRole().getGamePlayer().stun(39, false, false);
                new WaterRunnable(getRole().getGamePlayer(), gameTarget, this).runTaskTimer(getPlugin(), 1, 20);
                return true;
            }
            return false;
        }
        @EventHandler
        public void onWaterFlow(BlockFromToEvent event) {
            Material type = event.getBlock().getType();
            if (type == Material.WATER || type == Material.STATIONARY_WATER) {
                if (this.blocks.contains(event.getBlock())){
                    event.setCancelled(true);
                }
            }
        }
        private static class WaterRunnable extends BukkitRunnable {

            private final GamePlayer gameOwner;
            private final GamePlayer gameTarget;
            private final LiquefactionAccelere liquefactionAccelere;
            private int step = 0;

            private WaterRunnable(GamePlayer gameOwner, GamePlayer gameTarget, LiquefactionAccelere liquefactionAccelere) {
                this.gameOwner = gameOwner;
                this.gameTarget = gameTarget;
                this.liquefactionAccelere = liquefactionAccelere;
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                final Player owner = Bukkit.getPlayer(gameOwner.getUuid());
                if (owner == null)return;
                if (step == 0) {
                    owner.getLocation().getBlock().setType(Material.STATIONARY_WATER, true);
                    owner.getEyeLocation().getBlock().setType(Material.STATIONARY_WATER, true);
                    this.liquefactionAccelere.blocks.add(owner.getLocation().getBlock());
                    this.liquefactionAccelere.blocks.add(owner.getEyeLocation().getBlock());
                    this.step++;
                    return;
                }
                if (step == 1) {
                    int initX = owner.getLocation().getBlockX();
                    int initZ = owner.getLocation().getBlockZ();
                    int initY = owner.getLocation().getBlockY();
                    for (int x = (initX-1); x <= (initX+1); x++) {
                        for (int z = (initZ-1); z <= (initZ+1); z++) {
                            for (int y = (initY-1); y <= (initY+1); y++) {
                                final Block block = owner.getWorld().getBlockAt(x, y, z);
                                block.setType(Material.STATIONARY_WATER, true);
                                this.liquefactionAccelere.blocks.add(block);
                            }
                        }
                    }
                    this.step++;
                    return;
                }
                if (step == 2) {
                    final Player target = Bukkit.getPlayer(this.gameTarget.getUuid());
                    if (target != null) {
                        target.getLocation().getBlock().setType(Material.STATIONARY_WATER, true);
                        owner.teleport(target);
                        owner.sendMessage("§7Votre§b liquéfaction§7 a atteint§c "+target.getDisplayName()+"§7.");
                        this.step++;
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*30, 0, false, false), true);
                        target.sendMessage("§7Vous avez été toucher par la§b liquéfaction§7 de§a Makomo§7.");
                        return;
                    }
                }
                if (step == 3) {
                    for (final Block block : this.liquefactionAccelere.blocks) {
                        block.setType(Material.AIR, true);
                    }
                    this.liquefactionAccelere.blocks.clear();
                }
            }
        }
    }
}