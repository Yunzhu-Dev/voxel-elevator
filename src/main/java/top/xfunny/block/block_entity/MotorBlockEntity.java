package top.xfunny.block.block_entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import top.xfunny.BlockEntityTypes; // 导入你的注册类

public class MotorBlockEntity extends BlockEntity {
    private BlockPos pos1;
    private BlockPos pos2;

    // 【关键修改】构造函数只接受 pos 和 state
    public MotorBlockEntity(BlockPos pos, BlockState state) {
        // 【关键修改】在 super() 调用中传入注册好的 BlockEntityType
        super(BlockEntityTypes.MOTOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.pos1 != null) {
            nbt.put("pos1", NbtHelper.fromBlockPos(this.pos1));
        }
        if (this.pos2 != null) {
            nbt.put("pos2", NbtHelper.fromBlockPos(this.pos2));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("pos1")) {
            this.pos1 = NbtHelper.toBlockPos(nbt.getCompound("pos1"));
        }
        if (nbt.contains("pos2")) {
            this.pos2 = NbtHelper.toBlockPos(nbt.getCompound("pos2"));
        }
    }

    public void setArea(BlockPos pos1, BlockPos pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        markDirty();
    }
}