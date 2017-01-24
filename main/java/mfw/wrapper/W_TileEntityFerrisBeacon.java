package mfw.wrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.world.IBlockAccess;

public class W_TileEntityFerrisBeacon extends TileEntityBeacon{

	float power = 0;
	
	public W_TileEntityFerrisBeacon(IBlockAccess ba, Block underOfThis)
	{
		if(underOfThis.isBeaconBase(ba, 0, 0, 0, 0, 0, 0)) power = 1;
	}
	
	@SideOnly(Side.CLIENT)
    public float func_146002_i()
    {
        return power;
    }
}
