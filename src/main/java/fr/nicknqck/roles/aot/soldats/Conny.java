package fr.nicknqck.roles.aot.soldats;

import fr.nicknqck.GameState;
import fr.nicknqck.GameState.Roles;
import fr.nicknqck.Main;
import fr.nicknqck.roles.aot.builders.SoldatsRoles;
import fr.nicknqck.roles.builder.AutomaticDesc;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Conny extends SoldatsRoles {
	private int cd = 0;
	private RoleBase protegerRole;
	private boolean cmd = false;
	private boolean protegerdead = false;
	private final ItemStack sucre = new ItemBuilder(Material.SUGAR).addEnchant(Enchantment.ARROW_DAMAGE, 1).hideAllAttributes().setName("§r§fSucre").setDroppable(false).toItemStack();
	private TextComponent desc;
	public Conny(UUID player) {
		super(player);
	}
	@Override
	public Roles getRoles() {
		return Roles.Conny;
	}
	@Override
	public String[] Desc() {
		return new String[0];
	}
	@Override
	public String getName() {
		return "Conny";
	}
	@Override
	public ItemStack[] getItems() {
		return new ItemStack[] {
				this.sucre
		};
	}

	@Override
	public void RoleGiven(GameState gameState) {
		AutomaticDesc desc = getAutomaticDesc();
		this.desc = desc.getText();
		Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> {
			TextComponent message = new TextComponent("§7Choisissez qui vous voulez protéger: ");
			TextComponent Sasha = new TextComponent("§a§lSasha");
			Sasha.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/aot proteger sasha"));
			Sasha.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§cCliquez ici§7 pour§c choisir§7 de protéger§a Sasha")}));
			message.addExtra("\n\n");
			message.addExtra(Sasha);
			TextComponent Jean = new TextComponent("§a§lJean");
			Jean.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/aot proteger jean"));
			Jean.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§cCliquez ici§7 pour§c choisir§7 de protéger§a Jean")}));
			message.addExtra("\n\n");
			message.addExtra(Jean);
			message.addExtra("\n");
			if (owner != null) {
				owner.spigot().sendMessage(message);
			}
		}, 20);
	}

	private AutomaticDesc getAutomaticDesc() {
		AutomaticDesc desc = new AutomaticDesc(this);
		desc.setItems(new TripleMap<>(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Vous permet d'obtenir des effets en fonction de qui vous protégez: \n\n"+
				AllDesc.tab+"§aJean§7: Vous donne les effets§b Speed I§7 et§9 Résistance I§7 pendant§c 3 minutes§7.\n\n"+
				AllDesc.tab+"§aSasha§7: Vous donne l'effet§b Speed II§7 pendant§c 3 minutes§7.\n\n"+
				"§7A la mort de votre protéger vous aurez les effets de celui que vous n'aviez pas choisis précédemment.")}), "Sucre", 60*5));
		return desc;
	}

	@Override
	public TextComponent getComponent() {
		return this.desc;
	}

	@Override
	public void GiveItems() {
		giveItem(owner, false, getItems());
	}

	@Override
	public void Update(GameState gameState) {
		if (cd >= 0) {
			cd--;
			if (cd == 0) {
				owner.sendMessage("§7Vous pouvez à nouveau utiliser votre§a boost§7 de compétence.");
			}
		}
	}
	@Override
	public boolean ItemUse(ItemStack item, GameState gameState) {
		if (item.isSimilar(this.sucre)) {
			if (this.protegerRole != null) {
				if (this.protegerRole instanceof Jean) {
					if (!protegerdead) {
						giveJeanEffect(owner);
					} else {
						giveSashaEffect(owner);
					}
					cd = 60*8;
					return true;
				} else if (this.protegerRole instanceof Sasha) {
					if (!protegerdead) {
						giveSashaEffect(owner);
					} else {
						giveJeanEffect(owner);
					}
					cd = 60*8;
					return true;
				}
			} else {
				owner.sendMessage("§cErreur | Vous ne protégez personne.");
			}
		}
		return super.ItemUse(item, gameState);
	}

	@Override
	public void onAotCommands(String arg, String[] args, GameState gameState) {
		if (args[0].equalsIgnoreCase("proteger")) {
			if (args.length != 2)return;
			if (!cmd) {
				if (args[1].equalsIgnoreCase("Jean")) {
					int amountJean = 0;
					for (UUID u : gameState.getInGamePlayers()) {
						Player p = Bukkit.getPlayer(u);
						if (p == null)continue;
						if (gameState.getGamePlayer().get(p.getUniqueId()).getRole() instanceof Jean) {
							amountJean++;
							this.protegerRole = gameState.getGamePlayer().get(p.getUniqueId()).getRole();
							cmd = true;
							owner.sendMessage("Vous protegez désormais Jean vous obtenez donc le pseudo du Jean "+p.getName());
							break;
						}
					}
					if (amountJean == 0) {
						owner.sendMessage("§aJean§7 n'est pas présent dans la partie");
					}
				} else {
					if (args[1].equalsIgnoreCase("Sasha")) {
						int amountSasha = 0;
						for (UUID u : gameState.getInGamePlayers()) {
							Player p = Bukkit.getPlayer(u);
							if (p == null)continue;
							if (gameState.getGamePlayer().get(p.getUniqueId()).getRole() instanceof Sasha) {
								amountSasha++;
								this.protegerRole = gameState.getGamePlayer().get(p.getUniqueId()).getRole();
								cmd = true;
								owner.sendMessage("Vous protegez désormais Sasha vous obtenez donc le pseudo du Sasha "+p.getName());
								break;
							}
						}
						if (amountSasha == 0) {
							owner.sendMessage("§aSasha§7 n'est pas présent dans la partie");
						}
					} else {
						owner.sendMessage("veuillez préciser si vous proteger Sasha ou Jean");
					}
				}
			} else {
				owner.sendMessage("Vous avez déjà fais votre commande");
			}
		}
	}
	@Override
	public void OnAPlayerDie(Player player, GameState gameState, Entity killer) {
		if (player != null) {
			if (killer != null) {
				if (this.protegerRole != null) {
					if (this.protegerRole.getPlayer().equals(player.getUniqueId())) {
						this.protegerdead = true;
					}
				}
			}
		}
	}
	@Override
	public void resetCooldown() {
		cd = 0;
		this.protegerdead = false;
		this.protegerRole = null;
	}
	private void giveJeanEffect(Player owner) {
		OLDgivePotionEffet(owner, PotionEffectType.SPEED, 20*60*3, 1, true);
		OLDgivePotionEffet(owner, PotionEffectType.DAMAGE_RESISTANCE, 60*3*20, 1, true);
	}
	private void giveSashaEffect(Player owner) {
		OLDgivePotionEffet(owner, PotionEffectType.SPEED, 20*60*3, 2, true);
	}
}