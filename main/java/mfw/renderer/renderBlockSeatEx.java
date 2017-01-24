package mfw.renderer;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import mfw._core.MFW_Core;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public class renderBlockSeatEx implements ISimpleBlockRenderingHandler {

	private void render(Block block, RenderBlocks renderer, double x, double y, double z, int meta)
	{
		Tessellator tessellator = Tessellator.instance;
		IIcon icon = renderer.getBlockIconFromSideAndMetadata(block, 0, 0);
		double mu=0, mv=0, xu=0, xv=0;
		mu = icon.getMinU(); mv = icon.getMinV(); xu = icon.getMaxU(); xv = icon.getMaxV(); 
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		double x2 = x+1d;
		double y2 = y+0.0625d;
		double z2 = z+1d;
		switch(meta)
		{
		default :
		case 0 :
			tessellator.addVertexWithUV(x, y2, z, xu, xv);
			tessellator.addVertexWithUV(x, y2, z2, xu, mv);
			tessellator.addVertexWithUV(x2, y2, z2, mu, mv);
			tessellator.addVertexWithUV(x2, y2, z, mu, xv);
		break;
		case 1 : 
			tessellator.addVertexWithUV(x, y2, z, xu, mv);
			tessellator.addVertexWithUV(x, y2, z2, mu, mv);
			tessellator.addVertexWithUV(x2, y2, z2, mu, xv);
			tessellator.addVertexWithUV(x2, y2, z, xu, xv);
		break;
		case 2 : 
			tessellator.addVertexWithUV(x, y2, z, mu, mv);
			tessellator.addVertexWithUV(x, y2, z2, mu, xv);
			tessellator.addVertexWithUV(x2, y2, z2, xu, xv);
			tessellator.addVertexWithUV(x2, y2, z, xu, mv);
		break;
		case 3 : 
			tessellator.addVertexWithUV(x, y2, z, mu, xv);
			tessellator.addVertexWithUV(x, y2, z2, xu, xv);
			tessellator.addVertexWithUV(x2, y2, z2, xu, mv);
			tessellator.addVertexWithUV(x2, y2, z, mu, mv);
		break;
		}
		
		
//		renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.0625, 1.0D);
		
//		Tessellator tessellator = Tessellator.instance;
//
//		IIcon icon = renderer.getBlockIconFromSideAndMetadata(block, 0, 0);
//		double mu = icon.getMinU();
//		double mv = icon.getMinV();
//		double u = icon.getMaxU();
//		double v = icon.getMaxV();
//		double x1 = x;
//		double y1 = y;
//		double z1 = z;
//		double x2 = x+1d;
//		double y2 = y+1d;
//		double z2 = z+1d;
//		
//		renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0, 1.0D);
//
//		tessellator.setNormal(0.0F, -1.0F, 0.0F);
//		tessellator.addVertexWithUV(x1, y1, z1, u, v);
//		tessellator.addVertexWithUV(x2, y1, z1, mu, v);
//		tessellator.addVertexWithUV(x2, y1, z2, mu, mv);
//		tessellator.addVertexWithUV(x1, y1, z2, u, mv);
//		
//		tessellator.setNormal(0.0F, 1.0F, 0.0F);
//		tessellator.addVertexWithUV(x1, y2, z1, u, v);
//		tessellator.addVertexWithUV(x1, y2, z2, mu, v);
//		tessellator.addVertexWithUV(x2, y2, z2, mu, mv);
//		tessellator.addVertexWithUV(x2, y2, z1, u, mv);
//		
//		tessellator.setNormal(0.0F, 0.0F, -1.0F);
//		tessellator.addVertexWithUV(x1, y1, z1, u, v);
//		tessellator.addVertexWithUV(x1, y2, z1, mu, v);
//		tessellator.addVertexWithUV(x2, y2, z1, mu, mv);
//		tessellator.addVertexWithUV(x2, y1, z1, u, mv);
//		
//		tessellator.setNormal(0.0F, 0.0F, 1.0F);
//		tessellator.addVertexWithUV(x1, y1, z2, u, v);
//		tessellator.addVertexWithUV(x2, y1, z2, mu, v);
//		tessellator.addVertexWithUV(x2, y2, z2, mu, mv);
//		tessellator.addVertexWithUV(x1, y2, z2, u, mv);
//		
//		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
//		tessellator.addVertexWithUV(x1, y1, z1, u, v);
//		tessellator.addVertexWithUV(x1, y1, z2, mu, v);
//		tessellator.addVertexWithUV(x1, y2, z2, mu, mv);
//		tessellator.addVertexWithUV(x1, y2, z1, u, mv);
//		
//		tessellator.setNormal(1.0F, 0.0F, 0.0F);
//		tessellator.addVertexWithUV(x2, y1, z1, u, v);
//		tessellator.addVertexWithUV(x2, y2, z1, mu, v);
//		tessellator.addVertexWithUV(x2, y2, z2, mu, mv);
//		tessellator.addVertexWithUV(x2, y1, z2, u, mv);
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
//		if (modelId == this.getRenderId())
//		{
//			Tessellator tessellator = Tessellator.instance;
//			tessellator.startDrawingQuads();
//			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
//			render(block, renderer,0,0,0,0);
//			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
//			tessellator.draw();
//		}
	}
	
	//ワールドでのレンダー
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		if (modelId == this.getRenderId())
		{
			render(block, renderer, x, y, z, world.getBlockMetadata(x, y, z));
			return true;
		}
		return false;
	}
	
	@Override
	public int getRenderId()
	{
		return MFW_Core.blockSeatExId;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return false;
	}
}
