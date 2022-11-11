package com.julen.spacewars.Engine;

import com.badlogic.gdx.graphics.Pixmap;

import java.nio.ByteBuffer;

public class Heightmap {
    private float[][][] maps;
    public final int width;
    public final int height;

    public Heightmap(int width, int height, int seed) {
        float f1 = 0, f2 = 0, f3 = 0, f4 = 0;

        float min = 0;
        float max = 1;
        float wavelength = 55f;
        float range = max - min;
        float frequency = 1f / wavelength;

        float ka = seed + frequency;
        float kb = ka * width - frequency;
        float kc = kb * (height + width) + frequency;

        int offsetX = 0;

        this.maps = new float[6][width][height];

        this.width = width;
        this.height = height;

        SimplexNoise n = new SimplexNoise(seed);

        for (Direction d : Direction.sides) {
            for (int x = 0; x < width; x++) {
                float fNX = (float) (x + offsetX) / (float) (width * 4f);
                float fRdx = (float) (fNX * 2f * Math.PI);
                f1 = (float) (height * .55f * Math.sin(fRdx));
                f2 = (float) (height * .55f * Math.cos(fRdx));

                for (int y = 0; y < height; y++) {
                    f3 = y;

                    maps[d.index][x][y] = (float)
                            ((n.noise(ka + f1 * frequency, kb + f2 * frequency, kc + f3 * frequency) + 1f) * 0.5f * range) + min;
                }
            }

            offsetX += width;
        }
    }

    public Pixmap getPixmap(Direction d) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        ByteBuffer pixels = pixmap.getPixels();
        int idx1 = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                byte value = (byte) (maps[d.index][x][y] * 255f);
                pixels.put(idx1, new byte[]{value, value, value, (byte) 255});
                idx1 += 4;
            }
        }

        return pixmap;
    }
}