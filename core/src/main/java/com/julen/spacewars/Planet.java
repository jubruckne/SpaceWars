package com.julen.spacewars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;

import java.sql.Time;
import java.util.TimeZone;

import static com.badlogic.gdx.graphics.GL20.*;

public class Planet implements Disposable {
    private Environment environment;
    private final Material material;

    private short stride;
    private float[] vertices;
    private short vert_pos;
    private short verts;
    private short[] triangles;
    private short tri_pos;
    private short tris;
    private short[] lines;
    private short lin_pos;
    private short lins;

    private final Vector3 vec1 = new Vector3(0, 0, 0);
    private final Vector3 vec2 = new Vector3(0, 0, 0);

    private Mesh[] meshes;
    private Mesh[] wireframes;

    public Matrix4 transform;

    ShaderProgram shader;

    public Planet(float radius) {
        super();

        this.material = new Material(ColorAttribute.createDiffuse(1, 1, 1, 1));
        this.transform = new Matrix4();
        this.transform.idt();

        create_shader();
        create_sphere();
    }

    private void create_sphere() {
        meshes = new Mesh[20];
        wireframes = new Mesh[20];

        for (int i = 0; i < 20; i++) {
            create_sphere_section(i, 25);
        }
    }

    public void create_shader() {
        ShaderProgram.pedantic = true;
        shader = new ShaderProgram(
                Gdx.files.internal("shaders/default.vert"),
                Gdx.files.internal("shaders/clouds.glsl"));

        if (!shader.isCompiled()) {
            Utils.log("shader not compiled!");
            Utils.log("Shader Log: %s", shader.getLog());
        }
    }

    private void create_sphere_section(int section, int frequency) {
        final float ISH_X = 0.525731112119133606f;
        final float ISH_Z = 0.850650808352039932f;
        final float ISH_N = 0f;

        final float[] ICOSAHEDRON_VERTICES = {
                -ISH_X, ISH_N, ISH_Z, ISH_X, ISH_N, ISH_Z, -ISH_X, ISH_N, -ISH_Z, ISH_X, ISH_N, -ISH_Z,
                ISH_N, ISH_Z, ISH_X, ISH_N, ISH_Z, -ISH_X, ISH_N, -ISH_Z, ISH_X, ISH_N, -ISH_Z, -ISH_X,
                ISH_Z, ISH_X, ISH_N, -ISH_Z, ISH_X, ISH_N, ISH_Z, -ISH_X, ISH_N, -ISH_Z, -ISH_X, ISH_N
        };

        final short[] ICOSAHEDRON_TRIANGLES = {
                0, 4, 1,
                0, 9, 4,
                9, 5, 4,
                4, 5, 8,
                4, 8, 1,
                8, 10, 1,
                8, 3, 10,
                5, 3, 8,
                5, 2, 3,
                2, 7, 3,
                7, 10, 3,
                7, 6, 10,
                7, 11, 6,
                11, 0, 6,
                0, 1, 6,
                6, 1, 10,
                9, 0, 11,
                9, 11, 2,
                9, 2, 5,
                7, 2, 11
        };

        vertices = new float[Short.MAX_VALUE];
        verts = 0;
        vert_pos = 0;
        triangles = new short[Short.MAX_VALUE];
        tris = 0;
        tri_pos = 0;
        stride = 10; // 3 pos,3 norm, 4 color

        lins = 0;
        lin_pos = 0;
        lines = new short[Short.MAX_VALUE];

        Color color = new Color();

        if (section % 3 == 0)
            color.set(
                    (float) Math.random() * 0.3f + 0.7f,
                    (float) Math.random() * 0.8f + 0.2f,
                    (float) Math.random() * 0.8f + 0.2f,
                    1f
            );
        else if (section % 3 == 1)
            color.set(
                    (float) Math.random() * 0.8f + 0.2f,
                    (float) Math.random() * 0.3f + 0.7f,
                    (float) Math.random() * 0.8f + 0.2f,
                    1f
            );
        else
            color.set(
                    (float) Math.random() * 0.8f + 0.2f,
                    (float) Math.random() * 0.8f + 0.2f,
                    (float) Math.random() * 0.3f + 0.7f,
                    1f
            );
        // First create vertices for the base icosahedron
        final short baseVertexIndex = vertex(ICOSAHEDRON_VERTICES[0], ICOSAHEDRON_VERTICES[1], ICOSAHEDRON_VERTICES[2], color);
        for (int i = 3; i < ICOSAHEDRON_VERTICES.length; i += 3) {
            vertex(ICOSAHEDRON_VERTICES[i], ICOSAHEDRON_VERTICES[i + 1], ICOSAHEDRON_VERTICES[i + 2], color);
        }

        // There are 12 vertices which form 12*11/2 edges, disregarding edges to self
        // and removing symmetries. Some edges don't actually exist, but the number is small enough
        // to create a simple "perfect hash" lookup table.
        // The edge indices are easy to lookup directly, so they are not present here.
        // This array only contains the first index for the subdivided vertices, remaining inner ones are always sequential.
        final short[] edgeVertices = new short[12 * 11 / 2];

        // We iterate through every triangle, lazily creating edge subdivisions and tessellating the interior.
        //for (int triangle = 0; triangle < ICOSAHEDRON_TRIANGLES.length; triangle += 3) {
        int triangle = section * 3;
        final short i0 = ICOSAHEDRON_TRIANGLES[triangle];
        final short i1 = ICOSAHEDRON_TRIANGLES[triangle + 1];
        final short i2 = ICOSAHEDRON_TRIANGLES[triangle + 2];

        icosphere_tessellate(
                (short) (baseVertexIndex + i0),
                (short) (baseVertexIndex + i1),
                (short) (baseVertexIndex + i2),
                baseVertexIndex, frequency, edgeVertices);
        //}

        Mesh mesh = new Mesh(true, vert_pos, tri_pos,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE));
        mesh.setVertices(vertices, 0, vert_pos);
        mesh.setIndices(triangles, 0, tri_pos);
        meshes[section] = mesh;

