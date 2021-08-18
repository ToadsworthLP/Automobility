package io.github.foundationgames.automobility.config;

import io.github.foundationgames.automobility.Automobility;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import java.util.*;

@Config(name = Automobility.MOD_ID)
public class AutomobilityConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public static AutomobilityConfig CONFIG;

    public static void init() {
        AutoConfig.register(AutomobilityConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(AutomobilityConfig.class).getConfig();
    }

    public Set<Identifier> getBlacklist() {
        Set<Identifier> blacklistSet = new HashSet<>();

        for (String line : blacklist) {
            String[] parts = line.split(":");
            if(parts.length == 2) {
                Identifier identifier = new Identifier(parts[0], parts[1]);
                blacklistSet.add(identifier);
            }
        }

        return blacklistSet;
    }

    public List<CustomBlockEntry> getCustomBlocks() {
        List<CustomBlockEntry> customBlocks = new ArrayList<>();

        for (String line : this.customBlocks) {
            String[] parts = line.split(",");
            if(parts.length == 2) {
                Identifier identifier = new Identifier(parts[0]);
                customBlocks.add(new CustomBlockEntry(identifier, parts[1]));
            } else if(parts.length == 4) {
                Identifier identifier = new Identifier(parts[0]);
                customBlocks.add(new CustomBlockEntry(identifier, parts[1], parts[2], parts[3]));
            }
        }

        return customBlocks;
    }

    public RenderLayer getDynamicContentRenderLayer() {
        return dynamicBlocksOnCutoutLayer ? RenderLayer.getCutout() : RenderLayer.getSolid();
    }

    @Override
    public void validatePostLoad()
    {
        for (String str : blacklist) {
            String[] parts = str.split(":");
            if(parts.length != 2) {
                Automobility.LOGGER.warn("Skipping malformed blacklist config entry \"" + str + "\"!");
            }
        }

        for (String str : customBlocks) {
            String[] parts = str.split(",");
            if(!(parts.length == 2 || parts.length == 4)) {
                Automobility.LOGGER.warn("Skipping malformed custom block config entry \"" + str + "\"!");
            }
        }
    }

    @Comment("Block identifiers that should be excluded from dynamic content generation")
    private List<String> blacklist = new ArrayList<>();

    @Comment("""
            Used to add custom blocks to the dynamic content generation system.
            Format:
            <base block identifier>,<texture identifier> or, if registering more than one for that base block,
            <base block identifier>,<texture identifier>,<custom name>,<custom tooltip translation key>""")
    private List<String> customBlocks = new ArrayList<>();

    @Comment("""
            Whether dynamically generated blocks are rendered on the Solid or Cutout layer
            Turn this on if any custom blocks should be see-through, but aren't.""")
    private boolean dynamicBlocksOnCutoutLayer = false;
}
