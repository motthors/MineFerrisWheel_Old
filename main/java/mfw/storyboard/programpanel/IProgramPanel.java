package mfw.storyboard.programpanel;

import mfw.tileEntity.TileEntityFerrisWheel;

public interface IProgramPanel {

	public enum Mode{
		set,	//代入
		timer,	//時間待機
		linear,	//線形値変換
		loop,	//繰り返し
		wait,	//フラグ待機
		notify, //通知
	}
	public enum Type{
		change,
		inputvalue,
	}
	public class DataPack{
		public DataPack(Type t, String s, Object init){type=t; description=s; value=init;}
		Type type;
		String description;
		Object value;
	}
	//func
	//isAsync
	
	public int ApiNum();
	public Type getType(int apiIndex);
	public String getDescription(int apiIndex);
	public Mode getMode();
	public void Init(TileEntityFerrisWheel tile);
	public String getValue(int apiIndex);
	public int[] setValue(int apiIndex, Object value);
	public void start();
	public boolean run(); //retval : true=次を実行
	
}

