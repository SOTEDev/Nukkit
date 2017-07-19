package cn.nukkit.inventory;

import java.util.Set;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface TransactionGroup {

    public static final int DEFAULT_ALLOWED_RETRIES = 5;

    Set<Inventory> getInventories();

    SimpleTransactionGroup.Queue getTransactions();

    int getTransactionCount();

    void addTransaction(Transaction transaction);

    void execute();
}
