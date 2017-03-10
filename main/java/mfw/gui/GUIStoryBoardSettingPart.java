package mfw.gui;

import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;

import org.apache.commons.lang3.math.NumberUtils;

import cpw.mods.fml.client.config.GuiButtonExt;
import mfw._core.MFW_Logger;
import mfw.gui.GUIStoryBoard.buttonGroup;
import mfw.sound.SoundManager;
import mfw.storyboard.programpanel.IProgramPanel;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

public class GUIStoryBoardSettingPart {

	IProgramPanel.Type type;
	
	int buttonid;
	int textid;
	GuiButton button;
	GuiTextFieldExt textField;
	
	String description;
	
	int drawX;
	int drawY;
	
	FontRenderer fontRendererObj;
	
	public GUIStoryBoardSettingPart(GUIStoryBoard parent, int x, int y, IProgramPanel.Type type, String desc, Object value, FontRenderer fontRendererObj, buttonGroup group)
	{
		this.fontRendererObj = fontRendererObj;
		this.type = type;
		this.drawY = y;
		this.drawX = x;
		description = desc;
		switch(type)
		{
		case soundselector : 
		case change : 
			String buttontext = (String)value;
			button = new GuiButtonExt(-1, x, y, 50, 20, buttontext);
			buttonid = parent.AddToButtonList(button, group);
			break;
		case inputvalue	:
			String inittext = value.toString();
			textField = new GuiTextFieldExt(parent, parent.GetFontRenderer(), x, y, 50, 20);
			textField.setText(inittext);
			textid = parent.AddToTextFieldList(textField);
			break;
		default : break;
		}
	}
	
	public void setPos(int x)
	{
		drawX = x;
		switch(type)
		{
		case change : 
		case soundselector :
			button.xPosition = x;
//			button.yPosition = y;
			break;
		case inputvalue	:
			textField.xPosition = x;
//			text.yPosition = y;
			break; 
		default : break;
		}
	}
	
	public void setValue(String value)
	{
		switch(type)
		{
		case change : 
			button.displayString = value;
			break;
		case inputvalue	:
			textField.setText(value);
			break;
		case soundselector :
			button.displayString = "";
			description = SoundManager.getSoundDomain(Integer.parseInt(value));
		default : break;
		}
	}
	
	public String getValue()
	{
		switch(type)
		{
		case change : 
			return button.displayString;
		case inputvalue	:
			return textField.getText();
		case soundselector : 
			return "";
		default : return "";
		}
	}
	
	public boolean findButton(int buttonid)
	{
		return buttonid == this.buttonid;
	}
	
	public boolean findText(int textid)
	{
		return textid == this.textid;
	}
	
	public void Draw()
	{
		this.fontRendererObj.drawString(description, drawX - 30, drawY-10, 0x404040);
//		if(textField!=null)this.fontRendererObj.drawString(textField.getText(), drawX - 30, drawY+15, 0x404040);
//		MFW_Logger.debugInfo(description + " : " + drawX);
	}

}
