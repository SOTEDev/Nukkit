package cn.nukkit.inventory;

import java.util.HashSet;
import java.util.Set;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BaseTransaction implements Transaction {

    protected final Inventory inventory;

    protected final int slot;

    protected Item targetItem;

    protected final long creationTime;

    protected int transactionType = Transaction.TYPE_NOMAL;

    protected int failures = 0;

    protected boolean wasSuccessful = false;

    public BaseTransaction(Inventory inventory, int slot, Item targetItem){
        this(inventory, slot, targetItem, Transaction.TYPE_NOMAL);
    }

    public BaseTransaction(Inventory inventory, int slot, Item targetItem, int transactionType) {
        this.inventory = inventory;
        this.slot = slot;
        this.targetItem = targetItem.clone();
        this.creationTime = System.currentTimeMillis();
        this.transactionType = transactionType;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public Item getTargetItem() {
        return targetItem.clone();
    }

    public void setTargetItem(Item item){
        this.targetItem = item.clone();
    }

    @Override
    public int getFailures(){
        return this.failures;
    }

    @Override
    public void addFailure(){
        this.failures++;
    }

    @Override
    public boolean succeeded(){
        return this.wasSuccessful;
    }

    @Override
    public void setSuccess(){
        setSuccess(true);
    }

    @Override
    public void setSuccess(boolean value){
        this.wasSuccessful = value;
    }

    public int getTransactionType(){
        return this.transactionType;
    }

    @Override
    public void sendSlotUpdate(Player source){
        if(this.getInventory() instanceof TemporaryInventory){
            return;
        }

        Set<Player> targets = new HashSet<>();
        if(this.wasSuccessful){
            targets = this.getInventory().getViewers();
            targets.remove(source);
        }else{
            targets.add(source);
        }
        this.inventory.sendSlot(this.slot, targets);
    }

    public Item[] getChange(){
        Item sourceItem = this.getInventory().getItem(this.slot);

        if(sourceItem.equals(this.targetItem)){
            Item item = sourceItem.clone();
            int countDiff = this.targetItem.getCount() - sourceItem.getCount();
            item.setCount(Math.abs(countDiff));

            if(countDiff < 0){
                return new Item[]{null, item};
            }else if(countDiff > 0){
                return new Item[]{item, null};
            }else{
                return null;
            }
        }else if(sourceItem.getId() != Item.AIR && this.targetItem.getId() == Item.AIR){
            return new Item[]{null, sourceItem.clone()};
        }else if(sourceItem.getId() == Item.AIR && this.targetItem.getId() != Item.AIR){
            return new Item[]{this.getTargetItem(), null};
        }else{
            return new Item[]{this.getTargetItem(), sourceItem.clone()};
        }
    }

    @Override
    public boolean execute(Player source){
        if(this.getInventory().processSlotChange(this)){
            if(!source.isCreative()){
                Item[] change = this.getChange();

                if(change == null){
                    return true;
                }

                if(change[1] instanceof Item){
                    if(!this.getInventory().slotContains(this.getSlot(), change[1])){
                        return false;
                    }
                }
                if(change[0] instanceof Item){
                    if(!source.getFloatingInventory().contains(change[0])){
                        return false;
                    }
                }

                if(change[1] instanceof Item){
                    source.getFloatingInventory().addItem(change[1]);
                }
                if(change[0] instanceof Item){
                    source.getFloatingInventory().removeItem(change[0]);
                }
            }
            this.getInventory().setItem(this.getSlot(), this.getTargetItem(), false);
        }

        return true;
    }
}
