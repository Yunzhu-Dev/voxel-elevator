package top.xfunny;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemGroups {
    public static final ItemGroup VOXEL_ELEVATOR = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(VoxelElevator.MOD_ID, "voxel_elevator"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.voxel_elevator"))
                    .icon(Items.CABIN_TAPE_MEASURE::getDefaultStack)
                    .entries((ctx, entries) -> {
                        Init.VOXEL_ELEVATOR_ENTRIES.forEach(entries::add);
                    })
                    .build()
    );

    public static void init() {
        Init.LOGGER.info("正在注册物品组");
    }
}
