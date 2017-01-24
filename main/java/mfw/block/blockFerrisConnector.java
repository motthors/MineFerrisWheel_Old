package mfw.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class blockFerrisConnector extends Block{
	
	public blockFerrisConnector() {
		super(Material.glass);
		this.setHardness(0.3f);
		this.setResistance(2000.0F);
		this.setLightOpacity(0);
//		this.setBlockBounds(0.3F, 0.3F, 0.3F, 0.7F, 0.7F, 0.7F);
		this.setLightLevel(0.0F);
	}
	
	@Override
	public boolean isOpaqueCube() 
	{
		return false;
	}

	public int quantityDropped(Random p_149745_1_)
    {
        return 1;
    }
	
	public boolean renderAsNormalBlock()
    {
        return false;
    }
}
