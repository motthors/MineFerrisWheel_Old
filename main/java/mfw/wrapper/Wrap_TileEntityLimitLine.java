package mfw.wrapper;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;

public abstract class Wrap_TileEntityLimitLine extends TileEntity {

	public abstract void render(Tessellator tess);
}
