package fr.nicknqck.roles.ns.solo.kumogakure;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.NSRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Kinkaku extends NSRoles {
    private final ItemStack KyubiItem = new ItemBuilder(Material.NETHER_STAR).setName("§6§lKyubi").setLore("§7Vous permet d'obtenir des effets").toItemStack();
    private int cdKyubi = 0;
    private final ItemStack EventailItem = new ItemBuilder(Material.DIAMOND_SWORD).setUnbreakable(true).addEnchant(Enchantment.DAMAGE_ALL, 3).setName("§aEventail de bananier").setLore("§7Vous permet de cumulé la nature de chakra des joueurs tués avec la votre").toItemStack();
    private final ItemStack MissionItem = new ItemBuilder(Material.NETHER_STAR).setName("§aMission").setLore("§7Vous permet en ayant cibler un joueur de lui donner une mission").toItemStack();
    private final List<UUID> cantBeMission = new ArrayList<>();
    public Kinkaku(Player player, GameState.Roles roles) {
        super(player, roles);
        super.setChakraType(super.getRandomChakras());
        owner.sendMessage(Desc());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (!gameState.getAttributedRole().contains(GameState.Roles.Ginkaku)){
                givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, true);
            }
        }, 100);
    }

    @Override
    public Intelligence getIntelligence() {
        return Intelligence.PEUINTELLIGENT;
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        super.giveItem(owner, false, getItems());
    }

    @Override
    public String[] Desc() {
        KnowRole(owner, GameState.Roles.Ginkaku, 16);
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§6Kinkaku",
                AllDesc.objectifsolo+"avec§6 Ginkaku",
                "",
                AllDesc.effet,
                "",
                AllDesc.point+"§9Résistance I§f proche de§6 Ginkaku§f et§e Speed I§f la "+AllDesc.jour,
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§6§lKyubi§f: Pendant§c 3 minutes§f vous offre des effets, cependant ils changent chaque minutes: ",
                AllDesc.tab+"§aPremière minute§f: Vous obtenez les effets§e Speed II§f ainsi que§c Force I§f.",
                AllDesc.tab+"§6Deuxième minute§f: Vous obtenez les effets§e Speed I§f ainsi que§c Force I§f.",
                AllDesc.tab+"§cTroisième minute§f: Vous obtenez l'effet§e Speed I§f.",
                "§c! Ce pouvoir est utilisable une fois toute les 12 minutes !",
                "",
                AllDesc.point+"§aEventail de bananier§f: Symboliser par une épée en diamant§7 tranchant III§f, vous permet de cumuler la nature de Chakra d'un joueur que vous tuez avec à la votre.",
                "",
                AllDesc.point+"§aMission§f: Vous permet en ciblant un joueur, de lui attribuer une Mission aléatoire, si la cible l'accomplie, vous obtiendrez §e+4"+ AllDesc.Coeur("§e")+"§f et §d+8 secondes de régénération II§7.",
                "",
                AllDesc.particularite,
                "",
                "Vous connaissez le joueur possédant le rôle de§6 Ginkaku",
                "",
                "Vous possédez la nature de Chakra: "+getChakras().getShowedName(),
                AllDesc.bar
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                KyubiItem,
                EventailItem,
                MissionItem
        };
    }

    @Override
    public void resetCooldown() {
        cdKyubi = 0;
        cantBeMission.clear();
    }

    @Override
    public void Update(GameState gameState) {
        super.Update(gameState);
        if (!gameState.nightTime && cdKyubi <= 12*60){
            super.givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
        }
        if (cdKyubi >= 0) {
            cdKyubi--;
            if (cdKyubi == 0) {
                owner.sendMessage("§7Vous pouvez à nouveau utiliser§6§l Kyubi§7.");
            }
        }
        if (gameState.getDeadRoles().contains(GameState.Roles.Ginkaku)){
            givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false);
        } else if (new HashSet<>(Loc.getNearbyPlayersExcept(owner, 20)).containsAll(getListPlayerFromRole(GameState.Roles.Ginkaku))){
            givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 60, 1, true);
        }
    }

    @Override
    public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
        super.OnAPlayerDie(player, gameState, killer);
        if (owner != null && killer.getUniqueId().equals(owner.getUniqueId())){
            if (owner.getItemInHand().isSimilar(EventailItem)){
                if (getPlayerRoles(player).hasChakras() && !getPlayerRoles(player).getChakras().getChakra().getList().contains(owner.getUniqueId())){
                    getPlayerRoles(player).getChakras().getChakra().getList().add(owner.getUniqueId());
                    owner.sendMessage("En tuant§c "+player.getDisplayName()+"§f vous avez obtenue sa nature de Chakra: "+getPlayerRoles(player).getChakras().getShowedName());
                }
            }
        }
    }
    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(MissionItem)){
            Player target = getTargetPlayer(owner, 30);
            if (target != null){
                if (cantBeMission.contains(target.getUniqueId())){
                    owner.sendMessage("§cVous ne pouvez pas mettre de mission à un joueur qui en a déjà eu une dans les 10 dernières minutes.");
                    return true;
                }
                if (cantBeMission.isEmpty()){
                    new KinkakuMissions(owner.getUniqueId(), target.getUniqueId());
                    cantBeMission.add(target.getUniqueId());
                } else {
                    owner.sendMessage("§cVous ne pouvez pas donner une Mission a quelqu'un qui en a déjà une.");
                }
                return true;
            }
        }
        if (item.isSimilar(KyubiItem)) {
            if (cdKyubi <= 0) {
                owner.sendMessage("§7Activation de§6 Kyubi");
                cdKyubi = 60*15;
                new BukkitRunnable() {
                    private int time = 60;
                    private int state = 3;
                    @Override
                    public void run() {
                        if (owner == null) {
                            cancel();
                            return;
                        }
                        if (gameState.getServerState() != GameState.ServerStates.InGame) {
                            cancel();
                            return;
                        }
                        if (state == 0) {
                            owner.sendMessage("§7L'utilisation du chakra de§6 Kyubi§7 est maintenant§c terminer§7.");
                            cancel();
                            return;
                        }
                        if (state == 3) {
                            givePotionEffet(PotionEffectType.SPEED, 60, 2, true);
                            givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
                        }
                        if (state == 2) {
                            givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
                            givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
                        }
                        if (state == 1) {
                            givePotionEffet(PotionEffectType.SPEED, 60, 1, true);
                        }
                        if (time == 0) {
                            state--;
                            time = 60;
                        }
                        sendCustomActionBar(owner, "Temp avant prochain stade de§6 Kyubi§f:§c "+ StringUtils.secondsTowardsBeautiful(time));
                        time--;
                    }
                }.runTaskTimer(Main.getInstance(), 0, 20);
            } else {
                sendCooldown(owner, cdKyubi);
                return true;
            }
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public String getName() {
        return "§6Kinkaku";
    }

    private static class KinkakuMissions implements Listener {
        private UUID user;
        private UUID target;
        private Missions mission;
        private int timeNearby = 0;
        private int nmbGapEat = 0;
        private double distanceSquared;
        private int nmbCoup = 0;
        private KinkakuMissions(UUID user, UUID target){
            this.user = user;
            this.target = target;
            getRandomMissions();
        }
        private void getRandomMissions(){
            int rdm = RandomUtils.getRandomInt(0, Missions.values().length-1);
            for (Missions missions : Missions.values()){
                if (missions.getRdm() == rdm){
                    this.mission = missions;
                    break;
                }
            }
            if (isNotNull()){
                Bukkit.getPlayer(user).sendMessage("§7La mission de votre cible est§f "+mission.getMission());
                if (mission == Missions.Rester){
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (isNotNull()){
                                Player t = Bukkit.getPlayer(target);
                                Player u = Bukkit.getPlayer(user);
                                if (u != null && t != null){
                                    if (Loc.getNearbyPlayersExcept(u, 10).contains(t)){
                                        timeNearby++;
                                    } else {
                                        timeNearby = 0;
                                    }
                                    if (timeNearby == 10){
                                        accomplyMission();
                                        cancel();
                                    }
                                }
                            }
                        }
                    }.runTaskTimer(Main.getInstance(), 0, 20);
                }
            }
        }
        private void accomplyMission(){
            Player e = Bukkit.getPlayer(user);
            if (e == null) return;
            CraftPlayer craftPlayer = (CraftPlayer) e;
            craftPlayer.getHandle().setAbsorptionHearts(craftPlayer.getHandle().getAbsorptionHearts()+8.0f);
            craftPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*8 ,1, false, false), true);
            craftPlayer.sendMessage("§c"+e.getDisplayName()+"§7 à réussi sa mission secrète, vous obtenez donc §e+4"+ AllDesc.Coeur("§e")+"§7 et §d+8 secondes de régénération II§7.");
            mission = null;
            user = null;
            target = null;
        }
        private boolean isNotNull(){
            return user != null && target != null && mission != null;
        }
        @Getter
        private enum Missions {
            Infliger("Vous infligez 15 coups", 0),
            Rester("Il doit rester proche de vous (10 blocs) pendant 10s", 1),
            Gap("Il doit manger 3 pommes d'or en moins de 10s", 2),
            Parcourir("Il doit parcourir un total de§c 50 blocs", 3);
            private final String mission;
            private final int rdm;
            Missions(String mission, int rdmPoint){
                this.mission = mission;
                this.rdm = rdmPoint;
            }
        }
        @EventHandler
        private void onDamage(EntityDamageByEntityEvent e){
            if (isNotNull()){
                if (e.getDamager().getUniqueId().equals(target)){
                    nmbCoup++;
                    if (nmbCoup == 15) {
                        accomplyMission();
                    }
                }
            }
        }
        @EventHandler
        private void onPlayerEat(PlayerItemConsumeEvent e){
            if (isNotNull()){
                if (e.getPlayer().getUniqueId().equals(target) && mission == Missions.Gap && e.getItem().getType().equals(Material.GOLDEN_APPLE)){
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> nmbGapEat = 0, 200);
                    nmbGapEat++;
                    if (nmbGapEat == 3){
                        accomplyMission();
                    }
                }
            }
        }
        @EventHandler
        private void onMoove(PlayerMoveEvent e){
            if (isNotNull()){
                if (e.getPlayer().getUniqueId().equals(target) && mission == Missions.Parcourir){
                    if (distanceSquared >= 50){
                        accomplyMission();
                    } else {
                        distanceSquared+= e.getFrom().distance(e.getTo());
                    }
                }
            }
        }
    }
}