package mfw.renderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Core;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class renderBlockCutter  implements ISimpleBlockRenderingHandler{

	private void render(Block block, RenderBlocks renderer, double x, double y, double z)
	{
		Tessellator tessellator = Tessellator.instance;

		IIcon icon = renderer.getBlockIconFromSideAndMetadata(block, 0, 0);
		double mu = icon.getMinU();
		double mv = icon.getMinV();
		double u = icon.getMaxU();
		double v = icon.getMaxV();
		double x1 = x;
		double y1 = y;
		double z1 = z;
		double x2 = x+1d;
		double y2 = y+1d;
		double z2 = z+1d;
		
		renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0, 1.0D);

		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		tessellator.addVertexWithUV(x1, y1, z1, u, v);
		tessellator.addVertexWithUV(x2, y1, z1, mu, v);
		tessellator.addVertexWithUV(x2, y1, z2, mu, mv);
		tessellator.addVertexWithUV(x1, y1, z2, u, mv);
		
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(x1, y2, z1, u, v);
		tessellator.addVertexWithUV(x1, y2, z2, mu, v);
		tessellator.addVertexWithUV(x2, y2, z2, mu, mv);
		tessellator.addVertexWithUV(x2, y2, z1, u, mv);
		
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV(x1, y1, z1, u, v);
		tessellator.addVertexWithUV(x1, y2, z1, mu, v);
		tessellator.addVertexWithUV(x2, y2, z1, mu, mv);
		tessellator.addVertexWithUV(x2, y1, z1, u, mv);
		
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(x1, y1, z2, u, v);
		tessellator.addVertexWithUV(x2, y1, z2, mu, v);
		tessellator.addVertexWithUV(x2, y2, z2, mu, mv);
		tessellator.addVertexWithUV(x1, y2, z2, u, mv);
		
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(x1, y1, z1, u, v);
		tessellator.addVertexWithUV(x1, y1, z2, mu, v);
		tessellator.addVertexWithUV(x1, y2, z2, mu, mv);
		tessellator.addVertexWithUV(x1, y2, z1, u, mv);
		
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(x2, y1, z1, u, v);
		tessellator.addVertexWithUV(x2, y2, z1, mu, v);
		tessellator.addVertexWithUV(x2, y2, z2, mu, mv);
		tessellator.addVertexWithUV(x2, y1, z2, u, mv);
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		if (modelId == this.getRenderId())
		{
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			render(block, renderer,0,0,0);
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			tessellator.draw();
		}
	}
	
	//ワールドでのレンダー
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		if (modelId == this.getRenderId())
		{
			render(block, renderer,x,y,z);
			return true;
		}
		return false;
	}
		
	@Override
	public int getRenderId()
	{
		return MFW_Core.blockCutterRenderId;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}
}
