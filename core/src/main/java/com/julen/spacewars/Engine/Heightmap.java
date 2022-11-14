package com.julen.spacewars.Engine;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.julen.spacewars.Utils;

import java.nio.ByteBuffer;

public class Heightmap {
    private float[][][] maps;
    public final int width;
    public final int height;
    public final int octaves;
    public final int seed;

    public Heightmap(int width, int height, int seed, int octaves) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.octaves = octaves;
        this.maps = new float[6][width][height];
    }

    private void build(Direction d, Mesh mesh) {
        //mesh.getVertices();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //n.noise(ka + f1 * o_freq, kb + f2 * o_freq, kc + f3 * o_freq);
            }
        }
    }

    private void build_x() {
        float f1 = 0, f2 = 0, f3 = 0, f4 = 0;

        float min = 0;
        float max = 1;
        float wavelength = 59f;
        float range = max - min;
        float frequency = 1f / wavelength;
        float noise = 0f;

        float kc = seed % 11;
        float ka = kc + seed % 13;
        float kb = ka + seed % 17;

        int offsetX = 0;

        float fade = 1;

        Utils.log("new simplex with seed %i", seed);
        SimplexNoise n = new SimplexNoise(seed);

        for (Direction d : Direction.sides) {
            for (int x = 0; x < width; x++) {
                float fNX = (float) (x + offsetX) / (float) (width * 4f);
                float fRdx = (float) (fNX * 2f * Math.PI);
                f1 = (float) (height * Math.sin(fRdx));
                f2 = (float) (height * Math.cos(fRdx));
                fade = 0f;

                for (int y = 0; y < height * 2; y++) {
                    f3 = y;

                    noise = 0;
                    float o_freq = frequency;
                    float o_strengh = 1;
                    float o_div = 0;

                    for (int o = 0; o < octaves; o++) {
                        if (o % 2 == 0) {
                            noise += o_strengh * n.noise(ka + f1 * o_freq, kb + f2 * o_freq, kc + f3 * o_freq);
                        } else {
                            noise += o_strengh * n.noise(ka + f3 * o_freq, kb + f2 * o_freq, kc + f1 * o_freq);
                        }

                        o_freq *= 2f;
                        o_div += o_strengh;
                        o_strengh *= 0.5;
                    }

                    noise /= o_div;

                    noise = (noise + 1f) * 0.5f * range + min;
                    Utils.log("y=%i", y);


                    maps[d.index][x][y] = noise;
                }
            }

            offsetX += width;
        }
/*
        float percent_side = 1f;

        // north polar cap
        for (int x = 0; x < width; x++) {
            float percent_forward = 1f;

            for (int y = height / 2 - 1; y >= 0; y--) {
                maps[Direction.Up.index][x][y + height] = maps[Direction.Forward.index][x][y] * percent_forward;
                maps[Direction.Up.index][x][height - y - 1] = maps[Direction.Back.index][width - x - 1][y] * percent_forward;
                percent_forward *= 0.95f;
            }

            percent_side *= 0.9f;
        }
    */
    }

    public Pixmap getPixmap(Direction d, Mesh mesh) {
        build(d, mesh);

        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        ByteBuffer pixels = pixmap.getPixels();
        int idx1 = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                byte value = (byte) (maps[d.index][x][y] * 255f);
                /*
                if (value < 10 && value >= 0) {
                    pixels.put(idx1, new byte[]{35, 35, (byte) 100, (byte) 255});
                } else
                 */
                pixels.put(idx1, new byte[]{value, value, value, (byte) 255});

                idx1 += 4;
            }
        }

        return pixmap;
    }

}