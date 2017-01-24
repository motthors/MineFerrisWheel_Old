package mfw.handler;

import java.util.List;

import mfw.tileEntity.TileEntityChunkLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

public class handlerChunkLoading implements ForgeChunkManager.LoadingCallback{
	
	  public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world)
	  {
	    for (ForgeChunkManager.Ticket ticket : tickets)
	    {
	      int x = ticket.getModData().getInteger("xCoord");
	      int y = ticket.getModData().getInteger("yCoord");
	      int z = ticket.getModData().getInteger("zCoord");
	      TileEntity te = world.getTileEntity(x, y, z);
	      if ((te instanceof TileEntityChunkLoader)) {
	        ((TileEntityChunkLoader)te).forceChunkLoading(ticket);
	      }
	    }
	  }
	}