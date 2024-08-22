package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.events.custom.EndGameEvent;
import fr.nicknqck.events.custom.UHCPlayerBattleEvent;
import fr.nicknqck.events.custom.UHCPlayerKillEvent;
import fr.nicknqck.items.Items;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.utils.RandomUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PourfendeurV2 extends SlayerRoles implements Listener {

    private final TextComponent desc;
    @Getter
    private enum Soufles {
        ROCHE("de la§8 Roche"),
        FEU("du§6 Feu"),
        EAU("de l'§bEau"),
        VENT("du§a Vent"),
        FOUDRE("de la§e Foudre"),
        RIEN("rien");
        private final String name;
        Soufles(String name) {
            this.name = name;
        }
    }
    private final List<Soufles> souflesList = new ArrayList<>();
    private final ItemStack soufleVentItem = new ItemBuilder(Material.FEATHER).setName("§aSoufle du Vent").setUnbreakable(true).setDroppable(false).toItemStack();
    private int cdVent, cdFoudre, cdEau;
    private final ItemStack soufleFoudreItem = new ItemBuilder(Material.GLOWSTONE_DUST).setName("§eSoufle de la Foudre").setUnbreakable(true).setDroppable(false).toItemStack();
    private final ItemStack soufleEauItem = new ItemBuilder(Material.NETHER_STAR).setName("§bSoufle de l'Eau").setUnbreakable(true).setDroppable(false).toItemStack();
    public PourfendeurV2(UUID player) {
        super(player);
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.addParticularites(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Au début de la partie un soufle aléatoire vous est définie parmis:\n\n"
        + AllDesc.tab+"§6Soufle du feu§7: A chaque coup §cinfliger§7 vous aurez§c 10%§7 de§c chance§7 que le coup§6 enflamme§7 la§c cible§7.\n\n"
        + AllDesc.tab+"§bSoufle de l'eau§7: Vous obtenez un livre enchanter§b Depth Strider I§7 ainsi qu'un item vous donnant§b Speed I§7 pendant§c 3 minutes§7. (1x/5m)\n\n"
        + AllDesc.tab+"§aSoufle du vent§7: Vous obtenez un item vous donnant§b Speed II§7 pendant§c 2 minutes§7. (1x/4min)\n\n"
        + AllDesc.tab+"§8Soufle de la roche§7: Vous aurez l'effet§8 Résistance I§7 le jour.\n\n"
        + AllDesc.tab+"§eSoufle de la foudre§7: Vous aurez un item vous permettant d'infliger (§caprès activation§7)§c 1❤§7 de§c dégat§7 ainsi que l'effet§l Slowness I§r§7. (1x/3min)")})
        , new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7A chaque§c kill§7 que vous faite vous obtenez un soufle aléatoire parmis ceux cité.")}),
        new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Lorsque vous obtenez le§c cinquième§7 et §cdernier§7 soufle disponnible vous aurez un certain§c pourcentage de chance§7 de: \n\n"
        +AllDesc.tab+"§7(§c5%§7) Devenir§e Solo§7: Si celà arrive vous obtiendrez les effets§b Speed I§7 et§c Force I§7 permanent\n§7Votre item \"§eSoufle de la Foudre§7\" infligera en plus de l'effet§l Slowness I§7 l'effet§l Weakness I§7 pendant§c 15 secondes\n§7Pour finir vous obtiendrez l'§cenchantement§b Depth Strider 2§7 sur vos bottes.\n\n"
        +AllDesc.tab+"§7(§c15%§7) Devenir§c Démon§7: Si celà arrive vous obtiendrez l'effet§c Force I§7 la§c nuit§7, également, vous apparaitrez dans la liste de§c Muzan§7.\n\n"
        +AllDesc.tab+"§7(§c80%§7) Rester§a Slayer§7: Vous ne gagnez rien de plus")}));
        this.desc = desc.getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
            List<Soufles> allSoufles = new ArrayList<>(Arrays.asList(Soufles.values()));
            allSoufles.remove(Soufles.RIEN);
            Collections.shuffle(allSoufles, Main.RANDOM);
            Soufles soufle = allSoufles.get(0);
            giveSoufle(soufle);
        }, 19);
        EventUtils.registerEvents(this);
        new UpdateRunnable(this).runTaskTimerAsynchronously(Main.getInstance(), 0, 20);
        setCanuseblade(true);
    }

    @Override
    public void GiveItems() {
        giveItem(owner, false, Items.getLamedenichirin());
    }

    @Override
    public String[] Desc() {
        return new String[0];
    }
    @Override
    public ItemStack[] getItems() {
        List<ItemStack> toReturn = new ArrayList<>();
        if (this.souflesList.contains(Soufles.VENT)) {
            toReturn.add(this.soufleVentItem);
        }
        if (this.souflesList.contains(Soufles.FOUDRE)) {
            toReturn.add(this.soufleFoudreItem);
        }
        if (this.souflesList.contains(Soufles.EAU)) {
            toReturn.add(this.soufleEauItem);
        }
        return toReturn.toArray(new ItemStack[0]);
    }
    @Override
    public String getName() {
        return "Pourfendeur Simple§7 (§eV2§7)§r";
    }
    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Slayer;
    }
    @Override
    public void resetCooldown() {
        cdVent = 0;
        cdFoudre = 0;
        cdEau = 0;
    }
    @Override
    public TextComponent getComponent() {
        return this.desc;
    }
    @EventHandler
    private void onEndGame(EndGameEvent event) {
        EventUtils.unregisterEvents(this);
    }
    @EventHandler
    private void onBattle(UHCPlayerBattleEvent event) {
        if (event.getDamager().getUuid().equals(getPlayer()) && event.getOriginEvent().getEntity() instanceof Player) {//Si mon joueur tape
            if (this.souflesList.contains(Soufles.FEU)) {//Si il possède le soufle du feu
                if (RandomUtils.getOwnRandomProbability(10)) {//si la proba est bonne
                    event.getOriginEvent().getEntity().setFireTicks(20*15);//alors je met en feu le type contre qui il se bat (pour info c'est forcément un Player vue que je passe par mon event personnalisé
                }
            }
            if (this.souflesList.contains(Soufles.FOUDRE)) {
                if (this.cdFoudre == -500) {
                    GameState.getInstance().spawnLightningBolt(event.getOriginEvent().getEntity().getWorld(), event.getOriginEvent().getEntity().getLocation());
                    double health = ((Player) event.getOriginEvent().getEntity()).getHealth();
                    if (health - 2.0 <= 0.0) {
                        ((Player) event.getOriginEvent().getEntity()).setHealth(1.0);
                    } else {
                        ((Player) event.getOriginEvent().getEntity()).setHealth(((Player) event.getOriginEvent().getEntity()).getHealth()-2.0);
                    }
                    this.cdFoudre = 60*3;
                    event.getOriginEvent().getDamager().sendMessage("§7Votre§e Soufle de la Foudre§7 à fait effet.");
                    event.getOriginEvent().getEntity().sendMessage("§7Vous avez subit les effets du§e Soufle de la Foudre§7.");
                    event.getVictim().getRole().owner.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*15, 0, false, false), true);
                    if (getTeam().equals(TeamList.Solo)){
                        event.getVictim().getRole().owner.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20*15, 0, false, false), true);
                    }
                }
            }
        }
    }
    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.hasItem()) {
            if (event.getPlayer().getUniqueId().equals(getPlayer())) {
                if (event.getItem().isSimilar(this.soufleVentItem)) {
                    if (cdVent > 0) {
                        event.setCancelled(true);
                        sendCooldown(event.getPlayer(), cdVent);
                        return;
                    }
                    event.getPlayer().sendMessage("§7Vous avez activé votre§a Soufle du vent§7.");
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*2, 1, false, false), true);
                    this.cdVent = 60*6;
                    event.setCancelled(true);
                }
                if (event.getItem().isSimilar(this.soufleFoudreItem)) {
                    if (this.cdFoudre > 0) {
                        sendCooldown(event.getPlayer(), cdFoudre);
                        event.setCancelled(true);
                        return;
                    }
                    event.getPlayer().sendMessage("§7Votre§e Soufle de la Foudre§7 est§c activé§7, vous devez maintenant tapé un §cjoueur§7.");
                    this.cdFoudre = -500;
                    event.setCancelled(true);
                }
                if (event.getItem().isSimilar(this.soufleEauItem)) {
                    if (this.cdEau > 0) {
                        sendCooldown(event.getPlayer(), cdEau);
                        event.setCancelled(true);
                        return;
                    }
                    event.getPlayer().sendMessage("§7Vous avez activé votre§b Soufle de l'Eau§7.");
                    this.cdEau = 60*8;
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*3, 0, false, false), true);
                    event.setCancelled(true);
                }
            }
        }
    }
    @EventHandler
    private void onKill(UHCPlayerKillEvent event) {
        if (event.getGamePlayerKiller() != null) {
            if (event.getPlayerKiller().getUniqueId().equals(getPlayer())) {
                if (this.souflesList.size() < 5) {
                    List<Soufles> allSoufles = new ArrayList<>(Arrays.asList(Soufles.values()));
                    allSoufles.remove(Soufles.RIEN);
                    allSoufles.removeAll(this.souflesList);
                    Collections.shuffle(allSoufles, Main.RANDOM);
                    Soufles soufle = allSoufles.get(0);
                    giveSoufle(soufle);
                }
            }
        }
    }
    private void giveSoufle(Soufles soufle) {
        this.souflesList.add(soufle);
        owner.sendMessage("§7Vous avez obtenue le soufle "+soufle.getName());
        switch (soufle) {
            case ROCHE:
                givePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false), EffectWhen.DAY);
                break;
            case VENT:
                giveItem(owner, false, this.soufleVentItem);
                break;
            case FOUDRE:
                giveItem(owner, false, this.soufleFoudreItem);
                break;
            case EAU:
                ItemStack Depth = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta BookMeta = (EnchantmentStorageMeta) Depth.getItemMeta();
                BookMeta.addStoredEnchant(Enchantment.DEPTH_STRIDER, 1, false);
                Depth.setItemMeta(BookMeta);
                giveItem(owner, false, Depth, this.soufleEauItem);
                break;
        }
        if (this.souflesList.size() == 5) {
            this.souflesList.add(Soufles.RIEN);
            if (RandomUtils.getOwnRandomProbability(20)) {
                setTeam(TeamList.Demon);
                givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.NIGHT);
            } else if (RandomUtils.getOwnRandomProbability(5)) {
                setTeam(TeamList.Solo);
                owner.getInventory().remove(this.soufleEauItem);
                owner.getInventory().getBoots().getItemMeta().addEnchant(Enchantment.DEPTH_STRIDER, 2, true);
                givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false), EffectWhen.PERMANENT);
                givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
            }
        }
    }
    private static class UpdateRunnable extends BukkitRunnable {

        private final PourfendeurV2 role;

        public UpdateRunnable(PourfendeurV2 pourfendeurV2) {
            this.role = pourfendeurV2;
        }

        @Override
        public void run() {
            if (!GameState.getInstance().getServerState().equals(GameState.ServerStates.InGame)) {
                cancel();
                return;
            }
            if (!role.getGamePlayer().isAlive()) {
                return;
            }
            if (role.cdVent >= 0) {
                role.cdVent--;
                if (role.cdVent == 0) {
                    role.owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§a Soufle du vent§7.");
                }
            }
            if (role.cdFoudre >= 0) {
                role.cdFoudre--;
                if (role.cdFoudre == 0) {
                    role.owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§e Soufle de la Foudre§7.");
                }
            }
            if (role.cdEau >= 0) {
                role.cdEau--;
                if (role.cdEau == 0) {
                    role.owner.sendMessage("§7Vous pouvez à nouveua utiliser votre§b Soufle de l'Eau§7.");
                }
            }
        }
    }
}
