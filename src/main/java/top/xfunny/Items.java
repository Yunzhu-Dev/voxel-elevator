package top.xfunny;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import top.xfunny.item.CabinTapeMeasure;
import top.xfunny.item.TestTool;

public class Items {
    //轿厢卷尺
    public static final Item CABIN_TAPE_MEASURE = Register.registerItem("cabin_tape_measure", new CabinTapeMeasure(new FabricItemSettings().maxCount(1)));// 轿厢测量器，用于划定轿厢的范围
    public static final Item TEST_TOOL = Register.registerItem("test_tool", new TestTool(new FabricItemSettings()));

    public static void init() {
        Init.LOGGER.info("正在注册物品");
    }
}
