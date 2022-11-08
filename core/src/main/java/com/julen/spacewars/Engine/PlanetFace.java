package com.julen.spacewars.Engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.julen.spacewars.Builder;

import static com.badlogic.gdx.graphics.GL20.*;

public class PlanetFace {
    public final Vector3 position = new Vector3();
    public final Vector3 direction = new Vector3();
    public final Vector3 up = new Vector3();
    public final Mesh mesh;
    public final Mesh wireframe;

    public PlanetFace(Direction direction) {
        this.up.set(Direction.upVector);
        this.direction.set(direction.vector());
        this.position.add(direction.vector()).scl(0.5f);

        Builder b = new Builder();
        b.hasColor = true;
        b.hasNormal = true;

        b.rectangle2(0, 0, 0, direction.ccw(), direction.ccw(), direction.color(), 5);

        Mesh[] meshes = b.build(true);
        this.wireframe = meshes[0];
        this.mesh = meshes[1];
    }

    public void render(ShaderProgram shader) {
        shader.setUniformf("u_textureMode", 0.0f);

        Matrix4 modelMatrix = new Matrix4().idt();
        modelMatrix.setToWorld(this.position, this.direction, this.up);

        shader.setUniformi("u_texture", 7);
        shader.setUniformMatrix("u_modelMatrix", modelMatrix);
        shader.setUniformf("u_time", Gdx.graphics.getFrameId());

        Gdx.gl20.glEnable(GL_CULL_FACE);
        Gdx.gl20.glCullFace(GL_BACK);

        Gdx.gl20.glEnable(GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL_LESS);

        mesh.render(shader, GL_TRIANGLES);

        Gdx.gl20.glDisable(GL_CULL_FACE);
        Gdx.gl20.glDepthFunc(GL_LEQUAL);
        shader.setUniformf("u_textureMode", -1.0f);
        final int GL_PROGRAM_POINT_SIZE = 0x8642;

        Gdx.gl.glDisable(GL_PROGRAM_POINT_SIZE);
        wireframe.render(shader, GL_LINES);

        Gdx.gl.glDisable(GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL_PROGRAM_POINT_SIZE);
        wireframe.render(shader, GL_POINTS);
    }
}
