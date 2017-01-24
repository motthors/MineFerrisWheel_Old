package mfw.renderer;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import mfw._core.MFW_Core;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class renderBlockFerrisCore implements ISimpleBlockRenderingHandler{
	
//	private void render(Block block, RenderBlocks renderer, double x, double y, double z)
//	{
//		TextureManager texturemanager = TileEntityRendererDispatcher.instance.field_147553_e;
//        if (texturemanager != null) texturemanager.bindTexture(TextureMap.locationBlocksTexture);
//        
//    	Tessellator tessellator = Tessellator.instance;
//    	renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0, 1.0D);
//    	
//    	GL11.glScaled(1.5, 1.5, 1.5);
//    	GL11.glEnable(GL11.GL_CULL_FACE);
//    	GL11.glRotatef(45, 0, 1, 0);
//    	GL11.glRotatef(-90, 1, 0, 0);
//    	GL11.glTranslated(-0.5, -0.5, -0.5);
//    	tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
//    	IIcon iicon = Blocks.diamond_block.getIcon(0,0);
//    	double mu = iicon.getMinU();
//        double mv = iicon.getMinV();
//        double xu = iicon.getMaxU();
//        double xv = iicon.getMaxV();
//        double cu = (mu+xu)/2f;
//        double cv = (mv+xv)/2f;
//        
//        double _w1 = 0.92;
//        double _w2 = 0.08;
//             
//        {    
//        	tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
//        	tessellator.setNormal(0f, 1f, 0);
//        	tessellator.addVertexWithUV(x+0.5, y+0.5, z+_w1, cu, cv);
//        	tessellator.addVertexWithUV(x+0.5, y+_w2, z+0.5, mu, mv);
//        	tessellator.addVertexWithUV(x+_w1, y+0.5, z+0.5, xu, mv);
//        	tessellator.addVertexWithUV(x+0.5, y+_w1, z+0.5, xu, xv);
//        	tessellator.addVertexWithUV(x+_w2, y+0.5, z+0.5, mu, xv);
//        	tessellator.addVertexWithUV(x+0.5, y+_w2, z+0.5, mu, mv);
//        	tessellator.draw();
//        	tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
//         	tessellator.setNormal(0f, -1f, 0);
//        	tessellator.addVertexWithUV(x+0.5, y+0.5, z+_w2, cu, cv);
//        	tessellator.addVertexWithUV(x+0.5, y+_w2, z+0.5, mu, mv);
//        	tessellator.addVertexWithUV(x+_w2, y+0.5, z+0.5, mu, xv);
//        	tessellator.addVertexWithUV(x+0.5, y+_w1, z+0.5, xu, xv);
//        	tessellator.addVertexWithUV(x+_w1, y+0.5, z+0.5, xu, mv);
//        	tessellator.addVertexWithUV(x+0.5, y+_w2, z+0.5, mu, mv);
//        	tessellator.draw();
//        }
//        
//        iicon = Blocks.emerald_block.getIcon(0, 0);
//        mu = iicon.getMinU();
//        mv = iicon.getMinV();
//        xu = iicon.getMaxU();
//        xv = iicon.getMaxV();
//        cu = (mu+xu)/2f;
//        cv = (mv+xv)/2f;
//        {    
//        	tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
//        	tessellator.addVertexWithUV(x+0.5, y+0.5, z+0.0, cu, cv);
//        	tessellator.addVertexWithUV(x+0.5, y+0.0, z+0.5, mu, mv);
//        	tessellator.addVertexWithUV(x+1.0, y+0.5, z+0.5, xu, mv);
//        	tessellator.addVertexWithUV(x+0.5, y+1.0, z+0.5, xu, xv);
//        	tessellator.addVertexWithUV(x+0.0, y+0.5, z+0.5, mu, xv);
//        	tessellator.addVertexWithUV(x+0.5, y+0.0, z+0.5, mu, mv);
//        	tessellator.draw();    
//        	
//        	tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);                                                  
//        	tessellator.addVertexWithUV(x+0.5, y+0.5, z+1.0, cu, cv);
//        	tessellator.addVertexWithUV(x+0.5, y+0.0, z+0.5, mu, mv);
//        	tessellator.addVertexWithUV(x+0.0, y+0.5, z+0.5, mu, xv);
//        	tessellator.addVertexWithUV(x+0.5, y+1.0, z+0.5, xu, xv);
//        	tessellator.addVertexWithUV(x+1.0, y+0.5, z+0.5, xu, mv);
//        	tessellator.addVertexWithUV(x+0.5, y+0.0, z+0.5, mu, mv);
//        	tessellator.draw();
//        }
//        GL11.glDisable(GL11.GL_CULL_FACE);
//        GL11.glTranslated(0.5, 0.5, 0.5);
//        GL11.glRotatef(-90, 1, 0, 0);
//        GL11.glRotatef(45, 0, -1, 0);
//        GL11.glScaled(1/1.5, 1/1.5, 1/1.5);
//	}
//	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		if (modelId == this.getRenderId())
		{
//			Tessellator tessellator = Tessellator.instance;
//			tessellator.startDrawingQuads();
			
//			render(block, renderer,0,0,0);
//			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
//			tessellator.draw();
		}
	}
	
	//ワールドでのレンダー
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		if (modelId == this.getRenderId())
		{
			return true;
		}
		return false;
	}
		
	@Override
	public int getRenderId()
	{
		return MFW_Core.blockCoreRenderId;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return false;
	}
}
