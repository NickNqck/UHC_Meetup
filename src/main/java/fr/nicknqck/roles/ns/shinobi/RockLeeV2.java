package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.DoubleCircleEffect;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class RockLeeV2 extends ShinobiRoles implements Listener {

    private final TextComponent desc;
    private final ItemStack troisPorteItem = new ItemBuilder(Material.NETHER_STAR).setName("§aTroisième Porte").setUnbreakable(true).setDroppable(false).toItemStack();
    private final ItemStack sixPorteItem = new ItemBuilder(Material.NETHER_STAR).setName("§aSixième Porte").setUnbreakable(true).setDroppable(false).toItemStack();
    private final ItemStack huitPorteItem = new ItemBuilder(Material.NETHER_STAR).setName("§aHuitième Porte").setUnbreakable(true).setDroppable(false).toItemStack();
    private final ItemStack sakeItem = new ItemBuilder(Material.GLASS_BOTTLE).setName("§aAlcoolique no Jutsu").setUnbreakable(true).setDroppable(false).toItemStack();
    private int cdTrois, cdSix, cdSake;
    private boolean huitUsed = false;

    public RockLeeV2(UUID player) {
        super(player);
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.setItems(new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous donne l'effet§b Speed I§7 pendant§c 1m30s§7.\n\n§7Coût:§c 1" + AllDesc.coeur + "§7 (non permanent)")}),
                "§aTroisième Porte",
                90
                ),
        new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous donne les effets§b Speed I§7 et§c Force I§7 pendant§c 3 minutes§7.\n\n§7Coût:§c 1❤ permanent")}),
                "§aSixième Porte",
                180
        ),
        new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous donne les effets§b Speed II§7,§c Force I§7,§9 Résistance I§7 et§6 Fire Résistance I§7 pendant§c 5 minutes§7, également, vous obtiendrez§c 15❤§7 permanent.\n\n§7Coût: (Après§c 5 minutes§7) Vous fait tomber à§c 5❤ permanent§7, après ceci vous ne pourrez plus utiliser vos§a portes§7.")}),
                "§aHuitième Porte",
                -500
        ),
        new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous permet d'obtenir l'effet§b Speed I§7 pendant§c 1 minute§7, puis, vous obtiendrez§c 15 secondes§7 de§2 nausé§7. (1x/3m)")}),
                "§aAlcoolique no Jutsu",
                60*3
        ));
        this.desc = desc.getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        EventUtils.registerEvents(this);
        giveItem(owner, false, getItems());
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public TextComponent getComponent() {
        return desc;
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                this.troisPorteItem,
                this.sixPorteItem,
                this.huitPorteItem,
                this.sakeItem
        };
    }

    @Override
    public String getName() {
        return "Rock Lee";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.RockLee;
    }

    @Override
    public void resetCooldown() {
        cdTrois = 0;
        cdSix = 0;
        huitUsed = false;
        cdSake = 0;
    }

    @Override
    public void Update(GameState gameState) {
        if (cdTrois >= 0) {
            cdTrois--;
            if (cdTrois == 0) {
                owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§a Troisième Porte§7.");
            }
        }
        if (cdSix >= 0) {
            cdSix--;
            if (cdSix == 0) {
                owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§a Sixième Porte§7.");
            }
        }
        if (cdSake >= 0) {
            cdSake--;
            if (cdSake == 0) {
                owner.sendMessage("§7Vous pouvez à nouveau boire de l'alcool.");
            }
        }
    }

    @EventHandler
    private void EndGameEvent(EndGameEvent event) {
        EventUtils.unregisterEvents(this);
    }
    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        if (event.isCancelled()) return;
        if (!event.getPlayer().getUniqueId().equals(getPlayer())) return;
        ItemStack item = event.getItem();
        if (item.isSimilar(this.troisPorteItem)) {
            event.setCancelled(true);
            if (cdTrois > 0) {
                sendCooldown(event.getPlayer(), cdTrois);
                return;
            }
            if (huitUsed) {
                event.getPlayer().sendMessage("§cVous avez déjà utiliser toute votre espérence de vie");
                return;
            }
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 + 30, 0, false, false), true);
            if (event.getPlayer().getHealth() - 2.0 <= 0.0) {
                event.getPlayer().setHealth(1.0);
            } else {
                event.getPlayer().setHealth(event.getPlayer().getHealth() - 2.0);
            }
            cdTrois = 90;
        }
        if (item.isSimilar(this.sixPorteItem)) {
            event.setCancelled(true);
            if (cdSix > 0) {
                sendCooldown(owner, cdSix);
                return;
            }
            if (huitUsed) {
                event.getPlayer().sendMessage("§cVous avez déjà utiliser toute votre espérence de vie");
                return;
            }
            if (getMaxHealth() - 2.0 <= 0) {
                event.getPlayer().sendMessage("§cVous ne pouvez plus utiliser cette technique !");
                return;
            }
            cdSix = 180;
            setMaxHealth(getMaxHealth()-2.0);
            owner.setMaxHealth(getMaxHealth());
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 *3, 0, false, false), true);
            owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 3, 0, false, false), true);
            new DoubleCircleEffect(20*60*3, EnumParticle.VILLAGER_HAPPY).start(owner);
        }
        if (item.isSimilar(this.huitPorteItem)) {
            event.setCancelled(true);
            if (huitUsed) {
                event.getPlayer().sendMessage("§cVous avez déjà utiliser toute votre espérence de vie");
                return;
            }
            new DoubleCircleEffect(20*60*5, EnumParticle.REDSTONE).start(owner);
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 1, false, false), true);
            owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 1, false, false), true);
            owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*5, 1, false, false), true);
            owner.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*60*5, 1, false, false), true);
            setMaxHealth(30.0);
            owner.setMaxHealth(getMaxHealth());
            owner.setHealth(owner.getMaxHealth());
            cdSix = -1;
            cdTrois = -1;
            huitUsed = true;
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                if (Bukkit.getPlayer(getPlayer()) != null) {
                    Player owner = Bukkit.getPlayer(getPlayer());
                    if (gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                        if (!gameState.hasRoleNull(owner)) {
                            if (gameState.getGamePlayer().get(owner.getUniqueId()).getRole() instanceof RockLeeV2) {
                                if (gameState.getGamePlayer().get(owner.getUniqueId()).getRole().StringID.equals(StringID)) {//donc c'est définitivement la même partie que quand il a activer
                                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                        setMaxHealth(10.0);
                                        owner.setMaxHealth(getMaxHealth());
                                        owner.setHealth(owner.getHealth());
                                    });
                                }
                            }
                        }
                    }
                }
            }, 20*60*5);
        }
        if (item.isSimilar(this.sakeItem)) {
            event.setCancelled(true);
            if (cdSake > 0) {
                sendCooldown(owner, cdSake);
                return;
            }
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60, 0, false, false), true);
            cdSake = 60*3;
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> owner.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*15, 0, false, false), true), 20*60);
        }
    }
}