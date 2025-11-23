package fr.nicknqck.roles.ns.akatsuki;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ns.Chakras;
import fr.nicknqck.roles.ns.Intelligence;
import fr.nicknqck.roles.ns.akatsuki.blancv2.ZetsuBlancV2;
import fr.nicknqck.roles.ns.builders.AkatsukiRoles;
import fr.nicknqck.roles.ns.solo.jubi.ObitoV2;
import fr.nicknqck.utils.GlobalUtils;
import fr.nicknqck.utils.Loc;
import fr.nicknqck.utils.PropulserUtils;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import fr.nicknqck.utils.raytrace.RayTrace;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NagatoV2 extends AkatsukiRoles implements Listener {

    private final ItemStack ShuradoItem = new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 4).setName("§7Shuradô").setUnbreakable(true).setDroppable(false).toItemStack();

    public NagatoV2(UUID player) {
        super(player);
    }

    @Override
    public @NonNull Intelligence getIntelligence() {
        return Intelligence.INTELLIGENT;
    }

    @Override
    public String getName() {
        return "Nagato";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Nagato;
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[] {
                this.ShuradoItem
        };
    }

    @Override
    public void GiveItems() {
        giveItem(owner, false, getItems());
        super.GiveItems();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        setMaxHealth(getMaxHealth()+6.0);
        addKnowedPlayersWithRoles("§7Voici la liste de l'§cAkatsuki§7 (§cAttention il y a un traitre dans cette liste ayant le rôle de§d Obito§7):"
                , Deidara.class, HidanV2.class, ItachiV2.class,
                KakuzuV2.class, KisameV2.class, KonanV2.class,
                NagatoV2.class, ZetsuBlanc.class,
                ZetsuNoir.class, ZetsuBlancV2.class , ObitoV2.class);
        givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999, 0, false, false), EffectWhen.PERMANENT);
        setChakraType(Chakras.SUITON);
        addPower(new ShikushodoPower(this), true);
        addPower(new BenshoTeninPower(this), true);
        addPower(new JigokudoPower(this));
        addPower(new NingendoPower(this));
        EventUtils.registerRoleEvent(this);
    }

    @Override
    public TextComponent getComponent() {
        return new AutomaticDesc(this)
                .addEffects(getEffects())
                .setPowers(getPowers())
                .addCustomLine("§7Lorsque vous subissez un coup vous aurez§c 15%§7 de chance §7d'esquiver§c 25%§7 des dégâts")
                .getText();
    }
    @EventHandler
    private void DamageReducer(@NonNull final EntityDamageEvent event) {
        if (event.getEntity().getUniqueId().equals(getPlayer())) {
            if (Main.RANDOM.nextInt(100) <= 15) {
                event.setDamage(event.getDamage()*0.75);
                event.getEntity().sendMessage("§aGakido§7 vous à protégez des dégâts, vous avez donc subit§c 25% de dégât en moins");
            }
        }
    }
    private static class ShikushodoPower extends ItemPower {

        private Location location = null;

        protected ShikushodoPower(@NonNull RoleBase role) {
            super("Shikoshodo", new Cooldown(60*5), new ItemBuilder(Material.NETHER_STAR).setName("§fShikushodo"), role,
                    "§7Vous permet de vous téléporter à une position que vous aurez poser au préalable via un §c Shift§7 +§c Clique droit");
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                if (player.isSneaking()) {
                    this.location = player.getLocation();
                    player.sendMessage("§7Vous avez définie la position de§f Shikushodo§7 en:§c x: "+
                            this.location.getBlockX()+
                            ", y: "+
                            this.location.getBlockY()+
                            ", z: "+
                            this.location.getBlockZ());
                } else {
                    if (this.location == null) {
                        player.sendMessage("§cIl faut d'abord définir un endroit pour pouvoir vous téléportez !.");
                        return false;
                    }
                    player.teleport(this.location);
                    player.sendMessage("§7Vous avez été téléporter à l'emplacement de§f Shikushodo");
                    this.location.getWorld().getBlockAt(this.location).setType(Material.AIR);
                    return true;
                }
            }
            return false;
        }
    }
    private static class BenshoTeninPower extends ItemPower {

        private final ShinraTensei shinraTensei;
        private final CliqueDroit cliqueDroit;

        protected BenshoTeninPower(@NonNull RoleBase role) {
            super("Benshô Ten'in", new Cooldown(5), new ItemBuilder(Material.NETHER_STAR).setName("§cBenshô Ten'in"), role,
                    "§7Effectue différente action en fonction du clique utiliser:",
                    "",
                    "§aClique gauche§7: Vous permet de repousser toute entité étant à moins de§c 20 blocs§7 de vous.",
                    "",
                    "§cClique droit§7: Vous permet de téléporter le joueur viser à votre position.");
            setShowCdInDesc(false);
            this.shinraTensei = new ShinraTensei(role);
            this.cliqueDroit = new CliqueDroit(role);
            role.addPower(this.shinraTensei);
            role.addPower(this.cliqueDroit);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            if (getInteractType().equals(InteractType.INTERACT)) {
                @NonNull final PlayerInteractEvent event = (PlayerInteractEvent) map.get("event");
                if (event.getAction().name().contains("LEFT")) {
                    return this.shinraTensei.checkUse(player, map);
                } else {
                    return this.cliqueDroit.checkUse(player, map);
                }
            }
            return false;
        }
        private static class ShinraTensei extends Power {

            public ShinraTensei(@NonNull RoleBase role) {
                super("Shinra Tensei", new Cooldown(60*3), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                @NonNull final List<Player> playerList = new ArrayList<>(Loc.getNearbyPlayersExcept(player, 20));
                if (playerList.isEmpty()) {
                    player.sendMessage("§cImpossible d'utiliser ce pouvoir, personne n'est autours de vous");
                    return false;
                }
                for (@NonNull final Player target : playerList) {
                    new PropulserUtils(player, 20).setNoFall(true).soundToPlay("nsmtp.shinratensei").applyPropulsion(target);
                }
                player.sendMessage("§cShinra Tensei !");
                return true;
            }
        }
        private static class CliqueDroit extends Power {

            public CliqueDroit(@NonNull RoleBase role) {
                super("Clique droit (Benshô Ten'in)", new Cooldown(60*5), role);
                setShowInDesc(false);
            }

            @Override
            public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
                final Player target = RayTrace.getTargetPlayer(player, 50, null);
                if (target == null) {
                    player.sendMessage("§cIl faut viser un joueur !");
                    return false;
                }
                target.teleport(player);
                player.sendMessage("§cBenshô Ten'in !");
                target.sendMessage("§7Vous avez été toucher par le§c Benshô Ten'in");
                return true;
            }
        }
    }
    private static class JigokudoPower extends CommandPower {

        public JigokudoPower(@NonNull RoleBase role) {
            super("/ns jigokudo <joueur>", "jigokudo", null, role, CommandType.NS,
                    "§7Vous permet d'obtenir plusieurs information sur un joueur viser:",
                    "",
                    "§8 - §7S'il/elle possède oui ou non un ou des effets permanent(s)",
                    "",
                    "§8 - §7Le nombre de pouvoir(s) que la personne possède",
                    "",
                    "§8 - §7Le nombre de§c coeurs permanents§7 que possède la cible",
                    "",
                    "§cCette commande infligera 1❤ de dégat a la cible");
            setMaxUse(2);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                @NonNull final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§b"+args[1]+"§c n'est pas connectée");
                    return false;
                }
                if (this.getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                    player.sendMessage("§cImpossible d'exécuter cette commande, §b"+target.getName()+"§c n'a pas de rôle");
                    return false;
                }
                if (!Loc.getNearbyPlayersExcept(player, 5).contains(target)) {
                    player.sendMessage("§b"+target.getDisplayName()+"§c est trop loin pour que vous puissiez utiliser ce pouvoir sur lui");
                    return false;
                }
                @NonNull final GamePlayer gamePlayer = this.getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                player.sendMessage(new String[]{
                        "§7Vous semblez percevoir des choses chez §c" + target.getDisplayName() + "§7, on dirait qu'il/elle a: ",
                        "",
                        "§7"+(gamePlayer.getRole().getEffects().containsValue(EffectWhen.PERMANENT) ? "§cPossède§7 un ou des effets permanent(s)" : "Ne possède aucun effet permanent"),
                        "",
                        "§7Il/Elle possède §c"+gamePlayer.getRole().getPowers().size()+" pouvoir(s)",
                        "",
                        "§7Il/Elle possède §c"+(new DecimalFormat("0").format(target.getMaxHealth()/2)+"❤ permanents")
                });
                target.setHealth(Math.max(1.0, player.getHealth()-2.0));
                return true;
            }
            return false;
        }
    }
    private static class NingendoPower extends CommandPower {

        public NingendoPower(@NonNull RoleBase role) {
            super("/ns ningendo <joueur>", "ningendo", null, role, CommandType.NS,
                    "§7Vous permet d'obtenir des informations sur le joueur visé: ",
                    "",
                    "§8 - §7Les effets permanent que possède le joueur",
                    "",
                    "§8 - §7Le nombre de §epommes d'or§7 que possède le joueur",
                    "",
                    "§8 - §7Le nombre de §ccoeurs§7 que possède le joueur.",
                    "",
                    "§cCette commande infligera a la cible 2❤ de dégats");
            setMaxUse(2);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> map) {
            @NonNull final String[] args = (String[]) map.get("args");
            if (args.length == 2) {
                @NonNull final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§b"+args[1]+"§c n'est pas connectée");
                    return false;
                }
                if (this.getRole().getGameState().hasRoleNull(target.getUniqueId())) {
                    player.sendMessage("§cImpossible d'exécuter cette commande, §b"+target.getName()+"§c n'a pas de rôle");
                    return false;
                }
                if (!Loc.getNearbyPlayersExcept(player, 5).contains(target)) {
                    player.sendMessage("§b"+target.getDisplayName()+"§c est trop loin pour que vous puissiez utiliser ce pouvoir sur lui");
                    return false;
                }
                @NonNull final GamePlayer gamePlayer = this.getRole().getGameState().getGamePlayer().get(target.getUniqueId());
                @NonNull final StringBuilder sb = new StringBuilder();
                @NonNull final List<PotionEffect> permaEffectList = new ArrayList<>();
                for (@NonNull final PotionEffect potionEffect : gamePlayer.getRole().getEffects().keySet()) {
                    if (gamePlayer.getRole().getEffects().get(potionEffect).equals(EffectWhen.PERMANENT)) {
                        permaEffectList.add(potionEffect);
                    }
                }
                if (!permaEffectList.isEmpty()) {
                    for (@NonNull final PotionEffect potionEffect : permaEffectList) {
                        sb.append("§c").append(AutomaticDesc.getPotionEffectNameWithRomanLevel(potionEffect));
                        if (permaEffectList.size() > 1) {
                            sb.append((permaEffectList.get(permaEffectList.size()-2).equals(potionEffect) ? " §7et §c" : permaEffectList.get(permaEffectList.size()-1).equals(potionEffect) ? "" : "§7, "));
                        }
                    }
                } else {
                    sb.append("§7Aucun effet");
                }
                player.sendMessage(new String[]{
                        "§7Vous semblez percevoir des choses chez §c" + target.getDisplayName() + "§7, on dirait qu'il/elle a: ",
                        "",
                        "§7Il/Elle possède le/les effet(s) "+ sb +"§7 de manière§c permanente",
                        "",
                        "§7Il/Elle possède §e"+ GlobalUtils.getItemAmount(target, Material.GOLDEN_APPLE) +" pomme§7(§e§7)§e d'or",
                        "",
                        "§7Il/Elle possède §c"+
                                (new DecimalFormat("0").format(target.getHealth()/2))+
                                "❤§7/§c"+
                                (new DecimalFormat("0").format(target.getMaxHealth()/2)+" permanents")
                });
                target.setHealth(Math.max(1.0, player.getHealth()-4.0));
                return true;
            }
            return false;
        }
    }
}