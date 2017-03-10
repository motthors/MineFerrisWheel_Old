package mfw.blocksReplication;
//package MTYlib.blocksReplication;
//
//import mfw.block.blockFerrisConnector;
//import net.minecraft.block.Block;
//import net.minecraft.world.World;
//
//public class rendererFerrisBasket extends ConstructorBlocksVertex{
//	public MTYBlockAccess blockaccess; 
//	
//	
////	connectPos connector;
//
//	
//	public rendererFerrisBasket(MTYBlockAccess ba, int side, int meta, float ox, float oy, float oz)
//	{
//		super(ba);
//		blockaccess = ba;
//		offsetX = ox;
//		offsetY = oy;
//		offsetZ = oz;
//	}
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
////			connector = new connectPos();
////			connector.x=x; connector.y=y; connector.z=z;
////			connector.len = (float) Math.sqrt(x*x+y*y+z*z);
////			int w=1;
////			int u=y;
////			switch(meta)
////			{
////			case 0: case 1: w=x; u=z; break;
////			case 2: case 3: w=x; break;
////			case 4: case 5: w=z; break;
////			}
////			connector.angle = (float) Math.atan2(-w, u);
//			super.renderBlockFerrisCore(x, y, z);
//			return 1;
//		}
//		if(world.isRemote == false)return 0; //サーバー側はモデル構築しなくていいから抜ける
//		renderBlocks.renderBlockByRenderType(b, x, y, z);
//		return 1;
//	}
//	
//}
