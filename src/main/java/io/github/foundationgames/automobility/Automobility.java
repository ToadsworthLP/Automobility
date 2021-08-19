package io.github.foundationgames.automobility;

import io.github.foundationgames.automobility.block.AutomobilityBlocks;
import io.github.foundationgames.automobility.config.AutomobilityConfig;
import io.github.foundationgames.automobility.entity.AutomobilityEntities;
import io.github.foundationgames.automobility.item.AutomobilityItems;
import io.github.foundationgames.automobility.resource.AutomobilityData;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.lambdacontrols.ControllerUtils;
import io.github.foundationgames.automobility.util.network.PayloadPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Automobility implements ModInitializer {
    public static final String MOD_ID = "automobility";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final ItemGroup MAIN_GROUP = FabricItemGroupBuilder.build(Automobility.id("automobility"), AUtils::createGroupIcon);
    public static final ItemGroup PREFABS_GROUP = FabricItemGroupBuilder.build(Automobility.id("automobility_prefabs"), AUtils::createPrefabsIcon);
    public static final ItemGroup SLOPES_GROUP = FabricItemGroupBuilder.build(Automobility.id("automobility_slopes"), AUtils::createSlopesIcon);

    public static final Tag<Block> SLOPES = TagRegistry.block(Automobility.id("slopes"));
    public static final Tag<Block> STEEP_SLOPES = TagRegistry.block(Automobility.id("steep_slopes"));
    public static final Tag<Block> NON_STEEP_SLOPES = TagRegistry.block(Automobility.id("non_steep_slopes"));

    @Override
    public void onInitialize() {
        AutomobilityConfig.init();
        AutomobilityItems.init();
        AutomobilityBlocks.init();
        AutomobilityEntities.init();
        PayloadPackets.init();

        AutomobilityData.setup();

        ControllerUtils.initLCHandler();
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
