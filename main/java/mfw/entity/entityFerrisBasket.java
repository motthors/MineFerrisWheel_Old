package mfw.entity;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import MTYlib.blocksReplication.BlocksReplication;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Core;
import mfw._core.connectPos;
import mfw.block.blockSeatToSitDown;
import mfw.item.itemBlockRemoteController;
import mfw.math.MFW_Math;
import mfw.tileEntity.TileEntityFerrisWheel;
import mfw.util.MFWBlockAccess;
import mfw.wrapper.I_FerrisPart;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class entityFerrisBasket extends Entity implements I_FerrisPart{

	String BasketName;
	TileEntityFerrisWheel parentTile;
	public int currentWheelIdx=-1;
	public int currentSlotIdx=-1;
	float theta;
	float angleOffset;
	int connectOffsetX;
	int connectOffsetY;
	int connectOffsetZ;
	float rotCorrectX;
	float rotCorrectY;
	float rotCorrectZ;
	float rotOffsetX;
	float rotOffsetY;
	float rotOffsetZ;
	connectPos ConnectPos = new connectPos();
	entityParts[] partsArray;
	entityPartSit[] partSitArray;
	int decompressSize;
	byte[] compressedModelData;
	int spawnFlagSlotIdx = -1;
	int constructSide;
	int parentMeta = -1;
	float rot2;
	boolean updateFlag = false;
	
	public class entitySelector implements IEntitySelector{
		@Override
		public boolean isEntityApplicable(Entity entity)
		{
			boolean flag = (entity instanceof entityFerrisBasket)
					|| (entity instanceof entityParts)
					|| (entity instanceof entityPartSit)
					|| (entity instanceof entityPartSitEx)
					|| entity.isRiding();
			return !flag;
		}
	}
	private entitySelector selector = new entitySelector();
	
	public entityFerrisBasket(World world)
	{
		super(world);
		setSize(2.0f, 1.0f);
		
		this.setPosition(0, 2, 0);
		this.prevPosX = 0;
        this.prevPosY = 2;
        this.prevPosZ = 0;
        
        theta = 0;
        
        renderDistanceWeight = Double.MAX_VALUE;
        this.preventEntitySpawning = true;
        partsArray = new entityParts[]{new entityParts(world)};
        partSitArray = new entityPartSit[]{new entityPartSit(world)};
        
        blockAccess = new MFWBlockAccess(worldObj, parentTile);
        BlocksRep = new BlocksReplication(worldObj, blockAccess);
	}

	@Override
	protected void entityInit()
	{
		dataWatcher.addObject(19, new Float(0));
		dataWatcher.addObject(20, new Float(10));
		dataWatcher.addObject(21, new Integer(0)); // offsetcorex
		dataWatcher.addObject(22, new Integer(0)); // offsetcorey
		dataWatcher.addObject(23, new Integer(0)); // offsetcorez
		dataWatcher.addObject(24, new Float(0)); // entitywidth
		dataWatcher.addObject(25, new Float(0)); // entityheight
		dataWatcher.addObject(26, new Integer(-1)); // wheelnum
		dataWatcher.addObject(27, new Integer(-1)); // slotnum
	}
	
	public Entity[] getParts()
	{
		return this.partsArray;
    }
	
	public entityPartSit[] getPartsSit()
	{
		return this.partSitArray;
    }
	public boolean setPartsSit(int idx, entityPartSit e)
	{
		if(partSitArray == null)return false;
		if(partSitArray.length <= 0 || partSitArray.length <= idx)return false;
		partSitArray[idx] = e;
		return true;
    }
	
	public AxisAlignedBB getBoundingBox()
    {
//        if(worldObj.isRemote)
//        	return boundingBox;
//        else
        	return null;
    }
	@Override
    public boolean canBeCollidedWith()
    {
        return false;//!this.isDead;
    }
	
	@Override
	public float getBrightness(float p_70013_1_)
	{
		return 15 << 20 | 15 << 4;
	}
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float p_70070_1_)
    {
		return 15 << 20 | 15 << 4;
//        int i = MathHelper.floor_double(this.posX);
//        int j = MathHelper.floor_double(this.posZ);
//
//        if (this.worldObj.blockExists(i, 0, j))
//        {
//            double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
//            int k = MathHelper.floor_double(this.posY - (double)this.yOffset + d0);
//            return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
//        }
//        else
//        {
//            return 0;
//        }
    }

	
	@Override
    protected boolean canTriggerWalking() {return false;}
	
	public void onChunkLoad() {} //何かに使えるかも
	
	public boolean attackEntityFrom(DamageSource ds, float p_70097_2_)
    {
//    	boolean flag = ds.getEntity() instanceof EntityPlayer;// && ((EntityPlayer)ds.getEntity()).capabilities.isCreativeMode;
//
//	    if (flag)
//	    {
//	        setDead();
//	    }
        
    	return true;
    }

	@Override
	public boolean interactFirst(EntityPlayer player) 
	{
		if(player.getHeldItem()==null)return true;
		if(player.getHeldItem().getItem() instanceof itemBlockRemoteController)
		{
			if(player.getHeldItem().hasTagCompound()==false)return true;
			NBTTagCompound nbt = player.getHeldItem().getTagCompound();
			if(nbt.hasKey("mfrremotecontroller"))
			{
				if(parentTile==null)return true;
				TileEntityFerrisWheel tile = parentTile.getRootParent();
				if(tile==null)return true;
				nbt.setInteger("mfwcontrollerX", tile.xCoord);
				nbt.setInteger("mfwcontrollerY", tile.yCoord);
				nbt.setInteger("mfwcontrollerZ", tile.zCoord);
			}
		}
		return true;
	}

	public void setPositionToRoot(TileEntityFerrisWheel tile, Vec3 posInOut)
	{
		posInOut.xCoord *= tile.wheelSize;
		posInOut.yCoord *= tile.wheelSize;
		posInOut.zCoord *= tile.wheelSize;
		MFW_Math.rotateAroundVector(posInOut,
				tile.rotvecConst_meta2.xCoord, tile.rotvecConst_meta2.yCoord, tile.rotvecConst_meta2.zCoord,
				Math.toRadians(-tile.rotConst_meta2));
		
		MFW_Math.rotateAroundVector(posInOut, 0, 0, 1, Math.toRadians(-tile.rotation));

		MFW_Math.rotateAroundVector(posInOut, 0, 1, 0, Math.toRadians(-tile.rotVar2));
		MFW_Math.rotateAroundVector(posInOut, 1, 0, 0, Math.toRadians(-tile.rotVar1));
	
		MFW_Math.rotateAroundVector(posInOut,
				tile.rotvecMeta2_side.xCoord, tile.rotvecMeta2_side.yCoord, tile.rotvecMeta2_side.zCoord,
				Math.toRadians(-tile.rotMeta2_side));
		
		
		TileEntityFerrisWheel nextparent = tile.getParentTile(); 
		if(nextparent != null)
		{
			posInOut.xCoord += tile.ConnectPos.x;// * nextparent.wheelSize;
			posInOut.yCoord += tile.ConnectPos.y;// * nextparent.wheelSize;
			posInOut.zCoord += tile.ConnectPos.z;// * nextparent.wheelSize;
			setPositionToRoot(nextparent, posInOut);
		}
	}
	
	private void setPosition()
	{
//		setPosition(
//				getBaseX()+Math.cos(getAngle()+rotOffsetX)*getLength()*rotCorrectX,      //connectOffsetX+
//				getBaseY()+Math.cos(getAngle()+rotOffsetY)*getLength()*rotCorrectY,      //connectOffsetY+
//				getBaseZ()+Math.cos(getAngle()+rotOffsetZ)*getLength()*rotCorrectZ);     //connectOffsetZ+e
		
		Vec3 pos = Vec3.createVectorHelper(ConnectPos.x, ConnectPos.y, ConnectPos.z);
		setPositionToRoot(parentTile, pos);
//		MFW_Math.rotateAroundVector(pos,
//				parentTile.rotvecMeta2_side.x, parentTile.rotvecMeta2_side.y, parentTile.rotvecMeta2_side.z,
//				Math.toRadians(-parentTile.rotMeta2_side));
//		MFW_Math.rotateAroundVector(pos,
//				parentTile.vecAxisRot.x, parentTile.vecAxisRot.y, parentTile.vecAxisRot.z,
//				Math.toRadians(-parentTile.rotation));
		
		
		this.posX = pos.xCoord/**parentTile.wheelSize + parentTile.posX */+ getBaseX();
		this.posY = pos.yCoord/**parentTile.wheelSize + parentTile.posY */+ getBaseY();
		this.posZ = pos.zCoord/**parentTile.wheelSize + parentTile.posZ */+ getBaseZ();
		double sx = posX - connectOffsetX ;
		double sy = posY - connectOffsetY ;
		double sz = posZ - connectOffsetZ ;
		this.boundingBox.setBounds(sx, sy, sz, sx+blocklenX, sy+blocklenY + 1, sz+blocklenZ); 
		
		for(entityParts part : partsArray)
		{
			part.setPosition(posX, posY, posZ);
		}
//		for(entityPartSit part : partSitArray)if(part!=null)
//		{
//			part.setPosition();
//		}
	}
	
	private boolean CheckParent()
	{
//		if(worldObj.isRemote==false)return false;
		int x = getBaseX();
		int y = getBaseY();
		int z = getBaseZ();
		TileEntityFerrisWheel tile = (TileEntityFerrisWheel) worldObj.getTileEntity(x, y, z);
		if(tile==null)return false;
		TileEntityFerrisWheel parent = tile.getTileFromTreeIndex(getWheelIdx());
		if(parent==null)return false;
		if(parent.ArrayEntityParts.length <= getSlotIdx())return false;
		if(parent.ArrayEntityParts[getSlotIdx()]==null)parent.ArrayEntityParts[getSlotIdx()] = this;
		parentTile = parent;
		parent.ValidatePart_clientBasket(getSlotIdx(), this);
		return true;
	}
	
	@Override
	public void onUpdate()
	{
//		setDead();
		
		if(parentTile==null)
		{
			if(!CheckParent())
			{
				setDead();
				return;
			}
		}
		if(parentTile.isInvalid())
		{
			setDead();
			return;
		}
//		if(spawnFlag == false)
//		{
//			setDead();
//			return;
//		}
		
		super.onUpdate();
		theta += 0.05;
		if(theta>Math.PI*2)theta -= Math.PI*2;
		float rot = parentTile.rotation;
//		if(worldObj.isRemote)rot+=2f;
		setAngle((float) Math.toRadians(rot)+angleOffset);
		setPosition();
		
		
//		if(posY - prevPosY < 0)return;
		
//		boolean flag = false;
		@SuppressWarnings("unchecked")
		List<Entity> elist = worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox, selector);
		if(!worldObj.isRemote)return;
		for(Entity e : elist)
		{
			if(worldObj.isRemote)
			{
				e.setPosition(e.posX + posX - prevPosX,
						e.posY + (posY - prevPosY) * (((posY - prevPosY)>=0)?1.1:0.9) + 0.0001,
						e.posZ + posZ - prevPosZ);
			}
			else 
			{
				e.motionY=0d;
				e.moveEntity(posX - prevPosX,
						(posY - prevPosY)* (((posY - prevPosY)>0)?1.1:0.9),
						posZ - prevPosZ);
			}
			
//			if(posY - prevPosY < 0)ERC_Logger.info("flag");
//			ERC_Logger.info("done");
		}
