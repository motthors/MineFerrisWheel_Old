package mfw.storyboard.programpanel;

import java.nio.ByteBuffer;
import java.util.List;

import mfw._core.MFW_Core;
import mfw._core.MFW_Logger;
import mfw.sound.SoundManager;
import mfw.storyboard.programpanel.IProgramPanel.DataPack;
import mfw.storyboard.programpanel.IProgramPanel.Type;
import mfw.tileEntity.TileEntityFerrisWheel;
import mfw.wrapper.I_FerrisPart;

public class SoundPanel implements IProgramPanel {
	
	private static String[] modes = {
			"Once",
			"Continue",
	};
	
	private static DataPack[] datapacks = {
			new DataPack(Type.change, "Mode"),
			new DataPack(Type.change, "▲"),
			new DataPack(Type.change, "▼"),
	};
	private TileEntityFerrisWheel tile;
	
	final int id_Mode = 0;
	final int id_SoundIdx = 1;

	private int modeIndex = 0;
	public  int soundIndex = 0;

	@Override
	public void Init(TileEntityFerrisWheel tile) 
	{
		this.tile = tile;
	}

	@Override
	public Mode getMode() {
		return Mode.sound;
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
//		switch (apiIndex) {
//		case id_Mode:
			return datapacks[apiIndex].description;
//		case id_SoundIdx:
//			return "filename";
//		case id_SoundIdx+1:
//			return SoundManager.getSoundDomain(soundIndex);
//		default:
//			return "";
//		}		
	}
	
	@Override
	public int[] Clicked(int apiIndex)
	{
		switch(apiIndex){
		case id_Mode : 
			modeIndex = (modeIndex + 1) % modes.length;
			break;
		case id_SoundIdx :
			soundIndex = (soundIndex + 1) % SoundManager.sounds.size();
			return new int[]{apiIndex,apiIndex+1};
		case id_SoundIdx+1 :
			soundIndex--;
			if(soundIndex < 0) soundIndex = SoundManager.sounds.size() - 1;
			return new int[]{apiIndex,apiIndex-1};
		}
		return new int[]{apiIndex};
	}
	
	@Override
	public String getValue(int apiIndex){
		switch(apiIndex){
		case id_Mode : 
			return modes[modeIndex];
		case id_SoundIdx :
		case id_SoundIdx+1 :
//			return soundIndex+"";
			return SoundManager.sounds.get(soundIndex);
		default : return "";
		}
	}
	
	/**
	 * retval : Valueの更新が必要な項目のAPIIndexの配列
	 */
	@Override
	public int[] setValue(int apiIndex, Object value) {
		try{
			switch(apiIndex){
			case id_Mode : 
				modeIndex = Integer.parseInt((String) value);
				break;
			case id_SoundIdx :
				soundIndex = Integer.parseInt((String) value);
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
	}
	
	@Override
	public boolean CanDoNext()
	{
		return true;
	}
	
	@Override
	public boolean run() {
		switch (modeIndex) {
		case 0: //once
			String domain = MFW_Core.MODID+":"+SoundManager.getSoundDomain(soundIndex);
			tile.getWorldObj().playSoundEffect(tile.xCoord+0.5, tile.yCoord+0.5, tile.zCoord+0.5, domain, 1.0F, 0.9F);
			break;
		case 1: // continue
			tile.SetSoundIndex(soundIndex);
			break;
		default:
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
		return "Mx"+modeIndex+"x"+soundIndex+";";
	}

	@Override
	public void fromString(String source)
	{
		String[] p = source.split("x");
		setValue(id_Mode, p[1]);
		setValue(id_SoundIdx, p[2]);
	}
	
	@Override
	public String displayDescription()
	{
		switch (modeIndex) {
		case 0: //once
			return "Sound "+"\" "+SoundManager.getSoundDomain(soundIndex)+" \"";
		case 1: // continue
			return "Frame.Sound <= "+"\" "+SoundManager.getSoundDomain(soundIndex)+" \"";
		default:
			return "error";
		}
	}
}