        mesh = new Mesh(true, vert_pos, lin_pos,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE));
        mesh.setVertices(vertices, 0, vert_pos);
        mesh.setIndices(lines, 0, lin_pos);
        wireframes[section] = mesh;

        Utils.log("Section %i: Vertices = %i, Triangles = %i", section, (int) verts, (int) tris);
    }

    private long icosphere_base_edge(int frequency, short[] edgeVertices, short baseOffset, short from, short to) {
        final short fromI = (short) Math.min(from, to);
        final short toI = (short) Math.max(from, to);
        assert fromI != toI;

        final int index = ((toI - baseOffset) - 1) * (toI - baseOffset) / 2 + (fromI - baseOffset);
        short edgeVertex = edgeVertices[index];
        if (edgeVertex == 0) {
            // Not generated yet! (while index 0 is valid, it would have already been used)
            final float step = 1f / frequency;
            float t = step;
            edgeVertex = vertex_lerp(fromI, toI, t);
            for (int i = 2; i < frequency; i++) {
                t += step;
                vertex_lerp(fromI, toI, t);
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

    private long icosphere_edge(int frequency, short from, short to) {
        // Generate inner edges, if any
        short edgeVertex = 0;
        if (frequency > 1) {
            final float step = 1f / (float) frequency;
            float t = step;
            edgeVertex = vertex_lerp(from, to, t);
            for (int i = 2; i < frequency; i++) {
                t += step;
                vertex_lerp(from, to, t);
            }
        }

        // Pack information about edge vertices into the long:
        // [short first][short last][short first inner][short direction multiplier * frequency]
        return ((from & 0xFFFFL) << 48) | ((to & 0xFFFFL) << 32) | ((edgeVertex & 0xFFFFL) << 16) | (frequency & 0xFFFF);
    }

    private short icosphere_edge_index(long packedEdge, int index) {
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

    private void icosphere_tessellate(
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

    private void triangle(short v1, short v2, short v3) {
        triangles[tri_pos++] = v1;
        triangles[tri_pos++] = v2;
        triangles[tri_pos++] = v3;
        tris++;

        lines[lin_pos++] = v1;
        lines[lin_pos++] = v2;

        lines[lin_pos++] = v2;
        lines[lin_pos++] = v3;

        lines[lin_pos++] = v3;
        lines[lin_pos++] = v1;
        lins++;
    }

    private short vertex(float x, float y, float z, Color color) {
        vertices[vert_pos++] = x;
        vertices[vert_pos++] = y;
        vertices[vert_pos++] = z;
        vertices[vert_pos++] = x;
        vertices[vert_pos++] = y;
        vertices[vert_pos++] = z;
        vertices[vert_pos++] = color.r;
        vertices[vert_pos++] = color.g;
        vertices[vert_pos++] = color.b;
        vertices[vert_pos++] = 1f;
        return verts++;
    }

    private short vertex_lerp(short v1, short v2, float t) {
    /*
        takes the positions of vertices with indices a and b, computes their linear interpolation with
        time t, normalizes the result (to be on the sphere)
    */
        v1 *= stride;
        v2 *= stride;

        vec1.set(
                vertices[v1],
                vertices[v1 + 1],
                vertices[v1 + 2]
        );

        vec2.set(
                vertices[v2],
                vertices[v2 + 1],
                vertices[v2 + 2]
        );

        /*
        if (t != 1) {
            Utils.log("lerp: %f", t);
            Utils.log("vec1 %f", vec1);
            Utils.log("vec2 %f", vec2);
        }
        */

        vec1.lerp(vec2, t).nor();

        vertices[vert_pos++] = vec1.x;
        vertices[vert_pos++] = vec1.y;
        vertices[vert_pos++] = vec1.z;

        vec1.nor();
        vertices[vert_pos++] = vec1.x;
        vertices[vert_pos++] = vec1.y;
        vertices[vert_pos++] = vec1.z;

        vertices[vert_pos++] = vertices[v2 + 6]; //MathUtils.lerp(vertices[v1 + 3], vertices[v2 + 3], t);
        vertices[vert_pos++] = vertices[v2 + 7]; //MathUtils.lerp(vertices[v1 + 4], vertices[v2 + 4], t);
        vertices[vert_pos++] = vertices[v2 + 8]; // MathUtils.lerp(vertices[v1 + 5], vertices[v2 + 5], t);
        vertices[vert_pos++] = 1f;

        return verts++;
    }

    public void render(Environment environment, Camera camera) {
        shader.bind();
        shader.setUniformMatrix("u_projTrans", camera.combined);
        shader.setUniformMatrix("u_modelTrans", transform);
        shader.setUniformf("u_time", Gdx.graphics.getFrameId() / 100f);


        shader.setUniformf("u_bw", 0.0f);
        for (Mesh m : this.meshes) {
            m.render(shader, GL20.GL_TRIANGLES);
        }

        shader.setUniformf("u_bw", 1.0f);
        for (Mesh m : this.wireframes) {
            m.render(shader, GL_LINES);
        }
    }

    @Override
    public void dispose() {
        if (this.meshes != null)
            this.meshes[0].dispose();

        if (this.shader != null)
            this.shader.dispose();
    }
}
