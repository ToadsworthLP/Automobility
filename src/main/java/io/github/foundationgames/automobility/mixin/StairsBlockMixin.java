package io.github.foundationgames.automobility.mixin;

import io.github.foundationgames.automobility.block.BasedOnBlock;
import io.github.foundationgames.automobility.block.Sloped;
import io.github.foundationgames.automobility.util.Methods;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StairsBlock.class)
public class StairsBlockMixin implements Sloped, BasedOnBlock {
    @Shadow @Final private Block baseBlock;

    @Override
    public float getGroundSlopeX(World world, BlockState state, BlockPos pos) {
        return Methods.stairSlopeX(state);
    }

    @Override
    public float getGroundSlopeZ(World world, BlockState state, BlockPos pos) {
        return Methods.stairSlopeZ(state);
    }

    @Override
    public boolean isSticky() {
        return false;
    }

    @Override
    public Block getBaseBlock() {
        return baseBlock;
    }
}
