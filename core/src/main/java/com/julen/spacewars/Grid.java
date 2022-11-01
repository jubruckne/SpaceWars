package com.julen.spacewars;

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

    public Grid(float width, float height, float depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.transform = new Matrix4().idt();

        setup();
    }

    private void setup() {
        Builder builder = new Builder();
        builder.hasNormal = false;
        builder.hasColor = true;

        final float line_width = 0.001f;
        final float line_spacing = 0.25f;

        // x
        builder.cube(-2.5f, 0f, 0f, 5f, 0.005f, 0.005f, Color.GRAY);
        builder.cube(2.5f, 0f, 0f, 5f, 0.01f, 0.01f, Color.RED);

        for (float i = line_spacing; i <= 5f; i += line_spacing) {
            builder.cube(0f, 0f, -i, 10f, line_width * 0.5f, line_width, Color.DARK_GRAY);
            builder.cube(-2.5f, 0f, i, 5f, line_width * 0.5f, line_width, Color.DARK_GRAY);
            builder.cube(2.5f, 0f, i, 5f, line_width, line_width, Color.GRAY);
        }

        // y
        builder.cube(0, -2.5f, 0, 0.005f, 5f, 0.005f, Color.GRAY);
        builder.cube(0, 2.5f, 0, 0.01f, 5f, 0.01f, Color.GREEN);

        // z
        builder.cube(0, 0, -2.5f, 0.005f, 0.005f, 5f, Color.GRAY);
        builder.cube(0, 0, 2.5f, 0.01f, 0.01f, 5f, Color.BLUE);
        for (float i = line_spacing; i <= 5f; i += line_spacing) {
            builder.cube(-i, 0f, 0f, line_width, line_width * 0.5f, 10f, Color.DARK_GRAY);

            builder.cube(i, 0f, -2.5f, line_width, line_width * 0.5f, 5f, Color.DARK_GRAY);
            builder.cube(i, 0f, 2.5f, line_width, line_width, 5f, Color.GRAY);
        }


        this.mesh = builder.build();
    }

    public void update() {
    }

    public void render(Camera camera, ShaderProgram shader) {
        shader.bind();
        shader.setUniformMatrix("u_projTrans", camera.combined);
        shader.setUniformMatrix("u_modelTrans", transform);

        mesh.render(shader, GL20.GL_TRIANGLES);
    }

    @Override
    public void dispose() {
        if (this.mesh != null) {
            mesh.dispose();
            mesh = null;
        }
    }
}
