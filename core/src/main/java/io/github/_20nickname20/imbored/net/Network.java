package io.github._20nickname20.imbored.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import io.github._20nickname20.imbored.game_objects.*;
import org.reflections.Reflections;

import java.util.*;

public class Network {
    public static int tcpPort = 37468;
    public static int udpPort = 37469;

    public static List<Class<?>> dataClasses = new ArrayList<>();

    static {
        Reflections reflections = new Reflections("io.github._20nickname20.imbored");
        dataClasses.addAll(reflections.getSubTypesOf(Entity.EntityData.class));
        dataClasses.addAll(reflections.getSubTypesOf(Item.ItemData.class));

        dataClasses.addAll(reflections.getSubTypesOf(ControlsPacket.class));

        dataClasses.sort(Comparator.comparingInt((Class<?> aClass) -> aClass.getName().hashCode()));
    }

    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        kryo.register(Login.class);
        kryo.register(LoginResponse.class);

        kryo.register(RequestChunk.class);

        kryo.register(ControlsPacket.class);
        kryo.register(Interact.class);

        kryo.register(Entity.EntityData.class);
        kryo.register(Entity.EntityData[].class);
        kryo.register(JointEntity.JointData.class);
        kryo.register(JointEntity.JointData[].class);
        kryo.register(float[].class);
        kryo.register(Item.ItemData.class);
        kryo.register(Item.ItemData[].class);
        kryo.register(Inventory.InventoryData.class);

        for (Class<?> clazz : dataClasses) {
            kryo.register(clazz);
            kryo.register(clazz.arrayType());
        }
    }

    public static class Login {
        public String username;
    }

    public static class LoginResponse {
        public String username;
        public String uuid;
    }

    public static class ControlsPacket {
        public String uuid;
    }

    public static class Move extends ControlsPacket {
        public float x;
    }
    public static class Jump extends ControlsPacket {}
    public static class SetCursorAngle extends ControlsPacket {
        public float angleRad;
    }

    public static class ToggleItem extends ControlsPacket {}
    public static class StartApplying extends ControlsPacket {}
    public static class StopApplying extends ControlsPacket {}

    public static class ScrollInventory extends ControlsPacket {
        public int amount;
    }
    public static class DoInteract extends ControlsPacket {
        public Interact action;
        public boolean state;
    }
    public static class GrabOrUse extends ControlsPacket {}
    public static class PutOrStopUse extends ControlsPacket {}
    public static class DropOrThrow extends ControlsPacket {}

    public static class RequestChunk {
        public int position;
    }
}
