package mfw.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class itemBlockSeatToSitDown extends ItemBlock{

	public itemBlockSeatToSitDown(Block block) 
	{
		super(block);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void getSubItems(Item item, CreativeTabs tab, List itemList)
	{
		itemList.add(new ItemStack(this, 1, 0));
		itemList.add(new ItemStack(this, 1, 1));
    }
	
	public int getMetadata(int damage)
    {
        return damage;
    }
}