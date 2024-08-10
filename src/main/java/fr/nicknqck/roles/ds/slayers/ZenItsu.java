package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ZenItsu extends SlayerRoles implements Listener {
    private final ItemStack vitesseItem = new ItemBuilder(Material.NETHER_STAR).setName("§eVitesse").setUnbreakable(true).setDroppable(false).toItemStack();
    private int cdVitesse = -1, cdPassif = -1, cdEclair;

    public ZenItsu(UUID player) {
        super(player);
        setCanuseblade(true);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
        new ZenitsuRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
    }

    @Override
    public void GiveItems() {
        giveItem(owner, false, getItems());
        giveItem(owner, false, Items.getLamedenichirin());
    }

    @Override
    public String getName() {
        return "Zen'Itsu";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.ZenItsu;
    }

    @Override
    public void resetCooldown() {
        cdVitesse = 0;
        cdPassif = 0;
        cdEclair = 0;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public TextComponent getComponent() {
        TextComponent text = new TextComponent(AllDesc.bar+"\n");
        text.addExtra("§7Role: §a"+getName()+"\n§7Votre objectif est de gagner avec le camp des§a Slayers\n\n");
        text.addExtra(AllDesc.point+"§7Lorsque votre vie est en dessous de§c 5"+AllDesc.coeur+"§7 vous aurez les §7effets§c Speed II§7 et§c Force I\n\n");
        text.addExtra(AllDesc.point+"§7Vous possédez l'item ");
        text.addExtra(getVitesseText());
        text.addExtra("§7 (1x/10m).\n\n");
        text.addExtra(AllDesc.point+"§7Vous avez accès à la commande ");
        text.addExtra(getPassifText());
        text.addExtra("§7 (1x/15m).\n\n");
        text.addExtra(AllDesc.bar);
        return text;
    }
    private TextComponent getVitesseText(){
        TextComponent text = new TextComponent("§7\""+vitesseItem.getItemMeta().getDisplayName()+"§7\"");
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(
                "§7Vous donne l'effet§c Speed III§7 pendant§c 1 minutes§7, puis, vous donne les effets§c Weakness II§7 et§c Slowness II§7 pendant§c 3 minutes§7. (1x/10m)")}));
        return text;
    }
    private TextComponent getPassifText(){
        TextComponent text = new TextComponent("§c/ds passif");
        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                new TextComponent("§7Pendant§c 5 minutes§7 vous aurez§c 10%§7 de§c chance§7 d'infliger§c 2"+AllDesc.coeur+"§7 de dégat via un§e éclair§7. (1x/15m)")
        }));
        return text;
    }
    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                vitesseItem
        };
    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        HandlerList.unregisterAll(this);
    }
    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getItem().isSimilar(vitesseItem)) {
                if (event.getPlayer().getUniqueId().equals(getPlayer())) {
                    if (cdVitesse > 0) {
                        sendCooldown(event.getPlayer(), cdVitesse);
                        return;
                    }
                    cdVitesse = 60*13;
                    new BukkitRunnable() {
                        final UUID uuidUser = getPlayer();
                        int timeRemaining = 60*3;
                        @Override
                        public void run() {
                            Player player = Bukkit.getPlayer(uuidUser);
                            if (player != null) {
                                if (timeRemaining > 60*2) {
                                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 2, false, false), true));
                                } else {
                                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1, false, false), true);
                                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1, false, false), true);
                                    });
                                }
                                timeRemaining--;
                                NMSPacket.sendActionBar(owner, "§bTemp restant:§c "+ StringUtils.secondsTowardsBeautiful(cdVitesse-(60*10)));
                            }
                            if (timeRemaining == 0 || !GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                                cancel();
                            }
                        }

                    }.runTaskTimerAsynchronously(Main.getInstance(), 0,20);
                }
            }
        }
    }
    @EventHandler
    private void onBattle(EntityDamageByEntityEvent event) {
        if (event.getDamager().getUniqueId().equals(getPlayer()) && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if (cdPassif >= 60*15) {
                if (RandomUtils.getOwnRandomProbability(10)) {
                    if (cdEclair > 0) {
                        if (Main.isDebug()) {
                            System.out.println("ZenItsu"+((Player) event.getDamager()).getDisplayName()+" aurait du infliger un éclair au joueur "+((Player) event.getEntity()).getDisplayName());
                        }
                        return;
                    }
                    Player victim = (Player) event.getEntity();
                    if (victim.getHealth() > 2.0) {
                        victim.setHealth(victim.getHealth() - 2.0);
                    } else {
                        victim.setHealth(0.1);
                    }
                    cdEclair = 7;
                    owner.sendMessage(ChatColor.GREEN+"Vous avez touchez : "+ ChatColor.GOLD + victim.getName());
                    victim.sendMessage("§aZenItsu§7 vous à fait perdre 1"+AllDesc.coeur+"§7 suite à votre§e Foudroyage");
                    victim.getWorld().strikeLightningEffect(victim.getLocation());
                }
            }
        }
    }
    private static class ZenitsuRunnable extends BukkitRunnable {
        private final ZenItsu zenitsu;
        private ZenitsuRunnable(ZenItsu zenItsurugi){
            this.zenitsu = zenItsurugi;
        }
        @Override
        public void run() {
            if (zenitsu.getGameState().getServerState() != GameState.ServerStates.InGame) {
                System.out.println("Cancelled zenitsu runnable");
                cancel();
                return;
            }
            if (zenitsu.cdVitesse > 0) {
                zenitsu.cdVitesse--;
            }
            if (zenitsu.cdPassif > 0) {
                zenitsu.cdPassif--;
            }
            if (zenitsu.cdEclair >= 0) {
                zenitsu.cdEclair--;
            }
            Player owner = Bukkit.getPlayer(zenitsu.getPlayer());
            if (owner != null) {
                if (zenitsu.cdVitesse == 0) {
                    zenitsu.cdVitesse--;
                    owner.sendMessage("§7Vous pouvez à nouveau utiliser votre "+zenitsu.vitesseItem.getItemMeta().getDisplayName());
                }
                if (zenitsu.cdPassif == 0) {
                    zenitsu.cdPassif--;
                    owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§c /ds passif");
                }
                if (zenitsu.cdPassif >= 60*15) {
                    NMSPacket.sendActionBar(owner, "§bTemp restant:§c "+StringUtils.secondsTowardsBeautiful(zenitsu.cdPassif-(60*15)));
                }
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                    double maxHealth = owner.getMaxHealth();
                    if (owner.getHealth() <= maxHealth/2) {
                        if (zenitsu.cdVitesse <= 60*12) {
                            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1, false, false), true);
                        }
                        owner.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), true);
                    }
                });
            }
        }
    }

    @Override
    public void onDSCommandSend(String[] args, GameState gameState) {
        if (args[0].equalsIgnoreCase("passif")) {
            if (cdPassif > 0) {
                sendCooldown(Bukkit.getPlayer(getPlayer()), cdPassif);
                return;
            }
            cdPassif = 60*20;
            Bukkit.getPlayer(getPlayer()).sendMessage("§7Vous venez d'activé votre§c /ds passif");
        }
    }
}
