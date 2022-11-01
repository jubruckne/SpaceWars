package com.julen.spacewars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.graphics.GL20.*;

public class World {
    private final Grid grid;
    //private final Material material;
    private Mesh skybox;
    //private Planet planet2;
    Cubemap cm;

    Texture texture;

    private final ShaderProgram shader;

    public World() {
        ShaderProgram.pedantic = true;
        shader = new ShaderProgram(
                Gdx.files.internal("shaders/grid.vert.glsl"),
                Gdx.files.internal("shaders/grid.frag.glsl"));

        if (!shader.isCompiled()) {
            Utils.log("shader not compiled!");
            Utils.log("Shader Log: %s", shader.getLog());
            throw new IllegalArgumentException(shader.getLog());
        }


        // this.material = new Material(
        //        TextureAttribute.createDiffuse(new Texture("cubemap/sh_up.png")));

/*
        cm = new Cubemap(
                Gdx.files.internal("cubemap/sh_rt.png"),
                Gdx.files.internal("cubemap/sh_lf.png"),
                Gdx.files.internal("cubemap/sh_up.png"),
                Gdx.files.internal("cubemap/sh_dn.png"),
                Gdx.files.internal("cubemap/sh_bk.png"),
                Gdx.files.internal("cubemap/sh_ft.png"));
*/
        cm = new Cubemap(
                Gdx.files.internal("cubemap/teide/posx.jpg"),
                Gdx.files.internal("cubemap/teide/negx.jpg"),
                Gdx.files.internal("cubemap/teide/posy.jpg"),
                Gdx.files.internal("cubemap/teide/negy.jpg"),
                Gdx.files.internal("cubemap/teide/posz.jpg"),
                Gdx.files.internal("cubemap/teide/negz.jpg"));


/*
        cm = new Cubemap(
                Gdx.files.internal("cubemap/grid_posx.png"),
                Gdx.files.internal("cubemap/grid_negx.png"),
                Gdx.files.internal("cubemap/grid_posy.png"),
                Gdx.files.internal("cubemap/grid_negy.png"),
                Gdx.files.internal("cubemap/grid_posz.png"),
                Gdx.files.internal("cubemap/grid_negz.png"));
*/
        this.grid = new Grid(10f, 10f, 10f);

        texture = new Texture(
                OpenSimplex2.generatePixmap(256, 256, 0.0f, 0.25f, 1)
        );

        Builder builder = new Builder();
        builder.hasNormal = false;
        builder.hasColor = true;
        builder.reset();
        builder.cube(0, 0, 0f, 1f, 1f, 1f, Color.YELLOW, 3);
        builder.spherify(0, 0, 0f, 1f);
        skybox = builder.build();

        // planet2 = new Planet(1);
    }

    public void update() {
        grid.update();
    }

    public void render(Camera camera) {
        shader.bind();

        gl.glDisable(GL_CULL_FACE);
        gl.glCullFace(GL_BACK);

        shader.setUniformf("u_textureMode", 0);


        grid.render(camera, shader);


        // #########

        // TextureAttribute attribute = (TextureAttribute) material.get(TextureAttribute.Diffuse);
        // attribute.textureDescription.texture.bind();
/*
        int texture = 3;
        gl.glActiveTexture(texture);

        //gl20.glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS).

        // gl.glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
*/
/*
        int texture = 0;

        cm.bind(texture);
        shader.setUniformf("u_textureMode", 0);
        shader.setUniformi("u_textureCube", texture);

*/

        texture.bind(7);

        shader.setUniformf("u_textureMode", 1);
        shader.setUniformi("u_texture", 7);

        gl.glEnable(GL_CULL_FACE);
        gl.glCullFace(GL_BACK);

        skybox.render(shader, GL_TRIANGLES);


        gl.glEnable(GL_DEPTH_TEST);
        //planet2.render(null, camera);
    }
}