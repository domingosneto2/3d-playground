package com.codeinstructions.playground.models;

import com.codeinstructions.playground.Mesh;
import com.codeinstructions.playground.Vertex;

public class Cube extends BaseModel {

    public static Cube model() {
        return new Cube();
    }

    @Override
    protected PolygonMesh computeMesh() {

        PolygonMesh mesh = new PolygonMesh(true);

        Vertex v000 = new Vertex(0, 0, 0);
        Vertex v001 = new Vertex(0, 0, 1);
        Vertex v010 = new Vertex(0, 1, 0);
        Vertex v011 = new Vertex(0, 1, 1);
        Vertex v100 = new Vertex(1, 0, 0);
        Vertex v101 = new Vertex(1, 0, 1);
        Vertex v110 = new Vertex(1, 1, 0);
        Vertex v111 = new Vertex(1, 1, 1);

        mesh.polygon(v000, v010, v110, v100); // south
        mesh.polygon(v100, v110, v111, v101); // east
        mesh.polygon(v001, v011, v010, v000); // west
        mesh.polygon(v101, v111, v011, v001); // north
        mesh.polygon(v001, v000, v100, v101); // bottom
        mesh.polygon(v010, v011, v111, v110); // top


        mesh = mesh.translate(-0.5, -0.5, -0.5);

        return mesh;
    }
}
