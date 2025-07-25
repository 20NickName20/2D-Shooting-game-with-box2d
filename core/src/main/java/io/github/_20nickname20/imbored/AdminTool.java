package io.github._20nickname20.imbored;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github._20nickname20.imbored.game_objects.*;
import io.github._20nickname20.imbored.game_objects.entities.ContainerEntity;
import io.github._20nickname20.imbored.game_objects.entities.block.SimpleBlockEntity;
import io.github._20nickname20.imbored.game_objects.entities.container.locked_crate.parachute.AirdropCrate;
import io.github._20nickname20.imbored.game_objects.entities.statics.GroundEntity;
import io.github._20nickname20.imbored.game_objects.loot.TestRandomLoot;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.FindBody;
import io.github._20nickname20.imbored.util.Util;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdminTool {
    private static final List<Item> items = new ArrayList<>();
    private static final Material[] materials = {Material.WOOD, Material.CLOTH, Material.ROCK, Material.METAL, Material.GROUND, Material.FLESH};
    private static int materialMode = 0;
    private static FitViewport viewport;
    public static Random random = new Random();

    private static final SpriteBatch batch = new SpriteBatch();
    private static final BitmapFont font;

    static {
        Reflections reflections = new Reflections("io.github._20nickname20.imbored");
        for (Class<? extends Item> type : reflections.getSubTypesOf(Item.class)) {
            if (ClassReflection.isAbstract(type)) continue;
            items.add(Item.createFromType(type, null));
            // System.out.println(type.getSimpleName());
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PressStart2P-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 32;

        font = generator.generateFont(parameter);
        generator.dispose();



    }

    public static GameWorld world;
    public static boolean isEnabled = false;
    // Renders a slot
    static void renderSlot(GameRenderer renderer, boolean active, float time) {
        renderer.withRotation(-time * 2, () -> renderer.rect(-1.9f, -1.9f, 3.8f, 3.8f));
        renderer.withRotation(time * 10, () -> {
            renderer.rect(-2, -2, 4, 4);

            if (active) {
                for (float i = 0; i < 0.4f; i += 0.1f) {
                    renderer.rect(-2.5f + i, -2.5f + i, 5f - i * 2, 5f - i * 2);
                }
            }
        });
    }

    static void renderText(){
        batch.setProjectionMatrix(world.camera.combined);
        batch.setProjectionMatrix(world.camera.projection);
        font.getData().setScale(batch.getProjectionMatrix().getScaleY() * world.camera.viewportHeight / 7);

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, materials[materialMode].shortName, 66, 25);
        batch.end();
    }

    public static final float slotSize = 4.6f * 1.25f;
    public static int activeSlot = 0;


    public static void render(GameRenderer renderer, float dt) {
        if (!isEnabled) return;

        renderer.withTranslation(world.camera.position.x - 67f, world.camera.position.y + 37f, () -> {
            renderItems(renderer);
        });

        renderer.setColor(Color.OLIVE);
        for (Vector2 vertex : polygonVertices) {
            renderer.circle(vertex.x, vertex.y, 0.5f);
        }

        renderer.setColor(Color.WHITE);

        if (jointPosA != null) {
            renderer.circle(jointPosA.x, jointPosA.y, 1.4f);
            renderer.circle(jointPosA.x, jointPosA.y, 1.55f);
        }

        renderer.withTranslation(world.camera.position.x + 65, world.camera.position.y + 35, () -> {
            switch (mode) {
                case GRAB -> renderer.polygon(new float[] {
                    -2f, 4f,
                    -2f, 0.4f,
                    -1.05f, 0.92f,
                    -0.35f, -1.5f,
                    0.95f, -1.1f,
                    0.3f, 1.075f,
                    1.2f, 1.4f
                });
                case POLYGON -> renderer.polygon(new float[] {
                    -4f, -4f,
                    4f, -3.5f,
                    3.7f, 0.4f,
                    0f, 0f,
                    1f, 4f,
                    -3.3f, 3.9f
                });
                case JOINT -> {
                    renderer.circle(-3f, -3f, 1.3f);
                    renderer.line(-3f, -3f, 3f, 3f);
                    renderer.circle(3f, 3f, 1.3f);
                }
                case DELETE -> {
                    renderer.setColor(Color.RED);
                    renderer.line(-3.5f, -3.5f, 3.5f, 3.5f);
                    renderer.line(-3.5f, 3.5f, 3.5f, -3.5f);
                }
            }

            renderer.setColor(materials[materialMode].color);
            renderText();
            for (float r = 2.5f; r < 3.5f; r += 0.1f){
                renderer.circle(3, -10.6f, r);
            }

        });
    }

    public static void renderItems(GameRenderer renderer) {
        Random random = new Random(5_030_000);

        float time = Util.time();
        for (int i = 0; i < items.size(); i++) {
            int finalI = i;
            renderer.withTranslation((i % LINE_WIDTH) * slotSize, - (i / LINE_WIDTH) * slotSize, () -> {
                renderer.setColor(1f, 1f, 1f, 1f);
                renderSlot(renderer, finalI == activeSlot, time + random.nextFloat(360f));
                renderer.withRotation(-time * 30 + random.nextFloat(360f), () -> {
                    items.get(finalI).render(renderer, null);
                });
            });
        }
    }

    private static MouseJoint mouseJoint;
    private static Entity grabbedEntity;

    private static Vector2 jointPosA;



    private static Mode mode = Mode.GRAB;

    private static final List<Vector2> polygonVertices = new ArrayList<>();

    private static final int LINE_WIDTH = 20;

    private static Vector2 worldMousePosition = new Vector2();

    public static void onClickPressed(Vector2 worldPoint, Vector2 screenPoint, int button) {
        if (button == Input.Buttons.LEFT) {
            int index = (int) ((screenPoint.x + 67f) / slotSize + 0.5f);
            index += LINE_WIDTH * (int) (13f - (screenPoint.y + 37f) / slotSize + 0.5f);
            if (index < 0) return;
            if (index >= items.size()) return;
            activeSlot = index;
            world.dropItem(new Vector2(world.camera.position.x, world.camera.position.y), new Vector2(), Item.createFromType(items.get(index).getClass(), null));
        }
        if (button == Input.Buttons.RIGHT) {
            switch (mode) {
                case GRAB -> {
                    if (mouseJoint != null) return;
                    Body body = FindBody.atPoint(world.world, worldPoint);
                    if (body == null) return;
                    if (body.getType() == BodyDef.BodyType.StaticBody) return;
                    if (!(body.getUserData() instanceof Entity entity)) return;
                    grabbedEntity = entity;
                    MouseJointDef mouseJointDef = new MouseJointDef();
                    mouseJointDef.target.set(worldPoint);
                    mouseJointDef.bodyA = world.anyStaticBody;
                    mouseJointDef.bodyB = body;
                    mouseJointDef.maxForce = 15000;
                    mouseJointDef.collideConnected = true;
                    mouseJoint = (MouseJoint) world.world.createJoint(mouseJointDef);
                    mouseJoint.setTarget(worldPoint);
                }
                case POLYGON -> polygonVertices.add(worldPoint);
                case JOINT -> {
                    if (jointPosA == null) {
                        jointPosA = worldPoint.cpy();
                    } else {
                        JointEntity joint = new JointEntity(world, Color.CYAN, jointPosA, worldPoint, 5f, 0.5f);
                        joint.spawn();
                        jointPosA = null;
                    }
                }
                case DELETE -> {
                    Body body = FindBody.atPoint(world.world, worldPoint);
                    if (body == null) return;
                    if (!(body.getUserData() instanceof Entity entity)) return;
                    entity.remove();
                }
            }
        }
    }

    public static void onClickReleased(Vector2 worldPoint, Vector2 screenPoint, int button) {
        if (button == Input.Buttons.RIGHT) {
            if (mouseJoint == null) return;
            if (!grabbedEntity.isRemoved()) world.world.destroyJoint(mouseJoint);
            mouseJoint = null;
            grabbedEntity = null;
        }
    }

    public static void onMouseMove(Vector2 worldPoint, Vector2 screenPoint) {
        if (mouseJoint != null) {
            mouseJoint.setTarget(worldPoint);
        }
        worldMousePosition.set(worldPoint);
    }

    public static LootGenerator testLootGenerator = new TestRandomLoot();

    public static void keyPressed(int key) {
        switch (key) {
            case Input.Keys.A -> {
                float halfSide = 4.5f;
                ContainerEntity crate = new AirdropCrate(
                    world, world.camera.position.x, world.camera.position.y + 30f,
                    halfSide, halfSide,
                    random.nextFloat(halfSide * 2f) - halfSide,
                    random.nextFloat(halfSide * 2f) - halfSide
                );
                crate.getInventory().addAll(testLootGenerator.generate(10));
                crate.spawn();
            }
            case Input.Keys.F -> world.setFrozen(!world.isFrozen());
            case Input.Keys.R -> mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length];
            case Input.Keys.M -> materialMode = (materialMode + 1) % materials.length;
            case Input.Keys.LEFT -> {
                activeSlot--;
                if (activeSlot < 0) {
                    activeSlot = items.size() - 1 - activeSlot;
                }
            }
            case Input.Keys.RIGHT -> {
                activeSlot++;
                activeSlot = activeSlot % items.size();
            }
            case Input.Keys.NUM_1 -> mode = Mode.GRAB;
            case Input.Keys.NUM_2 -> mode = Mode.POLYGON;
            case Input.Keys.NUM_3 -> mode = Mode.JOINT;
            case Input.Keys.NUM_4 -> mode = Mode.DELETE;
            case Input.Keys.ENTER -> {
                if (mode == Mode.GRAB) {
                    world.dropItem(worldMousePosition, new Vector2(), Item.createFromType(items.get(activeSlot).getClass(), null));
                }

                if (mode == Mode.POLYGON) {
                    if (polygonVertices.size() >= 3) {
                        float[] vertices = new float[polygonVertices.size() * 2];
                        int i = 0;
                        Vector2 center = new Vector2();
                        for (Vector2 vertex : polygonVertices) {
                            center.add(vertex);
                        }
                        center.scl(1f / polygonVertices.size());
                        for (Vector2 vertex : polygonVertices) {
                            vertices[i * 2] = vertex.x - center.x;
                            vertices[i * 2 + 1] = vertex.y - center.y;
                            i++;
                        }

                        PolygonShape shape = new PolygonShape();
                        shape.set(vertices);
                        if (Util.getArea(shape) > 0.1f) {
                            if (materials[materialMode] == Material.GROUND) {
                                world.spawn(new GroundEntity(world, center.x, center.y, shape));
                            } else {
                                world.spawn(new SimpleBlockEntity(world, center.x, center.y, shape, materials[materialMode]));
                            }
                        }
                    }
                    polygonVertices.clear();
                }
            }
        }
    }

    enum Mode {
        GRAB,
        POLYGON,
        JOINT,
        DELETE
    }
}
