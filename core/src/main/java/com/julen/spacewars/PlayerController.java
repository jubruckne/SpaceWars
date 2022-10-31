package com.julen.spacewars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

public class PlayerController extends InputAdapter {
    protected final Camera camera;
    protected float velocity = 2.5f;
    protected float degreesPerPixel = 0.75f;
    protected final IntIntMap keys = new IntIntMap();
    protected final Vector3 tmp = new Vector3();
    protected final IEvents event_handler;
    private float mouse_deltaX;
    private float mouse_deltaY;

    public interface IEvents {
        void on_quit();
    }

    public PlayerController(Camera camera, IEvents event_handler) {
        this.camera = camera;
        this.event_handler = event_handler;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        final float dead_zone = 0.05f;

        mouse_deltaX = (Gdx.graphics.getWidth() / 2f - screenX) / Gdx.graphics.getWidth();
        mouse_deltaY = (Gdx.graphics.getHeight() / 2f - screenY) / Gdx.graphics.getHeight();

        if (Math.abs(mouse_deltaX) <= dead_zone)
            mouse_deltaX = 0f;

        if (Math.abs(mouse_deltaY) <= dead_zone)
            mouse_deltaY = 0f;

        mouse_deltaX *= 2.5f;
        mouse_deltaY *= 2.0f;

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        keys.put(keycode, keycode);

        if (keycode == Keys.ESCAPE) {
            this.event_handler.on_quit();
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        keys.remove(keycode, 0);
        return true;
    }

    /**
     * Sets the velocity in units per second for moving forward, backward and strafing left/right.
     *
     * @param velocity the velocity in units per second
     */
    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    /**
     * Sets how many degrees to rotate per pixel the mouse moved.
     *
     * @param degreesPerPixel
     */
    public void setDegreesPerPixel(float degreesPerPixel) {
        this.degreesPerPixel = degreesPerPixel;
    }

    public void update() {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float deltaTime) {
        if (keys.containsKey(Keys.W)) {
            tmp.set(camera.direction).nor().scl(deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(Keys.S)) {
            tmp.set(camera.direction).nor().scl(-deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(Keys.A)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(Keys.D)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * velocity);
            camera.position.add(tmp);
        }

        if (keys.containsKey(Keys.SHIFT_RIGHT)) {
            tmp.set(camera.up).nor().scl(deltaTime * velocity);
            camera.position.add(tmp);
        }
        if (keys.containsKey(Keys.SHIFT_LEFT)) {
            tmp.set(camera.up).nor().scl(-deltaTime * velocity);
            camera.position.add(tmp);
        }

        if (keys.containsKey(Keys.LEFT)) {
            camera.direction.rotate(camera.up, degreesPerPixel);
        }
        if (keys.containsKey(Keys.RIGHT)) {
            camera.direction.rotate(camera.up, -degreesPerPixel);
        }

        if (keys.containsKey(Keys.UP)) {
            tmp.set(camera.direction).crs(camera.up).nor();
            camera.direction.rotate(tmp, degreesPerPixel);
        }
        if (keys.containsKey(Keys.DOWN)) {
            tmp.set(camera.direction).crs(camera.up).nor();
            camera.direction.rotate(tmp, -degreesPerPixel);
        }

        if (keys.containsKey(Keys.Q)) {
            camera.rotate(camera.direction, -degreesPerPixel);
        }
        if (keys.containsKey(Keys.E)) {
            camera.rotate(camera.direction, degreesPerPixel);
        }

        if (mouse_deltaX != 0f) {
            camera.direction.rotate(camera.up, mouse_deltaX * degreesPerPixel);
        }
        if (mouse_deltaY != 0f) {
            tmp.set(camera.direction).crs(camera.up).nor();
            camera.direction.rotate(tmp, mouse_deltaY * degreesPerPixel);
        }

        camera.update(true);
    }
}
