package mfw.handler;

import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderWorldEvent;

@SideOnly(Side.CLIENT)
public class renderEventCompileWheel {

//	public static renderEventCompileWheel INSTANCE;
//	private static List<TileEntityFerrisWheel> listTileWheel = Collections.synchronizedList(new ArrayList<TileEntityFerrisWheel>());
	private static CopyOnWriteArrayList<Integer> deleteListNums = new CopyOnWriteArrayList<Integer>();
	private static CopyOnWriteArrayList<Integer> deleteListSum = new CopyOnWriteArrayList<Integer>();
	
//	synchronized
//	public static void setTileWheel(TileEntityFerrisWheel tile)
//	{
//		listTileWheel.add(tile);
//	}
	synchronized
	public static void setDeleteList(int idx, int num)
	{
		deleteListNums.add(idx);
		deleteListSum.add(num);
	}
	
	@SubscribeEvent
	synchronized
	public void render(RenderWorldEvent.Post event)
	{
//		if(listTileWheel.size()>0)
//		{
//			for(TileEntityFerrisWheel tile : listTileWheel)
//			{
////				tile.CompileRneder();
//			}
//			listTileWheel.clear();
//			MFW_Logger.debugInfo("***********************************Construct Wheel Vertex");
//		}
		if(deleteListNums.size()>0)
		{
			for(int i=0; i<deleteListNums.size(); ++i)
			{
				GL11.glDeleteLists(deleteListNums.get(i), deleteListSum.get(i));
//				MFW_Logger.debugInfo("delete GLCompileList Num : "+deleteListSum.get(i));
			}
			deleteListNums.clear();
			deleteListSum.clear();
		}
	}
}
