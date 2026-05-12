package top.xfunny;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import top.xfunny.entity.ElevatorCabinEntity;

public class EntityTypes {
    public static final EntityType<ElevatorCabinEntity> ELEVATOR_CABIN_ENTITY = Register.registerEntity(
            "elevator_cabin",
            FabricEntityTypeBuilder.<ElevatorCabinEntity>create(SpawnGroup.MISC, ElevatorCabinEntity::new)
                    .dimensions(EntityDimensions.fixed(0, 0))
                    .trackRangeBlocks(128)
                    .build()
    );

    public static void init() {
        Init.LOGGER.info("正在注册实体");
    }
}