package mfw._core;

import mfw.message.MFW_PacketHandler;
import mfw.message.MessageCommandSendToPlayerStC;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class MFW_Command extends CommandBase{
	
	public static boolean doSync = true;
	public static float renderDistRatio = 1.0f;
	public static final int chunkRatio = 2;
	
	@Override
	public int getRequiredPermissionLevel()
    {
        return 0;
    }

	@Override
	public String getCommandName()
	{
		return "mfw";
	}

	@Override
	public String getCommandUsage(ICommandSender par1)
	{
		return "commands.mfw.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] strings)
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		if(strings.length == 2)
		{
			byte flag = -1;
			float data = 0;
			if(strings[0].equals("sync"))
			{
				if(strings[1].equals("true")) data = 1;
				else if(strings[1].equals("false")) data = -1;
				else
				{
					player.addChatComponentMessage(new ChatComponentText("Åò4 /mfw sync [true/false]"));
					return;
				}
				flag = MessageCommandSendToPlayerStC.flag_sync;
			}
			else if(strings[0].equals("renderdist"))
			{
				try{
					data = Float.parseFloat(strings[1]);
					if(data < 1.0f || 10.0f < data)throw new Exception();
				}catch(Exception e){
					player.addChatComponentMessage(new ChatComponentText("Åò4 /mfw renderdist [1~10]"));
					return;
				}
				flag = MessageCommandSendToPlayerStC.flag_dist;
			}
			else 
			{
				player.addChatComponentMessage(new ChatComponentText("Åò4 /mfw [sync/renderdist] (data)"));
				return;
			}
			
			MessageCommandSendToPlayerStC packet = new MessageCommandSendToPlayerStC(flag, data);
			MFW_PacketHandler.INSTANCE.sendTo(packet, player);
			return;
		}
		else player.addChatComponentMessage(new ChatComponentText("Åò4 /mfw [sync/renderdist] (data)"));
	}
}