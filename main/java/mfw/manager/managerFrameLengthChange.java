package mfw.manager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw.wrapper.Wrap_TileEntityChangeLimitWithKey;

@SideOnly(Side.CLIENT)
public class managerFrameLengthChange {
	
	private managerFrameLengthChange(){}
	
	public static managerFrameLengthChange INSTANCE = new managerFrameLengthChange();
	
	Wrap_TileEntityChangeLimitWithKey savetile;
	
	public void saveTile(Wrap_TileEntityChangeLimitWithKey tile)
	{
		savetile = tile;
	}
	
	public Wrap_TileEntityChangeLimitWithKey getSaveTile()
	{
		return savetile;
	}
	
	public void reset()
	{
		savetile = null;
	}
}
