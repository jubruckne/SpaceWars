package com.julen.spacewars;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Builder {
    private int stride;
    private float[] vertices;
    private int vert_pos;
    private int verts;
    private short[] triangles;
    private int tri_pos;
    private int tris;
    private short[] lines;
    private int lin_pos;
    private int lins;
    private int resolution = 1;

    public boolean hasColor = true;
    public boolean hasNormal = true;
    public boolean logging = false;

    public Builder(boolean hasColor, boolean hasNormal) {
        this.hasColor = hasColor;
        this.hasNormal = hasNormal;
        reset();
    }

    public Builder() {
        this(false, false);
    }

    public void reset() {
        vertices = new float[Short.MAX_VALUE * 2 - 1];
        triangles = new short[Short.MAX_VALUE * 2 - 1];
        lines = new short[Short.MAX_VALUE * 2 - 1];

        verts = 0;
        vert_pos = 0;

        tris = 0;
        tri_pos = 0;

        lins = 0;
        lin_pos = 0;

        stride = 3 +
                (hasNormal ? 3 : 0) +
                (hasColor ? 1 : 0);

        logging = false;
    }

    private Mesh build_wireframe() {
        VertexAttributes va;

        if (hasColor && hasNormal)
            va = new VertexAttributes(
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
            );
        else if (hasColor)
            va = new VertexAttributes(
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
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

        Mesh mesh = new Mesh(false, vert_pos, lin_pos, va);
        mesh.setVertices(vertices, 0, vert_pos);
        mesh.setIndices(lines, 0, lin_pos);

        return mesh;
    }

    public Mesh[] build() {
        return build(true);
    }

    public Mesh[] build(boolean wireframe) {
        Mesh[] meshes;
        if (wireframe) {
            meshes = new Mesh[2];
            meshes[0] = build_wireframe();
            meshes[1] = build_body();
        } else {
            meshes = new Mesh[1];
            meshes[0] = build_body();
        }

        return meshes;
    }

    public Mesh build_body() {
        VertexAttributes va;

        if (hasColor && hasNormal)
            va = new VertexAttributes(
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
            );
        else if (hasColor)
            va = new VertexAttributes(
                    new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                    new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
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

        Utils.log("%s: Vertices = %i (%i), Triangles = %i (%i), Lines = %i",
                this.getClass().getSimpleName(),
                (int) verts,
                (int) vert_pos,
                (int) tris,
                (int) tri_pos,
                (int) lins);

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

        // 0 front ccw
        rectangle2(f1, f2, f3, f4);

        // 1 back cw
        // rectangle2(b4, b3, b2, b1);

        // 2 up ccw
        //rectangle2(f4, f3, b3, b4);

        //// 3 down cw
        rectangle2(f1, b1, b2, f2);

        // 4 left cw
        //rectangle2(b1, f1, f4, b4);

        // 5 right ccw
        rectangle2(f2, b2, b3, f3);

        /*
            // 0 front ccw
            rectangle2(f1, f2, f3, f4);

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
        */
    }

    public void rectangle2(float radius, Color color, int resolution) {
        this.rectangle2(0f, 0f, 0f, radius, radius, color, resolution);
    }

    public void rectangle2(Vector3 axisA, Vector3 axisB, Vector3 localUp, Color color, int resolution) {
        short v_bottom_left = 0;
        short v_bottom_right = 0;
        short v_top_right = 0;
        short v_top_left = 0;
        int offset = verts;

        Vector2 percent = new Vector2();
        Vector3 point = new Vector3();

        for (int i = 0; i < (resolution + 1) * (resolution + 1); i++) {
            int sx = i % (resolution + 1);
            int sy = i / (resolution + 1);

            percent.set((float) sx / (float) resolution, (float) sy / (float) resolution);

            point.set(localUp);
            point.add(axisA.cpy().scl(percent.x));
            point.add(axisB.cpy().scl(percent.y));

            vertex(point.x, point.y, point.z, color);
        }

        for (int i = 0; i < resolution * resolution; i++) {
            int sx = i % resolution;
            int sy = i / resolution;

            v_bottom_left = (short) (offset + (sx + sy * (resolution + 1)));
            v_bottom_right = (short) (v_bottom_left + 1);

            v_top_left = (short) (offset + sx + (sy + 1) * (resolution + 1));
            v_top_right = (short) (v_top_left + 1);

            //bottom-left
            triangles[tri_pos++] = v_bottom_left;
            triangles[tri_pos++] = v_bottom_right;
            triangles[tri_pos++] = v_top_left;
            tris++;

            //top-right 3-4-2
            triangles[tri_pos++] = v_top_right;
            triangles[tri_pos++] = v_top_left;
            triangles[tri_pos++] = v_bottom_right;
            tris++;

            // lines
            if (sy == 0) {
                lines[lin_pos++] = v_bottom_left;
                lines[lin_pos++] = v_bottom_right;
                lins++;
            }

            lines[lin_pos++] = v_bottom_right;
            lines[lin_pos++] = v_top_right;
            lins++;

            lines[lin_pos++] = v_top_right;
            lines[lin_pos++] = v_top_left;
            lins++;

            if (sx == 0) {
                lines[lin_pos++] = v_top_left;
                lines[lin_pos++] = v_bottom_left;
                lins++;
            }

            lines[lin_pos++] = v_top_left;
            lines[lin_pos++] = v_bottom_right;
            lins++;
        }
    }

    public void rectangle2(float x, float y, float z, float width, float height, Color color, int resolution) {
/*
        4-------3
        | \     |
        |   \   |
        |     \ |
        1-------2
*/
        boolean back_face = width < 0;
        width = Math.abs(width);
        height = Math.abs(height);

        log("rectangle2(float %f, float %f, float %f, float %f, float %f, Color %s, int %i)",
                x, y, z, width, height, color.toString(), resolution);

        this.resolution = resolution;

        x -= width * .5f;
        y -= height * .5f;

        short v_bottom_left = 0;
        short v_bottom_right = 0;
        short v_top_right = 0;
        short v_top_left = 0;
        int offset = verts;

        for (int i = 0; i < (resolution + 1) * (resolution + 1); i++) {
            int sx = i % (resolution + 1);
            int sy = i / (resolution + 1);

            vertex(x + width * sx / resolution, y + height * sy / resolution, z, color);
        }

        for (int i = 0; i < resolution * resolution; i++) {
            int sx = i % resolution;
            int sy = i / resolution;

            v_bottom_left = (short) (offset + (sx + sy * (resolution + 1)));
            v_bottom_right = (short) (v_bottom_left + 1);

            v_top_left = (short) (offset + sx + (sy + 1) * (resolution + 1));
            v_top_right = (short) (v_top_left + 1);

            if (!back_face) {
                //bottom-left
                triangles[tri_pos++] = v_bottom_left;
                triangles[tri_pos++] = v_bottom_right;
                triangles[tri_pos++] = v_top_left;
                tris++;

                //top-right 3-4-2
                triangles[tri_pos++] = v_top_right;
                triangles[tri_pos++] = v_top_left;
                triangles[tri_pos++] = v_bottom_right;
                tris++;
            } else {
                //bottom-left
                triangles[tri_pos++] = v_top_left;
                triangles[tri_pos++] = v_bottom_right;
                triangles[tri_pos++] = v_bottom_left;
                tris++;

                //top-right 3-4-2
                triangles[tri_pos++] = v_bottom_right;
                triangles[tri_pos++] = v_top_left;
                triangles[tri_pos++] = v_top_right;

                tris++;
            }

            // lines
            if (sy == 0) {
                lines[lin_pos++] = v_bottom_left;
                lines[lin_pos++] = v_bottom_right;
                lins++;
            }

            lines[lin_pos++] = v_bottom_right;
            lines[lin_pos++] = v_top_right;
            lins++;

            lines[lin_pos++] = v_top_right;
            lines[lin_pos++] = v_top_left;
            lins++;

            if (sx == 0) {
                lines[lin_pos++] = v_top_left;
                lines[lin_pos++] = v_bottom_left;
                lins++;
            }

            lines[lin_pos++] = v_top_left;
            lines[lin_pos++] = v_bottom_right;
            lins++;
        }
    }

    public void rectangle4(float radius, Color color, int resolution) {
        this.rectangle4(0f, 0f, 0f, radius, radius, color, resolution);
    }

    public void rectangle4(float x, float y, float z, float width, float height, Color color, int resolution) {
        /*

        4-------3
        | \   / |
        |   \   |
        | /   \ |
        1-------2

        width = 1
        height = 1
        resolution = 5

        0  1  2  3  4
        5  6  7  8  9
       10 11 12 13 14
       15 16 17 18 19
       20 21 22 23 24

        */

        x -= width * .5f;
        y -= height * .5f;

        short v_bottom_left = 0;
        short v_bottom_right = 0;
        short v_top_right = 0;
        short v_top_left = 0;
        short v_center = 0;

        for (int i = 0; i < (resolution + 1) * (resolution + 1); i++) {
            int sx = i % (resolution + 1);
            int sy = i / (resolution + 1);

            vertex(x + width * sx / resolution, y + height * sy / resolution, z, color);
        }

        for (int i = 0; i < resolution * resolution; i++) {
            int sx = i % resolution;
            int sy = i / resolution;

            v_bottom_left = (short) (sx + sy * (resolution + 1));
            v_bottom_right = (short) (v_bottom_left + 1);

            v_top_left = (short) (sx + (sy + 1) * (resolution + 1));
            v_top_right = (short) (v_top_left + 1);

            v_center = vertex(x + width * (sx + 0.5f) / (float) resolution, y + height * (sy + 0.5f) / (float) resolution, z, color);

            rectangle4(v_bottom_left, v_bottom_right, v_top_right, v_top_left, v_center);
        }
    }

    private void rectangle2(short v1, short v2, short v3, short v4) {
/*
        4-------3
        | \     |
        |   \   |
        |     \ |
        1-------2
*/
        // bottom left
        triangles[tri_pos++] = v1;
        triangles[tri_pos++] = v2;
        triangles[tri_pos++] = v4;
        tris++;

        // top right
        triangles[tri_pos++] = v3;
        triangles[tri_pos++] = v4;
        triangles[tri_pos++] = v2;
        tris++;

        // lines
        lines[lin_pos++] = v1;
        lines[lin_pos++] = v2;
        lins++;

        lines[lin_pos++] = v2;
        lines[lin_pos++] = v4;
        lins++;

        lines[lin_pos++] = v4;
        lines[lin_pos++] = v1;
        lins++;

        lines[lin_pos++] = v3;
        lines[lin_pos++] = v4;
        lins++;

        lines[lin_pos++] = v4;
        lines[lin_pos++] = v2;
        lins++;

        lines[lin_pos++] = v2;
        lines[lin_pos++] = v3;
        lins++;
    }

    private void rectangle4(short v1, short v2, short v3, short v4, short vc) {
        // Utils.log("Rectangle (%i, %i, %i, %i (%i))", v1, v2, v3, v4, vc);

        //top
        triangles[tri_pos++] = v3;
        triangles[tri_pos++] = vc;
        triangles[tri_pos++] = v2;
        tris++;

        //bottom
        triangles[tri_pos++] = v1;
        triangles[tri_pos++] = vc;
        triangles[tri_pos++] = v4;
        tris++;

        // left
        triangles[tri_pos++] = v1;
        triangles[tri_pos++] = v2;
        triangles[tri_pos++] = vc;
        tris++;

        //right
        triangles[tri_pos++] = v3;
        triangles[tri_pos++] = v4;
        triangles[tri_pos++] = vc;
        tris++;

        // lines

        lines[lin_pos++] = v1;
        lines[lin_pos++] = v2;
        lins++;

        lines[lin_pos++] = v2;
        lines[lin_pos++] = v3;
        lins++;

        lines[lin_pos++] = v3;
        lines[lin_pos++] = v4;
        lins++;

        lines[lin_pos++] = v4;
        lines[lin_pos++] = v1;
        lins++;

        lines[lin_pos++] = v1;
        lines[lin_pos++] = v3;
        lins++;

        lines[lin_pos++] = v2;
        lines[lin_pos++] = v4;
        lins++;
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
        Utils.log("rectangle");
        Utils.log("");

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

            triangle(v1, v1_2, v3_1, color, divisions - 1);
            triangle(v1_2, v2, v2_3, color, divisions - 1);
            triangle(v1_2, v2_3, v3_1, color, divisions - 1);
            triangle(v2_3, v3, v3_1, color, divisions - 1);
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
        log("vertex(float %f, float %f, float %f, %s)", x, y, z, color.toString());

        vertices[vert_pos++] = x;
        vertices[vert_pos++] = y;
        vertices[vert_pos++] = z;

        if (hasNormal) {
            vertices[vert_pos++] = x;
            vertices[vert_pos++] = y;
            vertices[vert_pos++] = z;
        }

        if (hasColor) {
            vertices[vert_pos++] = color.toFloatBits();
        }

        verts++;

        return (short) verts;
    }

    public void spherify(float radius) {
        spherify(0f, 0f, 0f, radius);
    }

    public void spherify(float centerX, float centerY, float centerZ, float radius, float[][] noise) {
        Vector3 vert = new Vector3();

        for (int i = 0; i < ((resolution + 1) * (resolution + 1)); i++) {
            // int sx = i % resolution;
            // int sy = i / resolution;

            vert.set(
                    vertices[i * stride] - centerX,
                    vertices[i * stride + 1] - centerY,
                    vertices[i * stride + 2] - centerZ);

            vert.scl(2.0f);

            vert.nor();
            /*
            float x2 = vert.x * vert.x;
            float y2 = vert.y * vert.y;
            float z2 = vert.z * vert.z;

            vert.x = (float) (vert.x * Math.sqrt(1f - y2 * 0.5f - z2 * 0.5f + y2 * z2 / 3f));
            vert.y = (float) (vert.y * Math.sqrt(1f - z2 * 0.5f - x2 * 0.5f + z2 * x2 / 3f));
            vert.z = (float) (vert.z * Math.sqrt(1f - x2 * 0.5f - y2 * 0.5f + x2 * y2 / 3f));
*/
            //vert.scl(radius);

            vertices[i * stride] = vert.x + centerX;
            vertices[i * stride + 1] = vert.y + centerY;
            vertices[i * stride + 2] = vert.z + centerZ;
        }
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

    public float[] getVertices() {
        return this.vertices;
    }

    public Vector3 getVertex(int vertexIndex) {
        int offset = vertexIndex * stride;
        return new Vector3(vertices[offset], vertices[offset + 1], vertices[offset + 2]);
    }

    public Vector3 getNormal(int vertexIndex) {
        if (!hasNormal)
            return null;

        int offset = vertexIndex * stride + 3;
        return new Vector3(vertices[offset], vertices[offset + 1], vertices[offset + 2]);
    }

    public void log(String message, Object... args) {
        if (logging) {
            Utils.log(message, args);
        }
    }
}
