package mfw.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import mfw._core.MFW_Core;
import mfw.gui.container.ContainerFerrisCore;
import mfw.manager.ManagerSyncRotationTileSave;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageFerrisMisc;
import mfw.message.MessageRegistSyncRotParentCtS;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import scala.reflect.internal.Types.FlagAgnosticCompleter;

public class GUIFerrisCore extends GuiContainer {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(MFW_Core.MODID, "textures/gui/ferriscore.png");
	private int buttonid;
	private int groupid;
//	private int nowGroupPage;
	private TileEntityFerrisWheel tile;
	private ContainerFerrisCore container;
	int blockposX;
	int blockposY;
	int blockposZ;
	
	int offsetx;
	int offsety;
	
	private int stopButtonID = -1;
	private int LcokButtonID = -1;
	
	class GUIName{
		String name;
		int x;
		int y;
		int flag;
		int baseID;
		GUIName(String str,int x, int y, int flag, int base){name=str; this.x=x; this.y=y; this.flag = flag; this.baseID = base;}
	}
	Map<Integer, GUIName> GUINameMap = new HashMap<Integer, GUIName>();
	Map<Integer, Integer> MapButtonidToGroup = new HashMap<Integer, Integer>();
	
	class bpos{int x,y;bpos(int X,int Y){x=X;y=Y;}}
	List<bpos> listBPos = new ArrayList<bpos>();
	
	public GUIFerrisCore(int x, int y, int z, InventoryPlayer invPlayer, TileEntityFerrisWheel tile)
	{
		super(new ContainerFerrisCore(invPlayer, tile));
		this.tile = tile;
		container = (ContainerFerrisCore) inventorySlots;
		blockposX = x;
		blockposY = y;
		blockposZ = z;
		xSize = 270;
		ySize = 166;
	}
	
