package mfw.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import erc._core.ERC_Core;
import mfw.blocksReplication.ConstructorBlocksVertex;
import mfw.blocksReplication.MTYBlockAccess;
import mfw.entity.entityFerrisBasket;
import mfw.entity.entityPartSit;
import mfw.entity.entityPartSitEx;
import mfw.entity.entityPartsTestBase;
import mfw.handler.KeyHandlerClient;
import mfw.handler.handerClientRenderTick;
import mfw.handler.handlerClientConnected;
import mfw.handler.renderEventCompileWheel;
import mfw.handler.renderEventPlayerRot;
import mfw.renderer.renderBlockCutter;
import mfw.renderer.renderBlockFerrisCore;
import mfw.renderer.renderBlockSeatEx;
import mfw.renderer.renderEntityFerrisBasket;
import mfw.renderer.renderEntityNon;
import mfw.renderer.renderEntityPartsTest;
import mfw.renderer.renderTileEntityFerrisWheel;
import mfw.renderer.renderTileEntityLimitFrame;
import mfw.sound.FerrisFrameSound;
import mfw.tileEntity.TileEntityFerrisBasketConstructor;
import mfw.tileEntity.TileEntityFerrisConstructor;
import mfw.tileEntity.TileEntityFerrisCutter;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements IProxy{
	
	@Override
	public int getNewRenderType()
	{
		return RenderingRegistry.getNextAvailableRenderId();
	}
	

	@Override
	public void preInit()
	{	
		renderTileEntityLimitFrame rendererFrame = new renderTileEntityLimitFrame();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisConstructor.class, rendererFrame);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisBasketConstructor.class, rendererFrame);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisWheel.class, new renderTileEntityFerrisWheel());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFerrisCutter.class, rendererFrame);
		
		// Entityï`âÊìoò^
		RenderingRegistry.registerEntityRenderingHandler(entityFerrisBasket.class, new renderEntityFerrisBasket());
		RenderingRegistry.registerEntityRenderingHandler(entityPartsTestBase.class, new renderEntityPartsTest());
		renderEntityNon renderNon = new renderEntityNon();
		RenderingRegistry.registerEntityRenderingHandler(entityPartSit.class, renderNon);
		RenderingRegistry.registerEntityRenderingHandler(entityPartSitEx.class, renderNon);
		
		// ÉuÉçÉbÉNÉJÉXÉ^ÉÄÉåÉìÉ_Å[ÇÃìoò^
		RenderingRegistry.registerBlockHandler(new renderBlockCutter());
		RenderingRegistry.registerBlockHandler(new renderBlockFerrisCore());
		RenderingRegistry.registerBlockHandler(new renderBlockSeatEx());
		
		KeyHandlerClient.init();
//		// HandlerÇÃìoò^
//		Minecraft mc = Minecraft.getMinecraft();
//		ERC_Core.tickEventHandler = new ERC_ClientTickEventHandler(mc);
//		FMLCommonHandler.instance().bus().register(new ERC_TickEventHandler());
//		FMLCommonHandler.instance().bus().register(ERC_Core.tickEventHandler);
//		MinecraftForge.EVENT_BUS.register(new ERC_InputEventHandler(mc));
		
		if(Loader.isModLoaded(ERC_Core.MODID))
		{
			MinecraftForge.EVENT_BUS.register(new renderEventPlayerRot());
			FMLCommonHandler.instance().bus().register(new handerClientRenderTick(Minecraft.getMinecraft()));
		}
		MinecraftForge.EVENT_BUS.register( new renderEventCompileWheel());
	}

	@Override
	public void init()
	{
		FMLCommonHandler.instance().bus().register(new KeyHandlerClient());
		FMLCommonHandler.instance().bus().register(new handlerClientConnected());
	}

	@Override
	public void postInit() 
	{

	}
	
	public ConstructorBlocksVertex getrendererFerrisWheel(MTYBlockAccess ba)
	{
		return new ConstructorBlocksVertex(ba);
	}
//	public rendererFerrisBasket getrendererFerrisBasket(MTYBlockAccess ba, int side, int meta, float ox, float oy, float oz)
//	{
//		return new rendererFerrisBasket(ba,side,meta,ox,oy,oz);
//	}
	
	public Side checkSide()
	{
		return Side.CLIENT;
	}
	
	public EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().thePlayer;
	}

	public void PlaySound(FerrisFrameSound sound)
	{
		Minecraft.getMinecraft().getSoundHandler().playSound(sound);
	}
}