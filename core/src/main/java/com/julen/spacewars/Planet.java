package com.julen.spacewars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class Planet implements Disposable {
    public final Vector3 corner00 = new Vector3(0, 0, 0);
    public final Vector3 corner10 = new Vector3(1, 0, 0);
    public final Vector3 corner01 = new Vector3(0, 0, 1);
    public final Vector3 corner11 = new Vector3(1, 0, 1);
    private Environment environment;
    private final Material material;
    private float[] data;
    private Mesh mesh;
    ShaderProgram shader;
    private Texture texture;

    public Planet(float radius) {
        super();

        this.material = new Material(ColorAttribute.createDiffuse(1, 1, 1, 1));

        update();
    }

    public void update() {
        String VERT_SHADER =
                "attribute vec2 a_position;\n" +
                        "attribute vec4 a_color;\n" +
                        "uniform mat4 u_projTrans;\n" +
                        "varying vec4 vColor;\n" +
                        "void main() {\n" +
                        "	vColor = a_color;\n" +
                        "	gl_Position =  u_projTrans * vec4(a_position.xy, 0.0, 1.0);\n" +
    //                    "   gl_Position.x = gl_Position.x + u_rnd;\n" +
                        "}";

        String FRAG_SHADER =
                "#ifdef GL_ES\n" +
                        "precision mediump float;\n" +
                        "#endif\n" +
                        "varying vec4 vColor;\n" +
                        "void main() {\n" +
                        "	gl_FragColor = vColor;\n" +
                        "}";


        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(VERT_SHADER, FRAG_SHADER);
        String log = shader.getLog();

        if (!shader.isCompiled())
            Utils.log("shader not compiled!");

        if (log!=null && log.length()!=0)
               Utils.log("Shader Log: " + log);

        create_sphere();
    }

    private void create_sphere() {
      /*
        float[] verts = new float[MAX_VERTS * NUM_COMPONENTS];
        int idx = 0;

        float x = 0.15f;
        float y = 0.15f;
        Color color = Color.YELLOW;
        float height = 1f;
        float width = 1f;

        //bottom left vertex
        verts[idx++] = x; 			//Position(x, y)
        verts[idx++] = y;
        verts[idx++] = 0;
        verts[idx++] = color.r; 	//Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;

        //top left vertex
        verts[idx++] = x; 			//Position(x, y)
        verts[idx++] = y + height;
        verts[idx++] = 0;
        verts[idx++] = color.r; 	//Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;

        //bottom right vertex
        verts[idx++] = x + width;	 //Position(x, y)
        verts[idx++] = y;
        verts[idx++] = 0;
        verts[idx++] = color.r;		 //Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;
        */

        float radius = 1.0f;
        float[] verts = new float[12 * 7];
        int idx = 0;

        // constants
        final float PI = 3.1415926f;
        final float H_ANGLE = PI / 180f * 72f;    // 72 degree = 360 / 5
        final float V_ANGLE = (float) Math.atan(1f / 2f);  // elevation = 26.565 degree

        float z, xy;                            // coords
        float hAngle1 = -PI / 2f - H_ANGLE / 2f;  // start from -126 deg at 1st row
        float hAngle2 = -PI / 2f;                // start from -90 deg at 2nd row

        Color color = Color.GREEN;

        int v = 0;

        // the first top vertex at (0, 0, r)
        verts[v++] = 0;
        verts[v++] = 0;
        verts[v++] = radius;
        verts[v++] = color.r;
        verts[v++] = color.g;
        verts[v++] = color.b;
        verts[v++] = color.a;

        // compute 10 vertices at 1st and 2nd rows
        for(int i = 1; i <= 5; ++i) {
            z  = (float) (radius * Math.sin(V_ANGLE));            // elevaton
            xy = (float) (radius * Math.cos(V_ANGLE));            // length on XY plane

            verts[v++] = (float) (xy * Math.cos(hAngle1));      // x
            verts[v++] = (float) (xy * Math.sin(hAngle1));  // y
            verts[v++] = z;                                 // z
            verts[v++] = color.r;
            verts[v++] = color.g;
            verts[v++] = color.b;
            verts[v++] = color.a;

            verts[v++] = (float) (xy * Math.cos(hAngle2));
            verts[v++] = (float) (xy * Math.sin(hAngle2));
            verts[v++] = -z;
            verts[v++] = color.r;
            verts[v++] = color.g;
            verts[v++] = color.b;
            verts[v++] = color.a;

            // next horizontal angles
            hAngle1 += H_ANGLE;
            hAngle2 += H_ANGLE;
        }

        // the last bottom vertex at (0, 0, -r)
        verts[v++] = 0;
        verts[v++] = 0;
        verts[v++] = -radius;
        verts[v++] = color.r;
        verts[v++] = color.g;
        verts[v++] = color.b;
        verts[v++] = color.a;

        MeshBuilder builder = new MeshBuilder();
        builder.nor

        mesh = new Mesh(true, 12, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, "a_color"));
        mesh.setVertices(verts);

        v1 = &tmpVertices[tmpIndices[j] * 3];
        v2 = &tmpVertices[tmpIndices[j + 1] * 3];
        v3 = &tmpVertices[tmpIndices[j + 2] * 3];

        mesh.setIndices(
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
