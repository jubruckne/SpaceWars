package com.julen.spacewars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;


public class Planet implements Disposable {
    private static final float ISH_X = 0.525731112119133606f;
    private static final float ISH_Z = 0.850650808352039932f;
    private static final float ISH_N = 0f;

    private static final float[] ICOSAHEDRON_VERTICES = {
            -ISH_X, ISH_N, ISH_Z, ISH_X, ISH_N, ISH_Z, -ISH_X, ISH_N,-ISH_Z, ISH_X, ISH_N,-ISH_Z,
            ISH_N, ISH_Z, ISH_X, ISH_N, ISH_Z,-ISH_X, ISH_N,-ISH_Z, ISH_X, ISH_N,-ISH_Z,-ISH_X,
            ISH_Z, ISH_X, ISH_N, -ISH_Z, ISH_X, ISH_N, ISH_Z,-ISH_X, ISH_N, -ISH_Z,-ISH_X, ISH_N
    };

    private static final short[] ICOSAHEDRON_TRIANGLES = {
            0,4,1,
            0,9,4,
            9,5,4,
            4,5,8,
            4,8,1,
            8,10,1,
            8,3,10,
            5,3,8,
            5,2,3,
            2,7,3,
            7,10,3,
            7,6,10,
            7,11,6,
            11,0,6,
            0,1,6,
            6,1,10,
            9,0,11,
            9,11,2,
            9,2,5,
            7,2,11
    };

    private Environment environment;
    private final Material material;
    private float[] data;
    private Mesh mesh;
    ShaderProgram shader;

    public Planet(float radius) {
        super();

        this.material = new Material(ColorAttribute.createDiffuse(1, 1, 1, 1));

        create_shader();
        create_sphere(1);
    }

    public void create_shader() {
        ShaderProgram.pedantic = true;
        shader = new ShaderProgram(
                Gdx.files.internal("shaders/default.vert"),
                Gdx.files.internal("shaders/default.frag"));

        if (!shader.isCompiled()) {
            Utils.log("shader not compiled!");
            Utils.log("Shader Log: %s", shader.getLog());
        }
    }

    private void create_sphere(int frequency) {
        assert frequency >= 1;

        // First create vertices for the base icosahedron
        final short baseVertexIndex = vertex(ICOSAHEDRON_VERTICES[0], ICOSAHEDRON_VERTICES[1], ICOSAHEDRON_VERTICES[2]);
        for (int i = 3; i < ICOSAHEDRON_VERTICES.length; i += 3) {
            // No need to project, already good
            vertex(ICOSAHEDRON_VERTICES[i], ICOSAHEDRON_VERTICES[i+1], ICOSAHEDRON_VERTICES[i+2]);
        }

        // There are 12 vertices which form 12*11/2 edges, disregarding edges to self
        // and removing symmetries. Some edges don't actually exist, but the number is small enough
        // to create a simple "perfect hash" lookup table.
        // The edge indices are easy to lookup directly, so they are not present here.
        // This array only contains the first index for the subdivided vertices, remaining inner ones are always sequential.
        final short[] edgeVertices = new short[12*11/2];

        // We iterate through every triangle, lazily creating edge subdivisions and tessellating the interior.
        for (int triangle = 0; triangle < ICOSAHEDRON_TRIANGLES.length; triangle += 3) {
            final short i0 = ICOSAHEDRON_TRIANGLES[triangle];
            final short i1 = ICOSAHEDRON_TRIANGLES[triangle + 1];
            final short i2 = ICOSAHEDRON_TRIANGLES[triangle + 2];

            icosphere_tessellate(
                    (short)(baseVertexIndex + i0),
                    (short)(baseVertexIndex + i1),
                    (short)(baseVertexIndex + i2),
                    baseVertexIndex, frequency, edgeVertices);
        }
    }

