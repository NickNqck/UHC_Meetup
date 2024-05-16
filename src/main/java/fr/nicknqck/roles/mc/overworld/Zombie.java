package fr.nicknqck.roles.mc.overworld;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Zombie extends RoleBase {

    private final ItemStack CerveauItem = new ItemBuilder(Material.ROTTEN_FLESH).setName("§cCerveau").setLore("§7Vous permez de dévorrez un cerveau pour vous rendre plus fort").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
    private int cdCerveau = 0;
    private int nmbCerveau = 1;
    private boolean CerveauActive = false;
    public Zombie(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§aZombie",
                AllDesc.objectifteam+"§aOverWorld",
                "",
                AllDesc.effet,
                "",
                AllDesc.point+"Vous possédez "+AllDesc.Force+" 1 la §8nuit ainsi que "+AllDesc.weak+" 1 le §ejour",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§cCerveau :§r Si vous l'activer la nuit vous gagnerez 10% de "+AllDesc.Resi+", mais si vous venez à l'utiliser le jour vous gagnerez "+AllDesc.Force+" 1 et perdrez votre "+AllDesc.weak+" 1 pendant 1 minute, cependant après cette minute vous obtiendrez "+AllDesc.slow+" 1 pendant 2 minutes. (1x/3minutes)",
                "",
                AllDesc.commande,
                "",
                AllDesc.point+"§c/mc cerveau :§r vous permez de voir votre nombre d'utilisation(s) restante(s)",
                "",
                AllDesc.particularite,
                "",
                "Dès que vous tuez un joueur vous obtenez une utilisation supplémentaire de §cCerveau",
                //"La prémière fois que vous croiserez le joueur possédant le rôle skelette vous entendrez un bruit de skelette",
                "",
                AllDesc.bar,
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                CerveauItem,
        };
    }

    @Override
    public void resetCooldown() {
                cdCerveau = 0;
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(CerveauItem)){
            if (cdCerveau <= 0){
                if (nmbCerveau >= 1){
                    owner.sendMessage("Vous venez de manger un cerveau et devenez donc plus fort");
                    cdCerveau = 180;
                    new BukkitRunnable() {
                        int i = 0;
                        @Override
                        public void run() {
                            if (gameState.getServerState() == GameState.ServerStates.InGame){
                                i++;
                                if (i <= 60) {
                                    CerveauActive = true;
                                    if (gameState.nightTime) {
                                        setResi(10);
                                        givePotionEffet(PotionEffectType.DAMAGE_RESISTANCE, 2, 1, false);
                                    } else {
                                        givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 2, 1, true);
                                    }
                                } else {
                                    if (i <= 180){
                                        setResi(20);
                                        givePotionEffet(PotionEffectType.SLOW, 2, 1 ,true);
                                        if (i == 61){
                                            owner.sendMessage("Vous perdez votre puissance et obtenez "+AllDesc.slow+" pendant 2 minutes");

                                        }
                                    } else {
                                        cancel();
                                    }
                                }
                            } else {
                                cancel();
                            }

                        }
                    }.runTaskTimer(Main.getInstance(), 0, 20);
                } else {
                    owner.sendMessage("Vous ne possédez plus de §cCerveau §rà dévorrer");
                    return true;
                }
            } else {
                sendCooldown(owner, cdCerveau);
                return true;
            }
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public void PlayerKilled(Player killer, Player victim, GameState gameState) {
        if (killer.getUniqueId() == owner.getUniqueId()){
            if (getIGPlayers().contains(victim)){
                owner.sendMessage("Vous venez de tuer un joueur vous obtenez donc un §cCerveau §rsupplémentaire et vous donc à §c"+nmbCerveau);
                nmbCerveau++;
            }
        }
        super.PlayerKilled(killer, victim, gameState);
    }

    @Override
    public void Update(GameState gameState) {
        if (cdCerveau >= 0){
            cdCerveau --;
            if (cdCerveau == 0){
                owner.sendMessage("Vous pouvez de nouveau utiliser §cCerveau");
            }
        }
        if (gameState.nightTime){
            givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 2 , 1 ,false);
        } else {
            if (!CerveauActive){
                givePotionEffet(PotionEffectType.WEAKNESS, 2,1,false);
            }
        }
        super.Update(gameState);
    }

    @Override
    public void onMcCommand(String[] args) {
        if (args.length == 1){
            if (args[0].equalsIgnoreCase("Cerveau")){
                owner.sendMessage("Il vous reste §c"+nmbCerveau+" Cerveau");
            } else {
                owner.sendMessage("Veuillez indiquer une commande correct");
            }
        }
        super.onMcCommand(args);
    }
}
