package com.julen.spacewars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.julen.spacewars.Engine.GameObject;
import com.julen.spacewars.Engine.Planet;

import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.graphics.GL20.*;

public class World {
    private Grid grid = null;
    private final Array<GameObject> gameObjects = new Array<>();
    Cubemap cm;

    Texture texture;

    Planet planet;
    Planet planet2;
    Planet planet3;

    private final ShaderProgram shader;

    public World() {
        planet = new Planet("planet", 1f);

        planet2 = new Planet("Planet2", 0.45f);
        planet2.setPosition(.5f, 1, 1.25f);

        planet3 = new Planet("Planet3", 0.45f);
        planet3.setPosition(-.5f, 1, 1.25f);


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

        /*
        cm = new Cubemap(
                Gdx.files.internal("cubemap/teide/posx.jpg"),
                Gdx.files.internal("cubemap/teide/negx.jpg"),
                Gdx.files.internal("cubemap/teide/posy.jpg"),
                Gdx.files.internal("cubemap/teide/negy.jpg"),
                Gdx.files.internal("cubemap/teide/posz.jpg"),
                Gdx.files.internal("cubemap/teide/negz.jpg"));

*/
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

        texture = new Texture(PerlinNoiseGenerator.generatePixmap(1024, 1024, 0, 256, 10));

        Builder builder = new Builder();
        builder.reset();
        builder.hasNormal = true;
        builder.hasColor = true;

//        builder.cube(1f, Color.DARK_GRAY, 1);
        // builder.spherify(1f);
/*
        GameObject skybox = new GameObject("skybox",
                new Mesh[]{builder.build(), builder.build_wireframe()}, Color.BROWN);
        skybox.wireframe = false;
        skybox.setPosition(0, 0, 0);
        skybox.setScale(1.0f);
        gameObjects.add(skybox);

        builder.reset();
        //builder.rectangle(1, 1, Color.LIGHT_GRAY, 2);


        builder.triangle(new Vector3(-0.5f, -0.5f, 0.0f),
                new Vector3(0.5f, -0.5f, 0.0f),
                new Vector3(0.5f, 0.5f, 0.0f), Color.GREEN, 1);
*/

        /*
        mesh = builder.build();
        wf = builder.build_wireframe();

        skybox = new Object("skybox", new Mesh[]{wf, mesh}, texture);
        skybox.wireframe = true;
        skybox.setPosition(0.5f, 0.5f, 0.5f);
        skybox.setScale(2.0f);
        gameObjects.add(skybox);

        skybox = new Object("skybox", new Mesh[]{wf, mesh}, texture);
        skybox.wireframe = true;
        skybox.setPosition(0.5f, 0.5f, 0.75f);
        skybox.setScale(1.5f);
        gameObjects.add(skybox);
*/
/*
        builder.reset();
        builder.rectangle(1, 1, Color.TEAL, 2);
        GameObject skybox = new GameObject("old quad", builder.build());
        skybox.setPosition(0.55f, 0.55f, 0.0f);
        skybox.setScale(1.0f);
        gameObjects.add(skybox);

        Utils.log("old quad");
        builder.reset();
        builder.rectangle(1, 1, Color.FIREBRICK, 3);
        skybox = new GameObject("new quad", builder.build());
        skybox.setPosition(-0.55f, 0.55f, 0.0f);
        skybox.setScale(1.0f);
        gameObjects.add(skybox);

        Utils.log("new quad");

        builder.reset();
        builder.rectangle4(1, Color.MAROON, 46);
        skybox = new GameObject("new quad", builder.build());
        skybox.setPosition(-0.55f, -0.55f, 0.0f);
        skybox.setScale(1.0f);
        gameObjects.add(skybox);

        Utils.log("new quad 2");
*/

        /*
        builder.reset();
        builder.logging = true;
        builder.rectangle2(1f, Color.FOREST, 9);
        //builder.spherify(-0.5f, 0.5f, -0.5f, 1f);
        GameObject skybox = new GameObject("new quad", builder.build());
        skybox.setPosition(0, 1.5f, 0);
        skybox.setScale(1.0f);
        gameObjects.add(skybox);

        builder.reset();
        builder.logging = true;
        builder.cube2(1f, Color.FOREST, 9);
        //builder.spherify(-0.5f, 0.5f, -0.5f, 1f);
        skybox = new GameObject("new quad", builder.build());
        skybox.setPosition(0, 0f, 0);
        skybox.setScale(1.0f);
        gameObjects.add(skybox);
*/

        // planet2 = new Planet(1);
    }

    public void update() {
        if (grid != null)
            grid.update();
    }

    public void render(Camera camera) {
        shader.bind();

        gl.glDisable(GL_DEPTH_TEST);
        gl.glDisable(GL_CULL_FACE);
        gl.glCullFace(GL_BACK);

        shader.setUniformf("u_textureMode", 0);

        if (grid != null)
            grid.render(camera, shader);

        // #########

        // TextureAttribute attribute = (TextureAttribute) material.get(TextureAttribute.Diffuse);
        // attribute.textureDescription.texture.bind();
/*
        int texture = 3;
        gl.glActiveTexture(texture);

        //
        // gl.glBindTexture(GL_TEXTURE_CUBE_MAP, texture);
*/
/*
        int texture = 0;

        cm.bind(texture);
        shader.setUniformf("u_textureMode", 0);
        shader.setUniformi("u_textureCube", texture);
*/


        //skybox.render(shader, GL_TRIANGLES);

        planet.render(shader);
        planet2.render(shader);
        planet3.render(shader);

        for (GameObject gameObject : gameObjects) {
            gameObject.render(shader);
        }
    }
}
