package com.julen.spacewars.Engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.julen.spacewars.Builder;

import static com.badlogic.gdx.graphics.GL20.*;

public class PlanetFace {
    public final Vector3 direction = new Vector3();

    public final Mesh mesh;
    public final Mesh wireframe;

    private final Texture texture;
    private static float[][][][] noise;

    private Matrix4 modelMatrix = new Matrix4().idt();

    public PlanetFace(Direction direction) {
        this.direction.set(direction.vector());
        if (noise == null) {
            noise = new SimplexNoise(1).create4D(64, 15, -1, 1);
        }

        this.texture = null; //direction.texture();

        modelMatrix.setToTranslation(this.direction.cpy().scl(0.5f));

        if (this.direction.epsilonEquals(Direction.Right.vector())) {
            modelMatrix.rotate(Direction.upVector, 90f);
        }
        if (this.direction.epsilonEquals(Direction.Left.vector())) {
            modelMatrix.rotate(Direction.downVector, 90f);
        }
        if (this.direction.epsilonEquals(Direction.Up.vector())) {
            modelMatrix.rotate(Direction.leftVector, 90f);
        }
        if (this.direction.epsilonEquals(Direction.Down.vector())) {
            modelMatrix.rotate(Direction.rightVector, 90f);
        }
        if (this.direction.epsilonEquals(Direction.Forward.vector())) {
            modelMatrix.rotate(Direction.upVector, 180f);
        }

        Builder b = new Builder();
        b.hasColor = true;
        b.hasNormal = true;
        b.reset();

        b.rectangle2(0, 0, 0, 1, 1, direction.color(), 64);

        b.spherify(0, 0, -1, 1f, noise);

        Mesh[] meshes = b.build(true);

        this.wireframe = meshes[0];
        this.mesh = meshes[1];
    }

    public void render(ShaderProgram shader) {
        if (this.texture != null) {
            texture.bind(7);
            shader.setUniformf("u_textureMode", 1.0f);
        } else {
            shader.setUniformf("u_textureMode", 0.0f);
        }

        shader.setUniformi("u_texture", 7);
        shader.setUniformMatrix("u_modelMatrix", modelMatrix);
        shader.setUniformf("u_time", Gdx.graphics.getFrameId());

        Gdx.gl20.glEnable(GL_CULL_FACE);
        Gdx.gl20.glCullFace(GL_BACK);

        Gdx.gl20.glEnable(GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL_LESS);

        mesh.render(shader, GL_TRIANGLES);

        Gdx.gl20.glDepthFunc(GL_LEQUAL);

        shader.setUniformf("u_textureMode", -1.0f);

        final int GL_PROGRAM_POINT_SIZE = 0x8642;
        Gdx.gl.glEnable(GL_PROGRAM_POINT_SIZE);
        //wireframe.render(shader, GL_POINTS);
        Gdx.gl.glDisable(GL_PROGRAM_POINT_SIZE);

        //wireframe.render(shader, GL_LINES);
    }
}
