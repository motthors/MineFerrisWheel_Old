package mfw.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class itemBlockRemoteController extends ItemBlock{

	public itemBlockRemoteController(Block block) 
	{
		super(block);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void getSubItems(Item item, CreativeTabs tab, List itemList)
	{
        ItemStack itemStack = new ItemStack(this, 1, 0);

        NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("mfwremotecontroller", 42);
		itemStack.setTagCompound(nbt);
		
        itemList.add(itemStack);//クリエイティブタブのアイテムリストに追加
    }
	
	@Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer player)
	{
        NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("mfwremotecontroller", 42);
		itemStack.setTagCompound(nbt);
    }

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ, int metadata) 
	{
		ItemStack is = player.getHeldItem();
		if(is == null)return false;
		if((is.getItem() instanceof itemBlockRemoteController)==false)return false;
		if(is.hasTagCompound()==false)return false;
		NBTTagCompound tag = is.getTagCompound();
		if(tag.hasKey("mfwcontrollerX"))
		{
			return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
		}
		return true;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer p_77648_2_, World world, int p_77648_4_,
			int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) 
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag == null || tag.hasKey("mfwcontrollerX") == false)
		{
			if(world.isRemote)
			{
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
	       				StatCollector.translateToLocal("message.remotecon.notag")
       				));
			}
			return true;
		}
		
		return super.onItemUse(stack, p_77648_2_, world, p_77648_4_, p_77648_5_, p_77648_6_, p_77648_7_, p_77648_8_,
				p_77648_9_, p_77648_10_);
	}

	public boolean onBlockActivatedWithController(EntityPlayer player, int x, int y, int z)
	{
		ItemStack is = player.getHeldItem();
		if(is == null)return false;
		if((is.getItem() instanceof itemBlockRemoteController)==false)return false;
		if(is.hasTagCompound()==false)return false;
		NBTTagCompound tag = is.getTagCompound();
		if(tag.hasKey("mfwremotecontroller"))
		{
			tag.setInteger("mfwcontrollerX", x);
			tag.setInteger("mfwcontrollerY", y);
			tag.setInteger("mfwcontrollerZ", z);
			
			return true;
		}
		return false;
	}
}
