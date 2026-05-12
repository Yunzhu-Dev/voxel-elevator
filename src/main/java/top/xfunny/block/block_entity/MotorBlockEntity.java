package top.xfunny.block.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import top.xfunny.BlockEntityTypes; // 你的方块实体注册类
import top.xfunny.entity.ElevatorCabinEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MotorBlockEntity extends BlockEntity {
    private BlockPos pos1;
    private BlockPos pos2;
    private UUID cabinEntityUuid;

    public MotorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.MOTOR_BLOCK_ENTITY, pos, state);
    }

    /**
     * 装配选区内的方块，形成一个电梯轿厢实体。
     */
    public void assemble() {
        if (world == null || world.isClient() || pos1 == null || pos2 == null) {
            return;
        }
        if (cabinEntityUuid != null) {
            disassemble();
        }

        Map<BlockPos, BlockState> structure = new HashMap<>();

        // 正确计算选区的最小角和最大角
        BlockPos minCorner = new BlockPos(
                Math.min(pos1.getX(), pos2.getX()),
                Math.min(pos1.getY(), pos2.getY()),
                Math.min(pos1.getZ(), pos2.getZ())
        );
        BlockPos maxCorner = new BlockPos(
                Math.max(pos1.getX(), pos2.getX()),
                Math.max(pos1.getY(), pos2.getY()),
                Math.max(pos1.getZ(), pos2.getZ())
        );

        // 1. 遍历选区，收集方块信息并替换为空气
        for (BlockPos currentPos : BlockPos.iterate(minCorner, maxCorner)) {
            BlockState stateInWorld = world.getBlockState(currentPos);
            // 排除空气，也可以在这里排除电机方块自身或基岩
            if (!stateInWorld.isAir()) {
                BlockPos relativePos = currentPos.subtract(minCorner);
                structure.put(relativePos, stateInWorld);
                world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), 3);
            }
        }

        if (structure.isEmpty()) {
            return; // 没有方块可以组合
        }

        // 2. 在结构的最小角位置创建并生成实体
        ElevatorCabinEntity cabin = new ElevatorCabinEntity(world, minCorner, structure);
        world.spawnEntity(cabin);
        this.cabinEntityUuid = cabin.getUuid();
        markDirty(); // 保存状态
    }

    /**
     * 拆解电梯轿厢实体，在实体当前位置恢复方块。
     */
    public void disassemble() {
        if (world == null || world.isClient() || cabinEntityUuid == null) {
            return;
        }

        ServerWorld serverWorld = (ServerWorld) world;
        ElevatorCabinEntity cabin = (ElevatorCabinEntity) serverWorld.getEntity(cabinEntityUuid);

        if (cabin != null) {
            Map<BlockPos, BlockState> structure = cabin.getStructure();
            BlockPos cabinOrigin = cabin.getBlockPos(); // 获取轿厢当前位置作为恢复原点

            // 1. 遍历结构，将方块放回世界
            for (Map.Entry<BlockPos, BlockState> entry : structure.entrySet()) {
                BlockPos relativePos = entry.getKey();
                BlockState state = entry.getValue();
                BlockPos absolutePos = cabinOrigin.add(relativePos);
                world.setBlockState(absolutePos, state, 3);
            }

            // 2. 移除实体
            cabin.discard();
        }

        this.cabinEntityUuid = null;
        markDirty(); // 保存状态
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.pos1 != null) nbt.put("pos1", NbtHelper.fromBlockPos(this.pos1));
        if (this.pos2 != null) nbt.put("pos2", NbtHelper.fromBlockPos(this.pos2));
        if (this.cabinEntityUuid != null) nbt.putUuid("CabinEntityUuid", this.cabinEntityUuid);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("pos1")) this.pos1 = NbtHelper.toBlockPos(nbt.getCompound("pos1"));
        if (nbt.contains("pos2")) this.pos2 = NbtHelper.toBlockPos(nbt.getCompound("pos2"));
        if (nbt.containsUuid("CabinEntityUuid")) this.cabinEntityUuid = nbt.getUuid("CabinEntityUuid");
    }

    public void setArea(BlockPos pos1, BlockPos pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        markDirty();
    }
}