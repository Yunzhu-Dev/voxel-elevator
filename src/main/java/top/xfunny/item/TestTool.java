package top.xfunny.item;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.xfunny.block.block_entity.MotorBlockEntity;


public class TestTool extends Item {

    public TestTool(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();

        // 确保逻辑只在服务端执行，且玩家不为空
        BlockEntity blockEntity = null;
        if (!world.isClient && player != null) {
            blockEntity = world.getBlockEntity(pos);

            // 检查点击的方块实体是否为 MotorBlockEntity
            if (blockEntity instanceof MotorBlockEntity motor) {
                if (player.isSneaking()) {
                    // 潜行 + 右击：拆卸
                    motor.disassemble();
                    player.sendMessage(Text.literal("执行：拆卸 (Disassemble)"), true);
                } else {
                    // 普通右击：组装
                    motor.assemble();
                    player.sendMessage(Text.literal("执行：组装 (Assemble)"), true);
                }
                return ActionResult.SUCCESS; // 返回 SUCCESS 表示交互成功，播放手臂摆动动画
            }
        }

        // 如果在客户端，或者点击的不是 Motor，则放行
        return blockEntity instanceof MotorBlockEntity ? ActionResult.SUCCESS : ActionResult.PASS;
    }
}