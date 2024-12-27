package fr.nicknqck.roles.custom;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LeComte extends CustomRolesBase implements Listener {

    private TextComponent automaticDesc;
    private boolean useInspection = false;
    private UUID inspected;
    private BukkitRunnable inspectionRunnable;
    private boolean inspectionEnded = false;
    private int cdInspection;
    private int cdDuel;
    private final List<UUID> inspecteds = new ArrayList<>();
    public LeComte(UUID player) {
        super(player);
    }

    @Override
    public void RoleGiven(GameState gameState) {
        final AutomaticDesc desc = new AutomaticDesc(this);
        desc.setCommands(new TripleMap<>(getInspectionHover(), "§c/c inspection <joueur>", 60*10), new TripleMap<>(getDuelHover(), "§c/c duel <joueur>", 60*15));
        Map<HoverEvent, String> particularites = new HashMap<>();
        particularites.put(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez§c 3"+AllDesc.coeur+"§7 supplémentaire")}), "§cVie supplémentaire");
        particularites.put(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Hors de votre dimension, vous infligez§c +35%§7 de dégat au joueurs que vous aviez complètement §cinspecter")}), "§cBoost de Force");
        desc.addParticularites(particularites.keySet().toArray(new HoverEvent[0]));
        this.automaticDesc = desc.getText();
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        new UpdateRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        setMaxHealth(26.0);
        owner.setMaxHealth(getMaxHealth());
        owner.setHealth(owner.getMaxHealth());
    }

    @Override
    public String getName() {
        return "Le Comte";
    }
    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.LeComte;
    }
    @Override
    public TeamList getOriginTeam() {
        return TeamList.Solo;
    }
    @Override
    public void resetCooldown() {}
    @Override
    public String[] Desc() {
        return new String[0];
    }
    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }
    @Override
    public TextComponent getComponent() {
        return automaticDesc;
    }
    @Override
    public boolean onCustomCommand(String[] args, Player player) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("inspection")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (!useInspection) {
                        this.inspectionRunnable = new InspectionRunnable(this, target.getUniqueId());
                        this.inspectionRunnable.runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
                        this.useInspection = true;
                        this.inspected = target.getUniqueId();
                        this.inspectionEnded = false;
                    } else {
                        player.sendMessage("§7Vous avez déjà utiliser votre§c /c inspection <joueur>");
                    }
                } else {
                    player.sendMessage("§c"+args[1]+"§7 n'est pas connectée");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("duel")) {
                player.sendMessage("§cCommande non créer");
            }
        }
        return false;
    }
    @EventHandler
    private void onKill(UHCPlayerKillEvent event) {
        if (event.getPlayerKiller() != null && useInspection && this.inspected != null && inspectionEnded) {
            if (event.getVictim().getUniqueId().equals(this.inspected) && event.getPlayerKiller().getUniqueId().equals(this.getPlayer())) {
                giveItem(event.getPlayerKiller(), false, new ItemBuilder(Material.GOLDEN_APPLE).setAmount(5).toItemStack());
                event.getPlayerKiller().sendMessage("§7Vous avez tuer le joueur que vous aviez inspecter, vous obtenez donc§e 5 pommes d'or§7 supplémentaire.");
                this.inspected = null;
                this.useInspection = false;
                if (this.inspectionRunnable != null) {
                    this.inspectionRunnable.cancel();
                }
                event.getPlayerKiller().sendMessage("§7Vous pouvez à nouveau utiliser votre§c /c inspection <joueur>");

            }
        }
    }
    @EventHandler
    private void onDeath(UHCDeathEvent event) {
        if (this.inspected != null) {
            if (event.getPlayer().getUniqueId().equals(this.inspected)) {
                if (this.inspectionRunnable != null) {
                    this.inspectionRunnable.cancel();
                    Player owner = Bukkit.getPlayer(getPlayer());
                    if (owner != null) {
                        owner.sendMessage("§c"+event.getPlayer().getName()+"§7 est §cmort§7 votre§c inspection§7 s'arrête donc.");
                    }
                }
            }
        }
    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        HandlerList.unregisterAll(this);
    }
    @EventHandler
    private void onBattle(EntityDamageByEntityEvent event) {
        if (event.getDamager().getUniqueId().equals(getPlayer()) && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if (this.inspecteds.contains(event.getEntity().getUniqueId())) {
                event.setDamage(event.getDamage()*1.35);
            }
        }
    }
    private HoverEvent getInspectionHover() {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous permet d'obtenir petit-à-petit des informations sur le joueur cibler dans cette ordre là:\n\n"
                +AllDesc.point+"§a1 minutes§7: Vous obtenez le nombre de §epomme d'or§7 du joueur\n"+
                AllDesc.point+"§a2 minutes§7: Vous obtenez le§c camp§7 du joueur\n" +
                AllDesc.point+"§a3 minutes§7: Vous obtenez le§c rôle§7 de la cible, de plus, vous obtiendrez§e 5 pommes d'or§7 supplémentaire en la§c tuant")});
    }
    private HoverEvent getDuelHover() {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {
           new TextComponent("§7Vous permet de forcer le duel entre vous et le joueur voulu dans une autre §cdimension§7, dans cette dernière vous aurez les effets§b Speed I§7 et§b Force I§7\n\n"+
                   "§7Si vous aviez précédemment utiliser votre commande \"§c/c inspection§7\" sur le joueur cibler il ne pourra pas utiliser de§6 seau de lave§7 dans votre§c dimension§7.")
        });
    }
    private static class UpdateRunnable extends BukkitRunnable {
        private final LeComte leComte;
        private UpdateRunnable(LeComte leCompte) {
            this.leComte = leCompte;
        }
        @Override
        public void run() {
            if (!leComte.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                if (leComte.inspectionRunnable != null) {
                    leComte.inspectionRunnable.cancel();
                }
                return;
            }
            Player owner = Bukkit.getPlayer(leComte.getPlayer());
            if (owner != null) {
                if (leComte.cdInspection >= 0) {
                    leComte.cdInspection--;
                    if (leComte.cdInspection == 0) {
                        owner.sendMessage("§7Vous pouvez à nouveau utiliser§c /c inspection <joueur>§7.");
                    }
                }
                if (leComte.cdDuel >= 0) {
                    leComte.cdDuel--;
                    if (leComte.cdDuel == 0) {
                        owner.sendMessage("§7Vous pouvez à nouveau entrer dans un§c duel§7 contre un joueur.");
                    }
                }
            }
        }
    }
    private static class InspectionRunnable extends BukkitRunnable {
        private final LeComte leCompte;
        private int time = 0;
        private final UUID uuidTarget;
        private InspectionRunnable(LeComte compte, UUID uuidTarget) {
            this.leCompte = compte;
            this.uuidTarget = uuidTarget;
        }
        @Override
        public void run() {
            if (!leCompte.getGameState().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (time >= 60*3) {
                leCompte.inspectionEnded = true;
                cancel();
                return;
            }
            Player owner = Bukkit.getPlayer(leCompte.getPlayer());
            Player target = Bukkit.getPlayer(uuidTarget);
            if (owner != null && target != null) {
                if (Loc.getNearbyPlayers(owner, 15).contains(target)) {
                    time++;
                }
                NMSPacket.sendActionBar(owner, "§bTemp d'inspection actuel: "+ StringUtils.secondsTowardsBeautiful(time));
                if (time == 60) {
                    int gapAmount = 0;
                    for (ItemStack itemStack : target.getInventory().getContents()) {
                        if (itemStack != null && itemStack.getType().equals(Material.GOLDEN_APPLE)) {
                            gapAmount += itemStack.getAmount();
                        }
                    }
                    owner.sendMessage("§c"+target.getName()+"§7 possède§e "+gapAmount+" pomme§7(§es§7)§e d'or");

                } else if (time == 120) {
                    TeamList team = GameState.getInstance().getGamePlayer().get(target.getUniqueId()).getRole().getTeam();
                    if (team != null) {
                        owner.sendMessage("§c"+target.getName()+"§7 est dans le camp \""+team.getName()+"§7\"");
                    }
                } else if (time == 60*3) {
                    if (!leCompte.getGameState().hasRoleNull(target.getUniqueId())) {
                        RoleBase role = leCompte.getGameState().getPlayerRoles().get(target);
                        owner.sendMessage("§c"+target.getName()+"§7 est le rôle "+role.getOriginTeam().getColor()+role.getName());
                    }
                    owner.sendMessage("§7Votre inspection prend fin, vous obtiendrez§e 5 pommes d'or§7 en tuant §c"+target.getName());
                    leCompte.inspected = target.getUniqueId();
                    leCompte.inspecteds.add(target.getUniqueId());
                }
            }
        }
    }
}
