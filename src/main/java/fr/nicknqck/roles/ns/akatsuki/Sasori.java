package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.fastinv.FastInv;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Sasori extends AkatsukiRoles {

    public Sasori(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public String getName() {
        return "Sasori";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Sasori;
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setMaxHealth(30.0);
        owner.setMaxHealth(getMaxHealth());
        owner.setHealth(owner.getMaxHealth());
        addPower(new MarionnetteSansVie(this), true);
        addPower(new MarionetisationCommand(this));
        setChakraType(getRandomChakras());
        super.RoleGiven(gameState);
    }
    private static class MarionetisationCommand extends CommandPower implements Listener {

        private boolean activate = false;
        private final List<UUID> resurrectedPlayers = new ArrayList<>();

        public MarionetisationCommand(@NonNull RoleBase role) {
            super("/ns marionette", "marionette", null, role, CommandType.NS,
                    "§7Lorsque vous effectuer cette commande vous§a activer§7/§cdésactiver§7 la \"§cMarionetisation§7\".",
                    "",
                    "§7Si la§c Marionetisation§7 est§a activer§7, lorsqu'un joueur§c meurt§7 à moins de§c 20 blocs§7 de§c vous§7,",
                    "§7vous pourrez§c cliquer§7 sur un message dans votre§c chat§7 pour le§a réanimer§7 dans votre§c camp§7.",
                    "",
                    "§7Si la personne avait pour§a camp d'origine§7 votre§c camp actuel§7, vous ne§c pourrez pas§7 la§a réanimer§7,",
                    "§7aussi, si la personne avait comme§a camp d'origine§7 un§e camp solitaire§7, ce pouvoir ne§c fonctionnera pas§7.");
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("revive")) {
                    final UUID uuid = UUID.fromString(args[2]);
                    final Player target = Bukkit.getPlayer(uuid);
                    if (target != null) {
                        final GamePlayer gameTarget = GamePlayer.of(uuid);
                        if (gameTarget != null && gameTarget.getRole() != null && !resurrectedPlayers.contains(uuid)) {
                            revive(getRole().getGameState(), target, player);
                            return true;
                        } else {
                            player.sendMessage("§cImpossible de§a réanimer§c le joueur§b "+target.getName());
                            return false;
                        }
                    }
                }
            }
            if (!activate) {
                activate = true;
                player.sendMessage("§7Vous avez§a activer§7 votre potentiel créatif.");
            } else {
                activate = false;
                player.sendMessage("§7Vous avez§c désactiver§7 votre potentiel créatif.");
            }
            return false;
        }
        @SuppressWarnings("deprecation")
        private void revive(@NonNull final GameState gameState,@NonNull final Player target,@NonNull final Player owner) {
            RoleBase targetRole = gameState.getGamePlayer().get(target.getUniqueId()).getRole();
            /// Envoie des messages et du Title
            owner.sendMessage("§7Votre§c art§7 a toucher§c "+target.getName()+"§7.");
            target.sendMessage("§7Vous avez été§c réanimer§7 par la§c Marionetisation§7 de§c Sasori§7.");
            target.resetTitle();
            target.sendTitle("§cMarionetisation !", "Vous êtes maintenant dans le camp "+this.getRole().getTeam().getName());

            /// Changement de camp et mise à zéro de ses points de vie
            targetRole.setTeam(this.getRole().getTeam());
            targetRole.setMaxHealth(20.0);
            /// Remise de son stuff
            target.getInventory().setContents(targetRole.getGamePlayer().getLastInventoryContent());
            target.getInventory().setArmorContents(targetRole.getGamePlayer().getLastArmorContent());
            /// Revive "officiel"
            gameState.RevivePlayer(target);
            /// Téléportation à son ancienne position
            target.teleport(owner);
            /// Suppression de ses pouvoirs pour pas qu'il les ait en double
            final List<Power> copyPower = new ArrayList<>(targetRole.getPowers());
            if (!copyPower.isEmpty()) {
                for (Power power : copyPower) {
                    if (power instanceof ItemPower) {
                        target.getInventory().removeItem(((ItemPower) power).getItem());
                    }
                    targetRole.removePower(power);
                }
            }
            /// Et on lui redonne tout
            targetRole.GiveItems();
            targetRole.RoleGiven(gameState);
            /// On fait perdre à Sasori de la vie suite à la réanimation
            getRole().setMaxHealth(getRole().getMaxHealth()-(Main.getInstance().getGameConfig().getNarutoConfig().getEdoHealthRemove()+1.0));

            this.resurrectedPlayers.add(target.getUniqueId());
        }
        @EventHandler(priority = EventPriority.HIGHEST)
        private void onDeath(final UHCDeathEvent event) {
            if (!activate)return;
            if (event.getRole() == null)return;
            if (getRole().getTeam().equals(event.getRole().getTeam())){
                if (event.getRole() instanceof AkatsukiRoles)return;
            }
            if (event.getRole().getTeam().isSolo())return;
            if (!event.getPlayer().getWorld().equals(getRole().getGamePlayer().getLastLocation().getWorld()))return;
            if (event.getPlayer().getLocation().distance(getRole().getGamePlayer().getLastLocation()) > 20)return;
            getRole().getGamePlayer().sendMessage("§7Vous avez§c 10 secondes§7 pour choisir si vous voulez§a réanimer§7 le joueur \"§c"+event.getPlayer().getName()+"§7\".");
            new ReviveRunnable(this, event.getPlayer().getName(), event.getPlayer().getUniqueId()).runTaskTimer(getPlugin(), 0, 20);
        }
        private static class ReviveRunnable extends BukkitRunnable {

            private int time = 10;
            private final MarionetisationCommand marionetisationCommand;
            private final String targetName;
            private final UUID targetUuid;

            private ReviveRunnable(MarionetisationCommand marionetisationCommand, String targetName, UUID targetUuid) {
                this.marionetisationCommand = marionetisationCommand;
                this.targetName = targetName;
                this.targetUuid = targetUuid;
            }

            @Override
            public void run() {
                if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (this.time <= 0 || this.marionetisationCommand.resurrectedPlayers.contains(targetUuid)) {
                    this.marionetisationCommand.getRole().getGamePlayer().sendMessage("§cVous ne pouvez utiliser votre§2 Marionetisation§c sur§2 "+targetName);
                    cancel();
                    return;
                }
                final Player owner = Bukkit.getPlayer(this.marionetisationCommand.getRole().getPlayer());
                if (owner == null)return;
                final TextComponent text = new TextComponent("§fCliquez pour§a ressusciter§f dans votre camp§c "+targetName);
                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                        new TextComponent("§aCliquez pour ramener à la vie§c "+targetName)
                }));
                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ns marionette revive "+this.targetUuid.toString()));
                owner.spigot().sendMessage(text);
                this.time--;
            }
        }
    }
    private static class MarionnetteSansVie extends ItemPower {

        private final LinkedHashMap<BasicMarionnette, Boolean> marionnetteMap = new LinkedHashMap<>();
        private BasicMarionnette actualMarionnette = null;

        public MarionnetteSansVie(@NonNull RoleBase role) {
            super("Marionnettes sans vie", null, new ItemBuilder(Material.NETHER_STAR).setName("§cMarionnettes sans vie"), role,
                    "§7Effectue différente action en fonction du clique: ",
                    "",
                    "§8 -§f Clique droit§7: Ouvre un menu, à l'intérieur vous aurez§c plusieurs§7 possibilité de fabrication de§c marionette§7,",
                    "§7chacune aura ses spécificités et son§c coût§7 de§c fabrication§7.",
                    "§7Une fois§c fabriquer§7, vous pourrez choisir une§c marionette§7 à§c équiper§7.",
                    "",
                    "§8 -§f Clique gauche§7: Permet d'utiliser l'effet de la§c marionette équiper§7."
            );
            marionnetteMap.put(new Hiruko(role), false);
            marionnetteMap.put(new Kazegake(role), false);
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                if (!Main.getInstance().getGameConfig().isMinage()) {
                    for (BasicMarionnette basicMarionnette : this.marionnetteMap.keySet()) {
                        final List<ItemStack> list = new ArrayList<>(basicMarionnette.neededToCraft());
                        for (ItemStack itemStack : list) {
                            itemStack.setItemMeta(null);
                        }
                        getRole().getGamePlayer().addItems(list.toArray(new ItemStack[0]));
                        getRole().getGamePlayer().sendMessage("§7Vous avez obtenue le nécéssaire pour crafter "+basicMarionnette.getName());
                    }
                }
            }, 20);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("LEFT")) {
                    if (actualMarionnette == null) {
                        player.sendMessage("§7Vous devez d'abord§a équiper§7 une§c marionnette§7.");
                        return false;
                    }
                    return this.actualMarionnette.checkUse(player, map);
                } else if (event.getAction().name().contains("RIGHT")) {
                    openChooseInv(player);
                    return false;
                }
            }
            return false;
        }
        private void openChooseInv(@NonNull final Player player) {
            final FastInv fastInv = new FastInv(27, "§cMarionnettes sans vie");
            fastInv.setItems(fastInv.getCorners(), new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").setDurability(7).toItemStack());
            for (BasicMarionnette marionnette : this.marionnetteMap.keySet()) {
                fastInv.setItem(marionnette.getWhereInMenu(),
                        new ItemBuilder(marionnette.getMaterialForMenu())
                                .setName(marionnette.getName())
                                .setLore(this.marionnetteMap.get(marionnette) ? new ArrayList<>() : marionnette.getWhatIsNeededToCraft(player))
                                .addLoreLine("")
                                .addLoreLine("§fEffet: Lance une§c flèche§f la ou vous regardez,")
                                .addLoreLine(marionnette.onTouch())
                                .addLoreLine("")
                                .addLoreLine("§f§lVous "+(this.marionnetteMap.get(marionnette) ? "§a§lpossédez" : "§c§lne possédez pas")+"§f§l cette§c§l marionette§f§l.")
                                .addLoreLine(marionnetteMap.get(marionnette) ? actualMarionnette == null ? null : actualMarionnette.getClass().equals(marionnette.getClass()) ? "§f§lCette§c§l marionette§f§l est actuellement utiliser" : null : null)
                                .toItemStack(), event1 -> {
                    if (!this.marionnetteMap.get(marionnette)) {
                        if (!marionnette.takeItemsToCraft((Player) event1.getWhoClicked())) {
                            event1.getWhoClicked().sendMessage("§cVous n'avez pas les matériaux nécéssaire à la fabrication de "+marionnette.getName());
                            return;
                        }
                        this.marionnetteMap.put(marionnette, true);
                        event1.getWhoClicked().sendMessage("§7Vous avez§a fabriquer§7 la§c marionette§7: \""+marionnette.getName()+"§7\".");
                        openChooseInv((Player) event1.getWhoClicked());
                        getRole().addPower(marionnette);
                        return;
                    }
                            if (this.actualMarionnette != null) {
                                if (this.actualMarionnette.equals(marionnette)) {
                                    return;
                                }
                                event1.getWhoClicked().sendMessage("§7Vous§c n'êtes plus§7 équiper de la§c marionette§7: \"" + actualMarionnette.getName() + "§7\".");
                                actualMarionnette.onUnEquip();
                            }
                            this.actualMarionnette = marionnette;
                            event1.getWhoClicked().sendMessage("§7Vous avez§a équiper§7 la§c marionette§7: \""+marionnette.getName()+"§7\".");
                            event1.getWhoClicked().closeInventory();
                            this.actualMarionnette.onEquip();
                        });
            }
            fastInv.open(player);
        }
        private static abstract class BasicMarionnette extends Power {

            public BasicMarionnette(@NonNull String name, @NonNull RoleBase role) {
                super(name, new Cooldown(30), role);
                setShowInDesc(false);
            }
            public abstract void onEquip();
            public abstract void onUnEquip();
            public abstract List<ItemStack> neededToCraft();
            public List<String> getWhatIsNeededToCraft(@NonNull final Player player) {
                @NonNull final List<String> list = new ArrayList<>();

                for (int i = 0; i <= neededToCraft().size()-1; i++) {
                    list.add("§8 -§7 ("+
                            (GlobalUtils.getItemAmount(
                                    player,
                                    neededToCraft().get(i).getType())
                                    >=
                                    neededToCraft().get(i).getAmount() ?
                                    "§a✔" :
                                    "§c✕")
                            +"§7) "+
                            neededToCraft().get(i).getItemMeta().getDisplayName()+
                            "§7,§f§l Quantité requise:§c "
                            +neededToCraft().get(i).getAmount());
                }
                return list;
            }
            public abstract int getWhereInMenu();
            public abstract Material getMaterialForMenu();
            public boolean canCraftMarionette(@NonNull final Player player) {
                for (ItemStack itemStack : neededToCraft()) {
                    if (GlobalUtils.getItemAmount(player, itemStack.getType()) < itemStack.getAmount()) {
                        return false;
                    }
                }
                return true;
            }
            public boolean takeItemsToCraft(@NonNull final Player player) {
                if (!canCraftMarionette(player)) {
                    return false;
                }

                // Retire les items
                PlayerInventory inv = player.getInventory();

                for (ItemStack needed : neededToCraft()) {
                    int toRemove = needed.getAmount();
                    Material type = needed.getType();

                    for (int slot = 0; slot < inv.getSize(); slot++) {
                        ItemStack content = inv.getItem(slot);

                        if (content == null) continue;
                        if (content.getType() != type) continue;

                        int stackAmount = content.getAmount();

                        if (stackAmount > toRemove) {
                            // Le stack contient plus que nécessaire → retirer juste la partie demandée
                            content.setAmount(stackAmount - toRemove);
                            inv.setItem(slot, content);
                            break;
                        } else {
                            // Le stack est plus petit ou égal → retirer entièrement et continuer
                            inv.setItem(slot, null);
                            toRemove -= stackAmount;
                        }

                        if (toRemove <= 0) break;
                    }
                }

                player.updateInventory();
                return true;
            }
            public abstract String onTouch();
        }
        private static class Hiruko extends BasicMarionnette implements Listener {

            public Hiruko(@NonNull RoleBase role) {
                super("§2Hiruko§r", role);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                @NonNull final Arrow arrow = player.launchProjectile(Arrow.class);
                arrow.setBounce(false);
                arrow.setVelocity(player.getLocation().getDirection().normalize().multiply(5));
                player.playSound(player.getEyeLocation(), Sound.SHOOT_ARROW, 8, 1);
                arrow.setMetadata("hiruko.arrow", new FixedMetadataValue(Main.getInstance(), arrow.getUniqueId()));
                player.sendMessage("§7Vous lancez une§c Senbon§7.");
                return true;
            }

            @Override
            public void onEquip() {
                EventUtils.registerRoleEvent(this);
            }

            @Override
            public void onUnEquip() {
                EventUtils.unregisterEvents(this);
            }

            @Override
            public List<ItemStack> neededToCraft() {
                @NonNull final List<ItemStack> list = new ArrayList<>();
                list.add(new ItemBuilder(Material.GOLDEN_APPLE,3).setName("§ePommes d'or").toItemStack());
                list.add(new ItemBuilder(Material.SPIDER_EYE, 1).setName("§2Oeil d'araignée").toItemStack());
                list.add(new ItemBuilder(Material.IRON_BLOCK, 2).setName("§fBlocs de fer").toItemStack());
                return list;
            }

            @Override
            public int getWhereInMenu() {
                return 12;
            }

            @Override
            public Material getMaterialForMenu() {
                return Material.SPIDER_EYE;
            }

            @Override
            public String onTouch() {
                return "§fLe joueur touché obtiendra§c 8 secondes§f de§c Poison I§f.";
            }

            @EventHandler(priority = EventPriority.LOW)
            private void onDamage(EntityDamageByEntityEvent event) {
                if (!(event.getEntity() instanceof Player)) return;
                if (!(event.getDamager() instanceof Player)) return;
                Player victim = (Player) event.getEntity();
                Player attacker = (Player) event.getDamager();
                if (!victim.getUniqueId().equals(getRole().getPlayer()))return;
                Vector victimBack = victim.getLocation().getDirection().normalize();
                Vector attackerDirection = attacker.getLocation().toVector().subtract(victim.getLocation().toVector()).normalize();

                double dot = victimBack.dot(attackerDirection);

                if (dot < -0.5) {
                    double newDamage = event.getDamage() * 0.8;
                    event.setDamage(newDamage);
                    victim.sendMessage("§7Vous avez subit§c 20%§7 de§c dégâts§7 en§c moins§7.");
                }
            }
            @EventHandler
            private void onDamageByArrow(@NonNull final EntityDamageByEntityEvent event) {
                if (!(event.getEntity() instanceof Player))return;
                final Player victim = (Player) event.getEntity();
                if (!(event.getDamager() instanceof Arrow))return;
                final Arrow arrow = (Arrow) event.getDamager();
                if (arrow.hasMetadata("hiruko.arrow")) {
                    event.setDamage(0.0);
                    victim.setHealth(Math.max(1.0, victim.getHealth()-3.0));
                    if (Main.RANDOM.nextInt(101) <= 30) {
                        final GamePlayer gamePlayer = GamePlayer.of(victim.getUniqueId());
                        if (gamePlayer != null) {
                            if (gamePlayer.getRole() != null) {
                                gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.POISON, 20*8, 0, false, false), EffectWhen.NOW);
                                if (arrow.getShooter() instanceof Player) {
                                    ((Player) arrow.getShooter()).sendMessage("§7Votre§c Senbon§7 a infliger§c 8 secondes§7 de§c Poison I§7 a§c "+victim.getDisplayName());
                                }
                            }
                        }
                    }
                    victim.sendMessage("§7Vous avez été toucher par§c Hiruko§7.");
                }
            }
        }
        private static class Kazegake extends BasicMarionnette implements Listener {

            public Kazegake(@NonNull RoleBase role) {
                super("§eKazekage§r", role);
            }

            @Override
            public void onEquip() {
                EventUtils.registerRoleEvent(this);
            }

            @Override
            public void onUnEquip() {
                EventUtils.unregisterEvents(this);
            }

            @Override
            public List<ItemStack> neededToCraft() {
                @NonNull final List<ItemStack> list = new ArrayList<>();
                list.add(new ItemBuilder(Material.GOLDEN_APPLE, 5).setName("§ePommes d'or").toItemStack());
                list.add(new ItemBuilder(Material.SAND, 64).setName("§eSables").toItemStack());
                list.add(new ItemBuilder(Material.OBSIDIAN, 4).setName("§fBlocs d'obsidienne").toItemStack());
                list.add(new ItemBuilder(Material.SPIDER_EYE, 1).setName("§2Oeil d'araigné").toItemStack());
                list.add(new ItemBuilder(Material.BONE, 3).setName("§fOs").toItemStack());
                return list;
            }

            @Override
            public int getWhereInMenu() {
                return 14;
            }

            @Override
            public Material getMaterialForMenu() {
                return Material.SAND;
            }

            @Override
            public String onTouch() {
                return "§fLe joueur touché subira directement§c 1,5❤§f de§c dégâts§f,\n§fil aura§c 30% de chance§f d'obtenir§c 8 secondes§f de§c Wither II§f.";
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                @NonNull final Arrow arrow = player.launchProjectile(Arrow.class);
                arrow.setBounce(false);
                arrow.setVelocity(player.getLocation().getDirection().normalize().multiply(5));
                player.playSound(player.getEyeLocation(), Sound.SHOOT_ARROW, 8, 1);
                arrow.setMetadata("kazekage.arrow", new FixedMetadataValue(Main.getInstance(), arrow.getUniqueId()));
                player.sendMessage("§7Vous lancez une§c Scie Circulaire§7.");
                return true;
            }
            @EventHandler
            private void onDamageByArrow(@NonNull final EntityDamageByEntityEvent event) {
                if (!(event.getEntity() instanceof Player))return;
                final Player victim = (Player) event.getEntity();
                if (!(event.getDamager() instanceof Arrow))return;
                final Arrow arrow = (Arrow) event.getDamager();
                if (arrow.hasMetadata("kazekage.arrow")) {
                    event.setDamage(0.0);
                    victim.setHealth(Math.max(1.0, victim.getHealth()-3.0));
                    if (Main.RANDOM.nextInt(101) <= 50) {
                        final GamePlayer gamePlayer = GamePlayer.of(victim.getUniqueId());
                        if (gamePlayer != null) {
                            if (gamePlayer.getRole() != null) {
                                gamePlayer.getRole().givePotionEffect(new PotionEffect(PotionEffectType.WITHER, 20*8, 1, false, false), EffectWhen.NOW);
                                if (arrow.getShooter() instanceof Player) {
                                    ((Player) arrow.getShooter()).sendMessage("§7Votre§c Scie Circulaire§7 a infliger§c 8 secondes§7 de§c Wither II§7 a§c "+victim.getDisplayName());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}