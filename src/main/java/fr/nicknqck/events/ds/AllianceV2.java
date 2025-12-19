package fr.nicknqck.events.ds;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.MDJ;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.slayers.pillier.KyojuroV2;
import fr.nicknqck.roles.ds.solos.Shinjuro;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AllianceV2 extends Event implements Listener {

    private KyojuroV2 kyojuro;
    private Shinjuro shinjuro;
    private boolean activated = false;

    @Override
    public String getName() {
        return "§fAlliance§a Kyojuro§7 -§6 Shinjuro";
    }

    @Override
    public void onProc(final GameState gameState) {
        for (final GamePlayer gamePlayer : gameState.getGamePlayer().values()) {
            if (!gamePlayer.isAlive())continue;
            if (gamePlayer.getRole() == null)continue;
            if (gamePlayer.getRole() instanceof KyojuroV2 || gamePlayer.getRole() instanceof Shinjuro) {
                gamePlayer.getRole().setTeam(TeamList.Alliance);
                if (gamePlayer.getRole() instanceof KyojuroV2) {
                    KyojuroV2 k = (KyojuroV2) gamePlayer.getRole();
                    gamePlayer.sendMessage("Vous gagnez maintenant avec "+TeamList.Alliance.getColor()+gameState.getOwner(Roles.Shinjuro).getName());
                    gamePlayer.sendMessage("Vous avez convaincue votre père d'arrêter l'alcool, temp que vous serez en vie il aura "+ AllDesc.Force+" 1 proche de vous, de plus vous gagnez §c2"+AllDesc.coeur);
                    k.giveHealedHeartatInt(2);
                    this.kyojuro = k;
                    k.setAlliance(true);
                    k.givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
                }
                if (gamePlayer.getRole() instanceof Shinjuro) {
                    Shinjuro s = (Shinjuro) gamePlayer.getRole();
                    s.getGamePlayer().sendMessage("Vous gagnez maintenant avec "+TeamList.Alliance.getColor()+gameState.getOwner(Roles.Kyojuro).getName());
                    s.getGamePlayer().sendMessage("Votre fils vous à convaincue d'arrêter l'alcool, temp qu'il sera en vie vous obtiendrez "+AllDesc.Force+" 1 proche de lui, de plus vous aurez un traqueur vers lui.");
                    this.shinjuro = s;
                    s.procAlliance();
                }
            }
        }
        EventUtils.registerRoleEvent(this);
        this.activated = true;
    }
    @EventHandler
    private void onDeath(UHCDeathEvent event) {
        if (this.kyojuro != null && this.shinjuro != null) {
            if (event.getPlayer().getUniqueId().equals(kyojuro.getPlayer())) {
                this.shinjuro.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
            } else if (event.getPlayer().getUniqueId().equals(shinjuro.getPlayer())) {
                this.kyojuro.givePotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false), EffectWhen.PERMANENT);
            }
        }
    }

    @Override
    public ItemStack getMenuItem() {
        return new ItemBuilder(Material.LAVA_BUCKET).setName(getName()).setLore(getLore()).toItemStack();
    }

    @Override
    public boolean canProc(GameState gameState) {
        return containsRoles(gameState);
    }

    private boolean containsRoles(final GameState gameState) {
        return gameState.getAttributedRole().contains(Roles.Kyojuro) &&
                !gameState.DeadRole.contains(Roles.Kyojuro) &&
                gameState.getAttributedRole().contains(Roles.Shinjuro) &&
                !gameState.DeadRole.contains(Roles.Shinjuro);
    }

    @Override
    public boolean isActivated() {
        return this.activated;
    }

    @Override
    public String[] getExplications() {
        return new String[] {
                "§7Fait en sorte que§a Kyojuro§7 et§e Shinjuro§7 (§6V1§7) sois en alliance et gagne ensemble,",
                "§7pour les aidées ils gagnent l'effet§c Force I§7 proche l'un de l'autre",
                "§1",
                "§8 -§e Shinjuro§7: Il perdra son§c Sake§7 mais gagnera un traqueur permanent vers§a Kyojuro§7.",
                "",
                "§8 -§a Kyojuro§7: Il gagnera§b Speed I§c permanent§7 et§c 2❤ supplémentaires ",
                "",
                "§cSi l'un des deux meurt l'autre recevra l'effet§c Force I permanent."
        };
    }

    @Override
    public @NonNull MDJ getMDJ() {
        return MDJ.DS;
    }
}