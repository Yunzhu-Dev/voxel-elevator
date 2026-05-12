package top.xfunny.item;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import top.xfunny.block.block_entity.MotorBlockEntity;

public class CabinTapeMeasure extends Item {
    // 使用常量来避免拼写错误
    private static final String KEY_POS1 = "Pos1";
    private static final String KEY_POS2 = "Pos2";
    private static final String KEY_DIMENSION = "Dimension"; // 只需要一个维度Key，因为两点必须在同一维度
    //轿厢卷尺
    public CabinTapeMeasure(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        // 确保在服务端执行
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        BlockPos clickedPos = context.getBlockPos();
        NbtCompound nbt = context.getStack().getOrCreateNbt();
        String currentDimension = world.getRegistryKey().getValue().toString();

        // 1. 如果玩家潜行，尝试将坐标保存到控制器
        if (context.getPlayer().isSneaking()) {
            BlockEntity motor = world.getBlockEntity(clickedPos);
            // 检查点击的是否是我们的控制器，并且卷尺已经选好了两个点
            if (motor instanceof MotorBlockEntity && nbt.contains(KEY_POS1) && nbt.contains(KEY_POS2)) {
                String storedDimension = nbt.getString(KEY_DIMENSION);
                MotorBlockEntity motorBlockEntity = (MotorBlockEntity) motor;

                // 【维度检查】确保控制器和选点在同一维度
                if (!currentDimension.equals(storedDimension)) {
                    context.getPlayer().sendMessage(Text.of("§c错误：控制器必须和选择的范围在同一个维度！"), false);
                    return ActionResult.FAIL;
                }

                BlockPos pos1 = NbtHelper.toBlockPos(nbt.getCompound(KEY_POS1));
                BlockPos pos2 = NbtHelper.toBlockPos(nbt.getCompound(KEY_POS2));

                // 调用方块实体的方法来保存数据
                motorBlockEntity.setArea(pos1, pos2);

                // 清空卷尺上的所有临时数据
                nbt.remove(KEY_POS1);
                nbt.remove(KEY_POS2);
                nbt.remove(KEY_DIMENSION);

                context.getPlayer().sendMessage(Text.of("§a电梯范围已成功绑定到曳引机！"), false);
                return ActionResult.SUCCESS;
            }
        }

        // 2. 正常右键，选择坐标点
        // 情况A：选择第一个点
        if (!nbt.contains(KEY_POS1)) {
            nbt.put(KEY_POS1, NbtHelper.fromBlockPos(clickedPos));
            nbt.putString(KEY_DIMENSION, currentDimension); // 存储维度
            context.getPlayer().sendMessage(Text.of("已选择第一个角点: " + clickedPos.toShortString()), false);
        }
        // 情况B：选择第二个点
        else if (!nbt.contains(KEY_POS2)) {
            String storedDimension = nbt.getString(KEY_DIMENSION);

            // 【维度检查】确保第二个点和第一个点在同一维度
            if (!currentDimension.equals(storedDimension)) {
                context.getPlayer().sendMessage(Text.of("§c错误：第二个点必须和第一个点在同一个维度！正在重置选择..."), false);
                // 重置所有数据
                nbt.remove(KEY_POS1);
                nbt.remove(KEY_DIMENSION);
                return ActionResult.FAIL;
            }

            nbt.put(KEY_POS2, NbtHelper.fromBlockPos(clickedPos));
            context.getPlayer().sendMessage(Text.of("已选择第二个角点: " + clickedPos.toShortString() + "。请潜行右键控制器以保存。"), false);
        }
        // 情况C：两个点都已选，再次点击则重置
        else {
            context.getPlayer().sendMessage(Text.of("选择已重置。请重新选择第一个角点。"), false);
            nbt.remove(KEY_POS1);
            nbt.remove(KEY_POS2);
            nbt.remove(KEY_DIMENSION);
        }

        return ActionResult.SUCCESS;
    }
}