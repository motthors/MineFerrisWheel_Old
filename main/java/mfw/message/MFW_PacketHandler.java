package mfw.message;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import mfw._core.MFW_Core;

public class MFW_PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MFW_Core.MODID);
  
  	public static void init()
  	{
  		int i=0;
  		INSTANCE.registerMessage(MessageFerrisMisc.class, MessageFerrisMisc.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageSyncWheelStC.class, MessageSyncWheelStC.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(MessageRegistSyncRotParentCtS.class, MessageRegistSyncRotParentCtS.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageSyncRSPowerStC.class, MessageSyncRSPowerStC.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(MessageCommandSendToPlayerStC.class, MessageCommandSendToPlayerStC.class, i++, Side.CLIENT);
  	}
}
