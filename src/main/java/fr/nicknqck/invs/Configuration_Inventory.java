package fr.nicknqck.invs;

import fr.nicknqck.Main;
import fr.nicknqck.items.GUIItems;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Configuration_Inventory extends FastInv {

    public Configuration_Inventory() {
        super(54, "§fConfiguration§7 ->§6 Inventaire");
        setItem(7, new ItemBuilder(Material.BANNER)
                .setDurability(10)
                .setName("§aConfiguration de l'inventaire")
                .setLore(
                        "§7Vous permet de modifier l'inventaire à votre guise l'inventaire de départ,",
                        "",
                        "§7Si vous faite§f Shift + Clique§7 vous pourrez remettre l'inventaire par défaut (votre édition sera sauvegarder)."
                )
                .toItemStack(), event -> {
            if (event.isShiftClick()) {
                if (Main.getInstance().getGameConfig().getStuffConfig().isDefaultInventory()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setDefaultInventory(false);
                    Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+"§c "+event.getWhoClicked().getName()+"§7 a modifié l'inventaire, il n'est§c plus§7 définie sur§c par défaut§7.");
                    new Configuration_Inventory().open((Player) event.getWhoClicked());
                } else {
                    Main.getInstance().getGameConfig().getStuffConfig().setDefaultInventory(true);
                    Main.getInstance().sendMessageToHosts(Main.getInstance().getNAME()+"§c "+event.getWhoClicked().getName()+"§7 a modifié l'inventaire, il est§c maintenant§7 définie sur§c par défaut§7.");
                    new Configuration_Inventory().open((Player) event.getWhoClicked());
                }
                return;
            }
            event.getWhoClicked().closeInventory();
            if (event.getWhoClicked() instanceof Player) {
                ((Player) event.getWhoClicked()).performCommand("a invconfig start");
            }
        });
        setItem(8, GUIItems.getSelectBackMenu(), event -> {
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().openInventory(Bukkit.createInventory(null, 54, "§7(§c!§7)§f Configuration"));
                    Main.getInstance().getInventories().updateAdminInventory((Player) event.getWhoClicked());
                }
        );
        if (Main.getInstance().getGameConfig().getStuffConfig().isDefaultInventory()) {
            setItem(48, new ItemBuilder(Material.GOLDEN_APPLE, Main.getInstance().getGameConfig().getStuffConfig().getNmbGap()).setName("§fNombre de pomme d'§eor").setLore(
                            "§fClique gauche:§a +1",
                            "§fClique droit:§c -1",
                            "§fMontant actuel:§e "+Main.getInstance().getGameConfig().getStuffConfig().getNmbGap())
                    .toItemStack(), event -> {
                if (event.isLeftClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setNmbGap(Math.min(Main.getInstance().getGameConfig().getStuffConfig().getNmbGap() + 1, 64));
                }
                if (event.isRightClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setNmbGap(Math.max(Main.getInstance().getGameConfig().getStuffConfig().getNmbGap() - 1, Main.getInstance().getGameConfig().getStuffConfig().getMinGap()));
                }
                new Configuration_Inventory().open((Player) event.getWhoClicked());
            });
            setItem(0, new ItemBuilder(Material.DIAMOND_HELMET).setLore(
                            "§fClique gauche: §a+1",
                            "§fClique droit: §c-1",
                            "§fNiveau de protection:§b "+Main.getInstance().getGameConfig().getStuffConfig().getProtectionHelmet()
                    ).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .toItemStack(), event -> {
                if (event.isLeftClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionHelmet(Math.min(4, Main.getInstance().getGameConfig().getStuffConfig().getProtectionHelmet()+1));
                }
                if (event.isRightClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionHelmet(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getProtectionHelmet()-1));
                }
                new Configuration_Inventory().open((Player) event.getWhoClicked());
            });
            setItem(9, new ItemBuilder(Material.DIAMOND_CHESTPLATE).setLore(
                            "§fClique gauche: §a+1",
                            "§fClique droit: §c-1",
                            "§fNiveau de protection:§b "+Main.getInstance().getGameConfig().getStuffConfig().getProtectionChestplate()
                    ).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .toItemStack(), event -> {
                if (event.isLeftClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionChestplate(Math.min(4, Main.getInstance().getGameConfig().getStuffConfig().getProtectionChestplate()+1));
                }
                if (event.isRightClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionChestplate(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getProtectionChestplate()-1));
                }
                new Configuration_Inventory().open((Player) event.getWhoClicked());
            });
            setItem(18, new ItemBuilder(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .setLore(
                            "§fClique gauche; §a+1",
                            "§fClique droit: §c-1",
                            "§fNiveau de protection:§b "+Main.getInstance().getGameConfig().getStuffConfig().getProtectionLeggings())
                    .toItemStack(), event -> {
                if (event.isLeftClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionLeggings(Math.min(4, Main.getInstance().getGameConfig().getStuffConfig().getProtectionLeggings()+1));
                }
                if (event.isRightClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionLeggings(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getProtectionLeggings()-1));
                }
                new Configuration_Inventory().open((Player) event.getWhoClicked());
            });
            setItem(27, new ItemBuilder(Material.DIAMOND_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .setLore(
                            "§fClique gauche: §a+1",
                            "§fClique droit: §c-1",
                            "§fNiveau de protection:§b "+Main.getInstance().getGameConfig().getStuffConfig().getProtectionBoost())
                    .toItemStack(), event -> {
                if (event.isLeftClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionBoost(Math.min(4, Main.getInstance().getGameConfig().getStuffConfig().getProtectionBoost()+1));
                }
                if (event.isRightClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setProtectionBoost(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getProtectionBoost()-1));
                }
                new Configuration_Inventory().open((Player) event.getWhoClicked());
            });
            setItem(45, new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, Main.getInstance().getGameConfig().getStuffConfig().getSharpness()).setLore(
                    "§fClique gauche:§a +1 niveau",
                    "§fClique droit:§c -1 niveau",
                    "§fNiveau de tranchant:§b "+Main.getInstance().getGameConfig().getStuffConfig().getSharpness()
            ).toItemStack(), event -> {
                if (event.isLeftClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setSharpness(Math.min(5, Main.getInstance().getGameConfig().getStuffConfig().getSharpness()+1));
                }
                if (event.isRightClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setSharpness(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getSharpness()-1));
                }
                new Configuration_Inventory().open((Player) event.getWhoClicked());
            });
            setItem(46, new ItemBuilder(Material.BRICK).setAmount(Main.getInstance().getGameConfig().getStuffConfig().getNmbblock())
                    .setLore(
                            "§fClique gauche:§a + 1 stack",
                            "§fClique droit:§c -1 stack",
                            "§fNombre de stack actuel: "+Main.getInstance().getGameConfig().getStuffConfig().getNmbblock(),
                            "§fNombre de blocs total:§c "+(64*Main.getInstance().getGameConfig().getStuffConfig().getNmbblock())
                    ).toItemStack(), event -> {
                if (event.isLeftClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setNmbblock(Math.min(4, Main.getInstance().getGameConfig().getStuffConfig().getNmbblock()+1));
                }
                if (event.isRightClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setNmbblock(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getNmbblock()-1));
                }
                new Configuration_Inventory().open((Player) event.getWhoClicked());
            });
            setItem(47, new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, Main.getInstance().getGameConfig().getStuffConfig().getPower())
                    .setLore(
                            "§fClique gauche:§a + 1 niveau",
                            "§fClique droit:§c - 1 niveau",
                            "§fNiveau actuel de puissance:§b "+Main.getInstance().getGameConfig().getStuffConfig().getPower()
                    )
                    .toItemStack(), event -> {
                if (event.isLeftClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setPower(Math.min(5, Main.getInstance().getGameConfig().getStuffConfig().getPower()+1));
                }
                if (event.isRightClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setPower(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getPower()-1));
                }
                new Configuration_Inventory().open((Player) event.getWhoClicked());
            });
            setItem(49, new ItemBuilder(Material.ENDER_PEARL).setAmount(Main.getInstance().getGameConfig().getStuffConfig().getPearl())
                    .setLore(
                            "§fClique gauche:§a +1",
                            "§fClique droit:§c -1",
                            "§fNombre de perles de l'ender:§c "+Main.getInstance().getGameConfig().getStuffConfig().getPearl()
                    )
                    .toItemStack(), event -> {
                if (event.isLeftClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setPearl(Math.min(4, Main.getInstance().getGameConfig().getStuffConfig().getPearl()+1));
                }
                if (event.isRightClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setPearl(Math.max(0, Main.getInstance().getGameConfig().getStuffConfig().getPearl()-1));
                }
                new Configuration_Inventory().open((Player) event.getWhoClicked());
            });
            setItem(50, GUIItems.getGoldenCarrot());
            setItem(51, new ItemBuilder(Material.LAVA_BUCKET)
                    .setAmount(Main.getInstance().getGameConfig().getStuffConfig().getLave())
                    .setLore(
                            "§fClique gauche:§a +1",
                            "§fClique droit:§c -1",
                            "§fNombre de seau de lave:§c "+Main.getInstance().getGameConfig().getStuffConfig().getLave()
                    )
                    .toItemStack(), event -> {
                if (event.isLeftClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setLave(Math.min(4, Main.getInstance().getGameConfig().getStuffConfig().getLave()+1));
                }
                if (event.isRightClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setLave(Math.max(0, Main.getInstance().getGameConfig().getStuffConfig().getLave()-1));
                }
                new Configuration_Inventory().open((Player) event.getWhoClicked());
            });
            setItem(52, new ItemBuilder(Material.WATER_BUCKET)
                    .setAmount(Main.getInstance().getGameConfig().getStuffConfig().getEau())
                    .setLore(
                            "§fClique gauche:§a +1",
                            "§fClique droit:§c -1",
                            "§fNombre de seau d'eau:§c "+Main.getInstance().getGameConfig().getStuffConfig().getLave()
                    )
                    .toItemStack(), event -> {
                if (event.isLeftClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setEau(Math.min(4, Main.getInstance().getGameConfig().getStuffConfig().getEau()+1));
                }
                if (event.isRightClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setEau(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getEau()-1));
                }
                new Configuration_Inventory().open((Player) event.getWhoClicked());
            });
            setItem(38, new ItemBuilder(Material.ARROW, Main.getInstance().getGameConfig().getStuffConfig().getNmbArrow()).setName("§fFlèches")
                    .toItemStack(), event -> {
                if (event.isLeftClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setNmbArrow(Math.min(64, Main.getInstance().getGameConfig().getStuffConfig().getNmbArrow()+1));
                }
                if (event.isRightClick()) {
                    Main.getInstance().getGameConfig().getStuffConfig().setNmbArrow(Math.max(1, Main.getInstance().getGameConfig().getStuffConfig().getNmbArrow()-1));
                }
                new Configuration_Inventory().open((Player) event.getWhoClicked());
            });
            //inv.setItem(9, GUIItems.getx());
        } else {
            for (final Integer place : Main.getInstance().getGameConfig().getStuffConfig().getStartInventoryMap().keySet()) {
                final ItemStack item = Main.getInstance().getGameConfig().getStuffConfig().getStartInventoryMap().get(place);
                setItem(place + 18, item);
            }
        }

    }

}