package com.julen.spacewars.Engine;

import com.badlogic.gdx.math.Vector3;

public enum Direction {
    Right,
    Left,
    Up,
    Down,
    Back,
    Front;

    private final Vector3 rightVector = new Vector3(1.0f, 0.0f, 0.0f);
    private final Vector3 leftVector = new Vector3(-1.0f, 0.0f, 0.0f);
    private final Vector3 upVector = new Vector3(0.0f, 1.0f, 0.0f);
    private final Vector3 downVector = new Vector3(0.0f, -1.0f, 0.0f);
    private final Vector3 backVector = new Vector3(0.0f, 0.0f, 1.0f);
    private final Vector3 frontVector = new Vector3(0.0f, 0.0f, -1.0f);

    public Vector3 vector() {
        if (this == Right) return rightVector;
        if (this == Left) return leftVector;
        if (this == Up) return upVector;
        if (this == Down) return downVector;
        if (this == Back) return backVector;
        if (this == Front) return frontVector;

        return this.frontVector;
    }
}
