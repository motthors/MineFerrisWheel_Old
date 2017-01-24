package mfw.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_checker;

//@TransformerExclusions
public class handlerClientConnected {
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) throws Exception 
	{
		MFW_checker.disp(event);
//		event.handler.handleChat(new S02PacketChat(new ChatComponentText("test:"+"\uE8A6B3\uE8A6A7\uE8BB8A")));
	}

}
