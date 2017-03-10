package mfw._core;

import java.io.File;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import erc._core.ERC_Core;
import mfw.block.blockChunkLoader;
import mfw.block.blockFerrisBasketConstructor;
import mfw.block.blockFerrisConnector;
import mfw.block.blockFerrisConstructor;
import mfw.block.blockFerrisCore;
import mfw.block.blockFerrisCutter;
import mfw.block.blockFerrisRemoteController;
import mfw.block.blockFerrisSupporter;
import mfw.block.blockFileManager;
import mfw.block.blockSeatToSitDown;
import mfw.block.blockSeatToSitEx;
import mfw.entity.entityFerrisBasket;
import mfw.entity.entityPartSit;
import mfw.entity.entityPartSitEx;
import mfw.entity.entityParts;
import mfw.entity.entityPartsTestBase;
import mfw.gui.MFW_GUIHandler;
import mfw.handler.handlerChunkLoading;
import mfw.item.itemBlockFerrisCore;
import mfw.item.itemBlockRemoteController;
import mfw.item.itemBlockSeatToSitDown;
import mfw.item.itemFerrisBasket;
import mfw.item.itemFerrisSeed;
import mfw.message.MFW_PacketHandler;
import mfw.proxy.IProxy;
import mfw.sound.SoundManager;
import mfw.tileEntity.TileEntityChunkLoader;
import mfw.tileEntity.TileEntityFerrisBasketConstructor;
import mfw.tileEntity.TileEntityFerrisConstructor;
import mfw.tileEntity.TileEntityFerrisCutter;
import mfw.tileEntity.TileEntityFerrisWheel;
import mfw.tileEntity.TileEntityFileManager;
import mfw.tileEntity.tileEntityRemoteController;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeChunkManager;

@Mod( 
		modid = MFW_Core.MODID, 
		name = "Mine Ferris Wheel", 
		version = MFW_Core.VERSION,
		dependencies = "required-after:Forge@[10.12.1.1090,);after:erc@[1.30,)",
		useMetadata = true
		)
@TransformerExclusions
public class MFW_Core {
	public static final String MODID = "mfw";
	public static final String VERSION = "1.99beta3";

	
	//proxy////////////////////////////////////////
	@SidedProxy(clientSide = "mfw.proxy.ClientProxy", serverSide = "mfw.proxy.ServerProxy")
	public static IProxy proxy;
	
	//観覧車ブロック/////////////////////////////////////////
	public static Block ferrisConstrcutor = new blockFerrisConstructor();
	public static Block ferrisBasketConstrcutor = new blockFerrisBasketConstructor();
	public static Block ferrisCore = new blockFerrisCore();
	public static Block ferrisSupporter = new blockFerrisSupporter();
	public static Block ferrisConnector = new blockFerrisConnector();
	public static Block ferrisRemoteController = new blockFerrisRemoteController();
	public static Block ferrisFileManager = new blockFileManager();
	public static Block ferrisSeatToSit = new blockSeatToSitDown();
	public static Block ferrisCutter = new blockFerrisCutter();
	public static Block ferrisSeatEx = new blockSeatToSitEx();
	public static Block ferrischunkLoader = new blockChunkLoader();
	
	//特殊ブロックレンダラID
	public static int blockCutterRenderId;
	public static int blockCoreRenderId;
	public static int blockSeatExId;
	
	//観覧車アイテム/////////////////////////////////////////
	public static ItemBlock ItemFerrisCore = new itemBlockFerrisCore(ferrisCore);
	public static Item ItemFerrisBasket = new itemFerrisBasket();
	public static Item ItemFerrisSeed = new itemFerrisSeed();
	
	//GUI/////////////////////////////////////////
	@Mod.Instance(MFW_Core.MODID)
    public static MFW_Core INSTANCE;
//    public static Item sampleGuiItem;
//    public static final int GUIID_RailBase = 0;
    public static final int GUIID_FerrisConstructor = 1;
    public static final int GUIID_FerrisBasketConstructor = 2;
    public static final int GUIID_FerrisCore = 3;
    public static final int GUIID_FerrisFileManager = 4;
    public static final int GUIID_FerrisCutter = 5;
    public static final int GUIID_FerrisStoryBoard = 6;
    
	////////////////////////////////////////////////////////////////
	// 独自クリエイティブタブ作成
	public static MFW_CreateCreativeTab MFW_Tab = new MFW_CreateCreativeTab("MineFW", ItemFerrisCore);
	
