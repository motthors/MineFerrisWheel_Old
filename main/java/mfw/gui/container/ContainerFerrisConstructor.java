package mfw.gui.container;

import org.apache.commons.lang3.StringUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw.gui.slot.slotCanInsertOnlyItemFerrisCore;
import mfw.item.itemBlockFerrisCore;
import mfw.tileEntity.TileEntityFerrisConstructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFerrisConstructor extends Container
{
    private TileEntityFerrisConstructor tileFerrisCore;
//    private int lastConstructTime;
    private String ItemName;
    
    public ContainerFerrisConstructor(InventoryPlayer invPlayer, TileEntityFerrisConstructor tile)
    {
        this.tileFerrisCore = tile;
        this.addSlotToContainer(new slotCanInsertOnlyItemFerrisCore(tile, 0, 257, 57));
//        this.addSlotToContainer(new Slot(p_i1812_2, 1, 56, 53));
//        this.addSlotToContainer(new SlotFurnace(p_i1812_1_.player, p_i1812_2_, 2, 116, 35));

        int i;
        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 117 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(invPlayer, i, 117 + i * 18, 142));
        }
    }

    public void addCraftingToCrafters(ICrafting p_75132_1_)
    {
    	//GUI開くたび１回だけ　クラにパケット送ってる
        super.addCraftingToCrafters(p_75132_1_);
//        p_75132_1_.sendProgressBarUpdate(this, 0, this.tileFerrisCore.progressState);
//        p_75132_1_.sendProgressBarUpdate(this, 1, this.tileFerrisCore.furnaceBurnTime);
//        p_75132_1_.sendProgressBarUpdate(this, 2, this.tileFerrisCore.currentItemBurnTime);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

//        for (int i = 0; i < this.crafters.size(); ++i)
//        {
//            ICrafting icrafting = (ICrafting)this.crafters.get(i);

//            if (this.lastConstructTime != this.tileFerrisCore.progressTime)
//            {
//                icrafting.sendProgressBarUpdate(this, 0, this.tileFerrisCore.progressTime);
//            }
//        }

//        this.lastConstructTime = this.tileFerrisCore.progressTime;
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int idx, int update)
    {
//        if (idx == 0)
//        {
//            this.tileFerrisCore.progressTime = update;
//        }
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return this.tileFerrisCore.isUseableByPlayer(p_75145_1_);
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
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
                if (!this.mergeItemStack(itemstack1, 1, this.inventorySlots.size(), true))
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
        }

        return itemstack;
    }
    
    /**
     * used by the Anvil GUI to update the Item Name being typed by the player
     */
    public void updateItemName(String str)
    {
        this.ItemName = str;

        if (this.getSlot(0).getHasStack())
        {
            ItemStack itemstack = this.getSlot(0).getStack();

            if (StringUtils.isBlank(str))
            {
                itemstack.func_135074_t();
            }
            else
            {
                itemstack.setStackDisplayName(this.ItemName);
            }
        }

//        this.updateRepairOutput();
    }
}