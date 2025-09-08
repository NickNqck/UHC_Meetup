package fr.nicknqck.roles.builder;

import fr.nicknqck.player.GamePlayer;
import fr.nicknqck.roles.desc.AllDesc;
import fr.nicknqck.roles.ns.builders.NSRoles;
import fr.nicknqck.roles.ns.orochimaru.edov2.OrochimaruV2;
import fr.nicknqck.utils.StringUtils;
import fr.nicknqck.utils.TripleMap;
import fr.nicknqck.utils.powers.CommandPower;
import fr.nicknqck.utils.powers.Cooldown;
import fr.nicknqck.utils.powers.ItemPower;
import fr.nicknqck.utils.powers.Power;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutomaticDesc {

    private static final String[] ROMAN_NUMERALS = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    private final TextComponent text;
    private final IRole role;
    public AutomaticDesc(IRole role) {
        this.role = role;
        text = new TextComponent(AllDesc.bar);
        if (role == null)return;
        addRoleName();
        addObjectif();
    }
    private void addRoleName() {
        text.addExtra(new TextComponent("\n§7Role: "+role.getTeam().getColor()+role.getName()));
    }
    private void addObjectif() {
        final TeamList team = role.getTeam();
        text.addExtra(new TextComponent("\n§7Votre objectif est de gagner "+(team.equals(TeamList.Solo) ? "tout§e Seul" : "avec le camp: "+team.getColor()+team.name())));
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
        @NonNull final StringBuilder permaEffects = new StringBuilder();
        @NonNull final StringBuilder dayEffects = new StringBuilder();
        @NonNull final StringBuilder nightEffects = new StringBuilder();
        @NonNull final List<PotionEffect> permaEffectList = new ArrayList<>();
        @NonNull final List<PotionEffect> dayEffectList = new ArrayList<>();
        @NonNull final List<PotionEffect> nightEffectList = new ArrayList<>();
        for (@NonNull final PotionEffect potionEffect : map.keySet()) {
            if (map.get(potionEffect).equals(EffectWhen.PERMANENT)) {
                permaEffectList.add(potionEffect);
            } else if (map.get(potionEffect).equals(EffectWhen.DAY)) {
                dayEffectList.add(potionEffect);
            } else if (map.get(potionEffect).equals(EffectWhen.NIGHT)) {
                nightEffectList.add(potionEffect);
            }
        }
        if (!permaEffectList.isEmpty()) {
            for (@NonNull final PotionEffect potionEffect : permaEffectList) {
                permaEffects.append("§c").append(getPotionEffectNameWithRomanLevel(potionEffect));
                if (permaEffectList.size() > 1) {
                    permaEffects.append((permaEffectList.get(permaEffectList.size()-2).equals(potionEffect) ? " §7et §c" : permaEffectList.get(permaEffectList.size()-1).equals(potionEffect) ? "" : "§7, "));
                }
            }
            text.addExtra(new TextComponent("\n\n"+AllDesc.point+"§7Vous possédez les effets "+permaEffects+" §7de §7manière §cpermanente"));
        }
        if (!dayEffectList.isEmpty()) {
            for (@NonNull final PotionEffect potionEffect : dayEffectList) {
                dayEffects.append("§c").append(getPotionEffectNameWithRomanLevel(potionEffect));
                if (dayEffectList.size() > 1){
                    dayEffects.append((dayEffectList.get(dayEffectList.size()-2).equals(potionEffect) ? " §7et §c" : permaEffectList.get(permaEffectList.size()-1).equals(potionEffect) ? "" : "§7, "));
                }
            }
            text.addExtra(new TextComponent("\n\n"+AllDesc.point+"§7Vous possédez les effets "+dayEffects+" §7le §cjour"));
        }
        if (!nightEffectList.isEmpty()) {
            for (@NonNull final PotionEffect potionEffect : nightEffectList) {
                nightEffects.append("§c").append(getPotionEffectNameWithRomanLevel(potionEffect));
                if (dayEffectList.size() > 1){
                    nightEffects.append((nightEffectList.get(nightEffectList.size()-2).equals(potionEffect) ? " §7et §c" : permaEffectList.get(permaEffectList.size()-1).equals(potionEffect) ? "" : "§7, "));
                }
            }
            text.addExtra(new TextComponent("\n\n"+AllDesc.point+"§7Vous possédez les effets "+nightEffects+" §7la §cnuit"));
        }
        for (@NonNull final PotionEffect effect : map.keySet()) {
            EffectWhen when = map.get(effect);
            if (when.equals(EffectWhen.PERMANENT))continue;
            if (when.equals(EffectWhen.DAY))continue;
            if (when.equals(EffectWhen.NIGHT))continue;
            text.addExtra(new TextComponent("\n\n"+AllDesc.point+"§7Vous possédez l'effet§c "));
            text.addExtra("§c"+getPotionEffectNameWithRomanLevel(effect)+"§7 ");
            text.addExtra(when.equals(EffectWhen.AT_KILL) ? "§7pendant §c"+StringUtils.secondsTowardsBeautiful(effect.getDuration()/20)+" §7" : "");
            text.addExtra("§7"+getWhenString(when));
        }
        return this;
    }
    public AutomaticDesc addCustomLine(String line) {
        if (line.isEmpty())return this;
        text.addExtra(new TextComponent("\n\n"+AllDesc.point+line));
        return this;
    }
    public AutomaticDesc addCustomLines(String[] lines) {
        if (lines.length < 1)return this;
        int i = 0;
        for (final String string : lines) {
            if (i == 0){
                text.addExtra(new TextComponent("\n\n"+AllDesc.point));
            }
            text.addExtra(new TextComponent(string));
            text.addExtra(new TextComponent("\n"));
            i++;
        }
        return this;
    }
    public AutomaticDesc addCustomText(TextComponent text) {
        this.text.addExtra(new TextComponent("\n\n"+AllDesc.point));
        this.text.addExtra(text);
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
            switch (tripleMap.getThird()) {
                case -500:
                    text.addExtra("§7 (1x/partie).");
                    break;
                case 0:
                    text.addExtra("§7.");
                    break;
                default:
                    text.addExtra("§7 (1x/"+StringUtils.secondsTowardsBeautiful(tripleMap.getThird())+").");
                    break;
            }
        }
        return this;
    }
    public final AutomaticDesc setPowers(List<Power> powers) {
        for (final Power power : powers) {
            if (power.getName() == null)continue;
            if (!power.isShowInDesc())continue;
            String name = power.getName();
            if (power instanceof ItemPower) {
                if (((ItemPower) power).getItem().getItemMeta().hasDisplayName()){
                    name = ((ItemPower) power).getItem().getItemMeta().getDisplayName();
                } else {
                    name = "§cLe nom n'a pas été définie";
                }
            }
            String[] description = power.getDescriptions();
            Cooldown cooldown = power.getCooldown();
            TextComponent textComponent = new TextComponent("\n\n"+AllDesc.point+"§7Vous possédez l"+(power instanceof ItemPower ? "'item" : power instanceof CommandPower ? "a commande" : "e pouvoir")+" \"");
            TextComponent powerName = getPowerName(name, description);
            textComponent.addExtra(powerName);
            textComponent.addExtra("§7\"");
            if (power.getUse() == power.getMaxUse()) {
                textComponent.addExtra("§7 (§cInutilisable§7).");
                this.text.addExtra(textComponent);
                continue;
            }
            if (power.isShowCdInDesc()) {
                if (cooldown != null) {
                    if (cooldown.getOriginalCooldown() == -500) {
                        textComponent.addExtra("§7 (1x/partie)");
                    } else {
                        textComponent.addExtra("§7 (1x/" + StringUtils.secondsTowardsBeautiful(cooldown.getOriginalCooldown()) + ")");
                    }
                }
                if (power.getMaxUse() != -1) {
                    textComponent.addExtra("§7 ("+(power.getMaxUse()-power.getUse())+"x/partie)");
                }
            }
            textComponent.addExtra("§7.");
            this.text.addExtra(textComponent);
        }
        return this;
    }

    private TextComponent getPowerName(String name, String[] description) {
        TextComponent powerName = new TextComponent(name);
        if (description != null && description.length > 0) {
            StringBuilder d = new StringBuilder();
            int lines = 0;
            for (String string : description) {
                lines++;
                if (lines != 1) {
                    d.append("\n");
                }
                d.append(string);
            }
            BaseComponent[] hoverText = new BaseComponent[]{new TextComponent(d.toString())};
            powerName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        } else {
            powerName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§cAucune description trouver !")}));
        }
        return powerName;
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
    public TextComponent getText(){
        if (this.role instanceof RoleBase) {
            if (!((RoleBase) this.role).getGamePlayer().getChatWithManager().isEmpty()) {
                for (@NonNull final GamePlayer.ChatWithManager chatWithManager : ((RoleBase) this.role).getGamePlayer().getChatWithManager()) {
                    if (chatWithManager.isShowInDesc()){
                        this.text.addExtra(new TextComponent("\n\n"+AllDesc.point+"§7Vous §7possédez §7un §7chat §7en §7commun §7avec "+chatWithManager.findGoodNameRoles()+" §7pour §7ce §7faire §7il §7vous §7faudra §7écrire §7un §7message §7en §7commençant §7par §7\"§c"+chatWithManager.getConstructor()+"§7\"."));
                    }
                }
            }
        }
        text.addExtra(new TextComponent(
                this.role instanceof NSRoles ?
                        "\n\n"+AllDesc.point+"§7Votre nature de chakra est: "+(((NSRoles) this.role).getChakras() == null ?
                                "§cInexistante" :
                                ((this.role instanceof OrochimaruV2) ?
                                        ((OrochimaruV2) this.role).getChakraString() :
                                        ((NSRoles) this.role).getChakras().getShowedName())) :
                        ""));
        text.addExtra(new TextComponent("\n\n"+AllDesc.bar));
        return text;
    }
    private String getWhenString(EffectWhen when) {
        return (when.equals(EffectWhen.PERMANENT) ?
                "de manière §cpermanente" :
                when.equals(EffectWhen.DAY) ? "le §cjour" :
                        when.equals(EffectWhen.NIGHT) ? "la §cnuit" :
                                when.equals(EffectWhen.MID_LIFE) ? "en ayant moins de §c5"+AllDesc.coeur :
                                        when.equals(EffectWhen.AT_KILL) ? "en tuant un §cjoueur§7" :
                                                "(EffectWhen hasn't been found)");
    }
    public static String getPotionEffectNameWithRomanLevel(PotionEffect potionEffect) {
        if (potionEffect == null || potionEffect.getType() == null) {
            return "";
        }
        PotionEffectType type = potionEffect.getType();
        int amplifier = potionEffect.getAmplifier();
        String effectName = capitalizeFirstLetter(type.getName().toLowerCase().replace('_', ' '));
        String romanLevel = getRomanNumeral(amplifier + 1);
        if (type.equals(PotionEffectType.INCREASE_DAMAGE)) {
            effectName = "§cForce";
        } else if (type.equals(PotionEffectType.DAMAGE_RESISTANCE)) {
            effectName = "§9Resistance";
        } else if (type.equals(PotionEffectType.FAST_DIGGING)) {
            effectName = "§6Haste";
        } else if (type.equals(PotionEffectType.SPEED)) {
            effectName = "§eSpeed";
        } else if (type.equals(PotionEffectType.FIRE_RESISTANCE)) {
            effectName = "§6Fire Résistance";
        }
        return effectName + " " + romanLevel;
    }
    private static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
    private static String getRomanNumeral(int number) {
        if (number <= 0 || number > ROMAN_NUMERALS.length) {
            return String.valueOf(number);
        }
        return ROMAN_NUMERALS[number - 1];
    }
    public static TextComponent createFullAutomaticDesc(final IRole iRole) {
        return new AutomaticDesc(iRole).addEffects(iRole.getEffects()).setPowers(iRole.getPowers()).getText();
    }
    public static AutomaticDesc createAutomaticDesc(final IRole iRole) {
        return new AutomaticDesc(iRole).addEffects(iRole.getEffects()).setPowers(iRole.getPowers());
    }
}