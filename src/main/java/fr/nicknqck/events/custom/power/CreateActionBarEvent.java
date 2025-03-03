package fr.nicknqck.events.custom.power;

import fr.nicknqck.utils.powers.ItemPower;

public class CreateActionBarEvent extends ActionBarEvent{

    public CreateActionBarEvent(String key, String value, ItemPower itemPower, boolean customText) {
        super(key, value, itemPower, customText);
    }
}
