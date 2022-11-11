package com.julen.spacewars.Engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public enum Direction {
    Left(0, Color.CORAL, new Vector3(-1.0f, 0.0f, 0.0f)),
    Forward(1, Color.NAVY, new Vector3(0.0f, 0.0f, 1.0f)),
    Right(2, Color.SCARLET, new Vector3(1.0f, 0.0f, 0.0f)),
    Back(3, Color.ROYAL, new Vector3(0.0f, 0.0f, -1.0f)),
    Down(4, Color.FOREST, new Vector3(0.0f, -1.0f, 0.0f)),
    Up(5, Color.LIME, new Vector3(0.0f, 1.0f, 0.0f));

    public final int index;
    public final Color color;
    public final Vector3 vector;
    public String texture;

    Direction(int idx, Color color, Vector3 vector3) {
        this.index = idx;
        this.color = color;
        this.vector = vector3;

        texture = this.name() + ".png";
    }

    @Override
    public String toString() {
        return "Direction{" +
                "index=" + index +
                ", name=" + name() +
                ", vector=" + vector +
                ", texture='" + texture + '\'' +
                '}';
    }
}
