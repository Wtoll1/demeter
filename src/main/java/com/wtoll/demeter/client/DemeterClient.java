package com.wtoll.demeter.client;

import com.wtoll.demeter.Demeter;
import com.wtoll.demeter.Utility;
import com.wtoll.demeter.block.Blocks;
import com.wtoll.demeter.client.gui.ObservationTableScreen;
import com.wtoll.demeter.container.Containers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

public class DemeterClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenProviderRegistry.INSTANCE.registerFactory(Containers.OBSERVATION_TABLE, (syncId, identifier, player, buffer) -> {
            return new ObservationTableScreen(syncId, player);
        });

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register(this::registerSprites);

        registerRenderers();
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
}
