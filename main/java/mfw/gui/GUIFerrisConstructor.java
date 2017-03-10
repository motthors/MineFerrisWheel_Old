package mfw.gui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import mfw._core.MFW_Core;
import mfw.gui.container.ContainerFerrisConstructor;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageFerrisMisc;
import mfw.tileEntity.TileEntityFerrisConstructor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GUIFerrisConstructor extends GuiContainer {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(MFW_Core.MODID, "textures/gui/ferrisConstructor.png");
    private static int buttonid;
    private GuiTextField textField;
    private TileEntityFerrisConstructor tile;
    int blockposX;
    int blockposY;
    int blockposZ;
    
    int offsetx;
    int offsety;
    
    int ButtonIDFlagDrawCore;
    int ButtonIDFlagDrawEntity;
    int ButtonIDFlagCopyMode;
    
    class GUIName{
		String name;
		int x;
		int y;
		int flag;
		int baseID;
		GUIName(String str,int x, int y, int flag, int base){name=str; this.x=x; this.y=y; this.flag = flag; this.baseID = base;}
	}
    Map<Integer, GUIName> GUINameMap = new HashMap<Integer, GUIName>();
    
    
    public GUIFerrisConstructor(int x, int y, int z, InventoryPlayer invPlayer, TileEntityFerrisConstructor tile)
    {
        super(new ContainerFerrisConstructor(invPlayer, tile));
        this.tile = tile;
        this.tile.gui = this;
        blockposX = x;
        blockposY = y;
        blockposZ = z;
    	xSize = 285;
    	ySize = 165;
    }
 
    // GUIÇäJÇ≠ÇΩÇ—åƒÇŒÇÍÇÈèâä˙âªä÷êî
	@Override
	public void initGui()
    {
		super.initGui();
		GUINameMap.clear();
//		this.guiLeft = this.width*7/8 - xSize/2;
		buttonid = 0;
		// É{É^Éììoò^
		offsetx = this.guiLeft + 20;
		offsety = this.guiTop;
//		int offset = 4;
//        int length = 27;
		
        this.textField = new GuiTextField(this.fontRendererObj, offsetx + 136, offsety + 15, 95, 12);
        this.textField.setTextColor(-1);
        this.textField.setDisabledTextColour(-1);
        this.textField.setEnableBackgroundDrawing(false);
        this.textField.setMaxStringLength(40);
        this.textField.setText(tile.wheelName);

        addButton6(-13, 30, "Length", MessageFerrisMisc.GUIAddLength);
        addButton6(-13, 60, "Width", MessageFerrisMisc.GUIAddWidth);

		addButton1(50, 26, 150, 52, "Create!", MessageFerrisMisc.GUIConstruct);
		
		ButtonIDFlagDrawCore = buttonid;
		addButton1(24, 13, 70, 120, tile.FlagDrawCore?"ON":"OFF", "draw core", MessageFerrisMisc.GUIDrawCoreFlag);
		
		addButton2(30, 84, "copy", MessageFerrisMisc.GUIAddCopyNum);
		ButtonIDFlagDrawEntity = buttonid;
		addButton1(24, 13, 70, 140, tile.FlagDrawEntity?"ON":"OFF", "draw Mobs", MessageFerrisMisc.GUIDrawEntityFlag);
		
		ButtonIDFlagCopyMode = buttonid;
		addButton1(34, 13, 60, 100, tile.copymode==0?"Add":"Clone", "copy mode", MessageFerrisMisc.GUICopyModeChange);
    }

	public void addButton1(int lenx, int leny, int posx, int posy, String str, int flag)
	{
		addButton1(lenx, leny, posx, posy, str, "", flag);
	}
	@SuppressWarnings("unchecked")
	public void addButton1(int lenx, int leny, int posx, int posy, String str, String buttonStr, int flag)
	{
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx, posy+offsety, lenx, leny, str));
    	GUINameMap.put(buttonid-1, new GUIName(buttonStr, posx-40, posy, flag, -1));
    }
	@SuppressWarnings("unchecked")
	public void addButton2(int posx, int posy, String str, int flag)
    {
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx,    posy+offsety, 18, 13, "-"));
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+20, posy+offsety, 18, 13, "+"));
    	GUIName data = new GUIName(str,posx-10,posy,flag,buttonid-2);
    	GUINameMap.put(buttonid-2, data);
    	GUINameMap.put(buttonid-1, data);
    }
    @SuppressWarnings("unchecked")
    public void addButton4(int posx, int posy, String str, int flag)
    {
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx ,   posy+offsety, 18, 14, "--"));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+19, posy+offsety, 14, 14, "-"));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+34, posy+offsety, 14, 14, "+"));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+49, posy+offsety, 18, 14, "++"));
		GUIName data = new GUIName(str,posx-14,posy+2,flag,buttonid-4);
		GUINameMap.put(buttonid-4, data);
    	GUINameMap.put(buttonid-3, data);
    	GUINameMap.put(buttonid-2, data);
    	GUINameMap.put(buttonid-1, data);
    }
    @SuppressWarnings("unchecked")
    public void addButton6(int posx, int posy, String str, int flag)
    {
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx-5,  posy+offsety, 24, 14, "---"));
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+20, posy+offsety, 18, 14, "--"));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+39, posy+offsety, 12, 14, "-"));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+52, posy+offsety, 12, 14, "+"));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+65, posy+offsety, 18, 14, "++"));
		this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx+84, posy+offsety, 24, 14, "+++"));
		GUIName data = new GUIName(str,posx+20,posy-10,flag,buttonid-6);
		GUINameMap.put(buttonid-6, data);
    	GUINameMap.put(buttonid-5, data);
		GUINameMap.put(buttonid-4, data);
    	GUINameMap.put(buttonid-3, data);
    	GUINameMap.put(buttonid-2, data);
    	GUINameMap.put(buttonid-1, data);
    }
    
    @Override
	public void onGuiClosed()
    {
//    	ERC_ManagerFerrisHweel.CloseRailGUI();
    	tile.wheelName = textField.getText();
		super.onGuiClosed();
	}
    
    public String getName()
    {
    	return textField.getText();
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
    
	/*GUIÇÃï∂éöìôÇÃï`âÊèàóù*/
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);

        this.fontRendererObj.drawString("Name", 120, 15, 0x404040);    
        
        for(GUIName g :  GUINameMap.values())
        {
        	this.fontRendererObj.drawString(g.name,g.x,g.y,0x404040);
        }
        
