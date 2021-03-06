package mfw.tileEntity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import mfw._core.MFW_Command;
import mfw._core.MFW_Core;
import mfw._core.MFW_Logger;
import mfw._core.connectPos;
import mfw.blocksReplication.BlocksReplication;
import mfw.entity.entityFerrisBasket;
import mfw.item.itemBlockFerrisCore;
import mfw.lib_util.InterpolationTick;
import mfw.math.MFW_Math;
import mfw.message.MessageFerrisMisc;
import mfw.sound.FerrisFrameSound;
import mfw.sound.SoundManager;
import mfw.storyboard.StoryBoardManager;
import mfw.util.MFWBlockAccess;
import mfw.wrapper.I_FerrisPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import sun.nio.cs.ArrayEncoder;

public class TileEntityFerrisWheel extends TileEntity implements ISidedInventory, I_FerrisPart {

	public float angle = 0, prevAngle = 0;
	public InterpolationTick rotation = new InterpolationTick(0);
	public float rotSpeed = 0;
	public float rotAccel = 0;
	public float rotResist = 0.1f;
	public float speedTemp;
	public InterpolationTick rotMiscFloat1 = new InterpolationTick(1f);
	public InterpolationTick rotMiscFloat2 = new InterpolationTick(0);
	public InterpolationTick wheelSize = new InterpolationTick(1.0f);
	public boolean stopFlag = false;
	public int   side;
	public int 	 meta;

	//public byte rotFlag = 0;
	//rotflag enum
//	public static final byte rotFlag_Normal = 0;
//	public static final byte rotFlag_Sin = 1;
//	public static final byte rotFlag_ComeAndGo = 2;
//	public static final byte rotFlag_Move_RsOnToggle = 3;
//	public static final byte rotFlag_StoryBoard = 4;
//	public static final byte rotFlag_End = 5; //番兵
//	public static final byte rotFlag_Sync = 100;
	public boolean isEnableSinConvert = false;
	public boolean isEnableStoryBoard = false;
	//同期モードは上2つのフラグは無効になる
	public boolean isEnableSyncRot = false;
	public boolean isSyncTargetSpeed = false;
	
	public byte rsFlag = 0;
	//rsflag enum
	public final byte rsFlag_Non = 0; //効果なし
	public final byte rsFlag_StopWhenOn = 1; //ON時停止
	public final byte rsFlag_StopWhenOff = 2; //OFF時停止
	public final byte rsFlag_RatioPositive = 3; //大きいほど高倍率
	public final byte rsFlag_RatioNegative = 4; //小さいほど高倍率
	public final byte rsFlag_End = 5;
	
	private float rspower;
	private float prevRSPower;
	public byte isToggleNow; // 0:non  1:off to ON  -1:on to OFF
	TileEntityFerrisWheel rootParentTile;
	
	// ２軸回転対応描画用回転マトリクス用軸設定
	public float rotConst_meta2 = 0, rotMeta2_side = 0;
	public Vec3 rotvecConst_meta2 = Vec3.createVectorHelper(0, 0, 0);
	public Vec3 rotvecMeta2_side = Vec3.createVectorHelper(0, 0, 0);
	public Vec3 vecAxisRot = Vec3.createVectorHelper(0, 0, 0);
	
	private int copyNum = 1;
	private float copyRotOffset = 0;
	private int copyMode = 0;
	// variable rotation axis
	public float rotVar1 = 0, rotVar2 = 0;
	// CTM用基準
	public int ctmPosX,ctmPosY,ctmPosZ;
	// BasketSlot
	private ItemStack[] ItemStacks = null;//new ItemStack[0];
	// root itemstack save
	private NBTTagCompound rootTagNBTSave;
	
	//ferrispart
	TileEntityFerrisWheel parentTile = null;
	public int currentIdx = -1;
	public int currentLayer = 0;
	public float posX;
	public float posY;
	public float posZ;
	float prevPosX,prevPosY,prevPosZ;
	public connectPos ConnectPos = new connectPos();
	
	// for GUI
	TileEntityFerrisWheel selectedPartTile = null;
	public String WheelName;
	
	boolean isConstructed = false;
	
	//ロック用
	public boolean isLock = false;
	
	//storyboard
	private StoryBoardManager storyboardManager = new StoryBoardManager(this);
	
	//sound
	private int soundidx = 0;
	public FerrisFrameSound sound;
	public void SetSoundIndex(int idx){
		if(soundidx != idx){
			soundidx = idx;
			if(sound!=null)sound.Invalid();
			if(idx==0)return;
			if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			{
				String domain = SoundManager.getSoundDomain(idx);
				if(idx >= SoundManager.sounds.size())soundidx = 0;
				FerrisFrameSound sound = new FerrisFrameSound(this, MFW_Core.proxy.getClientPlayer(), SoundManager.soundDomain+":"+domain);
				this.sound = sound;
				MFW_Core.proxy.PlaySound(sound);
//				MFW_Logger.debugInfo("sound register "+domain);
			}
		}
	}
	public int GetSoundIndex(){ return soundidx; }
	
	int orgsize;
	byte[] compressedModelData;

//	private int width;
	public int length;
	
	MFWBlockAccess blockAccess;
	BlocksReplication BlocksRep;
	
	public I_FerrisPart[] ArrayEntityParts = new entityFerrisBasket[1];

	// 同期回転用
	TileEntityFerrisWheel parentSyncTile = null;
	int parentTileTreeIndex = -1;
	int parentsyncX, parentsyncY, parentsyncZ;
	public List<TileEntityFerrisWheel> childSyncTileList = new ArrayList<TileEntityFerrisWheel>();
	
	public TileEntityFerrisWheel()
	{
		selectedPartTile = this;
		isConstructed = false;
		currentLayer = 0;
		blockAccess = new MFWBlockAccess(worldObj, this);
		BlocksRep = new BlocksReplication(worldObj, blockAccess);
		rootParentTile = this;
	}
	
	@Override
	public double getMaxRenderDistanceSquared()
	{
		return Double.MAX_VALUE;
	}
	@Override
	public AxisAlignedBB getRenderBoundingBox() 
	{
		return INFINITE_EXTENT_AABB;
	}
	
	public void setWorldObj(World world)
	{
		this.worldObj = world;
		if(blockAccess!=null)
		{
			blockAccess.setWorld(world);
			blockAccess.setWorldToTileEntities(world);
		}
		for(I_FerrisPart part : ArrayEntityParts)
		{
			if(part!=null)if(part.isTile())((TileEntityFerrisWheel)part).setWorldObj(world);
		}
		BlocksRep.setWorld(world);
	}
	
