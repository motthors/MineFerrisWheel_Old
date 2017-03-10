package mfw.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mfw._core.MFW_Core;
import mfw._core.connectPos;
import mfw.blocksReplication.MTYBlockAccess;
import mfw.entity.entityPartSitEx;
import mfw.math.MFW_Math;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class MFWBlockAccess extends MTYBlockAccess{

	TileEntityFerrisWheel parent;
	int copyNum = 1;
	int copyMode = 0;
	Vec3 vecForCopyRotAxis;
	public List<connectPos> listConnectPos = new ArrayList<connectPos>();
	public class sortConnectPos implements java.util.Comparator<connectPos>{
		public int compare(connectPos s, connectPos t) {
			float sub = s.angle - t.angle;
			if ( 0.02 > Math.abs(sub))return (int) Math.ceil(s.len - t.len);
			return (sub>0)?1:-1;
		}
	}
	
	//シートEX用
	public List<entityPartSitEx> listEntitySeatEx = new ArrayList<entityPartSitEx>();
	
	public MFWBlockAccess(World world, TileEntityFerrisWheel p)
	{
		super(world);
		parent = p;
		vecForCopyRotAxis = Vec3.createVectorHelper(0, 0, 0);
	}

	public void setCopyNum(int num, int constructormeta, int mode)
	{
		copyNum = num;
		copyMode = mode;
		switch(constructormeta)
		{
		case 1 : vecForCopyRotAxis.yCoord = 1; break;
		case 2 : vecForCopyRotAxis.zCoord = 1; break;
		case 3 : vecForCopyRotAxis.zCoord = -1; break;
		case 4 : vecForCopyRotAxis.xCoord = 1; break;
		case 5 : vecForCopyRotAxis.xCoord = -1; break;
		}
	}
	
	@Override
	public void postInit()
	{
		super.postInit();
		Collections.sort(listConnectPos, new sortConnectPos());
		float rotoffset = 360f / ((float)copyNum);
		connectPos list[] = new connectPos[listConnectPos.size()];
		entityPartSitEx seatlist[] = new entityPartSitEx[listEntitySeatEx.size()];
		listConnectPos.toArray(list);
		listEntitySeatEx.toArray(seatlist);
		if(copyMode==0)
		{
			for(int i=1; i < copyNum; ++i)
			{
				for(connectPos cOrg : list)
				{
					connectPos cNew = new connectPos();
					cNew.len = cOrg.len;
					cNew.angle = cOrg.len + i*rotoffset;
					Vec3 p = Vec3.createVectorHelper(cOrg.x, cOrg.y, cOrg.z);
					Vec3 a = vecForCopyRotAxis;
					MFW_Math.rotateAroundVector(p, a.xCoord, a.yCoord, a.zCoord, Math.toRadians(-i*rotoffset));
					cNew.x=(float) p.xCoord; 
					cNew.y=(float) p.yCoord; 
					cNew.z=(float) p.zCoord;
					listConnectPos.add(cNew);
				}
				for(entityPartSitEx e : seatlist)
				{
					Vec3 p = Vec3.createVectorHelper(e.getOffsetX(), e.getOffsetY()+1, e.getOffsetZ());
					Vec3 a = vecForCopyRotAxis;
					MFW_Math.rotateAroundVector(p, a.xCoord, a.yCoord, a.zCoord, Math.toRadians(-i*rotoffset));
					setSeatEx((float)p.xCoord, (float)p.yCoord, (float)p.zCoord, (int)MFW_Math.wrap(e.getSeatAngle()-i*rotoffset));
				}
			}
		}
	}
	
	@Override
	public void setBlock(Block b, int meta, int x, int y, int z)
	{
		if(b==MFW_Core.ferrisConnector)
		{
			setConnectPos(x, y, z);
			return;
		}
		else if(b==MFW_Core.ferrisSeatEx)
		{
			setSeatEx(x, y, z, meta*90);
			return;
		}
		super.setBlock(b, meta, x, y, z);
	}
	
	public void setConnectPos(int x, int y, int z)
	{
		connectPos c = new connectPos();
		c.x=x; c.y=y; c.z=z;
		c.len = (float) Math.sqrt(x*x+y*y+z*z);
		c.angle = (float) Math.atan2(-x, y);
//		MFW_Logger.debugInfo("connectpos x.y.z : "+x+"."+y+"."+z);
		listConnectPos.add(c);
	}
	
	public void setSeatEx(float x, float y, float z, int angle)
	{
//		if(MFW_Core.proxy.checkSide().isClient())return;
		if(getWorld()==null)return;
		if(getWorld().isRemote)return;
		entityPartSitEx e = new entityPartSitEx(getWorld(), parent, -1000, x, y, z, angle);
		listEntitySeatEx.add(e);
	}
	
	public int getConnectorNum()
	{
		return listConnectPos.size();
	}
	
	public void updateFirstAfterConstruct()
	{
		if(getWorld().isRemote)return;
		for(Entity e : listEntitySeatEx)
		{
			getWorld().spawnEntityInWorld(e);
		}
	}
	
	@Override
	public void invalidate()
	{
//		if(MFW_Core.proxy.checkSide().isClient())return;
		if(getWorld().isRemote)return;
		for(Entity e : listEntitySeatEx)
		{
			e.setDead();
		}
		listEntitySeatEx.clear();
	}
}
