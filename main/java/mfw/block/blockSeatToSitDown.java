package mfw.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Core;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class blockSeatToSitDown extends Block{

	@SideOnly(Side.CLIENT)
	private IIcon icon0;
	@SideOnly(Side.CLIENT)
	private IIcon iconU05; 
	
	public blockSeatToSitDown()
	{
		super(Material.ground);
		this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.1F, 0.9375F);
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
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
	{
		return meta;
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
	public boolean canCollideCheck(int meta, boolean flag)
	{
		return true;
	}
	
	@Override
	public IIcon getIcon(int side, int meta) 
	{
		return meta==0 ? icon0 : iconU05;
	}
	
	@Override
	public int damageDropped(int p_149692_1_)
    {
        return p_149692_1_;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.icon0 = iconRegister.registerIcon(MFW_Core.MODID+":SeatToSit");
		this.iconU05 = iconRegister.registerIcon(MFW_Core.MODID+":SeatToSit05");
	}
}
