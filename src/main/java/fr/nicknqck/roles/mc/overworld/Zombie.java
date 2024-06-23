package fr.nicknqck.roles.mc.overworld;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.mc.builders.OverWorldRoles;
import fr.nicknqck.utils.ItemBuilder;
import fr.nicknqck.utils.Loc;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Zombie extends OverWorldRoles {

    private final ItemStack CerveauItem = new ItemBuilder(Material.ROTTEN_FLESH).setName("§cCerveau").setLore("§7Vous permez de dévorrez un cerveau pour vous rendre plus fort").addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
    private int cdCerveau = 0;
    private int nmbCerveau = 1;
    private boolean CerveauActive = false;
    private boolean SqueletteSound = false;
    public Zombie(Player player) {
        super(player);
        owner.sendMessage(Desc());
        giveItem(owner, false, getItems());
    }
    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Zombie;
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
                AllDesc.point+"§cCerveau :§r Si vous l'activer la §8nuit §rvous gagnerez §410% §rde "+AllDesc.Resi+", mais si vous venez à l'utiliser le §ejour §rvous gagnerez "+AllDesc.Force+" §c1 §ret perdrez votre "+AllDesc.weak+" §71 §rpendant 1 minute, cependant après cette minute vous obtiendrez "+AllDesc.slow+" §41 §rpendant 2 minutes. §7(1x/3minutes)",
                "",
                AllDesc.commande,
                "",
                AllDesc.point+"§c/mc cerveau :§r vous permez de voir votre nombre §4d'utilisation(s) restante(s)§r, sur votre item §cCerveau",
                "",
                AllDesc.particularite,
                "",
                "Dès que vous tuez un joueur vous obtenez §cune utilisation supplémentaire §rde §cCerveau",
                "La prémière fois que vous croiserez le joueur possédant le rôle squelette vous entendrez un bruit de squelette",
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
    public String getName() {
        return "§aZombie";
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
                    owner.sendMessage("Vous venez de manger un §ccerveau §ret devenez donc plus fort");
                    cdCerveau = 180;
                    new BukkitRunnable() {
                        int i = 0;
                        @Override
                        public void run() {
                            if (gameState.getServerState() == GameState.ServerStates.InGame){
                                i++;
                                if (i <= 180 ) {
                                    if (i <= 60){
                                        CerveauActive = true;
                                        if (gameState.nightTime){
                                            addBonusResi(10);
                                        } else {
                                            givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 2*20, 1, true);
                                        }
                                    } else {
                                        givePotionEffet(PotionEffectType.SLOW, 2*20, 1, true);
                                    }
                                } else {
                                    owner.sendMessage("Vous perdez votre "+AllDesc.slow+" §41 §ret vous pouvez manger encore §c"+nmbCerveau+" Cerveau");
                                    cancel();
                                }

                                if (i == 60){
                                    owner.sendMessage("Vous perdez la puissance du §cCerveaur dévorrer §ret obtenez donc "+AllDesc.slow+" §41 §rpendant §42 §rminutes");
                                    CerveauActive = false;
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
                owner.sendMessage("Vous pouvez de nouveau manger un §cCerveau");
            }
        }
        if (gameState.nightTime){
            givePotionEffet(PotionEffectType.INCREASE_DAMAGE, 3*20 , 1 ,false);
        } else {
            if (!CerveauActive){
                givePotionEffet(PotionEffectType.WEAKNESS, 3*20,1,false);
            }
        }
        super.Update(gameState);
    }

    @Override
    public void onMcCommand(String[] args) {
        if (args.length == 1){
            if (args[0].equalsIgnoreCase("Cerveau")){
                owner.sendMessage("Il vous reste §c"+nmbCerveau+" Cerveau");
            }
        }
        super.onMcCommand(args);
    }
    @Override
    public void onAllPlayerMoove(PlayerMoveEvent e, Player moover) {
        if (moover.getUniqueId() == owner.getUniqueId()){
            if (!SqueletteSound) {
                for (Player p : Loc.getNearbyPlayersExcept(owner, 15)) {
                    if (getPlayerRoles(p) instanceof Zombie) {
                        playSound(owner, "entity.squelette.ambient");
                        SqueletteSound = true;
                    }
                }
            }
        }
        super.onAllPlayerMoove(e, moover);
    }
}