	public boolean onPushChildPart(int num)
	{
		if(ArrayEntityParts.length==0)return false;
		if(num < 0 || ArrayEntityParts.length <= num)return false;
		if(ArrayEntityParts[num] instanceof TileEntityFerrisWheel)
		{
//			MFW_Logger.debugInfo("onpushchildpart num"+((TileEntityFerrisWheel)ArrayEntityParts[num]).getSizeInventory());
			getRootParent().selectedPartTile = (TileEntityFerrisWheel) ArrayEntityParts[num];
			return true;
		}
		return false;
	}
	public boolean onPushBack()
	{
		if(parentTile == null)return false;
		getRootParent().selectedPartTile = parentTile;
		return true;
	}
	public TileEntityFerrisWheel getRootParent()
	{
		if(parentTile != null)return parentTile.getRootParent();
		else return this;
	}
	public TileEntityFerrisWheel getSelectedPartTile()
	{
		if(selectedPartTile == null)return this;
		return selectedPartTile;
	}
	public void selectedTileNull()
	{
		getRootParent().selectedPartTile = null;
	}
	public void resetSelectedTile()
	{
		selectedPartTile = getRootParent();
	}
	
	public NBTTagCompound getNbtFromSlotNum(int slotnum)
	{
		return ItemStacks[slotnum].getTagCompound();
	}
	
	public TileEntityFerrisWheel getParentTile()
	{
		return parentTile;
	}
	
	public void setRSPower(int p)
	{
//		MFW_Logger.debugInfo("istoggle:"+isToggleNow);
		rspower = p/(float)15;
//		MFW_Logger.debugInfo(String.format("rspower:%1.3f",rspower));
	}
	public float getRSPower()
	{
		return rootParentTile.rspower;
	}
	public float getRSTrigger()
	{
		return rootParentTile.isToggleNow;
	}
	public String getSRTitleStringForGUI()
	{
		return getSRTitleStringForGUI(rsFlag);
	}
	public void rotateRSFlag()
	{
		rsFlag = (byte) ((rsFlag+1) % rsFlag_End);
	}
	public String getSRTitleStringForGUI(int flag)
	{
//		public final byte rsFlag_Non = 0; //効果なし
//		public final byte rsFlag_StopWhenOn = 1; //ON時停止
//		public final byte rsFlag_StopWhenOff = 2; //OFF時停止
//		public final byte rsFlag_RatioPositive = 3; //大きいほど高倍率
//		public final byte rsFlag_RatioNegative = 4; //小さいほど高倍率
		String names[] = {
				StatCollector.translateToLocal("gui.core.switch.rsmode0"),
				StatCollector.translateToLocal("gui.core.switch.rsmode1"),
				StatCollector.translateToLocal("gui.core.switch.rsmode2"),
				StatCollector.translateToLocal("gui.core.switch.rsmode3"),
				StatCollector.translateToLocal("gui.core.switch.rsmode4")
				};
		return names[flag%rsFlag_End];
	}
	
	public StoryBoardManager getStoryBoardManager()
	{
		return storyboardManager;
	}
	public void setStoryBoardManager(StoryBoardManager manager)
	{
		storyboardManager = manager;
	}
	
	// Partをスロットから取り出したときに、取り出したTileのこれを呼ぶ
	public void dropChildParts()
	{
		if(worldObj.isRemote)return;
		for(int i=0; i<ItemStacks.length; ++i)
		{
			if(ItemStacks[i] == null)continue;
			if(ArrayEntityParts[i]!=null)
			{
				if(ArrayEntityParts[i].isTile())((TileEntityFerrisWheel) ArrayEntityParts[i]).dropChildParts();
				ArrayEntityParts[i].dead();
			}
//			EntityItem entityitem = entityplayer.dropPlayerItemWithRandomChoice(ItemStacks[i], false);
			// TODO プレイヤーから落とすようにしたい
			EntityItem entityitem = new EntityItem(worldObj, (double)(xCoord+0.5), (double)(yCoord+1), (double)(zCoord+0.5), ItemStacks[i]);
//            entityitem.delayBeforeCanPickup = 0;
            entityitem.motionX = 0;
            entityitem.motionY = 0.2;
            entityitem.motionZ = 0;
            if (ItemStacks[i].hasTagCompound())
            {
                entityitem.getEntityItem().setTagCompound((NBTTagCompound)ItemStacks[i].getTagCompound().copy());
            }
            worldObj.spawnEntityInWorld(entityitem);
		}
	}
	
	@Override
	public void invalidate()
	{
		super.invalidate();
		BlocksRep.invalidate();
		for(I_FerrisPart part : ArrayEntityParts)
		{
			if(part!=null)if(part.isTile())((TileEntityFerrisWheel) part).invalidate();
		}
	}

	public int getTreeIndexFromTile(TileEntityFerrisWheel target)
	{
		ArrayList<TileEntityFerrisWheel> list = createWheelList();
		return list.indexOf(target);
	}
	public TileEntityFerrisWheel getTileFromTreeIndex(int wheelidx)
	{
		if(wheelidx < 0)return null;
		ArrayList<TileEntityFerrisWheel> list = createWheelList();
		if(wheelidx >= list.size())return null;
		return list.get(wheelidx);
	}
	public ArrayList<TileEntityFerrisWheel> createWheelList()
	{
		TileEntityFerrisWheel tile = (TileEntityFerrisWheel) worldObj.getTileEntity(xCoord, yCoord, zCoord);
		ArrayDeque<TileEntityFerrisWheel> deque = new ArrayDeque<TileEntityFerrisWheel>();
		ArrayList<TileEntityFerrisWheel> list = new ArrayList<TileEntityFerrisWheel>();
		if(tile==null)return list;
		deque.add(tile);
		list.add(tile);
		for(;;)
		{
			if(deque.size()==0)break;
			TileEntityFerrisWheel t = deque.remove();
			for(I_FerrisPart part : t.ArrayEntityParts)
			{
				if(part==null)continue;
				if(!part.isTile())continue;
				deque.add((TileEntityFerrisWheel) part);
				list.add((TileEntityFerrisWheel) part);
			}
		}
		return list;
	}
	
