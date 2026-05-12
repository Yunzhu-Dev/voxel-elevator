package top.xfunny.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import top.xfunny.entity.ElevatorCabinEntity;

import java.util.Map;
import java.util.Random;

public class ElevatorCabinEntityRenderer extends EntityRenderer<ElevatorCabinEntity> {

    private final BlockRenderManager blockRenderManager;

    public ElevatorCabinEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.blockRenderManager = ctx.getBlockRenderManager();
    }

    @Override
    public void render(ElevatorCabinEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

        Map<BlockPos, BlockState> structure = entity.getStructure();
        if (structure.isEmpty()) {
            return;
        }

        matrices.push();
        // 实体的位置是其原点，渲染时直接从这个原点开始绘制相对方块

        Random random = new Random();
        for (Map.Entry<BlockPos, BlockState> entry : structure.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();

            matrices.push();
            matrices.translate(pos.getX(), pos.getY(), pos.getZ());

            // 使用 BlockRenderManager 来渲染每个方块
            this.blockRenderManager.renderBlockAsEntity(state, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);

            matrices.pop();
        }

        matrices.pop();
    }

    @Override
    public Identifier getTexture(ElevatorCabinEntity entity) {
        return null;
    }
}