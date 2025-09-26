package fr.nicknqck.events.ds.dkt;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.ds.Event;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.demons.lune.KokushiboV2;
import fr.nicknqck.roles.ds.slayers.Tanjiro;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
                                if (gamePlayer.getRole() instanceof KokushiboV2) {
                                    KokushiboV2 kokushibo = (KokushiboV2) gamePlayer.getRole();
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
                                        kokushibo.givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*3, 0, false, false), EffectWhen.AT_KILL);
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
        return gameState.getAttributedRole().contains(Roles.Tanjiro) &&
                gameState.getAttributedRole().contains(Roles.Muzan) &&
                gameState.DeadRole.contains(Roles.Muzan) &&
                !gameState.DeadRole.contains(Roles.Tanjiro);
    }

    @Override
    public boolean isActivated() {
        return this.activated;
    }

    @Override
    public String[] getExplications() {
        return new String[] {
                "§7Au moment de l'activation si§a Tanjiro§7 est en§a vie§7 et que§c Muzan§7 est§c mort§7",
                "§7alors§a Tanjiro§7 deviendra§c démon§7, il perdra l'accès à tout ses pouvoirs",
                "§7et gagnera les effets§e Speed I§7,§9 Résistance I§7 et§c Force I§7 de manière§c permanente§7.",
                "§7il recevra aussi un objet nommé \"§f§lBoule d'énergie§7\"",
                "§7lui permettant en frappant un joueur de lui infliger§c 2❤§7 de§c dégâts§7 via une§c explosion§7.",
                "",
                "§c60 secondes§7 après que§a Tanjiro§7 sois devenue un§c Démon§7 un message dans le chat annoncera que",
                "§7l'event c'est déclencher encore§c 10 secondes§7 plus tard§c Kokushibo§7 deviendra un rôle§e Solitaire§7,",
                "§7pour l'aider il aura§c 15❤ permanents§7 ainsi que§9 Résistance I§7 pendant§c 3 minutes§7 en tuant un joueur"
        };
    }
}
