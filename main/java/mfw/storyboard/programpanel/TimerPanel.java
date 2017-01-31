package mfw.storyboard.programpanel;

import java.util.function.Consumer;

import mfw.storyboard.programpanel.IProgramPanel.Mode;
import mfw.storyboard.programpanel.IProgramPanel.Type;
import mfw.tileEntity.TileEntityFerrisWheel;

public class TimerPanel implements IProgramPanel {
	
	private static DataPack[] datapacks = {
			new DataPack(Type.inputvalue, "Second", 0.0),
			new DataPack(Type.inputvalue, "Tick", 0),
	};
	private TileEntityFerrisWheel tile;
	
	int tickTimeCount;
	int tickTimeMax;

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
	public Type getType(int apiIndex){
		return datapacks[apiIndex].type;
	}
	
	@Override
	public String getDescription(int apiIndex) {
		return datapacks[apiIndex].description;
	}
	
	@Override
	public String getValue(int apiIndex){
		Object value = datapacks[apiIndex].value;
		if(apiIndex==0)
			return String.format("%.2f",value);
		if(apiIndex==1)
			return value.toString();
		return null;
	}
	
	/**
	 * retval : Valueの更新が必要な項目のAPIIndexの配列
	 */
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
		tickTimeMax = (int)(Double.parseDouble((String)value) * 20);
		if(apiIndex==1)tickTimeMax /= 20;
		datapacks[0].value = (float)tickTimeMax / 20.0;
		datapacks[1].value = (int)tickTimeMax;
		return new int[]{0,1};
		}catch(NumberFormatException e){
			return new int[]{};
		}
	}

	@Override
	public void start()
	{
		tickTimeCount = 0;
	}
	
	@Override
	public boolean run() {
		if(tickTimeCount == tickTimeMax)return true;
		tickTimeCount ++ ;
		return false;
	}

	
}
