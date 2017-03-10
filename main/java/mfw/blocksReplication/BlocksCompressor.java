package mfw.blocksReplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mfw.block.blockFerrisBasketConstructor;
import mfw.block.blockFerrisConnector;
import mfw.block.blockFerrisConstructor;
import mfw.block.blockFerrisCore;
import mfw.block.blockFerrisCutter;
import mfw.block.blockFerrisRemoteController;
import mfw.block.blockFerrisSupporter;
import mfw.tileEntity.TileEntityFerrisCutter;
import mfw.util.byteZip;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlocksCompressor {

	int srcPosX1, srcPosY1, srcPosZ1;
	int srcPosX2, srcPosY2, srcPosZ2;
	int baseForCtmX, baseForCtmY, baseForCtmZ;
	int sizex, sizey, sizez;
	int nowBlockArrayIndex;
	int blocknum = 0;
	int connectornum;
	int connectx, connecty, connectz;
	private BlockPiece[][][] BlockArray;
	public class BlockPiece{
//		int x,y,z;
		Block type = Blocks.air;
		int meta;
		TileEntity tile;
		boolean cutflag = false;
	}
	
	
	public void allocBlockArray(int x, int y, int z)
	{
		blocknum = 0;
		sizex = x; sizey = y; sizez = z;
		BlockArray = new BlockPiece[x][y][z];
		for(int i=0;i<x;++i)
	    	for(int j=0;j<y;++j)
	    		for(int k=0;k<z;++k)
	    			BlockArray[i][j][k] = new BlockPiece();
	}
	
	public int getAllBlockNum()
	{
		return sizex*sizey*sizez;
	}
	public int getNowBlockArrayIndex()
	{
		return nowBlockArrayIndex;
	}
	
/////////////////////////////ブロックコピー前にやること/////////////////////////////
	
	public void setSrcPosition(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax)
	{
		srcPosX1 = xmin;	srcPosY1 = ymin;	srcPosZ1 = zmin;	srcPosX2 = xmax;	srcPosY2 = ymax;	srcPosZ2 = zmax;
		allocBlockArray(srcPosX2-srcPosX1+1, srcPosY2-srcPosY1+1, srcPosZ2-srcPosZ1+1);
	}
	public void setSrcPosition(Vec3 min, Vec3 max)
	{
		srcPosX1 = (int) min.xCoord;	srcPosY1 = (int) min.yCoord;	srcPosZ1 = (int) min.zCoord;
		srcPosX2 = (int) max.xCoord;	srcPosY2 = (int) max.yCoord;	srcPosZ2 = (int) max.zCoord;
		allocBlockArray(srcPosX2-srcPosX1+1, srcPosY2-srcPosY1+1, srcPosZ2-srcPosZ1+1);
	}

	/////////////////////////////ブロックコピー/////////////////////////////
	
	public void makeTag(NBTTagCompound nbt, World world)
	{
		byteZip zipCompressor = new byteZip();
		SearchBlocks(nbt, zipCompressor, world);
        zipCompressor.compress();
        nbt.setInteger("decompresssize", zipCompressor.getOrgSize());
        nbt.setByteArray("compressedbytearray", zipCompressor.getOutput());
        nbt.setInteger("mtybr:sizex", sizex);
        nbt.setInteger("mtybr:sizey", sizey);
        nbt.setInteger("mtybr:sizez", sizez);
        nbt.setInteger("mtbr:connectnum", connectornum);
	}
	
	@SuppressWarnings("unchecked")
	public void SearchEntities(NBTTagCompound nbt, World world)
	{
		AxisAlignedBB aabbForEntity = AxisAlignedBB.getBoundingBox(srcPosX1-1, srcPosY1-3, srcPosZ1-1, srcPosX2+1, srcPosY2+3, srcPosZ2+1);
		List<Entity> listentity = world.getEntitiesWithinAABBExcludingEntity(null, aabbForEntity);
		nbt.setInteger("mtybr:entitynum", listentity.size());
		int i = 0;
		for(Entity e : listentity)
		{
			NBTTagCompound nbtentity = new NBTTagCompound();
			e.writeToNBT(nbtentity);
			nbtentity.setString("entityname", e.getClass().getName());
			nbt.setTag("entity."+i, nbtentity);
			++i;
		}
	}
	//////////////////////////////////////////////////////////
	//////////////////////以降細かい処理とか//////////////////////
	//////////////////////////////////////////////////////////
	
	
	
	// worldからブロックを取得してMTYBlockAccessへ格納後、圧縮
	private void SearchBlocks(NBTTagCompound nbt, byteZip zip, World world)
	{
		nowBlockArrayIndex = 0;
		connectornum = 0;
		int nameidx = 1;
		Map<String, Integer> nameMap = new HashMap<String, Integer>();
		List<String> nameList = new ArrayList<String>();
		nameList.add(Block.blockRegistry.getNameForObject(Blocks.air));
		nameMap.put(Block.blockRegistry.getNameForObject(Blocks.air), 0);
		
		// ブロック登録
		for(int x=srcPosX1;x<=srcPosX2;++x){
			for(int y=srcPosY1;y<=srcPosY2;++y){
				for(int z=srcPosZ1;z<=srcPosZ2;++z)
				{
					++nowBlockArrayIndex;
					Block block = world.getBlock(x, y, z);
					if(block != null)
					{	
						String name = Block.blockRegistry.getNameForObject(block);
						if(nameMap.containsKey(name)==false)
						{	// Mapにない名前が来たら
							nameMap.put(name, nameidx);
							nameList.add(name);
							nameidx+=1;
						}
						int meta = world.getBlockMetadata(x, y, z);
						TileEntity tile = world.getTileEntity(x, y, z);
						if(block instanceof blockFerrisCutter)this.cut(world,x,y,z);
						setBlock(block, meta, x, y, z, tile);
//						MFW_Logger.debugInfo("setblock :"+block.getUnlocalizedName()+"  x.y.z.m"+x+"."+y+"."+z+"."+meta);
					}
				}
			}
		}
		
		boolean byteORshort = nameMap.size() < 256;
		nbt.setBoolean("byteorshort", byteORshort);
		
		// コンパイル
		for(int x=0;x<sizex;++x){
			for(int y=0;y<sizey;++y){
				for(int z=0;z<sizez;++z)				
				{
					BlockPiece p = BlockArray[x][y][z];
					boolean flag = isExistBlock(p.type);
					flag &= !(BlockArray[x][y][z].cutflag);
//					if(BlockArray[x][y][z].cutflag)MFW_Logger.debugInfo("cut!!:"+x+"."+y+"."+z);
					// 特定のブロックは空気ブロックにしちゃう
					if(!flag)	p.type = Blocks.air;
					
					//MapでIDを置換
					int id = nameMap.get(Block.blockRegistry.getNameForObject(p.type));
					if(byteORshort) zip.setByte((byte)id);
					else zip.setShort((short)id);
					// 空気ブロック以外はmetaを入れる
					if(id != 0) zip.setByte((byte) p.meta);
					
//					MFW_Logger.debugInfo("zipper  block:"+p.type.getUnlocalizedName()+", meta:"+p.meta);
//					MFW_Logger.debugInfo("compress :"+p.type.getUnlocalizedName()+"  x.y.z.m"+x+"."+y+"."+z+"."+p.meta);
					//Tileセット
					if(p.tile != null)
					{
						NBTTagCompound nbttile = new NBTTagCompound();
						p.tile.writeToNBT(nbttile);
						nbt.setTag("tile."+(x)+"."+(y)+"."+(z), nbttile);
//						nbt.setString("tilepos", "tile."+(x+srcPosX1)+"."+(y+srcPosY1)+"."+(z+srcPosZ1));
//						tilelist.appendTag(nbttile);
					}
				}
			}
		}
		zip.compress();
		nbt.setInteger("decompresssize", zip.getOrgSize());
		nbt.setByteArray("compressedbytearray", zip.getOutput());
		for(int i=0; i<nameList.size();++i)
		{
			nbt.setString("blockunlocalname"+i, nameList.get(i));
		}
		nbt.setInteger("blockunlocalnameNum", nameList.size());
	}
	
	private void cut(World world, int x, int y, int z)
	{
		TileEntityFerrisCutter tile = (TileEntityFerrisCutter) world.getTileEntity(x, y, z);
		int X = tile.getLimitFrameX();
		int Y = tile.getLimitFrameY();
		int Z = tile.getLimitFrameZ();
		for(int i=0;i<X;++i)for(int j=0;j<Y;++j)for(int k=0;k<Z;++k)
		{
			try{
				BlockArray[x-srcPosX1+i][y-srcPosY1+j][z-srcPosZ1+k].cutflag = true;
//				MFW_Logger.debugInfo("cut!:"+i+"."+j+"."+k);
			}catch(ArrayIndexOutOfBoundsException e){
				continue;
			}
		}
	}
	
	private void setBlock(Block block, int meta, int x, int y, int z, TileEntity tile)
	{
		blocknum += 1;
		BlockArray[x-srcPosX1][y-srcPosY1][z-srcPosZ1].type = block;
		BlockArray[x-srcPosX1][y-srcPosY1][z-srcPosZ1].meta = meta;
		BlockArray[x-srcPosX1][y-srcPosY1][z-srcPosZ1].tile = tile;
		if(block instanceof blockFerrisConnector)
		{
			connectornum+=1;
			connectx = x-srcPosX1; connecty = y-srcPosY1; connectz = z-srcPosZ1;
		}
	}
	

	//////////////////////Override考慮関数//////////////////////
	
	private boolean isExistBlock(Block b)
    {
    	if(b == null)return false;
    	if(b instanceof blockFerrisBasketConstructor)return false;
    	if(b instanceof blockFerrisRemoteController)return false;
    	if(b instanceof blockFerrisConstructor)return false;
    	if(b instanceof blockFerrisSupporter)return false;
    	if(b instanceof blockFerrisCutter)return false;
    	if(b instanceof blockFerrisCore)return false;
    	
    	return true;
	}
	
	/////////////////////// get系 ///////////////////////////
	
	public int getConnectorNum()
	{
		return connectornum;
	}
	public int getConnectorPos(int xyz012)
	{
		switch(xyz012)
		{
		case 0 : return connectx;
		case 1 : return connecty;
		case 2 : return connectz;
		}
		return 0;
	}
}
