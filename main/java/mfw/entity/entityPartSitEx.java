package mfw.entity;

import erc.manager.ERC_CoasterAndRailManager;
import erc.math.ERC_MathHelper;
import mfw.math.MFW_Math;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class entityPartSitEx extends entityPartSit {

	AxisAlignedBB absoluteAABB;
	TileEntityFerrisWheel parenttile;
	int counterForSearchParent = 0;
//	int ConnectPosX;
//	int ConnectPosY;
//	int ConnectPosZ;
	Vec3 vx = Vec3.createVectorHelper(0, 0, 0);
	Vec3 vy = Vec3.createVectorHelper(0, 0, 0);
	Vec3 vz = Vec3.createVectorHelper(0, 0, 0);
	public float rotationRoll;
	public float prevRotationRoll;
	
	//for render
	public double renderroty, renderrotyPrev;
	public double renderrotp, renderrotpPrev;
	public double renderrotr, renderrotrPrev;
	
	public entityPartSitEx(World world) 
	{
		super(world);
	}

	public entityPartSitEx(World world, TileEntityFerrisWheel parent, int wheelmeta, float cpx, float cpy, float cpz, int seatAngle) 
	{
		super(world);
		setSize(1.0f, 1.0f);
		absoluteAABB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
		parenttile = parent;
		setSeatHeight(-0.3f);
		posX = parent.xCoord;
		posY = parent.yCoord;
		posZ = parent.zCoord;
		setSeatAngle(seatAngle);
		setMiscData(parent.xCoord, parent.yCoord, parent.zCoord, parent.getTreeIndexFromTile(parent));
		setPosOffset(cpx, cpy, cpz);
		setVecViewFromMeta(seatAngle, parent);
	}
	
	@Override
	public double getMountedYOffset()
    {
		return 0;
    }
	
	@Override
	public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_,
			float p_70056_8_, int p_70056_9_) {
	}

	public boolean interactFirst(EntityPlayer player)
    {
    	if(parenttile==null)return true;
    	//何か乗ってる　＋　プレイヤーが座ってる　＋　右クリックしたプレイヤーと違うプレイヤーが座ってる
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
        {
            return true;
        }
        //何かが乗ってる　＋　右クリックしたプレイヤー以外の何かが乗ってる
        else if (this.riddenByEntity != null && this.riddenByEntity != player)
        {
        	//おろす
        	riddenByEntity.mountEntity((Entity)null);
        	riddenByEntity = null;
            return true;
        }
        //何かが乗ってる　自分かもしれない
        else if (this.riddenByEntity != null)
        {
        	return true;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
            	ERC_CoasterAndRailManager.resetViewAngles();
                player.mountEntity(this);
            }
            return true;
        }
    }
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
		dataWatcher.addObject(11, new Integer(0));	// seatmeta