	// GUIを開くたび呼ばれる初期化関数
	@Override
	public void initGui()
	{
		super.initGui();
		GUINameMap.clear();
		listBPos.clear();
//			this.guiLeft = this.width*7/8 - xSize/2;
		buttonid = 0;
//		nowGroupPage = 0;
		// ボタン登録
		offsetx = this.guiLeft + 20;
		offsety = this.guiTop;
//			int offset = 4;
//     	   int length = 27;
		groupid = -1;
		offsety = this.guiTop;
		int num = tile.getSizeInventory();
		for (int i = 0; i <= num/5; ++i)
		{
			if(i >= 5)break;
			for (int j = 0; j < 5; ++j)
			{
				if(j+i*5 >= num)break;
				addButton1(16, 6, j*18-12, 33+i*24, "", MessageFerrisMisc.GUISubPartGUIOpenBase + i*5+j);
			}
		}
		addButton1(29, 13, -15, 2, "back", MessageFerrisMisc.GUIBackPartGUIOpen);
		addButton2(30, 3, "", MessageFerrisMisc.GUICoreSlotPage);
		addButton4(119, 49, "Size", MessageFerrisMisc.GUICoreSizes);
		addButton4(119, 60, "Rot1", MessageFerrisMisc.GUICoreRot1);
		addButton4(119, 71, "Rot2", MessageFerrisMisc.GUICoreRot2);
		addButton1(38, 10, 188, 60, "reset", MessageFerrisMisc.GUICoreRotReset);
		addButton1(38, 10, 248, 94, "save", MessageFerrisMisc.GUICoreSyncSaveParent);	
		LcokButtonID = buttonid;
		addButton1(30, 13, -17, -15, tile.getRootParent().isLock?"lock":"unlock", MessageFerrisMisc.GUICoreLock);
		
		//normal
		groupid = 0; //main param  tile.rotFlag_Normal;
		addButton6(153, 4, "Accel", MessageFerrisMisc.GUICoreAddSpeed);
		addButton1(48, 10, 198, 5, "Reverse", MessageFerrisMisc.GUICoreTurn);
//		rsRotateButtonID = buttonid;
		addButton1(100, 13, -20, 178, StatCollector.translateToLocal("gui.core.switch.rsmode"), MessageFerrisMisc.GUICoreRSFlagRotate);
		
		// stop
		groupid = 1; // stop toggle
		stopButtonID = buttonid;
		String stopbuttonname = tile.stopFlag ? "Restart" : "Stop";
		addButton1(48, 10, 198, 18, stopbuttonname, MessageFerrisMisc.GUICoreStop);
		
		groupid = 2; // change mode
		addButton1(100, 13, -20, 164, StatCollector.translateToLocal("gui.core.switch.changemode"), MessageFerrisMisc.GUICoreModeChange);	
		
		groupid = 3; // change sync mode
		addButton1(70, 13, 248, 144, StatCollector.translateToLocal("gui.core.switch.syncmode"), MessageFerrisMisc.GUICoreSyncMode);	
		
		groupid = 4; //sync param  tile.rotFlag_Sync; 
//     	   offsety += 1000;
		
		addButton1(38, 10, 248, 109, "regist", MessageFerrisMisc.GUICoreSyncRegistParent);
		addButton1(38, 10, 248, 124, "clear", MessageFerrisMisc.GUICoreSyncClear);		

		groupid = 5;
		addButton6(153, 26, "Misc 1", MessageFerrisMisc.GUICoreRotMisc1);
		addButton6(153, 38, "Misc 2", MessageFerrisMisc.GUICoreRotMisc2);
		
		groupid = 6;
		addButton4(119, 15, "Resist", MessageFerrisMisc.GUICoreResist);
		
		groupid = 7;
		addButton1(88, 30, 118, 15, "Open StoryBoard", MessageFerrisMisc.GUIOpenStoryBoard);
		
		//postinit
		setButtonPosFromNowTileState();
		stringRotMode = StatCollector.translateToLocal("gui.core.text.modename"+tile.rotFlag);
		stringRSMode = tile.getSRTitleStringForGUI();
		
	}	
	@SuppressWarnings("unchecked")
	public void addButton1(int lenx, int leny, int posx, int posy, String str, int flag)
	{
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx, posy+offsety, lenx, leny, str));
		GUINameMap.put(buttonid-1, new GUIName("",-100,-100, flag, -1));
		MapButtonidToGroup.put(buttonid-1, groupid);
		listBPos.add(new bpos(posx+offsetx, posy+offsety));
	}
	@SuppressWarnings("unchecked")
	public void addButton2(int posx, int posy, String str, int flag)
	{
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx, posy+offsety, 13, 10, "-"));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+32, posy+offsety, 13, 10, "+"));
		GUIName data = new GUIName(str,5,offsety-10-guiTop,flag,buttonid-2);
		GUINameMap.put(buttonid-2, data);
    	GUINameMap.put(buttonid-1, data);
    	MapButtonidToGroup.put(buttonid-2, groupid);
    	MapButtonidToGroup.put(buttonid-1, groupid);
    	listBPos.add(new bpos(posx+offsetx, posy+offsety));
    	listBPos.add(new bpos(posx+offsetx, posy+offsety));
	}
	@SuppressWarnings("unchecked")
	public void addButton4(int posx, int posy, String str, int flag)
	{
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx ,   posy+offsety, 14, 10, ""));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+19, posy+offsety, 12, 10, ""));	
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+34, posy+offsety, 12, 10, ""));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+49, posy+offsety, 14, 10, ""));
		GUIName data = new GUIName(str,posx-19,posy+2,flag,buttonid-4);
		GUINameMap.put(buttonid-4, data);
		GUINameMap.put(buttonid-3, data);
		GUINameMap.put(buttonid-2, data);
		GUINameMap.put(buttonid-1, data);
		MapButtonidToGroup.put(buttonid-4, groupid);
		MapButtonidToGroup.put(buttonid-3, groupid);
		MapButtonidToGroup.put(buttonid-2, groupid);
    	MapButtonidToGroup.put(buttonid-1, groupid);
    	listBPos.add(new bpos(posx+offsetx, posy+offsety));
    	listBPos.add(new bpos(posx+offsetx, posy+offsety));
    	listBPos.add(new bpos(posx+offsetx, posy+offsety));
    	listBPos.add(new bpos(posx+offsetx, posy+offsety));
	}
	@SuppressWarnings("unchecked")
    public void addButton6(int posx, int posy, String str, int flag)
    {
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx-41, posy+offsety, 14, 10, ""));
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx-26, posy+offsety, 12, 10, ""));
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx-13, posy+offsety, 10, 10, ""));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx   , posy+offsety, 10, 10, ""));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+11, posy+offsety, 12, 10, ""));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+24, posy+offsety, 14, 10, ""));
		GUIName data = new GUIName(str,posx-53,posy+2,flag,buttonid-6);
		GUINameMap.put(buttonid-6, data);
    	GUINameMap.put(buttonid-5, data);
		GUINameMap.put(buttonid-4, data);
    	GUINameMap.put(buttonid-3, data);
    	GUINameMap.put(buttonid-2, data);
    	GUINameMap.put(buttonid-1, data);
    	MapButtonidToGroup.put(buttonid-6, groupid);
    	MapButtonidToGroup.put(buttonid-5, groupid);
    	MapButtonidToGroup.put(buttonid-4, groupid);
    	MapButtonidToGroup.put(buttonid-3, groupid);
    	MapButtonidToGroup.put(buttonid-2, groupid);
    	MapButtonidToGroup.put(buttonid-1, groupid);
    	listBPos.add(new bpos(posx+offsetx, posy+offsety));
    	listBPos.add(new bpos(posx+offsetx, posy+offsety));
    	listBPos.add(new bpos(posx+offsetx, posy+offsety));
    	listBPos.add(new bpos(posx+offsetx, posy+offsety));
    	listBPos.add(new bpos(posx+offsetx, posy+offsety));
    	listBPos.add(new bpos(posx+offsetx, posy+offsety));
    }
	
	
	public byte covertGUIFlagFromTileState()
	{
		return convertGUIFlagFromState(tile.rotFlag, tile.stopFlag);
	}
	public byte convertGUIFlagFromState(int rotFlag, boolean stopFlag)
	{
		if(stopFlag)return GUIModeFlagEnum_Stop;
		if(rotFlag == TileEntityFerrisWheel.rotFlag_Sync)return GUIModeFlagEnum_Sync;
		if(rotFlag==0)return GUIModeFlagEnum_Normal;
		if(rotFlag==TileEntityFerrisWheel.rotFlag_StoryBoard)return GUIModeFlagEnum_StoryBoard;
		return GUIModeFlagEnum_ModeRotate;
	}
	public void setButtonPosFromEnumID(byte GUIModeFlag)
	{
		@SuppressWarnings("unchecked")
		List<GuiButton> _buttonlist = buttonList;
    	for(GuiButton b : _buttonlist)
    	{
        	if(MapButtonidToGroup.get(b.id) == -1)continue;
        	boolean isDispButton = flagManager(MapButtonidToGroup.get(b.id), covertGUIFlagFromTileState());
    		if(isDispButton) b.yPosition = listBPos.get(b.id).y;
        	else b.yPosition = -1000;
    	}
	}
    public void setButtonPosFromNowTileState()
    {
    	setButtonPosFromGUIModeFlagEnum(covertGUIFlagFromTileState());
    	//ついで　Change Name
    	ChangeNames(tile.rotFlag, covertGUIFlagFromTileState());
    }
    public void setButtonPosFromGUIModeFlagEnum(byte GUIModeFlag)
    {
    	@SuppressWarnings("unchecked")
		List<GuiButton> _buttonlist = buttonList;
    	for(GuiButton b : _buttonlist)
    	{
        	if(MapButtonidToGroup.get(b.id) == -1)continue;
    		if(flagManager(MapButtonidToGroup.get(b.id), GUIModeFlag)) b.yPosition = listBPos.get(b.id).y;
        	else b.yPosition = -1000;
  		
    		//ボタンチェンジしたらStopRestart設定する 今のフラグがStopならば次は必ずStopFlagのアレをオフに
    		GuiButtonExt bt = ((GuiButtonExt)buttonList.get(stopButtonID));
			if(tile.stopFlag) bt.displayString = "Stop";
    	}

    }
    // StopRestartボタンが押されるとやってくる
    public void ChangeButton_StopFlag()
    {
    	byte flag = tile.stopFlag ? GUIModeFlagEnum_Normal : GUIModeFlagEnum_Stop;
    	setButtonPosFromGUIModeFlagEnum(flag);
    	ChangeNames(tile.rotFlag, flag);
    }
    // ChangeModeボタンが押されるとやってくる
    public void ChangeButton_ChangeMode()
    {
    	if(tile.rotFlag ==  TileEntityFerrisWheel.rotFlag_Sync)return; // こないはずではある　一応
    	byte flag;
    	switch((tile.rotFlag + 1) % TileEntityFerrisWheel.rotFlag_End)
    	{
    	case TileEntityFerrisWheel.rotFlag_Normal : flag = GUIModeFlagEnum_Normal; break;
    	case TileEntityFerrisWheel.rotFlag_StoryBoard : flag = GUIModeFlagEnum_StoryBoard; break;
    	default : flag = GUIModeFlagEnum_ModeRotate;
    	}
    	setButtonPosFromGUIModeFlagEnum(flag);
    	ChangeNames(tile.rotFlag+1, flag);
    	stringRotMode = StatCollector.translateToLocal("gui.core.text.modename"+(tile.rotFlag+1)%(TileEntityFerrisWheel.rotFlag_End+1));
    }
    public void ChangeButton_ChangeSyncMode()
    {
    	byte flag = tile.rotFlag == TileEntityFerrisWheel.rotFlag_Normal ? GUIModeFlagEnum_Sync : GUIModeFlagEnum_Normal;
    	setButtonPosFromGUIModeFlagEnum(flag);
    	ChangeNames(tile.rotFlag, flag);
    }
    private final byte GUIModeFlagEnum_Normal= 0;
    private final byte GUIModeFlagEnum_Stop = 1;
    private final byte GUIModeFlagEnum_ModeRotate = 2;
    private final byte GUIModeFlagEnum_Sync = 3;
    private final byte GUIModeFlagEnum_StoryBoard = 4;
    private boolean flagManager(int groupid, byte enumflag)
    {
    	if(groupid == -1) return true;
    	
//    	groupid = 0; //main param  tile.rotFlag_Normal;
//		groupid = 1; // stop toggle
//		groupid = 2; // change mode
//		groupid = 3; // change sync mode
//		groupid = 4; //sync param  tile.rotFlag_Sync;
//    	groupid = 5; // for rotMisc, only ModeRotate
//    	groupid = 6; // only for Resist
//    	groupid = 7; // only for storyboard
    	boolean T = true;
    	boolean F = false;
//    	boolean table2[][]
//    			{
//    				{T, F, T, F	},
//    				{T, T, T, F },
//    				{T, T, T, F },
//    				{T, F, F, T },
//    				{F, F, F, T	},
//    				{F, F, T, T	},
//    				{T, F, F, F	},
//    			};
    	boolean table[][] = //[enum][groupid]
			{
				{T, T, T, T, F, F, T, F },
				{F, T, T, F, F, F, F, F },
				{T, T, T, F, F, T, T, F },
				{F, F, F, T, T, T ,F, F },
				{F, F, T, F, F, F ,F, T },
			};
    	return table[enumflag][groupid];
    }
    
    private void ChangeButton_ChangeRSMode()  //RSフラグ用ボタンの名前かえるだけ用
    {
//    	GuiButtonExt bt = ((GuiButtonExt)buttonList.get(rsRotateButtonID));
//		bt.displayString = tile.getSRTitleStringForGUI(tile.rsFlag + 1);
		stringRSMode = tile.getSRTitleStringForGUI(tile.rsFlag + 1);
    }
    
    @Override
	public void onGuiClosed()
    {
		super.onGuiClosed();
//		tile.selectedTileNull();
	}
    
    String[] names = new String[]{"Accel","Speed","Misc1","Misc2","Resist"};
    private void ChangeNames(int rotFlag, int enumflag)
    {
//    	  private final byte GUIModeFlagEnum_Normal= 0;
//        private final byte GUIModeFlagEnum_Stop = 1;
//        private final byte GUIModeFlagEnum_ModeRotate = 2;
//        private final byte GUIModeFlagEnum_Sync = 3;
    	switch(enumflag)
    	{
    	default :
    	case 0 : names = new String[]{"Accel","Speed","","","Resist"}; break;
    	case 1 : names = new String[]{"","","","",""}; break;
    	case 3 : names = new String[]{"","Speed","Amp","Phase",""}; break;
    	case 2 : 
    		switch((rotFlag)%TileEntityFerrisWheel.rotFlag_End)
    		{
    		case TileEntityFerrisWheel.rotFlag_ComeAndGo :
    			names = new String[]{"Accel","Speed","ang1","ang2",""}; break;
    		case TileEntityFerrisWheel.rotFlag_Move_RsOnToggle :
    			names = new String[]{"Accel","Speed","Value","(none)",""}; break;
    		default : names = new String[]{"Accel","Speed","Amp","Phase","Resist"}; break;
    		}
    		break;
    	case GUIModeFlagEnum_StoryBoard :
    		names = new String[]{"", "", "", "", "",}; break;
    	}
    }
    
	/*GUIの文字等の描画処理*/
    String stringRotMode;
    String stringRSMode;
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);   
        
        this.fontRendererObj.drawString(tile.WheelName,10,147,0x0000A);
