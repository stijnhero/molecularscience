package molecularscience.tileentity;

import molecularscience.RegisterBlocksItems;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityHeatConductant extends TileEntity{

	double Temperature = 21;
	
	public int color = 0x555555;

	@Override
   	public void writeToNBT(NBTTagCompound nbt)
   	{
   		super.writeToNBT(nbt);
      	nbt.setDouble("Temperature", Temperature);
      	nbt.setInteger("Color", color);
   	}

   	@Override
   	public void readFromNBT(NBTTagCompound nbt)
   	{
   		super.readFromNBT(nbt);
      	this.Temperature = nbt.getDouble("Temperature");
      	this.color = nbt.getInteger("Color");
   	}
   	
   	@Override
   	public boolean canUpdate(){
   		return true;
   	}
   	
   	public int getColor(){
   		int intcolor = 0;
   		int green = (int) ((64.0 / 700.0) * this.Temperature);
   		int red = (int) ((255.0 / 700.0) * this.Temperature);
   		return ((green*256) + (red*65536));
   	}
   	
   	@Override
   	public void updateEntity() {
   		checkblocks(worldObj.getBlock(xCoord+1, yCoord, zCoord).getLocalizedName(), "1 0 0");
   		checkblocks(worldObj.getBlock(xCoord-1, yCoord, zCoord).getLocalizedName(), "-1 0 0");
   		checkblocks(worldObj.getBlock(xCoord, yCoord+1, zCoord).getLocalizedName(), "0 1 0");
   		checkblocks(worldObj.getBlock(xCoord, yCoord-1, zCoord).getLocalizedName(), "0 -1 0");
   		checkblocks(worldObj.getBlock(xCoord, yCoord, zCoord+1).getLocalizedName(), "0 0 1");
   		checkblocks(worldObj.getBlock(xCoord, yCoord, zCoord-1).getLocalizedName(), "0 0 -1");
   		worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, this.getBlockType(), 1);
   		this.color = getColor();
   		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
   	}
   	
   	public void checkblocks(String block, String coord){
   		boolean aircool = true;
		String[] coords = coord.split(" ");
   		if(block.equals("Fire")){
   			if(Temperature <= 100.0){
   				Temperature = Temperature + 0.5;
   			}
   			aircool = false;
   		}
   		if(block.equals("Lava")){
   			if(Temperature <= 700.0){
   				Temperature = Temperature + 1;
   			}else{
   				
   			}
   			aircool = false;
   		}
   		if(block.equals("Water")){
   			if(Temperature >= 10.0){
   				Temperature = Temperature - 0.1;
   				if(Temperature >= 100){
   					//worldObj.setBlockToAir(xCoord + Integer.parseInt(coords[0]), yCoord + Integer.parseInt(coords[1]), zCoord + Integer.parseInt(coords[2]));
   				}
   			}
   			aircool = false;
   		}
   		if(block.contains("HeatConductant")){
   			int gettiletemp = 0;
   			
   	    	TileEntityHeatConductant tile = (TileEntityHeatConductant) worldObj.getTileEntity(xCoord + Integer.parseInt(coords[0]), yCoord + Integer.parseInt(coords[1]), zCoord + Integer.parseInt(coords[2]));
   	    	if (tile != null)
   	    	{
   	    		gettiletemp = (int) tile.Temperature;
   	    	}
   	    	if(gettiletemp >= this.Temperature){
   	    		int conductiveness = 0;
   	    		String[] test = this.getBlockType().getLocalizedName().split(" ");
   	    		if(test[0].equals("Fast")){
   	    			conductiveness = 5;
   	    		}
   	    		double add = gettiletemp - this.Temperature;
   	    		add = add / conductiveness;
   	    		Temperature = Temperature + add;
   	    	}
   	    	aircool = false;
   		}
   		if(aircool == true){
   			if(Temperature >= 20){
   				Temperature = Temperature - 0.3;
   			}
   		}
   		if(this.Temperature >= 100){
	   		if(block.equals("Wood") || block.equals("Leaves")){
	   			int meta = worldObj.getBlockMetadata(xCoord + Integer.parseInt(coords[0]), yCoord + Integer.parseInt(coords[1]), zCoord + Integer.parseInt(coords[2]));
	   			aircool = false;
	   			if(meta == 9 || meta == 5 || meta == 1){
	   				Block water = worldObj.getBlock(xCoord + Integer.parseInt(coords[0]), yCoord + Integer.parseInt(coords[1])+1, zCoord + Integer.parseInt(coords[2]));
	   				Block water2 = worldObj.getBlock(xCoord + Integer.parseInt(coords[0])+1, yCoord + Integer.parseInt(coords[1]), zCoord + Integer.parseInt(coords[2]));
	   				Block water3 = worldObj.getBlock(xCoord + Integer.parseInt(coords[0])-1, yCoord + Integer.parseInt(coords[1]), zCoord + Integer.parseInt(coords[2]));
	   				Block water4 = worldObj.getBlock(xCoord + Integer.parseInt(coords[0]), yCoord + Integer.parseInt(coords[1]), zCoord + Integer.parseInt(coords[2])+1);
	   				Block water5 = worldObj.getBlock(xCoord + Integer.parseInt(coords[0]), yCoord + Integer.parseInt(coords[1]), zCoord + Integer.parseInt(coords[2])-1);
	   				if(water.getLocalizedName().equals("Water") || water2.getLocalizedName().equals("Water") || water3.getLocalizedName().equals("Water") || water4.getLocalizedName().equals("Water") || water5.getLocalizedName().equals("Water")){
	   					worldObj.setBlock(xCoord + Integer.parseInt(coords[0]), yCoord + Integer.parseInt(coords[1]), zCoord + Integer.parseInt(coords[2]), RegisterBlocksItems.PineSteam);
	   				}
	   			}
	   		}
   		}
   	}
   	
   	@Override
   	public Packet getDescriptionPacket() {
   		NBTTagCompound tag = new NBTTagCompound();
   		this.writeToNBT(tag);
   		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
   	}

   	@Override
   	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
   		readFromNBT(packet.func_148857_g());
   	}
}
