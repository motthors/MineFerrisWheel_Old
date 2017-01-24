package mfw.message;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mfw.tileEntity.TileEntityFerrisWheel;

public class MessageSyncWheelStC implements IMessage, IMessageHandler<MessageSyncWheelStC, IMessage>{
	
	// GUIÇ©ÇÁëóÇËÇΩÇ¢èÓïÒ
	public int x, y, z;
	public float[] afloat;
	
	public MessageSyncWheelStC(){}
	
	public MessageSyncWheelStC(int x, int y, int z, float[] fa)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.afloat = fa;
  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(afloat.length);
		for(int i=0;i<afloat.length;++i)buf.writeFloat(afloat[i]);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
	    afloat = new float[buf.readInt()];
	    for(int i=0;i<afloat.length;++i)afloat[i] = buf.readFloat();
    }
	
	@Override
    public IMessage onMessage(MessageSyncWheelStC m, MessageContext ctx)
    {
		TileEntityFerrisWheel tile = (TileEntityFerrisWheel) FMLClientHandler.instance().getClient().theWorld.getTileEntity(m.x, m.y, m.z);
		if(tile!=null)tile.syncRot_recieve(m.afloat);
		return null;
    }
}
