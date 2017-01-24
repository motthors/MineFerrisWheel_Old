package mfw.tileEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import mfw._core.MFW_Core;
import mfw.gui.GUIFileManager;
import mfw.item.itemBlockFerrisCore;
import mfw.item.itemFerrisBasket;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageFerrisMisc;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

public class TileEntityFileManager extends TileEntity implements ISidedInventory {

	private int flag = 0;
	private ItemStack slot = null;
	public String reName = "";
	
	public GUIFileManager gui = null;
	
	private void updateFileNameList(Item item)
	{
		flag = 0;
		if(item instanceof itemBlockFerrisCore)flag = 1;
		if(item instanceof itemFerrisBasket)flag = 2;
	}
	
	public void setFlag(int f)
	{
		flag = f;
	}
	
	private String FlagToFolderName(int flag)
	{
		switch(flag)
		{
		case 1 : //core wheel
			return "./MFWFiles/WheelFrame/"; 
		case 2 : //basket
			return "./MFWFiles/Basket/";
		default : return "./MFWFiles/";
		}
	}
	
	private int getFolderFlagFromFile(File f)
	{
		return f.getParent().matches(".+WheelFrame") ? 1 : 
			(f.getParent().matches(".+Basket") ? 2 : 0);
	}
	
	private ItemStack FlagToItemStack(int flag)
	{
		switch(flag)
		{
		case 1 : //core wheel
			return new ItemStack(MFW_Core.ferrisCore);
		case 2 : //basket
			return new ItemStack(MFW_Core.ItemFerrisBasket);
		default : return null;
		}
	}
	
	public void FileWrite()
	{
		if(slot==null)return;
		if(slot.hasTagCompound()==false)return;
		// ファイル生成
		NBTTagCompound tag = slot.getTagCompound();
		String outputFileName = tag.getString("MFWOName") + "_" + tag.getString("author") + ".mfwo";// + VersionToString();
		File outputFile = new File(FlagToFolderName(flag)+outputFileName);
		try 
		{
			CompressedStreamTools.write(tag, outputFile);
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void FileRead()
	{
		// ファイル選択ダイアログ表示
		JFileChooser filechooser = new JFileChooser();
		filechooser.setCurrentDirectory(new File(FlagToFolderName(flag)));
		filechooser.setFocusable(true);
		int selected = filechooser.showOpenDialog(null);
		if (selected != JFileChooser.APPROVE_OPTION) return;
		
//		NameList = new String[]{"ba_tera_mottytest.mfwo"};
//		if(idx < 0 || idx >= NameList.length)return;
		NBTTagCompound tag = null;
		try 
		{
//			File file = new File(FlagToFolderName(flag) + NameList[idx]);
			File target = filechooser.getSelectedFile();
			boolean errorflag = false;
			if(target == null)errorflag = true;
			if(errorflag)
			{
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
	       				StatCollector.translateToLocal("message.fileread.error")
	       				));
					return;
			}
			//ファイルのバスケットフレームとスロットのアイテムの整合性判定
			String parentname = target.getParent();
			if(slot != null && slot.getItem() instanceof itemBlockFerrisCore && parentname.matches(".*WheelFrame")==false)errorflag = true;
			if(slot != null && slot.getItem() instanceof itemFerrisBasket && parentname.matches(".*Basket")==false)errorflag = true;
			if(errorflag)
			{
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
	       				StatCollector.translateToLocal("message.fileread.error")
	       				));
					return;
			}
			tag = CompressedStreamTools.read(target);
			if(tag == null)
			{
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
       				StatCollector.translateToLocal("message.fileread.error")
       				));
				return;
			}
			byte[] bytearray = CompressedStreamTools.compress(tag);
			
			//分割送信
			int bytenum = bytearray.length;
			int divnum = bytenum / (20*1024) + 1;
			ByteArrayInputStream ips = new ByteArrayInputStream(bytearray); 
			for(int i=0; i<divnum; ++i)
			{
				byte[] divarray = new byte[(20*1024)];
				ips.read(divarray, 0, (20*1024));
				int idxdata =  i | (divnum << 16);
				MessageFerrisMisc packet = new MessageFerrisMisc(xCoord, yCoord, zCoord, 
						MessageFerrisMisc.GUIFileSendTagArray+getFolderFlagFromFile(filechooser.getSelectedFile()), idxdata, divarray);
			    MFW_PacketHandler.INSTANCE.sendToServer(packet);
			}
			
