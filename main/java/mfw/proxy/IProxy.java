package mfw.proxy;

import cpw.mods.fml.relauncher.Side;
import mfw.blocksReplication.ConstructorBlocksVertex;
import mfw.blocksReplication.MTYBlockAccess;
import mfw.sound.FerrisFrameSound;
import net.minecraft.entity.player.EntityPlayer;

public interface IProxy{
	public int getNewRenderType();
	public void preInit();
	public void init();
	public void postInit();
	
	public ConstructorBlocksVertex getrendererFerrisWheel(MTYBlockAccess ba);
//	public rendererFerrisBasket getrendererFerrisBasket(MTYBlockAccess ba, int side, int meta, float ox, float oy, float oz);
	public Side checkSide();
	
	public EntityPlayer getClientPlayer();
	public void PlaySound(FerrisFrameSound sound);
}