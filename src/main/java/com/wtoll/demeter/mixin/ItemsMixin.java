package com.wtoll.demeter.mixin;

import com.wtoll.demeter.api.item.SeedItem;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Items.class)
public class ItemsMixin {

    @Inject(method = "register(Lnet/minecraft/util/Identifier;Lnet/minecraft/item/Item;)Lnet/minecraft/item/Item;", at = @At("HEAD"), cancellable = true)
    private static void register(Identifier id, Item item, CallbackInfoReturnable<Item> callback) {
        if (id.getNamespace() == "minecraft") {
            switch (id.getPath()) {
                case "wheat_seeds":
                    callback.setReturnValue(shadow$register(id, (new SeedItem(Blocks.WHEAT, (new Item.Settings()).group(ItemGroup.MATERIALS)))));
                    break;
                case "carrot":
                    callback.setReturnValue(shadow$register(id, (new SeedItem(Blocks.CARROTS, (new Item.Settings()).group(ItemGroup.FOOD)))));
                    break;
                case "potato":
                    callback.setReturnValue(shadow$register(id, (new SeedItem(Blocks.POTATOES, (new Item.Settings()).group(ItemGroup.FOOD)))));
                    break;
                case "beetroot_seeds":
                    callback.setReturnValue(shadow$register(id, (new SeedItem(Blocks.BEETROOTS, (new Item.Settings()).group(ItemGroup.MATERIALS)))));
                    break;
            }
        }
    }

    private static Item shadow$register(Identifier id, Item item) {
        if (item instanceof BlockItem) {
            ((BlockItem)item).appendBlocks(Item.BLOCK_ITEMS, item);
        }

        return (Item)Registry.register(Registry.ITEM, id, item);
    }
}
