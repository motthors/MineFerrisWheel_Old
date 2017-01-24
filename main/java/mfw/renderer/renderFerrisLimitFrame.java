package mfw.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class renderFerrisLimitFrame {

	// 描画関連パラメタ
	Vec3 posArray[] = new Vec3[16];
	Vec3 vertexArray[] = new Vec3[38];
	
	public renderFerrisLimitFrame()
	{
		for(int i=0;i<16;++i) posArray[i] = Vec3.createVectorHelper(0, 0, 0);
		for(int i=0;i<38;++i) vertexArray[i] = Vec3.createVectorHelper(0, 0, 0);
	}

	public void createVertex(TileEntity tile, World world, double minx, double maxx, double miny, double maxy, double minz, double maxz)
	{
		minx += 0.1;
		miny += 0.1;
		minz += 0.1;
		maxx -= 0.1;
		maxy -= 0.1;
		maxz -= 0.1;
		for(int i=0;i<2;++i)
		{
			posArray[0+i*8] = Vec3.createVectorHelper(maxx, maxy, maxz);
			posArray[1+i*8] = Vec3.createVectorHelper(maxx, maxy, minz);
			posArray[2+i*8] = Vec3.createVectorHelper(minx, maxy, minz);
			posArray[3+i*8] = Vec3.createVectorHelper(minx, maxy, maxz);
			posArray[4+i*8] = Vec3.createVectorHelper(maxx, miny, maxz);
			posArray[5+i*8] = Vec3.createVectorHelper(maxx, miny, minz);
			posArray[6+i*8] = Vec3.createVectorHelper(minx, miny, minz);
			posArray[7+i*8] = Vec3.createVectorHelper(minx, miny, maxz);
			minx -= 0.2;
			miny -= 0.2;
			minz -= 0.2;
			maxx += 0.2;
			maxy += 0.2;
			maxz += 0.2;
		}
		int j=0;
		// 上側四角1周
		vertexArray[j++] = posArray[0];
		vertexArray[j++] = posArray[0+8];
		vertexArray[j++] = posArray[1];
		vertexArray[j++] = posArray[1+8];
		vertexArray[j++] = posArray[2];
		vertexArray[j++] = posArray[2+8];
		vertexArray[j++] = posArray[3];
		vertexArray[j++] = posArray[3+8];
		vertexArray[j++] = posArray[0];
		vertexArray[j++] = posArray[0+8];
		// 横側追加
		vertexArray[j++] = posArray[4];
		vertexArray[j++] = posArray[4+8];
		vertexArray[j++] = posArray[5];
		vertexArray[j++] = posArray[5+8];
		vertexArray[j++] = posArray[1];
		vertexArray[j++] = posArray[1+8];
		// ジャンプ
		vertexArray[j++] = posArray[1+8];
		vertexArray[j++] = posArray[2];
		// L字1回目
		vertexArray[j++] = posArray[2];
		vertexArray[j++] = posArray[2+8];
		vertexArray[j++] = posArray[6];
		vertexArray[j++] = posArray[6+8];
		vertexArray[j++] = posArray[5];
		vertexArray[j++] = posArray[5+8];
		// ジャンプ
		vertexArray[j++] = posArray[5+8];
		vertexArray[j++] = posArray[3];
		// L字1回目
		vertexArray[j++] = posArray[3];
		vertexArray[j++] = posArray[3+8];
		vertexArray[j++] = posArray[7];
		vertexArray[j++] = posArray[7+8];
		vertexArray[j++] = posArray[4];
		vertexArray[j++] = posArray[4+8];
		// ジャンプ
		vertexArray[j++] = posArray[4+8];
		vertexArray[j++] = posArray[7];
		// ラスト１辺
		vertexArray[j++] = posArray[7];
		vertexArray[j++] = posArray[7+8];
		vertexArray[j++] = posArray[6];
		vertexArray[j++] = posArray[6+8];
	}
	
	public void render(Tessellator tess)
	{
		tess.startDrawing(GL11.GL_TRIANGLE_STRIP);
		tess.setNormal(0, 1, 0);
		for(Vec3 v : vertexArray)
		{
			tess.addVertexWithUV(v.xCoord, v.yCoord, v.zCoord, 0.0d, 1d);
		}
		tess.draw();
	}
}
