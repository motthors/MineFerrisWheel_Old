package mfw.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Core;
import mfw.tileEntity.TileEntityFileManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class blockFileManager extends BlockContainer{
	
	@SideOnly(Side.CLIENT)
	private IIcon TopIcon;
	@SideOnly(Side.CLIENT)
	private IIcon SideIcon; 
	
	public blockFileManager()
	{
		super(Material.ground);
		this.setHardness(1.0F);
		this.setResistance(2000.0F);
		this.setLightOpacity(0);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setLightLevel(0.0F);
	}
	
//	@SideOnly(Side.CLIENT)
//	public IIcon getIcon(int side, int meta)
//	{
//		if(side==0)return TopIcon;
//		else return SideIcon;
//	}
//
//	@Override
//	@SideOnly(Side.CLIENT)
//	public void registerBlockIcons(IIconRegister iconRegister)
//	{
//		this.TopIcon = iconRegister.registerIcon(MFW_Core.MODID+":ferrisConstructor");
//		this.SideIcon = iconRegister.registerIcon(MFW_Core.MODID+":ferrisConstructor_s");
//	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) 
	{
		return new TileEntityFileManager();
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
		//OPEN GUI
		player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisFileManager, player.worldObj, x, y, z);
        return true;
    }
	
	public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_)
    {
		TileEntityFileManager tile = (TileEntityFileManager)world.getTileEntity(x, y, z);

        if (tile != null)
        {
            ItemStack itemstack = tile.getStackInSlot(1);

            if (itemstack != null)
            {
                EntityItem entityitem;
                entityitem = new EntityItem(world, (double)x, (double)y, (double)z, itemstack);
                entityitem.motionX = 0;
                entityitem.motionY = 0.2;
                entityitem.motionZ = 0;
                
                if (itemstack.hasTagCompound())
                {
                    entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                }
                
            }
//            world.func_147453_f(x, y, z, block);
        }
        super.breakBlock(world, x, y, z, block, p_149749_6_);
    }
	
}
