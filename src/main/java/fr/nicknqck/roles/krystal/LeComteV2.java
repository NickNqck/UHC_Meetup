package fr.nicknqck.roles.krystal;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.GlobalUtils;
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
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LeComteV2 extends BonusKrystalBase implements Listener{

    private final Map<UUID, Integer> forcePercentMap = new HashMap<>();
    private final List<UUID> enqueteds = new ArrayList<>();

    public LeComteV2(UUID player) {
        super(player);
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Le Comte";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.LeComte;
    }

    @Override
    public @NonNull TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7Si vous§c tuez§7 un joueur que vous aviez enquêter vous §7gagnerez§c 1❤ permanent§7 ainsi que§c 5 krystaux")
                .addCustomLine("§7Chaque fois que vous gagnerez de la force grâce à votre §aenquête§7 vous perdrez§c 1 krystal")
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new EnqueteCommandPower(this));
        addPower(new LameDuMaitre(this), true);
        setKrystalAmount(50);
        addBonus(new ForcePermaBonus(55, this));
    }
    @EventHandler
    private void onKill(final UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() == null)return;
        if (!event.getKiller().getUniqueId().equals(getPlayer()))return;
        if (event.isCancel())return;
        if (this.enqueteds.contains(event.getVictim().getUniqueId())) {
            setMaxHealth(getMaxHealth()+2);
            setKrystalAmount(getKrystalAmount()+5);
            event.getPlayerKiller().setMaxHealth(getMaxHealth());
        }
    }
