package mfw.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class entityPartsTestBase extends Entity{

	entityParts[] partsArray;
	
	public entityPartsTestBase(World world)
	{
		super(world);
		setSize(10.0f, 10f);
		partsArray = new entityParts[]{new entityParts(world),new entityParts(world),new entityParts(world)};
	}
	
	public entityPartsTestBase(World world, double x, double y, double z)
	{
		this(world);
		setPosition(x, y, z);
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean canBeCollidedWith()
    {
        return false;
    }
	public AxisAlignedBB getBoundingBox()
    {
        return null;
    }
	
	public void onUpdate() 
	{
//		setDead();
//		ERC_Logger.info(boundingBox.toString());
		partsArray[0].onUpdate();
		partsArray[0].setPosition(posX+3f, posY+3f, posZ);
		partsArray[1].onUpdate();
		partsArray[1].setPosition(posX-3f, posY+3f, posZ);
		
		partsArray[2].onUpdate();
		partsArray[2].setPosition(posX, posY, posZ);
	}
	
	public Entity[] getParts()
	{
		return this.partsArray;
    }

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
		// TODO Auto-generated method stub
		
	}
}
