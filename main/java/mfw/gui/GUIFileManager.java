package mfw.gui;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import mfw._core.MFW_Core;
import mfw.gui.container.ContainerFileManager;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageFerrisMisc;
import mfw.tileEntity.TileEntityFileManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GUIFileManager extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(MFW_Core.MODID, "textures/gui/fileManager.png");
    private int buttonid;
    public GuiTextField textField;
    private TileEntityFileManager tile;
    
    int blockposX;
    int blockposY;
    int blockposZ;
    
    int offsetx;
    int offsety;
    
    class GUIName{
		String name;
		int x;
		int y;
		int flag;
		int baseID;
		GUIName(String str,int x, int y, int flag, int base){name=str; this.x=x; this.y=y; this.flag = flag; this.baseID = base;}
	}
    Map<Integer, GUIName> GUINameMap = new HashMap<Integer, GUIName>();
    
    
    public GUIFileManager(int x, int y, int z, InventoryPlayer invPlayer, TileEntityFileManager tile)
    {
        super(new ContainerFileManager(invPlayer, tile));
        this.tile = tile;
        blockposX = x;
        blockposY = y;
        blockposZ = z;
//    	xSize = 270;
//    	ySize = 166;
    }
    
    @Override
	public void initGui()
    {
		super.initGui();
        this.tile.gui = this;
		GUINameMap.clear();
//		this.guiLeft = this.width*7/8 - xSize/2;
		buttonid = 0;
		// ƒ{ƒ^ƒ““o˜^
		offsetx = this.guiLeft + 20;
		offsety = this.guiTop;
//		int offset = 4;
//        int length = 27;

		this.textField = new GuiTextField(this.fontRendererObj, offsetx + 26, offsety + 15, 110, 12);
        this.textField.setTextColor(-1);
        this.textField.setDisabledTextColour(-1);
        this.textField.setEnableBackgroundDrawing(false);
        this.textField.setMaxStringLength(40);
        this.textField.setText(tile.reName);
		
        addButton1(48, 15, 33, 48, "Save", MessageFerrisMisc.GUIFileWrite);
        addButton1(48, 15, 33, 66, "Load", MessageFerrisMisc.GUIFileRead);
        addButton1(50, 13, 93, 29, "ReName", MessageFerrisMisc.GUIFileRename);
    }
    
    @SuppressWarnings("unchecked")
    public void addButton1(int lenx, int leny, int posx, int posy, String str, int flag)
    {
    	this.buttonList.add(new GuiButtonExt(buttonid++, posx+offsetx, posy+offsety, lenx, leny, str));
    	GUINameMap.put(buttonid-1, new GUIName("",-100,-100, flag, -1));
    }
    
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
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseZ)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseZ);   
//        
//        for(GUIName g :  GUINameMap.values())
//        {
//        	this.fontRendererObj.drawString(g.name,g.x,g.y,0x404040);
//        }
//        
//        drawString(this.fontRendererObj, "Accel :"+String.format("% 4.2f",tile.rotAccel), 210, 5, 0xffffff);
//        drawString(this.fontRendererObj, "Speed :"+String.format("% 4.2f",tile.rotSpeed), 210, 15, 0xffffff);
//        drawString(this.fontRendererObj, "Resist :"+String.format("% 4.1f",tile.rotResist*100f), 203, 32, 0xffffff);
//        drawString(this.fontRendererObj, "Size :"+String.format("% 4.2f",tile.wheelSize), 207, 48, 0xffffff);

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
    
    @Override
   	public void onGuiClosed()
       {
//       	ERC_ManagerFerrisHweel.CloseRailGUI();
       	tile.reName = textField.getText();
       	tile.gui = null;
   		super.onGuiClosed();
   	}
    
    @Override
	protected void actionPerformed(GuiButton button)
	{
		GUIName obj = GUINameMap.get(button.id);
		int data = (button.id - obj.baseID);
		int flag = this.GUINameMap.get(button.id).flag;

		switch(flag)
		{
		case MessageFerrisMisc.GUIFileWrite:
			tile.FileWrite(); 
			return;
		case MessageFerrisMisc.GUIFileRead:
			tile.FileRead(); 
			return;
		case MessageFerrisMisc.GUIFileRename:
    		tile.reName = textField.getText();
    		if(tile.reName.equals(""))return;
			break;
		}
		MessageFerrisMisc packet = new MessageFerrisMisc(blockposX,blockposY,blockposZ,
				this.GUINameMap.get(button.id).flag , data, tile.reName.getBytes(Charsets.UTF_8));
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
//          	ERC_Logger.info("name:"+textField.getText());
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

          tile.reName = s;
//         ((ERC_ContainerFerris)this.inventorySlots).updateItemName(s);
//          this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("MC|ItemName", s.getBytes(Charsets.UTF_8)));
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
