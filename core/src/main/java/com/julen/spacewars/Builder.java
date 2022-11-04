package com.julen.spacewars;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class Builder {
    private int stride;
    final private float[] vertices;
    private short vert_pos;
    private short verts;
    final private short[] triangles;
    private short tri_pos;
    private short tris;
    private short[] lines;
    private short lin_pos;
    private short lins;

    public boolean hasColor = true;
    public boolean hasNormal = true;

    public Builder() {
        vertices = new float[Short.MAX_VALUE];
        triangles = new short[Short.MAX_VALUE];
        lines = new short[Short.MAX_VALUE];
        reset();
    }

    public void reset() {
        verts = 0;
        vert_pos = 0;

        tris = 0;
        tri_pos = 0;

        lins = 0;
        lin_pos = 0;

        stride = 3 +
                (hasNormal ? 3 : 0) +
                (hasColor ? 4 : 0);
    }

    public Mesh build_wireframe() {
        VertexAttributes va;

        if (hasColor && hasNormal)
            va = new VertexAttributes(
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
            );
        else if (hasColor)
            va = new VertexAttributes(
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
            );
        else if (hasNormal)
            va = new VertexAttributes(
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE)
            );
        else
            va = new VertexAttributes(
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE)
            );

        Mesh mesh = new Mesh(true, vert_pos, lin_pos, va);
        mesh.setVertices(vertices, 0, vert_pos);
        mesh.setIndices(lines, 0, lin_pos);

        Utils.log("%s: Vertices = %i, Triangles = %i", this.getClass().getSimpleName(), (int) vert_pos, (int) tri_pos);

        return mesh;
    }

    public Mesh build() {
        VertexAttributes va;

        if (hasColor && hasNormal)
            va = new VertexAttributes(
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
            );
        else if (hasColor)
            va = new VertexAttributes(
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
            );
        else if (hasNormal)
            va = new VertexAttributes(
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE)
            );
        else
            va = new VertexAttributes(
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE)
            );

        Mesh mesh = new Mesh(true, vert_pos, tri_pos, va);
        mesh.setVertices(vertices, 0, vert_pos);
        mesh.setIndices(triangles, 0, tri_pos);

        Utils.log("%s: Vertices = %i, Triangles = %i", this.getClass().getSimpleName(), (int) verts, (int) tris);

        return mesh;
    }

    /*

                    4-------3
                    |       |
                    |       |
                    1-------2

    https://en.wikipedia.org/wiki/File:Skybox_example.png

            up2
    back1  left4  front0  right5
           down3
    */
    public void cube(float x, float y, float z, float width, float height, float depth, Color color) {
        cube(x, y, z, width, height, depth, color, 0);
    }

    public void cube(float radius, Color color) {
        cube(radius, color, 0);
    }

    public void cube(float radius, Color color, int divisions) {
        cube(0f, 0f, 0f, radius, radius, radius, color, divisions);
    }

    public void cube(float x, float y, float z, float width, float height, float depth, Color color, int divisions) {
        x = x - width / 2f;
        y = y - height / 2f;
        z = z - depth / 2f;

        // front face
        short f1 = vertex(x, y, z + depth, color);
        short f2 = vertex(x + width, y, z + depth, color);
        short f3 = vertex(x + width, y + height, z + depth, color);
        short f4 = vertex(x, y + height, z + depth, color);

        // back face
        short b1 = vertex(x, y, z, color);
        short b2 = vertex(x + width, y, z, color);
        short b3 = vertex(x + width, y + height, z, color);
        short b4 = vertex(x, y + height, z, color);

        if (divisions == 0) {
            // 0 front ccw
            rectangle(f1, f2, f3, f4);

            // 1 back cw
            rectangle(b1, b4, b3, b2);

            // 2 up ccw
            rectangle(f4, f3, b3, b4);

            // 3 down cw
            rectangle(f1, b1, b2, f2);

            // 4 left cw
            rectangle(b1, f1, f4, b4);

            // 5 right ccw
            rectangle(f2, b2, b3, f3);
        } else {
            // 0 front ccw
            rectangle(f1, f2, f3, f4, color, divisions);

            // 1 back cw
            rectangle(b1, b4, b3, b2, color, divisions);

            // 2 up ccw
            rectangle(f4, f3, b3, b4, color, divisions);

            // 3 down cw
            rectangle(f1, b1, b2, f2, color, divisions);

            // 4 left cw
            rectangle(b1, f1, f4, b4, color, divisions);

            // 5 right ccw
            rectangle(f2, b2, b3, f3, color, divisions);
        }
    }

    public void rectangle(float width, float height, Color color) {
        rectangle(0f, 0f, 0f, width, height, color, 0);
    }

    public void rectangle(float width, float height, Color color, int divisions) {
        rectangle(0f, 0f, 0f, width, height, color, divisions);
    }

    public void rectangle(float x, float y, float z, float width, float height, Color color, int divisions) {
        x = x - width / 2f;
        y = y - height / 2f;

        Vector3 p1 = new Vector3(x, y, z);
        Vector3 p2 = new Vector3(x + width, y, z);
        Vector3 p3 = new Vector3(x + width, y + height, z);
        Vector3 p4 = new Vector3(x, y + height, z);

        rectangle(p1, p2, p3, p4, color, divisions);
    }

    public void rectangle(short v1, short v2, short v3, short v4) {
        this.triangle(v1, v2, v3);
        this.triangle(v3, v4, v1);
    }

    public void rectangle(short v1, short v2, short v3, short v4, Color color, int divisions) {
        this.triangle(v1, v2, v3, color, divisions);
        this.triangle(v3, v4, v1, color, divisions);
    }

    public void rectangle(Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, Color color) {
        rectangle(p1, p2, p3, p4, color, 0);
    }

    public void rectangle(Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, Color color, int divisions) {
        short v1 = vertex(p1, color);
        short v2 = vertex(p2, color);
        short v3 = vertex(p3, color);
        short v4 = vertex(p4, color);
        this.triangle(v1, v2, v3, color, divisions);
        this.triangle(v3, v4, v1, color, divisions);
    }

    /*
    
                    3---2
                    |  /
                    | /
                    1
    
    */
    public void triangle(short v1, short v2, short v3) {
        triangles[tri_pos++] = v1;
        triangles[tri_pos++] = v2;
        triangles[tri_pos++] = v3;
        tris++;

        lines[lin_pos++] = v1;
        lines[lin_pos++] = v2;
        lins++;

        lines[lin_pos++] = v2;
        lines[lin_pos++] = v3;
        lins++;

        lines[lin_pos++] = v3;
        lines[lin_pos++] = v1;
        lins++;
    }

    private void triangle(short v1, short v2, short v3, Color color, int divisions) {
        if (divisions == 0) {
            triangle(v1, v2, v3);
        } else {
            Vector3 vert1 = new Vector3(vertices[v1 * stride], vertices[v1 * stride + 1], vertices[v1 * stride + 2]);
            Vector3 vert2 = new Vector3(vertices[v2 * stride], vertices[v2 * stride + 1], vertices[v2 * stride + 2]);
            Vector3 vert3 = new Vector3(vertices[v3 * stride], vertices[v3 * stride + 1], vertices[v3 * stride + 2]);

            Vector3 vert1_2 = vert1.cpy().lerp(vert2, 0.5f);
            Vector3 vert2_3 = vert2.cpy().lerp(vert3, 0.5f);
            Vector3 vert3_1 = vert3.cpy().lerp(vert1, 0.5f);

            short v1_2 = vertex(vert1_2, color);
            short v2_3 = vertex(vert2_3, color);
            short v3_1 = vertex(vert3_1, color);

            triangle(v1, v1_2, v3_1, Color.GREEN, divisions - 1);
            triangle(v1_2, v2, v2_3, Color.BLUE, divisions - 1);
            triangle(v1_2, v2_3, v3_1, Color.RED, divisions - 1);
            triangle(v2_3, v3, v3_1, Color.YELLOW, divisions - 1);
        }
    }

    public void triangle(Vector3 p1, Vector3 p2, Vector3 p3, Color color) {
        triangle(p1, p2, p3, color, 0);
    }

    public void triangle(Vector3 p1, Vector3 p2, Vector3 p3, Color color, int divisions) {
        short v1 = vertex(p1, color);
        short v2 = vertex(p2, color);
        short v3 = vertex(p3, color);
        this.triangle(v1, v2, v3, color, divisions);
    }

    public short vertex(Vector3 pos, Color color) {
        return vertex(pos.x, pos.y, pos.z, color);
    }

    public short vertex(float x, float y, float z, Color color) {
        vertices[vert_pos++] = x;
        vertices[vert_pos++] = y;
        vertices[vert_pos++] = z;

        if (hasNormal) {
            vertices[vert_pos++] = x;
            vertices[vert_pos++] = y;
            vertices[vert_pos++] = z;
        }

        if (hasColor) {
            vertices[vert_pos++] = color.r;
            vertices[vert_pos++] = color.g;
            vertices[vert_pos++] = color.b;
            vertices[vert_pos++] = 1f;
        }

        return verts++;
    }

    public void spherify(float radius) {
        spherify(0f, 0f, 0f, radius);
    }

    public void spherify(float centerX, float centerY, float centerZ, float radius) {
        Vector3 vert = new Vector3();

        for (short i = 0; i < this.vert_pos; i += stride) {
            vert.set(vertices[i] - centerX, vertices[i + 1] - centerY, vertices[i + 2] - centerZ).setLength(radius);
            vertices[i] = vert.x + centerX;
            vertices[i + 1] = vert.y + centerY;
            vertices[i + 2] = vert.z + centerZ;
        }
    }
}
