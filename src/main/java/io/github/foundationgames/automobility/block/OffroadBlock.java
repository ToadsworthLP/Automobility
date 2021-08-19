package io.github.foundationgames.automobility.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec3f;

public interface OffroadBlock {
    float getSpeedPenalty(BlockState state);
    Vec3f getDebrisColor(BlockState state);
}
