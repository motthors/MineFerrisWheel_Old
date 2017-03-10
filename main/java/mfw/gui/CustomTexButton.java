package mfw.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mfw._core.MFW_Logger;
import mfw.storyboard.programpanel.IProgramPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class CustomTexButton extends GuiButtonExt{
	
	ResourceLocation customtexture;
	public IProgramPanel panel;
	
	public CustomTexButton(ResourceLocation tex, IProgramPanel panel, int id, int xPos, int yPos, int width, int height, String displayString) {
		super(id, xPos, yPos, width, height, displayString);
		customtexture = tex;
		this.panel = panel;
	}
	
	public void setTexture(ResourceLocation tex)
	{
		customtexture = tex;
	}

	public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
	{
		return;
	}
	public void drawButton2(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
    {
		if (this.visible)
        {
            FontRenderer fontrenderer = p_146112_1_.fontRenderer;
            p_146112_1_.getTextureManager().bindTexture(customtexture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawtexture(this.xPosition, 			this.yPosition, 20,  this.height, 	0, 		-0.5f+0.5f*k,  0.2f,  0.5f);
            this.drawtexture(this.xPosition+20, 		this.yPosition, 100, this.height, 	0.5f,   -0.5f+0.5f*k,  0.1f,  0.5f);
            this.drawtexture(this.xPosition+width-20, 	this.yPosition, 20,  this.height, 	0.8f,	-0.5f+0.5f*k,  0.2f,  0.5f);
            this.mouseDragged(p_146112_1_, p_146112_2_, p_146112_3_);
            int l = 14737632;

            if (packedFGColour != 0)
            {
                l = packedFGColour;
            }
            else if (!this.enabled)
            {
                l = 10526880;
            }
            else if (this.field_146123_n)
            {
                l = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
        }
    }
	
	public static void drawtexture(int xpos, int ypos, int lenx, int leny, float upos, float vpos, float pixlenx, float pixleny)
    {
        float f4 = 1.0F / pixlenx;
        float f5 = 1.0F / pixleny;
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        tess.addVertexWithUV((double)xpos, (double)(ypos + leny), 0.0D, (double)(upos), (double)((vpos + (float)pixleny)));
        tess.addVertexWithUV((double)(xpos + lenx), (double)(ypos + leny), 0.0D, (double)((upos + (float)pixlenx)), (double)((vpos + (float)pixleny)));
        tess.addVertexWithUV((double)(xpos + lenx), (double)ypos, 0.0D, (double)((upos + (float)pixlenx)), (double)(vpos));
        tess.addVertexWithUV((double)xpos, (double)ypos, 0.0D, (double)(upos), (double)(vpos));
        tess.draw();
    }
}
