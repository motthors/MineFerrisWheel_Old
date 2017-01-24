package mfw.gui.container;

import mfw.gui.slot.slotFileManager;
import mfw.item.itemBlockFerrisCore;
import mfw.tileEntity.TileEntityFileManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFileManager extends Container
{
    private TileEntityFileManager tile;
    
    public ContainerFileManager(InventoryPlayer invPlayer, TileEntityFileManager tile)
    {
        this.tile = tile;
        this.addSlotToContainer(new slotFileManager(tile, 0, 148, 57));

        for (int i = 0; i < 3; ++i)
        {
        	for (int j = 0; j < 9; ++j)
        	{
        		this.addSlotToContainer(new Slot(invPlayer, 9+j+i*9, 8+j*18, 84+i*18));
        	}
        }
        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
        }
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return this.tile.isUseableByPlayer(p_75145_1_);
    }

//    private boolean isItemCanSetSlot(ItemStack is)
//    {
//    	Item item = is.getItem();
//    	if(item==null)return false;
//    	if(item instanceof itemFerrisCore || item instanceof itemFerrisBasket)return true;
//    	return false;
//    }
    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
//    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
//    {
//    	ItemStack itemstack = null;
//    	//クリックされたスロットを取得
//        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);
//        if(slot == null)return null;
//        
//        if (slot.getHasStack()==false)return null;
//        
//        //クリックされたスロットのItemStackを取得
//        ItemStack itemstack1 = slot.getStack();
//        //書き換えるた後比較したいので変更前のItemStackの状態を保持しておく
//        itemstack = itemstack1.copy();
//        if(!isItemCanSetSlot(itemstack1))return null;
//        
//        if (p_82846_2_ == 0) // コンテナに保存されたスロットのIdxが0はGUIスロット　それ以外はプレイヤーインベントリ
//        {
//            if (!this.mergeItemStack(itemstack1, 1, this.inventorySlots.size(), true))
//            {
//                return null;
//            }
//            slot.onSlotChanged();
//        }
//        else
//        {
//        	if (!this.mergeItemStack(itemstack1, 0, 1, false))
//        	{
//        		return null;
//        	}
//        	slot.onSlotChanged();
//        }
//
//        if (itemstack1.stackSize == 0) //シフトクリックで移動先スロットが溢れなかった場合は移動元スロットを空にする
//        {
//            slot.putStack((ItemStack)null);
//        }
//        else //移動先スロットが溢れた場合は数だけ変わって元スロットにアイテムが残るので更新通知
//        {
//            slot.onSlotChanged();
//        }
//    
//        //シフトクリック前後で数が変わらなかった＝移動失敗
//  		if (itemstack.stackSize == itemstack1.stackSize) {
//  			return null;
//  		}
//  		
//  		slot.onPickupFromSlot(p_82846_1_, itemstack1);
//  		
//        return itemstack;
//    }
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if(!(itemstack1.getItem() instanceof itemBlockFerrisCore))return null;
            
            if (p_82846_2_ < 1)
            {
//                if (!this.mergeItemStack(itemstack1, 1, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, 1, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
            slot.onPickupFromSlot(p_82846_1_, itemstack1);
        }
        return itemstack;
    }
}