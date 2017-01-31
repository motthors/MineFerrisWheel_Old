package mfw.gui;

import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;

import cpw.mods.fml.client.config.GuiButtonExt;
import mfw._core.MFW_Logger;
import mfw.gui.GUIStoryBoard.buttonGroup;
import mfw.storyboard.GuiTextFieldExt;
import mfw.storyboard.programpanel.IProgramPanel;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

public class GUIStoryBoardSettingPart {

	IProgramPanel.Type type;
	
	int buttonid;
	int textid;
	GuiButton button;
	GuiTextField textField;
	
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
		case change : 
			String buttontext = (String)value;
			button = new GuiButtonExt(-1, x, y, 40, 20, buttontext);
			buttonid = parent.AddToButtonList(button, group);
			break;
		case inputvalue	:
			String inittext = value.toString();
			textField = new GuiTextFieldExt(parent, parent.GetFontRenderer(), x, y, 40, 20);
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
		default : break;
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
		this.fontRendererObj.drawString(description, drawX - 30, drawY, 0x404040);
//		if(textField!=null)this.fontRendererObj.drawString(textField.getText(), drawX - 30, drawY+15, 0x404040);
//		MFW_Logger.debugInfo(description + " : " + drawX);
	}

}
