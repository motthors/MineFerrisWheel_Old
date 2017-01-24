package mfw.handler;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import mfw.manager.managerFrameLengthChange;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageFerrisMisc;
import mfw.wrapper.Wrap_TileEntityChangeLimitWithKey;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.tileentity.TileEntity;

public class KeyHandlerClient {
	
	public static final KeyBinding keyEditX2 = new KeyBinding("Change Length-", 80, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditX3 = new KeyBinding("Change Length+", 81, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditY4 = new KeyBinding("Change height-",	75, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditY7 = new KeyBinding("Change height+", 71, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditZ5 = new KeyBinding("Change Depth-", 	76, "mfw : Change Frame Lnegth");
	public static final KeyBinding keyEditZ9 = new KeyBinding("Change Depth+", 	73, "mfw : Change Frame Lnegth");
	public static final KeyBinding[] keyarray = new KeyBinding[]{keyEditX2,keyEditX3,keyEditY4,keyEditY7,keyEditZ5,keyEditZ9};
	
	public static void init() {
		for(KeyBinding k : keyarray)
			ClientRegistry.registerKeyBinding(k);

	}

	@SubscribeEvent
	public void keyDown(InputEvent.KeyInputEvent event) 
	{
		int value = 0;
		int idx = -1;
		for(int i=0;i<keyarray.length;++i)
		{
			if(keyarray[i].getIsKeyPressed())
			{
				idx = i;
				break;
			}
		}
		Wrap_TileEntityChangeLimitWithKey tile = managerFrameLengthChange.INSTANCE.getSaveTile();
		if(tile==null)return;
		TileEntity TILE = (TileEntity)tile;
		int FLAG = -1;
		switch(idx)
		{
		case 0 : case 1 : FLAG =  MessageFerrisMisc.ChangeFrameLengthWithKeyX; break;
		case 2 : case 3 : FLAG =  MessageFerrisMisc.ChangeFrameLengthWithKeyY; break;
		case 4 : case 5 : FLAG =  MessageFerrisMisc.ChangeFrameLengthWithKeyZ; break;
		default : return;
		}
		int a = idx%2;
		int b = Keyboard.isKeyDown(Keyboard.KEY_LMENU)?10:0;
		switch(a+b)
		{
		case 0 : value = 2;  break;
		case 10 : value = 1; break;
		case 1 : value = 3;  break;
		case 11 : value = 4; break;
		}
		MFW_PacketHandler.INSTANCE.sendToServer(new MessageFerrisMisc(TILE.xCoord, TILE.yCoord, TILE.zCoord, FLAG, value));
	}
}
