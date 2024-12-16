package fr.nicknqck.roles.ns.shinobi.porte;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.ShinobiRoles;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.particles.DoubleCircleEffect;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public abstract class PortesRoles extends ShinobiRoles implements Listener {

    private boolean huitUsed = false;

    public PortesRoles(UUID player) {
        super(player);
        setChakraType(getRandomChakras());

    }
    @Override
    public void resetCooldown() {
        huitUsed = false;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }
    public TripleMap<HoverEvent, String, Integer> troisPorteMap() {
        return new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous donne l'effet§b Speed I§7 pendant§c 1m30s§7.\n\n§7Coût:§c 1" + AllDesc.coeur + "§7 (non permanent)")}),
                "§aTroisième Porte",
                90
        );
    }
    public TripleMap<HoverEvent, String, Integer> sixPorteMap() {
        return new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous donne les effets§b Speed I§7 et§c Force I§7 pendant§c 3 minutes§7.\n\n§7Coût:§c 1❤ permanent")}),
                "§aSixième Porte",
                180
        );
    }
    public TripleMap<HoverEvent, String, Integer> huitPorteMap() {
        return new TripleMap<>(
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous donne les effets§b Speed II§7,§c Force I§7,§9 Résistance I§7 et§6 Fire Résistance I§7 pendant§c 5 minutes§7, également, vous obtiendrez§c 15❤§7 permanent.\n\n§7Coût: (Après§c 5 minutes§7) Vous fait tomber à§c 5❤ permanent§7, après ceci vous ne pourrez plus utiliser vos§a portes§7.")}),
                "§aHuitième Porte",
                -500
        );
    }
    static class TroisPortePower extends ItemPower {
        private final PortesRoles role;
        protected TroisPortePower(PortesRoles role) {
            super("§aTroisième Porte", new Cooldown(90), new ItemBuilder(Material.NETHER_STAR).setName("§aTroisième Porte"), role);
            this.role = role;
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                PlayerInteractEvent event = (PlayerInteractEvent) args.get("event");
                event.setCancelled(true);
                if (role.huitUsed) {
                    player.sendMessage("§cVous avez déjà utiliser toute votre espérence de vie");
                    return false;
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 + 30, 0, false, false), true);
                if (event.getPlayer().getHealth() - 2.0 <= 0.0) {
                    event.getPlayer().setHealth(1.0);
                } else {
                    event.getPlayer().setHealth(event.getPlayer().getHealth() - 2.0);
                }
                return true;
            }
            return false;
        }
    }
    static class SixPortesPower extends ItemPower {
        private final PortesRoles role;
        protected SixPortesPower(PortesRoles role) {
            super("§aSix Porte", new Cooldown(180), new ItemBuilder(Material.NETHER_STAR).setName("§aSix Porte"), role);
            this.role = role;
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                PlayerInteractEvent event = (PlayerInteractEvent) args.get("event");
                event.setCancelled(true);
                if (role.huitUsed) {
                    event.getPlayer().sendMessage("§cVous avez déjà utiliser toute votre espérence de vie");
                    return false;
                }
                if (role.getMaxHealth() - 2.0 <= 0) {
                    event.getPlayer().sendMessage("§cVous ne pouvez plus utiliser cette technique !");
                    return false;
                }
                role.setMaxHealth(role.getMaxHealth()-2.0);
                role.owner.setMaxHealth(role.getMaxHealth());
                role.owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60 *3, 0, false, false), true);
                role.owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 3, 0, false, false), true);
                new DoubleCircleEffect(20*60*3, EnumParticle.VILLAGER_HAPPY).start(role.owner);
                return true;
            }
            return false;
        }
    }
    static class HuitPortesPower extends ItemPower {
        private final PortesRoles role;
        protected HuitPortesPower(PortesRoles role) {
            super("§aHuit Portes", new Cooldown(9999), new ItemBuilder(Material.NETHER_STAR).setName("§aHuit Portes"), role);
            this.role = role;
            setMaxUse(1);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                PlayerInteractEvent event = (PlayerInteractEvent) args.get("event");
                event.setCancelled(true);
                if (role.huitUsed) {
                    event.getPlayer().sendMessage("§cVous avez déjà utiliser toute votre espérence de vie");
                    return false;
                }
                new DoubleCircleEffect(20*60*5, EnumParticle.REDSTONE, 171, 34, 19).start(role.owner);
                role.owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*5, 1, false, false), true);
                role. owner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*5, 0, false, false), true);
                role.owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*60*5, 0, false, false), true);
                role.owner.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*60*5, 0, false, false), true);
                role.setMaxHealth(30.0);
                role.owner.setMaxHealth(role.getMaxHealth());
                role.owner.setHealth(role.owner.getMaxHealth());
                role.huitUsed = true;
                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                    if (Bukkit.getPlayer(role.getPlayer()) != null) {
                        Player owner = Bukkit.getPlayer(role.getPlayer());
                        if (GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                            if (!role.gameState.hasRoleNull(owner)) {
                                if (role.gameState.getGamePlayer().get(owner.getUniqueId()).getRole().roleID == this.role.roleID) {
                                    if (role.gameState.getGamePlayer().get(owner.getUniqueId()).getRole().StringID.equals(role.StringID)) {//donc c'est définitivement la même partie que quand il a activer
                                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                            role.setMaxHealth(10.0);
                                            role.owner.setMaxHealth(role.getMaxHealth());
                                            role.owner.setHealth(role.owner.getHealth());
                                        });
                                    }
                                }
                            }
                        }
                    }
                }, 20*60*5);
                return true;
            }
            return false;
        }
    }
}