//		dataWatcher.addObject(12, new Float(0f));	// offsetX
//		dataWatcher.addObject(13, new Float(0f));	// offsetY
//		dataWatcher.addObject(14, new Float(0f));	// offsetZ
//		dataWatcher.addObject(15, new Integer(0));	// offsetX
//		dataWatcher.addObject(16, new Integer(0));	// 
//		dataWatcher.addObject(17, new Integer(0));	// 
//		dataWatcher.addObject(18, new Integer(0));	// 
//		dataWatcher.addObject(19, new Integer(0));	// 
//		dataWatcher.addObject(20, new Float(0f));	// seatheight
	}
	
	public void setPosOffset(float x, float y, float z)
	{
		setOffsetX(x);
		setOffsetY(y-1);
		setOffsetZ(z);  // metaで回転されてない？
	}
	
	public void setMiscData(int bax, int bay, int baz, int wheelidx)
	{
		setBasePos(bax, bay, baz);
//		setSlotIdx(currentidx);
		setWheelIdx(wheelidx);
	}
	
	public void setPositionToRoot(TileEntityFerrisWheel tile, Vec3 posInOut)
	{
		float size = tile.wheelSize.get();
		posInOut.xCoord *= size;
		posInOut.yCoord *= size;
		posInOut.zCoord *= size;
		MFW_Math.rotateAroundVector(posInOut,
				tile.rotvecConst_meta2.xCoord, tile.rotvecConst_meta2.yCoord, tile.rotvecConst_meta2.zCoord,
				Math.toRadians(-tile.rotConst_meta2));
		
		MFW_Math.rotateAroundVector(posInOut, 0, 0, 1, Math.toRadians(-tile.rotation.get()));

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
	
	@Override
	protected void setPosition()
    {
    	prevPosX = posX;
    	prevPosY = posY;
    	prevPosZ = posZ;
    	if(parenttile==null)return;
    	
    	Vec3 pos = Vec3.createVectorHelper(getOffsetX(), getOffsetY(), getOffsetZ());
    	setPositionToRoot(parenttile, pos);
    	this.posX = pos.xCoord/**parentTile.wheelSize + parentTile.posX */+ getBaseX() +0.5;// + getOffsetX();
		this.posY = pos.yCoord/**parentTile.wheelSize + parentTile.posY */+ getBaseY() +0.5;// + getOffsetY();
		this.posZ = pos.zCoord/**parentTile.wheelSize + parentTile.posZ */+ getBaseZ() +0.5;// + getOffsetZ();

        double wx = 0.5;//(absoluteAABB.minX+absoluteAABB.maxX)/2;
        double wz = 0.5;//(absoluteAABB.minZ+absoluteAABB.maxZ)/2;
        double hu = 0.5;//this.absoluteAABB.maxY;
//        double hd = 0.0;//this.absoluteAABB.minY;
        this.boundingBox.setBounds(posX-wx, posY-hu, posZ-wz, posX+wx, posY+hu, posZ+wz);
        
//        if(worldObj.isRemote)MFW_Logger.debugInfo("seat setPosition y:"+posY);
//    	if(waitUpdateRiderFlag)super.updateRiderPosition();
//        if(riddenByEntity!=null)updateRiderPosition();
    }
	
	@Override
	protected boolean CheckParent()
	{
		if(parenttile!=null)return true;
		int x = getBaseX();
		int y = getBaseY();
		int z = getBaseZ();
		TileEntityFerrisWheel tile = (TileEntityFerrisWheel) worldObj.getTileEntity(x, y, z);
		if(tile==null)return false;
		TileEntityFerrisWheel parent = tile.getTileFromTreeIndex(getWheelIdx());
		if(parent==null)return false;
		this.parenttile = parent;
		setVecViewFromMeta(getSeatAngle(), parent);
		return true;
//		return this.parenttile.setPartsSit(getSlotIdx(), this);
	}
	
	@Override
	public void setDead()
	{
		//hock
//		MFW_Logger.debugInfo("dead");
		super.setDead();
	}
	
	@Override
	public void onUpdate()
	{
    	if(parenttile == null)
    	{
    		if(!CheckParent())
			{
    			counterForSearchParent+=1;
    			if(counterForSearchParent>=100)
				{
    				setDead();
				}
    			return;
			}
    	}
    	if(parenttile.isInvalid())
    	{
    		setDead();
    		return;
    	}
//    	super.onUpdate();
    	setPosition();
	}
	
	
	public void setVecViewFromMeta(int seatAngle, TileEntityFerrisWheel parenttile)
	{
		vx = Vec3.createVectorHelper(-1, 0.001, 0.001);
		vy = Vec3.createVectorHelper(-0.001, 1, -0.001);
		vz = Vec3.createVectorHelper(0.0001, 0.001, -1);
		
		double rot = Math.toRadians(seatAngle);
		vx = ERC_MathHelper.rotateAroundVector(vx, vy, rot);
		vz = ERC_MathHelper.rotateAroundVector(vz, vy, rot);
	}
	
	public void transformVec3ToRootTile(TileEntityFerrisWheel tile, Vec3 vx, Vec3 vy, Vec3 vz)
	{
		double drot;
		drot = -Math.toRadians(tile.rotConst_meta2);
		Vec3 _vx = ERC_MathHelper.rotateAroundVector(vx, tile.rotvecConst_meta2, drot);
		Vec3 _vy = ERC_MathHelper.rotateAroundVector(vy, tile.rotvecConst_meta2, drot);
		Vec3 _vz = ERC_MathHelper.rotateAroundVector(vz, tile.rotvecConst_meta2, drot);
		
		drot = Math.toRadians(tile.rotation.get());// - parenttile.prevRotation;
		Vec3 vecrot = Vec3.createVectorHelper(0, 0, -1);
		_vx = ERC_MathHelper.rotateAroundVector(_vx, vecrot, drot);
		_vy = ERC_MathHelper.rotateAroundVector(_vy, vecrot, drot);
		_vz = ERC_MathHelper.rotateAroundVector(_vz, vecrot, drot);
		
		drot = Math.toRadians(tile.rotVar2);// - parenttile.prevRotation;
		vecrot.xCoord = 0; vecrot.yCoord = -1; vecrot.zCoord = 0;
		_vx = ERC_MathHelper.rotateAroundVector(_vx, vecrot, drot);
		_vy = ERC_MathHelper.rotateAroundVector(_vy, vecrot, drot);
		_vz = ERC_MathHelper.rotateAroundVector(_vz, vecrot, drot);
		drot = Math.toRadians(tile.rotVar1);// - parenttile.prevRotation;
		vecrot.xCoord = -1; vecrot.yCoord = 0; vecrot.zCoord = 0;
		_vx = ERC_MathHelper.rotateAroundVector(_vx, vecrot, drot);
		_vy = ERC_MathHelper.rotateAroundVector(_vy, vecrot, drot);
		_vz = ERC_MathHelper.rotateAroundVector(_vz, vecrot, drot);
		
		drot = -Math.toRadians(tile.rotMeta2_side);
		_vx = ERC_MathHelper.rotateAroundVector(_vx, tile.rotvecMeta2_side, drot);
		_vy = ERC_MathHelper.rotateAroundVector(_vy, tile.rotvecMeta2_side, drot);
		_vz = ERC_MathHelper.rotateAroundVector(_vz, tile.rotvecMeta2_side, drot);
		
		vx.xCoord = _vx.xCoord; vx.yCoord = _vx.yCoord; vx.zCoord = _vx.zCoord;
		vy.xCoord = _vy.xCoord; vy.yCoord = _vy.yCoord; vy.zCoord = _vy.zCoord;
		vz.xCoord = _vz.xCoord; vz.yCoord = _vz.yCoord; vz.zCoord = _vz.zCoord;
		if(tile.getParentTile() != null)
		{
			transformVec3ToRootTile(tile.getParentTile(), vx, vy, vz);
		}
	}
	
	
	@Override
	public void updateRiderPosition()
	{
//		if(true)return;
		if(parenttile==null)return;
    	if (this.riddenByEntity != null)
        {
    		waitUpdateRiderFlag = false;
    		// 基準軸回転
    		Vec3 vx = Vec3.createVectorHelper(this.vx.xCoord, this.vx.yCoord, this.vx.zCoord);
    		Vec3 vy = Vec3.createVectorHelper(this.vy.xCoord, this.vy.yCoord, this.vy.zCoord);
    		Vec3 vz = Vec3.createVectorHelper(this.vz.xCoord, this.vz.yCoord, this.vz.zCoord);
    		transformVec3ToRootTile(parenttile, vx, vy, vz);
//    		vz = vx.crossProduct(vy);
    		
        	{
    			////////////// プレイヤー回転量計算
    			// ViewYaw回転ベクトル　dir1->dir_rotView, cross->turnCross
    			Vec3 dir_rotView = ERC_MathHelper.rotateAroundVector(vz, vy, Math.toRadians(ERC_CoasterAndRailManager.rotationViewYaw));
    			Vec3 turnCross = ERC_MathHelper.rotateAroundVector(vx, vy, Math.toRadians(ERC_CoasterAndRailManager.rotationViewYaw));
    			// ViewPitch回転ベクトル dir1->dir_rotView
    			Vec3 dir_rotViewPitch = ERC_MathHelper.rotateAroundVector(dir_rotView, turnCross, Math.toRadians(ERC_CoasterAndRailManager.rotationViewPitch));
    			// pitch用 dir_rotViewPitchの水平ベクトル
    			Vec3 dir_rotViewPitchHorz = Vec3.createVectorHelper(dir_rotViewPitch.xCoord, 0, dir_rotViewPitch.zCoord);
    			// roll用turnCrossの水平ベクトル
    			Vec3 crossHorzFix = Vec3.createVectorHelper(0, 1, 0).crossProduct(dir_rotViewPitch);
    			if(crossHorzFix.lengthVector()==0.0)crossHorzFix=Vec3.createVectorHelper(1, 0, 0);
		
    			// yaw OK
//    			prevRotationYaw = rotationYaw;
    			rotationYaw = (float) -Math.toDegrees( Math.atan2(dir_rotViewPitch.xCoord, dir_rotViewPitch.zCoord) );

    			// pitch OK
//    			prevRotationPitch = rotationPitch;
    			rotationPitch = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(dir_rotViewPitch, dir_rotViewPitchHorz) * (dir_rotViewPitch.yCoord>=0?-1f:1f) );
    			if(Float.isNaN(rotationPitch))
    				rotationPitch=0;
    			
    			// roll
    			prevRotationRoll = rotationRoll;
    			rotationRoll = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(turnCross, crossHorzFix) * (turnCross.yCoord>=0?1f:-1f) );
    			if(Float.isNaN(rotationRoll))
    				rotationRoll=0;
    		}
    		prevRotationYaw = ERC_MathHelper.fixrot(rotationYaw, prevRotationYaw);
    		prevRotationPitch = ERC_MathHelper.fixrot(rotationPitch, prevRotationPitch);
    		prevRotationRoll = ERC_MathHelper.fixrot(rotationRoll, prevRotationRoll);
    		
    		this.riddenByEntity.rotationYaw = this.rotationYaw;
    		this.riddenByEntity.rotationPitch = this.rotationPitch;
    		this.riddenByEntity.prevRotationYaw = this.prevRotationYaw;
    		this.riddenByEntity.prevRotationPitch = this.prevRotationPitch; 
    		
    		double toffsety = 0.3 + this.riddenByEntity.getYOffset();

    		this.riddenByEntity.setPosition(
    				this.posX + vy.xCoord*toffsety, 
    				this.posY + vy.yCoord*toffsety, 
    				this.posZ + vy.zCoord*toffsety);
            
            
            if(worldObj.isRemote)
            {
            	renderrotyPrev = renderroty;
            	renderrotpPrev = renderrotp;
            	renderrotrPrev = renderrotr;
            	renderroty = -Math.toDegrees( Math.atan2(vz.xCoord, vz.zCoord) );
            	renderrotp = Math.toDegrees( ERC_MathHelper.angleTwoVec3(vz, Vec3.createVectorHelper(vz.xCoord, 0, vz.zCoord)) * (vz.yCoord>=0?-1f:1f) );
            	renderrotr = Math.toDegrees( ERC_MathHelper.angleTwoVec3(vx, Vec3.createVectorHelper(0, 1, 0).crossProduct(vz)) * (vx.yCoord>=0?1f:-1f) );
//            	MFW_Logger.debugInfo("seatex : "+renderrotr);
            	EntityLivingBase el = (EntityLivingBase) this.riddenByEntity;
            	el.renderYawOffset = (float) renderroty;
            	if(riddenByEntity == Minecraft.getMinecraft().thePlayer)
            		el.rotationYawHead = ERC_CoasterAndRailManager.rotationViewYaw + el.renderYawOffset;
//            	el.head
            }
            
        }
//    	ERC_CoasterAndRailManager.setRotRoll(rotationRoll, prevRotationRoll);
	}   
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		setBasePos(nbt.getInteger("tilex"),nbt.getInteger("tiley"),nbt.getInteger("tilez"));
		setSeatAngle(nbt.getInteger("seatmeta"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) 
	{
		super.writeEntityToNBT(nbt);
		nbt.setInteger("tilex", getBaseX());
		nbt.setInteger("tiley", getBaseY());
		nbt.setInteger("tilez", getBaseZ());
		nbt.setInteger("seatmeta", getSeatAngle());
	}
	
	public void setSeatAngle(int d){dataWatcher.updateObject(11, Integer.valueOf(d));}
	public int getSeatAngle(){return dataWatcher.getWatchableObjectInt(11);}
}
