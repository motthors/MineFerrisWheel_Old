package mfw.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class renderLimitFrameDrawFace extends renderFerrisLimitFrame{

	Vec3 vertexArrayF[] = new Vec3[24];
	
	public renderLimitFrameDrawFace() 
	{
		super();
		for(int i=0;i<24;++i) vertexArrayF[i] = Vec3.createVectorHelper(0, 0, 0);
	}
	
	@Override
	public void render(Tessellator tess)
	{
		super.render(tess);
		TextureManager texturemanager = TileEntityRendererDispatcher.instance.field_147553_e;
		texturemanager.bindTexture(TextureMap.locationBlocksTexture);
		double u = Blocks.wool.getIcon(0,4).getMinU();
		double v = Blocks.wool.getIcon(0,4).getMinV();
		double U = Blocks.wool.getIcon(0,4).getMaxU();
		double V = Blocks.wool.getIcon(0,4).getMaxV();
//		GL11.glBindTexture( GL11.GL_TEXTURE_2D, 0 );
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(100, 100, 100, 0.2f);
//		tess.setColorRGBA(255, 255, 255, 100);
		tess.startDrawingQuads();
		tess.setNormal(0, 1, 0);
		tess.setBrightness(15<<20|15<<4);
		for(int i=0;i<24;i+=4)
		{
			tess.addVertexWithUV(vertexArrayF[i  ].xCoord, vertexArrayF[i  ].yCoord, vertexArrayF[i  ].zCoord, u, v);
			tess.addVertexWithUV(vertexArrayF[i+1].xCoord, vertexArrayF[i+1].yCoord, vertexArrayF[i+1].zCoord, U, v);
			tess.addVertexWithUV(vertexArrayF[i+2].xCoord, vertexArrayF[i+2].yCoord, vertexArrayF[i+2].zCoord, U, V);
			tess.addVertexWithUV(vertexArrayF[i+3].xCoord, vertexArrayF[i+3].yCoord, vertexArrayF[i+3].zCoord, u, V);
		}
		tess.draw();
	}
	
	@Override
	public void createVertex(TileEntity tile, World world, double minx, double maxx, double miny, double maxy, double minz, double maxz)
	{
		super.createVertex(tile, world, minx, maxx, miny, maxy, minz, maxz);
		vertexArrayF[0] = posArray[8];
		vertexArrayF[1] = posArray[9];
		vertexArrayF[2] = posArray[10];
		vertexArrayF[3] = posArray[11];
		
		vertexArrayF[4] = posArray[8];
		vertexArrayF[5] = posArray[9];
		vertexArrayF[6] = posArray[13];
		vertexArrayF[7] = posArray[12];

		vertexArrayF[8] = posArray[8];
		vertexArrayF[9] = posArray[12];
		vertexArrayF[10] = posArray[15];
		vertexArrayF[11] = posArray[11];
		
		vertexArrayF[12] = posArray[9];
		vertexArrayF[13] = posArray[13];
		vertexArrayF[14] = posArray[14];
		vertexArrayF[15] = posArray[10];
		
		vertexArrayF[16] = posArray[10];
		vertexArrayF[17] = posArray[14];
		vertexArrayF[18] = posArray[15];
		vertexArrayF[19] = posArray[11];
		
		vertexArrayF[20] = posArray[12];
		vertexArrayF[21] = posArray[13];
		vertexArrayF[22] = posArray[14];
		vertexArrayF[23] = posArray[15];
		
	}
}
