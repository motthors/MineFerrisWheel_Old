package mfw.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import mfw._core.MFW_Core;
import mfw.gui.container.DefContainer;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageFerrisMisc;
import mfw.storyboard.StoryBoardManager;
import mfw.storyboard.programpanel.IProgramPanel;
import mfw.storyboard.programpanel.IProgramPanel.Mode;
import mfw.storyboard.programpanel.LoopPanel;
import mfw.storyboard.programpanel.LoopPanel.LoopEndPanel;
import mfw.storyboard.programpanel.SetValuePanel;
import mfw.storyboard.programpanel.TimerPanel;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class GUIStoryBoard extends GuiContainer implements INotifiedHandler {
	
	private static final ResourceLocation TEX_BASE = new ResourceLocation(MFW_Core.MODID, "textures/gui/storyboard.png");
	private static final ResourceLocation TEX_ARROW = new ResourceLocation(MFW_Core.MODID, "textures/gui/sbarrow.png");
	private static final ResourceLocation Tex1 = new ResourceLocation(MFW_Core.MODID, "textures/gui/p1.png");
	private static final ResourceLocation Tex2 = new ResourceLocation(MFW_Core.MODID, "textures/gui/p2.png");
	private static final ResourceLocation Tex3 = new ResourceLocation(MFW_Core.MODID, "textures/gui/p3.png");
	
	public enum buttonGroup{
		PanelIdFlag,
		Close,
		AddPanel,
		Panel,
		Setting,
		Pageup,
		Pagedown,
		PanelUp,
		PanelDown,
		PanelDelete,
	}
	
    private int groupid;
    private ArrayList<CustomTexButton> PanelButtonList;
    private ArrayList<GUIStoryBoardSettingPart> SettingPartButtonlist;
    private List<GuiTextField> textFieldList = new ArrayList<GuiTextField>();
    private CustomTexButton nowTargetPanelB;
    private TileEntityFerrisWheel tile;
    int blockposX;
    int blockposY;
    int blockposZ;
    
    int offsetx;
    int offsety;
    
    int idOffsetAddPanel;
    
    GuiButtonExt AnsBtn;
    GuiButtonExt Adder;

    ArrayList<GUIStoryBoardSettingPart> nowSelectedSettingList;
    ArrayList<GUIStoryBoardSettingPart> ButtonListSelectMode 	= new ArrayList<GUIStoryBoardSettingPart>();
    ArrayList<GUIStoryBoardSettingPart> ButtonGroupListSet 		= new ArrayList<GUIStoryBoardSettingPart>();
    ArrayList<GUIStoryBoardSettingPart> ButtonGroupListTimer 	= new ArrayList<GUIStoryBoardSettingPart>();
    ArrayList<GUIStoryBoardSettingPart> ButtonGroupListRun 		= new ArrayList<GUIStoryBoardSettingPart>();
    ArrayList<GUIStoryBoardSettingPart> ButtonGroupListLoop		= new ArrayList<GUIStoryBoardSettingPart>();
    class PanelSettingGuiData{ 
    	public IProgramPanel targetPanel;
    	public ArrayList<GUIStoryBoardSettingPart> list;
    	PanelSettingGuiData(IProgramPanel c, ArrayList<GUIStoryBoardSettingPart> a)
    	{targetPanel = c; list = a;}
    };
    
    int settingButtonsOffsetX = 300;
     
    Map<Integer, buttonGroup> ButtonMap = new HashMap<Integer, buttonGroup>();
    
    int PanelPageIndex = 0;
    
    public GUIStoryBoard(int x, int y, int z, TileEntityFerrisWheel tile)
    {
    	super(new DefContainer(x, y, z, tile));
        this.tile = tile;
		blockposX = x;
		blockposY = y;
		blockposZ = z;
		xSize = 270;
		PanelButtonList = new ArrayList<CustomTexButton>();
		SettingPartButtonlist = new ArrayList<GUIStoryBoardSettingPart>();
    }
 
    // GUIを開くたび呼ばれる初期化関数
	@Override
	public void initGui()
    {
		super.initGui();
		tile.getStoryBoardManager().stop();
		
		ButtonMap.clear();
		PanelButtonList.clear();
		// ボタン登録
		ySize = mc.displayHeight;
		offsetx = this.guiLeft + 20;
		offsety = 0;
		
		//TODO シリアル文字列の表示用
        GuiTextField textField = new GuiTextField(this.fontRendererObj, offsetx + 136, offsety + 15, 95, 12);
		textField.setTextColor(-1);
		textField.setDisabledTextColour(-1);
		textField.setEnableBackgroundDrawing(false);
		textField.setMaxStringLength(40);
		textFieldList.add(textField);

        //共通
        addButton(29, 13, -15, 2, "back", buttonGroup.Close);
        //パネルスクロール
        addButton(80, 10, 50, 5, "▲", buttonGroup.Pageup);
        addButton(80, 10, 50, 220, "▼", buttonGroup.Pagedown);
        //パネル移動
        addButton(10, 10, 180, 5, "▲", buttonGroup.PanelUp);
        addButton(10, 10, 200, 5, "▼", buttonGroup.PanelDown);
        addButton(10, 10, 230, 5, "X", buttonGroup.PanelDelete);
        
        //追加したいパネルの種類を選ぶやつ
        idOffsetAddPanel = buttonList.size();
        for(int apiidx = 0; apiidx < IProgramPanel.Mode.values().length; ++apiidx)
    	{
        	String desc = IProgramPanel.Mode.values()[apiidx].name();
        	if(!desc.equals("loopend"))addButton(40, 15, -15, 50+(apiidx)*20, desc, buttonGroup.AddPanel);
    	}
        
        //各種パネル設定用ボタン
        PanelSettingGuiData[] classes  = {
        		new PanelSettingGuiData(new SetValuePanel(), ButtonGroupListSet),
        		new PanelSettingGuiData(new TimerPanel(), ButtonGroupListTimer),
        		new PanelSettingGuiData(new LoopPanel(), ButtonGroupListLoop),
        		
        };
        for(int i=0; i < classes.length; ++i)
        {
        	int apinum = (Integer) classes[i].targetPanel.ApiNum();
        	for(int apiidx = 0; apiidx < apinum; ++apiidx)
        	{
        		IProgramPanel.Type type = (IProgramPanel.Type)classes[i].targetPanel.getType(apiidx);
        		String desc = classes[i].targetPanel.getDescription(apiidx);
        		Object value = classes[i].targetPanel.getValue(apiidx);
        		GUIStoryBoardSettingPart part = new GUIStoryBoardSettingPart(this, -1000, 20+apiidx*30, type, desc, value, fontRendererObj, buttonGroup.Setting);
        		classes[i].list.add(part);
        		SettingPartButtonlist.add(part);
        	}
        }
        
        //パネルタイムライン独自ボタン
		for( IProgramPanel panel : tile.getStoryBoardManager().getPanelList())
		{
			addPanelButton(panel);
		}
    }
	public int AddToButtonList(GuiButton button, buttonGroup enumGroup)
	{
		button.id = buttonList.size();
		ButtonMap.put(button.id, enumGroup);
		buttonList.add(button);
		return button.id;
	}
	public int AddToTextFieldList(GuiTextField text)
	{
		int id = textFieldList.size();
		textFieldList.add(text);
		if(text instanceof GuiTextFieldExt)((GuiTextFieldExt)text).id = id;
		return id;
	}
	public FontRenderer GetFontRenderer(){
		return fontRendererObj;
	}
	
	private ResourceLocation DefaultTex()
	{
		return PanelModeToTex(IProgramPanel.Mode.set);
	}
	
	private ResourceLocation PanelModeToTex(IProgramPanel.Mode mode)
	{
		switch(mode)
		{
		case set : return Tex1;
		case timer : return Tex2;
		case loop : return Tex3;
		default : return DefaultTex();
		}
	}

	public GuiButtonExt addButton(int lenx, int leny, int posx, int posy, String str, buttonGroup enumGroup)
	{
		int id = buttonList.size();
		GuiButtonExt btn = new GuiButtonExt(id, posx+offsetx, posy+offsety, lenx, leny, str);
    	this.buttonList.add(btn);
    	ButtonMap.put(id, enumGroup);
    	return btn;
    }
    
	private void addPanelButton()
	{
		addPanelButton(null);
	}
	private CustomTexButton addPanelButton(IProgramPanel panel)
	{
		int id = buttonList.size();
		int panelIdx = PanelButtonList.size();
		int panelHeight = (panel.getMode() == Mode.loopend) ? 10 : 20;
		ResourceLocation tex = PanelModeToTex(panel.getMode());
		CustomTexButton btn = new CustomTexButton(tex, panel, buttonList.size(), offsetx+50, 20 + 0, 100, panelHeight, "");
		this.buttonList.add(btn);
		this.PanelButtonList.add(btn);
		ButtonMap.put(id, buttonGroup.Panel);
		CalcButtonOffset();
		return btn;
	}
	
	public void clearPanel()
	{
		PanelButtonList.clear();
		tile.getStoryBoardManager().clear();
	}
	
    @Override
	public void onGuiClosed()
    {
    	updateToServer();
		super.onGuiClosed();
	}
    
    private void updateToServer()
    {
    	StoryBoardManager manager = tile.getStoryBoardManager();
    	manager.clear();
    	for(CustomTexButton panelbutton : PanelButtonList)
    	{
    		if(panelbutton.panel.getMode()==Mode.loop)((LoopPanel)panelbutton.panel).clear();
    		manager.addPanel(panelbutton.panel);
    	}
    	byte[] serialArray = manager.getSerialCode().getBytes();
    	int flag = MessageFerrisMisc.GUIStoryBoardSendData;
    	MessageFerrisMisc packet = new MessageFerrisMisc(blockposX,blockposY,blockposZ, flag, 0, serialArray);
	    MFW_PacketHandler.INSTANCE.sendToServer(packet);
    }
    
	@Override
	protected void actionPerformed(GuiButton button)
	{
		buttonGroup Group = ButtonMap.get(button.id);
		int data = -9999;
		int flag = -9999;
		IProgramPanel panel;
		
		switch(Group)
		{
		case Close :
			flag = MessageFerrisMisc.GUICloseStoryBoard;
			break;
		case AddPanel:
			panel = StoryBoardManager.createPanel(button.displayString);
			AddNewPanel(panel);
			return;
		case Pageup : 
			PageUp();
			return;
		case Pagedown : 
			PageDown();
			return;
		case PanelUp :
			SortPanel(-1);
			return;
		case PanelDown :
			SortPanel(1);
			return;
		case PanelDelete :
			DeletePanel();
			return;
		case Panel :
			//まず前回分を全て保存する
			if(nowTargetPanelB!=null)
			{
				int apinum = nowTargetPanelB.panel.ApiNum();
				for(int i=0; i<apinum; ++i)
				{
					nowTargetPanelB.panel.setValue(i, nowSelectedSettingList.get(i).getValue());
				}
			}
			updateToServer();
			//パネル切り替え	
			changePanelSettingGui(((CustomTexButton)button));
			int num = nowTargetPanelB.panel.ApiNum();
			for(int i=0; i<num; ++i)nowSelectedSettingList.get(i).setValue(nowTargetPanelB.panel.getValue(i));
			return;
		case Setting :
			for(GUIStoryBoardSettingPart part : nowSelectedSettingList){
				if(part.findButton(button.id)){
					int apiidx = nowSelectedSettingList.indexOf(part);
					int[] update = nowTargetPanelB.panel.Clicked(apiidx);
					for(int i : update){
						nowSelectedSettingList.get(i).setValue(nowTargetPanelB.panel.getValue(i));
					}
				}
			}
			return;
		default:
            return;
		}

		MessageFerrisMisc packet = new MessageFerrisMisc(blockposX,blockposY,blockposZ,
				flag , data, null);
	    MFW_PacketHandler.INSTANCE.sendToServer(packet);
	}
	
	private void AddNewPanel(IProgramPanel panel)
	{
		panel.Init(tile);
		CustomTexButton btn = addPanelButton(panel);
		changePanelSettingGui(btn);
		if(panel.getMode() == Mode.loop)
		{
			panel = ((LoopPanel)panel).createLoopEndPanel();
			panel.Init(tile);
			addPanelButton(panel);
		}
	}
	
	private void DeletePanel()
	{
		if(nowTargetPanelB==null)return;
		if(nowTargetPanelB.panel.getMode()==Mode.loopend)return;
		int idx = PanelButtonList.indexOf(nowTargetPanelB);
		if(idx+1 >= PanelButtonList.size())idx--;
		if(idx<0){
			PanelButtonList.clear();
			changePanelSettingGui(null);
			return;
		}
		if(nowTargetPanelB.panel.getMode()==Mode.loop){
			int i = 1 + PanelButtonList.indexOf(nowTargetPanelB);
			for(; i<PanelButtonList.size(); ++i){
				CustomTexButton button = PanelButtonList.get(i);
				if(button.xPosition == nowTargetPanelB.xPosition && button.panel.getMode()==Mode.loopend){
					PanelButtonList.remove(i);
					break;
				}
			}
		}
		PanelButtonList.remove(nowTargetPanelB);
		changePanelSettingGui(PanelButtonList.get(idx));
		CalcButtonOffset();
	}
	
	public void actionPerformed(Object object)
	{
		GuiTextFieldExt text = (GuiTextFieldExt)object;
		for(GUIStoryBoardSettingPart part : nowSelectedSettingList){
			if(part.findText(text.id)){
				int apiidx = nowSelectedSettingList.indexOf(part);
				int[] update = nowTargetPanelB.panel.setValue(apiidx, text.getText());
				for(int i : update){
					nowSelectedSettingList.get(i).setValue(nowTargetPanelB.panel.getValue(i));
				}
			}
		}
	}
	
	private void PageUp()
	{
		PanelPageIndex--;
		if(PanelPageIndex<0)PanelPageIndex = 0;
		CalcButtonOffset();
	}
	private void PageDown()
	{
		PanelPageIndex++;
		if(PanelPageIndex >= PanelButtonList.size()-1)PanelPageIndex = PanelButtonList.size()-1;
		CalcButtonOffset();
	}
	private void SortPanel(int direction)
	{
		int idx_now = PanelButtonList.indexOf(nowTargetPanelB);
		int idx_to = idx_now + direction;
		//LoopとLoopEndは入れ替えしない
		if(nowTargetPanelB.panel.getMode()==Mode.loopend && PanelButtonList.get(idx_to).panel.getMode()==Mode.loop)return;
		if(nowTargetPanelB.panel.getMode()==Mode.loop && PanelButtonList.get(idx_to).panel.getMode()==Mode.loopend)return;
		if(idx_to < 0 || idx_to >= PanelButtonList.size())return;
		//入れ替え
		CustomTexButton temp = PanelButtonList.get(idx_now);
		PanelButtonList.set(idx_now, PanelButtonList.get(idx_to));
		PanelButtonList.set(idx_to, temp);
		CalcButtonOffset();
	}
	private void CalcButtonOffset()
	{
		int btnoffsetx = 0;
		int btnoffsety = 20 + -20 * PanelPageIndex;
		int size = PanelButtonList.size();
		Mode mode; 
		for(int i =0; i<size; ++i)
		{
			CustomTexButton button = PanelButtonList.get(i);
			mode = button.panel.getMode();
			int panelHeight = (mode == Mode.loopend) ? 10 : 20;
			//offsetx+50, 20 + panelIdx*panelHeight
			button.xPosition = offsetx+50+btnoffsetx + ((mode == Mode.loopend) ? -5 : 0);
			button.yPosition = btnoffsety;
			btnoffsetx += (mode == Mode.loop) ? +5 : (mode == Mode.loopend) ? -5 : 0;
			btnoffsety += panelHeight;
		}
	}
	
	private GUIStoryBoardSettingPart findSettingPartFromButtonID(int buttonid, ArrayList<GUIStoryBoardSettingPart> source)
	{
		for(GUIStoryBoardSettingPart part : source)
		{
			if(part.buttonid == buttonid)return part;
		}
		return null;
	}
	
	private void changePanelSettingGui(CustomTexButton panelButton)
	{
		if(nowTargetPanelB!=null)changePanelSettingGuiPos(nowTargetPanelB.panel.getMode(), -1000);
		nowTargetPanelB = panelButton;
		if(nowTargetPanelB!=null)changePanelSettingGuiPos(nowTargetPanelB.panel.getMode(), settingButtonsOffsetX);
	}
	
	private void changePanelSettingGuiPos(IProgramPanel.Mode mode, int x)
	{
		switch(mode)
		{
		case set : 
			for(GUIStoryBoardSettingPart button : ButtonGroupListSet){button.setPos(x);} nowSelectedSettingList = ButtonGroupListSet; break;
		case timer : 
			for(GUIStoryBoardSettingPart button : ButtonGroupListTimer){button.setPos(x);} nowSelectedSettingList = ButtonGroupListTimer; break;
		case linear : 
			for(GUIStoryBoardSettingPart button : ButtonGroupListRun){button.setPos(x);} nowSelectedSettingList = ButtonGroupListRun; break;
		case loop : 
			for(GUIStoryBoardSettingPart button : ButtonGroupListLoop){button.setPos(x);} nowSelectedSettingList = ButtonGroupListLoop; break;
		case loopend : break;
		}
	}
    
    @Override
    public void drawScreen(int x, int y, float partialtick)
    {
        super.drawScreen(x, y, partialtick);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        
        for(GuiTextField textField : textFieldList)
    	{
        	textField.drawTextBox();
    	}
        
        int start = PanelPageIndex;
        int num = 10; //TODO
        int end = (start+num < PanelButtonList.size()) ? start+num : PanelButtonList.size();
        for (int i = start; i < end; ++i)
        {
        	CustomTexButton button = PanelButtonList.get(i);
        	button.drawButton2(this.mc, x, y);
    		this.fontRendererObj.drawString(""+i, button.xPosition-15, button.yPosition, 0x404040);
        }
        
        
        for(GUIStoryBoardSettingPart part : SettingPartButtonlist)
        {
        	part.Draw();
        }
    }
	@Override
    public void drawWorldBackground(int p_146270_1_)
    {
        if (this.mc.theWorld != null)
        {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        }
        else
        {
            this.drawBackground(p_146270_1_);
        }
    }
    
	/*GUIの文字等の描画処理*/
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);
        
