package mfw.block;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Core;
import mfw.item.SliceMeat.SyabuMeat;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class blockSyabuNabe extends Block{
	
	// block positions
	public static final double pbottom = 2f / 8f;
	public static final double ptop = 5.0f / 8.0f;
	public static final double psidebase = 1.0f / 8.0f;
	public static final double pwidth = 7.0f / 8.0f;
	public static final double pNabeSoko = 3.0 / 8.0;
	public static final double pwaterheight = 4.0f / 8.0f;
	
	@SideOnly(Side.CLIENT)
    private IIcon tex;
//	@SideOnly(Side.CLIENT)
//    private IIcon texInner;
//    @SideOnly(Side.CLIENT)
//    private IIcon texTop;
//    @SideOnly(Side.CLIENT)
//    private IIcon texBottom;

    public blockSyabuNabe()
    {
        super(Material.iron);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
//        return side == 1 ? this.texTop : (side == 0 ? this.texBottom : this.blockIcon);
        return tex;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
    	this.tex = register.registerIcon(this.getTextureName());
//        this.texInner = register.registerIcon(this.getTextureName() + "_inner");
//        this.texTop = register.registerIcon(this.getTextureName() + "_top");
//        this.texBottom = register.registerIcon(this.getTextureName() + "_bottom");
//        this.blockIcon = register.registerIcon(this.getTextureName() + "_side");
    }

    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List aabblist, Entity entity)
    {
        minX = psidebase;
        minY = pbottom;
        minZ = psidebase;
        maxX = pwidth;
        maxY = ptop;
        maxZ = pwidth;
        super.addCollisionBoxesToList(world, x, y, z, aabb, aabblist, entity);
        this.setBlockBoundsForItemRender();
    }

    @SideOnly(Side.CLIENT)
    public static IIcon getIcon(String iconname)
    {
//    	if(iconname.equals("inner"))
//    	{
//    		return ((blockSyabuNabe)MFW_Core.blockSyabuNabe).texInner;
//    	}
//    	else if(iconname.equals("bottom"))
//		{
//    		 return ((blockSyabuNabe)MFW_Core.blockSyabuNabe).texBottom;
//		}
    	return ((blockSyabuNabe)MFW_Core.blockSyabuNabe).tex;
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
    	minX = psidebase;
        minY = pbottom;
        minZ = psidebase;
        maxX = pwidth;
        maxY = ptop;
        maxZ = pwidth;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return MFW_Core.blockSyabuNabeId;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if (world.isRemote)
        {
            return true;
        }

        ItemStack itemstack = player.inventory.getCurrentItem();

        if (itemstack == null)
        {
            return true;
        }
        else
        {
            int nowmeta = world.getBlockMetadata(x, y, z);

            if (itemstack.getItem() == Items.water_bucket)
            {
                if (nowmeta == 0)
                {
                    if (!player.capabilities.isCreativeMode)
                    {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
                    }

                    this.updateByMetadata(world, x, y, z, 1);
                }

                return true;
            }
            else if(world.getBlock(x, y-1, z) == Blocks.fire) 
            {
//                if (itemstack.getItem() == Items.glass_bottle)
//                {
//                    if (nowmeta > 0)
//                    {
//                        if (!player.capabilities.isCreativeMode)
//                        {
//                            ItemStack itemstack1 = new ItemStack(Items.potionitem, 1, 0);
//
//                            if (!player.inventory.addItemStackToInventory(itemstack1))
//                            {
//                                world.spawnEntityInWorld(new EntityItem(world, (double)x + 0.5D, (double)y + 1.5D, (double)z + 0.5D, itemstack1));
//                            }
//                            else if (player instanceof EntityPlayerMP)
//                            {
//                                ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
//                            }
//
//                            --itemstack.stackSize;
//
//                            if (itemstack.stackSize <= 0)
//                            {
//                                player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
//                            }
//                        }
//
//                        this.updateByMetadata(world, x, y, z, nowmeta - 1);
//                    }
//                }
            	if(itemstack.getItem() instanceof SyabuMeat)
            	{
            		if(!itemstack.hasTagCompound())
            		{
            			itemstack.setTagCompound(new NBTTagCompound());
            		}
            		//else
            		{
            			NBTTagCompound tag = itemstack.getTagCompound();
            			int count = 1 + tag.getInteger("count");
            			if(count > 3)
            			{
            				int stacksize = itemstack.stackSize;
            				player.inventory.mainInventory[player.inventory.currentItem] = new ItemStack(((SyabuMeat)itemstack.getItem()).cooked(), stacksize);
            			}
            			else tag.setInteger("count", count);
            		}
            	}

            }
            return false;
        }
        
    }
    
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
    	if(world.getBlock(x, y-1, z) == Blocks.fire) 
    	{
    		world.spawnParticle("explode", x+0.5*random.nextDouble(), y+0.8, z+0.5*random.nextDouble(), 0.01*random.nextDouble(), 0.1, 0.01*random.nextDouble());
//    		world.spawnParticle("cloud", x+random.nextDouble(), y+0.5, z+random.nextDouble(), 0, 0.1, 0);
    	}
    }

    public void updateByMetadata(World world, int x, int y, int z, int meta)
    {
        world.setBlockMetadataWithNotify(x, y, z, MathHelper.clamp_int(meta, 0, 1), 2);
        world.func_147453_f(x, y, z, this);
    }

    /**
     * currently only used by BlockCauldron to incrament meta-data during rain
     */
    public void fillWithRain(World p_149639_1_, int p_149639_2_, int p_149639_3_, int p_149639_4_)
    {
        if (p_149639_1_.rand.nextInt(20) == 1)
        {
            int l = p_149639_1_.getBlockMetadata(p_149639_2_, p_149639_3_, p_149639_4_);

            if (l < 3)
            {
                p_149639_1_.setBlockMetadataWithNotify(p_149639_2_, p_149639_3_, p_149639_4_, l + 1, 2);
            }
        }
    }

//    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
//    {
//        return Items.cauldron;
//    }

    /**
//     * Gets an item for the block being called on. Args: world, x, y, z
//     */
//    @SideOnly(Side.CLIENT)
//    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
//    {
//        return Items.cauldron;
//    }

}
