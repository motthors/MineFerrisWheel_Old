package mfw.tileEntity;

import mfw.message.MessageFerrisMisc;
import mfw.renderer.renderFerrisLimitFrame;
import mfw.wrapper.Wrap_TileEntityChangeLimitWithKey;
import mfw.wrapper.Wrap_TileEntityLimitLine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityFerrisCutter extends Wrap_TileEntityLimitLine implements Wrap_TileEntityChangeLimitWithKey{

	private int LimitFrameX;
	private int LimitFrameY;
	private int LimitFrameZ;
	renderFerrisLimitFrame limitFrame = new renderFerrisLimitFrame();
	
	public TileEntityFerrisCutter() {
		super();
		LimitFrameX = 2;
		LimitFrameY = 2;
		LimitFrameZ = 2;
	}

	@Override
	public double getMaxRenderDistanceSquared() 
	{
		return 100000d;
	}
	@Override
	public AxisAlignedBB getRenderBoundingBox() 
	{
		return INFINITE_EXTENT_AABB;
	}
	
	public int getLimitFrameX() {
		return LimitFrameX;
	}

	public int getLimitFrameY() {
		return LimitFrameY;
	}

	public int getLimitFrameZ() {
		return LimitFrameZ;
	}

	public void setFrame(int l, int FLAG)
	{
		int value = 0;
		switch(l)
		{
		case 0 : value = -100;break;
		case 1 : value = -10; break;
		case 2 : value = -1;  break;
		case 3 : value = 1;   break;
		case 4 : value = 10;  break;
		case 5 : value = 100; break;
		}
		FLAG -= MessageFerrisMisc.GUIFerrisCutterX;
//		MFW_Logger.debugInfo("value:"+value);
		switch(FLAG)
		{
		case 0 : _setFrameX(value);break;
		case 1 : _setFrameY(value);break;
		case 2 : _setFrameZ(value);break;
		}
	}
	
	public void _setFrameX(int l) {
		LimitFrameX += l;
		if (LimitFrameX > 1000)
			LimitFrameX = 1000;
		if (LimitFrameX < 1)
			LimitFrameX = 1;
	}

	public void _setFrameY(int w) {
		LimitFrameY += w;
		if (LimitFrameY > 1000)
			LimitFrameY = 1000;
		if (LimitFrameY < 1)
			LimitFrameY = 1;
	}

	public void _setFrameZ(int h) {
		LimitFrameZ += h;
		if (LimitFrameZ > 1000)
			LimitFrameZ = 1000;
		if (LimitFrameZ < 1)
			LimitFrameZ = 1;
	}
	
	public void setFrameX(int l) {
		setFrame(l, MessageFerrisMisc.GUIFerrisCutterX);
	}

	public void setFrameY(int w) {
		setFrame(w, MessageFerrisMisc.GUIFerrisCutterY);
	}

	public void setFrameZ(int h) {
		setFrame(h, MessageFerrisMisc.GUIFerrisCutterZ);
	}
	
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);      
    	loadFromNBT(par1NBTTagCompound, "");
    }
    public void loadFromNBT(NBTTagCompound nbt, String tag)
    {
    	LimitFrameX = nbt.getInteger(tag+"frameX");
    	LimitFrameY = nbt.getInteger(tag+"frameY");
    	LimitFrameZ = nbt.getInteger(tag+"frameZ");
    }

    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		saveToNBT(par1NBTTagCompound, "");
	}

	public void saveToNBT(NBTTagCompound nbt, String tag) {
		nbt.setInteger(tag + "frameX", LimitFrameX);
		nbt.setInteger(tag + "frameY", LimitFrameY);
		nbt.setInteger(tag + "frameZ", LimitFrameZ);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		this.writeToNBT(nbtTagCompound);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
		// 制御データを受け取ったらVertex構築
		limitFrame.createVertex(this, worldObj,-0.5,LimitFrameX-0.5,-0.5,LimitFrameY-0.5,-0.5,LimitFrameZ-0.5);
	}

	@Override
	public void render(Tessellator tess) 
	{
		limitFrame.render(tess);
	}

}
