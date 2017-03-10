package mfw.sound;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import mfw._core.MFW_Core;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundListSerializer;
import net.minecraft.util.ResourceLocation;

public class SoundManager {

	
	public static ArrayList<String> sounds = new ArrayList<String>();
	
	public static void JsonUpdate()
	{
		sounds.add("(none)");
		
		String newjson = "";
		String current = new File(".").getAbsoluteFile().getParent();
		//MFW_Logger.debugInfo("corrent path : "+current);
//		File file = new File(current+"/mods");
//		String[] mfwfolder = file.list(new FilenameFilter(){public boolean accept(File dir, String name){return name.contains("MineFerrisWheel");}});
//		if(mfwfolder.length == 0)return;
		File file = new File(current+"/MFWSounds/sounds.json");
//		if(file.exists() == false) return;
		
		//file exist
		
		//sound file explorer
		File folder = new File("./MFWSounds");
		if( folder.exists() == false)
		{
			folder.mkdirs();
			return;
		}
		
		newjson += "{\n";
		
		String format = "\"%s\":{\n"
							+"\"category\":\"master\","
							+"\"sounds\":[\"%s\"]\n}";
		
		newjson += String.format(format, "complete", "items/complete");
		
		
		String[] filelist = folder.list();
		for(String filename : filelist)
		{
			if(filename.contains(".ogg"))
			{
				filename = filename.replace(".ogg", "");
				newjson += ",\n" + String.format(format, filename, "../../../../../MFWSounds/" + filename);
				sounds.add(filename);
			}
		}
		
		newjson += "\n}";
		
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			pw.println(newjson);
		}catch(Exception e){
			
		}finally{
			if(pw!=null)pw.close();
		}
	}
	
	
	public static String getSoundDomain(int idx)
	{
		if(idx >= sounds.size())idx = 0;
		if(idx < 0)idx = 0;
		return sounds.get(idx);
	}
	

	private static final Gson gson = (new GsonBuilder()).registerTypeAdapter(SoundList.class, new SoundListSerializer()).create();
    public static void AddExternalSoundLoad(SoundHandler soundhandler)
	{
		try {
			Map map = (Map)gson.fromJson(new InputStreamReader(new FileInputStream("./MFWSounds/sounds.json")), paramtype);
            Iterator iterator2 = map.entrySet().iterator();

            while (iterator2.hasNext())
            {
                Entry entry = (Entry)iterator2.next();
//                this.loadSoundResource(new ResourceLocation(s, (String)entry.getKey()), (SoundList)entry.getValue());
                Method m = soundhandler.getClass().getDeclaredMethod("loadSoundResource", ResourceLocation.class, SoundList.class);
    			m.setAccessible(true);
    			m.invoke(soundhandler, new ResourceLocation(MFW_Core.MODID, (String)entry.getKey()), (SoundList)entry.getValue());
            }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private static final ParameterizedType paramtype = new ParameterizedType()
    {
        private static final String __OBFID = "CL_00001148";
        public Type[] getActualTypeArguments()
        {
            return new Type[] {String.class, SoundList.class};
        }
        public Type getRawType()
        {
            return Map.class;
        }
        public Type getOwnerType()
        {
            return null;
        }
    };
}
