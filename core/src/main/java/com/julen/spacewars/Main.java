package com.julen.spacewars;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.WindowedMean;
import com.badlogic.gdx.math.collision.Ray;

import static com.badlogic.gdx.graphics.GL20.*;

public class Main extends ApplicationAdapter implements GestureDetector.GestureListener, InputProcessor {
    PerspectiveCamera camera;
    OrthographicCamera camera_ui;
    private SpriteBatch batch_ui;
    private Map map;
    private Grid grid;

    private final WindowedMean fps_counter = new WindowedMean(60 * 5);

    private ModelBatch modelBatch;
    public Environment environment;

    boolean scroll_down = false;
    boolean scroll_up = false;
    boolean scroll_left = false;
    boolean scroll_right = false;

    private BitmapFont font;

    Planet planet;

    @Override
    public void create() {
        InputMultiplexer multiplexer = new InputMultiplexer(this, new GestureDetector(this));

        Gdx.input.setInputProcessor(multiplexer);

        font = new BitmapFont();
/*
		map = new Map("hightmap.png");
		map.double_sided = false;
*/

        map = new Map(15, 15);
		/*
		map.set_tile(2, 3, 2, 4,1.0f);
		map.set_tile(6, 0, 3, 9, 2.0f);
		map.set_tile(6, 0, 3, 8, 1.5f);
		map.set_tile(6, 0, 3, 7, 1.0f);
		map.set_tile(7, 0, 2, 4, 1.5f);
		map.set_tile(11,1,3, 4, -1.5f);
		*/

        camera = new PerspectiveCamera(67, 800, 600);
        camera.position.set(0, 0, 1.9f);
        camera.lookAt(0, 0, 0);
        //camera.rotate(map.right, 45);
        camera.near = 0.85f;
        camera.far = 10f;
        camera.update();

        camera_ui = new OrthographicCamera();
        camera_ui.setToOrtho(false, 800, 600);
        camera_ui.update();
        batch_ui = new SpriteBatch();
        batch_ui.setProjectionMatrix(camera_ui.combined);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.025f, 0.025f, 0.025f, 1f));

        DirectionalLight light = new DirectionalLight();
        light.setColor(1f, 1f, 1f, 1f);
        light.setDirection(new Vector3(1.5f, 0.15f, 0.0f).nor());
        environment.add(light);

        planet = new Planet(5);

        modelBatch = new ModelBatch();

        this.grid = new Grid(10, 10, 10);
    }

    @Override
    public void render() {
        update();

        //camController.update();
        camera.update();

        Gdx.gl20.glEnable(GL_CULL_FACE);
        Gdx.gl20.glCullFace(GL_FRONT);
        Gdx.gl20.glEnable(GL_DEPTH_TEST);
        Gdx.gl20.glDepthFunc(GL_LESS);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
/*
		modelBatch.begin(camera);
		map.render(modelBatch, environment);
		modelBatch.end();
*/
        grid.render(camera);

        planet.render(environment, camera);

        batch_ui.begin();
        font.getData().setScale(0.8f);
        font.draw(batch_ui, Utils.format("Screen: %.0f", get_mouse_pos_screen()), 10, 590);
        font.draw(batch_ui, Utils.format("World:  %.1f", get_mouse_pos_world()), 10, 575);
        font.draw(batch_ui, Utils.format("Camera:  %.1f, %.1f", camera.position, camera.direction), 10, 560);

        fps_counter.addValue((float) Gdx.graphics.getFramesPerSecond());
        font.draw(
                batch_ui,
                Utils.format("FPS:    %i (min: %.0f, max: %.0f, avg: %.1f)",
                        Gdx.graphics.getFramesPerSecond(),
                        fps_counter.getLowest(),
                        fps_counter.getHighest(),
                        fps_counter.getMean()), 10, 545);
        batch_ui.end();
    }

    private void update() {
        planet.transform.rotate(new Vector3(0, 1, 0), 0.15f);

        float scroll_speed = 8f * Gdx.graphics.getDeltaTime();
        // Utils.log("scrollspeed = %f", scroll_speed);

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            scroll_speed *= 10f;


        if (scroll_right || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.add(map.right.cpy().scl(scroll_speed));
        }

        if (scroll_left || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.sub(map.right.cpy().scl(scroll_speed));
        }

        if (scroll_up || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.add(map.up.cpy().scl(scroll_speed));
        }

        if (scroll_down || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.sub(map.up.cpy().scl(scroll_speed));
        }

        Vector3 center = new Vector3(0, 0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.Q))
            camera.rotateAround(center, map.up, 1);

        if (Gdx.input.isKeyPressed(Input.Keys.W))
            camera.rotateAround(center, map.up, -1);

        if (Gdx.input.isKeyPressed(Input.Keys.E))
            camera.rotateAround(center, map.right, 1);

        if (Gdx.input.isKeyPressed(Input.Keys.R))
            camera.rotateAround(center, map.right, -1);

        if (Gdx.input.isKeyPressed(Input.Keys.T))
            camera.rotateAround(center, new Vector3(0, 0, 1), 1);

        if (Gdx.input.isKeyPressed(Input.Keys.Y))
            camera.rotateAround(center, new Vector3(0, 0, 1), -1);

        if (Gdx.input.isKeyPressed(Input.Keys.Z)) {
            if (camera.position.z < 1000f)
                camera.position.add(map.into.cpy().scl(scroll_speed * 1.5f));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.X)) {
            if (camera.position.z > 5f)
                camera.position.sub(map.into.cpy().scl(scroll_speed * 1.5f));
        }
    }

    public Vector2 get_mouse_pos_screen() {
        Vector3 mouse = camera_ui.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        return new Vector2(mouse.x, mouse.y);
    }

    public Vector3 get_mouse_pos_world() {
        Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
        final float distance = -ray.origin.z / ray.direction.z;
        Vector3 pos = new Vector3().set(ray.direction).scl(distance).add(ray.origin);

        if (pos.x < 0 | pos.x >= map.width)
            return null;

        if (pos.y < 0 | pos.y >= map.height)
            return null;

        return pos;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.update();
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            Gdx.app.exit();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Vector3 panning = new Vector3();
        if (deltaX > 0)
            panning.x = -1;
        else if (deltaX < 0)
            panning.x = 1f;
        else
            panning.x = 0;

        if (deltaY > 0)
            panning.y = 1f;
        else if (deltaY < 0)
            panning.y = -1f;
        else
            panning.y = 0;

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            panning.scl(0.022f);

            camera.position.add(panning);

            return true;
        }

        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}