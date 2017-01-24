package mfw.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.world.World;

public class MessageRegistSyncRotParentCtS implements IMessage, IMessageHandler<MessageRegistSyncRotParentCtS, IMessage>{
	
	// GUIÇ©ÇÁëóÇËÇΩÇ¢èÓïÒ
	private int px, py, pz;
	private int cx, cy, cz;
	private	int TreeIndexP;
	private	int TreeIndexC;
	
	public MessageRegistSyncRotParentCtS(){}
	
	public void setParent(int x, int y, int z, int treeIdx)
	{
	    this.px = x;
	    this.py = y;
	    this.pz = z;
	    this.TreeIndexP = treeIdx;
  	}
	public void setChild(int x, int y, int z, int treeIdx)
	{
	    this.cx = x;
	    this.cy = y;
	    this.cz = z;
	    this.TreeIndexC = treeIdx;
  	}
	
	public boolean isSameFrame()
	{
		if(px != cx)return false;
		if(py != cy)return false;
		if(pz != cz)return false;
		if(TreeIndexP != TreeIndexC)return false;
		return true;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.px);
		buf.writeInt(this.py);
		buf.writeInt(this.pz);
		buf.writeInt(this.cx);
		buf.writeInt(this.cy);
		buf.writeInt(this.cz);
		buf.writeInt(this.TreeIndexP);
		buf.writeInt(this.TreeIndexC);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.px = buf.readInt();
	    this.py = buf.readInt();
	    this.pz = buf.readInt();
	    this.cx = buf.readInt();
	    this.cy = buf.readInt();
	    this.cz = buf.readInt();
	    this.TreeIndexP = buf.readInt();
	    this.TreeIndexC = buf.readInt();
    }
	
	@Override
    public IMessage onMessage(MessageRegistSyncRotParentCtS m, MessageContext ctx)
    {
		World world = ctx.getServerHandler().playerEntity.worldObj;
		TileEntityFerrisWheel parent = (TileEntityFerrisWheel) world.getTileEntity(m.px, m.py, m.pz);
		TileEntityFerrisWheel child = (TileEntityFerrisWheel) world.getTileEntity(m.cx, m.cy, m.cz);
		parent = parent.getTileFromTreeIndex(m.TreeIndexP);
		child = child.getTileFromTreeIndex(m.TreeIndexC);
		parent.setSyncChild(child);
		child.setSyncParent(parent);
		return null;
    }

}
