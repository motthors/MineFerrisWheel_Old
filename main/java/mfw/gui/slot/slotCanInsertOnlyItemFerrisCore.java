package mfw.gui.slot;

import mfw.item.itemBlockFerrisCore;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/*
 * Constructor—p
 */
public class slotCanInsertOnlyItemFerrisCore extends Slot{
	public slotCanInsertOnlyItemFerrisCore(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) {
		super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
	}
	public boolean isItemValid(ItemStack p_75214_1_)
	{
		return p_75214_1_ !=null && p_75214_1_.getItem() instanceof itemBlockFerrisCore;
	}
	public int getSlotStackLimit(){return 10;}
}

