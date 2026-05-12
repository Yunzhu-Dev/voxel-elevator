package top.xfunny;

import net.minecraft.block.Block;
import top.xfunny.block.Motor;

public class Blocks {
    //曳引机
    public static final Block MOTOR = Register.registerBlockWithItem("motor", new Motor(Block.Settings.create()));

    public static void init() {
        Init.LOGGER.info("正在注册方块");
    }
}