/*
    @Override
    public @NonNull Map<PotionEffect, Integer> getBonus() {
        final Map<PotionEffect, Integer> map = new HashMap<>();
        map.put(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), 55);
        return map;
    }
*/

    private static class LameDuMaitre extends ItemPower {

        private final LeComteV2 leComteV2;

        protected LameDuMaitre(@NonNull LeComteV2 role) {
            super("Lame du Maitre", null, new ItemBuilder(Material.DIAMOND_SWORD)
                    .setName("§aLame du Maitre")
                    .addEnchant(Enchantment.DAMAGE_ALL, 3), role,
                    "§7Vous permet d'§cinfliger§7 des§c dégats boosté§7 par votre§a enquête§7 (§f/c enquete <joueur>§7)");
            this.leComteV2 = role;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.ATTACK_ENTITY)){
                final UHCPlayerBattleEvent event = (UHCPlayerBattleEvent) map.get("event");
                if (event.isPatch())return false;
                if (this.leComteV2.forcePercentMap.containsKey(event.getVictim().getUuid())) {
                    double force = this.leComteV2.forcePercentMap.getOrDefault(event.getVictim().getUuid(), 0);
                    if (force == 0)return false;
                    force = force/100;
                    force = force+1;
                    event.setDamage(event.getDamage()*force);
                    return true;
                }
            }
            return false;
        }
    }
    private static class EnqueteCommandPower extends CommandPower {

        private boolean in = false;
        private final LeComteV2 leComteV2;

        public EnqueteCommandPower(@NonNull LeComteV2 role) {
            super("/c enquete <joueur>", "enquete", new Cooldown(60*5), role, CommandType.CUSTOM,
                    "§7Vous permet d'enquêter sur un§c joueur§7, toute les§c secondes§7 pour chaque joueur autours de lui l'enquête augmentera de§c 1 points",
                    "§7tout les§c 100 points§7 vous obtiendrez les informations suivante: ",
                    "",
                    "§7 - Le nombre de§e pomme d'or§7 qu'il à mangé depuis le dernier résultat de l'enquête",
                    "",
                    "§7 - Le nombre de§e pomme d'or§7 que le joueur possède",
                    "",
                    "§7 - Le§c rôle§7 de l'un des§c joueurs§7 étant autours de lui",
                    "",
                    "§7 - Le§c pourcentage§7 de§c force§7 que vous avez contre§c lui");
            this.leComteV2 = role;
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    if (!getRole().getGameState().hasRoleNull(target.getUniqueId())){
                        if (in) {
                            player.sendMessage("§cUne enquête est déjà en cours");
                            return false;
                        }
                        new EnqueteRunnable(getRole().getGameState().getGamePlayer().get(target.getUniqueId()), this);
                        this.in = true;
                        this.leComteV2.enqueteds.add(target.getUniqueId());
                        return true;
                    }
                }
            }
            return false;
        }
        private static class EnqueteRunnable extends BukkitRunnable implements Listener {

            private final GamePlayer gameTarget;
            private final EnqueteCommandPower enqueteCommandPower;
            private final GameState gameState;
            private final GamePlayer gamePlayer;

            private final List<UUID> rencontres;
            private int point = 0;
            private int gapEated = 0;

            private int stade = 0;

            private EnqueteRunnable(GamePlayer uuidTarget, EnqueteCommandPower enqueteCommandPower) {
                this.gameTarget = uuidTarget;
                this.enqueteCommandPower = enqueteCommandPower;
                this.gameState = enqueteCommandPower.getRole().getGameState();
                this.gamePlayer = this.enqueteCommandPower.getRole().getGamePlayer();
                this.rencontres = new ArrayList<>();
                runTaskTimerAsynchronously(enqueteCommandPower.getPlugin(), 0, 20);
                this.gamePlayer.getActionBarManager().addToActionBar(
                        "lecomte.enquete.points."+uuidTarget.getUuid(),
                        "§bPoints de§c "+uuidTarget.getPlayerName()+"§b:§c 0"
                );
                EventUtils.registerEvents(this);
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    this.stop();
                    return;
                }
                if (!this.gameTarget.isAlive()) {
                    return;
                }
                if (this.gameTarget.getDiscRunnable() != null && !this.gameTarget.getDiscRunnable().isOnline()) {
                    return;
                }
                if (this.enqueteCommandPower.leComteV2.getKrystalAmount() <= 0)return;
                final List<Player> aroundPlayers = Loc.getNearbyPlayers(this.gameTarget.getLastLocation(), 20);
                for (final Player p : aroundPlayers) {
                    this.point++;
                    if (!rencontres.contains(p.getUniqueId())) {
                        this.rencontres.add(p.getUniqueId());
                    }
                }
                this.gamePlayer.getActionBarManager().updateActionBar(
                        "lecomte.enquete.points."+this.gameTarget.getUuid(),
                        "§bPoints de§c "+this.gameTarget.getPlayerName()+"§b:§c "+this.point+"§7 (§a+"+aroundPlayers.size()+"§7)"
                        );
                final Player owner = Bukkit.getPlayer(this.gamePlayer.getUuid());
                if (owner != null) {
                    this.stade+=aroundPlayers.size();
                    if (stade >= 100) {
                        this.enqueteCommandPower.leComteV2.forcePercentMap.putIfAbsent(this.gameTarget.getUuid(), 0);
                        int force = this.enqueteCommandPower.leComteV2.forcePercentMap.getOrDefault(this.gameTarget.getUuid(), 0);
                        this.enqueteCommandPower.leComteV2.forcePercentMap.replace(this.gameTarget.getUuid(), force, force+5);
                        force+=5;
                        this.enqueteCommandPower.leComteV2.setKrystalAmount(this.enqueteCommandPower.leComteV2.getKrystalAmount()-1);
                        owner.sendMessage(new String[] {
                                "§7Voici toute les informations que vous avez pus obtenir sur§c "+this.gameTarget.getPlayerName()+"§7:",
                                "",
                                "§7 - Il a mangé§c "+this.gapEated+"§e pomme d'or§7, il lui en reste§c "+ GlobalUtils.getItemAmount(Bukkit.getPlayer(this.gameTarget.getUuid()), Material.GOLDEN_APPLE),
                                "",
                                "§7 - Il y a actuellement§c "+aroundPlayers.size()+" joueurs§7 autours de lui",
                                "",
                                "§7 - L'un des joueurs autours de lui possède le rôle§b "+this.findRole(this.gameTarget.getLastLocation()),
                                "",
                                "§7 - Vous avez actuellement§c "+force+"%§7 de§c force§7 contre§c "+this.gameTarget.getPlayerName()
                        });
                        this.stade = 0;
                        this.gapEated = 0;
                    }
                    if (this.point >= 400) {
                        owner.sendMessage("§7Votre enquête est maintenant§c terminer§7.");
                        this.enqueteCommandPower.in = false;
                        stop();
                    }
                }
            }
            private synchronized void stop() {
                EventUtils.unregisterEvents(this);
                cancel();
            }
            @EventHandler
            private void onEat(final PlayerItemConsumeEvent event) {
                if (!event.getPlayer().getUniqueId().equals(this.gameTarget.getUuid()))return;
                if (event.getItem().getType().equals(Material.GOLDEN_APPLE)) {
                    this.gapEated++;
                }
            }
            private synchronized String findRole(final Location location) {
                final List<GamePlayer> gamePlayers = Loc.getNearbyGamePlayers(location, 25);
                Collections.shuffle(gamePlayers, Main.RANDOM);
                for (final GamePlayer gamePlayer : gamePlayers) {
                    if (gamePlayer.getRole() == null)continue;
                    return gamePlayer.getRole().getName();
                }
                return this.gameTarget.getRole().getName();
            }
        }
    }
}