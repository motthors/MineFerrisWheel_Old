package mfw.renderer;

import mfw.entity.entityFerrisBasket;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class renderEntityFerrisBasket extends RenderEntity{
	public void doRender(entityFerrisBasket basket, double x, double y, double z, float f, float partialtick)
	{
		Tessellator.instance.setBrightness(15<<20|15<<4);
		basket.render(x, y, z, partialtick);
//		renderOffsetAABB(basket.boundingBox.getOffsetBoundingBox(-basket.posX, -basket.posY, -basket.posZ), x, y, z);
//		Entity[] ea = basket.getParts();
//		for(int i=0; i<ea.length; i++)
//		{
//			renderOffsetAABB(ea[i].boundingBox.getOffsetBoundingBox(-basket.posX, -basket.posY, -basket.posZ), x, y, z);
//		}
	}
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float partialtick) {
		doRender((entityFerrisBasket)entity, x, y, z, f, partialtick);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}
}
