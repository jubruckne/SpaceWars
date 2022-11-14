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
    public final Mesh mesh;
    public final Mesh wireframe;
    public final Vector3 axis = new Vector3();
    public final Direction direction;

    private Texture texture;
    public final float radius;

    private Matrix4 modelMatrix = new Matrix4().idt();
    public final Planet planet;

    public final int width;
    public final int height;

    public final PlanetTile[][] tiles;

    public PlanetFace(Planet planet, Direction direction, float radius) {
        this.direction = direction;
        this.planet = planet;

        this.width = 100;
        this.height = 100;
        this.tiles = new PlanetTile[width][height];

        this.radius = radius;
        this.axis.set(direction.vector);

        modelMatrix.setToTranslation(direction.vector.cpy().scl(0.5f));

        if (direction == Direction.Right) {
            modelMatrix.rotate(Direction.Up.vector, 90f);
        }
        if (direction == Direction.Left) {
            modelMatrix.rotate(Direction.Down.vector, 90f);
        }
        if (direction == Direction.Up) {
            modelMatrix.rotate(Direction.Left.vector, 90f);
        }
        if (direction == Direction.Down) {
            modelMatrix.rotate(Direction.Right.vector, 90f);
        }
        if (direction == Direction.Back) {
            modelMatrix.rotate(Direction.Up.vector, 180f);
        }

        Builder b = new Builder();
        b.hasColor = false;
        b.hasNormal = true;
        b.reset();

        b.rectangle2(0, 0, 0f, 1, 1, direction.color, 100);
        b.spherify(0, 0, -0.50f, radius, null);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.tiles[x][y] = new PlanetTile(this, x, y);
                //this.tiles[x][y].model_pos.
            }
        }


        Mesh[] meshes = b.build(true);

        this.wireframe = meshes[0];
        this.mesh = meshes[1];
        this.texture = new Texture(planet.heightmap.getPixmap(direction));
    }

    public PlanetFace up() {
        return null;
    }

    public void render(ShaderProgram shader, Matrix4 planetMatrix) {
        if (this.texture != null) {
            texture.bind(7);
            shader.setUniformf("u_textureMode", 1.0f);
        } else {
            shader.setUniformf("u_textureMode", 0.0f);
        }
        Gdx.gl20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        Gdx.gl20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        shader.setUniformi("u_texture", 7);
        shader.setUniformMatrix("u_modelMatrix", planetMatrix.cpy().mul(modelMatrix));
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
