package mfw.storyboard.programpanel;

import java.nio.ByteBuffer;
import java.util.List;

import mfw._core.MFW_Logger;
import mfw.storyboard.programpanel.IProgramPanel.DataPack;
import mfw.storyboard.programpanel.IProgramPanel.Type;
import mfw.tileEntity.TileEntityFerrisWheel;
import mfw.wrapper.I_FerrisPart;

public class NotifyPanel implements IProgramPanel {
	
	private static String[] targets = {
			"All",
			"Parent",
			"Children",
	};
	
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "Target"),
	};
	private TileEntityFerrisWheel tile;
	
	final int id_Target = 0;
	
	private int index = 0;

	@Override
	public void Init(TileEntityFerrisWheel tile) 
	{
		this.tile = tile;
	}

	@Override
	public Mode getMode() {
		return Mode.notify;
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
		index = (index + 1) % targets.length;
		return new int[]{apiIndex};
	}
	
	@Override
	public String getValue(int apiIndex){
		return targets[index];
	}
	
	/**
	 * retval : Valueの更新が必要な項目のAPIIndexの配列
	 */
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			index = Integer.parseInt((String) value);
			return new int[]{apiIndex};
		}catch(NumberFormatException e){
			return new int[]{};
		}
	}

	@Override
	public void start()
	{
	}
	
	@Override
	public boolean CanDoNext()
	{
		return false;
	}
	
	@Override
	public boolean run() {
		switch(index){
		case 0 : // all
			for(TileEntityFerrisWheel t : tile.createWheelList()){
				t.getStoryBoardManager().OnNotify();
			}
			break;
		case 1 : // parent
			TileEntityFerrisWheel parent = tile.getParentTile();
			if(parent!=null)parent.getStoryBoardManager().OnNotify();
			break;
		case 2 : // child
			for(I_FerrisPart t : tile.ArrayEntityParts){
				if(t==null)continue;
				if(t.isTile())
					((TileEntityFerrisWheel)t).getStoryBoardManager().OnNotify();
			}
			break;
		}
		return true;
	}

	@Override
	public void RSHandler(){}
	@Override
	public void NotifyHandler(){}
	
	@Override
	public String toString()
	{
		return "Nx"+index+";";
	}

	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_Target, p[1]);
	}
	
	@Override
	public String displayDescription()
	{
		return "notify to "+targets[index];
	}
}
