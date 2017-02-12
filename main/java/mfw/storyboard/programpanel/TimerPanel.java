package mfw.storyboard.programpanel;

import java.nio.ByteBuffer;
import java.util.List;

import mfw._core.MFW_Logger;
import mfw.tileEntity.TileEntityFerrisWheel;

public class TimerPanel implements IProgramPanel {
	
	private static DataPack[] datapacks = {
			new DataPack(Type.inputvalue, "Second"),
			new DataPack(Type.inputvalue, "Tick"),
	};
	private TileEntityFerrisWheel tile;
	
	final int id_Time = 0;
	final int id_Tick = 1;
	
	int tickTimeCount;
	int tickTimeTarget;

	@Override
	public void Init(TileEntityFerrisWheel tile) 
	{
		this.tile = tile;
	}

	@Override
	public Mode getMode() {
		return Mode.timer;
	}
	
	@Override
	public int ApiNum(){ return 2; }
	
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
		return new int[]{};
	}
	
	@Override
	public String getValue(int apiIndex){
		switch(apiIndex)
		{
		case id_Tick : return ""+tickTimeTarget;
		case id_Time : return String.format("%.2f",tickTimeTarget/20.0);
		}
		return "";
	}
	
	/**
	 * retval : Valueの更新が必要な項目のAPIIndexの配列
	 */
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			double v = (double)(Double.parseDouble((String)value));
			switch(apiIndex)
			{
			case id_Tick : tickTimeTarget = (int)v ; break;
			case id_Time : tickTimeTarget = (int)(v*20); break;
			}
			return new int[]{id_Tick,id_Time};
		}catch(NumberFormatException e){
			
			return new int[]{};
		}
	}

	@Override
	public void start()
	{
		tickTimeCount = 0;
		canDispose = false;
	}
	
	private boolean canDispose = false;
	@Override
	public boolean CanDoNext()
	{
		return canDispose;
	}
	
	@Override
	public boolean run() {
		if(tickTimeCount >= tickTimeTarget)
		{
			canDispose = true;
			return true;
		}
		tickTimeCount ++ ;
		return canDispose;
	}

	@Override
	public String toString()
	{
		return "Tx"+tickTimeTarget+"#";
	}

	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_Tick, p[1]);
	}
}
