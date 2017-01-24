package mfw.message;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mfw._core.MFW_Command;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

public class MessageCommandSendToPlayerStC implements IMessage, IMessageHandler<MessageCommandSendToPlayerStC, IMessage>{
	
	// GUI‚©‚ç‘—‚è‚½‚¢î•ñ
	public byte flag;
	public float f;
	
	public static final byte flag_sync = 0;
	public static final byte flag_dist = 1;
	
	public MessageCommandSendToPlayerStC(){}
	
	public MessageCommandSendToPlayerStC(byte flag, float data)
	{
		this.flag = flag;
	    this.f = data;
  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(flag);
		buf.writeFloat(this.f);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		this.flag = buf.readByte();
	    this.f = buf.readFloat();
    }
	
	@Override
    public IMessage onMessage(MessageCommandSendToPlayerStC m, MessageContext ctx)
    {
//		EntityClientPlayerMP player = FMLClientHandler.instance().getClient().thePlayer;
		switch(m.flag)
		{
		case flag_sync : 
			FMLClientHandler.instance().getClient().thePlayer.addChatComponentMessage(new ChatComponentText(
					StatCollector.translateToLocal("message.command.sync."+(m.f>=0?"true":"false"))
					));
			MFW_Command.doSync = (m.f >= 0); 
			break;
		case flag_dist : 
			MFW_Command.renderDistRatio = m.f; 
			FMLClientHandler.instance().getClient().thePlayer.addChatComponentMessage(new ChatComponentText(
					StatCollector.translateToLocal("message.command.renderdist")+" : x"+m.f
					));
			break;
		}
//		MFW_Logger.debugInfo(""+(MFW_Command.doSync?"true":"false")+"/"+MFW_Command.renderDistRatio);
		return null;
    }
}
