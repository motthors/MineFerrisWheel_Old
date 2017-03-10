package mfw.blocksReplication;
//package MTYlib.blocksReplication;
//
//import mfw.block.blockFerrisConnector;
//import net.minecraft.block.Block;
//import net.minecraft.client.Minecraft;
//import net.minecraft.world.World;
//
//public class rendererFerrisWheel extends ConstructorBlocksVertex{
//	
//	public MTYBlockAccess blockaccess; 
//
//	public rendererFerrisWheel(MTYBlockAccess ba)
//	{
//		super(ba);
//		blockaccess = ba;
//	}
//	
////	public void postInit()
////	{
////		super.postInit();
////		return;
////	}
//	
//	public int renderBlockByRenderType(Block b, int x, int y, int z, World world)
//	{
//		if(b.getRenderBlockPass() > 0)
//		{
//			postpass p = new postpass(x,y,z,b,b.getRenderBlockPass());
//			listPostPass.add(p);
//			return 0;
//		}
//		if(b instanceof blockFerrisConnector)
//		{
//			return 0;
//		}
//		World temp = Minecraft.getMinecraft().theWorld;
//		if(temp==null)return 0; //サーバー側はモデル構築しなくていいから抜ける
//		renderBlocks.renderBlockByRenderType(b, x, y, z);
//		return 1;
//	}
//	
//
//
//}
