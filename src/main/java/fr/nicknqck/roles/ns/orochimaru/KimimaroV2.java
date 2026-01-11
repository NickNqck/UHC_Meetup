package fr.nicknqck.roles.ns.orochimaru;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.power.CooldownFinishEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.OrochimaruRoles;
import fr.nicknqck.roles.ns.orochimaru.edov2.KabutoV2;
import fr.nicknqck.roles.ns.orochimaru.edov2.OrochimaruV2;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.WorldUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

import static fr.nicknqck.utils.WorldUtils.generateCylinder;

public class KimimaroV2 extends OrochimaruRoles {

    public KimimaroV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public String getName() {
        return "Kimimaro";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Kimimaro;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new EpeeItemPower(this), true);
        addPower(new ForetItemPower(this), true);
        setChakraType(getRandomChakras());
        addKnowedRole(OrochimaruV2.class);
        addKnowedRole(KabutoV2.class);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }
    private static class EpeeItemPower extends ItemPower implements Listener {

        private int coups = 0;

        public EpeeItemPower(@NonNull RoleBase role) {
            super("§fEpée en or§r", new Cooldown(120), new ItemBuilder(new EpeeOsItem().getItem()), role,
                    "§7Vous ne pouvez infliger que§c 8 coups§7 avec cette objet",
                    "",
                    "§7Au bout de§c 2 minutes§7 vous pourrez à nouveau l'utiliser§c 8 fois§7.",
                    "",
                    "§7Les coups infliger par cette objet feront les mêmes§c dégâts§7 qu'une§b épée en diamant tranchant IV");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.ATTACK_ENTITY)) {
                coups++;
                return coups >= 8;
            }
            return false;
        }
        @EventHandler
        private void onEndCD(@NonNull final CooldownFinishEvent event) {
            if (event.getCooldown().getUniqueId().equals(getCooldown().getUniqueId())) {
                this.coups = 0;
            }
        }
        @Getter
        private static class EpeeOsItem {

            private final ItemStack item;

            public EpeeOsItem() {
                final ItemStack item = new ItemBuilder(Material.BONE).setName("§fÉpée en os").toItemStack();
                net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);

                NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();

                NBTTagList modifiers = new NBTTagList();
                NBTTagCompound damage = new NBTTagCompound();

                damage.set("AttributeName", new NBTTagString("generic.attackDamage"));
                damage.set("Name", new NBTTagString("generic.attackDamage"));
                damage.set("Amount", new NBTTagDouble(12)); //13.25
                damage.set("Operation", new NBTTagInt(0));
                damage.set("UUIDLeast", new NBTTagInt(894654));
                damage.set("UUIDMost", new NBTTagInt(2872));

                modifiers.add(damage);
                compound.set("AttributeModifiers", modifiers);
                nmsStack.setTag(compound);

                this.item = CraftItemStack.asBukkitCopy(nmsStack);
            }
        }
    }
    private static class ForetItemPower extends ItemPower {

        public ForetItemPower(@NonNull RoleBase role) {
            super("§fFôret d'os§r", new Cooldown(60*10), new ItemBuilder(Material.QUARTZ).setName("§fFôret d'os").addEnchant(Enchantment.DEPTH_STRIDER, 1).hideEnchantAttributes(), role,
                    "§7Crée une grande§a Fôret§7 fait à base d'§fos§7.",
                    "",
                    "§7Toute les§c 5 secondes§7 tout les§c joueurs§7 étant dans la§f Fôret d'os§7",
                    "§7subiront§c 1/2❤§7 de§c dégât§7 (aucun joueur ne peut mourir de cette effet).",
                    "",
                    "§7Lorsqu'un joueur subit des§c dégâts§7 dû à l'effet de ce§c pouvoir§7",
                    "§7des§a particules§7 apparaisse autours de lui.",
                    "",
                    "§7Vous ne pouvez subir§c aucun dégât§7 venant de ce§c pouvoir§7",
                    "§7mais les§a particules§7 apparaitront quand même.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            Location center = player.getLocation().clone();
            List<Block> blocks = spawnForetOs(center);
            new ForetOsTask(player.getUniqueId(), blocks, center, 30, this)
                    .runTaskTimer(getPlugin(), 0, 20);
            return true;
        }
        private static void spawnAstraStunEffect(Location center) {
            World world = center.getWorld();

            double radius = 1.3;
            int points = 24;

            for (int i = 0; i < points; i++) {
                double angle = (2 * Math.PI / points) * i;
                double x = Math.cos(angle) * radius;
                double z = Math.sin(angle) * radius;

                Location loc = center.clone().add(x, 0.3, z);

                world.spigot().playEffect(
                        loc,
                        Effect.WITCH_MAGIC,
                        0, 0,
                        0, 0, 0,
                        0,
                        1,
                        64
                );

                world.spigot().playEffect(
                        loc,
                        Effect.PORTAL,
                        0, 0,
                        0, 0, 0,
                        0.2f,
                        3,
                        64
                );
            }
        }
        private void spawnAstraPulse(Location center) {
            new BukkitRunnable() {
                int ticks = 0;

                @Override
                public void run() {
                    spawnAstraStunEffect(center);

                    ticks++;
                    if (ticks >= 2) { // 2 pulses
                        cancel();
                    }
                }
            }.runTaskTimer(this.getPlugin(), 0L, 6L);
        }

        private static List<Block> spawnForetOs(Location center) {
            List<Block> blocks = WorldUtils.generateSphere(center, 30, false).stream().map(Location::getBlock).collect(Collectors.toList());

            blocks.stream().filter(block ->
                    block.getType() == Material.LOG ||
                            block.getType() == Material.LOG_2 ||
                            block.getType() == Material.LEAVES ||
                            block.getType() == Material.LEAVES_2).forEach(block -> block.setType(Material.AIR));

            QuartzTreeDelegator delegate = new QuartzTreeDelegator(center.getWorld());

            List<Location> cylinder = generateCylinder(center, 30);
            List<Location> treeSpawns = new ArrayList<>();

            for (int i = 0; i < cylinder.size() / 2; i++) {
                int index = new Random().nextInt(cylinder.size());
                treeSpawns.add(cylinder.get(index));
                cylinder.remove(index);
            }

            for (Location treeSpawn : treeSpawns) {
                Block down = treeSpawn.getWorld().getHighestBlockAt(treeSpawn).getRelative(BlockFace.DOWN);
                if (down.getType() != Material.QUARTZ_BLOCK) down.setType(Material.GRASS);
                center.getWorld().generateTree(treeSpawn, TreeType.SMALL_JUNGLE, delegate);
            }
            return delegate.getBlocks();
        }
        private static class QuartzTreeDelegator implements BlockChangeDelegate {

            private final World world;
            @Getter
            private final List<Block> blocks;

            public QuartzTreeDelegator(World world) {
                this.world = world;
                this.blocks = new ArrayList<>();
            }

            @Override
            public boolean setRawTypeId(int x, int y, int z, int i3) {
                world.getBlockAt(x, y, z).setType(Material.QUARTZ_BLOCK);
                return true;
            }

            @Override
            public boolean setRawTypeIdAndData(int x, int y, int z, int i3, int i4) {
                world.getBlockAt(x, y, z).setType(Material.QUARTZ_BLOCK);
                return true;
            }

            @Override
            public boolean setTypeId(int x, int y, int z, int i3) {
                world.getBlockAt(x, y, z).setType(Material.QUARTZ_BLOCK);
                return true;
            }

            @Override
            public boolean setTypeIdAndData(int x, int y, int z, int i3, int i4) {
                Block block = world.getBlockAt(x, y, z);
                block.setType(Material.QUARTZ_BLOCK);
                blocks.add(block);
                return true;
            }

            @Override
            @SuppressWarnings("deprecation")
            public int getTypeId(int x, int y, int z) {
                return Material.QUARTZ_BLOCK.getId();
            }

            @Override
            public int getHeight() {
                return 0;
            }

            @Override
            public boolean isEmpty(int i, int i1, int i2) {
                return false;
            }

        }
        private static class ForetOsTask extends BukkitRunnable {

            private final UUID playerID;
            private final List<Block> blocks;
            private final Location center;
            private final int radius;

            private int timer = 3 * 60;
            private int damageTick = 0;

            private final ForetItemPower foretItemPower;

            public ForetOsTask(UUID playerID,
                               List<Block> blocks,
                               Location center,
                               int radius,
                               ForetItemPower foretItemPower) {
                this.playerID = playerID;
                this.blocks = blocks;
                this.center = center;
                this.radius = radius;
                this.foretItemPower = foretItemPower;
            }


            @Override
            public void run() {
                Player owner = Bukkit.getPlayer(playerID);

                if (this.timer == 0) {
                    blocks.forEach(block -> block.setType(Material.AIR));
                    if (owner != null) {
                        owner.sendMessage("§7Votre forêt s'est détruite !");
                    }
                    cancel();
                    return;
                }

                // ActionBar
                if (owner != null) {
                    this.foretItemPower.getRole().getGamePlayer()
                            .getActionBarManager()
                            .updateActionBar(
                                    "kimimaro.foret",
                                    "§fForêt en Os§7:§c " + StringUtils.secondsTowardsBeautiful(this.timer)
                            );
                }

                damageTick++;
                if (damageTick >= 7) {
                    damageTick = 0;

                    for (@NonNull Player target : center.getWorld().getPlayers()) {
                        if (!(target.getLocation().distanceSquared(center) <= (radius * radius)))continue;
                        this.foretItemPower.spawnAstraPulse(target.getLocation());
                        if (target.getUniqueId().equals(this.playerID))continue;
                        if (target.getLocation().distanceSquared(center) <= radius * radius) {

                            // Empêche la mort
                            double newHealth = Math.max(1.0, target.getHealth() - 1.0);
                            target.setHealth(newHealth);
                            target.sendMessage("§7Les os de la forêt vous blessent...");
                        }
                    }
                }

                this.timer--;
            }

        }


    }
}