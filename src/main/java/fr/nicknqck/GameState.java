package fr.nicknqck;

import fr.nicknqck.events.custom.RoleGiveEvent;
import fr.nicknqck.items.Items;
import fr.nicknqck.items.RodTridimensionnelle;
import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.aot.builders.titans.Titans;
import fr.nicknqck.roles.aot.mahr.*;
import fr.nicknqck.roles.aot.soldats.*;
import fr.nicknqck.roles.aot.solo.Eren;
import fr.nicknqck.roles.aot.solo.Gabi;
import fr.nicknqck.roles.aot.solo.TitanUltime;
import fr.nicknqck.roles.aot.titanrouge.*;
import fr.nicknqck.roles.builder.RoleBase;
import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.roles.custom.LeComte;
import fr.nicknqck.roles.custom.LeJuge;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ds.demons.*;
import fr.nicknqck.roles.ds.demons.lune.*;
import fr.nicknqck.roles.ds.slayers.*;
import fr.nicknqck.roles.ds.slayers.pillier.*;
import fr.nicknqck.roles.ds.solos.*;
import fr.nicknqck.roles.mc.nether.Blaze;
import fr.nicknqck.roles.mc.nether.Brute;
import fr.nicknqck.roles.mc.nether.MagmaCube;
import fr.nicknqck.roles.mc.overworld.*;
import fr.nicknqck.roles.mc.solo.Warden;
import fr.nicknqck.roles.mc.solo.WitherBoss;
import fr.nicknqck.roles.ns.Hokage;
import fr.nicknqck.roles.ns.akatsuki.*;
import fr.nicknqck.roles.ns.akatsuki.blancv2.ZetsuBlancV2;
import fr.nicknqck.roles.ns.orochimaru.*;
import fr.nicknqck.roles.ns.orochimaru.edotensei.Kabuto;
import fr.nicknqck.roles.ns.orochimaru.edotensei.Orochimaru;
import fr.nicknqck.roles.ns.shinobi.*;
import fr.nicknqck.roles.ns.shinobi.porte.GaiV2;
import fr.nicknqck.roles.ns.shinobi.porte.RockLeeV2;
import fr.nicknqck.roles.ns.solo.Danzo;
import fr.nicknqck.roles.ns.solo.Gaara;
import fr.nicknqck.roles.ns.solo.jubi.Madara;
import fr.nicknqck.roles.ns.solo.jubi.Obito;
import fr.nicknqck.roles.ns.solo.kumogakure.Ginkaku;
import fr.nicknqck.roles.ns.solo.kumogakure.Kinkaku;
import fr.nicknqck.roles.ns.solo.zabuza_haku.Haku;
import fr.nicknqck.roles.ns.solo.zabuza_haku.Zabuza;
import fr.nicknqck.roles.valo.agents.Iso;
import fr.nicknqck.roles.valo.agents.Neon;
import fr.nicknqck.scenarios.impl.FFA;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import fr.nicknqck.utils.packets.NMSPacket;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GameState{
	@Getter
	@Setter
	private int timeProcHokage = 90;
	@Getter
	private final List<Roles> deadRoles = new ArrayList<>();
	public int TridiCooldown = 16;
	public boolean hasPregen = false;
	public boolean pregenNakime = false;
	public boolean gameCanLaunch = false;
	@Getter
	@Setter
	private boolean roleAttributed = false;
	@Getter
	private final Map<UUID, GamePlayer> GamePlayer = new LinkedHashMap<>();
	@Setter
	@Getter
	private int groupe = 5;
	@Getter
	@Setter
	private int minTimeSpawnBiju = 90;
	@Getter
	@Setter
	private int maxTimeSpawnBiju = 160;
	@Getter
	public int TimingAssassin = 10;
	public boolean morteclair = true;
	public String msgBoard = ChatColor.GOLD+"UHC-Meetup "+ChatColor.RED+"V1";
	public enum ServerStates {
		InLobby,
		InGame,
		GameEnded
    }
	@Getter
	public enum Roles {
		//Solo ds
		Yoriichi(TeamList.Solo, "ds", 0, new ItemBuilder(Material.DOUBLE_PLANT).setName("§eYoriichi").toItemStack(), "§bNickNqck"),
		Jigoro(TeamList.Solo, "ds", 1, new ItemBuilder(Material.GLOWSTONE).setName("§eJigoro").toItemStack(), "§bNickNqck"),
		Shinjuro(TeamList.Solo, "ds", 2, new ItemBuilder(Material.LAVA_BUCKET).setName("§eShinjuro").toItemStack(), "§bNickNqck"),
		ShinjuroV2(TeamList.Solo, "ds", 3, new ItemBuilder(Material.FLINT_AND_STEEL).setName("§eShinjuro§7 (§6V2§7)").toItemStack(), "§bNickNqck"),
		JigoroV2(TeamList.Solo, "ds", 4, new ItemBuilder(Material.NETHER_STAR).setName("§eJigoro§7 (§6V2§7)").toItemStack(), "§bNickNqck"),
		KyogaiV2(TeamList.Solo, "ds", 5, new ItemBuilder(Material.STICK).setName("§eKyogai §7(§6V2§7)").toItemStack(), "§bNickNqck"),
		SlayerSolo(TeamList.Solo, "ds", 6, new ItemBuilder(Material.IRON_SWORD).setName("§ePourfendeur Solitaire").toItemStack(), "§bNickNqck"),
		//Démons ds
		Muzan(TeamList.Demon, "ds", 0, new ItemBuilder(Material.REDSTONE_ORE).setName("§cMuzan").toItemStack(), "§bNickNqck"),
		Kokushibo(TeamList.Demon, "ds", 1, new ItemBuilder(Material.DIAMOND_SWORD).setName("§cKokushibo").toItemStack(), "§bNickNqck"),
		Doma(TeamList.Demon, "ds", 2, new ItemBuilder(Material.PACKED_ICE).setName("§cDoma").toItemStack(), "§bNickNqck"),
		Akaza(TeamList.Demon, "ds", 3, new ItemBuilder(Material.APPLE).setName("§cAkaza").toItemStack(), "§bNickNqck"),
		Nakime(TeamList.Demon, "ds", 18, new ItemBuilder(Material.MAGMA_CREAM).setName("§cNakime").toItemStack(), "§bNickNqck"),
		Hantengu(TeamList.Demon, "ds", 4, new ItemBuilder(Material.RABBIT_FOOT).setName("§cHantengu").toItemStack(), "§bNickNqck"),
		HantenguV2(TeamList.Demon, "ds", 11, new ItemBuilder(Material.NETHER_STAR).setName("§cHantengu§7 (§6V2§7)").toItemStack(), "§bNickNqck"),
		Gyokko(TeamList.Demon, "ds", 5, new ItemBuilder(Material.FLOWER_POT_ITEM).setName("§cGyokko").toItemStack(), "§bNickNqck"),
		Daki(TeamList.Demon, "ds", 6, new ItemBuilder(Material.IRON_FENCE).setName("§cDaki").toItemStack(), "§bNickNqck"),
		Gyutaro(TeamList.Demon, "ds", 7, new ItemBuilder(Material.DIAMOND_HOE).setName("§cGyutaro").toItemStack(), "§bNickNqck"),
		Kaigaku(TeamList.Demon, "ds", 8, new ItemBuilder(Material.YELLOW_FLOWER).setName("§cKaigaku").toItemStack(), "§bNickNqck"),
		Enmu(TeamList.Demon, "ds", 19, new ItemBuilder(Material.EYE_OF_ENDER).setName("§cEnmu").toItemStack(), "§bNickNqck"),
		Rui(TeamList.Demon, "ds", 16, new ItemBuilder(Material.STRING).setName("§cRui").toItemStack(), "§bNickNqck"),
		Kyogai(TeamList.Demon, "ds", 3, new ItemBuilder(Material.DISPENSER).setName("§cKyogai").toItemStack(), "§bNickNqck"),
		Susamaru(TeamList.Demon, "ds", 9, new ItemBuilder(Material.BOW).setName("§cSusamaru").toItemStack(), "§bNickNqck"),
		Furuto(TeamList.Demon, "ds", 10, new ItemBuilder(Material.NETHER_BRICK).setName("§cFuruto").toItemStack(), "§bNickNqck"),
		DemonSimpleV2(TeamList.Demon, "ds", 12, new ItemBuilder(Material.NETHER_STALK).setName("§cDemonSimple§7 (§6V2§7)").toItemStack(), "§bMega02600"),
		Yahaba(TeamList.Demon, "ds", 13, new ItemBuilder(Material.COMPASS).setName("§cYahaba").toItemStack(), "§bNickNqck"),
		DemonMain(TeamList.Demon, "ds", 14, new ItemBuilder(Material.SKULL_ITEM).setName("§cDemon Main").setDurability(3).toItemStack(), "§bNickNqck"),
		Demon(TeamList.Demon, "ds", 15, new ItemBuilder(Material.NETHER_FENCE).setName("§cDemon Simple").toItemStack(), "§bNickNqck"),
		Kumo(TeamList.Demon, "ds", 17, new ItemBuilder(Material.WEB).setName("§cKumo").toItemStack(), "§bNickNqck"),
		//Slayer ds
		Kagaya(TeamList.Slayer, "ds", 0, new ItemBuilder(Material.CHEST).setName("§aKagaya").toItemStack(), "§bNickNqck"),
		Gyomei(TeamList.Slayer, "ds", 1, new ItemBuilder(Material.IRON_AXE).setName("§aGyomei").toItemStack(), "§bNickNqck"),
		Sanemi(TeamList.Slayer, "ds", 2, new ItemBuilder(Material.QUARTZ).setName("§aSanemi").toItemStack(), "§bNickNqck"),
		Tomioka(TeamList.Slayer, "ds", 3, new ItemBuilder(Material.WATER_BUCKET).setName("§aTomioka").toItemStack(), "§bNickNqck"),
		Kyojuro(TeamList.Slayer, "ds", 4, new ItemBuilder(Material.FLINT_AND_STEEL).setName("§aKyojuro").toItemStack(), "§bNickNqck"),
		Muichiro(TeamList.Slayer, "ds", 5, new ItemBuilder(Material.FEATHER).setName("§aMuichiro").toItemStack(), "§bNickNqck"),
		Tengen(TeamList.Slayer, "ds", 6, new ItemBuilder(Material.JUKEBOX).setName("§aTengen").toItemStack(), "§bNickNqck"),
		Shinobu(TeamList.Slayer, "ds", 7, new ItemBuilder(Material.SPIDER_EYE).setName("§aShinobu").toItemStack(), "§bNickNqck"),
		Mitsuri(TeamList.Slayer, "ds", 8, new ItemBuilder(Material.RED_ROSE).setName("§aMitsuri").toItemStack(), "§bNickNqck"),
		Obanai(TeamList.Slayer, "ds", 9, new ItemBuilder(Material.GOLDEN_CARROT).setName("§aObanai").toItemStack(), "§bNickNqck"),
		Kanae(TeamList.Slayer, "ds", 10, new ItemBuilder(Material.DIAMOND_SWORD).setName("§aKanae").toItemStack(), "§bNickNqck"),

		Tanjiro(TeamList.Slayer, "ds", 11, new ItemBuilder(Material.BLAZE_ROD).setName("§aTanjiro").toItemStack(), "§bNickNqck"),
		Nezuko(TeamList.Slayer, "ds", 12, new ItemBuilder(Material.REDSTONE).setName("§aNezuko").toItemStack(), "§bNickNqck"),
		ZenItsu(TeamList.Slayer, "ds", 13, new ItemBuilder(Material.GLOWSTONE_DUST).setName("§aZenItsu").toItemStack(), "§bNickNqck"),
		Inosuke(TeamList.Slayer, "ds", 14, new ItemBuilder(Material.PORK).setName("§aInosuke").toItemStack(), "§bNickNqck"),
		Kanao(TeamList.Slayer, "ds", 15, new ItemBuilder(Material.LEATHER_BOOTS).setName("§aKanao").toItemStack(), "§bNickNqck"),
		Sabito(TeamList.Slayer, "ds", 16, new ItemBuilder(Material.POTION).setDurability(0).setName("§aSabito").toItemStack(), "§bNickNqck"),
		Urokodaki(TeamList.Slayer, "ds", 17, new ItemBuilder(Material.WATER_LILY).setName("§aUrokodaki").toItemStack(), "§bNickNqck"),
		Makomo(TeamList.Slayer, "ds", 18, new ItemBuilder(Material.BOWL).setName("§aMakomo").toItemStack(), "§bNickNqck"),
		Hotaru(TeamList.Slayer, "ds", 19, new ItemBuilder(Material.ANVIL).setName("§aHotaru").toItemStack(), "§bNickNqck"),
		Slayer(TeamList.Slayer, "ds", 20, new ItemBuilder(Material.IRON_SWORD).setName("§aPourfendeur Simple").toItemStack(), "§bNickNqck"),
		//Mahr aot
		Reiner(TeamList.Mahr, "aot", 0, new ItemBuilder(Material.QUARTZ).setName("§9Reiner").toItemStack(), "§bNickNqck"),
		Pieck(TeamList.Mahr, "aot", 1, new ItemBuilder(Material.CHEST).setName("§9Pieck").toItemStack(), "§bNickNqck"),
		Bertolt(TeamList.Mahr, "aot", 2, new ItemBuilder(Material.MAGMA_CREAM).setName("§9Bertolt").toItemStack(), "§bNickNqck"),
		Porco(TeamList.Mahr, "aot", 3, new ItemBuilder(Material.SLIME_BALL).setName("§9Porco").toItemStack(), "§bNickNqck"),
		Magath(TeamList.Mahr, "aot", 4, new ItemBuilder(Material.COMPASS).setName("§9Magath").toItemStack(), "§bNickNqck"),
		Lara(TeamList.Mahr, "aot", 5, new ItemBuilder(Material.IRON_BLOCK).setName("§9Lara").toItemStack(), "§bNickNqck"),
		//Titans aot
		TitanBestial(TeamList.Titan, "aot", 2, new ItemBuilder(Material.MOB_SPAWNER).setName("§cTitan Bestial").toItemStack(), "§bNickNqck"),
		PetitTitan(TeamList.Titan, "aot", 0, new ItemBuilder(Material.FEATHER).setName("§cPetit Titan").toItemStack(), "§bNickNqck"),
		GrandTitan(TeamList.Titan, "aot", 1, new ItemBuilder(Material.STICK).setName("§cGrand Titan").toItemStack(), "§bNickNqck"),
		TitanDeviant(TeamList.Titan, "aot", 4, new ItemBuilder(Material.SNOW_BALL).setName("§cTitan Deviant").toItemStack(), "§bNickNqck"),
		Jelena(TeamList.Titan, "aot", 3, new ItemBuilder(Material.CHEST).setName("§cJelena").toItemStack(), "§bNickNqck"),
		//Soldat aot
		Livai(TeamList.Soldat, "aot", 0, new ItemBuilder(Material.SUGAR).setName("§aLivai").toItemStack(), "§bMega02600"),
		Soldat(TeamList.Soldat, "aot", 1, new ItemBuilder(Material.IRON_SWORD).setName("§aSoldat").toItemStack(), "§bMega02600"),
		Erwin(TeamList.Soldat, "aot", 2, new ItemBuilder(Material.SIGN).setName("§aErwin").toItemStack(), "§bNickNqck"),
		Armin(TeamList.Soldat, "aot", 3, new ItemBuilder(Material.CHEST).setName("§aArmin").toItemStack(), "§bNickNqck"),
		Eclaireur(TeamList.Soldat, "aot", 4, new ItemBuilder(Material.GOLDEN_CARROT).setName("§aEclaireur").toItemStack(), "§bMega02600"),
		Jean(TeamList.Soldat, "aot", 5, new ItemBuilder(Material.FIREWORK).setName("§aJean").toItemStack(), "§bMega02600"),
		Onyankopon(TeamList.Soldat, "aot", 6, new ItemBuilder(Material.ENDER_PEARL).setName("§aOnyankopon").toItemStack(), "§bMega02600"),
		Hansi(TeamList.Soldat, "aot", 7, new ItemBuilder(Material.THIN_GLASS).setName("§aHansi").toItemStack(), "§bNickNqck"),
		Sasha(TeamList.Soldat, "aot", 8, new ItemBuilder(Material.BOW).setName("§aSasha").toItemStack(), "§bNickNqck"),
		Conny(TeamList.Soldat, "aot", 9, new ItemBuilder(Material.SUGAR_CANE).setName("§aConny").toItemStack(), "§bMega02600"),
		//Solo Aot
		Eren(TeamList.Solo, "aot", 11, new ItemBuilder(Material.ROTTEN_FLESH).setName("§eEren").toItemStack(), "§bNickNqck"),
		Gabi(TeamList.Solo, "aot", 10, new ItemBuilder(Material.SPONGE).setName("§eGabi").toItemStack(), "§bNickNqck"),
		TitanUltime(TeamList.Solo, "aot", 12, new ItemBuilder(Material.QUARTZ).setName("§eTitan Ultime").toItemStack(), "§bNickNqck"),
		//Jubi ns
		Madara(TeamList.Jubi, "ns", 0, new ItemBuilder(Material.NETHER_STAR).setName("§dMadara").toItemStack(), "§aYukan"),
		Obito(TeamList.Jubi, "ns", 1, new ItemBuilder(Material.COMPASS).setName("§dObito").toItemStack(), "§aYukan"),
		//Solo ns
		Gaara(TeamList.Solo, "ns", 0, new ItemBuilder(Material.SAND).setName("§eGaara").toItemStack(), "§bNickNqck"),
		Danzo(TeamList.Solo, "ns", 1, new ItemBuilder(Material.DIAMOND_SWORD).setName("§eDanzo").toItemStack(), "§bNickNqck"),
		//Orochimaru ns
		Orochimaru(TeamList.Orochimaru, "ns", 0, new ItemBuilder(Material.NETHER_STAR).setName("§5Orochimaru").toItemStack(), "§bNickNqck"),
		Kabuto(TeamList.Orochimaru, "ns", 1, new ItemBuilder(Material.WATER_LILY).setName("§5Kabuto").toItemStack(), "§bNickNqck"),
		Karin(TeamList.Orochimaru, "ns", 2, new ItemBuilder(Material.BOOK).setName("§5Karin").toItemStack(), "§bNickNqck"),
		Kimimaro(TeamList.Orochimaru, "ns", 3, new ItemBuilder(Material.BONE).setName("§5Kimimaro").toItemStack(), "§bNickNqck"),
		Suigetsu(TeamList.Orochimaru, "ns", 4, new ItemBuilder(Material.WATER_BUCKET).setName("§5Suigetsu").toItemStack(), "§bNickNqck"),
		Sasuke(TeamList.Orochimaru, "ns", 5, new ItemBuilder(Material.EYE_OF_ENDER).setName("§5Sasuke").toItemStack(), "§aYukan"),
		Jugo(TeamList.Orochimaru, "ns", 6, new ItemBuilder(Material.ROTTEN_FLESH).setName("§5Jugo").toItemStack(), "§bNickNqck"),
		//Akatsuki
		Nagato(TeamList.Akatsuki, "ns", 0, new ItemBuilder(Material.DIAMOND_SWORD).setName("§cNagato").toItemStack(), "§bNickNqck"),
		Konan(TeamList.Akatsuki, "ns", 1, new ItemBuilder(Material.PAPER).setName("§cKonan").toItemStack(), "§bNickNqck"),
		Itachi(TeamList.Akatsuki, "ns", 2, new ItemBuilder(Material.EYE_OF_ENDER).setName("§cItachi").toItemStack(), "§aYukan"),
		Kisame(TeamList.Akatsuki, "ns", 3, new ItemBuilder(Material.WATER_BUCKET).setName("§cKisame").toItemStack(), "§bNickNqck"),
		Deidara(TeamList.Akatsuki, "ns", 6, new ItemBuilder(Material.BOW).setName("§cDeidara").toItemStack(), "§bNickNqck"),
		Hidan(TeamList.Akatsuki, "ns", 7, new ItemBuilder(Material.DIAMOND_HOE).setName("§cHidan").toItemStack(), "§bNickNqck"),
		Kakuzu(TeamList.Akatsuki, "ns", 8, new ItemBuilder(Material.ROTTEN_FLESH).setName("§cKakuzu").toItemStack(), "§bNickNqck"),
		ZetsuNoir(TeamList.Akatsuki, "ns", 9, new ItemBuilder(Material.INK_SACK).setName("§cZetsu Noir").toItemStack(), "§bMega02600"),
		ZetsuBlanc(TeamList.Akatsuki, "ns", 10, new ItemBuilder(Material.BONE).setName("§cZetsu Blanc").toItemStack(), "§bMega02600"),
		ZetsuBlancV2(TeamList.Akatsuki, "ns", 11, new ItemBuilder(Material.ENDER_CHEST).setName("§cZetsu Blanc§7 (§6V2§7)").toItemStack(), "§bByC3RV0L3NT"),
		//Shinobi
		Naruto(TeamList.Shinobi, "ns", 0, new ItemBuilder(Material.INK_SACK).setDurability(14).setName("§aNaruto").toItemStack(), "§bNickNqck"),
		Sakura(TeamList.Shinobi, "ns", 1, new ItemBuilder(Material.POTION).setDurability(8229).setName("§aSakura").toItemStack(), "§bNickNqck"),
		Kakashi(TeamList.Shinobi, "ns", 2, new ItemBuilder(Material.EYE_OF_ENDER).setName("§aKakashi").toItemStack(), "§aYukan"),
		Jiraya(TeamList.Shinobi, "ns", 3, new ItemBuilder(Material.BOOK_AND_QUILL).setName("§aJiraya").toItemStack(), "§bNickNqck"),
		Minato(TeamList.Shinobi, "ns", 4, new ItemBuilder(Material.BOW).setName("§aMinato").toItemStack(), "§bNickNqck"),
		Tsunade(TeamList.Shinobi, "ns", 5, new ItemBuilder(Material.POTION).setDurability(16421).setName("§aTsunade").toItemStack(), "§bNickNqck"),
		Konohamaru(TeamList.Shinobi, "ns", 6, new ItemBuilder(Material.SULPHUR).setName("§aKonohamaru").toItemStack(), "§bNickNqck"),
		RockLee(TeamList.Shinobi, "ns", 7, new ItemBuilder(Material.GLASS_BOTTLE).setName("§aRock Lee").toItemStack(), "§aYukan"),
		Gai(TeamList.Shinobi, "ns", 8, new ItemBuilder(Material.NETHER_STAR).setName("§aGaï").toItemStack(), "§aYukan"),
		Asuma(TeamList.Shinobi, "ns", 9, new ItemBuilder(Material.FIREBALL).setName("§aAsuma").toItemStack(), "§bNickNqck"),
		KillerBee(TeamList.Shinobi, "ns", 10, new ItemBuilder(Material.INK_SACK).setName("§aKiller Bee").toItemStack(), "§aYukan"),
		Raikage(TeamList.Shinobi, "ns", 11, new ItemBuilder(Material.NETHER_STAR).setName("§aYondaime Raikage").toItemStack(), "§aYukan"),
		TenTen(TeamList.Shinobi, "ns", 12, new ItemBuilder(Material.BOW).setName("§aTenTen").toItemStack(), "§bNickNqck"),
		Kurenai(TeamList.Shinobi, "ns", 13, new ItemBuilder(Material.INK_SACK).setDurability(1).setName("§aKurenai").toItemStack(), "§bNickNqck"),
		Shikamaru(TeamList.Shinobi, "ns", 14, new ItemBuilder(Material.SPIDER_EYE).setName("§aShikamaru").toItemStack(), "§bNickNqck"),
		Ino(TeamList.Shinobi, "ns", 15, new ItemBuilder(Material.ARMOR_STAND).setName("§aIno").toItemStack(), "§bNickNqck"),
		Fugaku(TeamList.Shinobi, "ns", 16, new ItemBuilder(Material.MAGMA_CREAM).setName("§aFugaku").toItemStack(), "§bNickNqck"),
		//Haku et Zabuza
		Zabuza(TeamList.Zabuza_et_Haku, "ns", 0, new ItemBuilder(Material.DIAMOND_SWORD).setName("§bZabuza").toItemStack(), "§aYukan"),
		Haku(TeamList.Zabuza_et_Haku, "ns", 1, new ItemBuilder(Material.PACKED_ICE).setName("§bHaku").toItemStack(), "§aYukan"),
		//KumoGakure
		Ginkaku(TeamList.Kumogakure, "ns", 0, new ItemBuilder(Material.LADDER).setName("§6Ginkaku").toItemStack(), "§bByC3RV0L3NT"),
		Kinkaku(TeamList.Kumogakure, "ns", 1, new ItemBuilder(Material.NETHER_STAR).setName("§6Kinkaku").toItemStack(), "§bByC3RV0L3NT"),
		//OverWorld
        Poulet(TeamList.OverWorld, "mc", 0, new ItemBuilder(Material.FEATHER).setName("§aPoulet").toItemStack(), "§bMega02600"),
		Zombie(TeamList.OverWorld, "mc", 1, new ItemBuilder(Material.ROTTEN_FLESH).setName("§aZombie").toItemStack(), "§bMega02600"),
		Squelette(TeamList.OverWorld, "mc", 2, new ItemBuilder(Material.BONE).setName("§aSquelette").toItemStack(), "§bMega02600"),
		AraigneeVenimeuse(TeamList.OverWorld, "mc", 3, new ItemBuilder(Material.SPIDER_EYE).setName("§aAraignée Venimeuse").toItemStack(), "§bMega02600"),
		GolemDeFer(TeamList.OverWorld, "mc", 4, new ItemBuilder(Material.IRON_BLOCK).setName("§aGolem De Fer").toItemStack(), "§bMega02600"),
		Vache(TeamList.OverWorld, "mc", 5, new ItemBuilder(Material.MILK_BUCKET).setName("§aVache").toItemStack(), "§bRémi"),
		//Nether
		Blaze(TeamList.Nether, "mc", 0, new ItemBuilder(Material.BLAZE_ROD).setName("§cBlaze").toItemStack(), "§bMega02600"),
		Brute(TeamList.Nether, "mc", 1, new ItemBuilder(Material.GOLD_AXE).setName("§cBrute").toItemStack(), "§bMega02600"),
		MagmaCube(TeamList.Nether, "mc", 2, new ItemBuilder(Material.MAGMA_CREAM).setName("§cMagma Cube").toItemStack(), "§bMega02600"),

		//Solo mc
		Warden(TeamList.Solo, "mc", 0, new ItemBuilder(Material.NOTE_BLOCK).setName("§eWarden").toItemStack(), "§bNickNqck"),
		Wither(TeamList.Solo, "mc", 1, new ItemBuilder(Material.NOTE_BLOCK).setName("§eWither").toItemStack(), "§bNickNqck"),
		//Agent valorant (il n'y aura que Iso)
		Iso(TeamList.Solo, "valo", 0, new ItemBuilder(Material.NETHER_STAR).setName("§dIso").toItemStack(), "§bNickNqck"),
		Neon(TeamList.Solo, "valo", 1, new ItemBuilder(Material.NETHER_STAR).setName("§9Neon").toItemStack(), "§bNickNqck"),
		//Custom roles
		LeComte(TeamList.Solo, "custom", 0, new ItemBuilder(Material.NETHER_STAR).setName("§eLe Compte").toItemStack(), "§bNickNqck"),
		LeJuge(TeamList.Solo, "custom", 0, new ItemBuilder(Material.DIAMOND_SWORD).setName("§eLe Juge").toItemStack(), "§bNickNqck")
		;
		private final TeamList team;
		private final String mdj;
		private final int nmb;
		private final ItemStack item;
		private final String gDesign;
		Roles(TeamList team, String mdj, int nmb, ItemStack item, String GDesign) {
			this.team = team;
			this.mdj = mdj;
			this.nmb = nmb;
			this.item = item;
			this.gDesign = GDesign;
		}
	}
	@Getter
	public enum MDJ{
		Aucun(new ItemBuilder(Material.WOOL).setName("Aucun").toItemStack()),
		DS(new ItemBuilder(Material.REDSTONE).setName("§6Demon Slayer").toItemStack()),
		AOT(new ItemBuilder(Material.FEATHER).setName("§6AOT").toItemStack()),
		NS(new ItemBuilder(Material.NETHER_STAR).setName("§6Naruto").toItemStack()),
		MC(new ItemBuilder(Material.GRASS).setName("§aMinecraft").toItemStack());

		private final ItemStack item;
		MDJ(ItemStack item) {
			this.item = item;
		}

		public ItemStack getItem() {
			ItemStack itemC = item.clone();
			ItemMeta iMeta = item.getItemMeta();
			if (GameState.getInstance().mdj == this){
				iMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
				iMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				iMeta.setLore(Collections.singletonList("§r§aActivé"));
			} else {
				iMeta.setLore(Collections.singletonList("§r§cDésactivé"));
			}
			itemC.setItemMeta(iMeta);
			return itemC;
		}
	}
	@NonNull
	@Getter
	@Setter
	private MDJ mdj = MDJ.Aucun;
	public boolean isAllMdjNull() {
		return mdj == MDJ.Aucun;
	}
	@Getter
	public int roleTimer = 1;
	public int pvpTimer = 1;
	public int getPvPTimer() {
		return pvpTimer;
	}
	public boolean JigoroV2Pacte2 = false;
	public boolean JigoroV2Pacte3 = false;
	@Setter
	@Getter
	private ServerStates serverState = ServerStates.InLobby;
	@Getter
	private final HashMap<Roles, Integer> availableRoles = new HashMap<>();
	@Setter
	@Getter
	private List<UUID> inLobbyPlayers = new ArrayList<>();
	@Setter
	@Getter
	private List<UUID> inGamePlayers = new ArrayList<>();
	@Getter
	@Setter
	private List<Player> inSpecPlayers = new ArrayList<>();
	@Getter
	private final HashMap<Player, RoleBase> playerRoles = new HashMap<>();
	@Getter
	private final HashMap<UUID, HashMap<Player, RoleBase>> playerKills = new HashMap<>();
	public List<Player> igPlayers = new ArrayList<>();
	@Getter
	@Setter
	int inGameTime = 0;
	@Getter
	public boolean nightTime = false;
	boolean prevNightTime = true;
	boolean pvp = false;
	public boolean getPvP() {
		return pvp;
	}
	public void setPvP(boolean p) {
		pvp = p;
	}
	boolean shrinking = false;
	@Getter
	@Setter
	private int actualPvPTimer = getPvPTimer();
	public int timeday = 60*5;
	public int t = 0;//Utilisée dans GameListener
	public int xpfer = 0;
	public int xpor = 0;
	public int xpcharbon = 0;
	public int xpdiams =0;
	public Player infected = null;
	public int timewaitingbeinfected = 60;
	public Player infecteur = null;
	public boolean roletab = false;
	@Getter
	public int critP = 20;
	@Getter
	private static GameState instance;
	public List<Player> Shifter = new ArrayList<>();
	public List<Player> TitansRouge = new ArrayList<>();
	public List<Player> shutdown = new ArrayList<>();
	public List<Player> infectedbyadmin = new ArrayList<>();
	public ArrayList<Player> Obi = new ArrayList<>();
	public ArrayList<Player> getInObiPlayers() {return Obi;}
	public void setInObiPlayers(ArrayList<Player> SleepingPlayers) {Obi = SleepingPlayers;}
	public void addInObiPlayers(Player player) {Obi.add(player);}
	public void delInObiPlayers(Player player) {Obi.remove(player);}
	public List<Roles> DeadRole = new ArrayList<>();
	public ArrayList<Player> SleepingPlayer = new ArrayList<>();
	public ArrayList<Player> getInSleepingPlayers() {return SleepingPlayer;}
	public void setInSleepingPlayers(ArrayList<Player> SleepingPlayers) {SleepingPlayer = SleepingPlayers;}
	public void addInSleepingPlayers(Player player) {SleepingPlayer.add(player);}
	public void delInSleepingPlayers(Player player) {SleepingPlayer.remove(player);}

	public GameState() {
		instance = this;
	}

	public void addInLobbyPlayers(Player player) {inLobbyPlayers.add(player.getUniqueId());}

	public void delInLobbyPlayers(Player player) {inLobbyPlayers.remove(player.getUniqueId());}

	public void addInGamePlayers(Player player) {inGamePlayers.add(player.getUniqueId());}

	public void delInGamePlayers(Player player) {inGamePlayers.remove(player.getUniqueId());}

	public void addInSpecPlayers(Player player) {inSpecPlayers.add(player);}

	public void delInSpecPlayers(Player player) {inSpecPlayers.remove(player);}

	public void addInPlayerRoles(Player player, RoleBase role) {playerRoles.put(player, role);}
	public void delInPlayerRoles(Player player) {playerRoles.remove(player);}

	public final boolean hasRoleNull(final Player player) {
		if (player == null)return true;
		if (getGamePlayer().containsKey(player.getUniqueId())) {
            return getGamePlayer().get(player.getUniqueId()).getRole() == null;
        }
        return !getGamePlayer().containsKey(player.getUniqueId()) || !isRoleAttributed();
	}

	public void addInAvailableRoles(Roles role, Integer nmb) {availableRoles.put(role, nmb);}

	public void addPlayerKills(Player player) {playerKills.put(player.getUniqueId(), new HashMap<>());}
	//public void delPlayerKills(Player player) {playerKills.remove(player);}

	public RoleBase GiveRole(Player aziz) {
		if (getPlayerRoles().containsKey(aziz)) return null;
		//Roles roleType = getAvailableRoles().get(new Random().nextInt(getAvailableRoles().size()));
		ArrayList<Roles> roles = new ArrayList<>();
		for (Roles role : getAvailableRoles().keySet()) {
			for (int i = 0; i < getAvailableRoles().get(role); i++) {
				roles.add(role);
			}
		}	
		for (RoleBase r : getPlayerRoles().values()) {
            roles.remove(r.getRoles());
		}
		
		Roles roleType;
		roleType = roles.get(new Random().nextInt(roles.size()));
		RoleBase role = null;
		UUID player = aziz.getUniqueId();
		if (!getGamePlayer().containsKey(player))return null;
		switch(roleType) {
		case Muzan:
			role = new Muzan(player);
			break;
		case Nezuko:
			role = new NezukoV2(player);
			break;
		case Tanjiro:
			role = new Tanjiro(player);
			break;
		case Kokushibo:
			role = new Kokushibo(player);
			break;
		case Jigoro:
			role = new Jigoro(player);
			break;
		case ZenItsu:
			role = new ZenItsuV2(player);
			break;
		case Kaigaku:
			role = new Kaigaku(player);
			break;
		case Tomioka:
			role = new TomiokaV2(player);
			break;
		case Akaza:
			role = new Akaza(player);
			break;
		case Kyojuro:
			role = new KyojuroV2(player);
			break;
		case Gyokko:
			role = new Gyokko(player);
			break;
		case Muichiro:
			role = new MuichiroV2(player);
		break;
		case Gyomei:
			role = new GyomeiV2(player);
			break;
		case Daki:
			role = new Daki(player);
			break;
		case Gyutaro:
			role = new GyutaroV2(player);
			break;
		case Inosuke:
			role = new InosukeV2(player);
			break;
		case Tengen:
			role = new TengenV2(player);
			break;
		case Doma:
			role = new Doma(player);
			break;
		case Shinobu:
			role = new ShinobuV2(player);
			break;
		case Kanao:
			role = new KanaoV2(player);
			break;
		case Obanai:
			role = new ObanaiV2(player);
			break;
		case Yoriichi:
			role = new Yoriichi(player);
			break;
		case Slayer:
			role = new PourfendeurV2(player);
			role.setTeam(TeamList.Slayer);
			break;
		case DemonMain:
			role = new DemonMain(player);
			break;
		case Sabito:
			role = new Sabito(player);
			break;
		case Urokodaki:
			role = new UrokodakiV2(player);
			break;
		case Makomo:
			role = new Makomo(player);
			break;
		case Hantengu:
			role = new Hantengu(player);
			break;
		case Demon:
			role = new Demon_Simple(player);
			break;
		case Sanemi:
			role = new SanemiV2(player);
			break;
		case Shinjuro:
			role = new Shinjuro(player);
			break;			
		case Kyogai:
			role = new Kyogai(player);
			break;
		case Kanae:
			role = new KanaeV2(player);
			break;
		case Rui:
			role = new Rui(player);
			break;
		case Enmu:
			role = new Enmu(player);
			break;
		case Mitsuri:
			role = new MitsuriV2(player);
			break;
		case Kagaya:
			role = new KagayaV2(player);
			break;
		case Susamaru:
			role = new Susamaru(player);
			break;
		case Furuto:
			role = new Furuto(player);
			break;
		case JigoroV2:
			role = new JigoroV2(player);
			break;
		case HantenguV2:
			role = new HantenguV2(player);
			break;
		case DemonSimpleV2:
			role = new Demon_SimpleV2(player);
			break;
		case Yahaba:
			role = new Yahaba(player);
			break;
		case Hotaru:
			role = new HotaruV2(player);
			break;
		case Kumo:
			role = new Kumo(player);
		break;
		case Reiner:
			role = new Reiner(player);
			break;
		case Pieck:
			role = new Pieck(player);
			break;
		case Bertolt:
			role = new Bertolt(player);
			break;
		case Porco:
			role = new Porco(player);
			break;
		case Magath:
			role = new Magath(player);
			break;
		case Lara:
			role = new Lara(player);
			break;
		case PetitTitan:
			role = new PetitTitan(player);
			break;
		case GrandTitan:
			role = new GrandTitan(player);
			break;
		case Gaara:
			role = new Gaara(player);
			break;
		case Livai:
			role = new Livai(player);
			break;
		case TitanBestial:
			role = new TitanBestial(player);
			break;
		case Soldat:
			role = new Soldat(player);
			break;
		case Erwin:
			role = new Erwin(player);
			break;
		case Gabi:
			role = new Gabi(player);
			break;
		case Nakime:
			role = new Nakime(player);
			break;
		case Armin:
			role = new Armin(player);
			break;
		case Eren:
			role = new Eren(player);
			break;
		case Eclaireur:
			role = new Eclaireur(player);
			break;
		case Jean:
			role = new Jean(player);
			break;
		case Jelena:
			role = new Jelena(player);
			break;
		case TitanDeviant:
			role = new TitanDeviant(player);
			break;
		case Onyankopon:
			role = new Onyankopon(player);
			break;
		case Hansi:
			role = new Hansi(player);
			break;
		case TitanUltime:
			role = new TitanUltime(player);
			break;
		case Sasha:
			role = new Sasha(player);
			break;
		case Conny:
			role = new Conny(player);
			break;
		case KyogaiV2:
			role = new KyogaiV2(player);
			break;
		case Itachi:
			role = new Itachi(player);
			break;
		case ShinjuroV2:
			role = new ShinjuroV2(player);
			break;
		case Madara:
			role = new Madara(player);
			break;
		case Obito:
			role = new Obito(player);
			break;
		case Danzo:
			role = new Danzo(player);
			break;
		case Orochimaru:
			role = new Orochimaru(player);
			break;
		case Sasuke:
			role = new Sasuke(player);
			break;
		case Kabuto:
			role = new Kabuto(player);
			break;
		case Kisame:
			role = new Kisame(player);
			break;
		case Karin:
			role = new Karin(player);
			break;
		case Kimimaro:
			role = new Kimimaro(player);
			break;
		case ZetsuNoir:
			role = new ZetsuNoir(player);
			break;
		case ZetsuBlanc:
			role = new ZetsuBlanc(player);
			break;
		case Konan:
			role = new Konan(player);
			break;
		case Kakuzu:
			role = new Kakuzu(player);
			break;
		case Suigetsu:
			role = new Suigetsu(player);
			break;
		case Haku:
			role = new Haku(player);
			break;
			case Zabuza:
			role = new Zabuza(player);
			break;
		case Jugo:
			role = new Jugo(player);
			break;
		case Kakashi:
			role = new Kakashi(player);
			break;
		case Naruto:
			role = new Naruto(player);
			break;
		case Sakura:
			role = new Sakura(player);
			break;
		case Jiraya:
			role = new Jiraya(player);
			break;
		case Minato:
			role = new Minato(player);
			break;
		case Tsunade:
			role = new Tsunade(player);
			break;
		case Konohamaru:
			role = new Konohamaru(player);
			break;
		case Deidara:
			role = new Deidara(player);
			break;
		case Gai:
			role = new GaiV2(player);
			break;
		case RockLee:
			role = new RockLeeV2(player);
			break;
		case Hidan:
			role = new Hidan(player);
			break;
		case Asuma:
			role = new Asuma(player);
			break;
		case KillerBee:
			role = new KillerBee(player);
			break;
		case TenTen:
			role = new Tenten(player);
			break;
		case Raikage:
			role = new YondaimeRaikage(player);
			break;
		case Ginkaku:
			role = new Ginkaku(player);
			break;
		case Warden:
			role = new Warden(player);
			break;
		case Kinkaku:
			role = new Kinkaku(player);
			break;
		case Nagato:
			role = new Nagato(player);
			break;
		case Wither:
			role = new WitherBoss(player);
			break;
		case Kurenai:
			role = new Kurenai(player);
			break;
		case Shikamaru:
			role = new Shikamaru(player);
			break;
        case Poulet:
             role = new Poulet(player);
             break;
		case Ino:
			role = new Ino(player);
			break;
		case Zombie:
			role = new Zombie(player);
			break;
		case Iso:
			role = new Iso(player);
			break;
		case Squelette:
			role = new Squelette(player);
			break;
		case LeComte:
			role = new LeComte(player);
			break;
		case LeJuge:
			role = new LeJuge(player);
			break;
		case AraigneeVenimeuse:
			role = new AraigneeVenimeuse(player);
			break;
		case Fugaku:
			role = new Fugaku(player);
			break;
		case Blaze:
			role = new Blaze(player);
			break;
		case GolemDeFer:
			role = new GolemDeFer(player);
			break;
		case Brute:
			role = new Brute(player);
			break;
		case MagmaCube:
			role = new MagmaCube(player);
			break;
			case SlayerSolo:
				role = new SlayerSolo(player);
				break;
			case ZetsuBlancV2:
				role = new ZetsuBlancV2(player);
				break;
			case Vache:
				role = new Vache(player);
				break;
			case Neon:
				role = new Neon(player);
				break;
		}
		if (role == null) return null;
       getInSpecPlayers().remove(aziz);
		if (role.getRoles() != Roles.Slayer) {
			if (!FFA.getFFA()) {
				role.setTeam(role.getOriginTeam());
			} else {
				role.setTeam(TeamList.Solo);
			}
			System.out.println(role.getOriginTeam().name()+" for role "+role.getRoles().name());
		}
		role.gameState = this;
		addInPlayerRoles(aziz, role);
		fr.nicknqck.player.GamePlayer gamePlayer = getGamePlayer().get(player);
		gamePlayer.setRole(role);
		role.setGamePlayer(gamePlayer);
		if (getPlayerRoles().size() == getInGamePlayers().size()) {
			if (getPlayerRoles().get(aziz).getOriginTeam() == TeamList.Demon && !getPlayerRoles().get(aziz).getRoles().equals(Roles.Kyogai)) {
				canBeAssassin.add(aziz);
				System.out.println(aziz.getName()+" added to canBeAssassinList, size: "+canBeAssassin.size());
			}
			System.out.println("Giving Role Ended");
			System.out.println("Preparing Assassin System");
			Assassin assassin = new Assassin();
			OnEndGiveRole(assassin);
		} else {
			System.out.println("Giving Role: "+getPlayerRoles().size()+"/"+getInGamePlayers().size());
			if (getPlayerRoles().get(aziz).getOriginTeam() == TeamList.Demon) {
				canBeAssassin.add(aziz);
				System.out.println(aziz.getName()+" added to canBeAssassinList "+canBeAssassin.size());
			}
		}
		attributedRole.add(roleType);
		gamePlayer.setDeathLocation(aziz.getLocation());
		Bukkit.getPluginManager().callEvent(new RoleGiveEvent(this, role, roleType, gamePlayer, false));
		return role;
	}
	@Getter
	public List<Roles> attributedRole = new ArrayList<>();
	public ArrayList<Player> canBeAssassin = new ArrayList<>();
	public Player Assassin = null;
	public void OnEndGiveRole(Assassin assa) {
		if (canBeAssassin.isEmpty()) {
			System.out.println("Can't Enable to Start Assassin System because size of TeamList.Demon < 1");
			return;
		}
		System.out.println("Starting Assassin System");
		assa.start(this);
	}

	public void updateGameCanLaunch() {
		gameCanLaunch = (getInLobbyPlayers().size() == this.getroleNMB());
	}

	public int getroleNMB() {
		int nmbrole = 0;
		for (Roles r : getAvailableRoles().keySet()) {
			nmbrole += getAvailableRoles().get(r);
		}
		return nmbrole;
	}
	@Getter
	@Setter
	private int nmbGap = 12;
	public int minnmbGap = 12;
	public static int sharpness = 3;
	public static int nmbblock = 1;
	public static int power = 2;
	public static int pearl = 1;
	public static int eau = 1;
	public static int lave = 0;

	public void changeTabPseudo(final String name,final Player player) {
		try {
            player.setPlayerListName(name);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
	}
	public void spawnLightningBolt(World world, Location loc) {world.strikeLightningEffect(loc);}
	public boolean isApoil(Player player) {
		boolean apoil;
		org.bukkit.inventory.PlayerInventory inv = player.getInventory();
        apoil = inv.getHelmet() == null && inv.getChestplate() == null && inv.getLeggings() == null && inv.getBoots() == null;
		return apoil;
	}
	@SuppressWarnings("deprecation")
	public void sendTitleToAll(String title, String subtitle, boolean NMS) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        	if (NMS) {
        		NMSPacket.sendTitle(onlinePlayer, 10, 20, 10, title, subtitle);
        	} else {
				onlinePlayer.resetTitle();
				onlinePlayer.sendTitle(title, subtitle);
			}
        }
    }
	public void RevivePlayer(@NonNull Player player) {
        if (getServerState() == ServerStates.InGame) {
			if (!hasRoleNull(player)) {
				if (getInSpecPlayers().contains(player)) {
					delInSpecPlayers(player);
					if (!getInGamePlayers().contains(player.getUniqueId())){
						addInGamePlayers(player);
					}
					player.setGameMode(GameMode.SURVIVAL);
					player.teleport(getGamePlayer().get(player.getUniqueId()).getDeathLocation());
					getGamePlayer().get(player.getUniqueId()).setAlive(true);
				}
			}
		}
	}
	public int countEmptySlots(Player player) {
        int emptySlots = 0;
        ItemStack[] contents = player.getInventory().getContents();
        for (ItemStack item : contents) {
            if (item == null || item.getType() == Material.AIR) {
                emptySlots++;
            }
        }
        return emptySlots;
    }
	public void GiveRodTridi(Player player) {
		if (countEmptySlots(player) >=1) {
				player.getInventory().addItem(EquipementTridi());			
		}else {
			player.sendMessage("§7Votre inventaire étant remplis votre§l Équipement Tridimmentionnel§7 à été drop par terre");
			GameListener.dropItem(player.getLocation().clone(), EquipementTridi());
		}
	}
	public boolean rod = false;
	public ItemStack EquipementTridi() {
		if (this.rod) {
			return RodTridimensionnelle.getItem();
		}else {
			return Items.ArcTridi();
		}
	}

	public String sendIntBar(int fnmb, int nMax, int sizeChanger) {
		int max = nMax/sizeChanger;
		int nmb = fnmb/sizeChanger;
		StringBuilder bar = new StringBuilder(" ");
		for (int i = 0; i < nmb; i++) {
			bar.append("§a|");
		}
		for (int i = nmb; i < max; i++) {
			bar.append("§c|");
		}
		bar.append(" ");
		return bar.toString();
	}
	public void sendDescription(Player player) {
		if (!hasRoleNull(player)) {
			for (Titans t : Titans.values()) {
				t.getTitan().onGetDescription(player);
			}
			player.sendMessage(getPlayerRoles().get(player).Desc());
			player.spigot().sendMessage(getPlayerRoles().get(player).getComponent());
			RoleBase role = getPlayerRoles().get(player);
			if (!role.getKnowedRoles().isEmpty()) {
				for (Class<? extends RoleBase> know : role.getKnowedRoles()) {
					for (UUID u : getInGamePlayers()) {
						Player p = Bukkit.getPlayer(u);
						if (p == null)continue;
						if (!hasRoleNull(p)) {
							if (getPlayerRoles().get(p).getClass().equals(know)) {
								String teamColor = getPlayerRoles().get(p).getOriginTeam().getColor();
								player.sendMessage(teamColor+p.getDisplayName()+"§7 possède le rôle: "+teamColor+getPlayerRoles().get(p).getName());
							}
						}
					}
				}
			}
			if (!role.getMessageOnDescription().isEmpty()) {
				for (String string : role.getMessageOnDescription()) {
					player.sendMessage(string);
				}
			}
		}
	}
	public Player getOwner(Roles role) {
		for (UUID u : getInGamePlayers()) {
			Player p = Bukkit.getPlayer(u);
			if (p == null)continue;
			if (!hasRoleNull(p)) {
				if (getPlayerRoles().get(p).getRoles() == role) {
					return p;
				}
			}
		}
		return null;
	}
	public String getRolesList() {
		Map<TeamList, List<Roles>> hashMap = new LinkedHashMap<>();
		StringBuilder tr = new StringBuilder();
		if (Main.isDebug()) {
			System.out.println("getRolesList used");
		}
		tr.append(AllDesc.bar);
		if (getServerState() == ServerStates.InGame) {
			for (RoleBase e : getPlayerRoles().values()) {
				if (e.getOriginTeam() == null){
					e.setTeam(e.getRoles().getTeam());
				}
				if (e.getOriginTeam() != null) {
					if (e.owner != null && !e.owner.getGameMode().equals(GameMode.SPECTATOR)) {
						if (hashMap.get(e.getOriginTeam()) == null){
							List<Roles> r = new ArrayList<>();
							hashMap.put(e.getOriginTeam(), r);
						}
						if (Main.isDebug()){
							System.out.println(e+" zzz "+e.getRoles().getItem().getItemMeta().getDisplayName()+" aaa "+e.getRoles());
						}
						List<Roles> aList = hashMap.get(e.getOriginTeam());
						aList.add(e.getRoles());
						hashMap.remove(e.getOriginTeam(), hashMap.get(e.getOriginTeam()));
						hashMap.put(e.getOriginTeam(), aList);
					}
				}
			}
		} else {
			if (!getAvailableRoles().isEmpty()){
				for (Roles e : getAvailableRoles().keySet()) {
					if (getAvailableRoles().get(e) > 0){
						if (hashMap.get(e.getTeam()) == null){
							List<Roles> r = new ArrayList<>();
							hashMap.put(e.getTeam(), r);
						}
						List<Roles> aList = hashMap.get(e.getTeam());
						aList.add(e);
						hashMap.remove(e.getTeam(), hashMap.get(e.getTeam()));
						hashMap.put(e.getTeam(), aList);
					}
				}
			}
		}
		if (!hashMap.isEmpty()){
			List<Roles> appenned = new ArrayList<>();
			for (TeamList t : TeamList.values()){
				int size = 0;
				if (hashMap.get(t) != null){
					size = hashMap.get(t).size();
				}
				if (size != 0) {
					tr.append("\n§r(").append(t.getColor()).append(size).append("§f)").append(t.getColor()).append(StringUtils.replaceUnderscoreWithSpace(t.name())).append("(s): \n");
					int i = 0;
					if (hashMap.containsKey(t) && !hashMap.get(t).isEmpty()) {
						for (Roles roles : hashMap.get(t)){
							i++;
							if (!appenned.contains(roles)) {
								if (getServerState().equals(ServerStates.InLobby)) {
									if (i != hashMap.get(t).size()){
										tr.append(t.getColor()).append(roles.getItem().getItemMeta().getDisplayName()).append(getAvailableRoles().get(roles) > 1 ? " §7(x§c"+getAvailableRoles().get(roles)+"§7)" : "").append("§f, ");
									} else {
										tr.append(t.getColor()).append(roles.getItem().getItemMeta().getDisplayName()).append(getAvailableRoles().get(roles) > 1 ? "§7(x§c"+getAvailableRoles().get(roles)+"§7)" : "").append("\n");
									}
									appenned.add(roles);
								} else {
									if (i != hashMap.get(t).size()){
										tr.append(t.getColor()).append(roles.getItem().getItemMeta().getDisplayName()).append("§f, ");
									} else {
										tr.append(t.getColor()).append(roles.getItem().getItemMeta().getDisplayName()).append("\n");
									}
								}

							}
						}
					}
				}
			}
		}
		tr.append("\n");
		tr.append("\n").append(AllDesc.bar);
		return tr.toString();
	}
	@Setter
	@Getter
	private Player JubiCrafter;
	@Getter
	@Setter
	private boolean TNTGrief = false;
	@Getter
	@Setter
	private Hokage hokage;
}