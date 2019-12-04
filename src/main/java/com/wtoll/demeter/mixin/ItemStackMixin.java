package com.wtoll.demeter.mixin;

import com.wtoll.demeter.api.item.DefaultTagItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Shadow
    private CompoundTag tag;

    @Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;I)V", at = @At("RETURN"))
    private void initialize(ItemConvertible item, int count, CallbackInfo callback) {
        if (item instanceof DefaultTagItem) {
            this.tag = ((DefaultTagItem) item).getDefaultTag(tag != null ? tag : new CompoundTag());
        }
    }
}
