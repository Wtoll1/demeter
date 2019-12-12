package com.wtoll.demeter.container;

import com.wtoll.demeter.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.container.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class ObservationTableContainer extends Container {
    private ItemStack inputStack;
    final Slot inputSlot;
    private final BlockContext context;
    public final Inventory inventory;
    private Runnable contentsChangedListener;

    public ObservationTableContainer(int syncId, PlayerInventory inventory, final BlockContext context) {
        super(null, syncId);
        this.inputStack = ItemStack.EMPTY;
        this.context = context;
        this.inventory = new BasicInventory(1) {
            public void markDirty() {
                super.markDirty();
                ObservationTableContainer.this.onContentChanged(this);
                ObservationTableContainer.this.contentsChangedListener.run();
            }
        };
        this.inputSlot = this.addSlot(new Slot(this.inventory, 0, 17, 41){
            public boolean canInsert(ItemStack stack) {
                return (stack.getItem() instanceof BlockItem) && (((BlockItem) stack.getItem()).getBlock() instanceof CropBlock);
            }
        });

        int k;
        for(k = 0; k < 3; ++k) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + k * 9 + 9, 8 + j * 18, 118 + k * 18));
            }
        }

        for(k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 176));
        }

        this.contentsChangedListener = () -> {
            ItemStack stack = inputSlot.getStack();
            CompoundTag tag = stack.getOrCreateTag();
            tag.putBoolean("observed", true);
            stack.setTag(tag);
        };
    }

    @Override
    public ItemStack onSlotClick(int slotId, int clickData, SlotActionType action, PlayerEntity player) {
        if (action == SlotActionType.QUICK_MOVE) {
            return transferSlot(player, slotId);
        } else {
            return super.onSlotClick(slotId, clickData, action, player);
        }
    }

    public ObservationTableContainer(int i, PlayerInventory playerInventory) {
        this(i, playerInventory, BlockContext.EMPTY);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, Blocks.OBSERVATION_TABLE);
    }

    public void close(PlayerEntity player) {
        super.close(player);
        this.context.run((world, blockPos) -> {
            this.dropInventory(player, player.world, this.inventory);
        });
    }
}
