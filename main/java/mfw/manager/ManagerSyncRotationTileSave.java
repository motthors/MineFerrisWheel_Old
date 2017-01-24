package mfw.manager;

import mfw.tileEntity.TileEntityFerrisWheel;

public class ManagerSyncRotationTileSave {

	public static ManagerSyncRotationTileSave INSTANCE = new ManagerSyncRotationTileSave();
	TileEntityFerrisWheel rootTile;
	int TreeIndex = -1;
	
	private ManagerSyncRotationTileSave(){}
	
	public void saveTile(TileEntityFerrisWheel root, int TreeIndex)
	{
		rootTile = root;
		this.TreeIndex = TreeIndex;
//		MFW_Logger.debugInfo("save tile   idx:"+TreeIndex);
	}
	
	public TileEntityFerrisWheel getSaveTile()
	{
		return rootTile;
	}
	
	public int getSaveTileTreeIdx()
	{
		return TreeIndex;
	}
	
	public void reset()
	{
		rootTile = null;
		TreeIndex = -1;
	}
}
