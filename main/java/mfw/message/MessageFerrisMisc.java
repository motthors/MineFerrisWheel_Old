package mfw.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import mfw._core.MFW_Core;
import mfw.gui.container.ContainerFerrisCore;
import mfw.tileEntity.TileEntityFerrisBasketConstructor;
import mfw.tileEntity.TileEntityFerrisConstructor;
import mfw.tileEntity.TileEntityFerrisCutter;
import mfw.tileEntity.TileEntityFerrisWheel;
import mfw.tileEntity.TileEntityFileManager;
import mfw.wrapper.Wrap_TileEntityChangeLimitWithKey;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class MessageFerrisMisc implements IMessage, IMessageHandler<MessageFerrisMisc, IMessage>{

	
	public static final int GUIConstruct = 1;
	public static final int GUIAddLength = 2;
	public static final int GUIAddWidth  = 3;
	public static final int GUIAddCopyNum  = 4;
	public static final int GUIDrawCoreFlag = 5;
	public static final int GUIDrawEntityFlag = 6;
	public static final int GUIConstructSendTagArray = 7;
	
	public static final int GUIBConstruct = 50;
	public static final int GUIBAddOutLength = 51;
	public static final int GUIBAddOutWidth = 52;
	public static final int GUIBAddOutHeight = 53;
	public static final int GUIBConstructSendTagArray = 54;
	
	public static final int GUICoreAddSpeed = 100;
	public static final int GUICoreTurn = 101;
	public static final int GUICoreStop = 102;
	public static final int GUICoreResist = 103;
	public static final int GUICoreRotMisc1 = 104;
	public static final int GUICoreRotMisc2 = 105;
	
	public static final int GUICoreSizes = 200;
	public static final int GUICoreRot1 = 201;
	public static final int GUICoreRot2 = 202;
	public static final int GUICoreRotReset = 203;
	public static final int GUICoreSlotPage = 204;
	public static final int GUICoreSyncSaveParent = 205;
	public static final int GUICoreSyncRegistParent = 206;
	public static final int GUICoreSyncClear = 207;
	public static final int GUICoreRSFlagRotate = 208;
	public static final int GUICoreLock = 209;
	
	public static final int GUICoreSyncMode = 298;
	public static final int GUICoreModeChange = 299;
	
	public static final int GUIFileWrite = 300;
	public static final int GUIFileRead = 301;
	public static final int GUIFileRename = 302;
	public static final int GUIFileSendTagArray = 350; // 350~352 reserved
	
	public static final int GUIFerrisCutterX = 400;
	public static final int GUIFerrisCutterY = 401;
	public static final int GUIFerrisCutterZ = 402;
	
	public static final int ChangeFrameLengthWithKeyX = 500;
	public static final int ChangeFrameLengthWithKeyY = 501;
	public static final int ChangeFrameLengthWithKeyZ = 502;
	
	public static final int GUIOpenStoryBoard = 600;
	public static final int GUICloseStoryBoard = 601;
	public static final int GUIStoryBoardAddPanel = 602;
	
	public static final int GUIBackPartGUIOpen = 9999;
	public static final int GUISubPartGUIOpenBase = 10000; // +Éø
	
	// GUIÇ©ÇÁëóÇËÇΩÇ¢èÓïÒ
	public int x, y, z;
	public int FLAG;
	public int MiscInt;
	public byte[] arrayByte;
	
	public MessageFerrisMisc(){}
	
	public MessageFerrisMisc(int x, int y, int z, int flag, int imisc)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.FLAG = flag;
	    this.MiscInt = imisc; 
  	}
	public MessageFerrisMisc(int x, int y, int z, int flag, int imisc, byte[] abyte)
	{
		this(x,y,z,flag,imisc);
		this.arrayByte = abyte;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.FLAG);
		buf.writeInt(this.MiscInt);
		if(arrayByte!=null)buf.writeInt(arrayByte.length);
		else buf.writeInt(0);
		if(arrayByte!=null)buf.writeBytes(arrayByte);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
	    this.FLAG = buf.readInt();
	    this.MiscInt = buf.readInt();
	    int arraylen = buf.readInt();
	    if(arraylen<=0)return;
	    arrayByte = new byte[arraylen];
	    if(arrayByte.length>0)buf.readBytes(this.arrayByte);
    }
	
	@Override
    public IMessage onMessage(MessageFerrisMisc message, MessageContext ctx)
    {
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		TileEntity tile = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
		if (tile==null)return null;
		
		if(GUIBackPartGUIOpen == message.FLAG)
		{
			((TileEntityFerrisWheel) tile).getSelectedPartTile().onPushBack();
			player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, player.worldObj, message.x, message.y, message.z);
			return null;
		}
		if(GUISubPartGUIOpenBase <= message.FLAG)
		{
//			((TileEntityFerrisWheel) tile).getSelectedPartTile().onPushChildPart(message.FLAG - GUISubPartGUIOpenBase);
			((TileEntityFerrisWheel) tile).getSelectedPartTile().onPushChildPart((message.FLAG - GUISubPartGUIOpenBase)+25*message.MiscInt);
			player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, player.worldObj, message.x, message.y, message.z);
			return null;
		}
		switch(message.FLAG)
    	{
		////////////////////////////////GUI WheelConstruct ////////////////////////////////
    	case GUIAddLength:
			((TileEntityFerrisConstructor) tile).setFrameLength(message.MiscInt); 
			break;
    	case GUIAddWidth:
			((TileEntityFerrisConstructor) tile).setFrameWidth(message.MiscInt); 
			break;
    	case GUIDrawCoreFlag:
			((TileEntityFerrisConstructor) tile).toggleFlagDrawCore(); 
			break;
    	case GUIDrawEntityFlag:
    		((TileEntityFerrisConstructor) tile).toggleFlagDrawEntity(); 
			break;
    	case GUIAddCopyNum:
    		((TileEntityFerrisConstructor) tile).setCopyNum(message.MiscInt); 
    		break;
    	case GUIConstructSendTagArray :
//    		ctx.getServerHandler().playerEntity.worldObj.playSoundAtEntity(player, MFW_Core.MODID+":complete", 1.0F, 1.0F);
    		((TileEntityFerrisConstructor) tile).SetItemFerrisWheel(message.MiscInt, message.arrayByte);
    		break;
			//////////////////////////////// GUI BasketConstruct ////////////////////////////////
//    	case GUIBConstruct: Ç±ÇÍÇ‡Ç±Ç»Ç¢
//    		if(!((TileEntityFerrisBasketConstructor) tile).isExistBasket(player))break;
//			player = ctx.getServerHandler().playerEntity;
//			if(message.arrayByte!=null)((TileEntityFerrisBasketConstructor) tile).wheelName = new String(message.arrayByte);
//			((TileEntityFerrisBasketConstructor) tile).startConstructBasket(player);
//			break;
    	case GUIBAddOutLength:
			((TileEntityFerrisBasketConstructor) tile).setFrameLength(message.MiscInt); 
			break;
    	case GUIBAddOutWidth:
			((TileEntityFerrisBasketConstructor) tile).setFrameWidth(message.MiscInt); 
			break;
    	case GUIBAddOutHeight:
			((TileEntityFerrisBasketConstructor) tile).setFrameHeight(message.MiscInt); 
			break;
    	case GUIBConstructSendTagArray :
    		((TileEntityFerrisBasketConstructor) tile).SetItemFerrisBasket(message.arrayByte, message.MiscInt);
    		break;
			//////////////////////////////// GUI FileManager ////////////////////////////////
    	case GUIFileRename:
    		((TileEntityFileManager) tile).ReNameItemStack(new String(message.arrayByte));
			return null;
    	case GUIFileSendTagArray: return null;
    	case GUIFileSendTagArray+1:
    		((TileEntityFileManager) tile).FileRead_server(message.arrayByte, message.MiscInt, player, 1);
    		break;
    	case GUIFileSendTagArray+2:
    		((TileEntityFileManager) tile).FileRead_server(message.arrayByte, message.MiscInt, player, 2);
			break;
			
			//////////////////////////////// GUI core ////////////////////////////////
    	case GUICoreAddSpeed:
			((TileEntityFerrisWheel) tile).getSelectedPartTile().setSpeed(message.MiscInt); 
			break;
    	case GUICoreTurn:
			((TileEntityFerrisWheel) tile).getSelectedPartTile().turnSpeed(); 
			break;
    	case GUICoreResist:
			((TileEntityFerrisWheel) tile).getSelectedPartTile().setResist(message.MiscInt); 
			break;
    	case GUICoreRotMisc1 : 
    		((TileEntityFerrisWheel) tile).getSelectedPartTile().setRotMisc(message.MiscInt, 1); 
			break;
    	case GUICoreRotMisc2 : 
    		((TileEntityFerrisWheel) tile).getSelectedPartTile().setRotMisc(message.MiscInt, 2); 
			break;
    	case GUICoreSizes:
    		((TileEntityFerrisWheel) tile).getSelectedPartTile().setSize(message.MiscInt); 
			break;
    	case GUICoreRot1:
    	case GUICoreRot2:
    		((TileEntityFerrisWheel) tile).getSelectedPartTile().setRot(message.MiscInt, message.FLAG); 
			break;
    	case GUICoreRotReset:
    		((TileEntityFerrisWheel) tile).getSelectedPartTile().resetRot();
    		break;
    	case GUICoreSlotPage:
    		((ContainerFerrisCore)player.openContainer).changePage(message.MiscInt);
    		break;
    	case GUICoreSyncClear:
    		((TileEntityFerrisWheel)tile).getSelectedPartTile().clearSyncParent();
    		return null;
    	case GUICoreLock :
    		((TileEntityFerrisWheel)tile).getRootParent().toggleLock();
    		return null;
    		
    	case GUIOpenStoryBoard:
    		player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisStoryBoard, player.worldObj, message.x, message.y, message.z);
			return null;
    	case GUICloseStoryBoard :
    		player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, player.worldObj, message.x, message.y, message.z);
			return null;
			
    	case GUICoreStop:
    		((TileEntityFerrisWheel) tile).getSelectedPartTile().toggleStopFlag();
			break;	
    	case GUICoreModeChange : 
    		((TileEntityFerrisWheel)tile).getSelectedPartTile().changeRotFlag();
    		break;
    	case GUICoreSyncMode :
    		((TileEntityFerrisWheel)tile).getSelectedPartTile().changeRotFlag_Sync();
    		break;
    	case GUICoreRSFlagRotate :
    		((TileEntityFerrisWheel)tile).getSelectedPartTile().rotateRSFlag();
    		break;
    		////////////////////////////////////GUI Cutter/////////////////////////////
    		
    	case GUIFerrisCutterX:
    	case GUIFerrisCutterY:
    	case GUIFerrisCutterZ:
    		((TileEntityFerrisCutter)tile).setFrame(message.MiscInt, message.FLAG);
    		break;
    		
      	case ChangeFrameLengthWithKeyX :
      		((Wrap_TileEntityChangeLimitWithKey)tile).setFrameX(message.MiscInt);
      		break;
      	case ChangeFrameLengthWithKeyY :
    		((Wrap_TileEntityChangeLimitWithKey)tile).setFrameY(message.MiscInt);
      		break;
      	case ChangeFrameLengthWithKeyZ :
    		((Wrap_TileEntityChangeLimitWithKey)tile).setFrameZ(message.MiscInt);
      		break;
    	}
		player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
        return null;
    }
    
}
