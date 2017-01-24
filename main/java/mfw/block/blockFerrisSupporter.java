package mfw.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class blockFerrisSupporter extends Block{

	public blockFerrisSupporter()
	{
		super(Material.ground);
		this.setHardness(1.0f);
		this.setResistance(2000.0F);
		this.setLightOpacity(0);
		this.setBlockBounds(0.1875F, 0.1875F, 0.1875F, 0.8125F, 0.8125F, 0.8125F);
		this.setLightLevel(0.0F);
	}
	
	@Override
	public boolean isOpaqueCube() 
	{
		return false;
	}
	
	public boolean renderAsNormalBlock()
	{
		return false;
	}
}
