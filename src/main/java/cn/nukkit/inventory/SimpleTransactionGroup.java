package cn.nukkit.inventory;

import java.util.HashSet;
import java.util.Set;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.inventory.InventoryTransactionEvent;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SimpleTransactionGroup implements TransactionGroup {

    protected Player player = null;

    protected SimpleTransactionGroup.Queue  transactionQueue = null;

    protected SimpleTransactionGroup.Queue  transactionsToRetry = null;

    protected final Set<Inventory> inventories = new HashSet<>();

    protected long lastUpdate = -1;

    protected int transactionCount = 0;

    public SimpleTransactionGroup() {
        this(null);
    }

    public SimpleTransactionGroup(Player player) {
        this.player = player;
        this.transactionQueue = new SimpleTransactionGroup.Queue();
        this.transactionsToRetry = new SimpleTransactionGroup.Queue();
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public Set<Inventory> getInventories() {
        return inventories;
    }

    @Override
    public SimpleTransactionGroup.Queue getTransactions() {
        return transactionQueue;
    }

    @Override
    public int getTransactionCount(){
        return this.transactionCount;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        this.inventories.add(transaction.getInventory());
        this.transactionQueue.enqueue(transaction);
        if(transaction.getInventory() instanceof Inventory){
            this.inventories.add(transaction.getInventory());
        }
        this.lastUpdate = System.currentTimeMillis();
        this.transactionCount += 1;
    }

    @Override
    public void execute() {
        Set<Transaction> failed = new HashSet<>();

        while(!this.transactionsToRetry.isEmpty()){
            this.transactionQueue.enqueue(this.transactionsToRetry.dequeue());
        }

        InventoryTransactionEvent ev = new InventoryTransactionEvent(this);
        if(!this.transactionQueue.isEmpty()){
            Server.getInstance().getPluginManager().callEvent(ev);
        }else{
            return;
        }
        while(!this.transactionQueue.isEmpty()){
            Transaction transaction = this.transactionQueue.dequeue();

            if(ev.isCancelled()){
                this.transactionCount -= 1;
                transaction.sendSlotUpdate(this.player);
                this.inventories.remove(transaction);
            }else if(!transaction.execute(this.player)){
                transaction.addFailure();
                if(transaction.getFailures() >= SimpleTransactionGroup.DEFAULT_ALLOWED_RETRIES){
                    this.transactionCount -= 1;
                    failed.add(transaction);
                }else{
                    this.transactionsToRetry.enqueue(transaction);
                }
                continue;
            }

            this.transactionCount -= 1;
            transaction.setSuccess();
            transaction.sendSlotUpdate(this.player);
            this.inventories.remove(transaction);
        }

        for(Transaction f : failed){
            f.sendSlotUpdate(this.player);
            this.inventories.remove(f);
        }
    }

    static class Queue {

        final int SIZE = 5;
        private Transaction[] values = new Transaction[SIZE+1];
        private int head = 0;
        private int tail = 0;

        boolean enqueue(Transaction data) {
            if (((tail + 1) % values.length) == head) {
                return false;
            }
            values[tail++] = data;
            tail = tail % values.length;
            return true;
        }

        Transaction dequeue() {
            Transaction data = null;
            if (tail != head) {
                data = values[head++];
                head = head % values.length;
            }
            return data;
        }

        boolean isEmpty() {
            return (tail == head);
        }
    }

}

