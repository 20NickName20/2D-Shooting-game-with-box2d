package io.github._20nickname20.imbored.render;

import com.badlogic.gdx.graphics.Color;

public class JointDisplay {
    public final Color color;
    public boolean doRender = true;
    public JointDisplay(Color color) {
        this.color = color;
    }
    public JointDisplay(boolean doRender) {
        this.doRender = doRender;
        this.color = Color.BLACK;
    }
}
