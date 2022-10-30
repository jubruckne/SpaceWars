package com.julen.spacewars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;

public class Grid implements Disposable {
    public final float width;
    public final float height;
    public final float depth;
    public final Matrix4 transform;

    private Mesh mesh;
    private ShaderProgram shader;

    public Grid(float width, float height, float depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.transform = new Matrix4().idt();

        setup();
    }

    private void setup() {
        ShaderProgram.pedantic = true;
        shader = new ShaderProgram(
                Gdx.files.internal("shaders/grid.vert.glsl"),
                Gdx.files.internal("shaders/grid.frag.glsl"));

        if (!shader.isCompiled()) {
            Utils.log("shader not compiled!");
            Utils.log("Shader Log: %s", shader.getLog());
        }

        Builder builder = new Builder();

        builder.cube(-2.5f, 0, 0, 5f, 0.005f, 0.005f, Color.LIGHT_GRAY);
        builder.cube(2.5f, 0, 0, 5f, 0.01f, 0.01f, Color.RED);

        builder.cube(0, -2.5f, 0, 0.005f, 5f, 0.005f, Color.LIGHT_GRAY);
        builder.cube(0, 2.5f, 0, 0.01f, 5f, 0.01f, Color.GREEN);

        builder.cube(0, 0, -2.5f, 0.005f, 0.005f, 5f, Color.LIGHT_GRAY);
        builder.cube(0, 0, 2.5f, 0.01f, 0.01f, 5f, Color.BLUE);

        /*
        builder.cube(0.5f, 0.5f, 0.5f, 0.25f, 0.25f, 0.25f, Color.YELLOW);
        builder.cube(0.8f, 0.8f, 0.8f, 0.25f, 0.25f, 0.25f, Color.PINK);
        builder.cube(1.1f, 1.1f, 1.1f, 0.25f, 0.25f, 0.25f, Color.PURPLE);
    ^   */
        this.mesh = builder.build();
    }

    public void update() {

    }

    public void render(Camera camera) {
        shader.bind();
        shader.setUniformMatrix("u_projTrans", camera.combined);
        shader.setUniformMatrix("u_modelTrans", transform);

        mesh.render(shader, GL20.GL_TRIANGLES);
    }

    @Override
    public void dispose() {
        if (this.mesh != null) {
            mesh.dispose();
            shader.dispose();

            mesh = null;
            shader = null;
        }
    }
}
