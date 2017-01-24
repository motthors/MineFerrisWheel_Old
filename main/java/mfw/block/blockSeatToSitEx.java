package mfw.block;

import mfw._core.MFW_Core;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class blockSeatToSitEx extends Block {

	public blockSeatToSitEx()
	{
		super(Material.ground);
		this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.125F, 0.9375F);
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        return null;
    }
	
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) 
	{
		return side == 1;
	}
	
	@Override
	public boolean isOpaqueCube() 
	{
		return false;
	}
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override
	public int getRenderType()
	{
		return MFW_Core.blockSeatExId;
	}
	
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemstack)
	{
		int l = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int meta=l;
//		switch(l)
//		{
//        case 0: meta = 2; break;
//        case 1: meta = 5; break;
//        case 2: meta = 3; break;
//        case 3: meta = 4; break;
//        }
//		MFW_Logger.debugInfo("blockseatex : "+meta);
		world.setBlockMetadataWithNotify(x, y, z, meta, 2);
	}
}
