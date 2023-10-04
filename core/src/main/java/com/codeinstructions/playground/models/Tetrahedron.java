package com.codeinstructions.playground.models;

import com.codeinstructions.playground.Angle;
import com.codeinstructions.playground.Mesh;
import com.codeinstructions.playground.Vertex;

public class Tetrahedron extends BaseModel {

    public static Tetrahedron model() {
        return new Tetrahedron();
    }

    @Override
    protected PolygonMesh computeMesh() {
        PolygonMesh mesh = new PolygonMesh(true);

        Vertex v0 = new Vertex(0, 0, 0);
        Vertex v1 = new Vertex(1, 0, 0);
        Vertex v2 = v1.rotateY(Angle.toRadians(-60));
        Vertex v3 = v2.rotateX(-Math.atan(2 * Math.sqrt(2)));

        mesh.polygon( v0, v3, v1);
        mesh.polygon(v1, v3, v2);
        mesh.polygon(v2, v3, v0);
        mesh.polygon(v0, v1, v2);

        return mesh;
    }

    public static Vertex centroid() {
        Vertex v0 = new Vertex(0, 0, 0);
        Vertex v1 = new Vertex(1, 0, 0);
        Vertex v2 = v1.rotateY(Angle.toRadians(-60));
        Vertex v3 = v2.rotateX(-Math.atan(2 * Math.sqrt(2)));

        return Vertex.sum(v0, v1, v2, v3).mul(1.0 / 4);
    }
}
