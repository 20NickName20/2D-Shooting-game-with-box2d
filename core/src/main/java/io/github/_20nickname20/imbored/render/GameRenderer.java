package io.github._20nickname20.imbored.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public interface GameRenderer {
    void line(float x1, float y1, float x2, float y2);

    default void line(Vector2 point1, Vector2 point2) {
        line(point1.x, point1.y, point2.x, point2.y);
    }

    default void rect(float x, float y, float width, float height) {
        float x2 = x + width;
        float y2 = y + height;
        line(x, y, x2, y);
        line(x, y2, x2, y2);
        line(x, y, x, y2);
        line(x2, y, x2, y2);
    }

    default void polygon(float[] vertices) {
        if (vertices.length % 2 != 0) throw new RuntimeException("Vertices array should have even amount of elements!");
        for (int i = 0; i < vertices.length; i += 2) {
            float x1 = vertices[i];
            float y1 = vertices[i + 1];
            float x2 = vertices[(i + 2) % vertices.length];
            float y2 = vertices[(i + 3) % vertices.length];
            line(x1, y1, x2, y2);
        }
    }

    void saveTransform();
    void loadTransform();
    void resetTransform();

    void setColor(float r, float g, float b, float a);
    void setColor(float r, float g, float b);
    void setColor(Color color);

    void render(Runnable runnable);

    void dispose();

    void withTranslation(float x, float y, Runnable runnable);
    void withRotation(float angle, Runnable runnable);

    void circle(float x, float y, float radius);
    void circle(float x, float y, float radius, int segments);

    void drawTexture(float x, float y, TextureRegion textureRegion);
    void drawText(float x, float y, String text);
}