	////////////////////////////////////////////////////////////////
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) throws Exception
	{
		MFW_Logger.info("Start preInit");

		SoundManager.JsonUpdate();
		MFW_checker.check();
//		if(!MFW_checker.is())return;
		
		GameRegistry.registerTileEntity(TileEntityFerrisConstructor.class, "MFW:TileEntityFerrisConstructor");
		GameRegistry.registerTileEntity(TileEntityFerrisBasketConstructor.class, "MFW:TileEntityFerrisBasketConstructor");
		GameRegistry.registerTileEntity(TileEntityFerrisWheel.class, "MFW:TileEntityFerrisWheel");
		GameRegistry.registerTileEntity(tileEntityRemoteController.class, "MFW:TileEntityFerrisRemoteController");
		GameRegistry.registerTileEntity(TileEntityFileManager.class, "MFW:TileEntityFileManager");
		GameRegistry.registerTileEntity(TileEntityFerrisCutter.class, "MFW:TileEntityFerrisCutter");
		GameRegistry.registerTileEntity(TileEntityChunkLoader.class, "MFW:TileEntityChunkLoader");
		
		blockCutterRenderId = proxy.getNewRenderType();
		blockCoreRenderId = proxy.getNewRenderType();
		blockSeatExId = proxy.getNewRenderType();
		
		MFW_PacketHandler.init();
		proxy.preInit();
		
		new File("./MFWFiles/WheelFrame/").mkdirs();
		new File("./MFWFiles/Basket/").mkdirs();

		MFW_Logger.info("End preInit");
	}

	
	@EventHandler
	public void Init(FMLInitializationEvent e)
	{
		MFW_Logger.info("Start Init");

		proxy.init();
//		if(!MFW_checker.is())return;
		
		//エンティティの登録。
		int eid=100;
		EntityRegistry.registerModEntity(entityFerrisBasket.class, "mfw:basket", eid++, this, 200, 10, true);
		
		EntityRegistry.registerModEntity(entityPartsTestBase.class, "mfw:test", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(entityParts.class, "mfw:testp", eid++, this, 200, 10, true);
		EntityRegistry.registerModEntity(entityPartSit.class, "mfw:partsit", eid++, this, 200, 100, true);
		EntityRegistry.registerModEntity(entityPartSitEx.class, "mfw:partsitex", eid++, this, 200, 100, true);
		// アイテムの登録
		InitItem_Ferris();
		InitBlock_Ferris();
		//特殊シートの設定
		InitExSeat();
		
		// レシピの登録
		InitItemRecipe();
		
		//チャンクローダー用
		ForgeChunkManager.setForcedChunkLoadingCallback(this, new handlerChunkLoading());
		
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new MFW_GUIHandler());
		
		MFW_Logger.info("End Init");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
//		if(!MFW_checker.is())return;
		proxy.postInit();
	}
	////////////////////////////////////////////////////////////////

	private void InitBlock_Ferris()
	{
		ferrisConstrcutor
			.setBlockName("FerrisConstructor")
			.setBlockTextureName(MFW_Core.MODID+":ferrisConstructor")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisConstrcutor, "MFW.FerrisConstructor");
		
		ferrisBasketConstrcutor
			.setBlockName("FerrisBasketConstructor")
			.setBlockTextureName(MFW_Core.MODID+":ferrisBasketConstructor")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisBasketConstrcutor, "MFW.FerrisBasketConstructor");
	
		ferrisConnector
		.setBlockName("FerrisConnector")
		.setBlockTextureName(MFW_Core.MODID+":ferrisConnector")
		.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisConnector, "MFW.FerrisConnector");
		
		ferrisSupporter
			.setBlockName("FerrisSupporter")
			.setBlockTextureName(MFW_Core.MODID+":supporter")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisSupporter, "MFW.FerrisSupporter");
	
		ferrisRemoteController
			.setBlockName("FerrisRemoteController")
			.setBlockTextureName(MFW_Core.MODID+":remoteController")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisRemoteController, itemBlockRemoteController.class, "MFW.FerrisRemoteController");
		
		ferrisFileManager
			.setBlockName("FerrisFileManager")
			.setBlockTextureName(MFW_Core.MODID+":ferrisFileManager")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisFileManager, "MFW.FileManager");
		
		ferrisCutter
			.setBlockName("FerrisCutter")
			.setBlockTextureName(MFW_Core.MODID+":ferrisCutter")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisCutter, "MFW.Cutter");
		
		ferrischunkLoader
		.setBlockName("FerrisChunkLoader")
		.setBlockTextureName(MFW_Core.MODID+":chunkLaoder")
		.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrischunkLoader, "MFW.chunkLoader");
		
		ferrisSeatToSit
			.setBlockName("FerrisSeatToSit")
			.setBlockTextureName(MFW_Core.MODID+":SeatToSit")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisSeatToSit, itemBlockSeatToSitDown.class, "MFW.SeatToSit");
	}
	
	private void InitItem_Ferris()
	{
		ItemFerrisSeed.setCreativeTab(MFW_Tab)
			.setUnlocalizedName("ItemFerrisSeed")
			.setTextureName(MODID+":ferrisSeed")
			.setMaxStackSize(1);
		GameRegistry.registerItem(ItemFerrisSeed, "FerrisSeed");
		
		ItemFerrisCore.setMaxStackSize(10);
		ferrisCore
			.setBlockName("FerrisCore")
			.setBlockTextureName(MODID+":ferrisWheel")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisCore, itemBlockFerrisCore.class, "MFW.FerrisCore");
		
		ItemFerrisBasket.setCreativeTab(MFW_Tab);
		ItemFerrisBasket.setUnlocalizedName("ItemFerrisBasket");
		ItemFerrisBasket.setTextureName(MODID+":ferrisBasket");
		ItemFerrisBasket.setMaxStackSize(10);
		GameRegistry.registerItem(ItemFerrisBasket, "FerrisBasket");
	}
	
	private void InitExSeat()
	{
		if(Loader.isModLoaded(ERC_Core.MODID)==false)return;
		ferrisSeatEx
			.setBlockName("FerrisSeatEx")
			.setBlockTextureName(MFW_Core.MODID+":SeatEx")
			.setCreativeTab(MFW_Tab);
		GameRegistry.registerBlock(ferrisSeatEx, "MFW.SeatToSitEx");
	}
	
	private void InitItemRecipe()
	{

		// タネ
		GameRegistry.addRecipe(new ItemStack(ItemFerrisSeed),
				"DE",
				'D',Items.diamond,
				'E',Items.emerald
		);
		
		// フレーム
		GameRegistry.addRecipe(new ItemStack(ferrisCore),
				" I ",
				"ISI",
				" I ",
				'S',ItemFerrisSeed,
				'I',Items.iron_ingot
		);
		
		// バスケット
		GameRegistry.addRecipe(new ItemStack(ItemFerrisBasket),
				" S ",
				" I ",
				"III",
				'S',ItemFerrisSeed,
				'I',Items.iron_ingot
		);
		
		// フレーム工作台
		GameRegistry.addRecipe(new ItemStack(ferrisConstrcutor),
				"IWI",
				"ISI",
				"ITI",
				'S',ItemFerrisSeed,
				'W',ferrisCore,
				'T',Blocks.crafting_table,
				'I',Blocks.stone
		);
		
		// バスケット工作台
		GameRegistry.addRecipe(new ItemStack(ferrisBasketConstrcutor),
				"IBI",
				"ISI",
				"ITI",
				'S',ItemFerrisSeed,
				'B',ItemFerrisBasket,
				'T',Blocks.crafting_table,
				'I',Blocks.stone
		);
		
		// 接続ブロック
		GameRegistry.addRecipe(new ItemStack(ferrisConnector,10,0),
				" I ",
				"BSB",
				" I ",
				'S',ItemFerrisSeed,
				'B',Items.stick,
				'I',Items.iron_ingot
		);
		
		// サポータブロック
		GameRegistry.addRecipe(new ItemStack(ferrisSupporter,10,0),
				" G ",
				"GSG",
				" G ",
				'S',ItemFerrisSeed,
				'G',Blocks.glass
		);
		
		// リモコン
		GameRegistry.addRecipe(new ItemStack(ferrisRemoteController),
				"OTO",
				"OSO",
				"ORO",
				'S',ItemFerrisSeed,
				'O',Blocks.stone,
				'T',Blocks.redstone_torch,
				'R',Items.redstone
		);
		
		// ファイル読み書き
		GameRegistry.addRecipe(new ItemStack(ferrisFileManager),
				"OOO",
				"OSO",
				"OEO",
				'S',ItemFerrisSeed,
				'O',Blocks.stone,
				'E',Items.ender_pearl
		);
		
		// カッター
		GameRegistry.addRecipe(new ItemStack(ferrisCutter),
				"OOO",
				"OSO",
				"OCO",
				'S',ItemFerrisSeed,
				'O',Blocks.stone,
				'C',Items.shears
		);
		
		// シート0
		GameRegistry.addRecipe(new ItemStack(ferrisSeatToSit,1,0),
				"   ",
				"WSW",
				"   ",
				'S',ItemFerrisSeed,
				'W',Blocks.carpet
		);
		// シート0.5
		GameRegistry.addRecipe(new ItemStack(ferrisSeatToSit,1,1),
				"   ",
				"   ",
				"WSW",
				'S',ItemFerrisSeed,
				'W',Blocks.carpet
		);
		
		//ローダー
		GameRegistry.addRecipe(new ItemStack(ferrischunkLoader),
				"RLR",
				"GSG",
				"RLR",
				'S',ItemFerrisSeed,
				'L',new ItemStack(Items.dye, 1, 4), //ラピス
				'G',Items.glowstone_dust,
				'R',Blocks.stone
		);
		
		if(Loader.isModLoaded(ERC_Core.MODID))
		{
			// シートEx
			GameRegistry.addRecipe(new ItemStack(ferrisSeatEx),
					"   ",
					"WSW",
					" P ",
					'S',ItemFerrisSeed,
					'W',Blocks.carpet,
					'P',ERC_Core.ItemBasePipe
					);
		}
	}
	
	@EventHandler
	public void handleServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new MFW_Command());
	}
}