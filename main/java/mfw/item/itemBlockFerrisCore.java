package mfw.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class itemBlockFerrisCore extends ItemBlock{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean advanced)
	{
		if(itemStack.stackTagCompound == null)return;
        list.add("name : "+itemStack.stackTagCompound.getString("MFWOName"));
        list.add("author : "+itemStack.stackTagCompound.getString("author"));
    }
	
	public itemBlockFerrisCore(Block block)
	{
		super(block);
		setContainerItem(this);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		if(stack.hasTagCompound() == false)return true;
		return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) 
	{
		return 10;
	}
	
	
	
//	private boolean canPlaceItemBlockOnSide(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack)
//	{
//		Block block = world.getBlock(x, y, z);
//		if (block == Blocks.snow_layer)
//		{
//			side = 1;
//		}
//		else if ((block == null) || (!block.isReplaceable(world, x, y, z)))
//		{
//			switch(side)
//			{
//			case 0: y--; break;
//			case 1: y++; break;
//	    	case 2: z--; break;
//	    	case 3: z++; break;
//	    	case 4: x--; break;
//	    	case 5: x++; break;
//			}
//		}
//		return world.canPlaceEntityOnSide(Block.getBlockFromItem(stack.getItem()), x, y, z, false, side, (Entity)null, stack);
//	}

//	private void constructFromTag(World world, int x, int y, int z, int side, NBTTagCompound tag)
//	{
//		switch(side)
//		{
//		case 0: y--; break;
//		case 1: y++; break;
//    	case 2: z--; break;
//    	case 3: z++; break;
//    	case 4: x--; break;
//    	case 5: x++; break;
//		}
//		
//		Block setBlock = world.getBlock(x, y, z);
//		if (!(setBlock instanceof blockFerrisCore))return;
//		
//		TileEntityFerrisWheel tile = (TileEntityFerrisWheel) world.getTileEntity(x, y, z);
////		TileEntityFerrisWheel tile = (TileEntityFerrisWheel) setBlock.createTileEntity(world, world.getBlockMetadata(x, y, z));
//		tile.xCoord = x;
//		tile.yCoord = y;
//		tile.zCoord = z;
//		tile.saveRootTag(tag);
////		tile.my_readFromNBT(tag, 0);
////		tile.readRootWheelFromNBT(tag);
////		tile.readChildFromNBT(tag,1);
//		tile.constructFromTag(tag,world.getBlockMetadata(x, y, z));
//	}
	
//	@Override
//	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
//			float hitX, float hitY, float hitZ, int metadata) 
//	{
//		TileEntityFerrisWheel tile = (TileEntityFerrisWheel) world.getTileEntity(x, y, z);
//		if(stack.hasTagCompound()==false)return true;
//		NBTTagCompound nbt = stack.getTagCompound();
//    	tile.my_readFromNBT(nbt, 0);
//		tile.readRootWheelFromNBT(nbt);
//		tile.readChildFromNBT(nbt,1);
//		return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
//	}

}
