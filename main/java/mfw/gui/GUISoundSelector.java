//package mfw.gui;
//
//import java.util.ArrayList;
//
//import mfw._core.MFW_Core;
//import mfw.sound.SoundManager;
//import mfw.tileEntity.TileEntityFerrisWheel;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.audio.SoundList;
//import net.minecraft.client.gui.GuiLanguage;
//import net.minecraft.client.gui.GuiSlot;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.client.resources.Language;
//
//public class GUISoundSelector extends GuiSlot {
//
//	GUIFerrisCore parent;
//	TileEntityFerrisWheel tile;
//	private ArrayList<String> soundlist = SoundManager.sounds;
//
//	public GUISoundSelector(GUIFerrisCore parent, TileEntityFerrisWheel tile) 
//	{
//		super(Minecraft.getMinecraft(), 
//				parent.width/3, //width
//				parent.height, 			//height
//				0,		//top
//				parent.height,		 	//bottom
//				16);							//slot height
//		this.left = parent.width/3*2-100;
//		this.right = parent.width;
//		this.parent = parent;
//		this.tile = tile;
//	}
//
//	public void updateList() 
//	{
//		this.soundlist = SoundManager.sounds;
//	}
//
//	protected int getSize() 
//	{
//		return this.soundlist.size();
//	}
//
//	protected void elementClicked(int par1, boolean isDoubleClick, int par3, int par4) 
//	{
//		if(isDoubleClick){
//			tile.SetSoundIndex(par1);
//		}
//		else{
//			String domain = soundlist.get(par1);
//			tile.getWorldObj().playSoundEffect(tile.xCoord+0.5, tile.yCoord+0.5, tile.zCoord+0.5, domain, 1.0F, 0.9F);
//		}
//	}
//
//	protected boolean isSelected(int par1) {
//		return tile.GetSoundIndex() == par1;
//	}
//
//
//	protected void drawBackground() {
//	}
//
//	protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5, int par6, int par7) 
//	{
//		parent.drawCenteredString(parent.GetFontRenderer(), 
//				this.soundlist.get(par1),
//				this.width / 2, par3 + 1, 
//				16777215);
//	}
//}