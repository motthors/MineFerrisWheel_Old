package mfw.message;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mfw.tileEntity.TileEntityFerrisWheel;

public class MessageSyncRSPowerStC implements IMessage, IMessageHandler<MessageSyncRSPowerStC, IMessage>{
	
	// GUIÇ©ÇÁëóÇËÇΩÇ¢èÓïÒ
	private int x, y, z;
	private	int rs;
	
	public MessageSyncRSPowerStC(){}
	
	public MessageSyncRSPowerStC(int x, int y, int z, int rs)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.rs = rs;
  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.rs);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
	    this.rs = buf.readInt();
    }
	
	@Override
    public IMessage onMessage(MessageSyncRSPowerStC m, MessageContext ctx)
    {
		TileEntityFerrisWheel tile = (TileEntityFerrisWheel) FMLClientHandler.instance().getClient().theWorld.getTileEntity(m.x, m.y, m.z);
		if(tile==null)return null;
		tile.setRSPower(m.rs);
//		MFW_Logger.debugInfo("rspower:"+m.rs);
		return null;
    }

}
