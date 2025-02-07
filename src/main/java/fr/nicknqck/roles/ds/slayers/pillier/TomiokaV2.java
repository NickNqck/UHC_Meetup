package fr.nicknqck.roles.ds.slayers.pillier;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.slayers.Makomo;
import fr.nicknqck.roles.ds.slayers.Sabito;
import fr.nicknqck.roles.ds.slayers.Tanjiro;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TomiokaV2 extends PilierRoles {

    public TomiokaV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.EAU;
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public String getName() {
        return "Tomioka";
    }

    @Override
    public @NonNull GameState.Roles getRoles() {
        return GameState.Roles.Tomioka;
    }

    @Override
    public void resetCooldown() {}

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .addCustomLine("§7Vous possédez un livre enchanter§b Depth Strider III")
                .setPowers(getPowers())
                .getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
        ItemStack Book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta BookMeta = (EnchantmentStorageMeta) Book.getItemMeta();
        BookMeta.addStoredEnchant(Enchantment.DEPTH_STRIDER, 3, false);
        Book.setItemMeta(BookMeta);
        giveItem(owner, false, Book);
        addPower(new FindOthersPower(this));
        addPower(new DeathPassifPower(this));
        addPower(new AccalmiePower(this), true);
        if (!Main.getInstance().getGameConfig().isMinage()) {
            owner.setLevel(owner.getLevel()+6);
        }
    }

    private static class FindOthersPower extends Power {

        public FindOthersPower(@NonNull RoleBase role) {
            super("§7(§cPassif§7) Chercheur d'utilisateur du§b Soufle de l'Eau", null, role,
                    "§7Permet de savoir toute les "+(Main.getInstance().getGameConfig().isMinage() ? "§c10 minutes" : "§c3 minutes")+"§7 si un utilisateur du Soufle de l'§bEau§7 est présent autours de vous ou non");
            new FinderRunnable(role.getGameState(), this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            return true;
        }
        private static class FinderRunnable extends BukkitRunnable {

            private final GameState gameState;
            private final FindOthersPower power;
            private final int maxTime;
            private int actualTime;


            private FinderRunnable(GameState gameState, FindOthersPower power) {
                this.gameState = gameState;
                this.power = power;
                if (Main.getInstance().getGameConfig().isMinage()) {
                    maxTime = 60*10;
                } else {
                    maxTime = 60*3;
                }
                this.actualTime = maxTime;
            }

            @Override
            public void run() {
                if (!gameState.getServerState().equals(GameState.ServerStates.InGame)) {
                    cancel();
                    return;
                }
                if (actualTime == 0) {
                    GamePlayer gamePlayer = power.getRole().getGamePlayer();
                    if (!gamePlayer.isAlive()) {
                        return;
                    }
                    Player owner = Bukkit.getPlayer(gamePlayer.getUuid());
                    if (owner != null) {
                        boolean isPresent = false;
                        for (Player p : Loc.getNearbyPlayersExcept(owner, 20)) {
                            if (!gameState.hasRoleNull(p.getUniqueId())) {
                                GamePlayer gm = gameState.getGamePlayer().get(p.getUniqueId());
                                if (gm.isAlive()) {
                                    if (gm.getRole() instanceof DemonsSlayersRoles) {
                                        if (((DemonsSlayersRoles) gm.getRole()).getSoufle().equals(Soufle.EAU) || ((DemonsSlayersRoles) gm.getRole()).getSoufle().equals(Soufle.TOUS)) {
                                            owner.sendMessage("§7Quelque chose vous fait pensez qu'un utilisateur du§b Soufle de l'Eau§7 est proche de vous (§c20 blocs§7)");
                                            isPresent = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (!isPresent) {
                            owner.sendMessage("§7Vous ne sentez aucun autre utilisateur du§b Soufle de l'eau§7 autours de vous");
                        }
                    }
                    actualTime = maxTime;
                }
                final Player owner = Bukkit.getPlayer(this.power.getRole().getPlayer());
                if (owner != null) {
                    if (this.power.checkUse(owner, new HashMap<>()))return;
                }
                actualTime--;
            }
        }
    }
    private static class DeathPassifPower extends Power implements Listener {

        private boolean firstKill = false;

        public DeathPassifPower(@NonNull TomiokaV2 role) {
            super("Pilier de l'eau", null, role,
                    "§7Lors de la §cmort§7 de l'un de vos confrère élève d'§aUrokodaki§7 vous obtiendrez un bonus en fonction du §crôle§7 du§c joueur§7: ",
                    "",
                    "§7     →§aTanjiro§7:§c Force I§7 de§e jour",
                    "",
                    "§7     →§aSabito§7:§9 Résistance I§7 le§e jour",
                    "",
                    "§7     →§aMakomo§7:§b Speed II§7 durant le§e jour",
                    "",
                    "§4!§c Vous obtiendrez les bonus ci-dessus que s'il s'agit du premier mort, sinon vous obtiendrez§a +§c1❤ permanent§4!");
            EventUtils.registerRoleEvent(this);
            Bukkit.getScheduler().runTaskLaterAsynchronously(getPlugin(), () -> {
                boolean ig = false;
                for (final GamePlayer gamePlayer : getRole().getGameState().getGamePlayer().values()) {
                    if (gamePlayer.getRole() == null)continue;
                    if (isGoodRole(gamePlayer.getRole())) {
                        ig = true;
                        break;
                    }
                }
                if (!ig) {
                    Bukkit.getScheduler().runTask(getPlugin(), () -> {
                        EventUtils.unregisterEvents(this);
                        int random = Main.RANDOM.nextInt(101);
                        getRole().getGamePlayer().sendMessage("§7C'est étrange, on dirait que n'y§a Sabito§7, n'y§a Makomo§7, n'y§a Tanjiro§7 ne sont présant dans la partie");
                        String roleName = "";
                        String effectName = "";
                        if (random <= 33) {
                            getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false), EffectWhen.DAY);
                            roleName = "§aSabito";
                            effectName = "§9Résistance I";
                        } else if (random <= 66) {//donc n'est pas inférieur ou égale à 33
                            getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), EffectWhen.DAY);
                            roleName = "§aTanjiro";
                            effectName = "§cForce I";
                        } else if (random <= 99) {
                            final Player owner = Bukkit.getPlayer(getRole().getPlayer());
                            if (owner != null) {
                                owner.removePotionEffect(PotionEffectType.SPEED);
                            }
                            if (!getRole().getEffects().isEmpty()) {
                                for (final PotionEffect potionEffect : getRole().getEffects().keySet()) {
                                    if (potionEffect.getType().equals(PotionEffectType.SPEED)) {
                                        getRole().getEffects().remove(potionEffect);
                                        break;
                                    }
                                }
                            }
                            getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false), EffectWhen.DAY);
                            getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false), EffectWhen.NIGHT);
                            roleName = "§aMakomo";
                            effectName = "§bSpeed II";
                        } else {//donc random est égale à 100
                            getRole().getGamePlayer().sendMessage("§7L'absence de§a Tanjiro§7,§a Makomo§7 et§a Sabito§7 sont trop lourd sur vos épaule, pour vous aider à ne pas faillir vous n'allez gagner aucun des effets que vous auriez dû avoir, mais plutôt§a +§c3❤ permanents");
                            getRole().setMaxHealth(getRole().getMaxHealth()+6.0);
                            final Player owner = Bukkit.getPlayer(getRole().getPlayer());
                            if (owner != null) {
                                owner.setMaxHealth(getRole().getMaxHealth());
                            }
                        }
                        if (!roleName.isEmpty()) {
                            getRole().getGamePlayer().sendMessage("§7Vous allez donc devoir endosser les résponsabilités de la mort de "+roleName+"§7, vous avez maintenant§c l'effet "+effectName+"§7 le§e jour");
                        }
                    });
                }
            }, 20*10);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            return true;
        }
        @EventHandler
        private void UHCDeathEvent(final UHCDeathEvent event) {
            if (event.getRole() == null)return;
            final RoleBase roleBase = event.getRole();
            if (firstKill) {//J'ajoute 1❤ perma a Tomioka
                if (!isGoodRole(roleBase))return;
                getRole().setMaxHealth(getRole().getMaxHealth()+2.0);
                final Player owner = Bukkit.getPlayer(getRole().getPlayer());
                if (owner != null) {
                    owner.setMaxHealth(getRole().getMaxHealth());
                }
                return;//J'empêche Tomioka d'obtenir d'autre bonus que le ❤ perma s'il en a déjà eu 1
            }
            if (roleBase instanceof Tanjiro) {
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0, false, false), EffectWhen.DAY);
                this.firstKill = true;
                getRole().getGamePlayer().sendMessage("§aTanjiro§7 est§c mort§7, vous devez lui faire hônneur en étant aussi fort qu'il l'était, vous obtenez §cl'effet Force I§7 le§e jour");
            } else if (roleBase instanceof Sabito) {
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0, false, false), EffectWhen.DAY);
                this.firstKill = true;
                getRole().getGamePlayer().sendMessage("§aSabito§7, est§c mort§7, vous devez lui montrer qu'il à bien fait de vous sauver la vie, vous obtenez§c l'effet§9 Résistance I§7 le§e jour");
            } else if (roleBase instanceof Makomo) {
                this.firstKill = true;
                final Player owner = Bukkit.getPlayer(getRole().getPlayer());
                if (owner != null) {
                    owner.removePotionEffect(PotionEffectType.SPEED);
                }
                if (!getRole().getEffects().isEmpty()) {
                    for (final PotionEffect potionEffect : getRole().getEffects().keySet()) {
                        if (potionEffect.getType().equals(PotionEffectType.SPEED)) {
                            getRole().getEffects().remove(potionEffect);
                            break;
                        }
                    }
                }
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, false, false), EffectWhen.DAY);
                getRole().givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0, false, false), EffectWhen.NIGHT);
                getRole().getGamePlayer().sendMessage("§7La petite§a Makomo§7 est§c morte§7, en homage à son stylé très fluide vous obtenez§c l'effet§b Speed II§7 le§e jour");
            }
        }
        private boolean isGoodRole(@NonNull final RoleBase roleBase) {
            if (roleBase instanceof Tanjiro) {
                return true;
            }
            if (roleBase instanceof Makomo) {
                return true;
            }
            return roleBase instanceof Sabito;
        }
    }
    private static class AccalmiePower extends ItemPower implements Listener{

        private final Map<UUID, Long> aroundMap;

        protected AccalmiePower(@NonNull TomiokaV2 role) {
            super("Accalmie", new Cooldown(Main.getInstance().getGameConfig().isMinage() ? 60*15 : 60*5), new ItemBuilder(Material.NETHER_STAR).setName("§aAccalmie"), role,
                    "§7Lors de l'activation vous poserez des§c sources d'eau§7 sous les pieds des personnes",
                    "§7qui vont ont frappé pendant les§c 10§7 dernières§c secondes§7, également, vous leurs infligerez un effet de§c Slowness I§7 pendant§c 10 secondes");
            this.aroundMap = new HashMap<>();
            EventUtils.registerRoleEvent(this);
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                long now = System.currentTimeMillis();
                this.aroundMap.entrySet().removeIf(time -> now - time.getValue() > 10000);
                if (this.aroundMap.isEmpty()) {
                    player.sendMessage("§7Vous n'avez personne à calmer avec votre accalmie");
                    return false;
                }
                for (@NonNull final UUID uuid : this.aroundMap.keySet()) {
                    final Player target = Bukkit.getPlayer(uuid);
                    if (target == null)continue;
                    final Location location = target.getLocation();
                    location.getBlock().setType(Material.WATER);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*10, 0, false, false), true);
                }
                return true;
            }
            return false;
        }
        @EventHandler
        private void onBattle(@NonNull final UHCPlayerBattleEvent event) {
            if (!event.getVictim().getUuid().equals(this.getRole().getPlayer()))return;
            if (this.aroundMap.containsKey(event.getDamager().getUuid())) {
                this.aroundMap.remove(event.getDamager().getUuid(), this.aroundMap.get(event.getDamager().getUuid()));
            }
            this.aroundMap.put(event.getDamager().getUuid(), System.currentTimeMillis());
        }
    }
}