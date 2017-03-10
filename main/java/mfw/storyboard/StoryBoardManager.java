package mfw.storyboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import mfw._core.MFW_Logger;
import mfw.storyboard.programpanel.IProgramPanel;
import mfw.storyboard.programpanel.IProgramPanel.Mode;
import mfw.storyboard.programpanel.KeyFramePanel;
import mfw.storyboard.programpanel.LoopPanel;
import mfw.storyboard.programpanel.NotifyPanel;
import mfw.storyboard.programpanel.SetValuePanel;
import mfw.storyboard.programpanel.SoundPanel;
import mfw.storyboard.programpanel.TimerPanel;
import mfw.storyboard.programpanel.WaitPanel;
import mfw.tileEntity.TileEntityFerrisWheel;

public class StoryBoardManager {

	//texture array
	
	TileEntityFerrisWheel tile;
	protected String savedSerialCode;
	public WaitPanel startWaitPanel = new WaitPanel();
	
	//gui
	
	protected ArrayList<IProgramPanel> PanelList;
	protected Iterator<IProgramPanel> nowTargetPanelItr;
	protected LinkedList<IProgramPanel> runningPanelList;
	protected IProgramPanel nowTargetPanel;
	
	public StoryBoardManager(TileEntityFerrisWheel tile)
	{
		Init(tile);
	}
	
	public void Init(TileEntityFerrisWheel tile)
	{
		this.tile = tile;
		savedSerialCode = "";
		PanelList = new ArrayList<IProgramPanel>();
		runningPanelList = new LinkedList<IProgramPanel>();
		
		clear();
		startWaitPanel.Init(tile);
	}
	
	protected boolean isInLoop = false;
	protected LoopPanel looppanel = null;
	public void addPanel(IProgramPanel panel)
	{
		if(isInLoop){
			if(panel.getMode()==IProgramPanel.Mode.loopend){
				isInLoop = false;
				return;
			}
			looppanel.addPanel(panel);
			return;
		}
		if(panel.getMode()==IProgramPanel.Mode.loop){
			isInLoop = true;
			looppanel = (LoopPanel) panel;
		}
		PanelList.add(panel);
		return;
	}
	
	public void clear()
	{
		isInLoop = false;
		nowTargetPanel = null;
		PanelList.clear();
		runningPanelList.clear();
		startWaitPanel.start();
	}
	
	public ArrayList<IProgramPanel> getPanelList()
	{
		ArrayList<IProgramPanel> result = new ArrayList<IProgramPanel>();
		for(IProgramPanel panel : PanelList)
		{
			result.add(panel);
			panel.insertSubPanelToList(result);
		}
		return result;
	}
	
	public void SetNextPanel()
	{
		boolean candonext = false;
		do
		{
			if(nowTargetPanelItr.hasNext())
			{
				nowTargetPanel = nowTargetPanelItr.next();
				nowTargetPanel.start();
				candonext = nowTargetPanel.CanDoNext();
				runningPanelList.add(nowTargetPanel);//		MFW_Logger.debugInfo("add "+nowTargetPanel);
			}else{
				nowTargetPanel = null;
				candonext = false;
			}
		}while(candonext);
	}
	
	public void OnRSEnable()
	{
		if(runningPanelList.size()==0)
		{
			startWaitPanel.RSHandler();
			if(startWaitPanel.run())
			{
				Start();
			}
		}
		else
		{
			for(IProgramPanel panel : runningPanelList)panel.RSHandler();
		}
	}
	
	public void OnNotify()
	{
		if(runningPanelList.size()==0)
		{
			startWaitPanel.NotifyHandler();
			if(startWaitPanel.run())
			{
				Start();
			}
		}
		else
		{
			for(IProgramPanel panel : runningPanelList)panel.NotifyHandler();
		}
	}
	
	public void Start()
	{
		if(nowTargetPanel == null)
		{
			nowTargetPanelItr = PanelList.iterator();
			SetNextPanel();
		}
	}
	
