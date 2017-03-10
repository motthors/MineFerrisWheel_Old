package mfw.tileEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Core;
import mfw._core.MFW_Logger;
import mfw.gui.GUIFerrisConstructor;
import mfw.item.itemBlockFerrisCore;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageFerrisMisc;
import mfw.renderer.renderFerrisLimitFrame;
import mfw.util.multiThreadBlockCopier;
import mfw.wrapper.IMultiThreadBlockCopy;
import mfw.wrapper.Wrap_TileEntityChangeLimitWithKey;
import mfw.wrapper.Wrap_TileEntityLimitLine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class TileEntityFerrisConstructor extends Wrap_TileEntityLimitLine 
	implements IInventory, Wrap_TileEntityChangeLimitWithKey, IMultiThreadBlockCopy{

	// LimitFrame
	private int LimitFrameLength;
	private int LimitFrameWidth;
	// 描画関連パラメタ
	renderFerrisLimitFrame limitFrame = new renderFerrisLimitFrame();
//	Vec3 posArray[] = new Vec3[16];
//	Vec3 vertexArray[] = new Vec3[38];
	public ResourceLocation RailTexture;
	// 組み立て
	public byte progressState = 0; // 0:no craft 1:isCrafting 2:Complete
//	public int progressTime = 0;
//	public int totalTime = 200;
	// その他パラメータ
	public int copyNum = 1;
	public int copymode = 0;
	
	public String AuthorName;
	// ホイールアイテムを入れるところ
	private ItemStack[] ItemStacks = new ItemStack[1];
	// GUIに表示する名前保存
	public String wheelName;
	public GUIFerrisConstructor gui;
	// 非同期ブロックコピー
	private multiThreadBlockCopier threadBlockCopy = null;
	private NBTTagCompound copiedNBTTag;
	
	public boolean FlagDrawCore = true;
	public boolean FlagDrawEntity = false;
	
	public TileEntityFerrisConstructor()
	{
		super();
		LimitFrameLength = 3;
		LimitFrameWidth = 1;
		RailTexture = new ResourceLocation("textures/blocks/iron_block.png");
		wheelName = "";
		AuthorName = "";
	}

	@Override
	public double getMaxRenderDistanceSquared() 
	{
		return 100000d;
	}

	//@Override
	public boolean myisInvalid() 
	{
		return super.isInvalid();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() 
	{
		return INFINITE_EXTENT_AABB;
	}
	
	public void setFrameLength(int l)
	{
		LimitFrameLength += l*2;
		if(LimitFrameLength > 32767)LimitFrameLength = 32767;
		if(LimitFrameLength < 3)LimitFrameLength = 3;
	}
	public void setFrameWidth(int w)
	{
		LimitFrameWidth += w*2;
		if(LimitFrameWidth > 32767)LimitFrameWidth = 32767;
		if(LimitFrameWidth < 1)LimitFrameWidth = 1;
	}
	
	public void setFrameX(int flag)
	{
		flag = convertflagtoLength(flag);
		setFrameLength(flag);
	}
	public void setFrameY(int flag)
	{
		flag = convertflagtoLength(flag);
		if(1 == (7&worldObj.getBlockMetadata(xCoord, yCoord, zCoord)))setFrameWidth(flag);
		else setFrameLength(flag);
	}
	public void setFrameZ(int flag)
	{
		flag = convertflagtoLength(flag);
		if(1 < (7&worldObj.getBlockMetadata(xCoord, yCoord, zCoord)))setFrameWidth(flag);
		else setFrameLength(flag);
	}
	public static int convertflagtoLength(int flag)
	{
		switch(flag)
		{
		case 0 : return -100;
		case 1 : return -10;
		case 2 : return -1;
		case 3 : return 1;
		case 4 : return 10;
		case 5 : return 100;
		}
		return 0;
	}
	
	public void setCopyMode(int flag)
	{
		copymode += flag;
		if(copymode > 1)copymode = 1;
		if(copymode < 0)copymode = 0;
	}
	public void rotateCopyMode()
	{
		copymode = (copymode+1)%2;
	}
	
	public void setCopyNum(int flag)
	{
		copyNum += flag;
		if(copyNum > 100)copyNum = 100;
		if(copyNum < 1)copyNum = 1;
	}
	public int getLimitFrameLength() {
		return LimitFrameLength;
	}

	public int getLimitFrameWidth() {
		return LimitFrameWidth;
	}
	
	public void toggleFlagDrawCore()
	{
		FlagDrawCore = !FlagDrawCore;
	}
	
	public void toggleFlagDrawEntity()
	{
		FlagDrawEntity = !FlagDrawEntity;
	}
	
	public void createVertex(World world)
	{
		double Length1 = ((double)LimitFrameLength) / 2d;
		double Length2 = ((double)LimitFrameWidth) / 2d;
		
		double x = Length1;
		double y = Length1;
		double z = Length1;
		switch(world.getBlockMetadata(xCoord, yCoord, zCoord))
		{
		case 0 : case 1 : y = Length2; break;
		case 2 : case 3 : z = Length2; break;
		case 4 : case 5 : x = Length2; break;
		}
		limitFrame.createVertex(this, world, -x, x, -y, y, -z, z);
	}
	
	public void render(Tessellator tess)
	{
		limitFrame.render(tess);
	}
	

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);      
    	loadFromNBT(par1NBTTagCompound, "");
    }
    public void loadFromNBT(NBTTagCompound nbt, String tag)
    {
    	LimitFrameLength = nbt.getInteger(tag+"framelen");
      	LimitFrameWidth = nbt.getInteger(tag+"framewid");
      	FlagDrawCore = nbt.getBoolean(tag+"flagdrawcore");
      	FlagDrawEntity = nbt.getBoolean(tag+"flagdrawentity");
      	wheelName = nbt.getString(tag+"wheelname");
      	copyNum = nbt.getInteger(tag+"copynum");
      	copymode = nbt.getInteger(tag+"copymode");
      	progressState = nbt.getByte(tag+"progressstate");
    }
    
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        saveToNBT(par1NBTTagCompound, "");
    }
    public void saveToNBT(NBTTagCompound nbt, String tag)
    {
    	nbt.setInteger(tag+"framelen", LimitFrameLength);
    	nbt.setInteger(tag+"framewid", LimitFrameWidth);
    	nbt.setBoolean(tag+"flagdrawcore", FlagDrawCore);
    	nbt.setBoolean(tag+"flagdrawentity", FlagDrawEntity);
    	nbt.setString(tag+"wheelname", wheelName);
    	nbt.setInteger(tag+"copynum", copyNum);
    	nbt.setInteger(tag+"copymode", copymode);
    	nbt.setByte(tag+"progressstate", progressState);
    }

	@Override
	public Packet getDescriptionPacket() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        this.writeToNBT(nbtTagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
	}
 
	@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
        // 制御データを受け取ったらVertex構築
        createVertex(worldObj);
    }
	
	//Createボタンを押しても大丈夫か判定　コアが入っている　or クリエイティブモードならTrue
	public boolean canCreateCore()
	{
		return isExistCore() || Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode;
	}
	//スロットにコアフレームが入っているかどうか
	public boolean isExistCore()
	{
		if(ItemStacks[0]==null)return false;
		return ItemStacks[0].getItem() instanceof itemBlockFerrisCore;
	}
	
	// GUIConstructorから呼ばれる　クリエイト開始点 sideonly Client
	@SideOnly(Side.CLIENT)
	public void startConstructWheel()
	{
		if(progressState == 0)
		{
			if(canCreateCore()==false)return;
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			progressState = 1;
			if(player!=null)AuthorName = player.getDisplayName();
			threadBlockCopy = new multiThreadBlockCopier(this, worldObj);
			int constructormeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			int lenx = (LimitFrameLength-1)/2;
	    	int leny = lenx;
	    	int lenz = lenx;
	    	switch(constructormeta)
			{
			case 0 : case 1 : leny = (LimitFrameWidth-1)/2; break;
			case 2 : case 3 : lenz = (LimitFrameWidth-1)/2; break;
			case 4 : case 5 : lenx = (LimitFrameWidth-1)/2; break;
			}
	    	threadBlockCopy.setDrawEntityFlag(FlagDrawEntity);
			threadBlockCopy.setSrcPosition(xCoord-lenx, yCoord-leny, zCoord-lenz, xCoord+lenx, yCoord+leny, zCoord+lenz);
			threadBlockCopy.start();
			this.markDirty();
		}
	}

	//進行度からテクスチャの表示率計算
	@SideOnly(Side.CLIENT)
    public int getCookProgressScaled(int scale)
    {
        return (int) (scale * threadBlockCopy.getProgress());
    }
	
	public boolean isCrafting()
	{
		return progressState == 1;
	}
	
	//常時呼ばれる　　クライアント側でコピー中なら中身実行
    public void updateEntity()
    {
    	if (!worldObj.isRemote) return; //鯖はなにもしない

        if (this.progressState != 0) //コピー中なら
        {
        	if (this.progressState == 2) //完成してたら
        	{
        		this.sendNBTToServer();
        		this.progressState = 0;
        		markDirty();
        	}
        }
    }

    //////////// IMultiThreadBlockCopy ///////////
    public void Completing(NBTTagCompound nbt)
    {
    	copiedNBTTag = nbt;
    	progressState = 2;
    	threadBlockCopy = null;
    }
    
    public void abnormalEnd()
    {
    	progressState = 0;
    	threadBlockCopy = null;
    	MFW_Logger.warn("Coping blocks is abnormal end.");
    }
    //////////// IMultiThreadBlockCopy end ///////////    
    // 完成したアイテムをスロットにセット
    public void sendNBTToServer()
    {
//    	if(ItemStacks[0] == null)return;
//    	if(!(ItemStacks[0].getItem() instanceof itemFerrisCore))return;
//
    	int constructormeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
    	int lenx = (LimitFrameLength-1)/2;
    	int leny = lenx;
    	int lenz = lenx;
    	switch(constructormeta)
		{
		case 0 : case 1 : leny = (LimitFrameWidth-1)/2; break;
		case 2 : case 3 : lenz = (LimitFrameWidth-1)/2; break;
		case 4 : case 5 : lenx = (LimitFrameWidth-1)/2; break;
		}

    	NBTTagCompound nbt = copiedNBTTag;
//    	BlocksCompressor copier = new BlocksCompressor();
//    	copier.setpositionとかいろいろ設定
//    	copier.setSrcPosition(xCoord-lenx, yCoord-leny, zCoord-lenz, xCoord+lenx, yCoord+leny, zCoord+lenz);
//    	copier.makeTag(nbt, worldObj);
//    	if(FlagDrawEntity)copier.SearchEntities(nbt, worldObj);
    	nbt.setString("MFWOName", this.gui.getName().equals("")?"-NoName-":this.gui.getName());
    	nbt.setString("author", AuthorName);
    	nbt.setByte("constructormetaflag", (byte)( constructormeta | (FlagDrawCore?8:0) ));
    	nbt.setInteger("basefornbtx", xCoord); // CTM用基準座標
		nbt.setInteger("basefornbty", yCoord);
		nbt.setInteger("basefornbtz", zCoord);
		nbt.setInteger("connectoffsetx", lenx); // 中心位置（工作台の相対距離）
		nbt.setInteger("connectoffsety", leny);
		nbt.setInteger("connectoffsetz", lenz);
		nbt.setInteger("copynum", copyNum);
		nbt.setInteger("copymode", copymode);
//        ItemStacks[0].setStackDisplayName(this.wheelName.matches("")?"-NoName-":this.wheelName);
//        ItemStacks[0].setTagCompound(nbt);
//        
//        String orgname = ItemStacks[0].getItem().getItemStackDisplayName(ItemStacks[0]);
//        ItemStacks[0].setStackDisplayName(orgname+" : "+nbt.getString("MFWOName"));
		byte[] bytearray = null;
		try { bytearray = CompressedStreamTools.compress(nbt); }catch (IOException e) {e.printStackTrace(); return;}
		
		//分割送信
		int bytenum = bytearray.length;
		int divnum = bytenum / (20*1024) + 1;
		ByteArrayInputStream ips = new ByteArrayInputStream(bytearray); 
//		MFW_Logger.debugInfo("bytenum:"+bytenum+"  divnum:"+divnum);
//		MFW_Logger.debugInfo("20*1024*(divnum-1):"+(20*1024*(divnum-1))+"  .. amari:"+(bytenum-20*1024*(divnum-1)));
		for(int i=0; i<divnum; ++i)
		{
			byte[] divarray = new byte[(20*1024)];
			ips.read(divarray, 0, (20*1024));
			int idxdata =  i | (divnum << 16);
			MessageFerrisMisc packet = new MessageFerrisMisc(xCoord, yCoord, zCoord, 
					MessageFerrisMisc.GUIConstructSendTagArray, idxdata, divarray);
		    MFW_PacketHandler.INSTANCE.sendToServer(packet);
		}
    }
    
    byte arrayDataIndex[][];
    //サーバー側　スロットにセット
    public void SetItemFerrisWheel(int idxdata, byte[] arraybyte)
    {
    	int divnum = idxdata >> 16;
    	if(arrayDataIndex == null || arrayDataIndex.length != divnum)arrayDataIndex = new byte[divnum][];
    	arrayDataIndex[idxdata & 0xFFFF] = arraybyte;
    	
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
    	arraybyte = allbytearray.toByteArray();
//    	MFW_Logger.debugInfo("arraybyte.length:"+arraybyte.length);
    	
    	//データ解凍
    	ByteArrayInputStream ips = new ByteArrayInputStream(arraybyte); 
		NBTTagCompound nbt = null;
		try { nbt = CompressedStreamTools.readCompressed(ips); } catch (IOException e) { e.printStackTrace(); }
		if(nbt == null)
		{
			arrayDataIndex = null;
			return;
		}
		
		//工作フラグ
		nbt.setBoolean("fromPickBlockFlag", false);
    	if(ItemStacks[0]==null)ItemStacks[0] = new ItemStack(MFW_Core.ferrisCore,1);
    	ItemStacks[0].setTagCompound(nbt);
    	//名前つけ
    	String orgname = ItemStacks[0].getItem().getItemStackDisplayName(ItemStacks[0]);
    	ItemStacks[0].setStackDisplayName(orgname+" : "+nbt.getString("MFWOName"));
    	this.wheelName = nbt.getString("MFWOName");
    	
    	//後処理
    	arrayDataIndex = null;
    	worldObj.playSoundEffect(xCoord+0.5, yCoord+0.5, zCoord+0.5, MFW_Core.MODID+":complete", 1.0F, 0.9F);
    }
    
	/////////////////////////SidedInventry///////////////////////

	 
	@Override
	public int getSizeInventory(){return this.ItemStacks.length;}

	@Override
	public ItemStack getStackInSlot(int idx) {return this.ItemStacks[idx];}

	/**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
	@Override
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_)
    {
        if (this.ItemStacks[p_70298_1_] != null)
        {
            ItemStack itemstack;

            if (this.ItemStacks[p_70298_1_].stackSize <= p_70298_2_)
            {
                itemstack = this.ItemStacks[p_70298_1_];
                this.ItemStacks[p_70298_1_] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.ItemStacks[p_70298_1_].splitStack(p_70298_2_);

                if (this.ItemStacks[p_70298_1_].stackSize == 0)
                {
                    this.ItemStacks[p_70298_1_] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

	@Override
	/**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int p_70304_1_)
    {
        if (this.ItemStacks[p_70304_1_] != null)
        {
            ItemStack itemstack = this.ItemStacks[p_70304_1_];
            this.ItemStacks[p_70304_1_] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }
	
	@Override
	/**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int p_70299_1_, ItemStack itemstack)
    {
		if((itemstack!=null) && !(itemstack.getItem() instanceof itemBlockFerrisCore))return;
        this.ItemStacks[p_70299_1_] = itemstack;

        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
        {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
    }

	@Override
	public String getInventoryName(){return "container.mfw.ferricconstructor";}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {return 10;}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
	{
		return true;
//		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : p_70300_1_.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	/**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int slotidx, ItemStack itemstack)
	{
		return false;
	}

//	@Override
//	/**
//     * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
//     * block.
//     */
//    public int[] getAccessibleSlotsFromSide(int p_94128_1_)
//    {
//        return new int[1]; //TODO
//    }
//
//	@Override
//	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
//		return false;
//	}
//
//	@Override
//	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
//		return false;
//	}

}