//        for(GUIName g :  GUINameMap.values())
//        {
//        	this.fontRendererObj.drawString(g.name,g.x,g.y,0x404040);
//        }
        this.fontRendererObj.drawString(names[0],100,6,0x404040);
        this.fontRendererObj.drawString(names[4],100,17,0x404040);
        this.fontRendererObj.drawString(names[2],100,28,0x404040);
        this.fontRendererObj.drawString(names[3],100,40,0x404040);
        this.fontRendererObj.drawString("Size",  100,52,0x404040);
        this.fontRendererObj.drawString("Rot1",  100,63,0x404040);
        this.fontRendererObj.drawString("Rot2",  100,73,0x404040);
         
        drawString(this.fontRendererObj, names[0]+" :"+String.format("% 4.2f",tile.rotAccel), 270, 3, 0xffffff);
        drawString(this.fontRendererObj, names[1]+" :"+String.format("% 4.2f",tile.rotSpeed), 270, 13, 0xffffff);
        drawString(this.fontRendererObj, names[2]+" :"+String.format("% 4.1f",tile.rotMiscFloat1), 270, 23, 0xffffff);
        drawString(this.fontRendererObj, names[3]+" :"+String.format("% 4.1f",tile.rotMiscFloat2), 270, 33, 0xffffff);
        drawString(this.fontRendererObj, names[4]+" :"+String.format("% 4.1f",tile.rotResist*100f), 270, 43, 0xffffff);
        drawString(this.fontRendererObj, "Size :"+String.format("% 4.2f",tile.wheelSize), 270, 63, 0xffffff);
        
        drawString(this.fontRendererObj, "rot1 :"+String.format("% 4.2f",tile.rotVar1), 270, 73, 0xffffff);
        drawString(this.fontRendererObj, "rot2 :"+String.format("% 4.2f",tile.rotVar2), 270, 83, 0xffffff);
        this.fontRendererObj.drawString(""+(container.getPageNum()),70,5,0x404040);
