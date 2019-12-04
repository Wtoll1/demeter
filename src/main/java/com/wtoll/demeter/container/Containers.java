package com.wtoll.demeter.container;


import com.wtoll.demeter.Utility;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Identifier;

public class Containers {

    public static Identifier OBSERVATION_TABLE;

    public static void initialize() {
        ContainerProviderRegistry.INSTANCE.registerFactory(OBSERVATION_TABLE, (syncId, id, player, buf) -> {
            return new ObservationTableContainer(syncId, player.inventory, BlockContext.create(player.world, buf.readBlockPos()));
        });
    }

    static {
        OBSERVATION_TABLE = Utility.id("observation_table");
    }
}
