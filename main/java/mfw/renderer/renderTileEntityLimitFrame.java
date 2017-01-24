package mfw.renderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Core;
import mfw.wrapper.Wrap_TileEntityLimitLine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class renderTileEntityLimitFrame extends TileEntitySpecialRenderer{
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(MFW_Core.MODID,"textures/blocks/ferrisConstructor.png");
	
	public void renderTileEntityAt(Wrap_TileEntityLimitLine t, double x, double y, double z, float f)
	{
		Tessellator tessellator = Tessellator.instance;
		this.bindTexture(TEXTURE);
		GL11.glDisable(GL11.GL_CULL_FACE); // カリングOFF
		GL11.glPushMatrix();
		GL11.glTranslated(x+0.5, y+0.5, z+0.5);
//		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

		t.render(tessellator);

      	GL11.glEnable(GL11.GL_CULL_FACE); // カリングON
      	GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		renderTileEntityAt((Wrap_TileEntityLimitLine)t,x,y,z,f);
	}
}
