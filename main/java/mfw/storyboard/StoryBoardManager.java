package mfw.storyboard;

import java.util.ArrayList;

import mfw.tileEntity.TileEntityFerrisWheel;

public class StoryBoardManager {

	//texture array
	
	TileEntityFerrisWheel tile;
	
	//gui
	
	private ArrayList<ProgramPanel> PanelList;
	private ProgramPanel nowTargetPanel;
	
	public StoryBoardManager(TileEntityFerrisWheel tile)
	{
		this.tile = tile;
		PanelList = new ArrayList<ProgramPanel>();
	}
	
	public void addPanel()
	{
		PanelList.add(new ProgramPanel());
	}
	
	public void insertPanel(int idx)
	{
		PanelList.add(idx, new ProgramPanel());
	}
	
	public void clear()
	{
		PanelList.clear();
	}
	
	//run
	
	//drawgui
}
