package mfw.wrapper;

import mfw._core.connectPos;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.nbt.NBTTagCompound;

public interface I_FerrisPart {
	
	public void init(TileEntityFerrisWheel parent, connectPos cp, int currentidx);	// Core‚ÉƒZƒbƒg‚³‚ê‚½‚çŒÄ‚Ô
	public boolean isTile();
	public void constructFromTag(NBTTagCompound nbt, int side, int meta, int xCoord, int yCoord, int zCoord, boolean isMultiThread);
	public void completeConstruct(NBTTagCompound nbt);
	public void updateChildren();
	public void renderThis(double x, double y, double z, float f);
	public void renderThisPostPass(double x, double y, double z, float f);
	public void dead();
	public void writeChildToNBT(NBTTagCompound nbt, int layer);
	public void readChildFromNBT(NBTTagCompound nbt, int layer);
	public void my_readFromNBT(NBTTagCompound nbt, int snum);
	public void my_writeToNBT(NBTTagCompound nbt, int slotidx);
}
