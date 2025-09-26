package fr.nicknqck.roles.ds.slayers;

import fr.nicknqck.GameState;
import fr.nicknqck.enums.Roles;
import fr.nicknqck.items.Items;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.ds.builders.DemonsSlayersRoles;
import fr.nicknqck.roles.ds.builders.Lames;
import fr.nicknqck.roles.ds.builders.SlayerRoles;
import fr.nicknqck.roles.ds.builders.Soufle;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HotaruV2 extends SlayerRoles {

    private TextComponent textComponent;
    public HotaruV2(UUID player) {
        super(player);
    }

    @Override
    public Soufle getSoufle() {
        return Soufle.AUCUN;
    }

    @Override
    public String getName() {
        return "Hotaru";
    }

    @Override
    public @NonNull Roles getRoles() {
        return Roles.Hotaru;
    }

    @Override
    public void RoleGiven(GameState gameState) {
        addPower(new LamePower(this));
        addPower(new RepairPower(this));
        addPower(new UnbreakPower(this));
        addPower(new RolePower(this));
        AutomaticDesc desc = new AutomaticDesc(this).setPowers(getPowers());
        this.textComponent = desc.getText();
        setCanuseblade(false);
    }

    @Override
    public TextComponent getComponent() {
        return this.textComponent;
    }

    private static class LamePower extends CommandPower {
        public LamePower(@NonNull RoleBase role) {
            super("§6/ds lame <joueur>", "lame", new Cooldown(0), role, CommandType.DS, "Vous permez de savoir si un joueur possède une lame ou non, si il en possède une vous saurez quelle lame le joueur possède. (3x/partie)");
            setMaxUse(3);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            String[] strings = (String[]) args.get("args");
            if (strings.length == 2) {
                Player player1 = Bukkit.getPlayer(strings[1]);
                if (player1 != null) {
                    GamePlayer gamePlayer = getRole().getGameState().getGamePlayer().get(player1.getUniqueId());
                    if (gamePlayer != null) {
                        if (gamePlayer.getRole() != null && gamePlayer.getRole() instanceof DemonsSlayersRoles) {
                            if (((DemonsSlayersRoles) gamePlayer.getRole()).isHasblade()){
                                player.sendMessage("§7"+gamePlayer.getPlayerName() + " possède la lame de " + ((DemonsSlayersRoles) gamePlayer.getRole()).getLames() + ".");
                            } else {
                                player.sendMessage("§7"+gamePlayer.getPlayerName() + " ne possède pas de lame.");
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    private static class RepairPower extends CommandPower {
        public RepairPower(@NonNull RoleBase role) {
            super("§6/ds repair <joueur>", "repair", new Cooldown(60 * 5), role, CommandType.DS, "§7Vous permez de réparer la lame d'un joueur. (3x/partie)  §cAttention : §7si le joueur que vous réparer ne possède pas de lame alors vous perderez votre pouvoir.");
            setMaxUse(3);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            String[] strings = (String[]) args.get("args");
            if (strings.length == 2) {
                Player player1 = Bukkit.getPlayer(strings[1]);
                if (player1 != null) {
                    GamePlayer gamePlayer = getRole().getGameState().getGamePlayer().get(player1.getUniqueId());
                    if (gamePlayer != null) {
                        if (gamePlayer.getRole() != null && gamePlayer.getRole() instanceof DemonsSlayersRoles) {
                            if (((DemonsSlayersRoles) gamePlayer.getRole()).isHasblade()) {
                                DemonsSlayersRoles role = (DemonsSlayersRoles) gamePlayer.getRole();
                                int i = role.getLames().getUsers().get(role.getPlayer());
                                role.getLames().getUsers().remove(role.getPlayer(), i);
                                i = 40;
                                role.getLames().getUsers().put(role.getPlayer(), i);
                                player.sendMessage("§7Vous venez de réparer la lame de "+gamePlayer.getPlayerName());
                                player1.sendMessage("§aHotaru §7vient de réparer votre lame");
                            } else {
                                player.sendMessage("§7"+gamePlayer.getPlayerName()+" ne possède pas de lame vous perdez donc votre capacité");
                                setUse(3);
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    private static class UnbreakPower extends CommandPower{

        public UnbreakPower(@NonNull RoleBase role) {
            super("/ds Unbreak <joueur>", "unbreak", new Cooldown(0), role, CommandType.DS, "§7Vous permez de rendre la lame d'un joueur incassable. (1x/partie)");
            setMaxUse(1);
        }

        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            String[] strings = (String[]) args.get("args");
            if (strings.length == 2) {
                Player player1 = Bukkit.getPlayer(strings[1]);
                if (player1 != null) {
                    GamePlayer gamePlayer = getRole().getGameState().getGamePlayer().get(player1.getUniqueId());
                    if (gamePlayer != null) {
                        if (gamePlayer.getRole() != null && gamePlayer.getRole() instanceof DemonsSlayersRoles) {
                            if (((DemonsSlayersRoles) gamePlayer.getRole()).isHasblade()) {
                               ((DemonsSlayersRoles) gamePlayer.getRole()).setLameincassable(true);
                               player1.sendMessage("§7votre lame a été rendu incassable par §aHotaru");
                               player.sendMessage("§7vous venez de rendre incassable la lame de §a"+gamePlayer.getPlayerName());
                            } else {
                                player.sendMessage("§7Vous venez de rendre incassable la lame d'un joueur qui ne possède pas de lame. §cPauvre con!!");
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    private static class RolePower extends CommandPower implements Listener {
        private final Map<ItemStack, Lames> map;

        public RolePower(@NonNull RoleBase role) {
            super("/ds chooseblade", "role", new Cooldown(60*5), role, CommandType.DS, "§7Vous permez de choisir votre lame.");
            this.map = new HashMap<>();
            init();
        }


        @Override
        public boolean onUse(@NonNull Player player, @NonNull Map<String, Object> args) {
            Inventory inv = Bukkit.createInventory(player, 27, "§fChoix de la lame");
            inv.setItem(9, Items.getLamedenichirincoeur());
            inv.setItem(4, Items.getLamedenichirinfireresi());
            inv.setItem(13, Items.getLamedenichirinforce());
            inv.setItem(15, Items.getLamedenichirinnofall());
            inv.setItem(17, Items.getLamedenichirinresi());
            inv.setItem(11, Items.getLamedenichirinspeed());
            player.openInventory(inv);
            return false;
        }
        @EventHandler
        private void OnInventoryclick(InventoryClickEvent e){
            if (e.getClickedInventory().getTitle().equals("§fChoix de la lame")) {
                ItemStack tuclique = e.getCurrentItem();
                for (ItemStack item : map.keySet()){
                    if (item.isSimilar(tuclique)){
                        removeOldBlade();
                        map.get(item).getUsers().put(e.getWhoClicked().getUniqueId(), Integer.MAX_VALUE);
                        e.getWhoClicked().sendMessage("§7Vous venez de choisisr la lame "+item);
                        getCooldown().use();
                        break;
                    }
                }
            }
        }
        private void init(){
            map.put(Items.getLamedenichirincoeur(), Lames.Coeur);
            map.put(Items.getLamedenichirinfireresi(), Lames.FireResistance);
            map.put(Items.getLamedenichirinspeed(), Lames.Speed);
            map.put(Items.getLamedenichirinnofall(), Lames.NoFall);
            map.put(Items.getLamedenichirinforce(), Lames.Force);
            map.put(Items.getLamedenichirinresi(), Lames.Resistance);

        }
        private void removeOldBlade(){
            for(Lames lame : Lames.values()){
                lame.getUsers().remove(getRole().getPlayer());
            }
        }


    }



}
