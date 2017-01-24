package mfw.gui.container;

import java.util.List;

import mfw.gui.slot.slotFerrisCore;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFerrisCore extends Container{
	
    private TileEntityFerrisWheel tileFerrisCore;
    InventoryPlayer invPlayer;
    private int PageNum = 0;
//    private int lastConstructTime;
    
	public ContainerFerrisCore(InventoryPlayer invPlayer, TileEntityFerrisWheel tile)
	{
        this.tileFerrisCore = tile;
        this.invPlayer = invPlayer;
//        this.addSlotToContainer(new Slot(tile, 0, 148, 57));

		setContainerForPage();
		slideSlotPos();
	}
    
	public void setContainerForPage()
	{
//		this.inventorySlots.clear();
//        this.inventoryItemStacks.clear();
		int num = tileFerrisCore.getSizeInventory();
//		MFW_Logger.debugInfo("container inventory num"+num);
		for (int i = 0; i <= num/5; ++i)
		{
//			if(i >= 5)break;
			for (int j = 0; j < 5; ++j)
			{
				if(/*PageNum*25 + */j+i*5 >= num)break;
        		this.addSlotToContainer(new slotFerrisCore(tileFerrisCore, /*PageNum*25 +*/ j+i*5, 0,0/*8+j*18, 16+i*24*/, invPlayer.player));
			}
		}
        
		for (int i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(invPlayer, 9+j+i*9, 102+j*18, 84+i*18));
			}
		}
		for (int i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(invPlayer, i, 102 + i * 18, 142));
		}
	}

	public int getPageNum()
	{
		return PageNum;
	}
	public void changePage(int flag)
	{
		switch(flag)
		{
		case 0:PageNum--; break;
		case 1:++PageNum; break;
		}
		if(PageNum<0)PageNum=0;
		if(PageNum>tileFerrisCore.getSizeInventory()/25)PageNum = tileFerrisCore.getSizeInventory()/25;
//		MFW_Logger.debugInfo("changepage  pagenum:"+PageNum);
		slideSlotPos();
	}
	
	public void slideSlotPos()
	{
		@SuppressWarnings("unchecked")
		List<Slot> list = (List<Slot>)inventorySlots;
		int s = PageNum*25;
		int e = (PageNum+1)*25;
		int max = tileFerrisCore.getSizeInventory();
		for (Slot slot : list)
		{
			int idx = slot.getSlotIndex();
			if(idx >= max)continue;
			if(slot.inventory.getInventoryName().equals("container.erc.ferriccore")==false)continue;
			if( idx >= s && idx < e )
			{
				int i = (int) Math.floor((idx-s)/5f);
				int j =(idx-s) - i*5;
				slot.xDisplayPosition = 8+j*18;
				slot.yDisplayPosition = 16+i*24;
//				MFW_Logger.debugInfo("containercore : idx:"+idx+"   x.y:"+(8+j*18)+"."+(16+i*24));
			}
			else
			{
				slot.xDisplayPosition = -1000;
				slot.yDisplayPosition = -1000;
			}
		}
	}
	
    public void addCraftingToCrafters(ICrafting p_75132_1_)
    {
    	//GUI開くたび１回だけ　クラにパケット送ってる
        super.addCraftingToCrafters(p_75132_1_);
//        p_75132_1_.sendProgressBarUpdate(this, 0, this.tileFerrisCore.progressTime);
    }

    
    @Override
	public void onContainerClosed(EntityPlayer p_75134_1_) {
		// TODO Auto-generated method stub
		super.onContainerClosed(p_75134_1_);
//		 this.invPlayer.closeInventory();
	}

	/**
     * Looks for changes made in the container, sends them to every listener.
     */
//    public void detectAndSendChanges()
//    {
//        super.detectAndSendChanges();
//
//        for (int i = 0; i < this.crafters.size(); ++i)
//        {
//            ICrafting icrafting = (ICrafting)this.crafters.get(i);
//
//            if (this.lastConstructTime != this.tileFerrisCore.progressTime)
//            {
//                icrafting.sendProgressBarUpdate(this, 0, this.tileFerrisCore.progressTime);
//            }
//        }
//
//        this.lastConstructTime = this.tileFerrisCore.progressTime;
//    }

//    @SideOnly(Side.CLIENT)
//    public void updateProgressBar(int idx, int update)
//    {
//        if (idx == 0)
//        {
//            this.tileFerrisCore.progressTime = update;
//        }
//    }
//
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
//        return this.tileFerrisCore.isUseableByPlayer(p_75145_1_);
    	return true;
    }

//    /**
//     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
//     */
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
    {
        return null;
    }
    
}
    