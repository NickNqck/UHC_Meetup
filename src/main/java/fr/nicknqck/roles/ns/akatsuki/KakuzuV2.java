package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KakuzuV2 extends AkatsukiRoles implements Listener {

    private final List<Chakras> chakrasVoled = new ArrayList<>();
    private final Map<Integer, ItemStack> getContents = new HashMap<>();
    private ItemStack[] getArmors = new ItemStack[0];

    public KakuzuV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.MOYENNE;
    }

    @Override
    public String getName() {
        return "Kakuzu";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Kakuzu;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
        addPower(new CorpsRapiece(this), true);
        final List<Chakras> chakrasList = new ArrayList<>();
        int i = 0;
        while (i != 3) {
            for (final Chakras chakras : Chakras.values()) {
                if (Main.RANDOM.nextBoolean()) {
                    if (chakrasList.contains(chakras))continue;
                    chakrasList.add(chakras);
                    i++;
                    break;
                }
            }
        }
        Collections.shuffle(chakrasList, Main.RANDOM);
        setChakraType(chakrasList.get(0));
        this.chakrasVoled.addAll(chakrasList);
        EventUtils.registerRoleEvent(this);
        addPower(new NSChangeCommand(this));
        addKnowedRole(HidanV2.class);
        super.RoleGiven(gameState);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7Vous commencez la partie avec§c trois§a nature de chakra§c aléatoire§7, actuellement vous possédez les§a natures de chakras§7: "+((
                        this.chakrasVoled.isEmpty()) ?
                        "§cVous ne possédez aucune nature de chakra, la mort surviendra bientôt..."
                        :
                        getChakrasList())
                )
                .getText();
    }
    private String getChakrasList() {
        final StringBuilder sb = new StringBuilder();
        for (final Chakras chakras : this.chakrasVoled) {
            sb.append(chakras.getShowedName()).append("§7, ");
        }
        return sb.substring(0, sb.length()-4);
    }
    @EventHandler
    private void onKill(final UHCPlayerKillEvent event) {
        if (event.getVictim().getUniqueId().equals(getPlayer()))return;//Si Kakuzu est la victim return
        if (event.getGamePlayerKiller() == null)return;//Si le tueur n'est pas register return
        if (!event.getGamePlayerKiller().getUuid().equals(getPlayer()))return;//Si le tueur n'est pas mon Kakuzu return
        if (event.getGameState().hasRoleNull(event.getVictim().getUniqueId()))return;//Si la victime n'a pas de rôle return
        final RoleBase role = event.getGameState().getGamePlayer().get(event.getVictim().getUniqueId()).getRole();//récupération de l'instance du rôle
        if (!(role instanceof NSRoles))return;//si le rôle ne viens pas du NaruVerse return
        if (((NSRoles) role).getChakras() == null)return;//Si la victim n'à pas de chakra return
        if (!this.chakrasVoled.contains(((NSRoles) role).getChakras())) {
            final Chakras chakras = ((NSRoles) role).getChakras();
            event.getKiller().sendMessage("§7Vous avez gagner la§a nature de chakra§7: "+chakras.getShowedName());
            this.chakrasVoled.add(chakras);
        }
    }
    @EventHandler
    private void onDie(final UHCDeathEvent event) {
        if (!event.getPlayer().getUniqueId().equals(getPlayer()))return;//Si mon Kakuzu n'est pas le joueur mort
        //A partir de la je suis la victime
        if (getChakras() == null)return;
        if (this.chakrasVoled.size() == 1) {
            event.getPlayer().sendMessage("§7Vous n'avez plus asser de§a nature de chakra§7 pour§c réapparaitre !");
            return;
        }
        if (getMaxHealth() > 10.0) {//Il faut que le Kakuzu revive tant qu'il a + que 5coeurs perma (sa lui coûte une nature de chakra et 1coeurs perma)
            setMaxHealth(getMaxHealth()-2.0);
            this.chakrasVoled.remove(getChakras());
            Collections.shuffle(this.chakrasVoled);
            event.getPlayer().sendMessage("§7Vous avez perdu la nature de chakra: "+getChakras().getShowedName());
            if (!this.chakrasVoled.isEmpty()) {
                setChakraType(this.chakrasVoled.get(0));
                event.getPlayer().sendMessage("§7Vous utilisez maintenant la nature de chakra: "+getChakras().getShowedName());
            }
            getGamePlayer().setLastArmorContent(event.getPlayer().getInventory().getArmorContents());
            getGamePlayer().setLastInventoryContent(event.getPlayer().getInventory().getContents());
            this.getArmors = event.getPlayer().getInventory().getArmorContents();
            int i = 0;
            for (ItemStack stack : event.getPlayer().getInventory().getContents()) {
                if (stack != null && stack.getType() != Material.AIR){
                    System.out.println("Amount: "+stack.getAmount() + ", hasItemMeta "+stack.hasItemMeta()+", Type: "+stack.getType());
                    getContents.put(i, stack);
                }
                i++;
            }
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                final Player player = Bukkit.getPlayer(getPlayer());
                if (player == null)return;
                if (player.isDead()){
                    player.spigot().respawn();
                }
                player.teleport(fr.nicknqck.GameListener.generateRandomLocation(Main.getInstance().getWorldManager().getGameWorld()));
                player.getInventory().setContents(getGamePlayer().getLastInventoryContent());
                player.getInventory().setArmorContents(this.getArmors);
                getContents.keySet().stream().filter(z -> getContents.get(z).getAmount() > 0).filter(z -> getContents.get(z).getAmount() <= 64).forEach(z -> player.getInventory().setItem(z, getContents.get(z)));
            }, 20*5);
            event.setCancelled(true);
        }
    }
    private static class CorpsRapiece extends ItemPower {

        public CorpsRapiece(@NonNull RoleBase role) {
            super("Corps Rapiécé", new Cooldown(60*3), new ItemBuilder(Material.NETHER_STAR).setName("§cCorps Rapiécé"), role,
                    "§7Vous permet de§a stun§7 tout les joueurs autours de vous pendant§c 5 secondes§7.");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                final List<GamePlayer> gamePlayers = new ArrayList<>(Loc.getNearbyGamePlayers(player.getLocation(), 25));
                gamePlayers.remove(getRole().getGamePlayer());
                if (gamePlayers.isEmpty()) {
                    player.sendMessage("§cImpossible de§a stun§c personne.");
                    return false;
                }
                for (final GamePlayer gamePlayer : gamePlayers) {
                    if (gamePlayer.getUuid().equals(player.getUniqueId()))continue;
                    gamePlayer.stun(20*5, false);
                }
                player.sendMessage("§7Vous avez§a stun§c "+gamePlayers.size()+" personne(s) !");
                return true;
            }
            return false;
        }
    }
    private static class NSChangeCommand extends CommandPower implements Listener {

        private final KakuzuV2 kakuzuV2;

        public NSChangeCommand(@NonNull KakuzuV2 role) {
            super("/ns change", "change", null, role, CommandType.NS,
                    "§7Vous permet de changer de§a nature de chakra§7 (parmi celle que vous possédez).");
            this.kakuzuV2 = role;
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final Inventory inv = Bukkit.createInventory(player, 27, "§7(§c!§7)§a Nature de chakra");
            int i = 9;
            for (final Chakras chakras : this.kakuzuV2.chakrasVoled) {
                if (this.kakuzuV2.getChakras().equals(chakras)) {
                    inv.setItem(i, new ItemBuilder(Material.INK_SACK).setDurability(chakras.getColorCode()).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setName(chakras.getShowedName()).toItemStack());
                } else {
                    inv.setItem(i, new ItemBuilder(Material.INK_SACK).setDurability(chakras.getColorCode()).setName(chakras.getShowedName()).toItemStack());
                }
                i+=2;
            }
            player.openInventory(inv);
            return true;
        }
        @EventHandler
        private void onInventoryClick(final InventoryClickEvent event) {
            if (event.getWhoClicked() == null)return;
            if (!event.getWhoClicked().getUniqueId().equals(getRole().getPlayer()))return;
            if (event.getClickedInventory() == null)return;
            if (event.getClickedInventory().getTitle() == null)return;
            if (event.getClickedInventory().getTitle().equals("§7(§c!§7)§a Nature de chakra")) {
                event.setCancelled(true);
                if (event.getCurrentItem() == null)return;
                final ItemStack item = event.getCurrentItem();
                if (!item.getType().equals(Material.INK_SACK))return;
                if (item.getItemMeta() == null)return;
                if (!item.getItemMeta().hasDisplayName())return;
                for (final Chakras chakras : Chakras.values()) {
                    if (chakras.getShowedName().equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
                        if (this.kakuzuV2.getChakras() != null) {
                            this.kakuzuV2.getChakras().getChakra().getList().remove(event.getWhoClicked().getUniqueId());
                        }
                        this.kakuzuV2.setChakraType(chakras);
                        event.getWhoClicked().sendMessage("§7Vous pouvez maintenant utilisé le "+chakras.getShowedName());
                        event.getWhoClicked().closeInventory();
                        break;
                    }
                }
            }
        }
    }
}