package fr.nicknqck.roles.ds.demons;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.time.OnSecond;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.lune.RuiV2;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.*;

public class KumoV2 extends DemonInferieurRole {

    public KumoV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.DEMON;
    }

    @Override
    public String getName() {
        return "Kumo";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Kumo;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new PrisonPower(this), true);
        addPower(new CoconPower(this), true);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            if (getGameState().getAttributedRole().contains(Roles.Rui)) {
                final GamePlayer rui = getListGamePlayerFromRole(RuiV2.class).stream().findFirst().get();
                getMessageOnDescription().add("§7Votre§c lune§7 est §c"+rui.getPlayerName());
                setLune((DemonsRoles) rui.getRole());
            } else {
                super.RoleGiven(gameState);
            }
        }, 20);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .getText();
    }

    private static class PrisonPower extends ItemPower implements Listener {

        private final Map<Block, Material> blockMaterialMap = new LinkedHashMap<>();
        private int timeLeft = 30;
        private int timeInside = 4;

        public PrisonPower(@NonNull RoleBase role) {
            super("Prison de toile", new Cooldown(60*5), new ItemBuilder(Material.WEB).setName("§cPrison de toile"), role,
                    "§7Crée un dôme fait en§c cobweb§7 pendant une durée de§c 30 secondes§7.",
                            "§7Si vous êtes coincé dans vos propre toile d'araignée, le dôme disparaitra au bout de§c 4 secondes§7.");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> caca) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final MathUtil mathUtil = new MathUtil();
                final Map<Block, Material> blocks = new HashMap<>();
                for(Location location : mathUtil.sphere(player.getLocation(), 8, true)) {
                    Material type = location.getBlock().getType();
                    if(type == Material.AIR || type == Material.WATER || type == Material.STATIONARY_WATER || type == Material.LEAVES || type == Material.LEAVES_2) {
                        blocks.put(location.getBlock(), location.getBlock().getType());
                        location.getBlock().setType(Material.WEB);
                    }
                }
                this.blockMaterialMap.putAll(blocks);
                this.getRole().getGamePlayer().getActionBarManager().addToActionBar("kumo.prison", "§bTemp restant (§cPrison§b): "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                return true;
            }
            return false;
        }
        @EventHandler
        private void onBlockPose(final BlockPlaceEvent event) {
            if (event.isCancelled())return;
            if (!event.getPlayer().getUniqueId().equals(getRole().getPlayer()))return;
            System.out.println("Mat: "+event.getBlockPlaced().getType().name());
            if (event.getBlockPlaced().getType().equals(Material.WEB) || event.getBlockPlaced().getType().equals(Material.TRIPWIRE)) {
                event.setCancelled(true);
                event.setBuild(false);
            }
        }
        @EventHandler
        private void onSecond(final OnSecond event) {
            if (!event.isInGame()) {
                resetBlocks();
            } else {
                if (!this.blockMaterialMap.isEmpty()) {
                    this.getRole().getGamePlayer().getActionBarManager().addToActionBar("kumo.prison", "§bTemp restant (§cPrison§b): "+ StringUtils.secondsTowardsBeautiful(this.timeLeft));
                    if (this.timeLeft <= 0 || this.timeInside <= 0) {
                        this.getRole().getGamePlayer().getActionBarManager().removeInActionBar("kumo.prison");
                        this.resetBlocks();
                        return;
                    }
                    final Player owner = Bukkit.getPlayer(getRole().getPlayer());
                    if (owner != null) {
                        if (owner.getLocation().getBlock().getType().equals(Material.WEB)) {
                            this.timeInside--;
                            this.getRole().getGamePlayer().sendMessage("§cTemp avant disparition forcé de la prison: §b"+this.timeInside+"s");
                        }
                    }
                    this.timeLeft--;
                }
            }
        }
        private void resetBlocks() {
            this.blockMaterialMap.keySet().forEach(location -> location.setType(this.blockMaterialMap.get(location)));
            this.blockMaterialMap.clear();
            this.timeLeft = 60;
            this.timeInside = 4;
        }
    }
    private static class CoconPower extends ItemPower {

        public CoconPower(@NonNull RoleBase role) {
            super("Cocon", new Cooldown(60*5), new ItemBuilder(Material.STRING).setName("§cCocon"), role,
                    "§7En visant un joueur, vous permet de lui mettre des§c cobweb§7 la ou il est.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final Player target = RayTrace.getTargetPlayer(player, 25, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                final List<Block> blockList = this.getBlockAround(target);
                for (final Block block : blockList) {
                    block.setType(Material.WEB);
                }
                player.sendMessage("§bVous avez enfermer dans un cocon§c "+target.getName());
                target.sendMessage("§cVous avez été pris dans le cocon de§6 Kumo§c.");
                return true;
            }
            return false;
        }
        private List<Block> getBlockAround(@NonNull final Player target) {
            final List<Block> toReturn = new ArrayList<>();
            final Location initLoc = target.getLocation().clone();
            for (int x = initLoc.getBlockX(); x <= initLoc.getBlockX(); x++) {
                for (int y = initLoc.getBlockY()-1; y <= initLoc.getBlockY()+1; y++) {
                    for (int z = initLoc.getBlockZ(); z <= initLoc.getBlockZ(); z++) {
                        final Location newLoc = new Location(initLoc.getWorld(), x, y, z);
                        toReturn.add(newLoc.getBlock());
                    }
                }
            }
            return toReturn;
        }
    }
}