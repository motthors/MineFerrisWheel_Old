package mfw.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Core;
import mfw.tileEntity.TileEntityFerrisBasketConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class blockFerrisBasketConstructor extends BlockContainer{
	
	private final Random random = new Random();
	@SideOnly(Side.CLIENT)
	private IIcon TopIcon;
	@SideOnly(Side.CLIENT)
	private IIcon SideIcon; 
	
	public blockFerrisBasketConstructor()
	{
		super(Material.ground);
		this.setHardness(1.0F);
		this.setResistance(2000.0F);
		this.setLightOpacity(0);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setLightLevel(0.0F);
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if(side==1 || side==0)return TopIcon;
		else return SideIcon;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.TopIcon = iconRegister.registerIcon(MFW_Core.MODID+":ferrisBasketConstructor");
		this.SideIcon = iconRegister.registerIcon(MFW_Core.MODID+":ferrisBasketConstructor_s");
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return true;
	}
	
	public TileEntityFerrisBasketConstructor getTileEntityInstance()
	{
		return new TileEntityFerrisBasketConstructor();
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {

		return (side==0)?false:true;
	}

	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
		//OPEN GUI
		player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisBasketConstructor, player.worldObj, x, y, z);
        return true;
    }

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemstack)
	{
		int l = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int meta=0;
		switch(l)
		{
        case 0: meta = 2; break;
        case 1: meta = 5; break;
        case 2: meta = 3; break;
        case 3: meta = 4; break;
        }
		world.setBlockMetadataWithNotify(x, y, z, meta, 2);
		
		// ブロック置いたらTile作る
		TileEntityFerrisBasketConstructor tile = getTileEntityInstance();
		
		{
			tile.calcFramePos(world,meta);
//			return;
		}
		if(!world.isRemote)
		world.setTileEntity(x, y, z, tile);
	}
	
	public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_)
    {
		TileEntityFerrisBasketConstructor tile = (TileEntityFerrisBasketConstructor)world.getTileEntity(x, y, z);

        if (tile != null)
        {
            for (int i1 = 0; i1 < tile.getSizeInventory(); ++i1)
            {
                ItemStack itemstack = tile.getStackInSlot(i1);

                if (itemstack != null)
                {
                    float f = this.random.nextFloat() * 0.8F + 0.1F;
                    float f1 = this.random.nextFloat() * 0.8F + 0.1F;
                    EntityItem entityitem;

                    for (float f2 = this.random.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; world.spawnEntityInWorld(entityitem))
                    {
                        int j1 = this.random.nextInt(21) + 10;

                        if (j1 > itemstack.stackSize)
                        {
                            j1 = itemstack.stackSize;
                        }

                        itemstack.stackSize -= j1;
                        entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));
                        float f3 = 0.05F;
                        entityitem.motionX = (double)((float)this.random.nextGaussian() * f3);
                        entityitem.motionY = (double)((float)this.random.nextGaussian() * f3 + 0.2F);
                        entityitem.motionZ = (double)((float)this.random.nextGaussian() * f3);

                        if (itemstack.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                        }
                    }
                }
            }

            world.func_147453_f(x, y, z, block);
        }

        super.breakBlock(world, x, y, z, block, p_149749_6_);
    }

	//	// ブロックが破壊されたら呼ばれる　
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int i)
	{	
//		if(!Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode)
//			this.dropBlockAsItem(world, x, y, z, new ItemStack(this));
	}
 
	//当たり判定。サボテンやソウルサンドを参考にすると良い。ココの設定をすると、onEntityCollidedWithBlockが呼ばれるようになる
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z)
	{
		return AxisAlignedBB.getBoundingBox(
				((double)x)+this.minX,((double)y)+this.minY,((double)z)+this.minZ,
				((double)x)+this.maxX,((double)y)+this.maxY,((double)z)+this.maxZ
				);
	}
//
//	//ブロックに視点を合わせた時に出てくる黒い線のアレ
//	@SideOnly(Side.CLIENT)
//	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int x, int y, int z)
//	{
//		return AxisAlignedBB.getBoundingBox(
//				((double)x)+this.minX,((double)y)+this.minY,((double)z)+this.minZ,
//				((double)x)+this.maxX,((double)y)+this.maxY,((double)z)+this.maxZ
//				);
//	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return getTileEntityInstance();
	}
}