//				MessageFerrisMisc packet = new MessageFerrisMisc(xCoord,yCoord,zCoord,
//						MessageFerrisMisc.GUIFileSendTagArray, getFolderFlagFromFile(filechooser.getSelectedFile()), bytearray);
//			    MFW_PacketHandler.INSTANCE.sendToServer(packet);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	byte arrayDataIndex[][];
	public void FileRead_server(byte[] byteArray, int idxdata, EntityPlayer player, int itemflag)
	{
		if(byteArray==null)return;
		if(slot==null && player.capabilities.isCreativeMode)slot = FlagToItemStack(itemflag);
		if(slot==null)return;
		
		int divnum = idxdata >> 16;
    	if(arrayDataIndex == null || arrayDataIndex.length != divnum)arrayDataIndex = new byte[divnum][];
    	arrayDataIndex[idxdata & 0xFFFF] = byteArray;
    	
    	//揃ったかチェック
    	int i=0;
    	for( ; i<divnum; ++i)
    	{
    		if(arrayDataIndex[i] == null)break;
    	}
    	if(i != divnum)return;
    	
    	//揃ったらデータ結合
    	ByteArrayOutputStream allbytearray = new ByteArrayOutputStream();
    	for(i=0; i<divnum; ++i)
    	{
    		allbytearray.write(arrayDataIndex[i], 0, arrayDataIndex[i].length);
    	}
    	byteArray = allbytearray.toByteArray();
		
    	//データ解凍
		ByteArrayInputStream ips = new ByteArrayInputStream(byteArray); 
		NBTTagCompound tag = null;
		try {
			tag = CompressedStreamTools.readCompressed(ips);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(tag == null)return;
		slot.setTagCompound(tag);
		
		//後処理
    	arrayDataIndex = null;
	}
	
	public void ReNameItemStack(String newname)
	{
		reName = newname;
		ItemStack is = getStackInSlot(0);
		if(is == null)return;
		String orgname = is.getItem().getItemStackDisplayName(is);
		is.setStackDisplayName(orgname+" : "+reName);
		if(is != null && is.hasTagCompound())is.getTagCompound().setString("MFWOName", reName);
	}
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int p_70301_1_)
	{
		return slot;
	}

	// idx番のスロットのアイテムがnum個取り出された
	@Override
	public ItemStack decrStackSize(int idx, int num)
	{
		if(idx!=0)return null;
		 ItemStack itemstack;
		if (this.slot.stackSize <= num)
        {
            itemstack = slot;
            this.slot = null;
			reName = "";
	        if(gui!=null)gui.textField.setText("");
            return itemstack;
        }
        else
        {
            itemstack = this.slot.splitStack(num);

            if (this.slot.stackSize == 0)
            {
                this.slot = null;
            }

            return itemstack;
        }
	}

	// ブロック破壊時とかに呼ばれる
	@Override
	public ItemStack getStackInSlotOnClosing(int p_70304_1_) 
	{
		if (this.slot != null)
        {
            ItemStack itemstack = this.slot;
            this.slot = null;
            return itemstack;
        }
        else
        {
            return null;
        }
	}

	@Override
	public void setInventorySlotContents(int idx, ItemStack itemstack)
	{
		if(itemstack==null)return;
		if(!(itemstack.getItem() instanceof itemBlockFerrisCore) && !(itemstack.getItem() instanceof itemFerrisBasket))return;
        this.slot = itemstack;
        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
        {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
        
        // ファイル名一覧更新
        updateFileNameList(itemstack.getItem());
        if(itemstack.hasTagCompound()==false)return;
        if(itemstack.getTagCompound().hasKey("MFWOName"))
        	reName = itemstack.getTagCompound().getString("MFWOName");
        if(gui!=null)gui.textField.setText(reName);
	}

	@Override
	public String getInventoryName()
	{
		return "container.mfw.ferrisfilemanager";
	}

	@Override
	public boolean hasCustomInventoryName() 
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 10;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) 
	{
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
	{
		return p_94041_2_!=null;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) 
	{
		return new int[1];
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) 
	{
		return false;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) 
	{
		return false;
	}

}