	public void RunAnimation()
	{
		if(runningPanelList.size() > 0)
		{
			//MFW_Logger.debugInfo("###############StartRun###############..."+runningPanelList.size());
			for(int i=0; i<runningPanelList.size(); ++i)
			{
				IProgramPanel panel = runningPanelList.get(i);
				boolean isFinished = panel.run();	//MFW_Logger.debugInfo("do "+panel);
				if(isFinished && panel.equals(nowTargetPanel))
				{
					SetNextPanel();		//MFW_Logger.debugInfo("end "+panel+" and add next ... ... nowtarget="+nowTargetPanel);
				}
				if(isFinished)
				{
					runningPanelList.remove(panel);	//MFW_Logger.debugInfo("remove "+panel);
					i--;
				}
			}
		}else startWaitPanel.start(); // reset
	}
	
	public void stop()
	{
		nowTargetPanel = null;
		runningPanelList.clear();
	}
	
	public String getSerialCode()
	{
		String serial = "";
		serial += startWaitPanel.toString();
		for(IProgramPanel panel : PanelList)
		{
			serial += panel.toString();
		}
		//MFW_Logger.debugInfo("create serial : "+serial);
		return serial;
	}
	
	public boolean createFromSerialCode(String source)
	{
		ArrayList<IProgramPanel> keep = (ArrayList<IProgramPanel>) PanelList.clone();
		try{
			if(savedSerialCode.equals(source))return false;
			this.clear();
			if(source.equals(""))return true;
			//MFW_Logger.debugInfo("recieve serial : " + tile.WheelName + source);
			source.replace("\r\n", "");
			source.replace("\n", "");
			source.replace(" ", "");
			int start = 0;
			
			//for startwaitpanel
			{
				char id = source.charAt(0);
				if(id != 'W')new Exception();
				int end = source.indexOf(";");
				String sub = source.substring(start, end);
				source = source.substring(end+1);
				//decode
				{
					IProgramPanel panel = this.startWaitPanel;
					panel.Init(this.tile);
					panel.fromString(sub); //MFW_Logger.debugInfo("set startwait "+panel);
				}
			}
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
	
	protected int findLoopEndCode(String code)
	{
		//Å‰‚ÌŠ‡ŒÊ‚Ü‚ÅˆÚ“®
		int end = code.length();
		int i = code.indexOf("[");
		//‚»‚ÌŽŸ‚©‚ç•Â‚¶‚éŠ‡ŒÊ‚ðŒ©‚Â‚¯‚é‚Ü‚Å’Tõ@‚³‚ç‚ÉŠ‡ŒÊ‚ÅIdx+1A•Â‚¶Š‡ŒÊ‚Å-1A‚O‚Ì‚Æ‚«‚É•Â‚¶Š‡ŒÊ‚ÅI—¹
		i++;
		int idx = 0;
		while(true){
			if(code.charAt(i) == '[')idx++;
			if(code.charAt(i) == ']'){
				if(idx==0)return i+1;
				else idx--;
			}
			i++;
			if(i>=end)return -1;
		}
	}
	
	public static IProgramPanel createPanel_forSerial(char mode)
	{
		switch(mode){
		case 'S' : return createPanel(Mode.set);
		case 'T' : return createPanel(Mode.timer);
		case 'L' : return createPanel(Mode.loop);
		case 'k' : 
		case 'K' : return createPanel(Mode.keyframe);
		case 'W' : return createPanel(Mode.wait);
		case 'N' : return createPanel(Mode.notify);
		case 'M' : return createPanel(Mode.sound);
		}
		return createPanel(Mode.set);
	}
	public static IProgramPanel createPanel(String mode)
	{
		return createPanel(Mode.getType(mode));
	}
	public static IProgramPanel createPanel(IProgramPanel.Mode mode)
	{
		switch(mode){
		case set : return new SetValuePanel();
		case timer : return new TimerPanel();
		case loop : return new LoopPanel();
		case keyframe : return new KeyFramePanel();
		case wait : return new WaitPanel();
		case notify : return new NotifyPanel();
		case sound : return new SoundPanel();
		case loopend : return null;
		}
		return new SetValuePanel();
	}

}
