package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DropItemTransaction extends BaseTransaction {


    public static final int TRANSACTION_TYPE = Transaction.TYPE_DROP_ITEM;

    protected final Inventory inventory = null;

    protected final int slot = -1;

    protected Item sourceItem;

    public DropItemTransaction(Item droppedItem) {
        super(null, -1, droppedItem);
        this.targetItem = droppedItem;
    }

    public void setSourceItem(Item item){
        
    }

    @Override
    public Inventory getInventory(){
        return null;
    }

    @Override
    public int getSlot(){
        return -1;
    }

    @Override
    public void sendSlotUpdate(Player source){
        
    }

    public Item[] getChange(){
        return new Item[]{this.getTargetItem(), null};
    }

    @Override
    public boolean execute(Player source){
        Item droppedItem = this.getTargetItem();
        if(!source.isCreative()){
            if(!source.getFloatingInventory().contains(droppedItem)){
                return false;
            }
            source.getFloatingInventory().removeItem(droppedItem);
        }
        source.dropItem(droppedItem);
        return true;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
