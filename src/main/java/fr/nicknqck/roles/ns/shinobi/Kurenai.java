package fr.nicknqck.roles.ns.shinobi;

import fr.nicknqck.GameState;
import fr.nicknqck.Main;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Kurenai extends RoleBase {
    private final ItemStack BoisItem = new ItemBuilder(Material.NETHER_STAR).setName("§cGenjutsu des bois").setLore("§7Vous permet d'empêcher le joueur viser de bouger").toItemStack();
    private int cdBois = 0;
    private final ItemStack GenjutsuItem = new ItemBuilder(Material.NETHER_STAR).setName("§cGenjutsu temporel").setLore("§7Vous permet en ciblant un joueur de créer un pure combat 1v1").toItemStack();
    private int cdGenjutsu = 0;
    public Kurenai(Player player, GameState.Roles roles, GameState gameState) {
        super(player, roles, gameState);
        setChakraType(getRandomChakras());
        owner.sendMessage(Desc());
    }

    @Override
    public void GiveItems() {
        super.GiveItems();
        super.giveItem(owner, false, getItems());
    }

    @Override
    public String[] Desc() {
        return new String[]{
                AllDesc.bar,
                AllDesc.role+"§aKurenai",
                AllDesc.objectifteam+"§aShinobi",
                "",
                AllDesc.items,
                "",
                AllDesc.point+"§cGenjutsu des bois§f: Vous permet en ciblant un joueur, de l'empêcher de bouger pendant§c 5 secondes§f puis vous téléporte derrière ce joueur et lui inflige§c 3"+AllDesc.coeur+"§f.§7 (1x/5m)"
        };
    }

    @Override
    public ItemStack[] getItems() {
        return new ItemStack[]{
                BoisItem,
                GenjutsuItem
        };
    }

    @Override
    public void resetCooldown() {
        cdBois = 0;
        cdGenjutsu = 0;
    }

    @Override
    public void Update(GameState gameState) {
        super.Update(gameState);
        if (cdBois >= 0){
            cdBois--;
            if (cdBois == 0){
                owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§c Genjutsu des bois§7.");
            }
        }
        if (cdGenjutsu >= 0){
            cdGenjutsu--;
            if (cdGenjutsu == 0){
                owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§c Genjutsu temporel§7.");
            }
        }
    }

    @Override
    public boolean ItemUse(ItemStack item, GameState gameState) {
        if (item.isSimilar(BoisItem)){
            if (cdBois > 0){
                sendCooldown(owner, cdBois);
                return true;
            }
            Player target = getTargetPlayer(owner, 30);
            if (target == null) {
                owner.sendMessage("§cIl faut viser un joueur !");
                return true;
            }
            owner.sendMessage("§7Vous utiliser votre§c Genjutsu§7 sur§a "+target.getDisplayName());
            owner.setGameMode(GameMode.SPECTATOR);
            GamePlayer.get(target.getUniqueId()).stun(5.0);
            Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
                owner.setGameMode(GameMode.SURVIVAL);
                owner.sendMessage("§7Votre§c Genjutsu§7 est terminer.");
            }, 100);
            cdBois = 60*4+5;
            return true;
        }
        if (item.isSimilar(GenjutsuItem)){
            if (cdGenjutsu > 0){
                sendCooldown(owner, cdGenjutsu);
                return true;
            }
            Player target = getTargetPlayer(owner, 30);
            if (target == null) {
                owner.sendMessage("§cIl faut viser un joueur !");
                return true;
            }
        }
        return super.ItemUse(item, gameState);
    }
}
