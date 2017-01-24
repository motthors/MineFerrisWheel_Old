package mfw.block;

import mfw._core.MFW_Core;
import mfw.tileEntity.TileEntityFerrisCutter;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class blockFerrisCutter extends BlockContainer{

	public blockFerrisCutter()
	{
		super(Material.ground);
		this.setHardness(1.0F);
		this.setResistance(2000.0F);
	}

	@Override
	public int getRenderType()
	{
		return MFW_Core.blockCutterRenderId;
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) 
	{
		return new TileEntityFerrisCutter();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
		//OPEN GUI
		player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCutter, player.worldObj, x, y, z);
        return true;
    }
	
	
}
