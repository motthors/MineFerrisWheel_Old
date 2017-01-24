package mfw.block;

import mfw.tileEntity.tileEntityRemoteController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class blockFerrisRemoteController extends BlockContainer {

	public blockFerrisRemoteController() 
	{
		super(Material.ground);
		this.setHardness(0.4F);
		this.setResistance(2000.0F);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new tileEntityRemoteController();
	}
	
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack is)
	{
		super.onBlockPlacedBy(world, x, y, z, player, is); 

		// ブロック置いたらTile作る
		tileEntityRemoteController tile=null;
		tile = (tileEntityRemoteController) world.getTileEntity(x, y, z);
		if(tile==null) tile = (tileEntityRemoteController) createNewTileEntity(world, 0);
		if(is.hasTagCompound()==false)return;
		NBTTagCompound tag = is.getTagCompound();
		if(tag.hasKey("mfwcontrollerX")==false)return;
		tile.remotex = tag.getInteger("mfwcontrollerX");
		tile.remotey = tag.getInteger("mfwcontrollerY");
		tile.remotez = tag.getInteger("mfwcontrollerZ");
		world.setTileEntity(x, y, z, tile);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) 
	{
//		if(player.getHeldItem()!=null && player.getHeldItem().getItem() instanceof itemBlockRemoteController)return true;
		tileEntityRemoteController tile = (tileEntityRemoteController) world.getTileEntity(x, y, z);
		if(tile==null)return true;
		Block remotedblock = world.getBlock(tile.remotex, tile.remotey, tile.remotez);
		if(remotedblock == null)return true;
		return remotedblock.onBlockActivated(world, tile.remotex, tile.remotey, tile.remotez, player, p_149727_6_, p_149727_7_,
				p_149727_8_, p_149727_9_);
	}

}
