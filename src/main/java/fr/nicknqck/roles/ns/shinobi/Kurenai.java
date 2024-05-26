package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCPlayerKill;
import fr.nicknqck.roles.builder.NSRoles;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.particles.MathUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static fr.nicknqck.player.StunManager.stun;

public class Kurenai extends NSRoles {
    private final ItemStack BoisItem = new ItemBuilder(Material.NETHER_STAR).setName("§cGenjutsu des bois").setLore("§7Vous permet d'empêcher le joueur viser de bouger").toItemStack();
    private int cdBois = 0;
    private final ItemStack GenjutsuItem = new ItemBuilder(Material.NETHER_STAR).setName("§cGenjutsu temporel").setLore("§7Vous permet en ciblant un joueur de créer un pure combat 1v1").toItemStack();
    private int cdGenjutsu = 0;
    public Kurenai(Player player, GameState.Roles roles) {
        super(player, roles);
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        super.giveItem(owner, false, getItems());
    }

    @Override
    public void RoleGiven(GameState gameState) {
        super.RoleGiven(gameState);
        setChakraType(getRandomChakras());
        owner.sendMessage(Desc());
        setCanBeHokage(true);
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§aKurenai",
                AllDesc.objectifteam+"§aShinobi",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§cGenjutsu des bois§f: Vous permet en ciblant un joueur, de l'empêcher de bouger pendant§c 5 secondes§f puis vous téléporte derrière ce joueur et lui inflige§c 3"+AllDesc.coeur+"§f.§7 (1x/5m)",
                "",
                AllDesc.point+"§cGenjutsu temporel§f: Vous permet en ciblant un joueur, de créer une situation de§c 1v1§f avec cette personne pendant§c 60 secondes§f.§7 (1x/5m)",
                "",
                AllDesc.particularite,
                "",
                AllDesc.point+"Vous possédez l'effet§c Force I§f proche de§a Asuma",
                AllDesc.point+"Votre nature de Chakra est aléatoire",
                "",
                AllDesc.chakra+getChakras().getShowedName(),
                AllDesc.bar
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                BoisItem,
                GenjutsuItem
        };
    }

    @Override
    public void resetCooldown() {
        cdBois = 0;
        cdGenjutsu = 0;
    }

    @Override
    public void Update(GameState gameState) {
        super.Update(gameState);
        if (cdBois >= 0){
            cdBois--;
            if (cdBois == 0){
                owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§c Genjutsu des bois§7.");
            }
        }
        if (cdGenjutsu >= 0){
            cdGenjutsu--;
            if (cdGenjutsu == 0){
                owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§c Genjutsu temporel§7.");
            }
        }
        if (getPlayerFromRole(GameState.Roles.Asuma) != null){
            if (Loc.getNearbyPlayers(getPlayerFromRole(GameState.Roles.Asuma), 15).contains(owner)){
                givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 60, 1, true);
            }
        }
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(BoisItem)){
            if (cdBois > 0){
                sendCooldown(owner, cdBois);
                return true;
            }
            Player target = getTargetPlayer(owner, 30);
            if (target == null) {
                owner.sendMessage("§cIl faut viser un joueur !");
                return true;
            }
            owner.sendMessage("§7Vous utiliser votre§c Genjutsu§7 sur§a "+target.getDisplayName());
            owner.setGameMode(GameMode.SPECTATOR);
            stun(target.getUniqueId(), 5.0, false);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                Location loc = target.getLocation();
                loc.setX(loc.getX()+Math.cos(Math.toRadians(-target.getEyeLocation().getYaw()+90)));
                loc.setZ(loc.getZ()+Math.sin(Math.toRadians(target.getEyeLocation().getYaw()-90)));
                loc.setPitch(0);
                owner.setGameMode(GameMode.SURVIVAL);
                owner.sendMessage("§7Votre§c Genjutsu§7 est terminer.");
                owner.teleport(loc);
            }, 100);
            cdBois = 60*4+5;
            return true;
        }
        if (item.isSimilar(GenjutsuItem)){
            if (cdGenjutsu > 0){
                sendCooldown(owner, cdGenjutsu);
                return true;
            }
            Player target = getTargetPlayer(owner, 30);
            if (target == null) {
                owner.sendMessage("§cIl faut viser un joueur !");
                return true;
            }
            owner.sendMessage("§7Vous avez utiliser votre§c Genjutsu§7 contre§c "+target.getDisplayName());
            new KurenaiRunnable(owner, this).runTaskTimer(Main.getInstance(), 0, 20);
            cdGenjutsu = 60*6;
            return true;
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String getName() {
        return "§aKurenai";
    }

    private static class KurenaiRunnable extends BukkitRunnable implements Listener {
        private final Location initLocation;
        private int timeRemaining = 60;
        private final UUID owner;
        private final ItemStack[] armors;
        private final Kurenai kurenai;
        private final Map<Integer, ItemStack> getContents = new HashMap<>();

        private KurenaiRunnable(Player player, Kurenai kurenai) {
            this.initLocation = player.getLocation().clone();
            this.owner = player.getUniqueId();
            EventUtils.registerEvents(this, Main.getInstance());
            this.armors = player.getInventory().getArmorContents();
            this.kurenai = kurenai;
            this.kurenai.getGamePlayer().setCanRevive(true);
            int i = 0;
            for (ItemStack stack : player.getInventory().getContents()) {
                if (stack != null && stack.getType() != Material.AIR){
                    System.out.println("Amount: "+stack.getAmount() + ", hasItemMeta "+stack.hasItemMeta()+", Type: "+stack.getType());
                    getContents.put(i, stack);
                }
                i++;
            }
        }
        @Override
        public void run() {
            if (kurenai.getGameState().getServerState() != GameState.ServerStates.InGame){
                cancel();
                return;
            }
            if (timeRemaining <= 0){
                if (Bukkit.getPlayer(owner) != null){
                    Player player = Bukkit.getPlayer(owner);
                    player.sendMessage("§7Votre§c Genjutsu§7 est maintenant terminé.");
                    player.setGameMode(GameMode.SURVIVAL);
                    player.teleport(initLocation);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(armors);
                    getContents.keySet().stream().filter(z -> getContents.get(z).getAmount() > 0).filter(z -> getContents.get(z).getAmount() <= 64).forEach(z -> player.getInventory().setItem(z, getContents.get(z)));
                    kurenai.getGamePlayer().setCanRevive(false);
                    player.updateInventory();
                    player.setHealth(player.getMaxHealth());
                }
                cancel();
                return;
            }
            MathUtil.spawnMoovingCircle(EnumParticle.REDSTONE, initLocation, 1, 20, owner);
            timeRemaining--;
            kurenai.sendCustomActionBar(kurenai.owner, "§bTemp restant avant fin du§c Genjutsu§b: §c"+ StringUtils.secondsTowardsBeautiful(timeRemaining));
        }
        @EventHandler
        private void onUHCPlayerDie(UHCPlayerKill e){
            if (e.getVictim().getUniqueId().equals(owner) && timeRemaining > 0){
                e.getVictim().getInventory().clear();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    timeRemaining = 0;
                    e.getGameState().addInSpecPlayers(e.getVictim());
                    e.getGameState().RevivePlayer(e.getVictim());
                    if (e.getVictim().isDead()){
                        e.getVictim().spigot().respawn();
                    }
                    e.getVictim().teleport(initLocation);
                    e.getVictim().getInventory().clear();
                    if (Main.isDebug()){
                        System.out.println(timeRemaining+ " string "+StringUtils.secondsTowardsBeautiful(timeRemaining));
                    }
                }, 21);
            }
        }
    }
}
