package mfw.renderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Core;
import mfw.block.blockSyabuNabe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class renderBlockSyabuNabe  implements ISimpleBlockRenderingHandler{

	private void render(Block block, RenderBlocks renderer, int x, int y, int z, int meta)
	{
		//renderer.renderStandardBlock(block, x, y, z);
	    Tessellator tessellator = Tessellator.instance;
	    //tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
		int l = block.colorMultiplier(renderer.blockAccess, x, y, z);
		float f = (float)(l >> 16 & 255) / 255.0F;
		float f1 = (float)(l >> 8 & 255) / 255.0F;
		float f2 = (float)(l & 255) / 255.0F;
		
		double mx, my, mz, xx, xy, xz;
		
		tessellator.setColorOpaque_F(f, f1, f2);
		double mu=0, mv=0, xu=0, xv=0;
		IIcon icon = block.getBlockTextureFromSide(2);
		mu = icon.getMinU(); mv = icon.getMinV(); xu = icon.getMaxU(); xv = icon.getMaxV();
		double du = (xu - mu)*(1.0/16.0);
		double dv = (xv - mv)*(1.0/16.0);
		mu += du; xu -= du; mv += dv; xv -= dv;
		mx = x + blockSyabuNabe.psidebase;
		my = y+blockSyabuNabe.pbottom;
		mz = z + blockSyabuNabe.psidebase;
		xx = x + blockSyabuNabe.pwidth;
		xy = y+blockSyabuNabe.ptop;
		xz = z + blockSyabuNabe.pwidth;
		
		//side
		tessellator.addVertexWithUV(mx, my, mz, xu, xv);
		tessellator.addVertexWithUV(mx, my, xz, xu, mv);
		tessellator.addVertexWithUV(mx, xy, xz, mu, mv);
		tessellator.addVertexWithUV(mx, xy, mz, mu, xv);
		
		tessellator.addVertexWithUV(xx, xy, mz, xu, xv);
		tessellator.addVertexWithUV(xx, xy, xz, xu, mv);
		tessellator.addVertexWithUV(xx, my, xz, mu, mv);
		tessellator.addVertexWithUV(xx, my, mz, mu, xv);
		
		tessellator.addVertexWithUV(mx, xy, xz, xu, xv);
		tessellator.addVertexWithUV(mx, my, xz, xu, mv);
		tessellator.addVertexWithUV(xx, my, xz, mu, mv);
		tessellator.addVertexWithUV(xx, xy, xz, mu, xv);
		
		tessellator.addVertexWithUV(mx, my, mz, xu, xv);
		tessellator.addVertexWithUV(mx, xy, mz, xu, mv);
		tessellator.addVertexWithUV(xx, xy, mz, mu, mv);
		tessellator.addVertexWithUV(xx, my, mz, mu, xv);
		
		//top
//		tessellator.addVertexWithUV(mx, xy, mz, xu, xv);
//		tessellator.addVertexWithUV(mx, xy, xz, xu, mv);
//		tessellator.addVertexWithUV(xx, xy, xz, mu, mv);
//		tessellator.addVertexWithUV(xx, xy, mz, mu, xv);
		//左
		tessellator.addVertexWithUV(mx, xy, mz, xu-du*15, xv);
		tessellator.addVertexWithUV(mx, xy, xz-(1f/16f), xu-du*15, mv);
		tessellator.addVertexWithUV(xx-(11f/16f), xy, xz-(1f/16f), mu, mv);
		tessellator.addVertexWithUV(xx-(11f/16f), xy, mz, mu, xv);
		//下
		tessellator.addVertexWithUV(mx, xy, mz+(11f/16f), xu, xv-dv*15);
		tessellator.addVertexWithUV(mx, xy, xz, xu, mv);
		tessellator.addVertexWithUV(xx-(1f/16f), xy, xz, mu, mv);
		tessellator.addVertexWithUV(xx-(1f/16f), xy, mz+(11f/16f), mu, xv-dv*15);
		//右
		tessellator.addVertexWithUV(mx+(11f/16f), xy, mz+(1f/16f), xu-du*15, xv);
		tessellator.addVertexWithUV(mx+(11f/16f), xy, xz, xu-du*15, mv);
		tessellator.addVertexWithUV(xx, xy, xz, mu, mv);
		tessellator.addVertexWithUV(xx, xy, mz+(1f/16f), mu, xv);
		//上
		tessellator.addVertexWithUV(mx+(1f/16f), xy, mz, xu, xv-dv*15);
		tessellator.addVertexWithUV(mx+(1f/16f), xy, xz-(11f/16f), xu, mv);
		tessellator.addVertexWithUV(xx, xy, xz-(11f/16f), mu, mv);
		tessellator.addVertexWithUV(xx, xy, mz, mu, xv-dv*15);
		
		//bottom
		tessellator.addVertexWithUV(xx, my, mz, xu, xv);
		tessellator.addVertexWithUV(xx, my, xz, xu, mv);
		tessellator.addVertexWithUV(mx, my, xz, mu, mv);
		tessellator.addVertexWithUV(mx, my, mz, mu, xv);
		
		//なべ底
		tessellator.addVertexWithUV(mx+(1f/16f), y+blockSyabuNabe.pNabeSoko, mz+(1f/16f), xu, xv);
		tessellator.addVertexWithUV(mx+(1f/16f), y+blockSyabuNabe.pNabeSoko, xz-(1f/16f), xu, mv);
		tessellator.addVertexWithUV(xx-(1f/16f), y+blockSyabuNabe.pNabeSoko, xz-(1f/16f), mu, mv);
		tessellator.addVertexWithUV(xx-(1f/16f), y+blockSyabuNabe.pNabeSoko, mz+(1f/16f), mu, xv);

		//鍋内side
		tessellator.addVertexWithUV(mx+11f/16f, my, mz, xu, xv);
		tessellator.addVertexWithUV(mx+11f/16f, my, xz, xu, mv);
		tessellator.addVertexWithUV(mx+11f/16f, xy, xz, mu, mv);
		tessellator.addVertexWithUV(mx+11f/16f, xy, mz, mu, xv);
		
		tessellator.addVertexWithUV(xx-11f/16f, xy, mz, xu, xv);
		tessellator.addVertexWithUV(xx-11f/16f, xy, xz, xu, mv);
		tessellator.addVertexWithUV(xx-11f/16f, my, xz, mu, mv);
		tessellator.addVertexWithUV(xx-11f/16f, my, mz, mu, xv);
		
		tessellator.addVertexWithUV(mx, xy, xz-11f/16f, xu, xv);
		tessellator.addVertexWithUV(mx, my, xz-11f/16f, xu, mv);
		tessellator.addVertexWithUV(xx, my, xz-11f/16f, mu, mv);
		tessellator.addVertexWithUV(xx, xy, xz-11f/16f, mu, xv);
		
		tessellator.addVertexWithUV(mx, my, mz+11f/16f, xu, xv);
		tessellator.addVertexWithUV(mx, xy, mz+11f/16f, xu, mv);
		tessellator.addVertexWithUV(xx, xy, mz+11f/16f, mu, mv);
		tessellator.addVertexWithUV(xx, my, mz+11f/16f, mu, xv);
		
        if (meta > 0)
        {
            IIcon iicon = BlockLiquid.getLiquidIcon("water_still");
            mu = iicon.getMinU(); mv = iicon.getMinV(); xu = iicon.getMaxU(); xv = iicon.getMaxV();
//            renderer.renderFaceYPos(block, (double)x, y+blockSyabuNabe.pwaterheight, (double)z, iicon);
    		tessellator.addVertexWithUV(mx, y+blockSyabuNabe.pwaterheight, mz, xu, xv);
    		tessellator.addVertexWithUV(mx, y+blockSyabuNabe.pwaterheight, xz, xu, mv);
    		tessellator.addVertexWithUV(xx, y+blockSyabuNabe.pwaterheight, xz, mu, mv);
    		tessellator.addVertexWithUV(xx, y+blockSyabuNabe.pwaterheight, mz, mu, xv);
        }

	}

	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
	{
		if (modelId == this.getRenderId())
		{
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			render(block, renderer,0,0,0,0);
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
			int meta = renderer.blockAccess.getBlockMetadata(x, y, z);
			render(block, renderer,x,y,z,meta);
			return true;
		}
		return false;
	}
		
	@Override
	public int getRenderId()
	{
		return MFW_Core.blockSyabuNabeId;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId)
	{
		return true;
	}
}
