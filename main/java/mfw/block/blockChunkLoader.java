package mfw.block;

import mfw.tileEntity.TileEntityChunkLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class blockChunkLoader extends BlockContainer {
	
	public blockChunkLoader()
	{
		super(Material.ground);
	}
	
	public TileEntity createNewTileEntity(World world, int meta) 
	{
		return new TileEntityChunkLoader();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b,int m) 
	{
		((TileEntityChunkLoader)world.getTileEntity(x, y, z)).stopChunkLoading();
		super.breakBlock(world, x, y, z, b, m);
	}

	
}