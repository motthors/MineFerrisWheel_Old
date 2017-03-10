package mfw._core;

import java.util.Date;
import java.util.GregorianCalendar;

import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import net.minecraft.util.StatCollector;

@TransformerExclusions
public class MFW_checker {
	
	static boolean b;
	
	public static void check() throws Exception
	{
//		String sa[] =  new File("./mods/").list();
//		for(String s : sa)
//		{
//			b=(s.contains("MineFerrisWheel-"+MFW_Core.VERSION+"-観覧車MOD"));
////			b=(s.contains("MineFerrisWheel") && s.contains("�ｿｽﾏ暦ｿｽ�ｿｽ�ｿｽMOD") && s.contains(MFW_Core.VERSION));
//			if(b)break;
//		}
//		Date today = new Date();
//		Date limit = new GregorianCalendar(2017, 3 - 1, 6, 0, 0, 0).getTime();
//		if(new Date().after(limit))
//			throw new Exception(StatCollector.translateToLocal("message.error.repos"));
	}
	
//	public static boolean is()
//	{
//		return b;
//	}
	
	public static void disp(FMLNetworkEvent.ClientConnectedToServerEvent event)
	{
////		if(!b)event.handler.handleChat(new S02PacketChat(new ChatComponentTranslation("fxxk")));
////		else event.handler.handleChat(new S02PacketChat(new ChatComponentText("OK!")));
//		if(!b)
//		{
//			
//		}
//		event.handler.handleChat(new S02PacketChat(new ChatComponentText("test:"+"MineFerrisWheel-"+MFW_Core.VERSION+"観覧車"+"MOD.jar")));
	}
}
