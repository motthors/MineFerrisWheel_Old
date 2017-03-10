package mfw.asm;

import java.util.HashMap;
import java.util.Map;

import mfw.asm.transpack.TransPack;
import mfw.asm.transpack.tp_EntityRenderer;
import mfw.asm.transpack.tp_SoundHandler;

public class transformerMap {
	
	private static transformerMap INSTANCE;
	private Map<String, TransPack> transmap = new HashMap<String, TransPack>();
	
	public static transformerMap getInstance()
	{
		if(INSTANCE==null) INSTANCE = new transformerMap();
		return INSTANCE;
	}
	
	public TransPack getTransPack(String transformedName)
	{
		if(transmap.containsKey(transformedName))
		{
			TransPack ret = transmap.get(transformedName);
			return ret;
		}
		else 
		{
			return null;
		}
	}
	
	private transformerMap()
	{
		//add(new tp_test());
		//add(new tp_DimensionManager());
//		add(new tp_GuiIngameForge());
		
		
		add(new tp_SoundHandler());
		add(new tp_EntityRenderer());

	}
	
	private void add(TransPack tp)
	{
		String[] list = tp.getTargetClassName();
		for(String str : list)
		{
			transmap.put(str, tp);
		}
	}
	
	
	
}