	public void ValidatePart_clientBasket(int slotIdx, I_FerrisPart basket)
	{
		ItemStack itemstack = ItemStacks[slotIdx];
		if(itemstack == null)return;
		NBTTagCompound nbt = itemstack.getTagCompound();
		if(nbt == null)return;
		connectPos cp = blockAccess.listConnectPos.get(slotIdx);
		
		//Basketスポーン
		I_FerrisPart part = basket;
        part.init(this, cp, slotIdx);
        ArrayEntityParts[slotIdx] = part;
        part.constructFromTag(nbt, side, meta, xCoord, yCoord, zCoord, false);
	}
	
	private void ValidatePart(NBTTagCompound nbt, ItemStack itemstack, int slotidx, boolean isMultiThread)
	{
        boolean istile = (itemstack.getItem() instanceof itemBlockFerrisCore);
        if(nbt == null)return;
        
        //Basketスポーン
        connectPos cp = blockAccess.listConnectPos.get(slotidx);
        I_FerrisPart part;
        if(!istile)
    	{
        	if(worldObj==null || worldObj.isRemote) return;
    		part = new entityFerrisBasket(worldObj);
    	}
        else
    	{
        	part = new TileEntityFerrisWheel();
        	((TileEntity)part).setWorldObj(worldObj);
    	}
        part.init(this, cp, slotidx);
        if(!part.isTile() && worldObj!=null && !worldObj.isRemote)worldObj.spawnEntityInWorld((Entity) part);
        ArrayEntityParts[slotidx] = part;
        part.constructFromTag(nbt, side, meta, xCoord, yCoord, zCoord, isMultiThread);
        
//        if(!part.isTile() && worldObj!=null && !worldObj.isRemote)
//    	{
//        	for(entityPartSit e : (((entityFerrisBasket)part).getPartsSit()))
//        	{
//        		worldObj.spawnEntityInWorld(e);
//        	}
//    	}
        
        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
        {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
	}
	
	public void constructFromTag(NBTTagCompound nbt, int meta, boolean isMultiThread)
	{
		if(isConstructed)return;
		copyNum = nbt.getInteger("copynum");
		copyMode = nbt.getInteger("copymode");
		int connectnum = nbt.getInteger("mtbr:connectnum");
		int slotnum = (copyMode==0)?connectnum*copyNum:connectnum;
		ItemStacks = new ItemStack[slotnum];
		ArrayEntityParts = new I_FerrisPart[connectnum*copyNum];	
		this.meta = meta;
		side = nbt.getByte("constructormetaflag") & 7;
		blockAccess.setCopyNum(copyNum, side, copyMode);
		BlocksRep.setWorld(worldObj);
		BlocksRep.setCorePosition(xCoord, yCoord, zCoord);
		BlocksRep.constructFromTag(nbt, meta, this, isMultiThread);
		WheelName = nbt.getString("MFWOName");
//    	ItemStacks = new ItemStack[blockAccess.getConnectorNum()];
//		ArrayEntityParts = new I_FerrisPart[blockAccess.getConnectorNum()];	
	    setRotAxis();
	    isConstructed = true;
	    copyRotOffset = 360f / ((float)copyNum);
	    int len1 = nbt.getInteger("connectoffsetx"); // 中心位置（工作台の相対距離）
		int len2 = nbt.getInteger("connectoffsety");
		int len3 = nbt.getInteger("connectoffsetz");
	    length = Math.max(len1, Math.max(len2, len3));
	}
	
	public void completeConstruct(NBTTagCompound nbt)
	{
		blockAccess.postInit();
		// ライト
		if((worldObj==null) ? MFW_Core.proxy.checkSide().isClient() : worldObj.isRemote)blockAccess.diffLight();
		
//		int blocklenX = nbt.getInteger("mtybr:sizex");
//		int blocklenY = nbt.getInteger("mtybr:sizey");
//		int blocklenZ = nbt.getInteger("mtybr:sizez");
//		for(int x=0;x<blocklenX;++x){
//			for(int y=0;y<blocklenY;++y){
//				for(int z=0;z<blocklenZ;++z){
//					Block b = blockAccess.getBlockOrgPos(x, y, z);
//					//液体なら当たり判定なし
//					if(b.getMaterial().isLiquid())continue;
//					//空気ブロックは当たり判定なし
//					if(Block.getIdFromBlock(b)==0)continue;
//					//シートの当たり判定は消す
//					if(b.canCollideCheck(blockAccess.getBLockMetadata_AbsolutePos(x, y, z), false)==false)continue;
//					//上にシートがあったら別枠でEntity登録するのでPartとしての当たり判定は消す
//					if(b instanceof blockSeatToSitEx)
//					{
//						entityPartSitEx seatex = new entityPartSitEx(worldObj, this, meta, x, y-1, z, blockAccess.getBLockMetadata_AbsolutePos(x, y, z));
//						seatex.setMiscData(xCoord, yCoord, zCoord, 0, getTreeIndexFromTile(this), 0);
//						if(worldObj.isRemote==false)worldObj.spawnEntityInWorld(seatex);
//					}
//				}
//			}
//		}
	}
	
	public void setSpeed(int flag)
	{
		switch(flag)
		{
		case 0 : rotAccel -= 1.0f; break;
		case 1 : rotAccel -= 0.1f; break;
		case 2 : rotAccel -= 0.01f; break;
		case 3 : rotAccel += 0.01f; break;
		case 4 : rotAccel += 0.1f; break;
		case 5 : rotAccel += 1.0f; break;
		}
		if(rotAccel > 100f)rotAccel = 100f;
		else if(rotAccel < -100f)rotAccel = -100f;
	}
	public void turnSpeed()
	{
		rotAccel *= -1;
	}
	
	public void setRotMisc(int flag, int idx)
	{
		float v=0;
		switch(flag)
		{
		case 0 : v = -10.0f; break;
		case 1 : v = -1.0f; break;
		case 2 : v = -0.1f; break;
		case 3 : v = 0.1f; break;
		case 4 : v = 1.0f; break;
		case 5 : v = 10.0f; break;
		}
		switch(idx)
		{
		case 1 : 
			rotMiscFloat1.add(v);
			rotMiscFloat1.clamp(-400, 400);
		break;
		case 2 : 
			rotMiscFloat2.add(v);
			rotMiscFloat2.clamp(-400, 400);
		break;
		}
	}
	
	public void toggleLock()
	{
		isLock = !isLock;
	}
	
	// NormalとStopをトグルする関数
	public void toggleStopFlag()
	{
		stopFlag = !stopFlag;
	}
	
	public void toggleSinConvertFlag()
	{
		isEnableSinConvert = !isEnableSinConvert;
	}
	public void toggleStoryBoardFlag()
	{
		isEnableStoryBoard = !isEnableStoryBoard;
	}
	public void toggleSyncTarget()
	{
		isSyncTargetSpeed = !isSyncTargetSpeed;
	}
	
	public void reset()
	{
		stopFlag = false;
		rotAccel = 0;
		rotSpeed = 0;
		rotation.set(0);
		rotResist = 0.01f;
		rotMiscFloat1.set(1);
		rotMiscFloat2.set(0);
		speedTemp = 0;
	}
	// SyncとNormalを切り替えるための関数
	public void toggleSyncFlag()
	{
		isEnableSyncRot = !isEnableSyncRot;
	}
	
	public void setSize(int flag)
	{
		switch(flag)
		{
		case 0 : wheelSize.add(-0.1f); break;
		case 1 : wheelSize.add(-0.01f); break;
		case 2 : wheelSize.add(0.01f); break;
		case 3 : wheelSize.add(0.1f); break;
		}
		wheelSize.clamp(0, 100.0f);
	}
	public void setResist(int flag)
	{
		switch(flag)
		{
		case 0 : rotResist *= 1.1f; break;
		case 1 : rotResist *= 1.01f; break;
		case 2 : rotResist /= 1.01f; break;
		case 3 : rotResist /= 1.1f; break;
		}
		if(rotResist > 0.99f)rotResist = 0.99f;
		else if(rotResist < 0.001f)rotResist = 0.001f;
	}
	
	public void setRot(int flagVal, int flagGUIButton)
	{
		float rot = 0;
		switch(flagVal)
		{
		case 0 : rot = -10f; break;
		case 1 : rot = -1f; break;
		case 2 : rot = 1f; break;
		case 3 : rot = 10f; break;
		}
		switch(flagGUIButton)
		{
		case MessageFerrisMisc.GUICoreRot1: rotVar1 += rot; break;
		case MessageFerrisMisc.GUICoreRot2: rotVar2 += rot; break;
		}
		if(rotVar1 > 180f)rotVar1 = 180f;
		else if(rotVar1 < -180f)rotVar1 = -180f;
		if(rotVar2 > 180f)rotVar2 = 180f;
		else if(rotVar2 < -180f)rotVar2 = -180f;
	}
	public void resetRot()
	{
		rotVar1 = 0; rotVar2 = 0;
		wheelSize.set(1.0f);
	}
	
	public void setRotAxis()
	{
		int s = side, m = meta;
		if(parentTile!=null)
		{
//			s = parentTile.side;
//			rotConst_meta2 = 0;
			m = parentTile.side;
		}
//		int side = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		switch(s)
		{
		case 0 : rotvecConst_meta2.xCoord = 1; rotConst_meta2 = 90; break;
		case 1 : rotvecConst_meta2.xCoord = 1; rotConst_meta2 = -90; break;
		case 2 : rotvecConst_meta2.yCoord = 1; rotConst_meta2 = 0;  break;
		case 3 : rotvecConst_meta2.yCoord = 1; rotConst_meta2 = 180; break;
		case 4 : rotvecConst_meta2.yCoord = 1; rotConst_meta2 = -90; break;
		case 5 : rotvecConst_meta2.yCoord = 1; rotConst_meta2 = 90; break;
		}
		switch(m)
		{
		case -1 : rotMeta2_side = 0;
		case 0 : rotvecMeta2_side.xCoord = 1; rotMeta2_side = -90; break;
		case 1 : rotvecMeta2_side.xCoord = 1; rotMeta2_side = 90; break;
		case 2 : rotvecMeta2_side.yCoord = 1; rotMeta2_side = 0;  break;
		case 3 : rotvecMeta2_side.yCoord = 1; rotMeta2_side = 180; break;
		case 4 : rotvecMeta2_side.yCoord = 1; rotMeta2_side = 90; break;
		case 5 : rotvecMeta2_side.yCoord = 1; rotMeta2_side = -90; break;
		}
		switch(s)
		{
		case 0 : vecAxisRot.yCoord = 1; break;
		case 1 : vecAxisRot.yCoord =-1; break;
		case 2 : vecAxisRot.zCoord = 1; break;
		case 3 : vecAxisRot.zCoord =-1; break;
		case 4 : vecAxisRot.xCoord = 1; break;
		case 5 : vecAxisRot.xCoord =-1; break;
		}
	}
	public void resetRotAxis()
	{
		setRotAxis();
		for( I_FerrisPart p :ArrayEntityParts)if(p!=null)if(p.isTile())
		{
			((TileEntityFerrisWheel)p).resetRotAxis();
		}
		else
		{
			((entityFerrisBasket)p).fixEntityLength(this.meta);
		}
	}
	
	public void syncRot_recieve(float[] afloat)
	{
		ArrayList<TileEntityFerrisWheel>wheellist = createWheelList();
		if(compressedModelData==null)return;
		int i=0;
		for(TileEntityFerrisWheel tile : wheellist)tile.rotation.set(afloat[i++]);
	}
	
	private int counter=0;
	public void syncRot()
	{
		if(worldObj.isRemote)return;
		if(parentTile!=null)return;
		if(--counter<0)
		{
			counter=200;
//			ArrayList<TileEntityFerrisWheel>wheellist = createWheelList();
//			float[] floatlist = new float[wheellist.size()];
//			
//			for(int i=0; i<floatlist.length; ++i)floatlist[i] = wheellist.get(i).rotation;
//			MessageSyncWheelStC packet = new MessageSyncWheelStC(xCoord,yCoord,zCoord, floatlist);
//		    MFW_PacketHandler.INSTANCE.sendToAll(packet);
		    if(rootParentTile==this)worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	public void setSyncParent(int x, int y, int z, int treeidx)
	{
		TileEntityFerrisWheel tile = (TileEntityFerrisWheel) worldObj.getTileEntity(x, y, z);
		if(tile==null)return;
		tile = tile.getTileFromTreeIndex(treeidx);
		setSyncParent(tile);
	}
	public void setSyncParent(TileEntityFerrisWheel parent)
	{
		if(parent == null)return;
		isEnableSyncRot = true;
		parentSyncTile = parent;
		parentTileTreeIndex = getTreeIndexFromTile(parent);
		parentsyncX = parentSyncTile.xCoord;
		parentsyncY = parentSyncTile.yCoord;
		parentsyncZ = parentSyncTile.zCoord;
	}
	public void clearSyncParent()
	{
		parentSyncTile = null;
		parentTileTreeIndex = -1;
		parentsyncX = -1;
		parentsyncY = -1;
		parentsyncZ = -1;
		//isEnableSyncRot = false;
	}
	public void setSyncChild(TileEntityFerrisWheel child)
	{
		childSyncTileList.add(child);
	}
	public void clearOwnFromChildren()
	{
		for(TileEntityFerrisWheel tile : childSyncTileList)tile.clearSyncParent();
	}
	
	public boolean isRegisteredSyncParent()
	{
		return null != parentSyncTile;
	}
	
	
	@Override
	public void updateEntity()
	{
		if(parentTileTreeIndex != -1)
		{
			if(parentSyncTile==null)
			{
				setSyncParent(parentsyncX, parentsyncY, parentsyncZ, parentTileTreeIndex);
				if(parentSyncTile == null)return;
				parentSyncTile.setSyncChild(this);
			}
			if(parentSyncTile.isInvalid())
			{
				clearSyncParent();
			}
			return;
		}
		
		if(isEnableSyncRot==false) _updateEntity();
	}
	public void _updateEntity()
	{
		syncRot();

		isToggleNow = (byte) ((prevRSPower == rspower) ? 0 : rspower==0 ? -1 : 1 );
		prevRSPower = rspower;
		
		rotation.update();
		wheelSize.update();
		rotMiscFloat1.update();
		rotMiscFloat2.update();
		calcRotaion();

		blockAccess.updateTileEntity();
	    
		
		if(parentTile!=null)
		{
			prevPosX = posX;
			prevPosY = posY;
			prevPosZ = posZ;
			Vec3 pos;
			pos = MFW_Math.rotateAroundVector(ConnectPos.x, ConnectPos.y, ConnectPos.z, 0, 0, 1, Math.toRadians(-parentTile.rotation.get()));
			pos = MFW_Math.rotateAroundVector(pos.xCoord, pos.yCoord, pos.zCoord, 0, 1, 0, Math.toRadians(-parentTile.rotVar2));
			pos = MFW_Math.rotateAroundVector(pos.xCoord, pos.yCoord, pos.zCoord, 1, 0, 0, Math.toRadians(-parentTile.rotVar1));
			pos = MFW_Math.rotateAroundVector(pos.xCoord, pos.yCoord, pos.zCoord,
					rotvecMeta2_side.xCoord, rotvecMeta2_side.yCoord, rotvecMeta2_side.zCoord, Math.toRadians(-parentTile.rotMeta2_side));
			
			float parentSize = parentTile.wheelSize.get();
			posX = (float) pos.xCoord*parentSize + parentTile.posX;
			posY = (float) pos.yCoord*parentSize + parentTile.posY;
			posZ = (float) pos.zCoord*parentSize + parentTile.posZ;
//			MFW_Logger.info("x.px:"+posX+"."+prevPosX); 
		}
		
		updateChildren();
		
		for(int i=0; i<childSyncTileList.size(); ++i)
		{
			TileEntityFerrisWheel tile = childSyncTileList.get(i);
			tile._updateEntity();
			if(tile.isInvalid())
			{
//				MFW_Logger.debugInfo("child removed");
				childSyncTileList.remove(i);
			}
		}
		
		BlocksRep.update();
	}
	
	private void calcRotaion()
	{
		if(isEnableSyncRot)
		{
			if(parentSyncTile==null)
			{
				//isEnableSyncRot = false;
				return;
			}
			
			if(isSyncTargetSpeed)
			{
				rotSpeed = (parentSyncTile.rotSpeed*rotMiscFloat1.get()*getSpeedRatioFromRSFlag());
				rotation.add(rotSpeed);
				rotation.round();
			}
			else
			{
				rotation.set(parentSyncTile.rotation.get()*rotMiscFloat1.get()*getSpeedRatioFromRSFlag() + rotMiscFloat2.get());
				rotation.setPrev(parentSyncTile.rotation.getPrev()*rotMiscFloat1.get()*getSpeedRatioFromRSFlag() + rotMiscFloat2.get());
				rotation.round();
			}
			return;
		}

		if(isEnableStoryBoard)
		{
			if(getRSTrigger() == 1)storyboardManager.OnRSEnable();
			storyboardManager.RunAnimation();
		}
		
		rotSpeed *= (1f - rotResist);
		if(!stopFlag)rotSpeed += rotAccel*rotResist*getSpeedRatioFromRSFlag();

		
		if(isEnableSinConvert){
			speedTemp += rotSpeed;
			if(speedTemp > 180)speedTemp -= 360f;
			else if(speedTemp < -180)speedTemp += 360f;
			float sin = (float)Math.sin(Math.toRadians(speedTemp))
						*rotMiscFloat1.get()*getSpeedRatioFromRSFlag() 
						+ rotMiscFloat2.get();
			rotation.set(sin);
		}
		else{
//				speedTemp = rotSpeed;
			rotation.add(rotSpeed);
			rotation.round();
		}
		
		
//			break;
//		case rotFlag_Sin : 
//			rotSpeed += rotAccel*0.07*getSpeedRatioFromRSFlag();
//			if(rotSpeed>Math.PI)rotSpeed-=2*Math.PI;
//			else if(rotSpeed<-Math.PI)rotSpeed += 2*Math.PI;
//			rotation.set((float)Math.sin(rotSpeed)*rotMiscFloat1.get()*getSpeedRatioFromRSFlag() + rotMiscFloat2.get());
//			break;
//		case rotFlag_ComeAndGo : 
//			if(getRSTrigger() == 1)rotSpeed = 1;
//			if(rotResist > 0){
//				rotation.add(rotSpeed * rotAccel * getSpeedRatioFromRSFlag());
//				if(rotation.get() >= rotMiscFloat1.get()){
//					rotSpeed = 0; 
//					rotResist *= -1; 
//					rotation.set(rotMiscFloat1.get());
//				} 
//			}
//			else{
//				rotation.add(-rotSpeed * rotAccel * getSpeedRatioFromRSFlag());
//				if(rotation.get() <= rotMiscFloat2.get()){
//					rotSpeed = 0; 
//					rotResist *= -1;
//					rotation.set(rotMiscFloat2.get());
//				} 
//			}
//			break;
//		
//			
//		case rotFlag_Move_RsOnToggle :
//			if(getRSTrigger() == 1){
//				rotSpeed = 1;
//				rotResist = rotation.get() + rotMiscFloat1.get();
//				if(rotResist > 180f)
//				{
//					rotResist-=360f;
//					rotation.add(-360f);
//				}
//			}
//			rotation.add(rotSpeed * rotAccel * getSpeedRatioFromRSFlag());
//			if(rotation.get() >= rotResist){
//				rotSpeed = 0; 
//				rotation.set(rotResist);
//			}
//			break;
//		case rotFlag_Sync : 
//			if(parentSyncTile==null)return;
//			rotation.set(parentSyncTile.rotation.get()*rotMiscFloat1.get()*getSpeedRatioFromRSFlag() + rotMiscFloat2.get());
//			break;
//		}
	}
	
	private float getSpeedRatioFromRSFlag()
	{
//		public final byte rsFlag_Non = 0; //効果なし
//		public final byte rsFlag_StopWhenOn = 1; //ON時停止
//		public final byte rsFlag_StopWhenOff = 2; //OFF時停止
//		public final byte rsFlag_RatioPositive = 3; //大きいほど高倍率
//		public final byte rsFlag_RatioNegative = 4; //小さいほど高倍率
		switch(rsFlag)
		{
		case rsFlag_Non : return 1;
		case rsFlag_StopWhenOn : return (getRSPower() > 0.01) ? 0 : 1;
		case rsFlag_StopWhenOff : return (getRSPower() < 0.01) ? 0 : 1;
		case rsFlag_RatioPositive : return getRSPower();
		case rsFlag_RatioNegative : return 1f-getRSPower();
		}
		return -1;
	}
	
//	public float fixedRenderRotation(float tick)
//	{
//		return prevRotation + (rotation - prevRotation)*tick;
//	}

	public void render(double x, double y, double z, float partialtick)
	{
		float rot = rotation.getFix(partialtick);

		GL11.glRotated(rotMeta2_side, rotvecMeta2_side.xCoord, rotvecMeta2_side.yCoord, rotvecMeta2_side.zCoord);
		GL11.glRotatef(rotVar1, 1, 0, 0);
		GL11.glRotatef(rotVar2, 0, 1, 0);
		GL11.glRotatef(rot, 0, 0, 1);
		float size = wheelSize.getFix(partialtick);
		GL11.glScalef(size, size, size);
		GL11.glPushMatrix();
		
		//core
		GL11.glPushMatrix();
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		BlocksRep.renderCore();
		GL11.glPopMatrix();
		
		GL11.glRotated(rotConst_meta2, rotvecConst_meta2.xCoord, rotvecConst_meta2.yCoord, rotvecConst_meta2.zCoord);

		GL11.glPushMatrix();
		for(int i=0; i<copyNum; ++i)
		{
			GL11.glRotated(copyRotOffset, vecAxisRot.xCoord, vecAxisRot.yCoord, vecAxisRot.zCoord);
			GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
			BlocksRep.render();
			if(copyMode==1)renderChildren(x,y,z,partialtick);
			GL11.glTranslatef(0.5f, 0.5f, 0.5f);
		}
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		for(int i=0; i<copyNum; ++i)
		{
			GL11.glRotated(copyRotOffset, vecAxisRot.xCoord, vecAxisRot.yCoord, vecAxisRot.zCoord);
			GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
			BlocksRep.renderEntities(partialtick);
			GL11.glTranslatef(0.5f, 0.5f, 0.5f);
		}
		
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glRotated(rotConst_meta2, rotvecConst_meta2.xCoord, rotvecConst_meta2.yCoord, rotvecConst_meta2.zCoord);
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
	}
	
	public void renderPostPass(double x, double y, double z, float partialtick) 
	{
		
		float rot = rotation.getFix(partialtick);
		GL11.glRotated(rotMeta2_side, rotvecMeta2_side.xCoord, rotvecMeta2_side.yCoord, rotvecMeta2_side.zCoord);
		GL11.glRotatef(rotVar1, 1, 0, 0);
		GL11.glRotatef(rotVar2, 0, 1, 0);
		GL11.glRotatef(rot, 0, 0, 1);
		float size = wheelSize.getFix(partialtick);
		GL11.glScalef(size, size, size);
		GL11.glPushMatrix();
		GL11.glRotated(rotConst_meta2, rotvecConst_meta2.xCoord, rotvecConst_meta2.yCoord, rotvecConst_meta2.zCoord);
		
		GL11.glPushMatrix();
		for(int i=0; i<copyNum; ++i)
		{
			GL11.glRotated(copyRotOffset, vecAxisRot.xCoord, vecAxisRot.yCoord, vecAxisRot.zCoord);
			GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
			BlocksRep.renderPost();
			if(copyMode==1)renderChildrenPostPass(x,y,z,partialtick);
			GL11.glTranslatef(0.5f, 0.5f, 0.5f);
		}
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glRotated(rotConst_meta2, rotvecConst_meta2.xCoord, rotvecConst_meta2.yCoord, rotvecConst_meta2.zCoord);
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
	}
	
	public void renderForGUI(float f)
	{ 
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glTranslated(130, 140, 100);
        double s = (2d/(1d+Math.exp(-length*0.07))-1) * 150d/(double)length;
        GL11.glScaled(s,s,0.01);
//        GL11.glScaled(length*0.7, length*0.7, length*0.7);
        
		GL11.glRotatef(-15, 1, 0, 0);
		GL11.glRotatef(-35, 0, 1, 0);
		GL11.glRotated(rotMeta2_side, rotvecMeta2_side.xCoord, rotvecMeta2_side.yCoord, rotvecMeta2_side.zCoord);
		GL11.glRotatef(rotVar1, 1, 0, 0);
		GL11.glRotatef(rotVar2, 0, 1, 0);
		GL11.glRotatef(rotation.getFix(f), 0, 0, 1);
//		GL11.glScalef(wheelSize, wheelSize, wheelSize);
		GL11.glRotated(rotConst_meta2, rotvecConst_meta2.xCoord, rotvecConst_meta2.yCoord, rotvecConst_meta2.zCoord);
		GL11.glScaled(-1, -1, -1);
//		
		GL11.glPushMatrix();
		for(int i=0; i<copyNum; ++i)
		{
			GL11.glRotated(copyRotOffset, vecAxisRot.xCoord, vecAxisRot.yCoord, vecAxisRot.zCoord);
			GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
			BlocksRep.renderEntities(f);
			BlocksRep.render();
			BlocksRep.renderPost();
			GL11.glTranslatef(0.5f, 0.5f, 0.5f);
		}
		GL11.glPopMatrix();
		//core
		GL11.glPushMatrix();
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		BlocksRep.renderCore();
		GL11.glPopMatrix();
		
		GL11.glPopMatrix();
        GL11.glDepthFunc(GL11.GL_LEQUAL);
	}

	public void readChildFromNBT(NBTTagCompound nbt, int layer)
	{
		NBTTagList nbttaglist = nbt.getTagList("Items"+layer, 10);	// per2:タグ種類のID
		
		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbt1 = nbttaglist.getCompoundTagAt(i);
			int j = nbt1.getByte("Slot") & 255;
			if (j < 0 || j >= this.ItemStacks.length) continue;
            
			if(ItemStacks[j]==null)ItemStacks[j] = ItemStack.loadItemStackFromNBT(nbt1);
			if(ArrayEntityParts[j] == null)ValidatePart(ItemStacks[j].getTagCompound(), ItemStacks[j], j, false);

//			if(ArrayEntityParts[j].isTile())
//			{
//				TileEntityFerrisWheel tile = (TileEntityFerrisWheel) ArrayEntityParts[j];
			if(ArrayEntityParts[j] != null)
			{
				ArrayEntityParts[j].my_readFromNBT(nbt1, j);
//				constructFromTag(ItemStacks[j].getTagCompound(), meta);
				ArrayEntityParts[j].readChildFromNBT(nbt1, layer+1);
			}
		}	
	}
	
	public void my_readFromNBT(NBTTagCompound nbt, int snum)
	{
		wheelSize.Init(nbt.getFloat("wsize"));
		rotSpeed = nbt.getFloat("speed");
		if(MFW_Command.doSync)
		{
			rotation.set(nbt.getFloat("rot"));
			rotSpeed = nbt.getFloat("speed");
			speedTemp = nbt.getFloat("speedtemp");
		}
		rotAccel = nbt.getFloat("accel");
		rotResist = nbt.getFloat("resist");
		rotMiscFloat1.set(nbt.getFloat("rotMiscfloat1"));
		rotMiscFloat2.set(nbt.getFloat("rotMiscfloat2"));
		this.meta = nbt.getInteger("meta");
//		rotFlag = nbt.getByte("rotflag");
		isEnableSinConvert = nbt.getBoolean("enablesinconvert");
		isEnableStoryBoard = nbt.getBoolean("enablestoryboard");/* MFW_Logger.debugInfo("enablesb:"+isEnableStoryBoard+" ."+xCoord+"."+yCoord+"."+zCoord);*/
		isEnableSyncRot = nbt.getBoolean("enablesyncrot");
		isSyncTargetSpeed = nbt.getBoolean("synctargetspeed");
		rsFlag = nbt.getByte("rsflag");
		stopFlag = nbt.getBoolean("stopflag");
		rotVar1 = nbt.getFloat("rotVar1");
		rotVar2 = nbt.getFloat("rotVar2");
		isLock = nbt.getBoolean("islock");
		int slotnum = nbt.getByte("slotnum"+snum) & 255;
		if(ItemStacks == null || ItemStacks.length != slotnum)ItemStacks = new ItemStack[slotnum];
		// 同期用親の読み込み
		parentTileTreeIndex = nbt.getInteger("parentSyncTileIdx");
		if(parentTileTreeIndex != -1)
		{
			parentsyncX = nbt.getInteger("parentsyncx");
			parentsyncY = nbt.getInteger("parentsyncy");
			parentsyncZ = nbt.getInteger("parentsyncz");
		}
		storyboardManager.createFromSerialCode(nbt.getString("storyboard"));
		SetSoundIndex(nbt.getInteger("soundindex"));
	}
	
	public void readRootWheelFromNBT(NBTTagCompound nbt)
	{
		saveRootTag((NBTTagCompound) nbt.getTag("rootnbt"));
		if(rootTagNBTSave==null)return;
		constructFromTag(rootTagNBTSave , meta, false);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		super.readFromNBT(nbt);
		my_readFromNBT(nbt, 0);
		readRootWheelFromNBT(nbt);
		
		readChildFromNBT(nbt,1);
	}
	
	public void writeChildToNBT(NBTTagCompound nbt, int layer)
	{
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < this.ItemStacks.length; ++i)
		{  
			if (this.ItemStacks[i] != null)
			{
                NBTTagCompound nbt1 = new NBTTagCompound();
                nbt1.setByte("Slot", (byte)i);
//                if(ArrayEntityParts[i].isTile())
//                {
//                	TileEntityFerrisWheel tile = (TileEntityFerrisWheel) ArrayEntityParts[i];
//                }
                this.ItemStacks[i].writeToNBT(nbt1);
                if( ArrayEntityParts[i] != null)
                {
	                ArrayEntityParts[i].my_writeToNBT(nbt1,i);
	                ArrayEntityParts[i].writeChildToNBT(nbt1, layer+1);
                }
                nbttaglist.appendTag(nbt1);
			}
		}
		nbt.setTag("Items"+layer, nbttaglist);
	}
	
	public void my_writeToNBT(NBTTagCompound nbt, int slotidx)
	{
		nbt.setFloat("wsize", wheelSize.get());
		nbt.setFloat("speed", rotSpeed);
		nbt.setFloat("rot", rotation.get());
		nbt.setFloat("accel", rotAccel);
		nbt.setFloat("resist", rotResist);
		nbt.setFloat("speedtemp", speedTemp);
		nbt.setFloat("rotMiscfloat1",rotMiscFloat1.get());
		nbt.setFloat("rotMiscfloat2",rotMiscFloat2.get());
		nbt.setInteger("meta", this.meta);
//		nbt.setByte("rotflag", rotFlag);
		nbt.setBoolean("enablesinconvert", isEnableSinConvert);
		nbt.setBoolean("enablestoryboard", isEnableStoryBoard);
		nbt.setBoolean("enablesyncrot", isEnableSyncRot);
		nbt.setBoolean("synctargetspeed", isSyncTargetSpeed);
		nbt.setByte("rsflag", rsFlag);
		nbt.setBoolean("stopflag", stopFlag);
		nbt.setFloat("rotVar1", rotVar1);
		nbt.setFloat("rotVar2", rotVar2);
		nbt.setBoolean("islock", isLock);
		nbt.setByte("slotnum"+slotidx, (byte) ItemStacks.length);
		// 同期用親の保存
		if(parentSyncTile!=null)nbt.setInteger("parentSyncTileIdx", parentSyncTile.getTreeIndexFromTile(parentSyncTile));
		else nbt.setInteger("parentSyncTileIdx", -1);
		nbt.setInteger("parentsyncx", parentsyncX);
		nbt.setInteger("parentsyncy", parentsyncY);
		nbt.setInteger("parentsyncz", parentsyncZ);
		nbt.setString("storyboard", storyboardManager.getSerialCode()); 
		nbt.setInteger("soundindex", GetSoundIndex());

//		MFW_Logger.debugInfo("save : "+ WheelName + " : " + storyboardManager.getSerialCode());
	}
	
	public void writeRootWheelToNBT(NBTTagCompound nbt)
	{
		if(rootTagNBTSave == null)return;
		nbt.setTag("rootnbt", rootTagNBTSave); /////////////////////////////////////////////////
	}
	
	public void saveRootTag(NBTTagCompound nbt)
	{
		if(nbt == null)return;
		rootTagNBTSave = nbt;
	}
	
	@Override 
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		my_writeToNBT(nbt,0);
		writeRootWheelToNBT(nbt);
		writeChildToNBT(nbt,1);
	}

