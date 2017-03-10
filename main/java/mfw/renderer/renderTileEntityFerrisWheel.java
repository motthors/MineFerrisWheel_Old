package mfw.renderer;

import org.lwjgl.opengl.GL11;

import mfw.asm.renderPass1Hook;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class renderTileEntityFerrisWheel extends TileEntitySpecialRenderer {
	
	public void renderTileEntityAt(TileEntityFerrisWheel t, double x, double y, double z, float f)
	{
//		this.bindTexture(TextureMap.locationBlocksTexture);
//		GL11.glPushMatrix();
//		GL11.glScaled(0.1, 0.1, 0.1);
//		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
//		t.render(tessellator,f);
		GL11.glPushMatrix();
//		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		GL11.glTranslated(x, y, z);
      	t.renderThis(x,y,z,f);
      	renderPass1Hook.add(t);
      	GL11.glPopMatrix();
//      	GL11.glPopMatrix();
	}
	
	public void p_bindTexture(ResourceLocation texture){this.bindTexture(texture);}
	
	private void DrawArrow(Tessellator tess, Vec3 vec)
	{
      	tess.startDrawing(GL11.GL_TRIANGLES);
      	tess.addVertexWithUV(0.2d, 0d, 0.2d, 0.0d, 0.0d);
      	tess.addVertexWithUV(vec.xCoord*3d, vec.yCoord*3d, vec.zCoord*3d, 0.0d, 0.0d);
      	tess.addVertexWithUV(-0.2d, 0d, -0.2d, 0.0d, 0.0d);
      	tess.draw();
	}

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		renderTileEntityAt((TileEntityFerrisWheel)t,x,y,z,f);
	}
}
