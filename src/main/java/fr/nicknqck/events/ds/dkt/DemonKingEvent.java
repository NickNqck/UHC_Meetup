package fr.nicknqck.events.ds.dkt;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.ds.Event;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.demons.lune.Kokushibo;
import fr.nicknqck.roles.ds.slayers.Tanjiro;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DemonKingEvent extends Event {

    private boolean activated = false;

    @Override
    public String getName() {
        return "§cDemon King Tanjiro";
    }

    @Override
    public void onProc(GameState gameState) {
        boolean tanjiroDemon = false;
        for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
                if (gamePlayer.getRole() != null) {
                    if (gamePlayer.getRole() instanceof Tanjiro) {
                        Tanjiro role = (Tanjiro) gamePlayer.getRole();
                        Player owner = Bukkit.getPlayer(role.getPlayer());
                        if (owner != null) {
                            gameState.delInPlayerRoles(owner);
                            DemonKingTanjiroRole newRole = new DemonKingTanjiroRole(role.getPlayer());
                            newRole.setSuffixString(role.getSuffixString());
                            gameState.addInPlayerRoles(owner, newRole);
                            newRole.setGamePlayer(gamePlayer);
                            gamePlayer.setRole(newRole);
                            Main.getInstance().getGetterList().getDemonList(owner);
                            if (role.getLames().equals(Lames.Coeur)) {
                                newRole.setMaxHealth(24.0);
                            } else {
                                newRole.setMaxHealth(20.0);
                            }
                            newRole.setLames(role.getLames());
                            owner.sendMessage("Votre arrivé dans le camp des§c Démons§f restera secrète jusqu'à "+ StringUtils.secondsTowardsBeautiful(gameState.getInGameTime()+60));
                            tanjiroDemon = true;
                            if (role.getTeam() != TeamList.Slayer) {
                                newRole.setTeam(role.getTeam());
                            }
                            break;
                        }
                    }
                }
            }
        if (tanjiroDemon) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                    Bukkit.broadcastMessage(AllDesc.bar+"\n§rL'évènement aléatoire "+ getName()+" viens de ce déclancher, le rôle§c Tanjiro§f est maintenant dans le camp des Démons !\n"+AllDesc.bar);
                    this.activated = true;
                    if (RandomUtils.getOwnRandomProbability(50)) {
                        for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
                            if (gamePlayer.getRole() != null) {
                                if (gamePlayer.getRole() instanceof Kokushibo) {
                                    Kokushibo kokushibo = (Kokushibo) gamePlayer.getRole();
                                    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(Main.class), () -> kokushibo.getGamePlayer().sendMessage("§7Vous sentez des pulsions montez en vous..."), 20);
                                    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(Main.class), () -> kokushibo.getGamePlayer().sendMessage("§7Vous devennez de plus en plus aigri..."), 20*5);
                                    Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getPlugin(Main.class), () -> {
                                        kokushibo.setTeam(TeamList.Solo);
                                        kokushibo.orginalMaxHealth = kokushibo.getMaxHealth();
                                        kokushibo.getGamePlayer().sendMessage(" \n§7La nouvelle de la mort de§c Muzan§7 et maintenant un simple pourfendeur devenue le chef des§c démons§7 qu'elle honte, vous explosez de rage et décidé de§l tué§7 tout le monde\n ");
                                        if (kokushibo.getLames().equals(Lames.Coeur)) {
                                            kokushibo.setMaxHealth(34.0);
                                        }else {
                                            kokushibo.setMaxHealth(30.0);
                                        }
                                        kokushibo.getGamePlayer().sendMessage("Vous posséderez maintenant l'effet "+AllDesc.Resi+" 1 pendant 3 minutes en tuant un joueur");
                                        kokushibo.solo = true;
                                        Player owner = Bukkit.getPlayer(kokushibo.getPlayer());
                                        if (owner != null) {
                                            owner.setMaxHealth(kokushibo.getMaxHealth());
                                            owner.setHealth(owner.getMaxHealth());
                                        }
                                    }, 20*10);
                                    break;
                                }
                            }
                        }
                    }
                }, 20*60);
            }
    }

    @Override
    public ItemStack getMenuItem() {
        return new ItemBuilder(Material.REDSTONE_ORE).setName(getName()).setLore(getLore()).toItemStack();
    }

    @Override
    public boolean canProc(final GameState gameState) {
        return gameState.attributedRole.contains(GameState.Roles.Tanjiro) && gameState.attributedRole.contains(GameState.Roles.Muzan) && gameState.DeadRole.contains(GameState.Roles.Muzan) && !gameState.DeadRole.contains(GameState.Roles.Tanjiro);
    }

    @Override
    public boolean isActivated() {
        return this.activated;
    }
}
