package mfw.wrapper;

import net.minecraft.nbt.NBTTagCompound;

public interface IMultiThreadBlockCopy {

	public void Completing(NBTTagCompound nbt);
	public void abnormalEnd();
}
