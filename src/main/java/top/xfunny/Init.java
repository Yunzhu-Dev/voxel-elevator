package top.xfunny;

import net.minecraft.item.ItemConvertible;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Init {
    public static final String MOD_ID = "voxel-elevator";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final List<ItemConvertible> VOXEL_ELEVATOR_ENTRIES = new ArrayList<>();//背包物品、方块项目

    public static void init() {
        Items.init();
        ItemGroups.init();
        Blocks.init();
        BlockEntityTypes.init();
        EntityTypes.init();
        LOGGER.info("初始化完成");
    }

}
