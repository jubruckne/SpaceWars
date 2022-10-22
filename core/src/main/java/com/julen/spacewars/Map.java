package com.julen.spacewars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ArrayMap;

public class Map {
    private final ArrayMap<String, Model> models;
    private final ArrayMap<String, ModelInstance> instances;
    public int width;
    public int height;
    public final Vector3 up;
    public final Vector3 right;
    public final Vector3 into;
    public Vector3 center;
    private float[][] tiles;
    public Matrix4 transform;
    public boolean double_sided = false;

    public Map(String fn) {
        this(fn, 512 / 4f);
    }

    public Map(String fn, float scale) {
        this(1, 1);

        Utils.log("loading heightmap %s", fn);

        Texture pic = new Texture(fn);
        if (!pic.getTextureData().isPrepared()) {
            pic.getTextureData().prepare();
        }
        Pixmap pixmap = pic.getTextureData().consumePixmap();

        int width = pixmap.getWidth();
        int height = pixmap.getHeight();

        this.width = width;
        this.height = height;
        this.tiles = new float[width][height];

        Utils.log("width: %i", width);
        Utils.log("height: %i", height);

        int min = 0;
        int max = 0;
        int col = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                col = pixmap.getPixel(x, y) & 0xFF;
                min = Math.min(min, col);
                max = Math.max(max, col);
            }
        }

        Utils.log("min/max: %i, %i", min, max);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                col = pixmap.getPixel(x, y) & 0xFF;
                tiles[x][y] = (float) (col - min) / (float) max * scale;
            }
        }

        pic.dispose();
        pixmap.dispose();
    }

    public Map(int width, int height) {
        this.height = height;
        this.width = width;
        this.transform = new Matrix4();

        this.up = new Vector3(0, 1, 0);
        this.right = new Vector3(1, 0, 0);
        this.into = new Vector3(0, -1, 1).nor();

        this.instances = new ArrayMap<>();
        this.models = new ArrayMap<>();
    }

    private void create_mesh() {
        if (this.tiles == null) {
            this.tiles = new float[width][height];
        }

        center = new Vector3(width / 2f, height / 2f, 0f);

        ModelBuilder modelBuilder = new ModelBuilder();

        Model model = modelBuilder.createArrow(
                0, 0, 0,
                width + 0.1f, 0, 0,
                0.02f, 0.25f, 8, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        models.put("x-axis", model);
        instances.put("x-axis", new ModelInstance(model));

        model = modelBuilder.createArrow(
                0, 0, 0,
                0, height + 0.1f, 0,
                0.02f, 0.25f, 8, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        models.put("y-axis", model);
        instances.put("y-axis", new ModelInstance(model));

        model = modelBuilder.createArrow(
                0, 0, 0,
                0, 0, 5,
                0.02f, 0.45f, 8, GL20.GL_TRIANGLES,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        models.put("z-axis", model);
        instances.put("z-axis", new ModelInstance(model));

        Texture texture = new Texture("7wfqeqx.png");

        model = modelBuilder.createSphere(
                0.5f,
                0.5f,
                0.5f,
                32,
                32,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                //new Material(new TextureAttribute(TextureAttribute.createDiffuse(texture))),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        models.put("center", model);
        instances.put("center", new ModelInstance(model));
        //instances.get("center").transform.setTranslation(center);

        /*
        modelBuilder.begin();
        Vector3 r1 = new Vector3(); // top left
        Vector3 r2 = new Vector3(); // top right
        Vector3 r3 = new Vector3(); // bottom right
        Vector3 n;
        Color col1 = new Color();
        Color col2 = new Color();
        Color col3 = new Color();

        MeshPartBuilder.VertexInfo p1 = new MeshPartBuilder.VertexInfo();
        MeshPartBuilder.VertexInfo p2 = new MeshPartBuilder.VertexInfo();
        MeshPartBuilder.VertexInfo p3 = new MeshPartBuilder.VertexInfo();

        Material mat = new Material(
                TextureAttribute.createDiffuse(new Texture("ground_dry_d.jpg")));
        // ColorAttribute.createDiffuse(Color.WHITE));
        MeshPartBuilder tileBuilder = null;

        int points = 10000;
        float b = 0f;

        for (int x = 0; x < width; x++) {
            points += width * (double_sided ? 2 : 1);
            Utils.log(String.valueOf(points));

            if (points >= 5120) {
                tileBuilder = modelBuilder.part(
                        "map", Gdx.gl.GL_TRIANGLES,
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates | VertexAttributes.Usage.ColorPacked,
                        mat);
                b = (float) Math.random();
                points = 0;
            }

            int x_r = Math.min(x + 1, width - 1);

            for (int y = 0; y < height; y++) {
                int y_u = Math.min(y + 1, height - 1);

                //  ##
                //  #
                //
                // top left
                r1.set(x, y + 1f, tiles[x][y_u]);
                // rop right
                r2.set(x + 1f, y + 1f, tiles[x_r][y_u]);
                // bottom left
                r3.set(x, y, tiles[x][y]);
                // normal
                n = r2.cpy().sub(r3).crs(r1.cpy().sub(r3)).nor();

                col1.set(r1.y / (float) height, r1.x / (float) width, 0.25f, 1f);
                col2.set(r2.y / (float) height, r2.x / (float) width, 0.25f, 1f);
                col3.set(r3.y / (float) height, r3.x / (float) width, 0.25f, 1f);

                p1.set(r1, n, col1, new Vector2(0, 1));
                p2.set(r2, n, col2, new Vector2(1, 1));
                p3.set(r3, n, col3, new Vector2(0, 0));
                tileBuilder.triangle(p3, p2, p1);

                if (double_sided) {
                    n = n.scl(-1f);
                    p1.set(r1, n, col1, new Vector2(r1.x, r1.y));
                    p2.set(r2, n, col2, new Vector2(r2.x, r2.y));
                    p3.set(r3, n, col3, new Vector2(r3.x, r3.y));
                    tileBuilder.triangle(p1, p2, p3);
                }

                //   #
                //  ##
                //
                // top right
                r1.set(x + 1f, y + 1f, tiles[x_r][y_u]);
                // bottom right
                r2.set(x + 1f, y, tiles[x_r][y]);
                // bottom left
                r3.set(x, y, tiles[x][y]);
                // normal
                //n = r2.cpy().sub(r3).crs(r1.cpy().sub(r3)).nor();

                col1.set(r1.y / (float) height, r1.x / (float) width, 0.25f, 1f);
                col2.set(r2.y / (float) height, r2.x / (float) width, 0.25f, 1f);
                col3.set(r3.y / (float) height, r3.x / (float) width, 0.25f, 1f);

                p1.set(r1, n, col1, new Vector2(1, 1));
                p2.set(r2, n, col2, new Vector2(1, 0));
                p3.set(r3, n, col3, new Vector2(0, 0));
                tileBuilder.triangle(p3, p2, p1);

                if (double_sided) {
                    n = n.scl(-1f);
                    p1.set(r1, n, col1, new Vector2(r1.x, r1.y));
                    p2.set(r2, n, col2, new Vector2(r2.x, r2.y));
                    p3.set(r3, n, col3, new Vector2(r3.x, r3.y));
                    tileBuilder.triangle(p1, p2, p3);
                }
            }
        }

        model = modelBuilder.end();

        Utils.log("mesh has %i parts", model.meshParts.size);
        Utils.log("mesh has %i triangles", points * 5120 * 2);

        models.put("map", model);
        instances.put("map", new ModelInstance(model));
        */
    }

    public void select(int x, int y) {
        if(models.containsKey("selected_tile")) {
            models.get("selected_tile").dispose();
            models.removeKey("selected_tile");
            instances.removeKey("selected_tile");
        }

        ModelBuilder modelBuilder = new ModelBuilder();
        Vector3 r1 = new Vector3(); // top left
        Vector3 r2 = new Vector3(); // top right
        Vector3 r3 = new Vector3(); // bottom right
        Vector3 n;

        MeshPartBuilder.VertexInfo p1 = new MeshPartBuilder.VertexInfo();
        MeshPartBuilder.VertexInfo p2 = new MeshPartBuilder.VertexInfo();
        MeshPartBuilder.VertexInfo p3 = new MeshPartBuilder.VertexInfo();

        modelBuilder.begin();

        Material mat = new Material(
                ColorAttribute.createDiffuse(1f, 1f, 0f, .30f),
                new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));

        MeshPartBuilder tileBuilder = modelBuilder.part(
                "map", Gdx.gl.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal ,
                mat);

        int x_r = Math.min(x + 1, width - 1);
        int y_u = Math.min(y + 1, height - 1);

        //  ##
        //  #
        //
        // top left
        r1.set(x, y + 1f, tiles[x][y_u]);
        // rop right
        r2.set(x + 1f, y + 1f, tiles[x_r][y_u]);
        // bottom left
        r3.set(x, y, tiles[x][y]);
        // normal
        n = r2.cpy().sub(r3).crs(r1.cpy().sub(r3)).nor();

        p1.set(r1, n, null, null);
        p2.set(r2, n, null, null);
        p3.set(r3, n, null, null);
        tileBuilder.triangle(p3, p2, p1);

        if (double_sided) {
            n = n.scl(-1f);
            p1.set(r1, n, null, null);
            p2.set(r2, n, null, null);
            p3.set(r3, n, null, null);
            tileBuilder.triangle(p1, p2, p3);
        }

        //   #
        //  ##
        //
        // top right
        r1.set(x + 1f, y + 1f, tiles[x_r][y_u]);
        // bottom right
        r2.set(x + 1f, y, tiles[x_r][y]);
        // bottom left
        r3.set(x, y, tiles[x][y]);
        // normal
        n = r2.cpy().sub(r3).crs(r1.cpy().sub(r3)).nor();

        p1.set(r1, n, null, null);
        p2.set(r2, n, null, null);
        p3.set(r3, n, null, null);
        tileBuilder.triangle(p3, p2, p1);

        if (double_sided) {
            n = n.scl(-1f);
            p1.set(r1, n, null, null);
            p2.set(r2, n, null, null);
            p3.set(r3, n, null, null);
            tileBuilder.triangle(p1, p2, p3);
        }

        Model model = modelBuilder.end();
        models.put("selected_tile", model);
        instances.put("selected_tile", new ModelInstance(model));
    }

    public float get_tile(int x, int z) {
        return tiles[x][z];
    }

    public void set_tile(int x, int y, float z) {
        if(this.tiles == null) {
            this.tiles = new float[width][height];
        }

        tiles[x][y] = z;
        if(models.size != 0) {
            for (Model m : models.values()) {
                m.dispose();
            }
        }

        models.clear();
        instances.clear();
    }

    public void set_tile(int x, int y, int width, int height, float z) {
        if(this.tiles == null) {
            this.tiles = new float[this.width][this.height];
        }

        for(int x1 = 0; x1 < width; x1++)
            for(int y1 = 0; y1 < height; y1++)
                tiles[x+x1][y+y1] = z;

        if(models.size != 0) {
            for (Model m : models.values()) {
                m.dispose();
            }
        }

        models.clear();
        instances.clear();
    }

    public void render(ModelBatch modelBatch, Environment environment) {
        if(models.size == 0) {
            create_mesh();
        }
       // Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);

     /*
        Gdx.gl.glBlendFuncSeparate(

                Gdx.gl20.GL_DST_COLOR,
                Gdx.gl20.GL_SRC_COLOR,
                Gdx.gl20.GL_ONE,
                Gdx.gl20.GL_ONE);
*/


        for(ModelInstance i: instances.values()) {
            /*Quaternion q = new Quaternion();
            Vector3 v = new Vector3();
            this.transform.getRotation(q);
            i.transform.
            i.transform.set(i.transform.getTranslation(v), q); */
            modelBatch.render(i, environment);
        }

       // Gdx.gl.glDisable(Gdx.gl20.GL_BLEND);
    }
}
