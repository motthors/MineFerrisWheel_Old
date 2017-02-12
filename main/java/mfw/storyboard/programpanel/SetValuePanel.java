package mfw.storyboard.programpanel;

import java.util.List;

import mfw._core.MFW_Logger;
import mfw.tileEntity.TileEntityFerrisWheel;

public class SetValuePanel implements IProgramPanel {
	
	private static String[] targets = {
			"Rotation",
			"Accel",
			"Resist",
			"Size",
			"Amp",
			"Phase",
	};
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "Target"),
			new DataPack(Type.inputvalue, "Value"),
	};
	
	final int id_Mode = 0;
	final int id_Value = 1;
	
	private TileEntityFerrisWheel tile;
	private int rotindex = 0;
	private float Value = 0.0f;

	@Override
	public void Init(TileEntityFerrisWheel tile) 
	{
		this.tile = tile;
	}

	@Override
	public Mode getMode() {
		return Mode.set;
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
		if(apiIndex==id_Mode)
			rotindex = ( rotindex + 1 ) % targets.length;
		return new int[]{apiIndex};
	}
	
	@Override
	public String getValue(int apiIndex){
		switch(apiIndex)
		{
		case id_Mode : return targets[rotindex];
		case id_Value : return String.format("%.2f",Value);
		default : return "";
		}
	}

	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			switch(apiIndex)
			{
			case id_Mode : 
				rotindex = Integer.parseInt((String) value);
				break;
			case id_Value : 
				Value = Float.parseFloat((String) value);
				break;
			default : return new int []{};
			}
			return new int[]{apiIndex};
		}catch(NumberFormatException e){
			return new int[]{};
		}
	}
	
	@Override
	public void start()
	{
		//nothing
	}
	
	@Override
	public boolean CanDoNext()
	{
		return true;
	}
	
	@Override
	public boolean run() {
		switch(rotindex)
		{
		case 0/*rotate*/ : tile.rotation = Value; break;
		case 1/*Accel */ : tile.rotAccel = Value; break;
		case 2/*Resist*/ : tile.rotResist = Value; break;
		case 3/*Size  */ : tile.wheelSize = Value; break;
		case 4/*Amp   */ : tile.rotMiscFloat1 = Value; break;
		case 5/*Phase */ : tile.rotMiscFloat2 = Value; break;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "Sx"+rotindex+"x"+Value+"#";
	}

	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_Mode, p[1]);
		setValue(id_Value, p[2]);
	}
}
