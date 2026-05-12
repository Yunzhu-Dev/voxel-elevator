package top.xfunny;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

// 假设您的主类是 VoxelElevator，并且 MOD_ID 在那里定义
// 假设您的创造模式物品栏类是 Init
public class Register {
    public static Block registerBlock(String id, Block block) {
        Block registered = Registry.register(Registries.BLOCK, new Identifier(VoxelElevator.MOD_ID, id), block);
        Init.VOXEL_ELEVATOR_ENTRIES.add(registered);
        return registered;
    }

    public static Item registerItem(String id, Item item) {
        Item registered = Registry.register(Registries.ITEM, new Identifier(VoxelElevator.MOD_ID, id), item);
        Init.VOXEL_ELEVATOR_ENTRIES.add(registered);
        return registered;
    }

    public static Block registerBlockWithItem(String id, Block block) {
        Block registeredBlock = Registry.register(Registries.BLOCK, new Identifier(VoxelElevator.MOD_ID, id), block);
        Item registeredItem = Registry.register(Registries.ITEM, new Identifier(VoxelElevator.MOD_ID, id),
                new BlockItem(registeredBlock, new FabricItemSettings()));
        Init.VOXEL_ELEVATOR_ENTRIES.add(registeredItem);
        return registeredBlock;
    }

    public static <T extends BlockEntity> BlockEntityType<T> registerEntityType(String id, BlockEntityType<T> type) {
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(VoxelElevator.MOD_ID, id),
                type
        );
    }

    public static <T extends Entity> EntityType<T> registerEntity(String id, EntityType<T> type) {
        return Registry.register(
                Registries.ENTITY_TYPE,
                new Identifier(VoxelElevator.MOD_ID, id),
                type
        );
    }
}