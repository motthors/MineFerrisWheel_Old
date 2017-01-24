package mfw.proxy;

import MTYlib.blocksReplication.ConstructorBlocksVertex;
import MTYlib.blocksReplication.MTYBlockAccess;
import cpw.mods.fml.relauncher.Side;

public interface IProxy{
	public int getNewRenderType();
	public void preInit();
	public void init();
	public void postInit();
	
	public ConstructorBlocksVertex getrendererFerrisWheel(MTYBlockAccess ba);
//	public rendererFerrisBasket getrendererFerrisBasket(MTYBlockAccess ba, int side, int meta, float ox, float oy, float oz);
	public Side checkSide();
}