//        drawString(this.fontRendererObj, ""+ERC_BlockRailManager.clickedTileForGUI.GetPosNum(), 43, 29, 0xffffff);
        drawString(this.fontRendererObj, String.format("%d",tile.getLimitFrameLength()), 57, 20, 0xffffff);
        drawString(this.fontRendererObj, String.format("%d",tile.getLimitFrameWidth()), 57, 50, 0xffffff);
        fontRendererObj.drawString(""+tile.copyNum, 97, 84, 0x404040);
    }
 
    /*GUIÇÃîwåiÇÃï`âÊèàóù*/
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseZ)
    {
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(TEXTURE);
        
//        this.ERCRail_drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, xSize, ySize);
        
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.ERCRail_drawTexturedModalRect(k, l, 0, 0, this.xSize, 165, this.xSize, 256);
        // draw progress
        if (this.tile.isCrafting())
        {
            int i1 = tile.getCookProgressScaled(24);
            this.drawTexturedModalRect(k + 225, l + 58, 0, 166, i1 + 1, 16);
        }
    }
    
    public void ERCRail_drawTexturedModalRect(int x, int y, int u, int v, int u1, int v1, int width, int height)
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
 
    /*GUIÇ™äJÇ¢ÇƒÇ¢ÇÈéûÇ…ÉQÅ[ÉÄÇÃèàóùÇé~ÇﬂÇÈÇ©Ç«Ç§Ç©ÅB*/
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

	@Override
	protected void actionPerformed(GuiButton button)
	{
		GUIName obj = GUINameMap.get(button.id);
		int data = (button.id - obj.baseID);
		
		switch(this.GUINameMap.get(button.id).flag)
		{
		case MessageFerrisMisc.GUIConstruct :
			tile.startConstructWheel();
			return;
		case MessageFerrisMisc.GUIAddLength:
		case MessageFerrisMisc.GUIAddWidth:
			switch(data)
			{
			case 0 : data = -50;	break;
			case 1 : data = -5;		break;
			case 2 : data = -1;		break;
			case 3 : data =  1;		break;
			case 4 : data =  5;		break;
			case 5 : data =  50;	break;
			}
			break;
		case MessageFerrisMisc.GUIAddCopyNum:
			switch(data)
			{
			case 0 : data = -1;	break;
			case 1 : data =  1;	break;
			}
			break;
		case MessageFerrisMisc.GUIDrawCoreFlag:
			((GuiButtonExt)buttonList.get(ButtonIDFlagDrawCore)).displayString = tile.FlagDrawCore?"OFF":"ON";
			break;
		case MessageFerrisMisc.GUIDrawEntityFlag:
			((GuiButtonExt)buttonList.get(ButtonIDFlagDrawEntity)).displayString = tile.FlagDrawEntity?"OFF":"ON";
			break;
		case MessageFerrisMisc.GUICopyModeChange:
			((GuiButtonExt)buttonList.get(ButtonIDFlagCopyMode)).displayString = tile.copymode==0?"Clone":"Add";
			break;
		}

		MessageFerrisMisc packet = new MessageFerrisMisc(blockposX,blockposY,blockposZ,
				this.GUINameMap.get(button.id).flag , data, textField.getText().getBytes(Charsets.UTF_8));
	    MFW_PacketHandler.INSTANCE.sendToServer(packet);
	}
    
	
    ////////////////////////////text field//////////////////////////////////
	/**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char p_73869_1_, int p_73869_2_)
    {
        if (this.textField.textboxKeyTyped(p_73869_1_, p_73869_2_))
        {
//        	ERC_Logger.info("name:"+textField.getText());
            this.writeName();
        }
        else
        {
            super.keyTyped(p_73869_1_, p_73869_2_);
        }
    }
    
    private void writeName()
    {
        String s = this.textField.getText();
        Slot slot = this.inventorySlots.getSlot(0);

        if (slot != null && slot.getHasStack() && !slot.getStack().hasDisplayName() && s.equals(slot.getStack().getDisplayName()))
        {
            s = "";
        }

        tile.wheelName = s;
//       ((ERC_ContainerFerris)this.inventorySlots).updateItemName(s);
//        this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", s.getBytes(Charsets.UTF_8)));
    }
    
    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.textField.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        this.textField.drawTextBox();
    }
}
