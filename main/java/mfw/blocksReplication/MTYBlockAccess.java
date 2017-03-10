package mfw.blocksReplication;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import mfw._core.MFW_Core;
import mfw.block.blockSeatToSitDown;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public class MTYBlockAccess implements IBlockAccess{

	public class BlockPiece{
//		int x,y,z;
		Block type = Blocks.air;
		int meta;
		int lightlevel=0;
		TileEntity tile;
	}
	public class TileEntityPiece{
		int x,y,z;
		TileEntity tile;
	}
		
	private World worldObj;
	private BlockPiece[][][] BlockArray;
	private ArrayList<TileEntityPiece> listTileEntity = new ArrayList<TileEntityPiece>();
	private ArrayList<Entity> listEntity = new ArrayList<Entity>();
	protected int blocknum = 0;
	protected int blocknumPost = 0;
	protected int sizex, sizey, sizez;
	protected int xCoord,yCoord,zCoord;
	protected int arrayoffsetX,arrayoffsetY,arrayoffsetZ;
	protected int constructSide,CoreSide;
	// CTM対応用
	public int ctmBaseX=0,ctmBaseY=0,ctmBaseZ=0;

	// 描画用
	public class renderPiece{
		public int x,y,z;
		public Block b;
		public renderPiece(int _x,int _y,int _z,Block _b){x=_x;y=_y;z=_z;b=_b;}
	}
	public List<renderPiece> listPrePass = new ArrayList<renderPiece>();
	public List<renderPiece> listPostPass = new ArrayList<renderPiece>();
	

	public MTYBlockAccess(World world)
	{
		worldObj = world;
	}
	public void Init(World world, int sizex, int sizey, int sizez, int posx, int posy, int posz, int offsetx, int offsety, int offsetz)
	{
		setWorld(world);
		xCoord = posx;
		yCoord = posy;
		zCoord = posz;
		arrayoffsetX = offsetx;
		arrayoffsetY = offsety;
		arrayoffsetZ = offsetz;
		allocBlockArray(sizex+2,sizey+2,sizez+2);
	}
	
	public void postInit()
	{
//		if(worldObj != null && worldObj.isRemote)
//		{
//		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
//				"tw Nromal Block : "+	listPrePass.size()));
//		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
//				"tw TOUMEI Block : "+	listPostPass.size()));
//		}
	}
	
	public void invalidate()
	{
		//ok	for override
	}
	
	public void updateFirstAfterConstruct()
	{
		//ok	for override
	}
	
	public void setSide(int side, int meta)
	{
		constructSide = side;
		CoreSide = meta;
	}
	
	public void allocBlockArray(int x, int y, int z)
	{
		blocknum = 0;
		blocknumPost = 0;
		sizex = x; sizey = y; sizez = z;
		listTileEntity.clear();
		BlockArray = new BlockPiece[x][y][z];
		for(int i=0;i<x;++i)
	    	for(int j=0;j<y;++j)
	    		for(int k=0;k<z;++k)
	    			BlockArray[i][j][k] = new BlockPiece();
	}
	public void ctmForrow(int x, int y, int z)
	{
		ctmBaseX=x;
		ctmBaseY=y;
		ctmBaseZ=z;
	}
	
	public void setWorld(World world)
	{
		worldObj = world;
	}
	public World getWorld()
	{
		return worldObj;
	}
	
	public void setBlockAbsolute(Block b, int meta, int x,int y, int z)
	{
		setBlock(b, meta, x-arrayoffsetX, y-arrayoffsetY, z-arrayoffsetZ);
	}
	public void setBlock(Block b, int meta, int x,int y,int z)
	{
		boolean isremote = (worldObj==null)?MFW_Core.proxy.checkSide().isClient() : worldObj.isRemote;
		if(isremote)
		{
			if(b.getRenderBlockPass()==0) blocknum += 1;
			else blocknumPost += 1;
		}
//		MFW_Logger.debugInfo("mbtba::blocknum:"+blocknum+".."+blocknumPost+" ... block:"+b.getUnlocalizedName()+" : meta"+meta);
		BlockArray[x+arrayoffsetX+1][y+arrayoffsetY+1][z+arrayoffsetZ+1].type = b;
		BlockArray[x+arrayoffsetX+1][y+arrayoffsetY+1][z+arrayoffsetZ+1].meta = meta;
//		MFW_Logger.debugInfo("b:"+b.getUnlocalizedName()+" meta:"+meta);
		
		if(isremote)
		{
			setLightLevel(b, x + arrayoffsetX, y + arrayoffsetY, z + arrayoffsetZ);
			if(b instanceof blockSeatToSitDown)return;
			if(b.getRenderBlockPass() == 0) listPrePass.add(new renderPiece(x+ctmBaseX, y+ctmBaseY, z+ctmBaseZ, b));
			else listPostPass.add(new renderPiece(x+ctmBaseX, y+ctmBaseY, z+ctmBaseZ, b));
		}
	}
	public void setTileEntity(TileEntity tile, int x,int y,int z)
	{
		BlockArray[x+arrayoffsetX+1][y+arrayoffsetY+1][z+arrayoffsetZ+1].tile = tile;
		TileEntityPiece t = new TileEntityPiece();
		t.tile = tile;
		t.x = x;
		t.y = y;
		t.z = z;
		listTileEntity.add(t);
	}
	public void setTileEntityAbsolute(TileEntity tile, int x, int y, int z)
	{
		setTileEntity(tile, x-arrayoffsetX, y-arrayoffsetY, z-arrayoffsetZ);
	}
	public void setEntity(Entity entity)
	{
		listEntity.add(entity);
	}
	public void setWorldToTileEntities(World world)
	{
		for(TileEntityPiece te : listTileEntity)te.tile.setWorldObj(world);
	}
	
	public void updateTileEntity()
	{
		for(TileEntityPiece t :listTileEntity)
		{
			t.tile.updateEntity();
		}
	}
	
	public void renderTileEntity(float partialtick)
	{
		GL11.glDisable(GL11.GL_LIGHTING);
		int size = listTileEntity.size();
		for(int i=0; i<size; ++i)
	    {
			TileEntityPiece t = listTileEntity.get(i);
			TileEntityRendererDispatcher.instance.renderTileEntityAt(t.tile, t.x, t.y, t.z, partialtick);
	    }
//		renderEntity();
//		renderEntity_test();
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	public void renderEntity()
	{
		GL11.glPushMatrix();
		GL11.glTranslated(-ctmBaseX, -ctmBaseY, -ctmBaseZ);
		int size = listEntity.size();
		for(int i=0; i<size; ++i)
		{
			Entity e = listEntity.get(i);
			 RenderManager.instance.getEntityRenderObject(e).doRender(e, e.posX, e.posY, e.posZ, e.rotationYaw, 0);
		}
//		GL11.glTranslated(ctmBaseX, ctmBaseY, ctmBaseZ);
		GL11.glPopMatrix();
	}
//	
//	public void constructVertex(ConstructorBlocksVertex renderer)
//	{
//		for(int i=0; i<sizex; ++i)
//		{
//			for(int j=0; j<sizey; ++j)
//			{
//				for(int k=0; k<sizez; ++k)
//				{
//					Block type = BlockArray[i][j][k].type;
//					renderer.renderBlockByRenderType(type, i-arrayoffsetX+ctmBaseX, j-arrayoffsetY+ctmBaseY, k-arrayoffsetZ+ctmBaseZ, worldObj);
//				}	
//			}	
//		}
//	}
	
	@Override
	public Block getBlock(int x, int y, int z)
	{
		int tx = x+arrayoffsetX - ctmBaseX +1;
		int ty = y+arrayoffsetY - ctmBaseY +1;
		int tz = z+arrayoffsetZ - ctmBaseZ +1;
		if(tx<0)
			return Blocks.air;
		else if(tx>=sizex)
			return Blocks.air;
		if(ty<0)
			return Blocks.air;
		else if(ty>=sizey)
			return Blocks.air;
		if(tz<0)
			return Blocks.air;
		else if(tz>=sizez)
			return Blocks.air;
		return BlockArray[tx][ty][tz].type;
	}
	public Block getBlockOrgPos(int x, int y, int z)
	{
		try{
			return BlockArray[x+1][y+1][z+1].type;
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			return Blocks.air;
		}
	}
	
	@Override
	public TileEntity getTileEntity(int x, int y, int z)
	{
		try{
			return BlockArray[x+arrayoffsetX-ctmBaseX+1][y+arrayoffsetY-ctmBaseY+1][z+arrayoffsetZ-ctmBaseZ+1].tile;
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			return null;
		}
	}
	
	
	@Override
	public int getLightBrightnessForSkyBlocks(int x, int y, int z, int meta)
	{
		int i1 = worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, xCoord, yCoord, zCoord);
		int j1 = worldObj.getSkyBlockTypeBrightness(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
		int j2 = getBlightness(x-ctmBaseX, y-ctmBaseY, z-ctmBaseZ);
		j1 += (int)((15-j1)*j2/15f);
//		if (j1 < meta)
//		{
//			j1 = meta;
//		}
//		MFW_Logger.debugInfo("lightlevel:"+j1);
		return i1 << 20 | j1 << 4;
//		return 15 << 20 | 15 << 4;
	}
	
	public int getBLockMetadata_AbsolutePos(int x, int y, int z)
	{
		try{
			return BlockArray[x+1][y+1][z+1].meta;
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			return 0;
		}
	}
	@Override
	public int getBlockMetadata(int x, int y, int z)
	{
		int tx = x+arrayoffsetX - ctmBaseX +1;
		int ty = y+arrayoffsetY - ctmBaseY +1;
		int tz = z+arrayoffsetZ - ctmBaseZ +1; 
		if(tx<0)return 0;
		else if(tx>=sizex)return 0;
		if(ty<0)return 0;
		else if(ty>=sizey)return 0;
		if(tz<0)return 0;
		else if(tz>=sizez)return 0;
		return BlockArray[tx][ty][tz].meta;
	}
	
	
	@Override
	public int isBlockProvidingPowerTo(int p_72879_1_, int p_72879_2_, int p_72879_3_, int p_72879_4_)
	{
		return 0;
	}
	
	
	@Override
	public boolean isAirBlock(int x, int y, int z)
	{
		int tx = x+arrayoffsetX - ctmBaseX +1;
		int ty = y+arrayoffsetY - ctmBaseY +1;
		int tz = z+arrayoffsetZ - ctmBaseZ +1;
		if(tx<0)return false;
		else if(tx>=sizex)return false;
		if(ty<0)return false;
		else if(ty>=sizey)return false;
		if(tz<0)return false;
		else if(tz>=sizez)return false;
		return BlockArray[tx][ty][tz].type instanceof BlockAir;
	}
	
	
	@Override
	public BiomeGenBase getBiomeGenForCoords(int p_72807_1_, int p_72807_2_)
	{
		return worldObj.getBiomeGenForCoords(xCoord, zCoord);
	}
	
	
	@Override
	public int getHeight()
	{
		return worldObj.getHeight();
	}
	
	
	@Override
	public boolean extendedLevelsInChunkCache()
	{
		return worldObj.extendedLevelsInChunkCache();
	}
	
	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default)
	{
		return getBlock(x, y, z).isSideSolid(this, x, y, z, side);
	}
	
	
	public int getBlockNum(int pass)
	{
		return pass==0 ? blocknum : blocknumPost;
	}
	
	public int getSize(int flag)
	{
		switch(flag)
		{
		case 0:return sizex;
		case 1:return sizey;
		case 2:return sizez;
		}
		return -1;
	}
	
	public int getOffset(int flag)
	{
		switch(flag)
		{
		case 0:return arrayoffsetX;
		case 1:return arrayoffsetY;
		case 2:return arrayoffsetZ;
		}
		return -10000;
	}
	
	public int getBaseForCTM(int flag)
	{
		switch(flag)
		{
		case 0:return ctmBaseX;
		case 1:return ctmBaseY;
		case 2:return ctmBaseZ;
		}
		return -10;
	}
	
//	public void writeToNBT(NBTTagCompound nbt)
//	{
//		nbt.setInteger("mtybr:sizex", sizex);
//    	nbt.setInteger("mtybr:sizey", sizey);
//    	nbt.setInteger("mtybr:sizez", sizez);
//    	nbt.setInteger("connectoffsetx", arrayoffsetX); // 中心位置（工作台の相対距離）
//		nbt.setInteger("connectoffsety", arrayoffsetY);
//		nbt.setInteger("connectoffsetz", arrayoffsetZ);
//	}
//	@SuppressWarnings("unused")

	
	public void setLightLevel(Block b, int x, int y, int z)
	{
		BlockArray[x+1][y+1][z+1].lightlevel = b.getLightValue();
	}
	
	public void diffLight()
	{
		for(int x=0; x<sizex; ++x)for(int y=0; y<sizey; ++y)for(int z=0; z<sizez; ++z)
		{
			int level = BlockArray[x][y][z].lightlevel-1;
			if(level <= 0)continue;
			setLightLevel(level, x-1, y, z);
			setLightLevel(level, x+1, y, z);
			setLightLevel(level, x, y-1, z);
			setLightLevel(level, x, y+1, z);
			setLightLevel(level, x, y, z-1);
			setLightLevel(level, x, y, z+1);
		}
	}
	// 再起呼び出し関数
	private void setLightLevel(int lightlevel, int x, int y, int z)
	{
		try{
			if(BlockArray[x][y][z].lightlevel < lightlevel)
			{
				BlockArray[x][y][z].lightlevel = lightlevel;
			}
		}catch(java.lang.ArrayIndexOutOfBoundsException e){ 
			return; 
		}
		int level = lightlevel - 4; //- BlockArray[x][y][z].type.getLightOpacity();
		if(level <= 0)return;
		if(BlockArray[x][y][z].type != Blocks.air) return;
		setLightLevel(level, x-1, y, z);
		setLightLevel(level, x+1, y, z);
		setLightLevel(level, x, y-1, z);
		setLightLevel(level, x, y+1, z);
		setLightLevel(level, x, y, z-1);
		setLightLevel(level, x, y, z+1);
	}
	
	public int getLightLevel(int x, int y, int z)
	{
		try{
			return BlockArray[x+arrayoffsetX+1][y+arrayoffsetY+1][z+arrayoffsetZ+1].lightlevel;
		}catch(java.lang.ArrayIndexOutOfBoundsException e){ 
			return 0; 
		} 
	}
	
	public int getBlightness(int x, int y, int z)
	{
//		if(true)return (x-ctmBaseX+arrayoffsetX)*10;
//		x+=arrayoffsetX;
//		y+=arrayoffsetY;
//		z+=arrayoffsetZ;

        if (this.getBlock(x, y, z).getUseNeighborBrightness())
        {
            int j2 = this.getLightLevel( x, y + 1, z);
            int j1 = this.getLightLevel( x + 1, y, z);
            int k1 = this.getLightLevel( x - 1, y, z);
            int l1 = this.getLightLevel( x, y, z + 1);
            int i2 = this.getLightLevel( x, y, z - 1);

            if (j1 > j2)
            {
                j2 = j1;
            }

            if (k1 > j2)
            {
                j2 = k1;
            }

            if (l1 > j2)
            {
                j2 = l1;
            }

            if (i2 > j2)
            {
                j2 = i2;
            }

            return j2;
        }
        else
        {
            return getLightLevel(x, y, z);
        }
    }
	
	
	public int getEntityNum()
	{
		return listEntity.size();
	}
}
