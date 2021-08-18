package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.Automobility;
import io.github.foundationgames.automobility.config.AutomobilityConfig;
import io.github.foundationgames.automobility.config.CustomBlockEntry;
import io.github.foundationgames.automobility.item.SlopeBlockItem;
import io.github.foundationgames.automobility.item.SteepSlopeBlockItem;
import io.github.foundationgames.automobility.resource.AutomobilityAssets;
import io.github.foundationgames.automobility.resource.AutomobilityData;
import io.github.foundationgames.automobility.util.AUtils;
import io.github.foundationgames.automobility.util.AutomobilityBlockSettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockAccessor;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockSettingsAccessor;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum AutomobilityBlocks {;
    public static final Block AUTO_MECHANIC_TABLE = register("auto_mechanic_table", new AutoMechanicTableBlock(FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK)), Automobility.MAIN_GROUP);
    public static final Block GRASS_OFFROAD_LAYER = register("grass_offroad_layer", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.GRASS_BLOCK).noCollision(), AUtils.colorFromInt(0x406918), Blocks.GRASS_BLOCK), Automobility.OFFROAD_GROUP);
    public static final Block DIRT_OFFROAD_LAYER = register("dirt_offroad_layer", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.DIRT).noCollision(), AUtils.colorFromInt(0x594227), Blocks.DIRT), Automobility.OFFROAD_GROUP);
    public static final Block SAND_OFFROAD_LAYER = register("sand_offroad_layer", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.SAND).noCollision(), AUtils.colorFromInt(0xC2B185), Blocks.SAND), Automobility.OFFROAD_GROUP);
    public static final Block SNOW_OFFROAD_LAYER = register("snow_offroad_layer", new OffRoadBlock(FabricBlockSettings.copyOf(Blocks.SNOW).noCollision(), AUtils.colorFromInt(0xD0E7ED), Blocks.SNOW_BLOCK), Automobility.OFFROAD_GROUP);

    public static final Block DASH_PANEL = register("dash_panel", new DashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveLighting((state, world, pos) -> true).noCollision()), Automobility.MAIN_GROUP);
    public static final Block SLOPED_DASH_PANEL = register("dash_panel_slope", new SlopedDashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveLighting((state, world, pos) -> true)));
    public static final Block STEEP_SLOPED_DASH_PANEL = register("steep_dash_panel_slope", new SteepSlopedDashPanelBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).luminance(1).emissiveLighting((state, world, pos) -> true)));

    public static void init() {
        Registry.register(Registry.ITEM, Automobility.id("dash_panel_slope"), new SlopeBlockItem(DASH_PANEL, SLOPED_DASH_PANEL, new Item.Settings().group(Automobility.SLOPES_GROUP)));
        Registry.register(Registry.ITEM, Automobility.id("steep_dash_panel_slope"), new SteepSlopeBlockItem(DASH_PANEL, STEEP_SLOPED_DASH_PANEL, new Item.Settings().group(Automobility.SLOPES_GROUP)));

        registerSlopes();
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        RenderLayer layer = AutomobilityConfig.CONFIG.getDynamicContentRenderLayer();
        if(!layer.equals(RenderLayer.getSolid())) {
            for (Block block : Registry.BLOCK) {
                if(block instanceof BasedOnBlock basedOnBlock) {
                    BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static void initFoliageColorBlocks() {
        for (Block block : Registry.BLOCK) {
            if(block instanceof BasedOnBlock basedOnBlock && ColorProviderRegistry.BLOCK.get(basedOnBlock.getBaseBlock()) != null) {
                BlockColorProvider blockColorProvider = ColorProviderRegistry.BLOCK.get(basedOnBlock.getBaseBlock());
                ItemColorProvider itemColorProvider = ColorProviderRegistry.ITEM.get(basedOnBlock.getBaseBlock().asItem());

                if(blockColorProvider != null) ColorProviderRegistry.BLOCK.register(blockColorProvider, block);
                if(itemColorProvider != null) ColorProviderRegistry.ITEM.register(itemColorProvider, block.asItem());
            }
        }
    }

    public static Block register(String name, Block block) {
        return Registry.register(Registry.BLOCK, Automobility.id(name), block);
    }

    public static Block register(String name, Block block, ItemGroup group) {
        Registry.register(Registry.ITEM, Automobility.id(name), new BlockItem(block, new Item.Settings().group(group)));
        return register(name, block);
    }

    public static void registerSlopes() {
        AutomobilityData.NON_STEEP_SLOPE_TAG_CANDIDATES.add(Automobility.id("dash_panel_slope"));
        AutomobilityData.STEEP_SLOPE_TAG_CANDIDATES.add(Automobility.id("steep_dash_panel_slope"));

        Set<Identifier> blacklist = AutomobilityConfig.CONFIG.getBlacklist();
        for (Block base : Registry.BLOCK) {
            Identifier baseIdentifier = Registry.BLOCK.getId(base);
            if (base.getClass().equals(Block.class)) {
                Identifier id = Registry.BLOCK.getId(base);
                if (!blacklist.contains(id)) {
                    String texture = id.getNamespace() + ":block/" + id.getPath();
                    registerSlope(base, texture);
                }
            }
        }

        List<CustomBlockEntry> customBlocks = AutomobilityConfig.CONFIG.getCustomBlocks();
        for (CustomBlockEntry entry : customBlocks) {
            Block base = Registry.BLOCK.get(entry.getBaseBlockIdentifier());
            if (!base.equals(Blocks.AIR)) {
                if (entry.getCustomTooltipTranslationKey().isEmpty()) {
                    registerSlope(base, entry.getTexture());
                } else {
                    registerSlope(base, entry.getTexture(), entry.getCustomName(), entry.getCustomTooltipTranslationKey());
                }
            }
        }
    }

    private static void registerSlope(Block base, String texture) {
        Identifier id = Registry.BLOCK.getId(base);
        String path = id.getPath()+"_slope";
        String steepPath = "steep_"+path;
        Identifier normalId = Automobility.id(path);
        Identifier steepId = Automobility.id(steepPath);

        AutomobilityBlockSettings settings = AutomobilityBlockSettings.copyOfDefaultState(base);
        settings.nonOpaque();
        settings.collidable(true);

        SlopeBlock slopeBlock = new SlopeBlock(settings, base);
        Block block = register(path, slopeBlock);
        Registry.register(Registry.ITEM, normalId, new SlopeBlockItem(base, block, new Item.Settings().group(Automobility.SLOPES_GROUP)));

        Block steepBlock = register(steepPath, new SteepSlopeBlock(settings, base));
        Registry.register(Registry.ITEM, steepId, new SteepSlopeBlockItem(base, steepBlock, new Item.Settings().group(Automobility.SLOPES_GROUP)));

        AutomobilityAssets.addProcessor(pack -> AutomobilityAssets.addSlope(path, texture));
        AutomobilityData.NON_STEEP_SLOPE_TAG_CANDIDATES.add(normalId);
        AutomobilityData.STEEP_SLOPE_TAG_CANDIDATES.add(steepId);
    }

    private static void registerSlope(Block base, String texture, String name, String tooltipTranslationKey) {
        String path = name+"_slope";
        String steepPath = "steep_"+path;
        Identifier normalId = Automobility.id(path);
        Identifier steepId = Automobility.id(steepPath);

        Block block = register(path, new SlopeBlock(FabricBlockSettings.copyOf(base), base));
        Registry.register(Registry.ITEM, normalId, new SlopeBlockItem(block, new Item.Settings().group(Automobility.SLOPES_GROUP), tooltipTranslationKey));

        Block steepBlock = register(steepPath, new SteepSlopeBlock(FabricBlockSettings.copyOf(base), base));
        Registry.register(Registry.ITEM, steepId, new SteepSlopeBlockItem(steepBlock, new Item.Settings().group(Automobility.SLOPES_GROUP), tooltipTranslationKey));

        AutomobilityAssets.addProcessor(pack -> AutomobilityAssets.addSlope(path, texture));
        AutomobilityData.NON_STEEP_SLOPE_TAG_CANDIDATES.add(normalId);
        AutomobilityData.STEEP_SLOPE_TAG_CANDIDATES.add(steepId);
    }
}
