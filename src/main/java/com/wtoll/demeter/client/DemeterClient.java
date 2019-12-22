package com.wtoll.demeter.client;

import com.wtoll.demeter.Demeter;
import com.wtoll.demeter.Utility;
import com.wtoll.demeter.block.Blocks;
import com.wtoll.demeter.client.gui.ObservationTableScreen;
import com.wtoll.demeter.container.Containers;
import com.wtoll.demeter.helper.TooltipHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.event.client.ItemTooltipCallback;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.block.CropBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class DemeterClient implements ClientModInitializer {
    @SuppressWarnings("deprecation")
    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(Containers.OBSERVATION_TABLE, (syncId, identifier, player, buffer) -> {
            return new ObservationTableScreen(syncId, player);
        });

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register(this::registerSprites);

        registerRenderers();
        registerTooltips();
    }

    private void registerSprites(SpriteAtlasTexture atlas, ClientSpriteRegistryCallback.Registry registry) {
        Demeter.CROP_PROPERTIES.getIds().forEach((id) -> {
            registry.register(new Identifier(id.getNamespace(), "gui/property/" + id.getPath()));
        });
        registry.register(Utility.id("gui/property/missing"));
    }

    public static void registerRenderers() {
        BlockRenderLayerMapImpl.INSTANCE.putBlock(Blocks.OBSERVATION_TABLE, RenderLayer.getCutout());
        BlockRenderLayerMapImpl.INSTANCE.putBlock(net.minecraft.block.Blocks.FARMLAND, RenderLayer.getCutout());
    }

    public static void registerTooltips() {
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltip) -> {
            Item item = stack.getItem();
            if (item instanceof BlockItem) {
                if (((BlockItem) item).getBlock() instanceof CropBlock) {
                    CompoundTag tag = stack.getTag();
                    if (tag != null) {
                        if (tag.getBoolean("observed")) {
                            CompoundTag properties = stack.getSubTag("BlockStateTag");
                            if (properties != null) {
                                Demeter.CROP_PROPERTIES.forEach((property) -> {
                                    Tag propTag = properties.get(property.getName());
                                    if (propTag != null) {
                                        String value = TooltipHelper.parseTooltipValue(propTag.toString());
                                        if (!value.equals("0")) {
                                            String name = TooltipHelper.parseTooltipValue(property.getName());
                                            if (property instanceof IntProperty) {
                                                value = TooltipHelper.toRoman(Integer.parseInt(value));
                                            }
                                            tooltip.add(1, new LiteralText( "§7" + TooltipHelper.capitalizeTooltip(name) + " " + value));
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }
}
