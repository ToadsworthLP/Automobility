package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.item.SlopeBlockItem;
import io.github.foundationgames.automobility.item.SteepSlopeBlockItem;
import io.github.foundationgames.automobility.resource.AutomobilityAssets;
import io.github.foundationgames.automobility.resource.AutomobilityData;
import io.github.foundationgames.automobility.util.AUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public enum AutomobilityBlocks {;
    public static final Block AUTO_MECHANIC_TABLE = register("auto_mechanic_table", new AutoMechanicTableBlock(FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK)), Automobility.MAIN_GROUP);
    public static final Block GRASS_OFFROAD_LAYER = register("grass_offroad_layer", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.GRASS_BLOCK).noCollision(), AUtils.colorFromInt(0x406918)), Automobility.OFFROAD_GROUP);
    public static final Block DIRT_OFFROAD_LAYER = register("dirt_offroad_layer", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.DIRT).noCollision(), AUtils.colorFromInt(0x594227)), Automobility.OFFROAD_GROUP);
    public static final Block SAND_OFFROAD_LAYER = register("sand_offroad_layer", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.SAND).noCollision(), AUtils.colorFromInt(0xC2B185)), Automobility.OFFROAD_GROUP);
    public static final Block SNOW_OFFROAD_LAYER = register("snow_offroad_layer", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.SNOW).noCollision(), AUtils.colorFromInt(0xD0E7ED)), Automobility.OFFROAD_GROUP);

    public static final Block DASH_PANEL = register("dash_panel", new DashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveLighting((state, world, pos) -> true).noCollision()), Automobility.MAIN_GROUP);
    public static final Block SLOPED_DASH_PANEL = register("sloped_dash_panel", new SlopedDashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveLighting((state, world, pos) -> true)));
    public static final Block STEEP_SLOPED_DASH_PANEL = register("steep_sloped_dash_panel", new SteepSlopedDashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveLighting((state, world, pos) -> true)));

    public static void init() {
        Registry.register(Registry.ITEM, Automobility.id("sloped_dash_panel"), new SlopeBlockItem(null, SLOPED_DASH_PANEL, new Item.Settings().group(Automobility.SLOPES_GROUP)));
        Registry.register(Registry.ITEM, Automobility.id("steep_sloped_dash_panel"), new SteepSlopeBlockItem(null, STEEP_SLOPED_DASH_PANEL, new Item.Settings().group(Automobility.SLOPES_GROUP)));
        registerSlopes("minecraft");
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5D, 1.0D), GRASS_OFFROAD_LAYER);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> GrassColors.getColor(0.5D, 1.0D), GRASS_OFFROAD_LAYER.asItem());
    }

    public static Block register(String name, Block block) {
        return Registry.register(Registry.BLOCK, Automobility.id(name), block);
    }

    public static Block register(String name, Block block, ItemGroup group) {
        Registry.register(Registry.ITEM, Automobility.id(name), new BlockItem(block, new Item.Settings().group(group)));
        return register(name, block);
    }

    public static void registerSlopes(String namespace) {
        AutomobilityData.NON_STEEP_SLOPE_TAG_CANDIDATES.add(Automobility.id("sloped_dash_panel"));
        AutomobilityData.STEEP_SLOPE_TAG_CANDIDATES.add(Automobility.id("steep_sloped_dash_panel"));
        for (var base : Registry.BLOCK) {
            if (base.getClass().equals(Block.class)) {
                var id = Registry.BLOCK.getId(base);
                if (id.getNamespace().equals(namespace)) {
                    var path = id.getPath()+"_slope";
                    var steepPath = "steep_"+path;
                    var block = register(path, new SlopeBlock(FabricBlockSettings.copyOf(base)));
                    var normalId = Automobility.id(path);
                    var steepId = Automobility.id(steepPath);
                    Registry.register(Registry.ITEM, normalId, new SlopeBlockItem(base, block, new Item.Settings().group(Automobility.SLOPES_GROUP)));
                    block = register(steepPath, new SteepSlopeBlock(FabricBlockSettings.copyOf(base)));
                    Registry.register(Registry.ITEM, steepId, new SteepSlopeBlockItem(base, block, new Item.Settings().group(Automobility.SLOPES_GROUP)));
                    AutomobilityAssets.addProcessor(pack -> AutomobilityAssets.addMinecraftSlope(path, id.getPath()));
                    AutomobilityData.NON_STEEP_SLOPE_TAG_CANDIDATES.add(normalId);
                    AutomobilityData.STEEP_SLOPE_TAG_CANDIDATES.add(steepId);
                }
            }
        }
    }
}
