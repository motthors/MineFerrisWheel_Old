package mfw.storyboard.programpanel;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import mfw._core.MFW_Logger;
import mfw.storyboard.programpanel.IProgramPanel.DataPack;
import mfw.storyboard.programpanel.IProgramPanel.Type;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.util.MathHelper;

public class KeyFramePanel implements IProgramPanel {
	
	private static String[] targets = {
			"Angle",
			"Accel",
			//"Weight",
			"Size",
			"Amp",
			"Phase",
	};
	private static String[] modes = {
			"Set",
			"Add",
	};
	private static String[] interpolations = {
			"Linear",
			"InSine",
		    "OutSine",
		    "InOutSine",
		    "InBounce",
		    "OutBounce",
		    "InBack",
		    "OutBack",
		    "InSpring",
		    "OutSpring",
	};
	
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "parallel"),
			new DataPack(Type.change, "Mode"),
			new DataPack(Type.change, "Target"),
			new DataPack(Type.inputvalue, "To"),
			new DataPack(Type.inputvalue, "Second"),
			new DataPack(Type.inputvalue, "Tick"),
			new DataPack(Type.change, "Interpolation"),
	};
	private TileEntityFerrisWheel tile;
	
	final int id_Parallel = 0;
	final int id_Mode = 1;
	final int id_Target = 2;
	final int id_To = 3;
	final int id_Second = 4;
	final int id_Tick = 5;
	final int id_Interpolation = 6;
	
	boolean isParallel;
	int modeIndex = 0;
	int targetIndex = 0;
	int interpolationIndex = 0;
	float From; //初期位置記憶用
	float To;
	float targetValue;
	int tickTimeCount;
	int tickTimeTarget;

	@Override
	public void Init(TileEntityFerrisWheel tile) 
	{
		this.tile = tile;
	}

	@Override
	public Mode getMode() {
		return Mode.keyframe;
	}
	
	@Override
	public int ApiNum(){ return 7; }
	
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
		switch(apiIndex)
		{
		case id_Parallel : isParallel = !isParallel; break;
		case id_Mode : modeIndex = (modeIndex + 1) % modes.length; break;
		case id_Target : targetIndex = ( targetIndex + 1 ) % targets.length; break;
		case id_Interpolation : interpolationIndex = ( interpolationIndex + 1 ) % interpolations.length; break;
		}
		
		return new int[]{apiIndex};
	}
	
	@Override
	public String getValue(int apiIndex){
		switch(apiIndex)
		{
		case id_Parallel : return isParallel?"Enable":"Disable";
		case id_Mode : return modes[modeIndex];
		case id_Target : return targets[targetIndex];
		case id_To : return String.format("%.4f",To);
		case id_Tick : return ""+tickTimeTarget;
		case id_Second : return String.format("%.2f",tickTimeTarget/20.0);
		case id_Interpolation : return interpolations[interpolationIndex];
		}
		return "";
	}
	
	/**
	 * retval : Valueの更新が必要な項目のAPIIndexの配列
	 */
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			switch(apiIndex)
			{
			case id_Parallel : isParallel = "Enable".equals(value) ? true : false; break;
			case id_Mode : 
				modeIndex = Integer.parseInt((String) value); 
				modeIndex = MathHelper.clamp_int(modeIndex, 0, modes.length-1);
				break;
			case id_Target : 
				targetIndex = Integer.parseInt((String) value);
				targetIndex = MathHelper.clamp_int(targetIndex, 0, targets.length-1);
				break;
			case id_To : 
				To = (float)(Float.parseFloat((String)value));
				break;
			case id_Second : 
				tickTimeTarget = (int)((Double.parseDouble((String)value))*20); 
				if(tickTimeTarget < 1)tickTimeTarget = 1;
				return new int[]{id_Second,id_Tick};
			case id_Tick : 
				tickTimeTarget = (int)(Double.parseDouble((String)value)); 
				return new int[]{id_Tick,id_Second};
			case id_Interpolation : 
				interpolationIndex = Integer.parseInt((String) value); 
				interpolationIndex = MathHelper.clamp_int(interpolationIndex, 0, interpolations.length-1);
				break;
			}
			return new int[]{apiIndex};
		}catch(NumberFormatException e){
			return new int[]{};
		}
	}

	@Override
	public void start()
	{
		tickTimeCount = 0;
		From = getTargetValue();
		if(modeIndex == 0)/*set*/
			targetValue = To;
		else
			targetValue = To + From;
		canDispose = false;
		//frame calc
		KeyFrameValue.clear();
		for(int i=0; i<=tickTimeTarget; ++i){
			KeyFrameValue.add((targetValue - From)*calcInterpolation( (float)i / (float)tickTimeTarget ) + From);
		}
//		MFW_Logger.debugInfo("****K_");
	}
	
	private boolean canDispose = false;
	@Override
	public boolean CanDoNext()
	{
		return canDispose || isParallel;
	}
	
	ArrayList<Float> KeyFrameValue = new ArrayList<Float>();
	@Override
	public boolean run() {
		if(tickTimeCount >= tickTimeTarget)
		{
			if(canDispose==false)
			canDispose = true;
			return true;
		}
		tickTimeCount ++ ;
		float partial = KeyFrameValue.get(tickTimeCount);
		float prevpart = KeyFrameValue.get(tickTimeCount - 1);
		addTargetValue(partial - prevpart);
//		MFW_Logger.debugInfo("K_"+tickTimeCount+"_"+tickTimeTarget+"_"+partial+"_"+From);
		return canDispose;
	}
	
	private float calcInterpolation(float base)
	{
		switch(interpolationIndex)
		{
		case 0 : //linear
			return base;
		case 1 : // in sine
			return (float) - Math.cos(base * Math.PI * 0.5) + 1.0f;
		case 2 : // out sine
			return (float) Math.sin(base * Math.PI * 0.5);
		case 3 : // in out sine
			return (float) -Math.cos(base * Math.PI) * 0.5f + 0.5f;
		case 4 : // in bounce
			return (float) (Math.abs(Math.sin(Math.pow(base, 0.5)*3.5*Math.PI))*base);
		case 5 : // out bounce
			base = 1-base;
			return (float) (1f-(Math.abs(Math.sin(Math.pow(base, 0.5)*3.5*Math.PI))*base));
		case 6 : // in back
			return (float)( -(Math.cos(1.2*Math.PI*base)-1f)*(-0.7+1.25*base));
		case 7 : // out back
			base -= 1f;
			return (float)( 1+(Math.cos(1.2*Math.PI*base)-1f)*(-0.7-1.25*base));
		case 8 : // in spring
			return (float)(Math.sin(4.5*Math.PI*base)*base*base);
		case 9 : // out spring
			return (float)( 1 - Math.sin(4.5*Math.PI*(base-1f))*(Math.pow(base, 0.4)-1));
		}
		return base;
	}
	
	private float getTargetValue()
	{
		switch(targetIndex)
		{
		case 0 : return tile.rotation.get(); //"Rotation",
		case 1 : return tile.rotAccel; //"Accel",
		//case 2 : return 1f / tile.rotResist; //"Resist",
		case 2 : return tile.wheelSize.get(); //"Size",
		case 3 : return tile.rotMiscFloat1.get(); //"Amp",
		case 4 : return tile.rotMiscFloat2.get(); //"Phase",
		}
		return 0;
	}
	
	private void addTargetValue(float Value)
	{
		switch(targetIndex)
		{
		case 0/*rotate*/ : tile.rotation.add(Value); break;
		case 1/*Accel */ : tile.rotAccel += Value; break;
//		case 2/*Resist*/ : tile.rotResist += 1f/Value; break;
		case 2/*Size  */ : tile.wheelSize.add(Value); break;
		case 3/*Amp   */ : tile.rotMiscFloat1.add(Value); break;
		case 4/*Phase */ : tile.rotMiscFloat2.add(Value); break;
		}
	}
	
	@Override
	public void RSHandler(){}
	@Override
	public void NotifyHandler(){}

	@Override
	public String toString()
	{
		return (isParallel?"k":"K") +"x"
				+modeIndex +"x"
				+targetIndex +"x"
				+To +"x"
				+tickTimeTarget +"x"
				+interpolationIndex+";";
	}

	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_Parallel, (Character.isUpperCase(p[0].charAt(0))?"Disable":"Enable"));
		setValue(id_Mode, p[1]);
		setValue(id_Target, p[2]);
		setValue(id_To, p[3]);
		setValue(id_Tick, p[4]);
		setValue(id_Interpolation, p[5]);
	}
	
	@Override
	public String displayDescription()
	{
		return targets[targetIndex]
				+ (modeIndex==0 ? (" set " + To) : (" add "+(To<0?"":"+")+To))
				+ " in "+tickTimeTarget+" tick";
	}
}
