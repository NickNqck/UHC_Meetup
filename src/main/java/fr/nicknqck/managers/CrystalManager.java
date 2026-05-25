package fr.nicknqck.managers;

import fr.nicknqck.GameListener;
import fr.nicknqck.Main;
import fr.nicknqck.entity.bijus.Biju;
import fr.nicknqck.enums.CeintureElements;
import fr.nicknqck.events.custom.GameEndEvent;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.events.custom.particle.ParticleBallGroundEvent;
import fr.nicknqck.events.custom.time.SecondPassEvent;
import fr.nicknqck.events.power.CrystalSearchForPowerEvent;
import fr.nicknqck.interfaces.Buyable;
import fr.nicknqck.interfaces.IElements;
import fr.nicknqck.interfaces.IGotBuyable;
import fr.nicknqck.managers.schem.Schematic;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.crystal.CrystalBase;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.fastinv.PaginatedFastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.*;
import fr.nicknqck.utils.powers.crystal.EauPower;
import fr.nicknqck.utils.powers.crystal.FeuPower;
import fr.nicknqck.utils.powers.crystal.VentPower;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class CrystalManager implements Listener {

    private int timePass = 0;
    private boolean roleGive = false;
    private boolean firstSpawn = false;
    private int timeSpawn = 0;
    private int timeDespawn = 0;
    private boolean inMap = false;
    private final List<Block> blockList;
    private int timeCheckBorder = 0;
    private Villager villager;
    private final Map<UUID, GamePlayer> gettingRefinedList;
    private final Map<UUID, CrystalPower> crystalPowerMap;

    public static final ItemStack crystalItem = new ItemBuilder(Material.EMERALD)
            .setName("§dCrystal")
            .addEnchant(Enchantment.DURABILITY, 1)
            .hideEnchantAttributes()
            .setLore("§7Il parait que le§a forgeron§7 peut§c fabriquer§7 des choses§a très utile§7 avec ceci§7.")
            .toItemStack();

    public static final ItemStack refinedCrystalItem = new ItemBuilder(Material.EMERALD_BLOCK)
            .setName("§dCrystal raffiné")
            .addEnchant(Enchantment.DURABILITY, 1)
            .hideEnchantAttributes()
            .toItemStack();

    public CrystalManager() {
        EventUtils.registerEvents(this);
        removeDyeAndWoolRecipes();
        //Craft du crystal raffiné
        ShapedRecipe recipe = new ShapedRecipe(refinedCrystalItem);
        recipe.shape("EE ", "EE ");
        recipe.setIngredient('E', Material.EMERALD);
        Bukkit.addRecipe(recipe);
        this.blockList = new ArrayList<>();
        this.gettingRefinedList = new HashMap<>();
        this.crystalPowerMap = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockBreak(@NonNull final BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.EMERALD_ORE))return;
        final GamePlayer gamePlayer = GamePlayer.of(event.getPlayer().getUniqueId());
        if (gamePlayer != null) {
            if (gamePlayer.check()) {
                if (gamePlayer.getRole() instanceof CrystalBase) {
                    final Location location = event.getBlock().getLocation();
                    event.getBlock().setType(Material.AIR);
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> GameListener.dropItem(location, crystalItem), 1);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBreak2(@NonNull final BlockBreakEvent event) {
        if (this.blockList.isEmpty())return;
        if (this.blockList.contains(event.getBlock())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cImpossible de casser ce bloc !");
        }
    }
    @EventHandler
    private void onTime(SecondPassEvent event) {
        if (!event.isInGame())return;
        if (!event.getGameState().isRoleAttributed())return;
        if (!roleGive)return;
        if (!Main.getInstance().getGameConfig().getCrystalConfig().isForgeActivated())return;
        this.timePass++;
        if (this.timePass == Main.getInstance().getGameConfig().getCrystalConfig().getTimeForgeFirstSpawn()) {
            spawnForge();
            Bukkit.broadcastMessage("\n§bLa§a forge§b est apparue sur la carte, elle restera à cette endroit pour§c "+ StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getCrystalConfig().getTimeForgeStay()));
        }
        if (this.inMap) {
            this.timeCheckBorder++;
            if (this.timeCheckBorder == 5) {
                this.timeCheckBorder = 0;
                if (isAllForgeBehindBorder()) {
                    this.timeSpawn = Main.getInstance().getGameConfig().getCrystalConfig().getTimeForgeStay()-1;
                }
            }
            if (this.firstSpawn) {
                this.timeSpawn++;
                if (this.timeSpawn == Main.getInstance().getGameConfig().getCrystalConfig().getTimeForgeStay()) {
                    deleteForge();
                    Bukkit.broadcastMessage("\n§bLa§a forge§b se déplace, elle reviendra dans§c "+StringUtils.secondsTowardsBeautiful(Main.getInstance().getGameConfig().getCrystalConfig().getTimeForgeRespawn()));
                }
            }
        } else {
            if (this.firstSpawn) {
                this.timeDespawn++;
                if (this.timeDespawn == Main.getInstance().getGameConfig().getCrystalConfig().getTimeForgeRespawn()) {
                    spawnForge();
                }
            }
        }

    }
    @EventHandler(priority = EventPriority.MONITOR)
    private void onRoleGive(@NonNull final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        this.gettingRefinedList.clear();
        this.timePass = 0;
        this.roleGive = true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onVillagerInteract(@NonNull final PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType().equals(EntityType.VILLAGER)) {
            if (this.villager == null)return;
            if (!this.villager.getUniqueId().equals(event.getRightClicked().getUniqueId()))return;
            event.setCancelled(true);
            final GamePlayer gamePlayer = GamePlayer.of(event.getPlayer().getUniqueId());
            if (gamePlayer == null)return;
            if (!gamePlayer.check()) return;
            new ForgeronInventory(gamePlayer.getRole()).open(event.getPlayer());
        }
    }
    private void removeDyeAndWoolRecipes() {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();

        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();

            if (recipe == null || recipe.getResult() == null) {
                continue;
            }

            ItemStack resultItem = recipe.getResult();
            Material result = resultItem.getType();

            // Supprime tous les crafts de colorants
            if (result == Material.INK_SACK) {
                iterator.remove();
                continue;
            }

            // Supprime tous les crafts de laines
            if (result == Material.WOOL) {
                iterator.remove();
                continue;
            }

            // Empêche la fabrication de blocs d'émeraude
            if (result == Material.EMERALD_BLOCK) {
                iterator.remove();
                continue;
            }

            // Empêche la conversion bloc d'émeraude -> émeraudes
            if (result == Material.EMERALD) {

                if (recipe instanceof ShapelessRecipe) {
                    ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

                    for (ItemStack item : shapeless.getIngredientList()) {
                        if (item == null) {
                            continue;
                        }

                        if (item.getType() == Material.EMERALD_BLOCK) {
                            iterator.remove();
                            break;
                        }
                    }
                }

                if (recipe instanceof ShapedRecipe) {
                    ShapedRecipe shaped = (ShapedRecipe) recipe;

                    for (ItemStack item : shaped.getIngredientMap().values()) {
                        if (item == null) {
                            continue;
                        }

                        if (item.getType() == Material.EMERALD_BLOCK) {
                            iterator.remove();
                            break;
                        }
                    }
                }
            }

            // Supprime aussi les recettes utilisant laine/colorant
            if (recipe instanceof ShapedRecipe) {
                ShapedRecipe shaped = (ShapedRecipe) recipe;

                for (ItemStack item : shaped.getIngredientMap().values()) {
                    if (item == null) {
                        continue;
                    }

                    Material type = item.getType();

                    if (type == Material.WOOL || type == Material.INK_SACK) {
                        iterator.remove();
                        break;
                    }
                }
            }

            if (recipe instanceof ShapelessRecipe) {
                ShapelessRecipe shapeless = (ShapelessRecipe) recipe;

                for (ItemStack item : shapeless.getIngredientList()) {
                    if (item == null) {
                        continue;
                    }

                    Material type = item.getType();

                    if (type == Material.WOOL || type == Material.INK_SACK) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }
    private void spawnForge() {
        final Schematic forge = Main.getInstance().getSchematicManager().getSchematic("forge");
        if (forge != null) {
            this.inMap = true;
            this.firstSpawn = true;
            this.timeSpawn = 0;
            this.timeDespawn = 0;
            final Location randomLocation = Loc.getLocationAtDistance(new Location(Main.getInstance().getWorldManager().getGameWorld(), 0.0, 80.0, 0.0), RandomUtils.getRandomInt(150, 300));
            randomLocation.getChunk().load(true);
            final List<Block> blockList = new ArrayList<>(forge.paste(randomLocation, false));
            this.blockList.clear();
            this.blockList.addAll(blockList);
            for (Block block : blockList) {
                if (block.getType().equals(Material.HAY_BLOCK)) {
                    this.blockList.remove(block);
                    block.setType(Material.AIR);
                    spawnVillager(block.getLocation());
                    break;
                }
            }
            Main.getInstance().debug("La forge est apparue a la position: "+randomLocation);
        } else {
            Main.getInstance().debug("Forge schematic n'existe pas");
        }
    }
    private void deleteForge() {
        this.inMap = false;
        this.timeSpawn = 0;
        //On transforme tout en air pour qu'il n'y est plus rien
        for (Block block : this.blockList) {
            block.setType(Material.AIR);
        }
        if (this.villager!=null) {
            this.villager.remove();
            this.villager=null;
        }
        this.blockList.clear();
        this.timeDespawn = 0;
    }
    private boolean isAllForgeBehindBorder() {
        if (!this.blockList.isEmpty()) {
            final List<Block> list = new ArrayList<>(this.blockList);
            for (Block block : list) {
                if (!Biju.isOutsideOfBorder(block.getLocation())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    private void spawnVillager(@NonNull final Location location) {
        location.add(0.5, 0.0, 0.5);
        final Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.setProfession(Villager.Profession.BLACKSMITH);
        villager.setAdult();
        villager.setAgeLock(true);
        villager.setCustomNameVisible(true);
        villager.setCustomName("§aOscar le Forgeron");
        villager.setMaximumNoDamageTicks(Integer.MAX_VALUE);
        villager.setNoDamageTicks(Integer.MAX_VALUE);
        villager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 200, false, false));
        villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200, false, false));
        this.villager = villager;
        Main.getInstance().debug("Villager spawned");
    }
    @EventHandler
    private void onCraft(@NonNull final CraftItemEvent event) {
        if (event.getRecipe().getResult().isSimilar(refinedCrystalItem)) {
            for (HumanEntity humanEntity : event.getViewers()) {
                if (new ArrayList<>(Arrays.asList(humanEntity.getInventory().getContents())).contains(refinedCrystalItem)) {
                    humanEntity.sendMessage("§cVous avez déjà§d 1 crystal raffiné§c, votre corp ne pourrait pas en supporter plus");
                    event.setCancelled(true);
                } else {
                    final GamePlayer gamePlayer = GamePlayer.of(humanEntity.getUniqueId());
                    if (gamePlayer != null) {
                        if (tryToGiveRefinedCrystal(gamePlayer)) {
                            return;
                        }
                    }
                    event.setCancelled(true);
                    humanEntity.sendMessage("§cImpossible de craft ceci, vous n'êtes pas en jeu !");
                }
            }
        }
    }
    @EventHandler
    private void onMove(@NonNull final PlayerMoveEvent event) {
        if (this.gettingRefinedList.containsKey(event.getPlayer().getUniqueId())) {
            final GamePlayer gamePlayer = this.gettingRefinedList.get(event.getPlayer().getUniqueId());
            if (!gamePlayer.check()) return;
            if (!gamePlayer.getMetaData().containsKey("crystal.percent")) {
                gamePlayer.getMetaData().put("crystal.percent", 0.0);
            } else {
                final double data = (double) gamePlayer.getMetaData().get("crystal.percent");
                final double distance = event.getFrom().distance(event.getTo());
                final double result = data + distance;
                final ItemStack[] inventory = event.getPlayer().getInventory().getContents();
                boolean inside = false;
                for (ItemStack item : inventory) {
                    if (item == null)continue;
                    if (item.isSimilar(refinedCrystalItem)) {
                        inside = item.getAmount() == 1;
                        break;
                    }
                }
                if (!inside) {
                    gamePlayer.getActionBarManager().updateActionBar("crystal.percent", "§cImpossible de raffiné quelque chose que vous n'avez pas");
                    return;
                }
                if (result >= Main.getInstance().getGameConfig().getCrystalConfig().getNmbBlockForRaffinage()) {
                    gamePlayer.getMetaData().remove("crystal.percent");
                    gamePlayer.getActionBarManager().removeInActionBar("crystal.percent");
                    this.gettingRefinedList.remove(event.getPlayer().getUniqueId());
                    event.getPlayer().getInventory().remove(Material.EMERALD_BLOCK);
                    if (!this.crystalPowerMap.containsKey(event.getPlayer().getUniqueId())) {
                        event.getPlayer().sendMessage("§fVous avez reçus le pouvoir du crystal");
                        this.crystalPowerMap.put(event.getPlayer().getUniqueId(), new CrystalPower(gamePlayer.getRole()));
                        doManeuvre(gamePlayer);
                    } else {
                        doManeuvre(gamePlayer);
                    }
                    return;
                }
                gamePlayer.getMetaData().put("crystal.percent", result);
                gamePlayer.getActionBarManager().updateActionBar("crystal.percent", "§bRaffinage:§c "+StringUtils.getCompletionPercent(result, Main.getInstance().getGameConfig().getCrystalConfig().getNmbBlockForRaffinage()));
            }
        }
    }
    private void doManeuvre(GamePlayer gamePlayer) {
        final IElements ceintureElements = this.crystalPowerMap.get(gamePlayer.getUuid()).getCeintureElements();
        if (ceintureElements == null) {
            gamePlayer.sendMessage("§cImpossible d'obtenir un nouveau§d crystal§c.");
            return;
        }
        if (this.crystalPowerMap.get(gamePlayer.getUuid()).ceintures.get(ceintureElements).getMaxUse() == 0) {
            gamePlayer.sendMessage("§7Vous avez obtenu un nouveau§d crystal§7.");
            this.crystalPowerMap.get(gamePlayer.getUuid()).ceintures.get(ceintureElements).setMaxUse(1);
        } else {
            gamePlayer.sendMessage("§7Vous avez renforcé votre§d crystal§7 de "+ceintureElements.getName());
            this.crystalPowerMap.get(gamePlayer.getUuid()).ceintures.get(ceintureElements).setMaxUse(this.crystalPowerMap.get(gamePlayer.getUuid()).ceintures.get(ceintureElements).getMaxUse()+1);
        }
    }
    @EventHandler
    private void onDrop(@NonNull final PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().isSimilar(refinedCrystalItem)) {
            event.getPlayer().sendMessage("§7Le§d crystal raffiné§7 est trop§c instable§7 pour être§c jeter§7.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onSearch(@NonNull final CrystalSearchForPowerEvent event) {
        if (event.getElement().equals(CeintureElements.FEU)) {
            event.setPower(new FeuPower(event.getRole()));
        } else if (event.getElement().equals(CeintureElements.EAU)) {
            event.setPower(new EauPower(event.getRole()));
        } else if (event.getElement().equals(CeintureElements.VENT)) {
            event.setPower(new VentPower(event.getRole()));
        }
    }
    @EventHandler
    private void onEndGame(@NonNull final GameEndEvent event) {
        deleteForge();
        this.timeSpawn = 0;
        this.roleGive = false;
        this.firstSpawn = false;
        this.inMap = false;
        this.blockList.clear();
        this.timeCheckBorder = 0;
        this.villager = null;
        this.gettingRefinedList.clear();
        this.crystalPowerMap.clear();
    }

    public boolean tryToGiveRefinedCrystal(@NonNull final GamePlayer gamePlayer) {
        if (!gamePlayer.check()) {
            return false;
        }
        this.gettingRefinedList.put(gamePlayer.getUuid(), gamePlayer);
        return true;
    }

    private static final class CrystalPower extends ItemPower implements Listener{

        private final Map<IElements, ElementalPower> ceintures;
        private ElementalPower equippedCrystal = null;

        private CrystalPower(@NonNull RoleBase role) {
            super("§fPouvoir du crystal", new Cooldown(1), new ItemBuilder(Material.NETHER_STAR).setName("§fPouvoir du crystal"), role);
            setShowCdInDesc(false);
            setSendCooldown(false);
            role.addPower(this, true);
            this.ceintures = new HashMap<>();
            for (IElements ceintureElements : getPlugin().getGameConfig().getCrystalConfig().getElements()) {
                final CrystalSearchForPowerEvent event = new CrystalSearchForPowerEvent(ceintureElements, role);
                getPlugin().getServer().getPluginManager().callEvent(event);
                if (event.getPower() != null) {
                    this.ceintures.put(ceintureElements, event.getPower());
                }
            }
            getShowCdRunnable().setCustomText(true);
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                getShowCdRunnable().setCustomText(true);
                if (event.getAction().name().contains("RIGHT")) {
                    new CrystalInventory(this).open(player);
                    return true;
                } else {
                    if (this.equippedCrystal == null) {
                        player.sendMessage("§cPour équiper un§d crystal§c il faut faire§f clique droit§c.");
                        return false;
                    }
                    return this.equippedCrystal.checkUse(player, map);
                }
            }
            return false;
        }

        public IElements getCeintureElements() {
            int random = RandomUtils.getRandomInt(0, this.getPlugin().getGameConfig().getCrystalConfig().getElements().size());
            int i = 0;
            for (IElements ceintureElements : this.getPlugin().getGameConfig().getCrystalConfig().getElements()) {
                Main.getInstance().debug("Elements detected: " + ceintureElements+", id: "+i+", rdm =" + random);
                if (i == random) {
                    return ceintureElements;
                }
                i++;
            }
            return null;
        }

        @Override
        public void tryUpdateActionBar() {
            getShowCdRunnable().setCustomTexte(this.equippedCrystal == null ?
                    "§fIl faut équiper un§d crystal§f." :
                    this.equippedCrystal.getElement().getName()+"§f "+(
                            this.equippedCrystal.getCooldown().isInCooldown() ?
                                                                      "est en cooldown:§c "+StringUtils.secondsTowardsBeautiful(this.equippedCrystal.getCooldown().getCooldownRemaining())
                            :
                            this.equippedCrystal.getUse() < this.equippedCrystal.getMaxUse()
                            ?
                            "est§a utilisable"
                            :
                            "a atteint son§c utilisation maximal§f."
                    )
            );
        }
        @EventHandler
        private void onBallImpact(@NonNull final ParticleBallGroundEvent event) {
            if (!event.getShooterUUID().equals(getRole().getPlayer())) {return;}
            final Player owner = Bukkit.getPlayer(event.getShooterUUID());
            if (owner == null) {return;}
            final Map<IElements, ElementalPower> copy = new HashMap<>(this.ceintures);
            for (IElements iElements : copy.keySet()) {
                if (copy.get(iElements) == null)continue;
                if (copy.get(iElements).getBallName().equalsIgnoreCase(event.getBallName())) {
                    this.ceintures.get(iElements).onImpact(event.getImpactLocation(), event.getHitPlayer());
                    break;
                }
            }
        }

        private static final class CrystalInventory extends PaginatedFastInv {

            public CrystalInventory(@NonNull final CrystalPower power) {
                super(27, "§fChoix du§d crystal");
                setItems(getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setDurability(7).toItemStack());
                final List<Integer> integerList = new ArrayList<>();
                for (int i =10; i <= 16; i++) {
                    integerList.add(i);
                }
                setContentSlots(integerList);
                for (IElements ceintureElements : power.getPlugin().getGameConfig().getCrystalConfig().getElements()) {
                    if (!power.ceintures.containsKey(ceintureElements)) {continue;}
                    if (power.equippedCrystal != null) {
                        if (power.equippedCrystal.getElement().equals(ceintureElements)) {
                            addContent(new ItemBuilder(Material.INK_SACK)
                                    .setDurability(ceintureElements.getDyeColor())
                                    .setName(ceintureElements.getName())
                                    .addEnchant(Enchantment.DURABILITY, 1)
                                    .hideEnchantAttributes()
                                    .setLore(ceintureElements.getDescription())
                                    .addLoreLine("")
                                    .addLoreLine("§7Actuellement équiper")
                                    .toItemStack());
                            continue;
                        }
                    }
                    addContent(new ItemBuilder(Material.INK_SACK)
                            .setDurability(ceintureElements.getDyeColor())
                            .setName(ceintureElements.getName())
                            .setLore(ceintureElements.getDescription())
                            .toItemStack(), event -> {
                        power.equippedCrystal = power.ceintures.get(ceintureElements);
                        if (!power.getRole().getPowers().contains(power.equippedCrystal)) {
                            power.getRole().addPower(power.equippedCrystal);
                        }
                        event.getWhoClicked().closeInventory();
                    });
                }
                previousPageItem(3, new ItemBuilder(Material.WOOD_BUTTON)
                        .setName("§7◄ Page précédente").toItemStack());
                nextPageItem(5, new ItemBuilder(Material.WOOD_BUTTON)
                        .setName("§7Page suivante ►").toItemStack());
            }
        }
    }
    private static final class ForgeronInventory extends FastInv {

        private final RoleBase role;

        public ForgeronInventory(@NonNull final RoleBase role) {
            super(27, "§aOscar le Forgeron");
            this.role = role;
            setItems(getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).toItemStack());
            if (!role.getGamePlayer().getMetaData().containsKey("crystal.forge2")) {
                if (role instanceof IGotBuyable) {
                    setItem(13, ((IGotBuyable) role).getBuyable().getMenuItem(), event-> {
                        if (!(event.getWhoClicked() instanceof Player))return;
                        final Buyable buyable = ((IGotBuyable) role).getBuyable();
                        if (GlobalUtils.getItemAmount((Player) event.getWhoClicked(), CrystalManager.crystalItem) >= buyable.getCrystalCost()) {
                            Power power = buyable.createPower(this.role);
                            if (power instanceof ItemPower) {
                                role.addPower((ItemPower) power, true);
                            } else {
                                role.addPower(power);
                            }
                            role.getGamePlayer().getMetaData().put("crystal.forge2", power);
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§7[§aOscar le Forgeron§7]§f Merci de m'avoir acheté le pouvoir \"§c"+buyable.getMenuItem().getItemMeta().getDisplayName()+"§f\".");
                        } else {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§7[§aOscar le Forgeron§7]§c A quoi tu joue ? Tu es trop pauvre pour m'acheter ça !");
                        }
                    });
                } else {
                    setItem(13, new CommonBuyablePower().getMenuItem(), event-> {
                        if (!(event.getWhoClicked() instanceof Player))return;
                        final Buyable buyable = new CommonBuyablePower();
                        if (GlobalUtils.getItemAmount((Player) event.getWhoClicked(), CrystalManager.crystalItem) >= buyable.getCrystalCost()) {
                            Power power = buyable.createPower(this.role);
                            if (power instanceof ItemPower) {
                                role.addPower((ItemPower) power, true);
                            } else {
                                role.addPower(power);
                            }
                            role.getGamePlayer().getMetaData().put("crystal.forge2", power);
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§7[§aOscar le Forgeron§7]§f Merci de m'avoir acheté le pouvoir \"§c"+buyable.getMenuItem().getItemMeta().getDisplayName()+"§f\".");
                        } else {
                            event.getWhoClicked().closeInventory();
                            event.getWhoClicked().sendMessage("§7[§aOscar le Forgeron§7]§c A quoi tu joue ? Tu es trop pauvre pour m'acheter ça !");
                        }
                    });
                }
            } else {
                setItem(13, new ItemBuilder(Material.BARRIER).setName("§cArticle déjà acheter").toItemStack());
            }
        }
    }
}