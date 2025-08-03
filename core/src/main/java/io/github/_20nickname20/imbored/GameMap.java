package io.github._20nickname20.imbored;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.JointEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public class GameMap {
    public static final String MAPS_PATH = Main.SAVE_ROOT + "maps/";

    private static final Json json = new Json();
    
    public final GameWorld world;
    public final String name;

    public static GameMap loadMap(GameWorld world, String name) {
        FileHandle handle = Gdx.files.external(MAPS_PATH + name + ".json");
        if (!handle.exists()) {
            return null;
        }

        GameMap.MapData data = json.fromJson(GameMap.MapData.class, handle.readString());
        if (data == null) {
            Gdx.app.error("Map", "Failed to load map data from " + handle.path());
            return null;
        }
        for (Entity.EntityData entityData : data.entityData) {
            Entity entity = Entity.createFromData(world, entityData);
            if (entity instanceof PlayerEntity) {
                continue; // Players are spawned in ServerWorld
            }
            world.spawn(entity);
        }
        for (JointEntity.JointData jointData : data.jointData) {
            JointEntity joint = new JointEntity(world, jointData);
            world.spawn(joint);
        }

        return new GameMap(world, name);
    }

    public void save() {
        GameMap.MapData data = this.createPersistentData();
        json.toJson(data, Gdx.files.external(MAPS_PATH + this.name + ".json"));
    }

    public GameMap(GameWorld world, String name) {
        this.world = world;
        this.name = name;
    }
    
    public MapData createPersistentData() {
        MapData mapData = new MapData();

        Array<Body> bodies = new Array<>(); 
        world.world.getBodies(bodies);
        Set<Entity> entities = new HashSet<>(bodies.size);
        for (Body body : bodies) {
            if (body.getUserData() instanceof Entity entity) {
                entities.add(entity);
            }
        }
        Set<UUID> uniqueUuids = new HashSet<>();
        entities.forEach(entity -> {
            if (Entity.getByUuid(entity.uuid) != null) {
                uniqueUuids.add(entity.uuid);
            }
        });
        Entity.EntityData[] entityData = new Entity.EntityData[uniqueUuids.size()];
        int i = 0;
        for (UUID uuid : uniqueUuids) {
            entityData[i] = Entity.getByUuid(uuid).createPersistentData();
            i++;
        }
        mapData.entityData = entityData;

        Array<Joint> jointsArray = new Array<>();
        world.world.getJoints(jointsArray);
        Set<JointEntity> joints = new HashSet<>(jointsArray.size);
        for (Joint joint : jointsArray) {
            if (joint.getUserData() instanceof JointEntity jointEntity) {
                joints.add(jointEntity);
            }
        }
        JointEntity.JointData[] jointData = new JointEntity.JointData[joints.size()];
        i = 0;
        for (JointEntity joint : joints) {
            jointData[i] = joint.createPersistentData();
            i++;
        }
        mapData.jointData = jointData;
        return mapData;
    }

    public static class MapData {
        public Entity.EntityData[] entityData;
        public JointEntity.JointData[] jointData;
    }
}
