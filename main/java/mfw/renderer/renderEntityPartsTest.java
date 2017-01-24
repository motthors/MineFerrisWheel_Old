package mfw.renderer;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class renderEntityPartsTest extends Render{

	@Override
	public void doRender(Entity e, double x, double y, double z, float f, float p)
	{
		super.renderOffsetAABB(e.boundingBox.getOffsetBoundingBox(-e.posX, -e.posY, -e.posZ), x, y, z);
		Entity[] ea = e.getParts();
		for(int i=0; i<ea.length; i++)
		{
			renderOffsetAABB(ea[i].boundingBox.getOffsetBoundingBox(-e.posX, -e.posY, -e.posZ), x, y, z);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		// TODO Auto-generated method stub
		return null;
	}

}
