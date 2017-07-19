package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface Transaction {

    public static final int TYPE_NOMAL = 0;
    public static final int TYPE_DROP_ITEM  = 1;

    Inventory getInventory();

    int getSlot();

    Item getTargetItem();

    long getCreationTime();

    boolean execute(Player player);

    void sendSlotUpdate(Player source);

	int getFailures();

	void addFailure();

	boolean succeeded();

	void setSuccess(boolean value);

	void setSuccess();
}
