package com.julen.spacewars.Engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.graphics.GL20.*;

public class Object {
    private String id;
    private Color color;
    private final Vector3 position = new Vector3(0f, 0f, 0f);
    private final Quaternion rotation = new Quaternion(0f, 0f, 0f, 1f);
    private float scale = 1f;
    private final Matrix4 modelMatrix = new Matrix4(position, rotation, new Vector3(scale, scale, scale));
    private Mesh[] meshes;
    private Texture texture;

    public boolean cullface = false;
    public boolean depthtest = false;
    public boolean wireframe = false; //wireframe must be first mesh if true

    public Object(String id, Mesh mesh) {
        this(id, new Mesh[]{mesh}, Color.WHITE);
    }

    public Object(String id, Mesh[] meshes) {
        this(id, meshes, Color.WHITE);
    }

    public Object(String id, Mesh[] meshes, Color color) {
        this.id = id;
        this.meshes = meshes;
        this.color = color;
        this.texture = null;
    }

    public Object(String id, Mesh[] meshes, Texture texture) {
        this.id = id;
        this.meshes = meshes;
        this.color = Color.WHITE;
        this.texture = texture;
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(new Vector3(x, y, z));
        this.modelMatrix.set(this.position, this.rotation, new Vector3(scale, scale, scale));
    }

    public void setPosition(Vector3 pos) {
        this.position.set(pos);
        this.modelMatrix.set(pos, this.rotation, new Vector3(scale, scale, scale));
    }

    public void setRotation(float axisX, float axisY, float axisZ, float angle) {
        setRotation(new Quaternion(axisX, axisY, axisZ, angle));
    }

    public void setRotation(Vector3 axis, float angle) {
        setRotation(axis.x, axis.y, axis.z, angle);
    }

    public void setRotation(Quaternion q) {
        this.rotation.set(q);
        this.modelMatrix.set(this.position, q);
    }

    public void setRotation(Direction direction) {
        setRotation(Direction.Front.vector(), 0.0f);
    }

    public void setScale(float v) {
        this.scale = v;
    }

    public void render(ShaderProgram shader) {
        texture.bind(7);

        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        final int GL_TEXTURE_CUBE_MAP_SEAMLESS = 34895;
        gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        shader.setUniformf("u_textureMode", 1);
        shader.setUniformi("u_texture", 7);

        Matrix4 modelMatrix = new Matrix4().set(
                position, rotation, new Vector3(scale, scale, scale)
        );

        shader.setUniformMatrix("u_modelMatrix", modelMatrix);

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);

        for (int i = (this.wireframe ? 1 : 0); i < this.meshes.length; i++) {
            meshes[i].render(shader, GL20.GL_TRIANGLES);
        }
        gl.glDisable(GL_CULL_FACE);
        gl.glCullFace(GL_BACK);

        if (this.wireframe) {
            gl.glDepthFunc(GL_LEQUAL);
            shader.setUniformf("u_textureMode", 0);
            meshes[0].render(shader, GL20.GL_LINES);
        }
    }

}
