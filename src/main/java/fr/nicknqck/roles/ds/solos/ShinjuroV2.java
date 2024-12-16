package fr.nicknqck.roles.ds.solos;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ShinjuroV2 extends DemonsSlayersRoles {
    private final ItemStack SakeVide = new ItemBuilder(Material.GLASS_BOTTLE).setName("§6Sake§7 (§fVide§7)").setDroppable(false).toItemStack();
    private final ItemStack SakeRemplie = new ItemBuilder(Material.POTION).setDurability(0).setName("§6Sake§7 (§fRemplie§7)").setDroppable(false).toItemStack();
    private int SakeBar = 0;
    private int cdSake;
    private boolean flamme = false;
    private enum State {
        Nothing(0, 0),
        Fire(25, 0),
        Bower(50, 25),
        Meleer(100, 50),
        Regeneration(100, 75);
        private final int melee;
        private final int distance;
        State(int melee, int distance) {
            this.melee = melee;
            this.distance = distance;
        }
    }
    private State state = State.Nothing;
    public ShinjuroV2(UUID player) {
        super(player);

    }

    @Override
    public void GiveItems() {
        giveItem(owner, false, getItems());
        giveItem(owner, true, Items.getLamedenichirin());
    }

    @Override
    public void RoleGiven(GameState gameState) {
        Lames.FireResistance.getUsers().put(owner.getUniqueId(), Integer.MAX_VALUE);
        setCanuseblade(true);

        new ShinjuroRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        new SakePower(this);
    }

    @Override
    public String getName() {
        return "Shinjuro§7 (§6V2§7)";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.ShinjuroV2;
    }

    @Override
    public TeamList getOriginTeam() {
        return TeamList.Solo;
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§eShinjuro",
                AllDesc.objectifsolo+"§eSeul",
                "",
                "§lEffets:",
                "",
                AllDesc.point+"§e Speed I§f permanent",
                "",
                "§lItem:",
                "",
                AllDesc.point+"§6Sake§r: Ce remplit avec un§c clique droit§f sur une§c source d'eau§r, cette bouteille vous permet de remplir votre§c jauge d'alcool§r, chaque stade se cumule avec les bonus des stades précédents",
                AllDesc.tab+"§b20%§r: §c+25%§f de§c chance§f d'infliger des§c coups enflammé",
                AllDesc.tab+"§b40%§r: §c+25%§f de§c chance§f d'infliger des§c coups enflammé§f et§c +25%§f de§c chance§f que vos§c flèches§f enflamme le§c joueur§f toucher",
                AllDesc.tab+"§b60%§r: §c+50%§f de§c chance§f d'infliger des§c coups enflammé§f et§c +25%§f de§c chance§f que vos§c flèches§f enflamme le§c joueur§f toucher",
                AllDesc.tab+"§b80%§r: §c+25%§f de§c chance§f que vos§c flèches§f enflamme§f le§c joueur§f toucher et vous vous§c régénérerez§f de§c 1/2"+AllDesc.coeur+"§f toute les§c 6 secondes§f en§c feu",
                "",
                "§7(Vous perdez§c 1%§7 d'§calcool§7 toute les§c 6 secondes§7)",
                "",
                "§lCommande:",
                "",
                AllDesc.point+"§6/ds flamme§f: §aActivable§f/§cDésactivable§f, ce pouvoir vous donne l'effet§c Fire Résistance I§f de manière§c permanente§f ainsi que les bonus donner par votre§6 Sake§f.",
                "",
                AllDesc.bar
        };
    }



    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                SakeVide
        };
    }
    @Override
    public void resetCooldown() {
        cdSake = 0;
    }

    @Override
    public void onDSCommandSend(String[] args, GameState gameState) {
        if (args[0].equalsIgnoreCase("flamme")) {
            this.flamme = !flamme;
            owner.sendMessage("§7Vous avez "+(flamme ? "§aactiver" : "§cdésactiver")+"§7 votre passif");
            if (!flamme) {
                owner.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
            }
        }
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.FLAMME;
    }

    private static class ShinjuroRunnable extends BukkitRunnable {
        private final ShinjuroV2 shinjuro;
        private int timeInFlame = 0;
        private int timeReduceSake = 6;
        private ShinjuroRunnable(ShinjuroV2 shinjuro){
            this.shinjuro = shinjuro;
        }
        @Override
        public void run() {
            if (shinjuro.getGameState().getServerState() != GameState.ServerStates.InGame) {
                cancel();
                return;
            }
            Player owner = Bukkit.getPlayer(shinjuro.getPlayer());
            if (owner != null){
                shinjuro.givePotionEffet(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false);
                if (shinjuro.flamme) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> owner.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false), true));
                }
                if (shinjuro.cdSake >= 0){
                    shinjuro.cdSake--;
                    if (shinjuro.cdSake == 0){
                        owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§c gourde§7.");
                    }
                }
                if (timeReduceSake > 0) {
                    timeReduceSake--;
                }
                if (shinjuro.SakeBar > 0 && timeReduceSake == 0) {
                    shinjuro.SakeBar--;
                    timeReduceSake = 6;
                }
                NMSPacket.sendActionBar(owner, "§bTaux d'alcoolémie: "+shinjuro.getGameState().sendIntBar(shinjuro.SakeBar, 100, 1)+"§7 (§b"+shinjuro.SakeBar+"%§7)");
                State state = getState();
                if (state != shinjuro.state) {
                    shinjuro.state = state;
                }
                if (shinjuro.state.equals(State.Regeneration) && shinjuro.flamme) {
                    if (owner.getFireTicks() > 0) {
                        timeInFlame++;
                    }
                    if (timeInFlame == 6) {
                        shinjuro.Heal(owner, 1);
                        timeInFlame = 0;
                    }
                }
            }
        }
        private State getState() {
            State state = shinjuro.state;
            if (state.equals(State.Nothing) && shinjuro.SakeBar >= 20) {
                state = State.Fire;
            } else if (state.equals(State.Fire) && shinjuro.SakeBar >= 40){
                state = State.Bower;
            } else if (state.equals(State.Bower) && shinjuro.SakeBar >= 60) {
                state = State.Meleer;
            } else if (state.equals(State.Meleer) && shinjuro.SakeBar >= 80) {
                state = State.Regeneration;
            } else if (state.equals(State.Regeneration) && shinjuro.SakeBar < 80) {
                state = State.Meleer;
            } else if (state.equals(State.Meleer) && shinjuro.SakeBar < 60) {
                state = State.Bower;
            } else if (state.equals(State.Bower) && shinjuro.SakeBar < 40) {
                state = State.Fire;
            } else if (state.equals(State.Fire) && shinjuro.SakeBar < 20) {
                state = State.Nothing;
            }
            return state;
        }
    }
    private static class SakePower implements Listener {
        private final ShinjuroV2 shinjuro;
        private boolean gameEnded = false;
        private SakePower(ShinjuroV2 shinjuro) {
            this.shinjuro = shinjuro;
            Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
        }
        @EventHandler
        private void onEndGame(EndGameEvent event){
            this.gameEnded = true;
            this.shinjuro.flamme = false;
            HandlerList.unregisterAll(this);
        }
        @EventHandler
        private void onPlayerInteract(PlayerInteractEvent event) {
            if (!gameEnded){
                if (event.getPlayer().getUniqueId().equals(this.shinjuro.getPlayer())) {
                    if (event.getPlayer().getItemInHand().isSimilar(this.shinjuro.SakeVide)) {
                        event.setCancelled(true);
                        event.getPlayer().updateInventory();
                        if (event.getClickedBlock() == null)return;
                        Block block = event.getPlayer().getWorld().getBlockAt(new Location(event.getClickedBlock().getWorld(), event.getClickedBlock().getX(), event.getClickedBlock().getY()+1, event.getClickedBlock().getZ()));
                        if (event.getClickedBlock().getType().name().contains("WATER") || block.getType().name().contains("WATER")){
                            event.getPlayer().setItemInHand(shinjuro.SakeRemplie);
                            event.getPlayer().sendMessage("§7Vous avez remplie votre fiole.");
                        }
                        event.setCancelled(true);
                        event.getPlayer().updateInventory();
                    } else if (event.getPlayer().getItemInHand().isSimilar(this.shinjuro.SakeRemplie)) {
                        if (shinjuro.cdSake <= 0){
                            event.getPlayer().setItemInHand(shinjuro.SakeVide);
                            int rdm = RandomUtils.getRandomInt(5, 20);
                            if (rdm + shinjuro.SakeBar > 100) {
                                shinjuro.SakeBar = 100;
                            } else {
                                shinjuro.SakeBar += rdm;
                            }
                            shinjuro.cdSake += 10;
                            event.getPlayer().sendMessage("§7Vous avez gagner§b "+rdm+"%§7 d'alcool.");
                            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false));
                            event.setCancelled(true);
                        } else {
                            shinjuro.sendCooldown(event.getPlayer(), shinjuro.cdSake);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
        @EventHandler
        private void onBattle(EntityDamageByEntityEvent event){
            if (!gameEnded && shinjuro.flamme) {
                if (event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();
                    if (damager.getUniqueId().equals(shinjuro.getPlayer())) {
                        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) && RandomUtils.getOwnRandomProbability(shinjuro.state.melee)) {
                            event.getEntity().setFireTicks(15*20);
                        }
                    }
                } else if (event.getDamager() instanceof Projectile) {
                    if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                        Player shooter = (Player) ((Projectile) event.getDamager()).getShooter();
                        if (shooter.getUniqueId().equals(shinjuro.getPlayer())) {
                            if (RandomUtils.getOwnRandomProbability(shinjuro.state.distance)) {
                                event.getEntity().setFireTicks(15*20);
                            }
                        }
                    }
                }
            }
        }
    }
}