    private static long icosphere_base_edge(int frequency, short[] edgeVertices, short baseOffset, short from, short to) {
        final short fromI = (short) Math.min(from, to);
        final short toI = (short) Math.max(from, to);
        assert fromI != toI;

        final int index = ((toI - baseOffset) - 1) * (toI - baseOffset) / 2 + (fromI - baseOffset);
        short edgeVertex = edgeVertices[index];
        if (edgeVertex == 0) {
            // Not generated yet! (while index 0 is valid, it would have already been used)
            final float step = 1f / frequency;
            float t = step;
            edgeVertex = normalizedLerpVertex(fromI, toI, t);
            for (int i = 2; i < frequency; i++) {
                t += step;
                normalizedLerpVertex(fromI, toI, t);
            }

            edgeVertices[index] = edgeVertex;
        }

        // Pack information about edge vertices into the long:
        // [short first][short last][short first inner][short direction multiplier * frequency]
        long packedEdge = ((from & 0xFFFFL) << 48) | ((to & 0xFFFFL) << 32);
        if (from < to) {
            // Normal direction
            packedEdge |= ((edgeVertex & 0xFFFFL) << 16) | (frequency & 0xFFFF);
        } else {
            // Reverse direction
            edgeVertex += frequency - 2;
            packedEdge |= ((edgeVertex & 0xFFFFL) << 16) | ((-frequency) & 0xFFFF);
        }

        return packedEdge;
    }

    private static long icosphere_edge(int frequency, short from, short to) {
        // Generate inner edges, if any
        short edgeVertex = 0;
        if (frequency > 1) {
            final float step = 1f / frequency;
            float t = step;
            edgeVertex = normalizedLerpVertex(from, to, t);
            for (int i = 2; i < frequency; i++) {
                t += step;
                normalizedLerpVertex(from, to, t);
            }
        }

        // Pack information about edge vertices into the long:
        // [short first][short last][short first inner][short direction multiplier * frequency]
        return ((from & 0xFFFFL) << 48) | ((to & 0xFFFFL) << 32) | ((edgeVertex & 0xFFFFL) << 16) | (frequency & 0xFFFF);
    }

    private static short icosphere_edge_index(long packedEdge, int index) {
        int frequency = (short) (packedEdge & 0xFFFF);
        int multiplier = 1;
        if (frequency < 0) {
            multiplier = -1;
            frequency = -frequency;
        }

        if (index <= 0) {
            // Return first
            return (short) (packedEdge >>> 48);
        }
        // There are actually frequency + 1 vertices on each edge
        if (index >= frequency) {
            return (short) ((packedEdge >>> 32) & 0xFFFF);
        }

        final int firstInner = (int) ((packedEdge >>> 16) & 0xFFFF);
        return (short) (firstInner + (index - 1) * multiplier);
    }

    private static void icosphere_tessellate(
                                             short i0, short i1, short i2,
                                             short baseOffset,
                                             int frequency,
                                             short[] edgeVertices) {
        // The tessellation is concerned with three line segments
        // left (going from bottom left to top)
        // right (going from bottom right to top)
        // bottom (going from bottom left to bottom right)
        // We assume CCW winding.

        final long leftEdge = icosphere_base_edge(frequency, edgeVertices, baseOffset, i1, i0);
        final long rightEdge = icosphere_base_edge(frequency, edgeVertices, baseOffset, i2, i0);

        long stripBottomEdge = icosphere_base_edge(frequency, edgeVertices, baseOffset, i1, i2);
        int topIndex = 1;
        int topFrequency = frequency - 1;
        while (topFrequency >= 0) {
            final long stripTopEdge = icosphere_edge(topFrequency, icosphere_edge_index(leftEdge, topIndex), icosphere_edge_index(rightEdge, topIndex));

            // Create triangles!
            int nextBot = 0;
            int nextTop = 0;

            short bottomLeft = icosphere_edge_index(stripBottomEdge, nextBot++);
            short bottomRight = icosphere_edge_index(stripBottomEdge, nextBot++);
            short top = icosphere_edge_index(stripTopEdge, nextTop++);

            while (true) {
                triangle(top, bottomLeft, bottomRight);

                if (nextTop <= topFrequency) {
                    short topRight = icosphere_edge_index(stripTopEdge, nextTop++);
                    triangle(top, bottomRight, topRight);

                    bottomLeft = bottomRight;
                    bottomRight = icosphere_edge_index(stripBottomEdge, nextBot++);
                    top = topRight;
                } else {
                    break;
                }
            }

            stripBottomEdge = stripTopEdge;
            topIndex++;
            topFrequency--;
        }
    }

    private short vertex(float x, float y, float z) {

    }

    public void render(Environment environment, Camera camera) {
        Gdx.gl20.glDisable(GL20.GL_CULL_FACE);

        shader.bind();
        shader.setUniformMatrix("u_projTrans", camera.combined);
        mesh.render(shader, GL20.GL_POINTS);
    }

    @Override
    public void dispose() {
        if(this.mesh != null)
            this.mesh.dispose();

        if(this.shader != null)
            this.shader.dispose();
    }
}
