package com.julen.spacewars.Engine;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Planet extends GameObject {
    public final PlanetFace forward;
    public final PlanetFace back;
    public final PlanetFace left;
    public final PlanetFace right;
    public final PlanetFace up;
    public final PlanetFace down;

    public Planet(String id) {
        super(id);

        this.meshes = new Mesh[6];

        this.forward = new PlanetFace(Direction.Forward);
        this.back = new PlanetFace(Direction.Back);
        this.left = new PlanetFace(Direction.Left);
        this.right = new PlanetFace(Direction.Right);
        this.up = new PlanetFace(Direction.Up);
        this.down = new PlanetFace(Direction.Down);

        this.meshes[0] = forward.mesh;
        this.meshes[1] = back.mesh;
        this.meshes[2] = left.mesh;
        this.meshes[3] = right.mesh;
        this.meshes[4] = up.mesh;
        this.meshes[5] = down.mesh;
    }

    @Override
    public void render(ShaderProgram shader) {
        forward.render(shader);
        back.render(shader);
        left.render(shader);
        right.render(shader);
        up.render(shader);
        down.render(shader);
    }
}
