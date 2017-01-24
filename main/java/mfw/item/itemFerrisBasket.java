package mfw.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class itemFerrisBasket extends Item{

	public itemFerrisBasket()
	{
		super();
		setContainerItem(this);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean advanced)
	{
		if(itemStack.stackTagCompound == null)return;
        list.add("BasketName:"+itemStack.stackTagCompound.getString("MFWOName"));
//     	list.add("name:"+itemStack.getDisplayName());
        list.add("BasketAuthor:"+itemStack.stackTagCompound.getString("author"));
	}
	
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
//    	if (!world.isRemote)
//    	{
//    		Entity e = new entityFerrisBasket(world);
//    		world.spawnEntityInWorld(e);
//    	}
//    	--itemStack.stackSize;
    	return true;
    }
	
}
