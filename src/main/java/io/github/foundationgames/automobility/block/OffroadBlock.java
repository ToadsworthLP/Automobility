package io.github.foundationgames.automobility.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.WorldView;

public interface OffroadBlock {
    float getSpeedPenalty(BlockState state, WorldView world, BlockPos pos);
    Vec3f getDebrisColor(BlockState state, WorldView world, BlockPos pos);
}
