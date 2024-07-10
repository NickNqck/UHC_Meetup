package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Kanae extends PillierRoles implements Listener {
    private final ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 3).setName("§aLame végétal").setUnbreakable(true).setDroppable(false).toItemStack();
    private int cooldown = 0;
    private final KanaeRunnable runnable;
    public Kanae(Player player) {
        super(player);
        new SwordListener(this);
        setCanuseblade(true);
        this.runnable = new KanaeRunnable(this);
        this.runnable.runTaskTimerAsynchronously(Main.getInstance(), 0 ,20);
        giveItem(player, false, getItems());
        giveItem(player, false, Items.getLamedenichirin());
        owner.sendMessage(Desc());
    }

    @Override
    public String getName() {
        return "§aKanae";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Kanae;
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+getName(),
                AllDesc.objectifteam+(getTeam() != null ? getTeam().getColor()+getTeam().name() : "§aSlayers"),
                "",
                AllDesc.items,
                "",
                AllDesc.point+sword.getItemMeta().getDisplayName()+"§f: A chaque coup d'épée vous aurez un§c pourcentage de chance§f d'infliger des§c effets négatifs§f.",
                AllDesc.tab+"§fPourcentage: ","",
                AllDesc.point+"§c25%§f de ne rien faire du tout","",
                AllDesc.point+"§c25%§f d'infliger§c Weakness I§f pendant§c 15s","",
                AllDesc.point+"§c20%§f d'infliger§c Slowness I§f pendant§c 12s","",
                AllDesc.point+"§c15%§f d'infliger§c Poison I§f pendant§c 10s","",
                AllDesc.point+"§c10%§f d'infliger de vous§c soignez§f de§c 2"+AllDesc.Coeur("§c"),"",
                AllDesc.point+"§c5%§f d'infliger§c Weakness I§f,§c Slowness I§f et§c Poison I§f pendant§c 10s",
                "",
                AllDesc.bar
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
            sword
        };
    }

    @Override
    public void resetCooldown() {
        cooldown = 0;
    }
    private static class SwordListener implements Listener {
        private boolean ended = false;
        private final Kanae kanae;
        private SwordListener(Kanae kanae){
            this.kanae = kanae;
            Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
        }
        @EventHandler
        private void onBattle(EntityDamageByEntityEvent event){
            if (!(event.getDamager() instanceof Player))return;
            if (!(event.getEntity() instanceof Player))return;
            if (ended || kanae.getPlayer() == null)return;
            if (kanae.cooldown > 0)return;
            if (!((Player) event.getDamager()).getItemInHand().isSimilar(kanae.sword))return;
            if (kanae.getPlayer().equals(event.getDamager().getUniqueId())) {
                StringBuilder toKanae = new StringBuilder("§c"+((Player) event.getEntity()).getDisplayName()+"§7 à reçus§c ");
                StringBuilder toVictim = new StringBuilder("§7Vous avez reçus§c ");
                int rdm = Main.RANDOM.nextInt(100);
                System.out.println("rdm "+rdm);
                if (rdm < 25) {
                    toKanae = null;
                    toVictim = null;
                }
                if (rdm >= 25 && rdm < 50) {
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*15, 0, false, false), true);
                    toKanae.append("15 secondes§7 de§c Weakness§7.");
                    toVictim.append("15 secondes§7 de§c Weakness§7");
                } else if (rdm >= 50 && rdm < 70) {
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*12, 0, false, false), true);
                    toKanae.append("12 secondes§7 de§c Slowness§7.");
                    toVictim.append("12 secondes§7 de§c Slowness§7");
                } else if (rdm >= 70 && rdm < 85) {
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*10, 0, false, false), true);
                    toKanae.append("10 secondes§7 de§c Poison§7.");
                    toVictim.append("10 secondes§7 de§c Poison§7");
                } else if (rdm >= 85 && rdm < 95){
                    toKanae = null;
                    toVictim = null;
                    kanae.Heal((Player) event.getDamager(), 4);
                    event.getDamager().sendMessage("§7Vous vous êtes soigner de§c 2"+AllDesc.coeur+"§7.");
                } else if (rdm >= 95) {
                    toKanae.append("10 secondes§7 de§c Weakness§7,§c Slowness§7 et§c Poison§7.");
                    toVictim.append("10 secondes§7 de§c Weakness§7,§c Slowness§7 et§c Poison");
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*10, 0, false, false), true);
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*10, 0, false, false), true);
                    ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*10, 0, false, false), true);
                }
                kanae.cooldown = 40;
                if (toVictim != null && !toVictim.toString().equals("§7Vous avez reçus§c ")) {
                    toVictim.append("§7 de la part de§a Kanae");
                    event.getEntity().sendMessage(toVictim.toString());
                    event.getDamager().sendMessage(toKanae.toString());
                }
            }
        }
        @EventHandler
        private void onEndGame(EndGameEvent event){
            kanae.runnable.cancel();
            ended = true;
            HandlerList.unregisterAll(this);
        }
    }
    private static class KanaeRunnable extends BukkitRunnable {
        private final Kanae kanae;
        private KanaeRunnable(Kanae kanaev2){
            this.kanae = kanaev2;
        }
        @Override
        public void run() {
            if (kanae.cooldown >= 0){
                kanae.cooldown--;
                if (kanae.cooldown == 0){
                    assert kanae.owner != null;
                    kanae.owner.sendMessage("§7Vous pouvez à nouveau utiliser le poison de votre "+kanae.sword.getItemMeta().getDisplayName());
                }
            }
        }
    }
}
