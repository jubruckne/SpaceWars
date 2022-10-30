package com.julen.spacewars;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class Builder {
    private short stride;
    final private float[] vertices;
    private short vert_pos;
    private short verts;
    final private short[] triangles;
    private short tri_pos;
    private short tris;

    public Builder() {
        vertices = new float[Short.MAX_VALUE];
        triangles = new short[Short.MAX_VALUE];

        reset();
    }

    public void reset() {
        verts = 0;
        vert_pos = 0;
        tris = 0;
        tri_pos = 0;
        stride = 10; // 3 pos,3 norm, 4 color
    }

    public Mesh build() {
        Mesh mesh = new Mesh(true, vert_pos, tri_pos,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.Normal, 3, ShaderProgram.NORMAL_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE));
        mesh.setVertices(vertices, 0, vert_pos);
        mesh.setIndices(triangles, 0, tri_pos);

        Utils.log("%s: Vertices = %i, Triangles = %i", this.getClass().getSimpleName(), (int) verts, (int) tris);

        this.reset();

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

        x = x - width / 2f;
        y = y - height / 2f;
        z = z - depth / 2f;

        // front face: 0
        short f1 = vertex(x, y, z + depth, color);
        short f2 = vertex(x + width, y, z + depth, color);
        short f3 = vertex(x + width, y + height, z + depth, color);
        short f4 = vertex(x, y + height, z + depth, color);

        // back face: 1
        short b1 = vertex(x, y, z, color);
        short b2 = vertex(x + width, y, z, color);
        short b3 = vertex(x + width, y + height, z, color);
        short b4 = vertex(x, y + height, z, color);

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
    }

    public void rectangle(short v1, short v2, short v3, short v4) {
        this.triangle(v1, v2, v3);
        this.triangle(v3, v4, v1);
    }

    public void rectangle(Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, Color color) {
        short v1 = vertex(p1, color);
        short v2 = vertex(p2, color);
        short v3 = vertex(p3, color);
        short v4 = vertex(p4, color);
        this.triangle(v1, v2, v3);
        this.triangle(v3, v4, v1);
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
    }

    public void triangle(Vector3 p1, Vector3 p2, Vector3 p3, Color color) {
        short v1 = vertex(p1, color);
        short v2 = vertex(p2, color);
        short v3 = vertex(p3, color);
        this.triangle(v1, v2, v3);
    }

    public short vertex(Vector3 pos, Color color) {
        return vertex(pos.x, pos.y, pos.z, color);
    }

    public short vertex(float x, float y, float z, Color color) {
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

}
