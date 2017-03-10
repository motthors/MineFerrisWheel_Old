package mfw.storyboard.programpanel;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

import mfw._core.MFW_Logger;
import mfw.storyboard.StoryBoardManager;
import mfw.tileEntity.TileEntityFerrisWheel;

public class LoopPanel extends StoryBoardManager implements IProgramPanel {
	
	private static DataPack[] datapacks = {
			new DataPack(Type.inputvalue, "LoopNum"),
	};
	private TileEntityFerrisWheel tile;
	
	final int id_loopNum = 0;
	final int id_ChildrenDataSource = 1;
	
	int LoopNum;
	int LoopCount;
	
	public LoopPanel()
	{
		super(null);
	}

	@Override
	public void Init(TileEntityFerrisWheel tile) 
	{
		this.tile = tile;
		LoopNum = 1;
		super.Init(tile);
	}

	@Override
	public Mode getMode() {
		return Mode.loop;
	}
	
	@Override
	public int ApiNum(){ return 1; }
	
	@Override
	public void insertSubPanelToList(List<IProgramPanel> inout_panel)
	{
		inout_panel.addAll(getPanelList());
		inout_panel.add(new LoopEndPanel(this));
	}
	
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
		case id_loopNum : return ""+LoopNum;
		}
		return "";
	}
	
	/**
	 * retval : Valueの更新が必要な項目のAPIIndexの配列
	 */
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			int temp;
			switch(apiIndex)
			{
			case id_loopNum : 
				temp = (Integer.parseInt((String)value)); 
				LoopNum = (temp<1) ? 1 : temp;
				break;
			case id_ChildrenDataSource : createFromSerialCode((String)value); break;
			}
			return new int[]{apiIndex};
		}catch(NumberFormatException e){
			return new int[]{};
		}
	}

	@Override
	public void start()
	{
		LoopCount = 0;
		super.Start();
	}
	
	@Override
	public boolean CanDoNext()
	{
		return false;
	}
	
	@Override
	public boolean run() 
	{
		if(LoopCount < LoopNum)
		{
			super.RunAnimation();
			if(runningPanelList.size() == 0){
				LoopCount++;
				if(LoopCount == LoopNum)return true;
				super.Start();
			}
		}
		return (LoopCount >= LoopNum);
	}		
	
	public LoopEndPanel createLoopEndPanel()
	{
		return new LoopEndPanel(this);
	}

	@Override
	public void RSHandler(){}
	@Override
	public void NotifyHandler(){}
	
	@Override
	public String toString()
	{
		String subcode = "";
		for(IProgramPanel panel : PanelList)subcode += panel.toString();
		return "Lx"+LoopNum+"x["+subcode+"];";
	}
	
	@Override
	public void fromString(String source)
	{
		int St = source.indexOf("[");
		int Ed= source.indexOf("]");
		String childrenSource = source.substring(St+1, Ed);
		setValue(id_ChildrenDataSource, childrenSource);
		String[] p = source.split("x");
		setValue(id_loopNum, p[1]);
	}
	
	@Override
	public String getSerialCode()
	{
		String serial = "";
		for(IProgramPanel panel : PanelList){serial += panel.toString();}
		//MFW_Logger.debugInfo("create serial : "+serial);
		return serial;
	}
	
	@Override
	public boolean createFromSerialCode(String source)
	{
		ArrayList<IProgramPanel> keep = (ArrayList<IProgramPanel>) PanelList.clone();
		try{
			if(savedSerialCode.equals(source))return false;
			this.clear();
			if(source.equals(""))return true;
			//MFW_Logger.debugInfo("recieve serial : "+source);
			source.replace("\r\n", "");
			source.replace("\n", "");
			source.replace(" ", "");
			int start = 0;
			
			while(true)
			{
				if("".equals(source))break;
				char id = source.charAt(0);
				int end = source.indexOf(";");
				if(id == 'L'){
					end = findLoopEndCode(source);
				}
				if(end < 0)return false;
				String sub = source.substring(start, end);
				source = source.substring(end+1);
				
				//decode
				{
					IProgramPanel panel = createPanel_forSerial(id);
					panel.Init(this.tile);
					panel.fromString(sub);
					PanelList.add(panel); //MFW_Logger.debugInfo("add "+panel);
				}
			}

			savedSerialCode = getSerialCode();
		}catch(Exception e){
			PanelList = keep;
			return false;
		}
		return true;
	}
	
	@Override
	public String displayDescription()
	{
		return "Loop "+LoopNum+" time(s)";
	}
	
	public class LoopEndPanel implements IProgramPanel{

		public LoopPanel parent;
		public LoopEndPanel(LoopPanel parent)
		{
			this.parent = parent;
		}
		
		@Override
		public int ApiNum() {
			return 0;
		}

		@Override
		public Mode getMode() {
			return Mode.loopend;
		}

		@Override
		public Type getType(int apiIndex) {
			return null;
		}

		@Override
		public String getDescription(int apiIndex) {
			return null;
		}

		@Override
		public void Init(TileEntityFerrisWheel tile) {
		}

		@Override
		public void insertSubPanelToList(List<IProgramPanel> inout_panel) {
		}

		@Override
		public int[] Clicked(int apiIndex) {
			return null;
		}

		@Override
		public String getValue(int apiIndex) {
			return null;
		}

		@Override
		public int[] setValue(int apiIndex, Object value) {
			return new int[]{};
		}

		@Override
		public void start() {
		}

		@Override
		public boolean CanDoNext() {
			return true;
		}

		@Override
		public boolean run() {
			return true;
		}
		
		@Override
		public void RSHandler(){}
		@Override
		public void NotifyHandler(){}
		
		@Override
		public String toString()
		{
			return "";
		}
		
		@Override
		public void fromString(String source)
		{
		}
		
		@Override
		public String displayDescription()
		{
			return "";
		}
	}
}
