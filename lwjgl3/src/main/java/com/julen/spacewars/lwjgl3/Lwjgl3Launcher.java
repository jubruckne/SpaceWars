package com.julen.spacewars.lwjgl3;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.julen.spacewars.Main;

/**
 * Launches the desktop (LWJGL3) application.
 */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("SpaceWars");
        configuration.useVsync(true);
        configuration.enableGLDebugOutput(true, System.out);
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2);
        //// Limits FPS to the refresh rate of the currently active monitor.
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
        configuration.setWindowedMode(1240, 768);
        configuration.setWindowPosition(10, 40);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        configuration.setBackBufferConfig(8, 8, 8, 8, 8, 0, 4);
        Graphics.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
        //configuration.setMaximized(true);
        // configuration.setOpenGLEmulation(GL30, 3, 2);

		/*
		Graphics.Monitor monitor = Lwjgl3ApplicationConfiguration.getMonitors()[0];
		Graphics.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayModes(monitor)[1];

		configuration.setMaximizedMonitor(monitor);

		configuration.setFullscreenMode(displayMode);
		configuration.setMaximized(true);
		*/
        configuration.setForegroundFPS(displayMode.refreshRate);

        return configuration;
    }
}