//        drawString(this.fontRendererObj, String.format("% 2.1f",ERC_BlockRailManager.clickedTileForGUI.BaseRail.Power), 37, 56, 0xffffff);
//        drawString(this.fontRendererObj, ERC_BlockRailManager.clickedTileForGUI.SpecialGUIDrawString(), 42, 199, 0xffffff);
        
        //debug
        drawString(this.fontRendererObj, stringRotMode, 105, 167, 0xffffff);
        drawString(this.fontRendererObj, stringRSMode, 105, 181, 0xffffff);
        drawString(this.fontRendererObj, String.format("Input RS Power : %1.2f",tile.getRSPower()), 200, 181, 0xffffff);
    }
 
    /*GUIの背景の描画処理*/
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseZ)
    {
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    	this.mc.renderEngine.bindTexture(TEXTURE);
        
//        this.ERCRail_drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
        
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.ERCRail_drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
        this.mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);

        tile.renderForGUI(partialTick);
    }
    
    public void ERCRail_drawTexturedModalRect(int x, int y, int z, int v, int width, int height)
    {
        float f = 1f/(float)width;
        float f1 = 1f/(float)height;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + height), (double)this.zLevel, (double)((float)(z + 0) * f), (double)((float)(v + height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + height), (double)this.zLevel, (double)((float)(z + width) * f), (double)((float)(v + height) * f1));
        tessellator.addVertexWithUV((double)(x + width), (double)(y + 0), (double)this.zLevel, (double)((float)(z + width) * f), (double)((float)(v + 0) * f1));
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel, (double)((float)(z + 0) * f), (double)((float)(v + 0) * f1));
        tessellator.draw();
    }
 
    /*GUIが開いている時にゲームの処理を止めるかどうか。*/
