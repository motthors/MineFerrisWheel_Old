package mfw.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class renderEntityNon extends Render {

		@Override
		public void doRender(Entity entity, double x, double y, double z, float yaw, float f) {
//			renderOffsetAABB(entity.boundingBox.getOffsetBoundingBox(-entity.posX, -entity.posY, -entity.posZ), x, y, z);
//			MFW_Logger.debugInfo("seat setPosition yf:"+f);
			return;
		}

		@Override
		protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
			return null;
		}


}