//        this.fontRendererObj.drawString("Name", 120, 15, 0x404040);    
        
//        for(GUIName g :  ButtonMap.values())
//        {
//        	this.fontRendererObj.drawString(g.name,g.x,g.y,0x404040);
//        }
        
//        drawString(this.fontRendererObj, ""+ERC_BlockRailManager.clickedTileForGUI.GetPosNum(), 43, 29, 0xffffff);
//        drawString(this.fontRendererObj, String.format("%d",tile.getLimitFrameLength()), 57, 20, 0xffffff);
//        drawString(this.fontRendererObj, String.format("%d",tile.getLimitFrameWidth()), 57, 50, 0xffffff);
//        fontRendererObj.drawString(""+tile.copyNum, 97, 91, 0x404040);
    }
 
    /*GUIの背景の描画処理*/
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseZ)
    {
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(TEX_BASE);
        
        //partList.drawScreen(mouseX, mouseZ, partialTick);
        
        int k = (this.width - this.xSize) / 2;
        int l = 0;//(this.height - this.ySize) / 2;
        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, ySize, this.xSize, 100);

    	//選択中パネルの矢印
        if(nowTargetPanelB!=null)
        {
        	int height = nowTargetPanelB.height;
	        this.mc.renderEngine.bindTexture(TEX_ARROW);
	        this.drawTexturedModalRect(nowTargetPanelB.xPosition+120, nowTargetPanelB.yPosition, 0, 0, 6, height, 6, height);
        }
    }
    
    public void drawTexturedModalRect(int x, int y, int u, int v, int u1, int v1, int width, int height)
    {
    	double f = 1f/(float)(width);
        double f1 = 1f/(float)(height);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + v1), (double)this.zLevel, u*f, v1*f1);
        tessellator.addVertexWithUV((double)(x + u1), (double)(y + v1), (double)this.zLevel, u1*f, v1*f1);
        tessellator.addVertexWithUV((double)(x + u1), (double)(y + 0), (double)this.zLevel, u1*f, v*f1);
        tessellator.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel, u*f, v*f1);
        tessellator.draw();
    }
 
    /*GUIが開いている時にゲームの処理を止めるかどうか。*/
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    
	
    ////////////////////////////text field//////////////////////////////////
	/**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char p_73869_1_, int p_73869_2_)
    {
    	for( GuiTextField textField : textFieldList)
    	{
	        if (textField.textboxKeyTyped(p_73869_1_, p_73869_2_))
	        {
	            this.writeName();
	            return;
	        }
    	}
        super.keyTyped(p_73869_1_, p_73869_2_);
    }
    
    private void writeName()
    {
//        String s = this.textField.getText();
//        Slot slot = this.inventorySlots.getSlot(0);
//
//        if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && s.equals(slot.getStack().getDisplayName()))
//        {
//            s = "";
//        }

//        tile.wheelName = s;
//       ((ERC_ContainerFerris)this.inventorySlots).updateItemName(s);
//        this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", s.getBytes(Charsets.UTF_8)));
    }
    
    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        for(GuiTextField textField : textFieldList)
    	{
        	textField.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    	}
        
    }
    
    
    
}