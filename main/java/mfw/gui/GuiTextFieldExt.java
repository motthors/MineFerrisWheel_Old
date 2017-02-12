package mfw.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextFieldExt extends GuiTextField {

	FontRenderer renderer;
	INotifiedHandler handler;
	public int id;
	int BackgroundColor;
	
	public GuiTextFieldExt(INotifiedHandler handler, FontRenderer renderer, int x, int y, int width, int height) {
		super(renderer, x, y, width, height);
		this.handler = handler;
		this.renderer = renderer;
		BackgroundColor = 0xFF000000;
	}
	
	@Override
	public boolean textboxKeyTyped(char c, int keycode)
	{
		if(c=='\r'){
			handler.actionPerformed(this);
			return false;
		}
		if(( c < '0'|| '9' < c ) && c != '.' && c != '-' && c != 0 && c!='\b') return false;
		return super.textboxKeyTyped(c, keycode);
	}
	
	public void setFocused(boolean flag)
    {
        if (flag == false && this.isFocused())
        {
        	handler.actionPerformed(this);
        }

        super.setFocused(flag);
    }
	
	public void changeColor(int color)
	{
		BackgroundColor = color;
	}
	
}
