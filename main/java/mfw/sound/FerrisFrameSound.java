package mfw.sound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Logger;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class FerrisFrameSound extends MovingSound {
	final TileEntityFerrisWheel tile;
	final EntityPlayer player;

	public FerrisFrameSound(TileEntityFerrisWheel tile, EntityPlayer player, String domain)
	{
	    super(new ResourceLocation(domain));
	    this.tile = tile;
	    this.player = player;
	    this.field_147666_i = ISound.AttenuationType.NONE;
	    this.repeat = true;
	    this.field_147665_h = 0;
	    this.field_147663_c = 10.7f;
	    this.volume = 0.01f;
	}

	public void update() 
	{
		if (!tile.isInvalid())
		{
			float distance = 0.05f * (float) player.getDistanceSq(tile.posX+tile.xCoord, tile.posY+tile.yCoord, tile.posZ+tile.zCoord);
			float v = Math.abs(tile.rotSpeed) * 0.1f;
			float f = v/ (distance + v);
			if (f >= 0.00001D) 
			{
				this.volume = (MathHelper.clamp_float(f, 0.0F, 1.0F));
				this.field_147663_c = 0.86f + Math.min(v*0.1f, 5.5f);
//				MFW_Logger.debugInfo(field_147663_c+" : "+ v);
			} 
			else 
			{
				this.volume = 0.0F;
			}
		} 
		else
		{
			this.donePlaying = true;
			MFW_Logger.debugInfo("sound invalid 1");
		}
	}
	
	public void Invalid()
	{
		this.donePlaying = true;
		MFW_Logger.debugInfo("sound invalid 2");
	}
}