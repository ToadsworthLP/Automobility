package io.github.foundationgames.automobility.util;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockAccessor;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockSettingsAccessor;
import net.minecraft.block.*;

public class AutomobilityBlockSettings extends FabricBlockSettings {
    protected AutomobilityBlockSettings(Material material, MapColor color) {
        super(material, color);
    }

    protected AutomobilityBlockSettings(AbstractBlock.Settings settings) {
        super(settings);
    }

    public static AutomobilityBlockSettings copyOfDefaultState(Block block) {
        AutomobilityBlockSettings settings = new AutomobilityBlockSettings(((AbstractBlockAccessor) block).getSettings());

        AbstractBlockSettingsAccessor thisAccessor = (AbstractBlockSettingsAccessor) settings;

        BlockState defaultState = block.getDefaultState();

        thisAccessor.setMaterial(defaultState.getMaterial());
        thisAccessor.setHardness(defaultState.getHardness(null, null));
        thisAccessor.setRandomTicks(defaultState.hasRandomTicks());
        thisAccessor.setLuminanceFunction((BlockState state) -> defaultState.getLuminance());
        thisAccessor.setMapColorProvider((BlockState state) -> defaultState.getMapColor(null, null));
        settings.sounds(defaultState.getSoundGroup());
        thisAccessor.setOpaque(defaultState.isOpaque());
        thisAccessor.setIsAir(defaultState.isAir());
        thisAccessor.setToolRequired(defaultState.isToolRequired());

        return settings;
    }
}
