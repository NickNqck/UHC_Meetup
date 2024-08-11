package fr.nicknqck.roles.builder;

import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.TripleMap;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class AutomaticDesc {
    private static final String[] ROMAN_NUMERALS = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    private final TextComponent text;
    private final IRole role;
    public AutomaticDesc(IRole role) {
        this.role = role;
        text = new TextComponent(AllDesc.bar);
        addRoleName();
        addObjectif();
    }
    private void addRoleName() {
        text.addExtra(new TextComponent("\n§7Role: "+role.getTeam().getColor()+role.getName()));
    }
    private void addObjectif() {
        text.addExtra(new TextComponent("\n§7Votre objectif est de gagner avec le camp: "+role.getTeam().getColor()+role.getTeam().name()));
    }
    public AutomaticDesc addEffect(PotionEffect potionEffect, EffectWhen when) {
        text.addExtra(new TextComponent("\n\n"+AllDesc.point+"§7Vous possédez l'effet§c "+getPotionEffectNameWithRomanLevel(potionEffect)+"§7 "+getWhenString(when)));
        return this;
    }
    public AutomaticDesc addCustomWhenEffect(PotionEffect potionEffect, String when) {
        text.addExtra(new TextComponent("\n\n"+AllDesc.point+"§7Vous possédez l'effet§c "+getPotionEffectNameWithRomanLevel(potionEffect)+"§7 "+when));
        return this;
    }
    public AutomaticDesc addEffects(Map<PotionEffect, EffectWhen> map) {
        for (PotionEffect effect : map.keySet()) {
            EffectWhen when = map.get(effect);
            text.addExtra(new TextComponent("\n\n"+AllDesc.point+"§7Vous possédez l'effet§c "));
            text.addExtra("§c"+getPotionEffectNameWithRomanLevel(effect)+"§7 ");
            text.addExtra("§7"+getWhenString(when));
        }
        return this;
    }
    public AutomaticDesc addItem(TextComponent textComponent, int cooldown) {
        text.addExtra(new TextComponent("\n\n"+AllDesc.point+"§7Vous possédez l'item "));
        text.addExtra(textComponent);
        text.addExtra(new TextComponent("§7"+(cooldown > 0 ? " (1x/"+StringUtils.secondsTowardsBeautiful(cooldown)+")" : "" )+"."));
        return this;
    }
    @SafeVarargs
    public final AutomaticDesc setItems(TripleMap<HoverEvent, String, Integer>... tripleMaps) {
        for (TripleMap<HoverEvent, String, Integer> tripleMap : tripleMaps) {
            TextComponent interogativDot = new TextComponent(tripleMap.getSecond());
            interogativDot.setHoverEvent(tripleMap.getFirst());
            text.addExtra("\n\n"+AllDesc.point+"§7Vous possédez l'item \"");
            text.addExtra(interogativDot);
            text.addExtra("§7\" ");
            text.addExtra(new TextComponent("§7"+(tripleMap.getThird() > 0 ? " (1x/"+StringUtils.secondsTowardsBeautiful(tripleMap.getThird())+")" : "" )+"."));
        }
        return this;
    }
    public AutomaticDesc setCommands(Map<HoverEvent, Map<String, Integer>> hoverAndCooldown) {
        text.addExtra("\n\n"+"§7 - Commandes: \n\n");
        for (HoverEvent hoverEvent : hoverAndCooldown.keySet()) {
            for (String string : hoverAndCooldown.get(hoverEvent).keySet()) {
                int cooldown = hoverAndCooldown.get(hoverEvent).get(string);
                TextComponent interogativDot = new TextComponent("§b[?]");
                interogativDot.setHoverEvent(hoverEvent);
                text.addExtra("§7 "+AllDesc.point+" ");
                text.addExtra(interogativDot);
                text.addExtra("§7 "+string);
                text.addExtra(new TextComponent("§7 (1x/"+(cooldown != -500 ? StringUtils.secondsTowardsBeautiful(cooldown) : "partie" )+")."));
            }
        }
        return this;
    }
    @SafeVarargs
    public final AutomaticDesc setCommands(TripleMap<HoverEvent, String, Integer>... hoverAndCooldown) {
        text.addExtra("\n\n" + "§7 - Commandes: ");
        for (TripleMap<HoverEvent, String, Integer> tripleMap : hoverAndCooldown) {
            TextComponent interogativDot = new TextComponent("§b[?]");
            interogativDot.setHoverEvent(tripleMap.getFirst());
            text.addExtra("\n\n§7 " + AllDesc.point + " ");
            text.addExtra(interogativDot);
            text.addExtra("§7 " + tripleMap.getSecond());
            text.addExtra(new TextComponent("§7 (1x/" + (tripleMap.getThird() != -500 ? StringUtils.secondsTowardsBeautiful(tripleMap.getThird()) : "partie") + ")."));
        }
        return this;
    }
    public AutomaticDesc addParticularites(HoverEvent... hoverEvents) {
        text.addExtra("\n\n"+AllDesc.point+"§7Voud possédez §c"+hoverEvents.length+"§7 particularités: ");
        int i = 1;
        for (HoverEvent hover : hoverEvents) {
            TextComponent toAdd = new TextComponent("§b["+i+"]");
            toAdd.setHoverEvent(hover);
            i++;
            text.addExtra(toAdd);
            text.addExtra("§7, ");
        }
        return this;
    }
    public AutomaticDesc setParticularites(Map<HoverEvent, String> hoverEvents) {
        text.addExtra("\n\n"+"§7 - Particularitées: ");
        for (HoverEvent hover : hoverEvents.keySet()) {
            text.addExtra("\n\n "+AllDesc.point);
            TextComponent toAdd = new TextComponent("§b[?]");
            toAdd.setHoverEvent(hover);
            text.addExtra(toAdd);
            text.addExtra("§7, "+hoverEvents.get(hover));
        }
        return this;
    }
    public TextComponent getText(){
        text.addExtra(new TextComponent("\n\n"+AllDesc.bar));
        return text;
    }
    private String getWhenString(EffectWhen when) {
        return (when.equals(EffectWhen.PERMANENT) ? "de manière§c permanente" : when.equals(EffectWhen.DAY) ? "le§c jour" : when.equals(EffectWhen.NIGHT) ? "la§c nuit" : " en ayant moins de §c5"+AllDesc.coeur);
    }
    private String getPotionEffectNameWithRomanLevel(PotionEffect potionEffect) {
        if (potionEffect == null || potionEffect.getType() == null) {
            return "";
        }
        PotionEffectType type = potionEffect.getType();
        int amplifier = potionEffect.getAmplifier();
        String effectName = capitalizeFirstLetter(type.getName().toLowerCase().replace('_', ' '));
        String romanLevel = getRomanNumeral(amplifier + 1);
        if (type.equals(PotionEffectType.INCREASE_DAMAGE)) {
            effectName = "Force";
        } else if (type.equals(PotionEffectType.DAMAGE_RESISTANCE)) {
            effectName = "Resistance";
        } else if (type.equals(PotionEffectType.FAST_DIGGING)) {
            effectName = "Haste";
        }
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