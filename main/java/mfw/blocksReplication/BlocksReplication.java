package mfw.blocksReplication;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import cpw.mods.fml.relauncher.Side;
import mfw._core.MFW_Core;
import mfw.block.blockFerrisBasketConstructor;
import mfw.block.blockFerrisConstructor;
import mfw.block.blockFerrisCore;
import mfw.block.blockFerrisSupporter;
import mfw.block.blockFileManager;
import mfw.util.byteZip;
import mfw.wrapper.I_FerrisPart;
import mfw.wrapper.W_TileEntityFerrisBeacon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlocksReplication {

	World worldObj;
	int side, meta;
	int xCoord, yCoord, zCoord;			// このクラスを保持しているTileなどのブロックの座標
	int srcPosX1, srcPosY1, srcPosZ1;
	int srcPosX2, srcPosY2, srcPosZ2;
	int connectOffsetX, connectOffsetY, connectOffsetZ;
	int baseForCtmX, baseForCtmY, baseForCtmZ;
	private int orgsize;
	private boolean updateFirst = false;
	
	MTYBlockAccess blockAccess;
	ConstructorBlocksVertex BlocksVertex;
	
	
	public BlocksReplication(World world, MTYBlockAccess ba)
	{
		worldObj = world;
		blockAccess = ba;
		ba.setWorld(world);
		BlocksVertex = MFW_Core.proxy.getrendererFerrisWheel(blockAccess);
	}
	
	public void setWorld(World world)
	{
		worldObj = world;
		blockAccess.setWorld(world);
	}

	public void setCorePosition(int x, int y, int z)
	{
		xCoord = x; yCoord = y; zCoord = z;
	}
	
	public void setBasePosForCTM(int x, int y, int z)
	{
		baseForCtmX = x; baseForCtmY = y; baseForCtmZ = z;
	}
	
	
	/////////////////////////////データからブロックデータ展開/////////////////////////////
	
	public void constructFromTag(NBTTagCompound nbt, int metam, I_FerrisPart tile, boolean isMultiThread)
	{
//		isMultiThread = false;
		if(isMultiThread)
		{
//			MFW_Logger.debugInfo("thread create");
			Thread thread = new multiThread_BlockCopy(nbt, meta, tile);
//			MFW_Logger.debugInfo("thread start run");
			thread.start();
		}
		else 
		{
			_constructFromTag(nbt, meta);
			tile.completeConstruct(nbt);
			if(MFW_Core.proxy.checkSide()==Side.CLIENT)BlocksVertex.startRender();
			updateFirst = true;
		}
	}
	public void _constructFromTag(NBTTagCompound nbt, int meta)
	{
		if(nbt!=null)
		{
//			MFW_Logger.debugInfo("thread construct checker 1");
			//NBTからデータ取り出し
			side = nbt.getByte("constructormetaflag");
	    	baseForCtmX = nbt.getInteger("basefornbtx"); // CTM用基準座標
	    	baseForCtmY = nbt.getInteger("basefornbty");
	    	baseForCtmZ = nbt.getInteger("basefornbtz");
	    	int sizex = nbt.getInteger("mtybr:sizex");
	    	int sizey = nbt.getInteger("mtybr:sizey");
	    	int sizez = nbt.getInteger("mtybr:sizez");
	    	int offsetx = nbt.getInteger("connectoffsetx"); // 中心位置（工作台の相対距離）
			int offsety = nbt.getInteger("connectoffsety");
			int offsetz = nbt.getInteger("connectoffsetz");
//	    	int copyNum = nbt.getInteger("copynum");
			orgsize = nbt.getInteger("decompresssize");
//			MFW_Logger.debugInfo("thread construct checker 2 start unzip");
			//////////////解凍/////////
			if(orgsize == 0)return;
			byte[] compressedModelData = nbt.getByteArray("compressedbytearray");
			byte[] decompressedData = new byte[orgsize];
			if(compressedModelData==null)return;
			try {
				byteZip.decompress(decompressedData, compressedModelData);
			} catch (DataFormatException e) {
				e.printStackTrace();
			}
			ByteBuffer buf = ByteBuffer.allocate(decompressedData.length);
			buf.put(decompressedData);
			buf.rewind();
			//////////////解凍おわり/////////
//			MFW_Logger.debugInfo("thread construct checker 3 zip end");
	        boolean drawcoreFlag = (side & 8)>0 ? true : false;
	        side &= 7;
	        
//	        MFW_Logger.debugInfo("thread construct checker 4 alloc ba");
	        // BAのサイズが決まったら領域確保A
	        blockAccess.Init(worldObj, sizex, sizey, sizez, xCoord, yCoord, zCoord, offsetx, offsety, offsetz);
	        
	        if(BlocksVertex!=null)BlocksVertex.setFlagDrawCore(drawcoreFlag);
	        blockAccess.ctmForrow(baseForCtmX, baseForCtmY, baseForCtmZ);
	        blockAccess.setSide(side,meta);
	        
//	        MFW_Logger.debugInfo("thread construct checker 5 create name list");
	        // block名配列生成
	        int nameNum = nbt.getInteger("blockunlocalnameNum");
	        List<String> nameList = new ArrayList<String>();
//	        nameList.add(Block.getBlockById(0).getUnlocalizedName());
	        for(int i=0; i<nameNum; ++i)
	        {
	        	nameList.add(nbt.getString("blockunlocalname"+i));
	        }
	        
	        boolean byteorshort = nbt.getBoolean("byteorshort");
	        
//	        MFW_Logger.debugInfo("thread construct checker 6 start set block to ba");
	        for(int x=0;x<sizex;++x)
			{
				for(int y=0;y<sizey;++y)
				{
					for(int z=0;z<sizez;++z)
					{
				    	int blockid;
				    	if(byteorshort) blockid = (int)buf.get() & 0xff;
				    	else blockid = (int)buf.getShort() & 0xffff;
				    	int blockmeta = 0;
				    	if(blockid!=0)blockmeta = (int)buf.get() & 0xff;
				    	// ID置換
				    	
				    	if(blockid >= nameList.size())continue;
				    	Block type = Block.getBlockFromName(nameList.get(blockid));
				    	blockid = Block.getIdFromBlock(type);
				    	if(!isExistBlock(type))continue;
				    	blockAccess.setBlockAbsolute(type, blockmeta, x, y, z);
//				    	MFW_Logger.debugInfo("setblock :"+type.getUnlocalizedName()+"  x.y.z.m"+x+"."+y+"."+z+"."+blockmeta);
				    	if(nbt.hasKey("tile."+x+"."+y+"."+z))
			    		{
				    		TileEntity tile = type.createTileEntity(worldObj, blockmeta);
					    	if(tile!=null)
					    	{	
					    		NBTTagCompound nbttile = (NBTTagCompound) nbt.getTag("tile."+x+"."+y+"."+z);
					    		tile.readFromNBT(nbttile);
					    		if(tile instanceof TileEntityBeacon)tile = new W_TileEntityFerrisBeacon(blockAccess, blockAccess.getBlockOrgPos(x, y-1, z));
						    	tile.setWorldObj(worldObj);
						    	blockAccess.setTileEntityAbsolute(tile, x, y, z);
					    	}
			    		}
					}
				}
		    }
	        //entity
	        int entitynum = nbt.getInteger("mtybr:entitynum");
	        for(int i=0; i<entitynum; ++i)
    		{
	        	NBTTagCompound nbtentity = (NBTTagCompound) nbt.getTag("entity."+i);
	        	if(nbtentity==null)continue;
	        	String entityName = nbtentity.getString("entityname");
	        	try{
		        	// 文字列からクラスを取得
		    		Class<?> clazz;
		    		Constructor<?> constructor = null;
		    		Entity e = null;
		    		if(entityName.equals(EntityPlayerMP.class.getName()))
		    		{
//		    			clazz = Class.forName(EntityOtherPlayerMP.class.getName());
//		    			constructor = clazz.getConstructor(World.class, GameProfile.class);
//		    			e = (Entity)constructor.newInstance(
//		    					worldObj,
//		    					Minecraft.getMinecraft().getSession().func_148256_e());
		    		}
		    		else
	    			{
		    			clazz = Class.forName(entityName);
		    			constructor = clazz.getConstructor(World.class);
		    			e = (Entity)constructor.newInstance(worldObj);
	    			}
		    		if(e == null)continue;
		    		e.readFromNBT(nbtentity);
		    		blockAccess.setEntity(e);
	        	}catch(Exception e)
	        	{
	        		continue;
	        	}
    		}
		}
	}
	
	
	/////////////////////////////描画/////////////////////////////
	
	public void render()
	{
		BlocksVertex.render();
	}
	
	public void renderPost()
	{
		BlocksVertex.render2();
	}
	
	public void renderCore()
	{
		BlocksVertex.renderCore();
	}
	
	public void renderEntities(float partialtick)
	{
		blockAccess.renderTileEntity(partialtick);
		blockAccess.renderEntity();
//		BlocksVertex.renderEntity_test();
	}
	
	public void invalidate()
	{
		if(BlocksVertex!=null)BlocksVertex.delete();
		if(blockAccess!=null)blockAccess.invalidate();
	}
	
	//////////////////////////////////////////////////////////
	//////////////////////以降細かい処理とか//////////////////////
	//////////////////////////////////////////////////////////

	
	//////////////////////getset系//////////////////////
	
	public void getSrcPosition(Vec3 min, Vec3 max)
	{
		min.xCoord = srcPosX1;
		min.yCoord = srcPosY1;
		min.zCoord = srcPosZ1;
		max.xCoord = srcPosX2;
		max.yCoord = srcPosY2;
		max.zCoord = srcPosZ2;
	}
	
	public int getSide()
	{
		return side;
	}
	
	public int getCTMX(){ return baseForCtmX; }
	public int getCTMY(){ return baseForCtmY; }
	public int getCTMZ(){ return baseForCtmZ; }
	
	//////////////////////Override考慮関数//////////////////////
	
	//含めないブロック設定
	private boolean isExistBlock(Block b)
    {
    	if(b == null)return false;
    	if(b instanceof BlockAir)return false;
    	
    	if(b instanceof blockFerrisConstructor)return false;
    	if(b instanceof blockFerrisBasketConstructor)return false;
     	if(b instanceof blockFerrisSupporter)return false;
     	if(b instanceof blockFileManager)return false;
     	if(b instanceof blockFerrisCore)return false;
    	return true;
	}
	
//	private void setBlock(Block block, int meta, int x, int y, int z)
//	{
//		blockAccess.setBlock(block, meta, x, y, z);
//	}
	
	//モデル構築後最初に1回だけやりたい処理を書くところ
	public void update()
	{
		if(updateFirst)
		{
			blockAccess.updateFirstAfterConstruct();
			updateFirst = false;
		}
	}
	
	//////////////////////ブロックコピー用のマルチスレッド//////////////////////
	
	private class multiThread_BlockCopy extends Thread{
		
		NBTTagCompound nbt;
		int meta;
		I_FerrisPart tile;
		
		public multiThread_BlockCopy(NBTTagCompound nbt, int meta, I_FerrisPart tile)
		{
			this.nbt = nbt;
			this.meta = meta;
			this.tile = tile;
		}
		
		public void run()
		{
			_constructFromTag(nbt, meta);
			tile.completeConstruct(nbt);
			if(MFW_Core.proxy.checkSide()==Side.CLIENT)BlocksVertex.startRender();
			updateFirst = true;
//			MFW_Logger.debugInfo("thread end run");
		}
	}
	
}
