package io.github._20nickname20.imbored.render;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class PenRenderer extends SpriteBatch {
    Texture penTexture = new Texture("pen.png");
    TextureRegion pen = new TextureRegion(penTexture);
    float width = penTexture.getWidth() / 10f;
    float height = penTexture.getHeight() / 10f;

    public void lineAngle(float x, float y, float angle, float length) {
        //this.draw(pen, x, y, x, y, width, height, length / width, 1, angle);
    }

    public void line(float x1, float y1, float x2, float y2) {
        float distance = Vector2.dst(x1, y1, x2, y2);
        float angle = MathUtils.atan2(y2 - y1, x2 - x1);

        for (float i = 0; i < distance; i += penTexture.getWidth()) {
            //this.draw(penTexture, x1 + MathUtils.cos(angle) * i,y1 + MathUtils.sin(angle) * i);
        }
    }

    public void line(Vector2 start, Vector2 end) {
        lineAngle(start.x, start.y, end.cpy().sub(start).angleDeg(), start.dst(end));
    }
}
