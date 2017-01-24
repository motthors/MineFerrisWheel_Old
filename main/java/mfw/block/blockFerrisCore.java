package mfw.block;

import mfw._core.MFW_Core;
import mfw.item.itemBlockRemoteController;
import mfw.message.MFW_PacketHandler;
import mfw.message.MessageSyncRSPowerStC;
import mfw.tileEntity.TileEntityFerrisWheel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class blockFerrisCore extends BlockContainer {

	public blockFerrisCore()
	{
		super(Material.ground);
		this.setHardness(1.0f);
		this.setResistance(2000.0F);
		this.setLightOpacity(0);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setLightLevel(0.0F);
	}

	@Override
	public boolean isOpaqueCube() {return false;}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityFerrisWheel();
	}
	
	@Override
	public int getRenderType()
	{
		return MFW_Core.blockCoreRenderId;
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
	{
		meta = side;
		return side;
	}
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
		// 右クリックされたときにそれがコントローラーだったら情報保存してあげる
//		if(onBlockActivatedWithController(player,x,y,z))return true;
		if(player.getHeldItem()!=null && player.getHeldItem().getItem() instanceof itemBlockRemoteController)
		{
			if(world.isRemote)return true;
			// 関数の呼び出し元がリモコンブロックでなかった場合のみ登録、リモコンブロックからの呼び出しの場合は通常と同じくGUI開く
			StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
	        if (steArray.length > 3){
	        	if(steArray[2].getClassName().equals("mfw.block.blockFerrisRemoteController")==false){
	        		((itemBlockRemoteController)player.getHeldItem().getItem()).onBlockActivatedWithController(player,x,y,z);
	        		return true;
	        	}
	        }
		}
		TileEntityFerrisWheel tile = (TileEntityFerrisWheel) world.getTileEntity(x, y, z);
		if(player.isSneaking())tile.resetSelectedTile();
		
		//OPEN GUI
		if(!world.isRemote)
			player.openGui(MFW_Core.INSTANCE, MFW_Core.GUIID_FerrisCore, player.worldObj, x, y, z);
		
        return true;
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z)
	{
		TileEntityFerrisWheel tile = (TileEntityFerrisWheel) world.getTileEntity(x, y, z);
		if(tile!=null && tile.isLock)return false;
		return super.removedByPlayer(world, player, x, y, z);
	}

	//	private final Random random = new Random();
	public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_)
    {
		TileEntityFerrisWheel tile = (TileEntityFerrisWheel)world.getTileEntity(x, y, z);

        if (tile != null)
        {	
//        	// 中に入ってるアイテムを撒き散らす
        	tile.dropChildParts();
        	// 同期回転の後処理
        	tile.clearOwnFromChildren();
        	
            world.func_147453_f(x, y, z, block);
            tile.invalidate();
        }

        super.breakBlock(world, x, y, z, block, p_149749_6_);
    }
	
	
	////////////////////////////// RS入力関連//////////////////////////
	
	@Override
	public boolean canProvidePower() 
	{
		return false;
	}
	
	public void onBlockAdded(World world, int x, int y, int z)
    {
        if (world.isRemote)return;
    	boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z);
    	if (flag)
    	{
//    		world.setBlockMetadataWithNotify(x, y, z, 8^world.getBlockMetadata(x, y, z), 2);
        } 
    }

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) 
	{
		if(stack.hasTagCompound()==false)return;
		NBTTagCompound nbt = stack.getTagCompound();
		super.onBlockPlacedBy(world, x, y, z, player, stack);
		TileEntityFerrisWheel tile = (TileEntityFerrisWheel) world.getTileEntity(x, y, z);
		tile.setRSPower(world.getStrongestIndirectPower(x, y, z));
		if(nbt.getBoolean("fromPickBlockFlag"))
		{ //コアをピックして取得したアイテムスタック
			
			tile.my_readFromNBT(nbt, 0);
			tile.readRootWheelFromNBT(nbt);
			tile.readChildFromNBT(nbt,1);
			tile.meta = world.getBlockMetadata(x, y, z);
			tile.resetRotAxis();
		}
		else
		{ //工作台で作成したアイテムスタック
			tile.saveRootTag(nbt);
			tile.constructFromTag(nbt, world.getBlockMetadata(x, y, z), true);
		}
	}
	
	// 赤石入力用
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
//        if (!world.isRemote)
        {
            boolean flag = world.isBlockIndirectlyGettingPowered(x, y, z);
            
            if (flag || block.canProvidePower())
            {
            	TileEntityFerrisWheel tile = (TileEntityFerrisWheel)world.getTileEntity(x, y, z);
            	if(tile==null)return;
            	int rs = world.getStrongestIndirectPower(x, y, z);
        		tile.setRSPower(rs);
        		MessageSyncRSPowerStC packet = new MessageSyncRSPowerStC(x, y, z, rs);
        		MFW_PacketHandler.INSTANCE.sendToAll(packet);
//        		MFW_Logger.debugInfo(""+world.getStrongestIndirectPower(x, y, z));
            }
        }
    }
    
    
    //ピック
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
    	if(player.capabilities.isCreativeMode==false)return null;
    	
		ItemStack stack = super.getPickBlock(target, world, x, y, z, player);
    	TileEntityFerrisWheel tile = (TileEntityFerrisWheel) world.getTileEntity(x, y, z);
    	NBTTagCompound nbt = new NBTTagCompound();
    	tile.my_writeToNBT(nbt,0);
    	tile.writeRootWheelToNBT(nbt);
    	tile.writeChildToNBT(nbt,1);
    	nbt.setBoolean("fromPickBlockFlag", true);
    	stack.setTagCompound(nbt);
    	return stack;
    }
}
