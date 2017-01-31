package mfw.storyboard.programpanel;

import mfw.tileEntity.TileEntityFerrisWheel;

public interface IProgramPanel {

	public enum Mode{
		set,	//���
		timer,	//���ԑҋ@
		linear,	//���`�l�ϊ�
		loop,	//�J��Ԃ�
		wait,	//�t���O�ҋ@
		notify, //�ʒm
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
	public boolean run(); //retval : true=�������s
	
}

