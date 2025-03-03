package fr.nicknqck.events.custom.power;

import fr.nicknqck.utils.powers.ItemPower;

public class UpdateActionBarEvent extends ActionBarEvent{

    public UpdateActionBarEvent(String key, String value, ItemPower itemPower, boolean customText) {
        super(key, value, itemPower, customText);
    }
}