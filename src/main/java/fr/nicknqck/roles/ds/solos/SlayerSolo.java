package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.items.Items;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.MathUtil;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SlayerSolo extends DemonsSlayersRoles {

    private final ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).setName("§aLame de Pourfendeur").addEnchant(Enchantment.DAMAGE_ALL, 4).setUnbreakable(true).setDroppable(false).toItemStack();
    private TextComponent desc;

    public SlayerSolo(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new EauPower(this), true);
        addPower(new VentPower(this), true);
        addPower(new FoudrePower(this), true);
        addPower(new FeuPower(this), true);
        addPower(new RochePower(this), true);
        givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999, 0), EffectWhen.PERMANENT);
        setMaxHealth(24.0);
        owner.setMaxHealth(getMaxHealth());
        owner.setHealth(owner.getMaxHealth());
        AutomaticDesc automaticDesc = new AutomaticDesc(this)
                .addEffects(getEffects())
                .addCustomLine("§7Vous possédez§c 2❤ permanent§7 supplémentaire.")
                .setItems(
          new TripleMap<>(
                  new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                          new TextComponent("§7Épée en§b diamant§7 enchanté§c tranchant IV")
                  }),
                  "§aLame de Pourfendeur",
                  0
          ),
          new TripleMap<>(
                  new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                       new TextComponent("§7Vous donne l'effet§e Speed I§7 et enchante vos bottes§b Depht Strider III§7 pendant§c 3 minutes")
                  }),
                  "§bSoufle de l'Eau",
                  60*3
          ),
          new TripleMap<>(
                  new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                          new TextComponent("§7Vous donne l'effet§e Speed II§7 et§a No Fall§7 pendant§c 2 minutes")
                  }),
                  "§aSoufle du Vent",
                  60*3
          ),
          new TripleMap<>(
                  new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                      new TextComponent("§7Déclanche un§c timer§7 de§c 60 secondes§7, durant lesquelles le §cpremier§7 coup porter à chaque§c joueur\n" +
                              "§7infligera§c 2❤§7 supplémentaire ainsi que l'effet§8 Slowness I§7 pendant§c 15 secondes§7.")
                  }),
                  "§eSoufle de la Foudre",
                  60*5
        ),
        new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {
                        new TextComponent("§7Vous donne l'effet§6 Résistance au feu§7 et§c 20%§7 de§c chance§7 de§6 bruler§7 les joueurs que vous frappez pendant§c 3 minutes§7.")
                }),
                "§cSoufle du Feu",
                60*3
        ),
        new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                    new TextComponent("§7En visant un joueur, permet de l'enfermer dans une boule de pierre\n" +
                            "§7et de vous §ctéléportez§7 au dessus de cette dernière, également, vous donne§c 10 secondes§7 de§6 Haste II§7.")
                }),
                "§8Soufle de la Roche",
                60*8-15
        )
                );
        this.desc = automaticDesc.getText();
    }

    @Override
    public TextComponent getComponent() {
        return this.desc;
    }

    @Override
    public void GiveItems() {
        giveItem(owner, false, getItems());
        giveItem(owner, false, Items.getLamedenichirin());
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Pourfendeur Solitaire";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.SlayerSolo;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[] {
                sword
        };
    }
    private static class FoudrePower extends ItemPower implements Listener {

        private boolean using = false;
        private final List<UUID> taped = new ArrayList<>();

        protected FoudrePower(RoleBase role) {
            super("§eSoufle de la Foudre", new Cooldown(60*6), new ItemBuilder(Material.GLOWSTONE_DUST).setName("§eSoufle de la Foudre"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                this.using = true;
                player.sendMessage("§7Votre§e Foudre§7 est prête, vous avez maintenant§c 60 secondes§7 pour l'utiliser sur un§c joueur§7.");
                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                    this.using = false;
                    player.sendMessage("§7Vous ne ressentez plus la§e Foudre§7 dans votre corp.");
                    EventUtils.unregisterEvents(this);
                }, 20*60);
                EventUtils.registerEvents(this);
                return true;
            }
            return false;
        }
        @EventHandler
        private void onDamage(UHCPlayerBattleEvent event) {
            if (!event.getDamager().getUuid().equals(getRole().getPlayer()))return;
            if (this.using) {
                if (this.taped.contains(event.getVictim().getUuid()))return;
                Player victim = event.getVictim().getRole().owner;
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*15, 0, false, false));
                if (victim.getHealth() - 4.0 <= 0.0) {
                     victim.damage(9999.0, event.getOriginEvent().getDamager());
                } else {
                    victim.setHealth(victim.getHealth()-4.0);
                    victim.damage(0.0);
                }
                victim.sendMessage("§7Vous subissez une§e foudre§c très puissante§7.");
                this.taped.add(event.getVictim().getUuid());
            }
        }
    }
    private static class VentPower extends ItemPower implements Listener{

        protected VentPower(RoleBase role) {
            super("§aSoufle du vent", new Cooldown(60*5), new ItemBuilder(Material.FEATHER).setName("§aSoufle du Vent"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*120, 1, false, false), true);
                player.sendMessage("§7Vous activez votre §aSoufle du Vent");
                EventUtils.registerEvents(this);
                Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                    assert player.isOnline();
                    player.sendMessage("§7Votre§a Soufle du Vent§7 est maintenant désactiver");
                    EventUtils.unregisterEvents(this);
                },20*120);
                return true;
            }
            return false;
        }
        @EventHandler
        private void onFall(EntityDamageEvent event) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                if (event.getEntity().getUniqueId().equals(getRole().getPlayer())) {
                    event.setCancelled(true);
                }
            }
        }
    }
    private static class EauPower extends ItemPower implements Listener {

        protected EauPower(RoleBase role) {
            super("§bSoufle de l'Eau", new Cooldown(60*6), new ItemBuilder(Material.NETHER_STAR).setName("§bSoufle de l'Eau"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Vous activez le§b Soufle de l'Eau");
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*180, 0, false, false));
                ItemStack boots = player.getInventory().getBoots();
                if (boots != null) {
                    ItemMeta mBoots = boots.getItemMeta();
                    if (mBoots != null) {
                        mBoots.addEnchant(Enchantment.DEPTH_STRIDER, 3, true);
                        boots.setItemMeta(mBoots);
                        player.getInventory().setBoots(boots);
                        Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                            ItemStack boot = player.getInventory().getBoots();
                            if (boot != null) {
                                ItemMeta mBoot = boot.getItemMeta();
                                if (mBoot == null)return;
                                if (mBoot.hasEnchant(Enchantment.DEPTH_STRIDER)) {
                                    mBoot.removeEnchant(Enchantment.DEPTH_STRIDER);
                                    boot.setItemMeta(mBoot);
                                    player.getInventory().setBoots(boot);
                                    player.sendMessage("§7Votre§b Soufle de l'Eau§7 ne fais plus effet");
                                }
                            }
                        }, 20*180);
                    }
                }
                return true;
            }
            return false;
        }
        @EventHandler
        private void onDie(UHCDeathEvent event) {
            if (event.getRole() == null)return;
            if (event.getRole().getPowers().contains(this)) {
                if (event.getPlayer().getUniqueId().equals(getRole().getPlayer())) {
                    ItemStack boots = event.getPlayer().getInventory().getBoots();
                    if (boots != null) {
                        if (!boots.hasItemMeta())return;
                        if (boots.getItemMeta().hasEnchant(Enchantment.DEPTH_STRIDER)) {
                            boots.getItemMeta().removeEnchant(Enchantment.DEPTH_STRIDER);
                        }
                    }
                }
            }
        }
    }
    private static class FeuPower extends ItemPower implements Listener{

        protected FeuPower(RoleBase role) {
            super("§cSoufle du Feu", new Cooldown(60*6), new ItemBuilder(Material.BLAZE_ROD).setName("§cSoufle du Feu"), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                player.sendMessage("§7Activation du§c Soufle du Feu");
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*180, 0, false, false));
                EventUtils.registerEvents(this);
                Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                    player.sendMessage("§7Votre§c Soufle du Feu§7 est maintenant§c désactiver");
                    EventUtils.unregisterEvents(this);
                }, 20*180);
                return true;
            }
            return false;
        }
        @EventHandler
        private void onBaston(UHCPlayerBattleEvent event) {
            if (event.getDamager().getUuid().equals(getRole().getPlayer())) {
                if (Main.RANDOM.nextInt(100) <= 20) {
                    event.getOriginEvent().getEntity().setFireTicks(20*20);
                }
            }
        }
    }
    private static class RochePower extends ItemPower {

        protected RochePower(RoleBase role) {
            super("§8Soufle de la Roche", new Cooldown(60*8), new ItemBuilder(Material.STONE_AXE).setName("§8Soufle de la Roche").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideEnchantAttributes(), role);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                Player target = getRole().getTargetPlayer(player, 25);
                if (target != null) {
                    GamePlayer gamePlayer = getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                    if (gamePlayer == null) {
                        player.sendMessage("§c"+target.getDisplayName()+"§7 n'est pas en jeu");
                        return false;
                    }
                    gamePlayer.stun(10);
                    final Set<Location> sphere = new MathUtil().sphere(target.getLocation(), 5, true);
                    for (final Location loc : sphere) {
                        loc.getBlock().setType(Material.STONE);
                    }
                    Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                        for (final Location loc : sphere) {
                            loc.getBlock().setType(Material.AIR);
                        }
                        assert player.isOnline();
                        player.sendMessage("§7Votre§8 Soufle de la Roche§7 s'arrête maintenant");
                    }, 20*15);
                    player.teleport(new Location(target.getWorld(), target.getLocation().getX(), target.getLocation().getY()+6.0, target.getLocation().getZ()));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20*15, 1, false, false));
                    player.sendMessage("§7Vous avez utiliser votre§8 Soufle de la Roche");
                    return true;
                } else {
                    player.sendMessage("§cIl faut viser un joueur");
                    return false;
                }
            }
            return false;
        }
    }
}