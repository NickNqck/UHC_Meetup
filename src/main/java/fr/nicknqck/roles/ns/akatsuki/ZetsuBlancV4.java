package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.*;
import fr.nicknqck.events.power.PowerTakeInfoEvent;
import fr.nicknqck.interfaces.IRoles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ZetsuBlancV4 extends AkatsukiRoles {

    @Setter
    private Intelligence intelligence = Intelligence.PEUINTELLIGENT;

    public ZetsuBlancV4(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return this.intelligence;
    }

    @Override
    public EChakras[] getChakrasCanHave() {
        return EChakras.values();
    }

    @Override
    public String getName() {
        return "Zetsu Blanc";
    }

    @Override
    public @NonNull IRoles<?> getRoles() {
        return Roles.ZetsuBlanc;
    }

    @Override
    public @NonNull TextComponent getComponent() {
        return AutomaticDesc.createFullAutomaticDesc(this);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        addPower(new EjectionDeSpores(this), true);
        addPower(new DefineIntelligence(this));
    }
    private static final class EjectionDeSpores extends ItemPower implements Listener {

        private final List<UUID> list = new ArrayList<>();

        public EjectionDeSpores(@NonNull RoleBase role) {
            super("§aÉjection de spores§r", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§aÉjection de spores"), role,
                    "§7Lance vos§a spores§7 dans un§c rayon§7 de§c 50 blocs§7,",
                    "§7vous saurez quel joueur est§a Shinobi§7 (uniquement si vous avez la même§a intelligence§7).",
                    "",
                    "§7Vous subissez§c 10%§7 de§c dégâts§7 en§c moins§7 par les joueurs découvert.",
                    "§7Si vous êtes la§c cible§7 d'un§c pouvoir informatif§7 venant d'une personne ayant reçu vos§a spores§7,",
                    "§7la§c cible§7 de ce§c pouvoir§7 deviendra son§a lanceur§7."
            );
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                @NonNull
                final List<GamePlayer> list = Loc.getNearbyGamePlayers(player.getLocation(), 50.0);
                @NonNull
                final Intelligence intelligence = this.getRole() instanceof NSRoles ? ((NSRoles) this.getRole()).getIntelligence() : Intelligence.MOYENNE;
                for (GamePlayer gamePlayer : list) {
                    if (gamePlayer == null)continue;
                    if (!gamePlayer.check())continue;
                    if (!(gamePlayer.getRole() instanceof NSRoles))continue;
                    @NonNull final PowerTakeInfoEvent event = new PowerTakeInfoEvent(this, gamePlayer, InfoType.TEAM);
                    getPlugin().getServer().getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        event.sendCancelMessage(player);
                        continue;
                    }
                    if (!(event.getGameTarget().getRole() instanceof  NSRoles))continue;
                    if (intelligence.getEnseignemenTime() != ((NSRoles) event.getGameTarget().getRole()).getIntelligence().getEnseignemenTime())continue;
                    if (this.list.contains(gamePlayer.getUuid()))continue;
                    @NonNull final PowerTakeInfoEvent powerTakeInfoEvent = new PowerTakeInfoEvent(this, gamePlayer, InfoType.TEAM);
                    getPlugin().getServer().getPluginManager().callEvent(powerTakeInfoEvent);
                    if (!powerTakeInfoEvent.isCancelled()) {
                        if (powerTakeInfoEvent.getGameTarget().getRole().getTeam().equals(TeamList.Shinobi)) {
                            player.sendMessage("§a"+gamePlayer.getPlayerName()+"§7 semble faire partie du camp des§a Shinobis§7.");
                            Main.getInstance().getCustomTabManager().setSuffix(player.getUniqueId(), gamePlayer.getUuid(), "§2 ✔");
                        } else {
                            player.sendMessage("§c"+gamePlayer.getPlayerName()+"§7 ne§c semble pas§7 faire partie du camp des§a Shinobis§7.");
                            Main.getInstance().getCustomTabManager().setSuffix(player.getUniqueId(), gamePlayer.getUuid(), "§c§l ✘");
                        }
                    } else {
                        powerTakeInfoEvent.sendCancelMessage(player);
                    }
                    this.list.add(gamePlayer.getUuid());
                }
                return true;
            }
            return false;
        }
        @EventHandler(priority = EventPriority.HIGHEST)
        private void onDamage(@NonNull final EntityDamageByEntityEvent event) {
            if (!event.getEntity().getUniqueId().equals(getRole().getPlayer()))return;
            if (this.list.contains(event.getDamager().getUniqueId())) {
                event.setDamage(event.getDamage()*0.9);
            }
        }
        @EventHandler(priority =  EventPriority.HIGHEST)
        private void  onInfoGet(@NonNull final PowerTakeInfoEvent event) {
            if (this.list.contains(event.getPower().getRole().getPlayer()) && event.getGameTarget().getUuid().equals(getRole().getPlayer())) {
                event.setGameTarget(event.getPower().getRole().getGamePlayer());
            }
        }
    }
    private static final class DefineIntelligence extends CommandPower {

        public DefineIntelligence(@NonNull RoleBase role) {
            super("/ns intelligence <intelligence>", "intelligence", new Cooldown(60*5), role, CommandType.NS,
                    "§7Vous pouvez définir quel est votre§a niveau d'intelligence perçu§7."
            );
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final String intelligence = args[1];
                for (@NonNull final Intelligence value : Intelligence.values()) {
                    if (value.equals(Intelligence.CONNUE))continue;
                    if (intelligence.equalsIgnoreCase(value.name()) && getRole() instanceof ZetsuBlancV4) {
                        ((ZetsuBlancV4) getRole()).setIntelligence(value);
                        player.sendMessage("§7Vous avez définie votre§a intelligence§7 sur§a "+value.getName()+"§7.");
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public List<String> getCompletor(String[] args) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase(getArg0())) {
                    final List<String> toReturn = new ArrayList<>();
                    for (@NonNull final Intelligence value : Intelligence.values()) {
                        if (value.equals(Intelligence.CONNUE))continue;
                        if (getRole() instanceof NSRoles && ((NSRoles) getRole()).getIntelligence().equals(value)) {
                            continue;
                        }
                        toReturn.add(value.name().toLowerCase());
                    }
                    return toReturn;
                }
            }
            return super.getCompletor(args);
        }
    }
}