package mfw.blocksReplication;

import org.lwjgl.opengl.GL11;

import mfw._core.MFW_Core;
import mfw.blocksReplication.MTYBlockAccess.renderPiece;
import mfw.handler.renderEventCompileWheel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ConstructorBlocksVertex /*extends RenderBlocks*/{

	protected RenderBlocks renderBlocks;
	protected MTYBlockAccess blockaccess;
	
	protected int GLCallList = -1;
	protected int GLCallListPost = -1;
	protected int GLCallListNum = 0;
	protected int GLCallListPostNum = 0;
	protected int isCompile = -2;		//flags  -2:before Blockaccess done copy -1:compiled  0:init compile  1~:compiling
	protected int isCompilePost = -2;	//flags  -2:before Blockaccess done copy -1:compiled  0:init compile  1~:compiling
	protected final int blockNumPer1List = 5000;
	protected int GLCallListCore = -1;
	protected int saveidx;
	protected int saveidxPost;
	
	protected float offsetX = 0;
	protected float offsetY = 0;
	protected float offsetZ = 0;
	protected boolean FlagDrawCore = true;
	


	
	public ConstructorBlocksVertex(MTYBlockAccess ba)
	{
		renderBlocks = new RenderBlocks(ba);
		blockaccess = ba;
	}
	
	public void setFlagDrawCore(boolean flag)
	{
		FlagDrawCore = flag;
	}
	
	public void CompileRnederInit()
	{
		int blocknum = blockaccess.getBlockNum(0);
//		MFW_Logger.debugInfo("pass0 blocknum:"+blocknum);
		////// 20000ブロックごとにリスト1つ
		GLCallListNum = (int) Math.ceil((double)blocknum/(double)blockNumPer1List);
		if(blocknum <= 0)
		{
			isCompile = -1;
			return;
		}
		this.GLCallList = GLAllocation.generateDisplayLists(GLCallListNum);
		saveidx = 0;
		CompileRneder();
	}
	
	public void CompileRnederPostInit()
	{
		int blocknum = blockaccess.getBlockNum(1);
//		MFW_Logger.debugInfo("pass1 blocknum:"+blocknum);
		////// 20000ブロックごとにリスト1つ
		GLCallListPostNum = (int) Math.ceil((double)blocknum/(double)blockNumPer1List);
		if(blocknum <= 0)
		{
			isCompilePost = -1;
			return;
		}
		this.GLCallListPost = GLAllocation.generateDisplayLists(GLCallListPostNum);
		saveidxPost = 0;
		CompileRenderPost();
	}
	
	public void CompileRneder()
	{
		GL11.glNewList(this.GLCallList+isCompile, GL11.GL_COMPILE_AND_EXECUTE);
		
		//////////////////////////////////描画
		GL11.glPushMatrix();
		TextureManager texturemanager = TileEntityRendererDispatcher.instance.field_147553_e;
        if (texturemanager != null) texturemanager.bindTexture(TextureMap.locationBlocksTexture);
        
        Tessellator.instance.startDrawingQuads();
		Tessellator.instance.setNormal(1, 0, 0);
		
		MTYBlockAccess baf = (MTYBlockAccess) blockaccess;
		GL11.glTranslated((double)-baf.ctmBaseX,(double)-baf.ctmBaseY,(double)-baf.ctmBaseZ);

		int size = blockaccess.listPrePass.size();
		int i;
		for(i=0; i<blockNumPer1List; ++i)
		{
			if(i+saveidx >= size)break;
			renderPiece pp = blockaccess.listPrePass.get(i+saveidx);
			Block b = pp.b;
			if(b==Blocks.air)continue;
			renderBlocks.renderBlockByRenderType(b, pp.x, pp.y, pp.z);
//			MFW_Logger.debugInfo("renderpostpass : "+b.getLocalizedName()+" . "+pp.x+" . "+pp.y+" . "+pp.z);
		}
		saveidx += i;
		
	    Tessellator.instance.draw();
		GL11.glPopMatrix();
		//////////////////////////////////描画おわり
		
		GL11.glEndList();
		++isCompile;
		if(isCompile>=GLCallListNum)isCompile=-1;
	}
	
	public void CompileRenderPost()
	{
		GL11.glNewList(this.GLCallListPost+isCompilePost, GL11.GL_COMPILE_AND_EXECUTE);
		
		//////////////////////////////////描画
		GL11.glPushMatrix();
		TextureManager texturemanager = TileEntityRendererDispatcher.instance.field_147553_e;
        if (texturemanager != null) texturemanager.bindTexture(TextureMap.locationBlocksTexture);
        
        Tessellator.instance.startDrawingQuads();
		Tessellator.instance.setNormal(1, 0, 0);
		
		MTYBlockAccess baf = (MTYBlockAccess) blockaccess;
		GL11.glTranslated((double)-baf.ctmBaseX,(double)-baf.ctmBaseY,(double)-baf.ctmBaseZ);
		int size = blockaccess.listPostPass.size();
		int i;
		for(i=0; i<blockNumPer1List; ++i)
		{
			if(i+saveidxPost >= size)break;
			renderPiece pp = blockaccess.listPostPass.get(i+saveidxPost);
			Block b = pp.b;
			if(b==Blocks.air)continue;
			renderBlocks.renderBlockByRenderType(b, pp.x, pp.y, pp.z);
//			MFW_Logger.debugInfo("renderpostpass : "+b.getLocalizedName()+" . "+pp.x+" . "+pp.y+" . "+pp.z);
		}
		saveidxPost += i;
	    Tessellator.instance.draw();
		GL11.glPopMatrix();
		//////////////////////////////////描画おわり
		
		GL11.glEndList();
		++isCompilePost;
		if(isCompilePost>=GLCallListPostNum)isCompilePost=-1;
	}
	
	public void CompileRenderCore()
	{
		GLCallListCore = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(this.GLCallListCore, GL11.GL_COMPILE);
		TextureManager texturemanager = TileEntityRendererDispatcher.instance.field_147553_e;
        if (texturemanager != null) texturemanager.bindTexture(TextureMap.locationBlocksTexture);
		renderBlockFerrisCore(0,0,0);
		GL11.glEndList();
	}
	
	int GLCallListEntity;
	int isRenderEntity = -2;
	public void CompileRenderEntity_test()
	{
		if(((MTYBlockAccess)blockaccess).getEntityNum() <= 0)return;
		GLCallListEntity = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(this.GLCallListEntity, GL11.GL_COMPILE);
		((MTYBlockAccess)blockaccess).renderEntity();
		GL11.glEndList();
		isRenderEntity = 1;
	}
	public void renderEntity_test()
	{
		//entity
//		if(isRenderEntity==1)GL11.glCallList(this.GLCallListEntity);
//		else CompileRenderEntity_test();
	}
	
	public void startRender()
	{
		isCompile = 0;
		isCompilePost = 0;
//		MFW_Logger.debugInfo("start render");
	}
	
	public void render()
	{
		GL11.glDisable(GL11.GL_LIGHTING);
		switch(isCompile)
		{
		case -2: break;
		case -1:
			for(int i=0;i<GLCallListNum;++i)GL11.glCallList(this.GLCallList+i);
			break;
		case 0 :
			CompileRenderCore();
			CompileRnederInit();
			break;
		default :
			int i;
			for(i=0;i<isCompile;++i)GL11.glCallList(this.GLCallList+i);
			CompileRneder();
			break;
		}
		
		switch(isCompilePost)
		{
		case -2: break;
		case -1: break;
		case 0 :
			CompileRnederPostInit();
			break;
		default :
			CompileRenderPost();
			break;
		}
		
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	public void render2()
	{
		GL11.glDisable(GL11.GL_LIGHTING);
		switch(isCompilePost)
		{
		case -2: break;
		case -1:
			for(int i=0;i<GLCallListPostNum;++i)GL11.glCallList(this.GLCallListPost+i);
			break;
		case 0 :
			break;
		default :
			for(int i=0;i<isCompilePost;++i)GL11.glCallList(this.GLCallListPost+i);
			break;
		}
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	public void renderCore()
	{
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glCallList(this.GLCallListCore);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	public void recompile()
	{
		isCompile = 0;
	}
	
	public void delete()
	{
		if(GLCallList>0)renderEventCompileWheel.setDeleteList(GLCallList,GLCallListNum);
		if(GLCallListPost>0)renderEventCompileWheel.setDeleteList(GLCallListPost,GLCallListPostNum);
		if(GLCallListCore>0)renderEventCompileWheel.setDeleteList(GLCallListCore,1);
	}
	
	public int renderBlockByRenderType(Block b, int x, int y, int z, World world){return 0;}
	public int renderBlockByRenderType_OrgPos(Block b, int x, int y, int z, World world)
	{
		MTYBlockAccess baf = (MTYBlockAccess) blockaccess;
		return renderBlockByRenderType(b, 
				x-baf.getOffset(0)+baf.getBaseForCTM(0),
				y-baf.getOffset(1)+baf.getBaseForCTM(1),
				z-baf.getOffset(2)+baf.getBaseForCTM(2),
				world);
	}

    public void renderBlockFerrisCore(int x, int y, int z)
    {
    	TextureManager texturemanager = TileEntityRendererDispatcher.instance.field_147553_e;
        if (texturemanager != null) texturemanager.bindTexture(TextureMap.locationBlocksTexture);
        
//    	GL11.glScaled(11, 11, 11);
    	if(FlagDrawCore==false)return;
    	Tessellator tessellator = Tessellator.instance;
    	MFW_Core.ferrisCore.setBlockBoundsBasedOnState(this.blockaccess, x, y, z);
    	renderBlocks.setRenderBoundsFromBlock(MFW_Core.ferrisCore);
        
//        GL11.GL_ENABLE_BIT;GL11.GL_QUADS
    	tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    	IIcon iicon = Blocks.diamond_block.getIcon(0,0);
    	double mu = iicon.getMinU();
        double mv = iicon.getMinV();
        double xu = iicon.getMaxU();
        double xv = iicon.getMaxV();
        double cu = (mu+xu)/2f;
        double cv = (mv+xv)/2f;
        
        double _w1 = 0.92;
        double _w2 = 0.08;
             
        {    
        	tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
        	tessellator.setNormal(0f, 1f, 0);
        	tessellator.addVertexWithUV(x+0.5, y+0.5, z+_w1, cu, cv);
        	tessellator.addVertexWithUV(x+0.5, y+_w2, z+0.5, mu, mv);
        	tessellator.addVertexWithUV(x+_w1, y+0.5, z+0.5, xu, mv);
        	tessellator.addVertexWithUV(x+0.5, y+_w1, z+0.5, xu, xv);
        	tessellator.addVertexWithUV(x+_w2, y+0.5, z+0.5, mu, xv);
        	tessellator.addVertexWithUV(x+0.5, y+_w2, z+0.5, mu, mv);
        	tessellator.draw();
        	tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
         	tessellator.setNormal(0f, -1f, 0);
        	tessellator.addVertexWithUV(x+0.5, y+0.5, z+_w2, cu, cv);
        	tessellator.addVertexWithUV(x+0.5, y+_w2, z+0.5, mu, mv);
        	tessellator.addVertexWithUV(x+_w2, y+0.5, z+0.5, mu, xv);
        	tessellator.addVertexWithUV(x+0.5, y+_w1, z+0.5, xu, xv);
        	tessellator.addVertexWithUV(x+_w1, y+0.5, z+0.5, xu, mv);
        	tessellator.addVertexWithUV(x+0.5, y+_w2, z+0.5, mu, mv);
        	tessellator.draw();
        }
        
        iicon = Blocks.emerald_block.getIcon(0, 0);
        mu = iicon.getMinU();
        mv = iicon.getMinV();
        xu = iicon.getMaxU();
        xv = iicon.getMaxV();
        cu = (mu+xu)/2f;
        cv = (mv+xv)/2f;
        {    
        	tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
        	tessellator.addVertexWithUV(x+0.5, y+0.5, z+0.0, cu, cv);
        	tessellator.addVertexWithUV(x+0.5, y+0.0, z+0.5, mu, mv);
        	tessellator.addVertexWithUV(x+1.0, y+0.5, z+0.5, xu, mv);
        	tessellator.addVertexWithUV(x+0.5, y+1.0, z+0.5, xu, xv);
        	tessellator.addVertexWithUV(x+0.0, y+0.5, z+0.5, mu, xv);
        	tessellator.addVertexWithUV(x+0.5, y+0.0, z+0.5, mu, mv);
        	tessellator.draw();    
        	
        	tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);                                                  
        	tessellator.addVertexWithUV(x+0.5, y+0.5, z+1.0, cu, cv);
        	tessellator.addVertexWithUV(x+0.5, y+0.0, z+0.5, mu, mv);
        	tessellator.addVertexWithUV(x+0.0, y+0.5, z+0.5, mu, xv);
        	tessellator.addVertexWithUV(x+0.5, y+1.0, z+0.5, xu, xv);
        	tessellator.addVertexWithUV(x+1.0, y+0.5, z+0.5, xu, mv);
        	tessellator.addVertexWithUV(x+0.5, y+0.0, z+0.5, mu, mv);
        	tessellator.draw();
        }
    }
}