package fr.nicknqck.titans;

import lombok.Getter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public enum BestialForm {

    UNKNOW(999, "§fInconnue", "§cAucune description trouvé !"),
    SINGE(0, "§cSinge", "§7Votre transformation vous donne les effets§c Force I§7 et§c Résistance I§7 ainsi que§c 3❤ supplémentaires§7.\n",
            new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false),
            new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false)),
    TAUREAU(1, "§cTaureau", "§7Votre transformation vous donne l'effet§c Force I§7.\n"+
            " \n"+
            "§8 -§7 Lorsque vous êtes transformé vous pouvez utiliser votre item \"§cDash§7\", il vous permettra d'effectuer un grand bond en avant",
            new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false)),
    CROCODILE(2, "§bCrocodile", "§7Votre transformation vous donne les effets§c Résistance I§7 et§c Speed I§7.\n"+
            " \n"+
            "§8 -§7 Lorsque vous êtes transformé vous avez des bottes enchanté§c Depht Strider III§7.",
            new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 0, false, false),
            new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false)),
    OISEAU(3, "§aOiseau", "§7Votre transformation vous donne l'effet§c Speed I§7 et§a NoFall§7 ainsi que§c deux pouvoirs§7 nommé \"§fBattement d'aile§7\" et \"§fCharge en piqué§7\":\n"+
            " \n"+
            "§7     → \"§fBattement d'aile§7\": Vous permet de§a voler§7 pendant une durée de§c 10 secondes\n"+
            " \n"+
            "§7     → \"§fCharge en piqué§7\": Vous permet d'effectuer une charge en avant propulsant légèrement toute personne proche de la ou vous allez en plus de leurs infliger§c 1,5❤§7 de dégat",
            new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false)),
    OKAPI(4, "§cOkapi", "§7Votre transformation vous donne l'effet§c Force I§7 et§c Speed 1§7.\n"+
            " \n"+
            "§8 -§7 Lorsque vous êtes transformé vous pouvez utiliser votre item \"§fLangue§7\" pour pouvoir attirer le joueur visé à votre position",
            new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60, 0, false, false),
            new PotionEffect(PotionEffectType.SPEED, 60, 0, false, false))
    ;

    private final int random;
    private final String name;
    private final String descriptions;
    private final List<PotionEffect> potionEffects;

    BestialForm(int random, String name, String descriptions, PotionEffect... potionEffects) {
        this.random = random;
        this.name = name;
        this.descriptions = descriptions;
        if (potionEffects.length < 1) {
            this.potionEffects = Collections.emptyList();
        } else {
            this.potionEffects = new ArrayList<>();
            this.potionEffects.addAll(Arrays.asList(potionEffects));
        }
    }
}