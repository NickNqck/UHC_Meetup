package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.roles.ds.slayers.pillier.TomiokaV2;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class UrokodakiV2 extends SlayerRoles {

    private TextComponent textComponent;
    private final ItemStack DepthRider = new ItemBuilder(Material.ENCHANTED_BOOK).addStoredEnchantment(Enchantment.DEPTH_STRIDER, 2).toItemStack();

    public UrokodakiV2(UUID player) {
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
        return "Urokodaki";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Urokodaki;
    }

    @Override
    public void resetCooldown() {

    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[0];
    }

    @Override
    public void RoleGiven(GameState gameState) {
        giveItem(owner, false, DepthRider);
        AutomaticDesc desc = new AutomaticDesc(this)
                .addCustomWhenEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*3, 0, false, false), "dans l'§beau")
                .addCustomLine("§7A la mort de §6Tanjiro§7,§6 Sabito§7,§6 Tomioka§7 ou §6 Makomo §7vous obtiendrez 5% de "+AllDesc.Force+" §7 ou 5% de "+AllDesc.Speed)
                .addCustomLine("§7Au début de la partie vous obtenez un livre Depth Rider 2.");
                addPower(new SoufflePower(this));
        this.textComponent = desc.getText();
        setCanuseblade(true);
    }
    @Override
    public TextComponent getComponent() {
        return this.textComponent;
    }

    private static class SoufflePower extends ItemPower {
        protected SoufflePower(@NonNull RoleBase role) {
            super("§bSouffle de l'eau", new Cooldown(60*5),new ItemBuilder(Material.NETHER_STAR), role, "§7Vous donne "+ AllDesc.Speed+" §71 pendnat 2 minutes.");
        }

        @Override
        public boolean onUse(Player player, Map<String, Object> args) {
            player.sendMessage("§7Vous venez d'utiliser votre §bsouffle de l'eau §7vous obtenez donc "+AllDesc.Speed+" §71 pendnant 2 minutes");
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*120, 0, false), true);
            return true;
        }
    }

    @Override
    public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
        if (gameState.getServerState().equals(GameState.ServerStates.InGame)){
            RoleBase role = gameState.getPlayerRoles().get(player);
            if (role instanceof TomiokaV2 || role instanceof Tanjiro || role instanceof Makomo || role instanceof Sabito){
                int rint = Main.RANDOM.nextInt(2);
                if (rint == 0){
                    this.addBonusforce(5);
                    owner.sendMessage("§7"+ player.getName()+" il possédait le rôle de: §6"+ role.getRoles() +"§7 vous obtenez donc 5% de "+AllDesc.Force);
                } else {
                    this.addSpeedAtInt(owner, 5);
                    owner.sendMessage("§7"+ player.getName()+" il possédait le rôle de: §6"+ role.getRoles() +"§7 vous obtenez donc 5% de "+AllDesc.Speed);
                }
            }


        }
        super.OnAPlayerDie(player, gameState, killer);
    }
}
