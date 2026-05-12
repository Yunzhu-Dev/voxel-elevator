package top.xfunny;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import top.xfunny.block.block_entity.MotorBlockEntity;

public class BlockEntityTypes {
    public static void init() {
        Init.LOGGER.info("正在注册方块实体");
    }    public static final BlockEntityType<MotorBlockEntity> MOTOR_BLOCK_ENTITY = Register.registerEntityType("motor",
            FabricBlockEntityTypeBuilder.create(MotorBlockEntity::new, Blocks.MOTOR).build());

}