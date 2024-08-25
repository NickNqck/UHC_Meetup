package fr.nicknqck.roles.mc.nether;

import fr.nicknqck.GameState;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.EffectWhen;
import fr.nicknqck.roles.mc.builders.NetherRoles;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Blaze extends NetherRoles {

    private final ItemStack flyItem = new ItemBuilder(Material.BLAZE_ROD).setName("§cFly").setLore("§7Vous permez de voler pendant 5 secondes. (1x/5mins)").setDroppable(false).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().toItemStack();
    private int cdFly = 0;
    private final ItemStack arcItem = new ItemBuilder(Material.BOW).setName("§cArc").setDroppable(false).addEnchant(Enchantment.ARROW_DAMAGE, 3).addEnchant(Enchantment.ARROW_FIRE, 1).toItemStack();
    private final TextComponent automaticDesc;
    public Blaze(UUID player) {
        super(player);
        giveItem(owner, false,getItems());
        AutomaticDesc desc = new AutomaticDesc(this);
        desc.addEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20, 0, false, false), EffectWhen.PERMANENT)
        .setItems(new TripleMap<>(getflyText().getHoverEvent(), getflyText().getText(), 60*5), new TripleMap<>(getBowText().getHoverEvent(), getBowText().getText(), 0))
        .addParticularites(getNofall().getHoverEvent());
        this.automaticDesc = desc.getText();
    }

    @Override
    public void RoleGiven(GameState gameState) {
        getEffects().put(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60, 0, false, false), EffectWhen.PERMANENT);
        setNoFall(true);
        super.RoleGiven(gameState);
    }
    public TextComponent getComponent(){return automaticDesc;}
    private TextComponent getflyText(){
        TextComponent fly = new TextComponent("§cFly");
        fly.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous permez de voler pendant 5 secondes. (1x/5mins)")}));
        return fly;
    }
    private TextComponent getBowText(){
        TextComponent bow = new TextComponent("§cArc");
        bow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Arc power 3 et flame 1.")}));
        return bow;
    }
    private  TextComponent getNofall(){
        TextComponent Nofall = new TextComponent("§6§lParticularité");
        Nofall.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous possédez §aNoFall permanent.")}));
        return Nofall;
    }


    @Override
    public String[] Desc() {
        return new String[0];
    }

    @Override
    public ItemStack[] getItems() {
       return new ItemStack[]{
               flyItem,
               arcItem
       };
    }

    @Override
    public String getName() {
        return "Blaze";
    }

    @Override
    public GameState.Roles getRoles() {
        return GameState.Roles.Blaze;
    }

    @Override
    public void resetCooldown() {
        cdFly = 0;
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if(item.isSimilar(flyItem)){
            if (cdFly <= 0) {
                owner.setAllowFlight(true);
                owner.setFlying(true);
                owner.sendMessage("Vous pouvez désormais voler");
                new BukkitRunnable(){
                    private int i = 5;
                    @Override
                    public void run() {
                        if (gameState.getInGamePlayers().contains(getPlayer())) {
                            if (i >= 5) {
                                i--;
                                NMSPacket.sendActionBar(owner, "Vous pouvez encore voler pendant §b"+i);
                                if (i == 0){
                                    owner.sendMessage("Vous ne pouvez plus voler ");
                                    owner.setFlying(false);
                                    owner.setAllowFlight(false);
                                    cdFly = 60*5;
                                    cancel();
                                }
                            }
                        } else {
                            cancel();
                        }
                    }
                };
            } else {
                sendCooldown(owner, cdFly);
            }
        }
        return super.ItemUse(item, gameState);
    }

    @Override
    public void Update(GameState gameState) {
        if (cdFly >= 0){
            cdFly --;
            if (cdFly == 0){
                owner.sendMessage("Vous pouvez de nouveau réutiliser votre §cFly");
            }
        }
        super.Update(gameState);
    }
}
