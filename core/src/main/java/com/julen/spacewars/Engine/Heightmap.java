package com.julen.spacewars.Engine;

import com.badlogic.gdx.graphics.Pixmap;

import java.nio.ByteBuffer;

public class Heightmap {
    private float[][][] maps;
    public final int width;
    public final int height;

    public Heightmap(int width, int height) {
        float f1 = 0, f2 = 0, f3 = 0;

        float min = 0;
        float max = 1;
        float wavelength = 35f;
        float range = max - min;
        float frequency = 1f / wavelength;

        int offsetX = 0;

        this.maps = new float[6][width][height];

        this.width = width;
        this.height = height;

        SimplexNoise n = new SimplexNoise(11);

        /*
        A circle can be defined as the locus of all points that satisfy the equations
x = r cos(t)    y = r sin(t)
where x,y are the coordinates of any point on the circle, r is the radius of the circle and
*/

        for (Direction d : Direction.sides) {
            for (int x = 0; x < width; x++) {

                for (int y = 0; y < height; y++) {
                    if (d == Direction.Left) {
                        f1 = x;
                        f2 = x;
                    } else if (d == Direction.Forward) {
                        f1 = width + x;
                        f2 = width;
                    } else if (d == Direction.Right) {
                        f1 = width * 2 - x;
                        f2 = width;
                    } else if (d == Direction.Back) {
                        f1 = width - x;
                        f2 = width - x;
                    }

                    f3 = y;

                    maps[d.index][x][y] = (float)
                            ((n.noise(f1 * frequency, f2 * frequency, f3 * frequency) + 1f) * 0.5f * range) + min;
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