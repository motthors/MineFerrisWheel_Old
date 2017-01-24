
package mfw.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import mfw._core.MFW_Core;
import mfw.gui.container.ContainerFerrisBasketConstructor;
import mfw.gui.container.ContainerFerrisConstructor;
import mfw.gui.container.ContainerFerrisCore;
import mfw.gui.container.ContainerFileManager;
import mfw.gui.container.DefContainer;
import mfw.manager.managerFrameLengthChange;
import mfw.tileEntity.TileEntityFerrisBasketConstructor;
import mfw.tileEntity.TileEntityFerrisConstructor;
import mfw.tileEntity.TileEntityFerrisCutter;
import mfw.tileEntity.TileEntityFerrisWheel;
import mfw.tileEntity.TileEntityFileManager;
import mfw.wrapper.Wrap_TileEntityChangeLimitWithKey;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public class MFW_GUIHandler implements IGuiHandler {
	
	/*サーバー側の処理*/
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile == null)return null;
		switch(ID)
		{
		case MFW_Core.GUIID_FerrisConstructor :
			return new ContainerFerrisConstructor(player.inventory, (TileEntityFerrisConstructor) tile);
		case MFW_Core.GUIID_FerrisBasketConstructor :
			return new ContainerFerrisBasketConstructor(player.inventory, (TileEntityFerrisBasketConstructor) tile);
		case MFW_Core.GUIID_FerrisCore :
			return new ContainerFerrisCore(player.inventory, ((TileEntityFerrisWheel) tile).getSelectedPartTile());
		case MFW_Core.GUIID_FerrisFileManager :
			return new ContainerFileManager(player.inventory, (TileEntityFileManager) tile);
		case MFW_Core.GUIID_FerrisCutter :
			return new DefContainer(x, y, z, tile);
		case MFW_Core.GUIID_FerrisStoryBoard :
			return new DefContainer(x, y, z, tile);
		}
		return null;
	}
    
	/*クライアント側の処理*/
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile==null)return null;
		if(tile instanceof Wrap_TileEntityChangeLimitWithKey)managerFrameLengthChange.INSTANCE.saveTile((Wrap_TileEntityChangeLimitWithKey) tile);
		else managerFrameLengthChange.INSTANCE.reset();
		switch(ID)
		{
		case MFW_Core.GUIID_FerrisConstructor :
			return new GUIFerrisConstructor(x, y, z, player.inventory, (TileEntityFerrisConstructor) tile);
		case MFW_Core.GUIID_FerrisBasketConstructor :
			return new GUIFerrisBasketConstructor(x, y, z, player.inventory, (TileEntityFerrisBasketConstructor) tile);
		case MFW_Core.GUIID_FerrisCore :
			return new GUIFerrisCore(x, y, z, player.inventory, ((TileEntityFerrisWheel) tile).getSelectedPartTile());
		case MFW_Core.GUIID_FerrisFileManager :
			return new GUIFileManager(x, y, z, player.inventory, (TileEntityFileManager) tile);
		case MFW_Core.GUIID_FerrisCutter :
			return new GUIFerrisCutter(x, y, z, player.inventory, (TileEntityFerrisCutter) tile);
		case MFW_Core.GUIID_FerrisStoryBoard :
			return new GUIStoryBoard(x, y, z, (TileEntityFerrisWheel) tile);
		}
		return null;
	}
}