	/*
	 * 呼ばれるタイミングリストアップ
	 * ・ログイン時(チャンク読み込み時？)、GUIボタン押したとき（GUIのパケット受信時に自分で同期処理してるから？）、チャンクの保存or再読み込みした時
	 */
	@Override
	public Packet getDescriptionPacket()
	{
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        this.writeToNBT(nbtTagCompound);
//		if(modeltag == null)return null;
//		nbtTagCompound.setInteger("wheelmeta", this.meta);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
	}
 
	@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
        this.readFromNBT(pkt.func_148857_g());
//		modeltag = pkt.func_148857_g();
//        if(modeltag!=null)constructFromTag(modeltag, modeltag.getInteger("wheelmeta"));
    }
	
	/////////////////////////SidedInventry/////////////////////////////

	 
	@Override
	public int getSizeInventory(){return this.ItemStacks.length;}

	@Override
	public ItemStack getStackInSlot(int idx) 
	{
		if(ItemStacks==null)return null;
		if(ItemStacks.length==0)return null;
		return this.ItemStacks[idx];
	}

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
	public void setInventorySlotContents(int slotidx, ItemStack itemstack)
	{
		if(itemstack==null)return;
		if(this.ItemStacks[slotidx] != null)return;	//GUIを開いたときにも呼ばれるから、再構成を防ぐための処理
		this.ItemStacks[slotidx] = itemstack;
		
		//以下新たにパーツがスロットに入れられたとき　鯖のみItemStackスロットにデータを入れて同期
		if(worldObj.isRemote)return;
		
		//以下、鯖のみ
		if(itemstack.hasTagCompound() == false)return;
		NBTTagCompound nbt = ItemStacks[slotidx].getTagCompound();
		ValidatePart(nbt, itemstack, slotidx, true);
		//nbtでスロット情報同期
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		
	}
	
	@Override
	public String getInventoryName(){return "container.erc.ferriccore";}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {return 1;}

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
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_){return false;}

	@Override
	/**
     * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
     * block.
     * 自動搬入されるときのsideから入ってきたアイテムはslotの何番に入れるのか？
     */
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return new int[1];
    }

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
		return false;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
		return false;
	}
	
	public void onChunkUnload()
    {
		if(sound!=null)sound.Invalid();
    }
	
	///////////////////////////////////i_ferrispart
	
	public void init(TileEntityFerrisWheel parent, connectPos cp, int currentidx)
	{
		parentTile = parent;
		currentIdx = currentidx;
		currentLayer = parent.currentLayer+1;
		cp.copy(ConnectPos);
		posX = cp.x + 0.5f;
		posY = cp.y + 0.5f;
		posZ = cp.z + 0.5f;
		xCoord = parent.xCoord;
		yCoord = parent.yCoord;
		zCoord = parent.zCoord;
//		rootParentTile = (TileEntityFerrisWheel) worldObj.getTileEntity(xCoord, yCoord, zCoord);
		rootParentTile = getRootParent();
		wheelSize.Init(1.0f);
		isConstructed = false;
	}
	
	public boolean isTile()
	{
		return true;
	}
	
	public void constructFromTag(NBTTagCompound nbt, int side, int meta, int xCoord, int yCoord, int zCoord, boolean isMultiThread)
	{
		constructFromTag(nbt, meta, isMultiThread);
	}
	
	public void updateChildren()
	{
		for( I_FerrisPart p :ArrayEntityParts)
		{
			if(p!=null)
			{
				if(p.isTile())
				{
					((TileEntityFerrisWheel)p).updateEntity();
//					p.updateChildren();
				}
			}
		}
	}
	
	private void renderchildpre(double x, double y, double z, float f)
	{
//		double px = MFW_Math.Lerp(f,prevPosX,posX);
//		double py = MFW_Math.Lerp(f,prevPosY,posY);
//		double pz = MFW_Math.Lerp(f,prevPosZ,posZ);
//		GL11.glTranslated(x+0.5+px, y+0.5+py, z+0.5+pz);
		GL11.glTranslated(0.5+ConnectPos.x, 0.5+ConnectPos.y, 0.5+ConnectPos.z);
//		GL11.glTranslated(ConnectPos.x, ConnectPos.y, ConnectPos.z);
//		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
	}
	public void renderThis(double x, double y, double z, float f)
	{
		renderchildpre(x,y,z,f);
		this.render(x,y,z,f);
		if(copyMode==0)renderChildren(x,y,z,f);
	}
	public void renderChildren(double x, double y, double z, float f)
	{
		for( I_FerrisPart p :ArrayEntityParts)
		{
			if(p!=null)
			{
				GL11.glPushMatrix();
				p.renderThis(x, y, z, f);
				GL11.glPopMatrix();
			}
		}
	}
	
	public void renderThisPostPass(double x, double y, double z, float f)
	{
		renderchildpre(x,y,z,f);
		this.renderPostPass(x,y,z,f);
		if(copyMode==0)renderChildrenPostPass(x, y, z, f);
	}
	public void renderChildrenPostPass(double x, double y, double z, float f)
	{
		for( I_FerrisPart p :ArrayEntityParts)
		{
			if(p!=null)
			{
				GL11.glPushMatrix();
				p.renderThisPostPass(x, y, z, f);
				GL11.glPopMatrix();
			}
		}
	}
	
	public void dead(){}
	
}
