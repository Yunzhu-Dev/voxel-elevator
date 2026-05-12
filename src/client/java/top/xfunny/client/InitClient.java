package top.xfunny.client;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import top.xfunny.EntityTypes;
import top.xfunny.client.render.ElevatorCabinEntityRenderer;

public class InitClient {
    public static void init(){
        EntityRendererRegistry.register(EntityTypes.ELEVATOR_CABIN_ENTITY, ElevatorCabinEntityRenderer::new);
    }
}
