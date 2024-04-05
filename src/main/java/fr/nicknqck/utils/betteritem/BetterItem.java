package fr.nicknqck.utils.betteritem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.inventory.ItemStack;

/**
 * Easily create clickable item, with custom event without creating error with listeners.
 *
 * @author Kuosai
 */
public class BetterItem {

    private ItemStack itemStack;
    private Predicate<BetterItemEvent> eventPredicate;
    private boolean droppable;
    private boolean despawnable;
    private boolean movableOther;
    private int ticksLived;
    private long nextUse = -1L;
    private int cooldown;
    private boolean leftClick = true;
    private boolean posable;

    public BetterItem(ItemStack itemStack, Predicate<BetterItemEvent> eventPredicate) {
        this.itemStack = itemStack;
        this.eventPredicate = eventPredicate;
        this.droppable = true;
        this.despawnable = true;
        this.ticksLived = 6000;
        this.movableOther = true;
        this.posable = true;
    }

    public BetterItem setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public BetterItem setEventPredicate(Predicate<BetterItemEvent> eventPredicate) {
        this.eventPredicate = eventPredicate;
        return this;
    }

    public Predicate<BetterItemEvent> getEventPredicate() {
        return eventPredicate;
    }

    public boolean isSame(ItemStack stack) {
        if(stack == null) return false;
        ItemStack clone = stack.clone();
        clone.setAmount(itemStack.getAmount());
        return clone.equals(itemStack);
    }

    public boolean isPosable() {
    	return this.posable;
    }
    public BetterItem setPosable(boolean e) {
    	this.posable = e;
    	return this;
    }
    
    public boolean isDroppable() {
        return this.droppable;
    }

    public BetterItem setDroppable(boolean b) {
        this.droppable = b;
        return this;
    }

    public BetterItem setLeftClick(boolean leftClick) {
        this.leftClick = leftClick;
        return this;
    }

    public boolean isLeftClick() {
        return leftClick;
    }

    public boolean isDespawnable() {
        return this.despawnable;
    }

    public BetterItem setDespawnable(boolean b) {
        this.despawnable = b;
        return this;
    }

    public int getTicksLived() {
        return ticksLived;
    }

    public BetterItem setTicksLived(int ticksLived) {
        this.ticksLived = ticksLived;
        return this;
    }

    public boolean isMovableOther() {
        return movableOther;
    }

    public BetterItem setMovableOther(boolean movableOther) {
        this.movableOther = movableOther;
        return this;
    }

    public BetterItem setCooldown(int time){
        this.cooldown = time;
        return this;
    }

    public void setNextUse() {
        this.nextUse = System.currentTimeMillis() + (cooldown * 1000L);
    }

    public long getNextUse() {
        return nextUse;
    }

    public boolean isUseable(){
        return nextUse <= System.currentTimeMillis();
    }

    public static final List<BetterItem> registeredItems = new ArrayList<>();

    public static BetterItem of(ItemStack stack, Predicate<BetterItemEvent> eventPredicate) {
        BetterItem item = new BetterItem(stack, eventPredicate);

        BetterItem betterItem = registeredItems.stream().filter(bi -> bi.isSame(stack)).findAny().orElse(null);
        if(betterItem == null){
            registeredItems.add(item);
            betterItem = item;
        }

        return betterItem.setEventPredicate(eventPredicate);
    }

    public static List<BetterItem> getRegisteredItems() {
        return registeredItems;
    }
}
