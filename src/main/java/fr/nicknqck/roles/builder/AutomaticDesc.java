package fr.nicknqck.roles.builder;

import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.StringUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AutomaticDesc {
    private static final String[] ROMAN_NUMERALS = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    private final List<TextComponent> Descs = new ArrayList<>();
    private final Role role;
    public AutomaticDesc(Role role) {
        this.role = role;
        Descs.add(new TextComponent(AllDesc.bar));
        addRoleName();
        addObjectif();
    }
    public void addRoleName() {
        Descs.get(0).addExtra(new TextComponent("\n§7Role: "+role.getTeam().getColor()+role.getName()));
    }
    public void addObjectif() {
        Descs.get(0).addExtra(new TextComponent("\n§7Votre objectif est de gagner avec le camp: "+role.getTeam().getColor()+role.getTeam().name()));
    }
    public AutomaticDesc addEffects(Map<PotionEffect, EffectWhen> map) {
        for (PotionEffect effect : map.keySet()) {
            EffectWhen when = map.get(effect);
            Descs.get(0).addExtra(new TextComponent("\n\n§7Vous possédez l'effet§c "+getPotionEffectNameWithRomanLevel(effect)+"§7 "+(when.equals(EffectWhen.PERMANENT) ? "de manière§c permanente" : when.equals(EffectWhen.DAY) ? "le§c jour" : "la§c nuit")));
        }
        return this;
    }
    public AutomaticDesc addItem(TextComponent text, int cooldown) {
        Descs.get(0).addExtra(new TextComponent("\n\n§7Vous possédez l'item "));
        Descs.get(0).addExtra(text);
        Descs.get(0).addExtra(new TextComponent("§7"+(cooldown > 0 ? "(1x/"+StringUtils.secondsTowardsBeautiful(cooldown)+")" : "" )+"."));
        return this;
    }
    public AutomaticDesc addCommand(TextComponent text, int cooldown) {
        Descs.get(0).addExtra(new TextComponent("\n\n§7Vous avez accès à la commande: "));
        Descs.get(0).addExtra(new TextComponent(text));
        Descs.get(0).addExtra(new TextComponent("§7"+(cooldown > 0 ? "(1x/"+StringUtils.secondsTowardsBeautiful(cooldown)+")" : "" )+"."));
        return this;
    }
    public void addCommands(Map<TextComponent, Integer> textAndCooldown) {
        Descs.get(0).addExtra(new TextComponent("\n\n§7Vous avez accès aux commandes "));
        Iterator<TextComponent> iterator = textAndCooldown.keySet().iterator();
        while (iterator.hasNext()) {
            if (textAndCooldown.isEmpty())return;
            TextComponent text = iterator.next();
            Descs.get(0).addExtra(new TextComponent(text));

            StringBuilder suffix = new StringBuilder("§7");
            if (textAndCooldown.get(text) != null) {
                suffix.append(" §7(1x/").append(textAndCooldown.get(text) != -500 ? StringUtils.secondsTowardsBeautiful(textAndCooldown.get(text)) : "§7p§7a§7r§7t§7i§7e").append("§7)");
            }
            suffix.append(iterator.hasNext() ? "§7, " : "§7.");

            Descs.get(0).addExtra(new TextComponent(suffix.toString()));
            iterator.remove();
        }
    }
    public List<TextComponent> getList(){
        Descs.get(0).addExtra(new TextComponent("\n\n"+AllDesc.bar));
        return Descs;
    }
    private String getPotionEffectNameWithRomanLevel(PotionEffect potionEffect) {
        if (potionEffect == null || potionEffect.getType() == null) {
            return "";
        }
        PotionEffectType type = potionEffect.getType();
        int amplifier = potionEffect.getAmplifier();
        String effectName = capitalizeFirstLetter(type.getName().toLowerCase().replace('_', ' '));
        String romanLevel = getRomanNumeral(amplifier + 1);
        return effectName + " " + romanLevel;
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private String getRomanNumeral(int number) {
        if (number <= 0 || number > ROMAN_NUMERALS.length) {
            return String.valueOf(number);
        }
        return ROMAN_NUMERALS[number - 1];
    }
}