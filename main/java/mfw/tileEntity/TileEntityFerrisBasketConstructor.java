package mfw.tileEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Core;
import mfw._core.MFW_Logger;
import mfw.gui.GUIFerrisBasketConstructor;
import mfw.item.itemFerrisBasket;
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
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class TileEntityFerrisBasketConstructor extends Wrap_TileEntityLimitLine 
	implements IInventory, Wrap_TileEntityChangeLimitWithKey, IMultiThreadBlockCopy {

	// LimitFrame
	private int LimitFrameLengthOut;
	private int LimitFrameWidthOut;
	private int LimitFrameHeightOut;
//	private int LimitFrameLengthIn;
//	private int LimitFrameWidthIn;
//	private int LimitFrameHeightIn;
	private int LFxOmax,LFxOmin;
	private int LFyOmax,LFyOmin;
	private int LFzOmax,LFzOmin;
//	private int LFxImax,LFxImin;
//	private int LFyImax,LFyImin;
//	private int LFzImax,LFzImin;
	private int connectx,connecty,connectz;
	// 描画関連パラメタ
	renderFerrisLimitFrame limitFrameOut = new renderFerrisLimitFrame();
//	renderFerrisLimitFrame limitFrameIn = new renderFerrisLimitFrame();
//	Vec3 posArray[] = new Vec3[16];
//	Vec3 vertexArray[] = new Vec3[38];
	public ResourceLocation RailTexture;
	// 組み立て時間
	public byte progressState = 0; // 0:no craft 1:isCrafting 2:Complete
//	public int progressTime = 0;
//	public int totalTime = 200;
	// 構成ブロックリスト
//	class BlockPiece{
//		public int x,y,z;
//		public int blockId,meta;
//		public BlockPiece(int x, int y, int z, int id, int m){this.x=x; this.y = y; this.z = z; blockId = id; meta = m;}
//	}
//	public List<BlockPiece> listBlockPieces = new ArrayList<BlockPiece>();
	public String AuthorName;
	// Createボタン押したプレイヤー
//	public EntityPlayer playerCreating;
	// ホイールアイテムを入れるところ
	private ItemStack[] ItemStacks = new ItemStack[1];
	// GUIに表示する名前保存
	public String wheelName;
	public GUIFerrisBasketConstructor gui;
	// 非同期ブロックコピー
	private multiThreadBlockCopier threadBlockCopy = null;
	private NBTTagCompound copiedNBTTag;
	private int CopiedConnectorNum;
		
	public boolean FlagDrawCore = true;
	public boolean FlagDrawEntity = false;
	
	public TileEntityFerrisBasketConstructor()
	{
		super();
		LimitFrameLengthOut = 5;
		LimitFrameWidthOut = 3;
		LimitFrameHeightOut = 5;
//		LimitFrameLengthIn = 3;
//		LimitFrameWidthIn = 1;
//		LimitFrameHeightIn = 3;
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
	
	public int getLimitFrameLength() {
		return LimitFrameLengthOut;
	}

	public int getLimitFrameWidth() {
		return LimitFrameWidthOut;
	}
	
	public int getLimitFrameHeight() {
		return LimitFrameHeightOut;
	}
	
	/*
	 * flag ... 1:in  2 out
	 */
	public void setFrameLength(int l)
	{
		LimitFrameLengthOut += l;
		if(LimitFrameLengthOut > 100)LimitFrameLengthOut = 100;
		if(LimitFrameLengthOut < 5)LimitFrameLengthOut = 5;
		calcFramePos(worldObj);
	}
	public void setFrameWidth(int w)
	{
		LimitFrameWidthOut += w;
		if(LimitFrameWidthOut > 100)LimitFrameWidthOut = 100;
		if(LimitFrameWidthOut < 3)LimitFrameWidthOut = 3;
		calcFramePos(worldObj);
	}
	public void setFrameHeight(int h)
	{
		LimitFrameHeightOut += h;
		if(LimitFrameHeightOut > 100)LimitFrameHeightOut = 100;
		if(LimitFrameHeightOut < 5)LimitFrameHeightOut = 5;
		calcFramePos(worldObj);
	}
	
	public void setFrameX(int flag)
	{
		flag = TileEntityFerrisConstructor.convertflagtoLength(flag);
		setFrameLength(flag);
	}
	public void setFrameY(int flag)
	{
		flag = TileEntityFerrisConstructor.convertflagtoLength(flag);
		setFrameHeight(flag);
	}
	public void setFrameZ(int flag)
	{
		flag = TileEntityFerrisConstructor.convertflagtoLength(flag);
		setFrameWidth(flag);
	}
	
	public void calcFramePos(World world)
	{
		calcFramePos(world,world.getBlockMetadata(xCoord, yCoord, zCoord));
	}
	public void calcFramePos(World world, int meta)
	{
		double Length1 = ((double)LimitFrameLengthOut)/2d;
		double Length2 = ((double)LimitFrameWidthOut)/2d;
		
		double x = Length1;
		double y = LimitFrameHeightOut;
		double z = Length1;
		double xoffset = 0;
		double zoffset = 0;
		//向き
      	switch(meta)
		{
		case 0 : case 1 : return; // こない
		case 2 : case 3 : z = Length2; break;
		case 4 : case 5 : x = Length2; break;
		}
		//奥へずらす処理
		switch(meta)
		{
		case 2 : xoffset = +x+1; zoffset = +z; break;
		case 3 : xoffset = -x  ; zoffset = -z+1; break;
		case 4 : zoffset = -z  ; xoffset = +x; break;
		case 5 : zoffset = +z+1; xoffset = -x+1; break;
		}
		setframeposOut((int)(-x+xoffset), (int)(x+xoffset), 0, (int)(y), (int)(-z+zoffset), (int)(z+zoffset));

//		double x1 = (LFxOmin+LFxOmax)/2d;
//		double y1 = (LFyOmax)/2;
//		double z1 = (LFzOmin+LFzOmax)/2d;
//		double X=0;
//		double Z=0;
//		double Y = LimitFrameHeightIn/2d;
//		switch(meta)
//		{
//		case 2 : case 3 : X = LimitFrameLengthIn/2d; Z = LimitFrameWidthIn/2d; break;
//		case 4 : case 5 : X = LimitFrameWidthIn/2d; Z = LimitFrameLengthIn/2d; break;
//		}
//		setframeposIn(
//				(int)Math.floor(x1-X), (int)Math.floor(x1+X), 
//				(int)Math.floor(y1-Y), (int)Math.floor(y1+Y), 
//				(int)Math.floor(z1-Z), (int)Math.floor(z1+Z));
	}
	private void setframeposOut(int xmin,int xmax,int ymin,int ymax,int zmin,int zmax)
	{
		LFxOmin = xmin; LFxOmax = xmax;	LFyOmin = ymin; LFyOmax = ymax;	LFzOmin = zmin; LFzOmax = zmax;
	}
//	private void setframeposIn(int xmin,int xmax,int ymin,int ymax,int zmin,int zmax)
//	{
//		LFxImin = xmin; LFxImax = xmax;	LFyImin = ymin; LFyImax = ymax;	LFzImin = zmin; LFzImax = zmax;
//	}
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
//		int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
//		double x1 = (LFxOmin+LFxOmax)/2d;
//		double y1 = (LFyOmax)/2;
//		double z1 = (LFzOmin+LFzOmax)/2d;
//		double X=0;
//		double Z=0;
//		double Y = LimitFrameHeightIn/2d;
//		switch(meta)
//		{
//		case 2 : case 3 : X = LimitFrameLengthIn/2d; Z = LimitFrameWidthIn/2d; break;
//		case 4 : case 5 : X = LimitFrameWidthIn/2d; Z = LimitFrameLengthIn/2d; break;
//		}
		limitFrameOut.createVertex(this, world,	LFxOmin-0.5,LFxOmax-0.5,LFyOmin-0.5,LFyOmax-0.5,LFzOmin-0.5,LFzOmax-0.5);
//		limitFrameIn.createVertex(this, world, LFxImin-0.5,LFxImax-0.5,LFyImin+0.5,LFyImax+0.5,LFzImin-0.5,LFzImax-0.5);
//					Math.floor(x1-X)-0.5, Math.floor(x1+X)-0.5, 
//					Math.floor(y1-Y)+0.5, Math.floor(y1+Y)+0.5, 
//					Math.floor(z1-Z)-0.5, Math.floor(z1+Z)-0.5);
	}

	
	public void render(Tessellator tess)
	{
		limitFrameOut.render(tess);
	}

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);      
    	loadFromNBT(par1NBTTagCompound, "");
    }
    public void loadFromNBT(NBTTagCompound nbt, String tag)
    {
    	LimitFrameLengthOut = nbt.getInteger(tag+"framelenO");
    	LimitFrameWidthOut = nbt.getInteger(tag+"framewidO");
    	LimitFrameHeightOut = nbt.getInteger(tag+"frameheiO");
    	
    	wheelName = nbt.getString(tag+"wheelname");
    	
    	LFxOmax = nbt.getInteger(tag+"lfxomax");
    	LFxOmin = nbt.getInteger(tag+"lfxomin");
    	LFyOmax = nbt.getInteger(tag+"lfyomax");
    	LFyOmin = nbt.getInteger(tag+"lfyomin");
    	LFzOmax = nbt.getInteger(tag+"lfzomax");
    	LFzOmin = nbt.getInteger(tag+"lfzomin");
    	
    	FlagDrawCore = nbt.getBoolean(tag+"flagdrawcore");
      	FlagDrawEntity = nbt.getBoolean(tag+"flagdrawentity");
    	
    	// スロットの中身解凍
    	NBTTagList nbttaglist = nbt.getTagList(tag+"Items", 10);
//        this.ItemStacks = new ItemStack[1];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte(tag+"Slot");

            if (b0 >= 0 && b0 < this.ItemStacks.length)
            {
                this.ItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }
    
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        saveToNBT(par1NBTTagCompound, "");
    }
    public void saveToNBT(NBTTagCompound nbt, String tag)
    {
    	nbt.setInteger(tag+"framelenO", LimitFrameLengthOut);
    	nbt.setInteger(tag+"framewidO", LimitFrameWidthOut);
    	nbt.setInteger(tag+"frameheiO", LimitFrameHeightOut);

    	nbt.setString(tag+"wheelname", wheelName);
    	
    	nbt.setInteger(tag+"lfxomax", LFxOmax);
    	nbt.setInteger(tag+"lfxomin", LFxOmin);
    	nbt.setInteger(tag+"lfyomax", LFyOmax);
    	nbt.setInteger(tag+"lfyomin", LFyOmin);
    	nbt.setInteger(tag+"lfzomax", LFzOmax);
    	nbt.setInteger(tag+"lfzomin", LFzOmin);
    	
    	nbt.setBoolean(tag+"flagdrawcore", FlagDrawCore);
    	nbt.setBoolean(tag+"flagdrawentity", FlagDrawEntity);
//    	nbt.setInteger(tag+"lfximax", LFxImax);
//    	nbt.setInteger(tag+"lfximin", LFxImin);
//    	nbt.setInteger(tag+"lfyimax", LFyImax);
//    	nbt.setInteger(tag+"lfyimin", LFyImin);
//    	nbt.setInteger(tag+"lfzimax", LFzImax);
//    	nbt.setInteger(tag+"lfzimin", LFzImin);
    	// スロットの中身保存
    	NBTTagList nbttaglist = new NBTTagList();
    	for (int i = 0; i < this.ItemStacks.length; ++i)
        {
            if (this.ItemStacks[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte(tag+"Slot", (byte)i);
                this.ItemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        nbt.setTag(tag+"Items", nbttaglist);
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
		return isExistBasket() || Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode;
	}
	//スロットにコアフレームが入っているかどうか
	public boolean isExistBasket()
	{
		if(ItemStacks[0]==null)return false;
		return ItemStacks[0].getItem() instanceof itemFerrisBasket;
	}

	// GUIConstructorから呼ばれる　クリエイト開始点 sideonly Client
	@SideOnly(Side.CLIENT)
	public void startConstructBasket()
	{
		if(progressState == 0)
		{
			if(canCreateCore()==false)return;
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			progressState = 1;
			if(player!=null)AuthorName = player.getDisplayName();
			threadBlockCopy = new multiThreadBlockCopier(this, worldObj);
	    	threadBlockCopy.setSrcPosition(LFxOmin+xCoord, LFyOmin+yCoord, LFzOmin+zCoord, LFxOmax+xCoord-1, LFyOmax+yCoord-1, LFzOmax+zCoord-1);
			threadBlockCopy.start();
			this.markDirty();
		}
	}

	@SideOnly(Side.CLIENT)
    public int getCookProgressScaled(int scale)
    {
		return (int) (scale * threadBlockCopy.getProgress());
    }
	
	public boolean isCreating()
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
    	CopiedConnectorNum = threadBlockCopy.getBlocksCompressor().getConnectorNum();
    	connectx = threadBlockCopy.getBlocksCompressor().getConnectorPos(0);
    	connecty = threadBlockCopy.getBlocksCompressor().getConnectorPos(1);
    	connectz = threadBlockCopy.getBlocksCompressor().getConnectorPos(2);
    	threadBlockCopy = null;
    }
    
    public void abnormalEnd()
    {
    	progressState = 0;
    	threadBlockCopy = null;
    	MFW_Logger.warn("Coping blocks is abnormal end.");
    }
    //////////// IMultiThreadBlockCopy end ///////////  
    
    public void sendNBTToServer()
    {
    	// できたデータをカゴに登録
    	if(CopiedConnectorNum != 1)
    	{
       		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
//       				"Failure : There is not a connector, or there is too many it."	
       				StatCollector.translateToLocal("message.construct.connector_only1")
       				));
       		return;
        }
//    	if(ItemStacks[0] == null)return;
//    	if(!(ItemStacks[0].getItem() instanceof itemFerrisBasket))return;
    	int constructormeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
    	
    	NBTTagCompound nbt = copiedNBTTag;
//        BlocksCompressor copier = new BlocksCompressor();
    	nbt.setString("MFWOName", this.gui.getName().equals("")?"-NoName-":this.gui.getName());
    	nbt.setString("author", AuthorName);
    	nbt.setByte("constructormetaflag", (byte)( constructormeta | (FlagDrawCore?8:0) ));
    	nbt.setInteger("basefornbtx", xCoord); // CTM用基準座標
		nbt.setInteger("basefornbty", yCoord);
		nbt.setInteger("basefornbtz", zCoord);
		nbt.setInteger("connectoffsetx", (connectx)); // 中心位置（工作台の相対距離）
		nbt.setInteger("connectoffsety", (connecty));
		nbt.setInteger("connectoffsetz", (connectz));
		
//		copier.setSrcPosition(LFxOmin+xCoord, LFyOmin+yCoord, LFzOmin+zCoord, LFxOmax+xCoord, LFyOmax+yCoord, LFzOmax+zCoord);
//    	copier.makeTag(nbt, worldObj);
    	
//		ItemStacks[0].setStackDisplayName(this.wheelName.matches("")?"-NoName-":this.wheelName);
//        ItemStacks[0].setTagCompound(nbt);
//        String orgname = ItemStacks[0].getItem().getItemStackDisplayName(ItemStacks[0]);
//        ItemStacks[0].setStackDisplayName(orgname+" : "+nbt.getString("BasketName"));
		byte[] bytearray = null;
		try { bytearray = CompressedStreamTools.compress(nbt); }catch (IOException e) {e.printStackTrace(); return;}
		
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
					MessageFerrisMisc.GUIBConstructSendTagArray, idxdata, divarray);
		    MFW_PacketHandler.INSTANCE.sendToServer(packet);
		}
    }
    
    byte arrayDataIndex[][];
    //サーバー側　スロットにセット
    public void SetItemFerrisBasket(byte[] arraybyte, int idxdata)
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
    	
    	ByteArrayInputStream ips = new ByteArrayInputStream(arraybyte); 
		NBTTagCompound nbt = null;
		try { nbt = CompressedStreamTools.readCompressed(ips); } catch (IOException e) { e.printStackTrace(); }
		if(nbt == null)return;
		
		//工作フラグ
		nbt.setBoolean("fromPickBlockFlag", false);
		if(ItemStacks[0]==null)ItemStacks[0] = new ItemStack(MFW_Core.ItemFerrisBasket,1);
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
		if((itemstack!=null) && !(itemstack.getItem() instanceof itemFerrisBasket))return;
        this.ItemStacks[p_70299_1_] = itemstack;

        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
        {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
    }

	@Override
	public String getInventoryName(){return "container.erc.ferricconstructor";}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {return 10;}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : p_70300_1_.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
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
		if(itemstack==null)return false;
		if(itemstack.getItem() instanceof itemFerrisBasket)return true;
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
