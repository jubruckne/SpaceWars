package com.julen.spacewars;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;

public class Grid implements Disposable {
    public final float width;
    public final float height;
    public final float depth;
    public final Matrix4 transform;

    private short stride;
    private float[] vertices;
    private short vert_pos;
    private short verts;
    private short[] triangles;
    private short tri_pos;
    private short tris;

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
        vertices = new float[1000];
        verts = 0;
        vert_pos = 0;
        triangles = new short[1000];
        tris = 0;
        tri_pos = 0;
        stride = 10; // 3 pos, 3 norm, 4 color

        this.mesh = new Mesh(true, vert_pos, tri_pos,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE));
        mesh.setVertices(vertices, 0, vert_pos);
        mesh.setIndices(triangles, 0, tri_pos);

        Utils.log("%s: Vertices = %i, Triangles = %i", this.getClass().getSimpleName(), (int) verts, (int) tris);
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
