package mfw.storyboard;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextFieldExt extends GuiTextField {

	INotifiedHandler handler;
	public int id;
	
	public GuiTextFieldExt(INotifiedHandler handler, FontRenderer renderer, int x, int y, int width, int height) {
		super(renderer, x, y, width, height);
		this.handler = handler;
	}
	
	@Override
	public boolean textboxKeyTyped(char c, int p_146201_2_)
	{
		if(( c < '0'|| '9' < c ) && c != '.' ) return false;
		return super.textboxKeyTyped(c, p_146201_2_);
	}
	
	public void setFocused(boolean flag)
    {
        if (flag == false && this.isFocused())
        {
        	handler.actionPerformed(this);
        }

        super.setFocused(flag);
    }
}
