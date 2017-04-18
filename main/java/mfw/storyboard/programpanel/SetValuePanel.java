package mfw.storyboard.programpanel;

import java.util.List;

import mfw._core.MFW_Logger;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.util.MathHelper;

public class SetValuePanel implements IProgramPanel {
	
	private static String[] targets = {
			"Angle",
			"Accel",
			"Weight",
			"Size",
			"Amp",
			"Phase",
	};
	private static String[] modes = {
			"Set",
			"Add",
	};
	
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "Mode"),
			new DataPack(Type.change, "Target"),
			new DataPack(Type.inputvalue, "Value"),
	};
	
	final int id_Mode = 0;
	final int id_Target = 1;
	final int id_Value = 2;
	
	private TileEntityFerrisWheel tile;
	private int modeindex = 0;
	private int targetindex = 0;
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
	public int ApiNum(){ return 3; }
	
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
		switch(apiIndex){
		case id_Mode :
			modeindex = (modeindex + 1) % modes.length;
			break;
		case id_Target :
			targetindex = ( targetindex + 1 ) % targets.length;
			break;
		}
		return new int[]{apiIndex};
	}
	
	@Override
	public String getValue(int apiIndex){
		switch(apiIndex)
		{
		case id_Mode : return modes[modeindex];
		case id_Target : return targets[targetindex];
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
				modeindex = Integer.parseInt((String) value);
				modeindex = MathHelper.clamp_int(modeindex, 0, modes.length-1);
				break;
			case id_Target : 
				targetindex = Integer.parseInt((String) value);
				targetindex = MathHelper.clamp_int(targetindex, 0, targets.length-1);
				break;
			case id_Value : 
				Value = ClampWithTarget(Float.parseFloat((String) value), 0);
				break;
			default : return new int []{};
			}
			return new int[]{apiIndex};
		}catch(NumberFormatException e){
			return new int[]{};
		}
	}
	
	private float ClampWithTarget(float value, int targetindex)
	{
		switch(targetindex)
		{
		case 0/*rotate*/ : return value;
		case 1/*Accel */ : return value;
		case 2/*Resist*/ : return MathHelper.clamp_float(value, 0.001f, 0.99f);
		case 3/*Size  */ : return (value < 0) ? 0f : value;
		case 4/*Amp   */ : return value;
		case 5/*Phase */ : return MathHelper.clamp_float(value, -180f, 180f);
		}
		return 1;
	}
	
	@Override
	public void start()
	{
		//MFW_Logger.debugInfo("***S_"+modeindex+"_"+Value);
		if(modeindex == 0) //set
		{
			switch(targetindex)
			{
			case 0/*rotate*/ : tile.rotation.set(Value); tile.speedTemp=Value; break;
			case 1/*Accel */ : tile.rotAccel = Value; break;
			case 2/*Resist*/ : tile.rotResist = 1f/Value; break;
			case 3/*Size  */ : tile.wheelSize.set(Value);; break;
			case 4/*Amp   */ : tile.rotMiscFloat1.set(Value); break;
			case 5/*Phase */ : tile.rotMiscFloat2.set(Value); break;
			}
			return;
		}
		else // add
		{
			switch(targetindex)
			{
			case 0/*rotate*/ : tile.rotation.add(Value); tile.speedTemp+=Value; break;
			case 1/*Accel */ : tile.rotAccel += Value; break;
			case 2/*Resist*/ : tile.rotResist += 1f/Value; break;
			case 3/*Size  */ : tile.wheelSize.add(Value); break;
			case 4/*Amp   */ : tile.rotMiscFloat1.add(Value); break;
			case 5/*Phase */ : tile.rotMiscFloat2.add(Value); break;
			}
			return;
		}
	}
	
	@Override
	public boolean CanDoNext()
	{
		return true;
	}
	
	@Override
	public boolean run() {
		//MFW_Logger.debugInfo("S_"+modeindex+"_"+Value);
		return true;
	}

	@Override
	public void RSHandler(){}
	@Override
	public void NotifyHandler(){}
	
	@Override
	public String toString()
	{
		return "Sx"+modeindex+"x"+targetindex+"x"+Value+";";
	}
	
	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_Mode, p[1]);
		setValue(id_Target, p[2]);
		setValue(id_Value, p[3]);
	}
	
	@Override
	public String displayDescription()
	{
		return targets[targetindex]
				+ (modeindex==1 ? " += " : " << ")
				+ Value;
	}
}