//		ERC_Logger.info("posx:"+posX+".y"+posY+".z"+posZ+".angle:"+parentTile.rotation);
//		ERC_Logger.info("basket:"+boundingBox.toString());
	}
	
	@Override
	public void setDead()
	{
		super.setDead();
//		MFW_Logger.debugInfo("basket dead,  call from : "+(worldObj.isRemote?"client":"server")); 
	}

	@Override
	public void setPositionAndRotation2(double x, double y, double z, float ry, float rp, int p_70056_9_) {}

	public void render(double x, double y, double z, float partialtick)
	{
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
//		GL11.glDisable(GL11.GL_CULL_FACE); // カリングOFF
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
//		GL11.glTranslatef(-length-0.5f, -length-0.5f, -width-0.5f);	
		
//		GL11.glPushMatrix();
		GL11.glTranslatef(0.5f, 0.5f, 0.5f);
//		GL11.glRotatef(rot3, 1, 0, 0);
		GL11.glRotatef(rot2, 0, 1, 0);
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		BlocksRep.renderCore();
		BlocksRep.render();
		BlocksRep.renderPost(); //TODO
		blockAccess.renderTileEntity(partialtick);
//		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
//		GL11.glRotatef(-rot2, 0, 1, 0);
//		GL11.glRotatef(-rot3, 1, 0, 0);
//		GL11.glTranslatef(0.5f, 0.5f, 0.5f);
//		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
	private void setTileRotParam(int side)
	{
		rot2 = 0f;
		switch(side){
		case 2 :
			switch(parentMeta){
			case 3 : rot2 = 180f; break;
			case 4 : rot2 = 90f; break;
			case 5 : rot2 = -90f;  break;
			}break;
		case 3 :
			switch(parentMeta){
			case 2 : rot2 = 180f; break;
			case 4 : rot2 = -90f; break;
			case 5 : rot2 = 90f;  break;
			}break;
		case 4 :
			switch(parentMeta){
			case 2 : rot2 = -90f; break;
			case 3 : rot2 = 90f;  break;
			case 5 : rot2 = 180f; break;
			}break;
		case 5 :
			switch(parentMeta){
			case 2 : rot2 = 90f; break;
			case 3 : rot2 = -90f;  break;
			case 4 : rot2 = 180f; break;
			}break;
		}
	}
	
	public void openRootCoreGUI(EntityPlayer player)
	{
		player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, worldObj, getBaseX(), getBaseY(), getBaseZ());
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		setBasePos(nbt.getInteger("tilex"),nbt.getInteger("tiley"),nbt.getInteger("tilez"));
		setWheelIdx(nbt.getInteger("wheelidx"));
		setSlotIdx(nbt.getInteger("slotidx"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("tilex", getBaseX());
		nbt.setInteger("tiley", getBaseY());
		nbt.setInteger("tilez", getBaseZ());
		nbt.setInteger("wheelidx", getWheelIdx());
		nbt.setInteger("slotidx", getSlotIdx());
	}
	
	private void setAngle(float angle)
	{
		dataWatcher.updateObject(19, Float.valueOf(angle));
	}
	
//	private float getAngle()
//	{
//		return dataWatcher.getWatchableObjectFloat(19);
//	}
	
	private void setLength(float len)
	{
		dataWatcher.updateObject(20, Float.valueOf(len));
	}
	
//	private float getLength()
//	{
//		return dataWatcher.getWatchableObjectFloat(20);
//	}
	
	private void setBasePos(int x, int y, int z)
	{
		dataWatcher.updateObject(21, Integer.valueOf(x));
		dataWatcher.updateObject(22, Integer.valueOf(y));
		dataWatcher.updateObject(23, Integer.valueOf(z));
	}
	
	private int getBaseX(){return dataWatcher.getWatchableObjectInt(21);}
	private int getBaseY(){return dataWatcher.getWatchableObjectInt(22);}
	private int getBaseZ(){return dataWatcher.getWatchableObjectInt(23);}
	
	public void setWheelIdx(int idx){dataWatcher.updateObject(26, Integer.valueOf(idx));}
	private int getWheelIdx(){return dataWatcher.getWatchableObjectInt(26);}
	public void setSlotIdx(int idx){dataWatcher.updateObject(27, Integer.valueOf(idx));}
	private int getSlotIdx(){return dataWatcher.getWatchableObjectInt(27);}
	
	// model renderer
	MFWBlockAccess blockAccess;
//	public rendererFerrisBasket BlocksVertex = MFW_Core.proxy.getrendererFerrisBasket(blockAccess,0,0,0f,0f,0f);
	BlocksReplication BlocksRep;
	
	private int blocklenX,blocklenY,blocklenZ;
	public void constructFromTag(NBTTagCompound nbt, int parentcside, int parentmeta, int xCoord, int yCoord, int zCoord, boolean isMultiThread)
	{
		if(worldObj==null)return;
//		if(isConstructed)return;
		constructSide = nbt.getByte("constructormetaflag") & 7;
		this.parentMeta = parentmeta;
//		BlocksRep = new BlocksReplication(worldObj, blockAccess);
		connectOffsetX = nbt.getInteger("connectoffsetx");
		connectOffsetY = nbt.getInteger("connectoffsety");
		connectOffsetZ = nbt.getInteger("connectoffsetz");
		
		blocklenX = nbt.getInteger("mtybr:sizex");
		blocklenY = nbt.getInteger("mtybr:sizey");
		blocklenZ = nbt.getInteger("mtybr:sizez");

		BlocksRep.setWorld(worldObj);
		BlocksRep.setCorePosition(xCoord, yCoord, zCoord);
		BlocksRep.constructFromTag(nbt, parentMeta, this, false);
		BasketName = nbt.getString("MFWOName");


        /////////////////////////////////////
	}
	
	public void completeConstruct(NBTTagCompound nbt)
	{
		setTileRotParam(BlocksRep.getSide());
        ArrayList<entityParts> entitypartList = new ArrayList<entityParts>();
        ArrayList<entityPartSit> listPartsSit = new ArrayList<entityPartSit>();

        
        //partsArray用当たり判定設定
        for(int x=0;x<blocklenX;++x){
			for(int y=0;y<blocklenY;++y){
				for(int z=0;z<blocklenZ;++z){
					Block b = blockAccess.getBlockOrgPos(x, y, z);
					//液体なら当たり判定なし
					if(b.getMaterial().isLiquid())continue;
					//空気ブロックは当たり判定なし
					if(Block.getIdFromBlock(b)==0)continue;
					//シートの当たり判定は消す
					if(b.canCollideCheck(blockAccess.getBLockMetadata_AbsolutePos(x, y, z), false)==false)continue;
					//上にシートがあったら別枠でEntity登録するのでPartとしての当たり判定は消す
					if(blockAccess.getBlockOrgPos(x, y+1, z) instanceof blockSeatToSitDown)
					{
//						MFW_Logger.debugInfo("b:"+blockAccess.getBlockOrgPos(x, y+1, z)+"meta:"+blockAccess.getBLockMetadata_AbsolutePos(x, y+1, z));
						entityPartSit e = new entityPartSit(worldObj, this, blockAccess.getBLockMetadata_AbsolutePos(x, y+1, z));
//						e.setPosOffset(x-connectOffsetX, y-connectOffsetY, z-connectOffsetZ, parentMeta);
//						listPartsSit.add(e);
						addEntityPart(listPartsSit, e, x-connectOffsetX, y-connectOffsetY, z-connectOffsetZ, constructSide, parentMeta, true);
						continue;
					}
					addEntityPart(entitypartList, new entityParts(worldObj), x-connectOffsetX, y-connectOffsetY, z-connectOffsetZ, constructSide, parentMeta, false);
				}
			}
	    }
        // entitypart set
        partsArray = new entityParts[entitypartList.size()];
        int i=0;
        for(entityParts e : entitypartList)
        {
        	Block b = blockAccess.getBlock(e.bax+BlocksRep.getCTMX(), e.bay+BlocksRep.getCTMY(), e.baz+BlocksRep.getCTMZ());
        	b.setBlockBoundsBasedOnState(blockAccess, e.bax, e.bay, e.baz);
        	ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
        	AxisAlignedBB aabb;
        	try{
        		b.addCollisionBoxesToList(worldObj, 0, 0, 0, TileEntity.INFINITE_EXTENT_AABB, list, this);
        		aabb = b.getCollisionBoundingBoxFromPool(worldObj, 0, 0, 0);
        	}catch(Exception exception){aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);}
        	if(aabb!=null)e.setAbsoluteAABB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
        	e.setParent(this);
        	partsArray[i++] = e;
        }
        entitypartList.clear();
        
        //entitypartSit set
        partSitArray = new entityPartSit[listPartsSit.size()];
        i=0;
        if(worldObj.isRemote == false)
        {
	        for(entityPartSit e : listPartsSit)
	        {
	        	e.setMiscData(getBaseX(), getBaseY(), getBaseZ(), getSlotIdx(), getWheelIdx(), i);
	        	partSitArray[i++] = e;
	        	 worldObj.spawnEntityInWorld(e);
	        }
        }
        fixEntityLength(parentMeta);
	}
	
	@SuppressWarnings("unchecked")
	private void addEntityPart(ArrayList<?> list, Entity e, int x, int y, int z, int constructside, int parentmeta, boolean isSeat)
	{
		int X=0, Z=0;
		switch(constructside)
		{
//		case 0:	case 1: こない
		case 2: X = x; Z = z; break;
		case 3: X = -x; Z =-z; break;
		case 4: X =-z; Z = x; break;
		case 5: X = z; Z =-x; break;
		}
		int XX=X, ZZ=Z;
		switch(parentmeta)
		{
		case 0: case 1: X = x; Z = z; break;
		case 2: X = XX; Z = ZZ; break;
		case 3: X = -XX; Z =-ZZ; break;
		case 4: X = ZZ; Z =-XX; break;
		case 5: X =-ZZ; Z = XX; break;
		}
		if(isSeat)
		{
			entityPartSit p = (entityPartSit) e;
			p.setPosOffset(X, y, Z);
			((ArrayList<entityPartSit>)list).add(p);
		}
		else
		{
			entityParts p = (entityParts) e;
			p.setPosOffset(X, y, Z, x, y, z);
			((ArrayList<entityParts>)list).add(p);
		}
	}
	
	public void fixEntityLength(int parentmeta)
	{
		int constructside = BlocksRep.getSide();
		if(parentmeta <= 1)return;
		int X, Z;
		int rotnum = fixrotforconnect(constructside) - fixrotforconnect(parentmeta);
		if(rotnum<0)rotnum+=4;
		for(int i=0; i<rotnum; ++i)
		{
			X=connectOffsetX; Z=connectOffsetZ;
			connectOffsetX = Z;
			connectOffsetZ = blocklenX-X-1;
			X=blocklenX; Z=blocklenZ;
			blocklenX = Z; blocklenZ = X;
		}
	}
	private int fixrotforconnect(int i){
		switch(i){case 2: return 0; case 4: return 3; case 3: return 2; case 5: return 1; default: return 0;}
	}
	
	//////////////////////////////////////////// wrap_ferrispart
	
	public void init(TileEntityFerrisWheel parent, connectPos cp, int currentidx)
	{
		parentTile = parent;
		setSlotIdx(currentidx);
		setWheelIdx(parent.getTreeIndexFromTile(parent));
		angleOffset = cp.angle;
		cp.copy(ConnectPos);
//		fixConnectorWithParentMeta(parent.meta);
//		MFW_Logger.debugInfo("debug connectpos : x:"+cp.x+", y:"+cp.y+", z:"+cp.z);
		setLength(cp.len);
		setBasePos(parent.xCoord,parent.yCoord,parent.zCoord);
		setPosition();
		switch(parent.meta)
		{
		case 0: rotCorrectX = 1; rotCorrectY = 0; rotCorrectZ = 1; break;
		case 1: rotCorrectX = 1; rotCorrectY = 0; rotCorrectZ =-1; break;
		case 2: rotCorrectX = 1; rotCorrectY = 1; rotCorrectZ = 0; break;
		case 3: rotCorrectX =-1; rotCorrectY = 1; rotCorrectZ = 0; break;
		case 4: rotCorrectX = 0; rotCorrectY =-1; rotCorrectZ =-1; break;
		case 5: rotCorrectX = 0; rotCorrectY = 1; rotCorrectZ = 1; break;
		}
		switch(parent.meta)
		{
		case 0: rotOffsetX = 0; rotOffsetY = 0; rotOffsetZ =(float)(-Math.PI/2d); break;
		case 1: rotOffsetX = 0; rotOffsetY = 0; rotOffsetZ = (float)(Math.PI/2d); break;
		case 2: rotOffsetX = 0; rotOffsetY =(float)(-Math.PI/2d); rotOffsetZ = 0; break;
		case 3: rotOffsetX = 0; rotOffsetY = (float)(Math.PI/2d); rotOffsetZ = 0; break;
		case 4: rotOffsetX = 0; rotOffsetY = 0; rotOffsetZ =(float)(-Math.PI/2d); break;
		case 5: rotOffsetX = 0; rotOffsetY = 0; rotOffsetZ = (float)(Math.PI/2d); break;
		}
	}
	public boolean isTile()
	{
		return false;
	}
	public void updateChildren()
	{
		return;
	}
	public void renderChildren(double x, double y, double z, float f)
	{
		return;
	}

	@Override
	public void renderChildrenPostPass(double x, double y, double z, float f)
	{
		return;
	}
	public void dead()
	{
		setDead();
		if(worldObj.isRemote)BlocksRep.invalidate();
	}

	@Override
	public void writeChildToNBT(NBTTagCompound nbt, int layer) {}

	@Override
	public void readChildFromNBT(NBTTagCompound nbt, int layer) {}
	
	@Override
	public void my_readFromNBT(NBTTagCompound nbt, int snum) 
	{
		parentMeta = nbt.getInteger("meta");
		compressedModelData = nbt.getByteArray("compressedbytearray");
	}

	@Override
	public void my_writeToNBT(NBTTagCompound nbt, int slotidx) 
	{
		nbt.setInteger("meta", parentMeta);
		nbt.setInteger("decompresssize", decompressSize);
		if(compressedModelData!=null)nbt.setByteArray("compressedbytearray", compressedModelData);
	}

}
