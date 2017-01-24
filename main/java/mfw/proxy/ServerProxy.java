package mfw.proxy;

import MTYlib.blocksReplication.ConstructorBlocksVertex;
import MTYlib.blocksReplication.MTYBlockAccess;
import cpw.mods.fml.relauncher.Side;

public class ServerProxy implements IProxy{
	
	@Override
	public int getNewRenderType()
	{
		return -1;
	}

	@Override
	public void preInit()
	{

//		ERC_Core.tickEventHandler = new ERC_TickEventHandler();
//		FMLCommonHandler.instance().bus().register(ERC_Core.tickEventHandler);
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}
	
	
	public ConstructorBlocksVertex getrendererFerrisWheel(MTYBlockAccess ba)
	{
		return null;
	}
//	public rendererFerrisBasket getrendererFerrisBasket(MTYBlockAccess ba, int side, int meta, float ox, float oy, float oz)
//	{
//		return null;
//	}
	
	public Side checkSide()
	{
		return Side.SERVER;
	}
}
