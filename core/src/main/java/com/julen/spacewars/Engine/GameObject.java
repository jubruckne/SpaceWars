package com.julen.spacewars.Engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import static com.badlogic.gdx.graphics.GL20.*;

public class GameObject {
    private String id;
    private Color color;
    protected final Vector3 position = new Vector3(0f, 0f, 0f);
    protected final Quaternion rotation = new Quaternion().idt();
    protected final Matrix4 modelMatrix = new Matrix4().idt();
    protected Mesh[] meshes;
    private Texture texture;

    public final Vector3 right = new Vector3(1f, 0f, 0f); // x+
    public final Vector3 left = new Vector3(-1f, 0f, 0f); // x-

    public final Vector3 up = new Vector3(0f, 1f, 0f);    // y+
    public final Vector3 down = new Vector3(0f, -1f, 0f); // y-

    public final Vector3 forward = new Vector3(0f, 0f, -1f); //z-
    public final Vector3 back = new Vector3(0f, 0f, 1f);   //z+

    public boolean cullface = false;
    public boolean depthtest = false;
    public boolean wireframe = false; //wireframe must be first mesh if true


    public GameObject(String id) {
        this.id = id;
        this.color = color;
    }

    public GameObject(String id, Mesh mesh) {
        this(id, new Mesh[]{mesh}, Color.WHITE);
    }

    public GameObject(String id, Mesh[] meshes) {
        this(id, meshes, Color.WHITE);
    }

    public GameObject(String id, Mesh[] meshes, Color color) {
        this(id);
        this.meshes = meshes;
        this.color = color;
        this.wireframe = meshes.length > 1;
    }

    public GameObject(String id, Mesh[] meshes, Texture texture) {
        this(id);
        this.meshes = meshes;
        this.texture = texture;
        this.wireframe = meshes.length > 1;
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(new Vector3(x, y, z));
        this.modelMatrix.setTranslation(x, y, z);
    }

    public void setPosition(Vector3 pos) {
        this.position.set(pos);
        this.modelMatrix.setTranslation(pos);
    }

    public void setRotation(float axisX, float axisY, float axisZ, float angle) {
        setRotation(new Quaternion(new Vector3(axisX, axisY, axisZ), angle));
    }

    public void setRotation(Vector3 axis, float angle) {
        setRotation(axis.x, axis.y, axis.z, angle);
    }

    public void setRotation(Quaternion q) {
        this.rotation.set(q);
        this.modelMatrix.set(this.position, q);
    }

    public void setRotation(Direction direction) {
        setRotation(Direction.Forward.vector, 0.0f);
    }

    public void render(ShaderProgram shader) {
        if (texture != null) {
            texture.bind(7);

            Gdx.gl20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            Gdx.gl20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            //final int GL_TEXTURE_CUBE_MAP_SEAMLESS = 34895;
            //gl.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
        }

        if (this.texture == null)
            shader.setUniformf("u_textureMode", 0.0f);
        else {
            shader.setUniformf("u_textureMode", 1.0f);
        }

        Matrix4 modelMatrix = new Matrix4().set(
                position, rotation
        );

        shader.setUniformi("u_texture", 7);
        shader.setUniformMatrix("u_modelMatrix", modelMatrix);
        shader.setUniformf("u_time", Gdx.graphics.getFrameId());

        Gdx.gl20.glEnable(GL_CULL_FACE);
        Gdx.gl20.glCullFace(GL_BACK);

        Gdx.gl20.glEnable(GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL_LESS);

        for (int i = (this.wireframe ? 1 : 0); i < this.meshes.length; i++) {
            meshes[i].render(shader, GL_TRIANGLES);
        }

        if (this.wireframe) {
            Gdx.gl20.glDisable(GL_CULL_FACE);
            Gdx.gl20.glDepthFunc(GL_LEQUAL);
            shader.setUniformf("u_textureMode", -1.0f);
            final int GL_PROGRAM_POINT_SIZE = 0x8642;

            Gdx.gl.glDisable(GL_PROGRAM_POINT_SIZE);
            meshes[0].render(shader, GL_LINES);

            Gdx.gl.glDisable(GL_DEPTH_TEST);
            Gdx.gl.glEnable(GL_PROGRAM_POINT_SIZE);
            meshes[0].render(shader, GL_POINTS);
        }
    }
}