//    @Override
//    public boolean doesGuiPauseGame()
//    {
//        return false;
//    }

	@Override
	protected void actionPerformed(GuiButton button)
	{
		GUIName obj = GUINameMap.get(button.id);
		int data = (button.id - obj.baseID);
		int flag = this.GUINameMap.get(button.id).flag;
		GuiButtonExt bt;
		
		if(flag >= MessageFerrisMisc.GUISubPartGUIOpenBase)
		{
			if(!tile.getSelectedPartTile().onPushChildPart((GUINameMap.get(button.id).flag - MessageFerrisMisc.GUISubPartGUIOpenBase)+25*container.getPageNum()))
			{
				return;
			}
			data = container.getPageNum();
//			mc.thePlayer.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
		}
		else if(flag == MessageFerrisMisc.GUIBackPartGUIOpen)
		{	
			if(!tile.onPushBack())
			{
				mc.thePlayer.closeScreen();
				return;
			}
//			mc.thePlayer.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, mc.thePlayer.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
		}
		if(tile.getRootParent() != tile)
		{
			switch(flag)
			{
			case MessageFerrisMisc.GUICoreAddSpeed:
				tile.getSelectedPartTile().setSpeed(data); 
				break;
			case MessageFerrisMisc.GUICoreTurn:
				tile.getSelectedPartTile().turnSpeed(); 
				break;
			case MessageFerrisMisc.GUICoreStop:
				tile.getSelectedPartTile().toggleStopFlag(); 
				break;
			}
		}
		switch(flag)
		{
		case MessageFerrisMisc.GUICoreSlotPage :
			((ContainerFerrisCore)mc.thePlayer.openContainer).changePage(data);
			break;
		case  MessageFerrisMisc.GUICoreSyncSaveParent :
			ManagerSyncRotationTileSave.INSTANCE.saveTile(tile, tile.getTreeIndexFromTile(tile));
			return;
		case  MessageFerrisMisc.GUICoreSyncRegistParent :
			if(tile.childSyncTileList.size()!=0)
			{
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
       				StatCollector.translateToLocal("message.coreGUI.sync.cannotregisttoparent")
       				));
				return;
			}
			MessageRegistSyncRotParentCtS packet = new MessageRegistSyncRotParentCtS();
			TileEntityFerrisWheel parent = ManagerSyncRotationTileSave.INSTANCE.getSaveTile();
			int parettreeidx = ManagerSyncRotationTileSave.INSTANCE.getSaveTileTreeIdx();
			packet.setParent(parent.xCoord, parent.yCoord, parent.zCoord, parettreeidx);
			packet.setChild(tile.xCoord, tile.yCoord, tile.zCoord, tile.getTreeIndexFromTile(tile));
			if(packet.isSameFrame())return;
			MFW_PacketHandler.INSTANCE.sendToServer(packet);
			
