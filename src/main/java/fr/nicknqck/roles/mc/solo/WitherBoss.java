package fr.nicknqck.roles.mc.solo;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class WitherBoss extends RoleBase {
    private boolean isFlying = false;
    private final ItemStack FlyItem = new ItemBuilder(Material.FEATHER).setName("§aFly").setLore("§7Vous permet de voler pendant un temp maximum de§c 15s").toItemStack();
    private int cdFly = 0;
    private boolean passifActive = false;
    public WitherBoss(Player player, GameState.Roles roles) {
        super(player, roles);
        owner.sendMessage(Desc());
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        super.giveItem(owner, false, getItems());
        super.giveHealedHeartatInt(2.0);
    }

    @Override
    public void Update(GameState gameState) {
        super.Update(gameState);
        if (!isFlying){
            givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false);
            givePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
        } else {
            givePotionEffet(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false);
        }
        if (cdFly >= 0){
            cdFly--;
            if (cdFly == 0){
                owner.sendMessage("§7Vous pouvez à nouveau§a voler§7.");
            }
        }
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§e WitherBoss",
                AllDesc.objectifsolo+"§e Seul",
                "",
                AllDesc.effet,
                "",
                AllDesc.point+"§c2"+AllDesc.coeur+"§f, §eSpeed I§f et §9 Résistance I§f permanent",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§aFly§f: Vous permet d'obtenir la capacité de voler pendant§c 15 secondes§f, également, pendant ce temp vous perdrez votre effet de§9 Résistance§f mais gagnerez l'effet§c Force I§f§f.§7 (1x/7m)",
                "",
                AllDesc.commande,
                "",
                AllDesc.point+"§6/mc passif§f: Vous permet d'activé votre passif, il vous permet d'avoir§c 25%§f de chance d'infliger l'effet Wither I au joueur qui subisse des dégats de votre provenance.",
                "",
                AllDesc.bar

        };
    }

    @Override
    public void onMcCommand(String[] args) {
        super.onMcCommand(args);
        if (args[0].equalsIgnoreCase("passif")){
            if (!passifActive){
                passifActive = true;
                owner.sendMessage("§7Vous avez activé votre§a passif");
            } else {
                passifActive = false;
                owner.sendMessage("§7Vous avez désactivé votre§c passif");
            }
        }
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                FlyItem
        };
    }
    @Override
    public void resetCooldown() {
        isFlying = false;
        cdFly = 0;
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(FlyItem)){
            if (cdFly <= 0) {
                isFlying = true;
                owner.sendMessage("§7Vous avez activé votre§a Fly");
                cdFly = 60*7+15;
                owner.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                owner.setAllowFlight(true);
                owner.setFlying(true);
                new BukkitRunnable(){
                    private int timeRemaining = 15;
                    @Override
                    public void run() {
                        if (gameState.getServerState() != GameState.ServerStates.InGame || !getIGPlayers().contains(owner) || !isFlying){
                            cancel();
                            return;
                        }
                        if (timeRemaining == 0){
                            owner.sendMessage("§7Votre§a Fly§7 prend fin.");
                            owner.setFlying(false);
                            owner.setAllowFlight(false);
                            owner.setFallDistance(0f);
                            isFlying = false;
                            owner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                            cdFly = 60*7;
                            cancel();
                            return;
                        }
                        sendCustomActionBar(owner, "§bTemp de vole restant:§c "+ StringUtils.secondsTowardsBeautiful(timeRemaining));
                        timeRemaining--;
                    }
                }.runTaskTimer(Main.getInstance(), 0, 20);
            } else {
                if (cdFly > 60*7){
                    owner.sendMessage("§7Votre§a Fly§7 prend fin.");
                    owner.setFlying(false);
                    owner.setAllowFlight(false);
                    owner.setFallDistance(0f);
                    isFlying = false;
                    owner.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                    cdFly = 60*7;
                } else {
                    sendCooldown(owner, cdFly);
                }
            }
            return true;
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public void onALLPlayerDamageByEntity(EntityDamageByEntityEvent event, Player victim, Entity entity) {
        super.onALLPlayerDamageByEntity(event, victim, entity);
        if (!victim.getUniqueId().equals(owner.getUniqueId())){
            UUID damager = getUuid(entity);
            if (damager != null){
                if (RandomUtils.getOwnRandomProbability(25.0) && passifActive){
                    givePotionEffet(victim, PotionEffectType.WITHER, 80, 1, true);
                }
            }
        }
    }

    private UUID getUuid(Entity entity) {
        UUID damager = null;
        if (entity.getUniqueId().equals(owner.getUniqueId())){
            damager = entity.getUniqueId();
        }
        if (entity instanceof Projectile){
            if (((Projectile) entity).getShooter() instanceof Entity){
                if (((Entity) ((Projectile) entity).getShooter()).getUniqueId().equals(owner.getUniqueId())){
                    damager = ((Entity) ((Projectile) entity).getShooter()).getUniqueId();
                }
            }
        }
        return damager;
    }
}
