package com.julen.spacewars.Engine;

import com.badlogic.gdx.math.Vector3;
import com.julen.spacewars.Utils;

public class PlanetTile {
    public final PlanetFace face;
    public final Planet planet;
    public final int u;
    public final int v;
    public final Vector3 model_pos;

    public PlanetTile(PlanetFace face, int u, int v) {
        this.face = face;
        this.planet = face.planet;
        this.u = u;
        this.v = v;

        float uc = (2f * (this.u + 0.5f) / (float) this.face.width) - 1f;
        float vc = (2f * (this.v + 0.5f) / (float) this.face.height) - 1f;

        this.model_pos = face.direction.toUnitSphere(uc, vc);

        Utils.log("%s", this.toString());
    }

    @Override
    public String toString() {
        return Utils.format("%i.%i.%i: %.1f", face.direction.index, u, v, local_pos);
    }

}