//			parent = parent.getTileFromTreeIndex(m.TreeIndexP);
//			tile = tile.getTileFromTreeIndex(m.TreeIndexC);
			parent.setSyncChild(tile);
			tile.setSyncParent(parent);
			return;
		case MessageFerrisMisc.GUICoreSyncClear :
			tile.clearSyncParent();
			break;
		
		// ﾒﾆｭｰ遷移用３つ
		case MessageFerrisMisc.GUICoreStop :
			ChangeButton_StopFlag();
			bt = ((GuiButtonExt)buttonList.get(stopButtonID));
			if(tile.stopFlag)bt.displayString = "Stop";
			else bt.displayString = "Restart";
			break;
		case MessageFerrisMisc.GUICoreModeChange :
			ChangeButton_ChangeMode();
			break;
		case MessageFerrisMisc.GUICoreSyncMode :
			ChangeButton_ChangeSyncMode();
			break;
			
		case MessageFerrisMisc.GUICoreRSFlagRotate :
			ChangeButton_ChangeRSMode();
			break;
			
		case MessageFerrisMisc.GUICoreLock : 
			tile.getRootParent().toggleLock();
			bt = ((GuiButtonExt)buttonList.get(LcokButtonID));
			bt.displayString = tile.getRootParent().isLock?"lock":"unlock";
			
			break;
		}
		MessageFerrisMisc packet = new MessageFerrisMisc(blockposX,blockposY,blockposZ, flag, data, new byte[1]);
	    MFW_PacketHandler.INSTANCE.sendToServer(packet);
	}

}