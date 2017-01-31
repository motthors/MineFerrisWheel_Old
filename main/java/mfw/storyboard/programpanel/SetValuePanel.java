package mfw.storyboard.programpanel;

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
			new DataPack(Type.change, "Target", targets[0]),
			new DataPack(Type.inputvalue, "Value", 0.0),
	};
	private TileEntityFerrisWheel tile;
	private int rotindex = 0;

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
	public Type getType(int apiIndex){
		return datapacks[apiIndex].type;
	}
	
	@Override
	public String getDescription(int apiIndex) {
		return datapacks[apiIndex].description;
	}
	
	@Override
	public String getValue(int apiIndex){
		return datapacks[apiIndex].value.toString();
	}
	
	@Override
	public int[] setValue(int apiIndex, Object value) {
		rotindex = ( rotindex + 1 ) % targets.length;
		datapacks[apiIndex].value = (apiIndex==0) ? targets[rotindex] : value;
		return new int[]{apiIndex};
	}
	
	@Override
	public void start()
	{
		//nothing
	}
	
	@Override
	public boolean run() {
		switch(rotindex)
		{
		case 0/*rotate*/ : tile.rotation = (Float)datapacks[0].value; break;
		case 1/*Accel */ : tile.rotAccel = (Float)datapacks[0].value; break;
		case 2/*Resist*/ : tile.rotResist = (Float)datapacks[0].value; break;
		case 3/*Size  */ : tile.wheelSize = (Float)datapacks[0].value; break;
		case 4/*Amp   */ : tile.rotMiscFloat1 = (Float)datapacks[0].value; break;
		case 5/*Phase */ : tile.rotMiscFloat2 = (Float)datapacks[0].value; break;
		}
		return true;
	}

	
}
