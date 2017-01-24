package mfw.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class entityParts extends Entity {

	double ox,oy,oz;
	int bax,bay,baz; //blockaccess org position
	AxisAlignedBB absoluteAABB;
	entityFerrisBasket parent;
	
	public entityParts(World world) {
		super(world);
		setSize(1.0f, 1.0f);
		absoluteAABB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
	}
	
	public void setParent(entityFerrisBasket b)
	{
		parent = b;
	}

	public void setPosOffset(int x, int y, int z, int bax, int bay, int baz)
	{
		ox = x+0.5;
		oy = y;
		oz = z+0.5;
		this.bax = bax;
		this.bay = bay;
		this.baz = baz;
	}

	public boolean canBeCollidedWith()
    {
        return true;
    }
	public AxisAlignedBB getBoundingBox()
    {
        return worldObj.isRemote?boundingBox:null;
//		 return boundingBox;
    }
	
	public void setAbsoluteAABB(double minx, double miny, double minz, double maxx, double maxy, double maxz)
	{
		absoluteAABB.setBounds(minx, miny, minz, maxx, maxy, maxz);
	}
	public void setAbsoluteAABBTurnMeta(int meta, double minx, double miny, double minz, double maxx, double maxy, double maxz)
	{
//		double X = minx, Z = minz; TODO
//		double XX = maxx, ZZ = maxz;
//		switch(meta)
//		{
//		case 2 : break;
//		case 3 : minx = -X 
//		}
		setAbsoluteAABB(minx, miny, minz, maxx, maxy, maxz);
	}
	
    public void setPosition(double x, double y, double z)
    {
    	prevPosX = posX;
    	prevPosY = posY;
    	prevPosZ = posZ;
        this.posX = x + ox;
        this.posY = y + oy;
        this.posZ = z + oz;
        if(absoluteAABB==null)return;
        double wx = (absoluteAABB.minX+absoluteAABB.maxX)/2;
        double wz = (absoluteAABB.minZ+absoluteAABB.maxZ)/2;
        double hu = this.absoluteAABB.maxY;
        double hd = this.absoluteAABB.minY;
        this.boundingBox.setBounds(posX-wx, posY+hd, posZ-wz, posX+wx, posY+hu, posZ+wz);
    }
    
	@Override
	public boolean interactFirst(EntityPlayer player)
	{
		if(parent==null)return true;
		if(player.isSneaking()==false)return true;
//		if(worldObj.isRemote)return true;
		parent.openRootCoreGUI(player);
		return true;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {}

	@Override
	protected void entityInit() {}

}
