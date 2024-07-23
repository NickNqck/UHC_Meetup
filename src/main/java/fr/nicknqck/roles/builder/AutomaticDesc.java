package fr.nicknqck.roles.builder;

import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.StringUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Iterator;
import java.util.Map;

public class AutomaticDesc {
    private static final String[] ROMAN_NUMERALS = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    private final TextComponent text;
    private final Role role;
    public AutomaticDesc(Role role) {
        this.role = role;
        text = new TextComponent(AllDesc.bar);
        addRoleName();
        addObjectif();
    }
    public void addRoleName() {
        text.addExtra(new TextComponent("\n§7Role: "+role.getTeam().getColor()+role.getName()));
    }
    public void addObjectif() {
        text.addExtra(new TextComponent("\n§7Votre objectif est de gagner avec le camp: "+role.getTeam().getColor()+role.getTeam().name()));
    }
    public AutomaticDesc addEffects(Map<PotionEffect, EffectWhen> map) {
        for (PotionEffect effect : map.keySet()) {
            EffectWhen when = map.get(effect);
            text.addExtra(new TextComponent("\n\n"+AllDesc.point+"§7Vous possédez l'effet§c "+getPotionEffectNameWithRomanLevel(effect)+"§7 "+(when.equals(EffectWhen.PERMANENT) ? "de manière§c permanente" : when.equals(EffectWhen.DAY) ? "le§c jour" : "la§c nuit")));
        }
        return this;
    }
    public AutomaticDesc addItem(TextComponent text, int cooldown) {
        text.addExtra(new TextComponent("\n\n"+AllDesc.point+"§7Vous possédez l'item "));
        text.addExtra(text);
        text.addExtra(new TextComponent("§7"+(cooldown > 0 ? "(1x/"+StringUtils.secondsTowardsBeautiful(cooldown)+")" : "" )+"."));
        return this;
    }
    public AutomaticDesc addCommand(TextComponent text, int cooldown) {
        text.addExtra(new TextComponent("\n\n"+AllDesc.point+"§7Vous avez accès à la commande: "));
        text.addExtra(new TextComponent(text));
        text.addExtra(new TextComponent("§7"+(cooldown > 0 ? "(1x/"+StringUtils.secondsTowardsBeautiful(cooldown)+")" : "" )+"."));
        return this;
    }
    public void addCommands(Map<TextComponent, Integer> textAndCooldown) {
        text.addExtra(new TextComponent("\n\n" + AllDesc.point + "§7Vous avez accès aux commandes: "));

        Iterator<Map.Entry<TextComponent, Integer>> iterator = textAndCooldown.entrySet().iterator();
        boolean first = true;
        while (iterator.hasNext()) {
            if (textAndCooldown.isEmpty()) return;

            Map.Entry<TextComponent, Integer> entry = iterator.next();
            TextComponent textComponent = entry.getKey();
            Integer cooldown = entry.getValue();

            if (!first) {
                text.addExtra(new TextComponent("\n"));
            } else {
                first = false;
                text.addExtra("\n");
            }
            text.addExtra("\n");
            text.addExtra(AllDesc.tab + " ");
            text.addExtra(new TextComponent(textComponent));

            StringBuilder suffix = new StringBuilder("§7");
            if (cooldown != null) {
                suffix.append(" §7(1x/");
                suffix.append(cooldown != -500 ? StringUtils.secondsTowardsBeautiful(cooldown) : "partie");
                suffix.append("§7)");
            }
            suffix.append(iterator.hasNext() ? ", " : ".");
            text.addExtra(new TextComponent(suffix.toString()));
        }
    }


    public TextComponent getText(){
        text.addExtra(new TextComponent("\n\n"+AllDesc.bar));
        return text;
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