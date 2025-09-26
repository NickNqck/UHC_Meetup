package fr.nicknqck.roles.ds.demons.lune;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.ds.builders.DemonType;
import fr.nicknqck.roles.ds.builders.DemonsRoles;
import fr.nicknqck.roles.ds.demons.MuzanV2;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class GyutaroV2 extends DemonsRoles implements Listener {

    private boolean passif = false;
    private DakiV2 daki;
    private final ItemStack FauxItem = new ItemBuilder(Material.DIAMOND_SWORD).setName("§cFaux Démoniaques")
            .addEnchant(Enchantment.DAMAGE_ALL, 3)
            .setUnbreakable(true)
            .setDroppable(false)
            .toItemStack();
    private PassifCommandPower passifCommandPower;

    public GyutaroV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull DemonType getRank() {
        return DemonType.SUPERIEUR;
    }

    @Override
    public String getName() {
        return "Gyutaro";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Gyutaro;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Demon;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new RappelPower(this), true);
        PassifCommandPower passifCommandPower = new PassifCommandPower(this);
        addPower(passifCommandPower);
        this.passifCommandPower = passifCommandPower;
        EventUtils.registerRoleEvent(this);
        getGamePlayer().addItems(this.FauxItem);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.NIGHT);
        addKnowedRole(DakiV2.class);
        addKnowedRole(Daki.class);
        addKnowedRole(MuzanV2.class);
        super.RoleGiven(gameState);
    }
    @EventHandler
    private void onEndGiveRole(final RoleGiveEvent event) {
        if (!event.isEndGive())return;
        final List<GamePlayer> dakiPlayers = new ArrayList<>(getListGamePlayerFromRoles(Roles.Daki));
        if (!dakiPlayers.isEmpty()) {
            Collections.shuffle(dakiPlayers, Main.RANDOM);
            this.daki = (DakiV2) dakiPlayers.get(0).getRole();
        }
    }
    @EventHandler
    private void onBaston(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))return;
        if (this.passifCommandPower == null)return;
        if (event.getDamager().getUniqueId().equals(getPlayer())) {
            final HashMap<String, Object> map = new HashMap<>();
            map.put("passif", this);
            this.passifCommandPower.checkUse((Player) event.getDamager(), map);
        }
    }

    @Override
    public TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    private static class RappelPower extends ItemPower {

        private final GyutaroV2 gyutaroV2;

        protected RappelPower(@NonNull GyutaroV2 role) {
            super("Rappel", new Cooldown(60 * 10), new ItemBuilder(Material.NETHER_STAR).setName("§cRappel"), role,
                    "§7Vous permez de vous téléporter à §cDaki §7si elle est présente dans un rayon de §c50 blocs§7 autour de vous et qu'elle possède moins de§c 7❤§7.");
            this.gyutaroV2 = role;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            if (gyutaroV2.daki == null) {
                player.sendMessage("§cDaki§7 n'est pas dans la partie...");
                return false;
            }
            Player daki = Bukkit.getPlayer(gyutaroV2.daki.getPlayer());
            if (daki == null) {
                player.sendMessage("§cDaki n'est pas connecter");
                return false;
            }
            if (!player.getWorld().equals(daki.getWorld())) {
                player.sendMessage("§cDaki§7 ne peut pas être téléportée pour l'instant.");
                return false;
            }
            final double distance = player.getLocation().distance(daki.getLocation());
            if (distance <= 50.0) {
                Location loc = Loc.getRandomLocationAroundPlayer(player, 5);
                daki.teleport(loc);
                player.sendMessage("§7Vous venez de Téléportez §cDaki §7à vous.");
                daki.sendMessage("§cGyutaro §7vous téléportez sur lui.");
            }
            return true;
        }
    }
    private static class PassifCommandPower extends CommandPower {

        public PassifCommandPower(@NonNull RoleBase role) {
            super("/ds faux", "faux", null, role, CommandType.DS,
                    "§7Vous permet d'§aactiver§7/§cdésactiver§7 votre§c passif§7 (§cDésactiver par défauts§7)",
                    "§7Il vous permettra d'avoir§c 5%§7 de§c chance§7 d'infliger l'effet§c Wither I§7 à la personne que vous",
                    "§7frappez avec votre épée \"§cFaux Démoniaques§7\", l'effet aura une durée de§c 5 secondes§7.");
        }
        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            if (!(getRole() instanceof GyutaroV2))return false;
            if (!args.containsKey("passif"))return false;
            if (!(args.get("passif") instanceof GyutaroV2))return false;
            if (!(Main.RANDOM.nextInt(101) <= 5))return false;
            if (!((GyutaroV2) getRole()).passif) {
                ((GyutaroV2) getRole()).passif = true;
                player.sendMessage("§7Vous venez d'§aactiver§7 votre§c passif§7.");
            } else {
                ((GyutaroV2) getRole()).passif = false;
                player.sendMessage("§7Vous venez de§c désactiver§7 votre§c passif§7.");
            }
            return false;
        }
    }
}