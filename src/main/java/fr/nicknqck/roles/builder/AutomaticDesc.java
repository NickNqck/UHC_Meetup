package fr.nicknqck.roles.builder;

import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.StringUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class AutomaticDesc {
    private static final String[] ROMAN_NUMERALS = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    private final Map<UUID, TextComponent> getDescs = new HashMap<>();
    private final UUID user;
    private final Role role;
    public AutomaticDesc(UUID user, Role role) {
        this.user = user;
        this.role = role;
        getDescs.put(user, new TextComponent(AllDesc.bar));
        addRoleName();
        addObjectif();
    }
    public void addRoleName() {
        getDescs.get(this.user).addExtra("\n§7Role: "+role.getTeam().getColor()+role.getName());
    }
    public void addObjectif() {
        getDescs.get(this.user).addExtra("\n§7Votre objectif est de gagner avec le camp: "+role.getTeam().getColor()+role.getTeam().name());
    }
    public AutomaticDesc addEffects(Map<PotionEffect, EffectWhen> map) {
        for (PotionEffect effect : map.keySet()) {
            EffectWhen when = map.get(effect);
            getDescs.get(this.user).addExtra("\n\n§7Vous possédez l'effet§c "+getPotionEffectNameWithRomanLevel(effect)+"§7 "+(when.equals(EffectWhen.PERMANENT) ? "de manière§c permanente" : when.equals(EffectWhen.DAY) ? "le§c jour" : "la§c nuit"));
        }
        return this;
    }
    public AutomaticDesc addItem(TextComponent text, int cooldown) {
        getDescs.get(this.user).addExtra("\n\n§7Vous possédez l'item ");
        getDescs.get(this.user).addExtra(text);
        getDescs.get(this.user).addExtra("§7"+(cooldown > 0 ? "(1x/"+StringUtils.secondsTowardsBeautiful(cooldown)+")" : "" )+".");
        return this;
    }
    public AutomaticDesc addCommand(TextComponent text, int cooldown) {
        getDescs.get(this.user).addExtra("\n\n§7Vous avez accès à la commande: ");
        getDescs.get(this.user).addExtra(text);
        getDescs.get(this.user).addExtra("§7"+(cooldown > 0 ? "(1x/"+StringUtils.secondsTowardsBeautiful(cooldown)+")" : "" )+".");
        return this;
    }
    public void addCommands(Map<TextComponent, Integer> textAndCooldown) {
        getDescs.get(user).addExtra("\n\n§7Vous avez accès aux commande: ");
        Iterator<TextComponent> iterator = textAndCooldown.keySet().iterator();
        while (iterator.hasNext()) {
            if (textAndCooldown.isEmpty())return;
            TextComponent text = iterator.next();
            getDescs.get(user).addExtra(text);
            iterator.remove();

            StringBuilder suffix = new StringBuilder("§7");
            if (textAndCooldown.get(text) != null && textAndCooldown.get(text) > 0) {
                suffix.append("(1x/").append(textAndCooldown.get(text) == -500 ? StringUtils.secondsTowardsBeautiful(textAndCooldown.get(text)) : "partie").append(")");
            }
            suffix.append(iterator.hasNext() ? ", " : ".");

            getDescs.get(user).addExtra(suffix.toString());
        }
    }
    public Map<UUID, TextComponent> getFinalDesc() {
        this.getDescs.get(this.user).addExtra("\n\n"+AllDesc.bar);
        return this.getDescs;
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