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

    public final float radius;

    public final Heightmap heightmap;

    public Planet(String id, float radius) {
        super(id);

        this.heightmap = new Heightmap(100, 100);

        this.radius = radius;
        this.meshes = new Mesh[6];

        this.up = new PlanetFace(this, Direction.Up, radius);
        this.down = new PlanetFace(this, Direction.Down, radius);

        this.left = new PlanetFace(this, Direction.Left, radius);
        this.right = new PlanetFace(this, Direction.Right, radius);

        this.forward = new PlanetFace(this, Direction.Forward, radius);
        this.back = new PlanetFace(this, Direction.Back, radius);

        this.meshes[0] = forward.mesh;
        this.meshes[1] = back.mesh;
        this.meshes[2] = left.mesh;
        this.meshes[3] = right.mesh;
        this.meshes[4] = up.mesh;
        this.meshes[5] = down.mesh;
    }

    @Override
    public void render(ShaderProgram shader) {
        forward.render(shader, modelMatrix);
        back.render(shader, modelMatrix);

        left.render(shader, modelMatrix);
        right.render(shader, modelMatrix);

        up.render(shader, modelMatrix);
        down.render(shader, modelMatrix);
    }
}
