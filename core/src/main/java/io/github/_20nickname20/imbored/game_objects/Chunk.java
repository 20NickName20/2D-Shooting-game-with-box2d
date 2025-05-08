package io.github._20nickname20.imbored.game_objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.data.ChunkData;
import io.github._20nickname20.imbored.game_objects.entities.BlockEntity;
import io.github._20nickname20.imbored.game_objects.entities.StaticEntity;
import io.github._20nickname20.imbored.game_objects.entities.block.box.MetalBoxEntity;
import io.github._20nickname20.imbored.game_objects.entities.block.box.WoodenBoxEntity;
import io.github._20nickname20.imbored.game_objects.entities.block.circle.RockCircleEntity;
import io.github._20nickname20.imbored.game_objects.entities.container.CrateEntity;
import io.github._20nickname20.imbored.game_objects.entities.container.crate.WoodenCrateEntity;
import io.github._20nickname20.imbored.game_objects.entities.statics.GroundEntity;
import io.github._20nickname20.imbored.game_objects.loot.supply.AmmoSupplyLoot;
import io.github._20nickname20.imbored.game_objects.loot.supply.GunSupplyLoot;
import io.github._20nickname20.imbored.game_objects.loot.supply.HealSupplyLoot;
import io.github._20nickname20.imbored.game_objects.loot.supply.StuffSupplyLoot;

import java.util.Set;

import static io.github._20nickname20.imbored.GameWorld.CHUNK_WIDTH;

public class Chunk {
    public final int position;
    public final GameWorld world;
    private float leftLevel;
    private float rightLevel;

    public Chunk left = null, right = null;

    private boolean isGenerated = false;

    public Chunk(GameWorld world, int position) {
        this.world = world;
        this.position = position;
    }

    public Chunk(GameWorld world, int position, ChunkData data) {
        this.world = world;
        this.position = position;
        this.leftLevel = data.leftLevel;
        this.rightLevel = data.rightLevel;
        this.isGenerated = true;
        for (Entity.EntityData entityData : data.entityData) {
            world.spawn(Entity.createFromData(world, entityData));
        }
        for (JointEntity.JointData jointData : data.jointData) {
            world.spawn(new JointEntity(world, jointData));
            int x1 = world.getChunkPosition(jointData.x1);
            int x2 = world.getChunkPosition(jointData.x2);
            if (x1 != position) {
                if (!world.isChunkLoaded(x1)) {
                    world.loadChunk(x1);
                }
            } else if (x2 != position) {
                if (!world.isChunkLoaded(x2)) {
                    world.loadChunk(x2);
                }
            }
        }
    }

    public float getLeftLevel() {
        return leftLevel;
    }

    public float getRightLevel() {
        return rightLevel;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    private static final float maxOffset = 20f;

    public void generate(Chunk leftChunk, Chunk rightChunk) {
        if (isGenerated) return;
        isGenerated = true;

        float leftLevel, rightLevel;

        if (leftChunk == null && rightChunk == null) {
            if (MathUtils.randomBoolean()) {
                rightLevel = MathUtils.random(0f, 100f);
                leftLevel = rightLevel;
            } else {
                leftLevel = MathUtils.random(0f, 100f);
                rightLevel = MathUtils.random(0f, 100f);
            }
        } else if (leftChunk != null && rightChunk != null) {
            leftLevel = leftChunk.getRightLevel();
            rightLevel = rightChunk.getLeftLevel();
        } else if (leftChunk != null) {
            leftLevel = leftChunk.getRightLevel();
            if (MathUtils.randomBoolean()) {
                rightLevel = leftLevel;
            } else {
                rightLevel = leftLevel + MathUtils.random(-maxOffset, maxOffset);
            }
        } else {
            rightLevel = rightChunk.getLeftLevel();
            if (MathUtils.randomBoolean()) {
                leftLevel = rightLevel;
            } else {
                leftLevel = rightLevel + MathUtils.random(-maxOffset, maxOffset);
            }
        }

        leftLevel = MathUtils.clamp(leftLevel, 0f, 100f);
        rightLevel = MathUtils.clamp(rightLevel, 0f, 100f);

        if (leftLevel == rightLevel) {
            LootGenerator gunLoot = new GunSupplyLoot();
            LootGenerator healLoot = new HealSupplyLoot();
            LootGenerator stuffLoot = new StuffSupplyLoot();
            LootGenerator ammoLoot = new AmmoSupplyLoot();

            for (float x = -1; x <= 1; x++) {
                for (int i = -2; i <= 2; i++) {
                    if (MathUtils.random(2) > 0) continue;
                    int height = MathUtils.random(1, (int) (10f - (float) Math.sin(x / 10) * 7f));
                    for (int y = 0; y < height; y++) {
                        boolean type = MathUtils.randomBoolean();
                        if (type) {
                            world.spawn(new WoodenBoxEntity(world, position * CHUNK_WIDTH + x * 60 + MathUtils.random(0, 0.25f) + i * 6f, leftLevel + 2 + y * 4, 2, 2));
                        } else {
                            world.spawn(new MetalBoxEntity(world, position * CHUNK_WIDTH + x * 60 + MathUtils.random(0, 0.25f) + i * 6f, leftLevel + 2 + y * 4, 2, 2));
                        }
                    }
                }

                CrateEntity entity = new WoodenCrateEntity(world, position * CHUNK_WIDTH + x * 60 + 30, leftLevel + 3.5f, 3.5f, 3.5f);
                LootGenerator lootGenerator = switch (MathUtils.random(3)) {
                    case 0 -> gunLoot;
                    case 1 -> healLoot;
                    case 2 -> ammoLoot;
                    default -> stuffLoot;
                };

                entity.getInventory().addAll(lootGenerator.generate(1));
                world.spawn(entity);

                if (MathUtils.randomBoolean()) {
                    BlockEntity circle = new RockCircleEntity(world, position * CHUNK_WIDTH + x * 60 + 30, leftLevel + 2.5f + 7f, 2.5f);
                    world.spawn(circle);
                }
            }
        }

        PolygonShape shape = new PolygonShape();
        shape.set(new float[] {
            -CHUNK_WIDTH / 2, -25,
            -CHUNK_WIDTH / 2, leftLevel,
            CHUNK_WIDTH / 2, rightLevel,
            CHUNK_WIDTH / 2, -25,
        });
        this.leftLevel = leftLevel;
        this.rightLevel = rightLevel;
        StaticEntity ground = new GroundEntity(world, position * CHUNK_WIDTH, 0, shape);
        world.spawn(ground);
    }

    public ChunkData createPersistentData() {
        ChunkData chunkData = new ChunkData();
        chunkData.leftLevel = this.leftLevel;
        chunkData.rightLevel = this.rightLevel;

        Set<Entity> entities = world.getEntitiesInChunk(position);
        Entity.EntityData[] entityData = new Entity.EntityData[entities.size()];
        int i = 0;
        for (Entity entity : entities) {
            entityData[i] = entity.createPersistentData();
            i++;
        }
        chunkData.entityData = entityData;

        Set<JointEntity> joints = world.getJointsInChunk(position);
        JointEntity.JointData[] jointData = new JointEntity.JointData[joints.size()];
        i = 0;
        for (JointEntity joint : joints) {
            jointData[i] = joint.createPersistentData();
            i++;
        }
        chunkData.jointData = jointData;
        return chunkData;
    }

    public static class ChunkData {
        public Entity.EntityData[] entityData;
        public JointEntity.JointData[] jointData;
        float rightLevel, leftLevel;
    }
}
