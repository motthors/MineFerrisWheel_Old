package mfw.gui.slot;

import mfw.item.itemBlockFerrisCore;
import mfw.item.itemFerrisBasket;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/*
 * FerrisCore用
 */
public class slotFerrisCore extends Slot{
	
//    private EntityPlayer thePlayer;
//    private int field_75228_b;

    public slotFerrisCore(IInventory iinventory, int num, int x, int y, EntityPlayer player)
    {
        super(iinventory, num, x, y);
//        this.thePlayer = player;
    }
    
    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack is)
    {
    	if(is==null)return false;
    	if(is.getItem() instanceof itemBlockFerrisCore || is.getItem() instanceof itemFerrisBasket)
		{
    		return is.hasTagCompound();
		}
    	return false;
    }
    
    public int getSlotStackLimit()
    {
    	return 1;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     * アイテムを取り出すとくる
     */
    public ItemStack decrStackSize(int p_75209_1_)
    {
    	if(!(inventory instanceof TileEntityFerrisWheel))return null;
    	TileEntityFerrisWheel tile = (TileEntityFerrisWheel)inventory;
    	if(tile.ArrayEntityParts[this.slotNumber]!=null)
    	{
    		if(tile.ArrayEntityParts[this.slotNumber].isTile())
    			((TileEntityFerrisWheel) tile.ArrayEntityParts[this.slotNumber]).dropChildParts();
    		tile.ArrayEntityParts[this.slotNumber].dead();
    		tile.ArrayEntityParts[this.slotNumber]=null;
    	}
        return super.decrStackSize(p_75209_1_);
    }

    public void onPickupFromSlot(EntityPlayer p_82870_1_, ItemStack p_82870_2_)
    {
//        this.onCrafting(p_82870_2_);
        super.onPickupFromSlot(p_82870_1_, p_82870_2_);
    }
    
}