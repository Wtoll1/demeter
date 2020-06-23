package com.wtoll.demeter.loottable;

import com.wtoll.demeter.Demeter;
import com.wtoll.demeter.tag.Tags;
import net.fabricmc.fabric.api.loot.v1.FabricLootPool;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplier;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.block.Block;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <Wtoll> Will Toll on 2020-05-28
 * @project demeter
 */
public class LootTables {
    public static void init() {
        if (Demeter.config.getAutoModifyLootTables()) {
            LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplierBuilder, setter) -> {
                Block block = Registry.BLOCK.get(new Identifier(id.getNamespace(), StringUtils.removeStart(id.getPath(), "blocks/")));
                FabricLootSupplier lootTable = (FabricLootSupplier) lootManager.getTable(id);
                if (Tags.CROP.contains(block)) {
                    for (LootPool pool : lootTable.getPools()) {
                        Demeter.YIELD.forState((level) -> {
                            FabricLootPoolBuilder builder = FabricLootPoolBuilder.builder();
                            builder.copyFrom(pool);
                            builder.rolls(BinomialLootTableRange.create((int) level, Demeter.config.getLootTableBinomProbability()));
                            builder.withCondition(new BlockStatePropertyLootCondition.Builder(block).properties(StatePredicate.Builder.create().exactMatch(Demeter.YIELD.blockProperty(), (int) level)).build());
                            supplierBuilder.withPool(builder.build());
                        });
                    }
                }
            });
        }
    }
}
