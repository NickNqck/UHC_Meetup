package fr.nicknqck.enums;

import fr.nicknqck.roles.builder.TeamList;
import fr.nicknqck.utils.itembuilder.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
    Nakime(TeamList.Demon, "ds", 4, new ItemBuilder(Material.MAGMA_CREAM).setName("§cNakime").toItemStack(), "§bNickNqck"),
    Hantengu(TeamList.Demon, "ds", 5, new ItemBuilder(Material.RABBIT_FOOT).setName("§cHantengu").toItemStack(), "§bNickNqck"),
    HantenguV2(TeamList.Demon, "ds", 6, new ItemBuilder(Material.NETHER_STAR).setName("§cHantengu§7 (§6V2§7)").toItemStack(), "§bNickNqck"),
    Gyokko(TeamList.Demon, "ds", 7, new ItemBuilder(Material.FLOWER_POT_ITEM).setName("§cGyokko").toItemStack(), "§bNickNqck"),
    Daki(TeamList.Demon, "ds", 8, new ItemBuilder(Material.IRON_FENCE).setName("§cDaki").toItemStack(), "§bNickNqck"),
    Gyutaro(TeamList.Demon, "ds", 9, new ItemBuilder(Material.DIAMOND_HOE).setName("§cGyutaro").toItemStack(), "§bNickNqck"),
    Kaigaku(TeamList.Demon, "ds", 10, new ItemBuilder(Material.YELLOW_FLOWER).setName("§cKaigaku").toItemStack(), "§bByC3RV0L3NT"),
    Enmu(TeamList.Demon, "ds", 11, new ItemBuilder(Material.EYE_OF_ENDER).setName("§cEnmu").toItemStack(), "§bNickNqck"),
    Rui(TeamList.Demon, "ds", 12, new ItemBuilder(Material.STRING).setName("§cRui").toItemStack(), "§bNickNqck"),
    Kyogai(TeamList.Demon, "ds", 13, new ItemBuilder(Material.DISPENSER).setName("§cKyogai").toItemStack(), "§bNickNqck"),
    Susamaru(TeamList.Demon, "ds", 14, new ItemBuilder(Material.BOW).setName("§cSusamaru").toItemStack(), "§bNickNqck"),
    Furuto(TeamList.Demon, "ds", 15, new ItemBuilder(Material.NETHER_BRICK).setName("§cFuruto").toItemStack(), "§bNickNqck"),
    Yahaba(TeamList.Demon, "ds", 16, new ItemBuilder(Material.COMPASS).setName("§cYahaba").toItemStack(), "§bNickNqck"),
    DemonMain(TeamList.Demon, "ds", 17, new ItemBuilder(Material.SKULL_ITEM).setName("§cDemon Main").setDurability(3).toItemStack(), "§bNickNqck"),
    Demon(TeamList.Demon, "ds", 18, new ItemBuilder(Material.NETHER_FENCE).setName("§cDemon Simple").toItemStack(), "§bNickNqck"),
    Kumo(TeamList.Demon, "ds", 19, new ItemBuilder(Material.WEB).setName("§cKumo").toItemStack(), "§bNickNqck"),
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
    KaigakuSlayer(TeamList.Slayer, "ds", 21, new ItemBuilder(Material.GLOWSTONE).setName("§aKaigaku").toItemStack(), "§bNickNqck\n\n§7Il n'a pas encore été transformé par§c Kokushibo§7."),
    //Mahr aot
    Reiner(TeamList.Mahr, "aot", 0, new ItemBuilder(Material.QUARTZ).setName("§9Reiner").toItemStack(), "§bNickNqck"),
    Pieck(TeamList.Mahr, "aot", 1, new ItemBuilder(Material.CHEST).setName("§9Pieck").toItemStack(), "§bNickNqck"),
    Bertolt(TeamList.Mahr, "aot", 2, new ItemBuilder(Material.MAGMA_CREAM).setName("§9Bertolt").toItemStack(), "§bNickNqck"),
    Porco(TeamList.Mahr, "aot", 3, new ItemBuilder(Material.SLIME_BALL).setName("§9Porco").toItemStack(), "§bNickNqck"),
    Magath(TeamList.Mahr, "aot", 4, new ItemBuilder(Material.COMPASS).setName("§9Magath").toItemStack(), "§bNickNqck"),
    Lara(TeamList.Mahr, "aot", 5, new ItemBuilder(Material.IRON_BLOCK).setName("§9Lara").toItemStack(), "§bNickNqck"),
    //Titans aot
    Sieg(TeamList.Titan, "aot", 2, new ItemBuilder(Material.MOB_SPAWNER).setName("§cTitan Bestial").toItemStack(), "§bNickNqck"),
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
    Shisui(TeamList.Solo, "ns", 2, new ItemBuilder(Material.EYE_OF_ENDER).setName("§eShisui").toItemStack(), "§bNickNqck"),
    //Orochimaru ns
    Orochimaru(TeamList.Orochimaru, "ns", 0, new ItemBuilder(Material.NETHER_STAR).setName("§5Orochimaru").toItemStack(), "§bNickNqck"),
    Kabuto(TeamList.Orochimaru, "ns", 1, new ItemBuilder(Material.WATER_LILY).setName("§5Kabuto").toItemStack(), "§bNickNqck"),
    Karin(TeamList.Orochimaru, "ns", 2, new ItemBuilder(Material.BOOK).setName("§5Karin").toItemStack(), "§bNickNqck"),
    Kimimaro(TeamList.Orochimaru, "ns", 3, new ItemBuilder(Material.BONE).setName("§5Kimimaro").toItemStack(), "§bNickNqck"),
    Suigetsu(TeamList.Orochimaru, "ns", 4, new ItemBuilder(Material.WATER_BUCKET).setName("§5Suigetsu").toItemStack(), "§bNickNqck"),
    Sasuke(TeamList.Orochimaru, "ns", 5, new ItemBuilder(Material.EYE_OF_ENDER).setName("§5Sasuke").toItemStack(), "§aYukan"),
    Jugo(TeamList.Orochimaru, "ns", 6, new ItemBuilder(Material.ROTTEN_FLESH).setName("§5Jugo").toItemStack(), "§bNickNqck"),
    Tayuya(TeamList.Orochimaru, "ns", 7, new ItemBuilder(Material.STICK).setName("§5Tayuya").toItemStack(), "§bNickNqck"),
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
    Hinata(TeamList.Shinobi, "ns", 17, new ItemBuilder(Material.SNOW_BALL).setName("§aHinata").toItemStack(), "§bNickNqck"),
    Neji(TeamList.Shinobi, "ns", 18, new ItemBuilder(Material.SNOW_BLOCK).setName("§aNeji").toItemStack(), "§bNickNqck"),
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
    LeJuge(TeamList.Solo, "custom", 1, new ItemBuilder(Material.DIAMOND_SWORD).setName("§eLe Juge").toItemStack(), "§bNickNqck"),
    Heldige(TeamList.Solo, "custom", 2, new ItemBuilder(Material.ENDER_PEARL).setName("§eHeldige").toItemStack(), "§bNickNqck§7 &§b Mega02600")
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