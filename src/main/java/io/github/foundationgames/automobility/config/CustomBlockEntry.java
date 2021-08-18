package io.github.foundationgames.automobility.config;

import net.minecraft.util.Identifier;

public class CustomBlockEntry {
    private final Identifier baseBlockIdentifier;
    private final String texture;
    private final String customName;
    private final String customTooltipTranslationKey;

    public CustomBlockEntry(Identifier baseBlock, String texture) {
        this.baseBlockIdentifier = baseBlock;
        this.texture = texture;
        this.customName = "";
        this.customTooltipTranslationKey = "";
    }

    public CustomBlockEntry(Identifier baseBlock, String texture, String name, String translationKey) {
        this.baseBlockIdentifier = baseBlock;
        this.texture = texture;
        this.customName = name;
        this.customTooltipTranslationKey = translationKey;
    }

    public Identifier getBaseBlockIdentifier() {
        return baseBlockIdentifier;
    }

    public String getTexture() {
        return texture;
    }

    public String getCustomName() {
        return customName;
    }

    public String getCustomTooltipTranslationKey() {
        return customTooltipTranslationKey;
    }
}
