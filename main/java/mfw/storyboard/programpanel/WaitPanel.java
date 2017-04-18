package mfw.storyboard.programpanel;

import java.nio.ByteBuffer;
import java.util.List;

import mfw._core.MFW_Logger;
import mfw.storyboard.programpanel.IProgramPanel.DataPack;
import mfw.storyboard.programpanel.IProgramPanel.Type;
import mfw.tileEntity.TileEntityFerrisWheel;

public class WaitPanel implements IProgramPanel {
	
	private static String[] modes = {
			"RsInput",
			"Notify",
			"NonStop",
	};
	
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "Mode"),
	};
	private TileEntityFerrisWheel tile;
	
	final int id_Mode = 0;
	
	private int modeindex = 0;

	private boolean canDispose = false;
	
	@Override
	public void Init(TileEntityFerrisWheel tile) 
	{
		this.tile = tile;
	}

	@Override
	public Mode getMode() {
		return Mode.wait;
	}
	
	@Override
	public int ApiNum(){ return 1; }
	
	@Override
	public void insertSubPanelToList(List<IProgramPanel> inout_panel)
	{}
	
	@Override
	public Type getType(int apiIndex){
		return datapacks[apiIndex].type;
	}
	
	@Override
	public String getDescription(int apiIndex) {
		return datapacks[apiIndex].description;
	}
	
	@Override
	public int[] Clicked(int apiIndex)
	{
		modeindex = (modeindex + 1) % modes.length;
		return new int[]{apiIndex};
	}
	
	@Override
	public String getValue(int apiIndex){
		return modes[modeindex];
	}
	
	/**
	 * retval : Valueの更新が必要な項目のAPIIndexの配列
	 */
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			modeindex = Integer.parseInt((String) value);
			return new int[]{apiIndex};
		}catch(NumberFormatException e){
			return new int[]{};
		}
	}

	@Override
	public void start()
	{
		if(modeindex == 2) // nonstop
		{
			canDispose = true;
		}
		else canDispose = false;
	}
	
	
	@Override
	public boolean CanDoNext()
	{
		return canDispose;
	}
	
	@Override
	public boolean run() {
		return canDispose;
	}

	@Override
	public void RSHandler(){if(modeindex==0)canDispose=true;}
	@Override
	public void NotifyHandler(){if(modeindex==1)canDispose=true;}
	
	@Override
	public String toString()
	{
		return "Wx"+modeindex+";";
	}

	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_Mode, p[1]);
	}
	
	@Override
	public String displayDescription()
	{
		return "wait "+modes[modeindex];
	}
}
