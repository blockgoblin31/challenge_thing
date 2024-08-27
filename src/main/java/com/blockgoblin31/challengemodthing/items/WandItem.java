package com.blockgoblin31.challengemodthing.items;

import appeng.api.features.IPlayerRegistry;
import appeng.api.implementations.items.ISpatialStorageCell;
import appeng.core.AELog;
import appeng.spatial.SpatialStorageHelper;
import appeng.spatial.SpatialStoragePlot;
import appeng.spatial.SpatialStoragePlotManager;
import appeng.spatial.TransitionInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.function.Function;

public class WandItem extends Item implements ISpatialStorageCell {

    public WandItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pLevel.isClientSide) return super.use(pLevel, pPlayer, pUsedHand);
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        MinecraftServer server = ((ServerLevel) pLevel).getServer();
        try {
            if (!pLevel.dimension().equals(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ae2:spatial_storage")))) {
                int id = getAllocatedPlotId(stack);
                if (id == -1) {
                    SpatialStoragePlot plot = SpatialStoragePlotManager.INSTANCE.allocatePlot(new BlockPos(32, 32, 32), IPlayerRegistry.getPlayerId((ServerPlayer) pPlayer));
                    stack.getOrCreateTag().put("plot_id", IntTag.valueOf(plot.getId()));
                    id = plot.getId();
                }
                SpatialStoragePlot plot = SpatialStoragePlotManager.INSTANCE.getPlot(id);
                assert plot != null;

                CompoundTag posTag = new CompoundTag();
                posTag.put("x", DoubleTag.valueOf(pPlayer.getX()));
                posTag.put("y", DoubleTag.valueOf(pPlayer.getY()));
                posTag.put("z", DoubleTag.valueOf(pPlayer.getZ()));
                posTag.put("level", StringTag.valueOf(pLevel.dimension().location().toString()));
                stack.getTag().put("old_pos", posTag);
                ServerLevel newLevel = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation("ae2:spatial_storage")));
                newLevel.getChunkSource().getChunk(Mth.floor(plot.getOrigin().getX()) >> 4, Mth.floor(plot.getOrigin().getZ()) >> 4,
                        ChunkStatus.FULL, true);
                pPlayer.changeDimension(newLevel, new ITeleporter() {
                    @Override
                    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                        return repositionEntity.apply(false);
                    }

                    @Override
                    public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                        return new PortalInfo(plot.getOrigin().above().east().south().getCenter(), Vec3.ZERO, entity.getYRot(), entity.getXRot());
                    }
                });
            } else {
                ServerPlayer player = (ServerPlayer) pPlayer;
                if (!stack.hasTag() || !stack.getTag().contains("old_pos")) {
                    ServerLevel newLevel = server.getLevel(player.getRespawnDimension());
                    newLevel.getChunkSource().getChunk(Mth.floor(player.getRespawnPosition().getX()) >> 4, Mth.floor(player.getRespawnPosition().getZ()) >> 4,
                            ChunkStatus.FULL, true);
                    pPlayer.changeDimension(newLevel, new ITeleporter() {
                        @Override
                        public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                            return repositionEntity.apply(false);
                        }

                        @Override
                        public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                            return new PortalInfo(player.getRespawnPosition().getCenter(), Vec3.ZERO, entity.getYRot(), entity.getXRot());
                        }
                    });
                } else {
                    CompoundTag oldPos = (CompoundTag) stack.getTag().get("old_pos");
                    ServerLevel newLevel = server.getLevel(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(oldPos.getString("level"))));
                    newLevel.getChunkSource().getChunk(Mth.floor(oldPos.getDouble("x")) >> 4, Mth.floor(oldPos.getDouble("z")) >> 4,
                            ChunkStatus.FULL, true);
                    pPlayer.changeDimension(newLevel, new ITeleporter() {
                        @Override
                        public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                            return repositionEntity.apply(false);
                        }

                        @Override
                        public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                            return new PortalInfo(new Vec3(oldPos.getDouble("x"), oldPos.getDouble("y"), oldPos.getDouble("z")), Vec3.ZERO, entity.getYRot(), entity.getXRot());
                        }
                    });
                }
            }
            return InteractionResultHolder.pass(stack);
        } catch(Exception ignored) {
            return InteractionResultHolder.fail(stack);
        }
    }

    @Override
    public boolean isSpatialStorage(ItemStack is) {
        return true;
    }

    @Override
    public int getMaxStoredDim(ItemStack is) {
        return 32;
    }

    @Override
    public int getAllocatedPlotId(ItemStack is) {
        final CompoundTag c = is.getTag();
        if (c != null && c.contains("plot_id")) {
            try {
                int plotId = c.getInt("plot_id");
                if (SpatialStoragePlotManager.INSTANCE.getPlot(plotId) == null) {
                    return -1;
                }
                return plotId;
            } catch (Exception e) {
                AELog.warn("Failed to retrieve spatial storage dimension: %s", e);
            }
        }
        return -1;    }

    @Override
    public boolean doSpatialTransition(ItemStack is, ServerLevel level, BlockPos min, BlockPos max, int playerId) {
        final int targetX = max.getX() - min.getX() - 1;
        final int targetY = max.getY() - min.getY() - 1;
        final int targetZ = max.getZ() - min.getZ() - 1;
        final int maxSize = this.getMaxStoredDim(is);
        if (targetX > maxSize || targetY > maxSize || targetZ > maxSize) {
            AELog.info(
                    "Failing spatial transition because the transfer area (%dx%dx%d) exceeds the cell capacity (%s).",
                    targetX, targetY, targetZ, maxSize);
            return false;
        }

        final BlockPos targetSize = new BlockPos(targetX, targetY, targetZ);

        SpatialStoragePlotManager manager = SpatialStoragePlotManager.INSTANCE;

        SpatialStoragePlot plot = SpatialStoragePlotManager.INSTANCE.getPlot(this.getAllocatedPlotId(is));
        if (plot != null) {
            // Check that the existing plot has the right size
            if (!plot.getSize().equals(targetSize)) {
                AELog.info(
                        "Failing spatial transition because the transfer area (%dx%dx%d) does not match the spatial storage plot's size (%s).",
                        targetX, targetY, targetZ, plot.getSize());
                return false;
            }
        } else {
            // Otherwise allocate a new one
            plot = manager.allocatePlot(targetSize, playerId);
        }

        // Store some information about this transition in the plot
        TransitionInfo info = new TransitionInfo(level.dimension().location(), min, max, Instant.now());
        manager.setLastTransition(plot.getId(), info);

        try {
            ServerLevel cellLevel = manager.getLevel();

            BlockPos offset = plot.getOrigin();

            this.setStoredDimension(is, plot.getId(), plot.getSize());
            SpatialStorageHelper.getInstance().swapRegions(level, min.getX() + 1, min.getY() + 1, min.getZ() + 1,
                    cellLevel,
                    offset.getX(), offset.getY(), offset.getZ(), targetX - 1, targetY - 1, targetZ - 1);

            return true;
        } finally {
            // clean up newly created dimensions that failed transfer
            if (this.getAllocatedPlotId(is) == -1) {
                manager.freePlot(plot.getId(), true);
            }
        }
    }

    public void setStoredDimension(ItemStack is, int plotId, BlockPos size) {
        final CompoundTag c = is.getOrCreateTag();
        c.putInt("plot_id", plotId);
        c.putLong("plot_size", size.asLong());
    }
}
