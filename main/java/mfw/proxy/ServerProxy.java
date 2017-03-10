package mfw.proxy;

import cpw.mods.fml.relauncher.Side;
import mfw.blocksReplication.ConstructorBlocksVertex;
import mfw.blocksReplication.MTYBlockAccess;
import mfw.sound.FerrisFrameSound;
import net.minecraft.entity.player.EntityPlayer;

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
	
	public EntityPlayer getClientPlayer()
	{
		return null;
	}
	
	
	public void PlaySound(FerrisFrameSound sound)
	{}
}
