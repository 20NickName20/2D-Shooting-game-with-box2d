package io.github._20nickname20.imbored.data;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import io.github._20nickname20.imbored.game_objects.Chunk;
import io.github._20nickname20.imbored.game_objects.Entity;

public class ChunkData {
    public Array<Entity> entities = new Array<>();
    public Array<JointData> joints = new Array<>();

    public ChunkData(Chunk chunk) {

    }

    public void addEntity(Entity entity) {
        
    }
}
