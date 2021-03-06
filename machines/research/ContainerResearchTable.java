package molecularscience.machines.research;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerResearchTable extends Container {

        protected TileEntityResearchTable tileEntity;
        private int progress;

        public ContainerResearchTable (InventoryPlayer inventoryPlayer, TileEntityResearchTable te){
                tileEntity = te;
                addSlotToContainer(new Slot(tileEntity, 0, 26, 36));
                addSlotToContainer(new Slot(tileEntity, 1, 134, 36));
                bindPlayerInventory(inventoryPlayer);
        }
        
        @Override
        public boolean canInteractWith(EntityPlayer player) {
                return tileEntity.isUseableByPlayer(player);
        }


        protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
                for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 9; j++) {
                                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
                                                8 + j * 18, 76 + i * 18));
                        }
                }

                for (int i = 0; i < 9; i++) {
                        addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 134));
                }
        }

        @Override
        public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
                ItemStack stack = null;
                Slot slotObject = (Slot) inventorySlots.get(slot);
                if (slotObject != null && slotObject.getHasStack()) {
                        ItemStack stackInSlot = slotObject.getStack();
                        stack = stackInSlot.copy();
                        if (slot < 9) {
                                if (!this.mergeItemStack(stackInSlot, 0, 35, true)) {
                                        return null;
                                }
                        }
                        else if (!this.mergeItemStack(stackInSlot, 0, 9, false)) {
                                return null;
                        }

                        if (stackInSlot.stackSize == 0) {
                                slotObject.putStack(null);
                        } else {
                                slotObject.onSlotChanged();
                        }

                        if (stackInSlot.stackSize == stack.stackSize) {
                                return null;
                        }
                        slotObject.onPickupFromSlot(player, stackInSlot);
                }
                return stack;
        }
        
    	public void addCraftingToCrafters(ICrafting crafting){
    		super.addCraftingToCrafters(crafting);
    	    crafting.sendProgressBarUpdate(this, 0, this.tileEntity.progress);
    	}
    	
    	public void detectAndSendChanges(){
    		super.detectAndSendChanges();
    		for (int i = 0; i < this.crafters.size(); ++i){
    			ICrafting icrafting = (ICrafting)this.crafters.get(i);
    			if (this.progress != this.tileEntity.progress){
    				icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.progress);
    	        }
    	    }
    	    this.progress = this.tileEntity.progress;
    	}
    	
    	@SideOnly(Side.CLIENT)
    	public void updateProgressBar(int par1, int par2){
    		if (par1 == 0){
    			this.tileEntity.progress = par2;
    	    }
    	}
    	
}