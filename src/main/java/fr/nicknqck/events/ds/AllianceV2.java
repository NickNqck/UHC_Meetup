package fr.nicknqck.events.ds;

import fr.nicknqck.GameState;
import fr.nicknqck.events.custom.UHCDeathEvent;
import fr.nicknqck.items.Items;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.slayers.pillier.KyojuroV2;
import fr.nicknqck.roles.ds.solos.Shinjuro;
import fr.nicknqck.utils.event.EventUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
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
                    gamePlayer.sendMessage("Vous gagnez maintenant avec "+TeamList.Alliance.getColor()+gameState.getOwner(GameState.Roles.Shinjuro).getName());
                    gamePlayer.sendMessage("Vous avez convaincue votre père d'arrêter l'alcool, temp que vous serez en vie il aura "+ AllDesc.Force+" 1 proche de vous, de plus vous gagnez §c2"+AllDesc.coeur);
                    k.giveHealedHeartatInt(2);
                    this.kyojuro = k;
                    k.setAlliance(true);
                    k.givePotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false), EffectWhen.PERMANENT);
                }
                if (gamePlayer.getRole() instanceof Shinjuro) {
                    Shinjuro s = (Shinjuro) gamePlayer.getRole();
                    s.owner.sendMessage("Vous gagnez maintenant avec "+TeamList.Alliance.getColor()+gameState.getOwner(GameState.Roles.Kyojuro).getName());
                    s.owner.sendMessage("Votre fils vous à convaincue d'arrêter l'alcool, temp qu'il sera en vie vous obtiendrez "+AllDesc.Force+" 1 proche de lui, de plus vous aurez un traqueur vers lui.");
                    s.owner.getInventory().removeItem(Items.getSake());
                    s.setSakeCooldown(-1);
                    this.shinjuro = s;
                    this.shinjuro.alliance = true;
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
        return gameState.attributedRole.contains(GameState.Roles.Kyojuro) && !gameState.DeadRole.contains(GameState.Roles.Kyojuro) && gameState.attributedRole.contains(GameState.Roles.Shinjuro) && !gameState.DeadRole.contains(GameState.Roles.Shinjuro);
    }

    @Override
    public boolean isActivated() {
        return this.activated;
    }
}