package mfw.storyboard.programpanel;

import java.util.List;

import mfw.tileEntity.TileEntityFerrisWheel;

public interface IProgramPanel {

	public enum Mode{
		set,		//代入
		timer,		//時間待機
		keyframe,	//キーブレーム補間
		loop,		//繰り返し
		wait,		//フラグ待機
		notify,		//通知
		sound,
		loopend,
		;
		public static Mode getType(final String src) {
			Mode[] Modes = Mode.values();
	        for (Mode m : Modes) {if (m.toString() == src) { return m;}}
	        return null;
	    }
	}
	public enum Type{
		change,
		inputvalue,
		soundselector,
	}
	public enum Notify{
		redstone,
		notify,
	}
	public class DataPack{
		public DataPack(Type t, String s){type=t; description=s;}
		Type type;
		String description;
	}
	//func
	//isAsync
	
	public void Init(TileEntityFerrisWheel tile);
	public int ApiNum();
	public Mode getMode();
	public Type getType(int apiIndex);
	public String getDescription(int apiIndex);
	public void insertSubPanelToList(List<IProgramPanel> inout_panel);
	public int[] Clicked(int apiIndex);
	public String getValue(int apiIndex);
	public int[] setValue(int apiIndex, Object value);
	public void start();
	public boolean CanDoNext();
	public boolean run(); //retval : true=終了
	public void RSHandler();
	public void NotifyHandler();
	public void fromString(String source);
	public String displayDescription();
}

