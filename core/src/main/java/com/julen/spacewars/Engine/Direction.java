package com.julen.spacewars.Engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public enum Direction {
    Right,
    Left,
    Up,
    Down,
    Back,
    Forward;

    public static final Vector3 rightVector = new Vector3(1.0f, 0.0f, 0.0f);
    public static final Vector3 leftVector = new Vector3(-1.0f, 0.0f, 0.0f);
    public static final Vector3 upVector = new Vector3(0.0f, 1.0f, 0.0f);
    public static final Vector3 downVector = new Vector3(0.0f, -1.0f, 0.0f);
    public static final Vector3 backVector = new Vector3(0.0f, 0.0f, 1.0f);
    public static final Vector3 forwardVector = new Vector3(0.0f, 0.0f, -1.0f);

    public int ccw() {
        if (this == Back) return -1;
        if (this == Forward) return -1;

        if (this == Left) return 1;
        if (this == Right) return 1;

        if (this == Up) return 1;
        if (this == Down) return -1;

        return 1;
    }

    public Color color() {
        if (this == Right) return Color.FOREST;
        if (this == Left) return Color.LIME;

        if (this == Up) return Color.RED;
        if (this == Down) return Color.CORAL;

        if (this == Back) return Color.BLUE;
        if (this == Forward) return Color.ROYAL;

        return Color.WHITE;
    }

    public Vector3 vector() {
        if (this == Right) return rightVector.cpy();
        if (this == Left) return leftVector.cpy();
        if (this == Up) return upVector.cpy();
        if (this == Down) return downVector.cpy();
        if (this == Back) return backVector.cpy();
        if (this == Forward) return forwardVector.cpy();

        return this.forwardVector.cpy();
    }
}
