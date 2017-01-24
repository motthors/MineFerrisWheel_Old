package mfw._core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class MFW_CreateCreativeTab extends CreativeTabs{
	
	private ItemBlock IconItem;
	
	public MFW_CreateCreativeTab(String label, ItemBlock item)
	{
		super(label);
		IconItem = item;
	}
 
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem()
	{
		return IconItem;
	}
 
	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel()
	{
		return "MineFerrisWheel";
	}
}
