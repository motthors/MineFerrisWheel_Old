package mfw.util;

import MTYlib.blocksReplication.BlocksCompressor;
import mfw.wrapper.IMultiThreadBlockCopy;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class multiThreadBlockCopier extends Thread{

	IMultiThreadBlockCopy tile;
	BlocksCompressor copier;
	World world;
	boolean isDrawEntity;
	
	public multiThreadBlockCopier(IMultiThreadBlockCopy tile, World world)
	{
		this.tile = tile;
		this.world = world;
		copier = new BlocksCompressor();
	}
	
	public void setSrcPosition(int minx, int miny, int minz, int maxx, int maxy, int maxz)
	{
		copier.setSrcPosition(minx, miny, minz, maxx, maxy, maxz);
	}

	public void setDrawEntityFlag(boolean flag)
	{
		isDrawEntity = flag;
	}
	
	@Override
	public void run() 
	{
		progress();
	}
	
	private void progress()
	{
		NBTTagCompound nbt = new NBTTagCompound();
    	copier.makeTag(nbt, world);
    	if(isDrawEntity)copier.SearchEntities(nbt, world);
    	if(tile!=null)tile.Completing(nbt);
	}

	//外から呼ばれるためrun,progress関数とは別スレッド
	public float getProgress()
	{
		return (float)copier.getNowBlockArrayIndex() / (float)copier.getAllBlockNum();
	}
	
	public BlocksCompressor getBlocksCompressor()
	{
		return copier;
	}
}
