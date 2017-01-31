package mfw.storyboard;

import java.util.ArrayList;

import mfw.storyboard.programpanel.IProgramPanel;
import mfw.storyboard.programpanel.IProgramPanel.Mode;
import mfw.storyboard.programpanel.SetValuePanel;
import mfw.storyboard.programpanel.TimerPanel;
import mfw.tileEntity.TileEntityFerrisWheel;

public class StoryBoardManager {

	//texture array
	
	TileEntityFerrisWheel tile;
	
	//gui
	
	private ArrayList<IProgramPanel> PanelList;
	private IProgramPanel nowTargetPanel;
	
	public StoryBoardManager(TileEntityFerrisWheel tile)
	{
		this.tile = tile;
		PanelList = new ArrayList<IProgramPanel>();
	}
	
	public void addPanel()
	{
		PanelList.add(new SetValuePanel());
	}
	
	public void insertPanel(int idx)
	{
//		PanelList.add(idx, new IProgramPanel());
	}
	
	public void clear()
	{
		PanelList.clear();
	}
	
	public ArrayList<IProgramPanel> getPanelList()
	{
		return PanelList;
	}
	
	public static IProgramPanel createPanel(String mode)
	{
		switch(mode){
		case "set" : return createPanel(Mode.set);
		case "timer" : return createPanel(Mode.timer);
		}
		return null;
	}
	public static IProgramPanel createPanel(IProgramPanel.Mode mode)
	{
		switch(mode){
		case set : return new SetValuePanel();
		case timer : return new TimerPanel();
		}
		return null;
	}
	
	//run
	
	//drawgui
}
