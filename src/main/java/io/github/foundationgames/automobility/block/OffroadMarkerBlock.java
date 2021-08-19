package io.github.foundationgames.automobility.block;

import io.github.foundationgames.automobility.util.AUtils;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class OffroadMarkerBlock extends Block implements OffroadBlock {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final Vec3i BLOCK_POS_UP = new Vec3i(0, 1, 0);
    private final float speedPenalty;

    public OffroadMarkerBlock(Settings settings, float speedPenalty) {
        super(settings);
        this.speedPenalty = speedPenalty;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return !(world.isAir(pos.down()) || world.getBlockState(pos.subtract(BLOCK_POS_UP)).getBlock() instanceof OffroadBlock);
    }

    @Override
    public float getSpeedPenalty(BlockState state, WorldView world, BlockPos pos) {
        return speedPenalty;
    }

    @Override
    public Vec3f getDebrisColor(BlockState state, WorldView world, BlockPos pos) {
        return AUtils.colorFromInt(world.getBlockState(pos.subtract(BLOCK_POS_UP)).getMapColor(world, pos).color);
    }
}
