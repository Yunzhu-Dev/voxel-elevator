package top.xfunny.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import top.xfunny.EntityTypes;

import java.util.HashMap;
import java.util.Map;

public class ElevatorCabinEntity extends Entity {

    // 用于自动同步服务端和客户端数据的追踪器
    private static final TrackedData<NbtCompound> STRUCTURE_NBT = DataTracker.registerData(ElevatorCabinEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);

    private Map<BlockPos, BlockState> structure = new HashMap<>();
    private VoxelShape collisionShape;
    private Box localBoundingBox;

    public ElevatorCabinEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true;
        this.collisionShape = VoxelShapes.empty();
        this.localBoundingBox = new Box(0, 0, 0, 0, 0, 0);
    }

    public ElevatorCabinEntity(World world, BlockPos pos, Map<BlockPos, BlockState> structure) {
        super(EntityTypes.ELEVATOR_CABIN_ENTITY, world);
        this.noClip = true;
        this.setPosition(pos.getX(), pos.getY(), pos.getZ());

        this.structure = structure;
        // 在服务端创建时，将结构写入 DataTracker 以便同步给客户端
        this.dataTracker.set(STRUCTURE_NBT, writeStructureToNbt());

        this.buildCollisionModel();
    }

    @Override
    protected void initDataTracker() {
        //初始化追踪器，默认给一个空的 NBT
        this.dataTracker.startTracking(STRUCTURE_NBT, new NbtCompound());
    }

    // 当客户端接收到服务端的数据更新时触发
    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        if (STRUCTURE_NBT.equals(data)) {
            readStructureFromNbt(this.dataTracker.get(STRUCTURE_NBT));
            buildCollisionModel();
        }
        super.onTrackedDataSet(data);
    }

    private void buildCollisionModel() {
        if (structure.isEmpty() || getWorld() == null) {
            this.collisionShape = VoxelShapes.empty();
            this.localBoundingBox = new Box(0, 0, 0, 0, 0, 0);
            return;
        }

        VoxelShape combinedShape = VoxelShapes.empty();
        for (Map.Entry<BlockPos, BlockState> entry : structure.entrySet()) {
            BlockPos relativePos = entry.getKey();
            BlockState state = entry.getValue();

            VoxelShape blockShape = state.getCollisionShape(getWorld(), BlockPos.ORIGIN);
            if (!blockShape.isEmpty()) {
                combinedShape = VoxelShapes.union(combinedShape, blockShape.offset(relativePos.getX(), relativePos.getY(), relativePos.getZ()));
            }
        }
        this.collisionShape = combinedShape;
        this.localBoundingBox = combinedShape.getBoundingBox();
        this.setBoundingBox(this.localBoundingBox.offset(this.getPos()));
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
        if (this.localBoundingBox != null) {
            this.setBoundingBox(this.localBoundingBox.offset(x, y, z));
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    private NbtCompound writeStructureToNbt() {
        NbtCompound nbt = new NbtCompound();
        NbtList structureList = new NbtList();
        for (Map.Entry<BlockPos, BlockState> entry : structure.entrySet()) {
            NbtCompound structureEntry = new NbtCompound();
            structureEntry.put("Pos", NbtHelper.fromBlockPos(entry.getKey()));
            structureEntry.put("State", NbtHelper.fromBlockState(entry.getValue()));
            structureList.add(structureEntry);
        }
        nbt.put("Structure", structureList);
        return nbt;
    }

    private void readStructureFromNbt(NbtCompound nbt) {
        this.structure.clear();
        if (nbt.contains("Structure")) {
            NbtList structureList = nbt.getList("Structure", 10);
            for (int i = 0; i < structureList.size(); i++) {
                NbtCompound entry = structureList.getCompound(i);
                BlockPos pos = NbtHelper.toBlockPos(entry.getCompound("Pos"));
                BlockState state = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), entry.getCompound("State"));
                this.structure.put(pos, state);
            }
        }
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        readStructureFromNbt(nbt);
        this.dataTracker.set(STRUCTURE_NBT, writeStructureToNbt()); // 读档后同步给客户端
        buildCollisionModel();
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        NbtCompound structureNbt = writeStructureToNbt();
        nbt.put("Structure", structureNbt.getList("Structure", 10));
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    public Map<BlockPos, BlockState> getStructure() {
        return structure;
    }

    @Override
    public boolean isPushable() {
        return false;
    }
}