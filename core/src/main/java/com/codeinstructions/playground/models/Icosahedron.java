package com.codeinstructions.playground.models;

import com.codeinstructions.playground.Angle;
import com.codeinstructions.playground.Mesh;
import com.codeinstructions.playground.Vertex;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;

public class Icosahedron extends BaseModel {

    public static Icosahedron model() {
        return new Icosahedron();
    }


    @Override
    protected PolygonMesh computeMesh() {
        PolygonMesh mesh = new PolygonMesh(true);

        Vertex north = Vertex.K;
        Vertex south = north.mul(-1);

        List<Vertex> layer1 = new ArrayList<>();

        Vertex layer1Head = north.rotateX(PI / 2 - Math.atan(1/2d));
        layer1.add(layer1Head);

        for (int i = 1; i < 5; i++) {
            Vertex next = layer1Head.rotateZ(Angle.toRadians(i * 72));
            layer1.add(next);
        }
        layer1.add(layer1Head);

        List<Vertex> layer2 = new ArrayList<>();
        Vertex layer2Head = south.rotateX(Math.atan(1/2d) - PI/2).rotateZ(Angle.toRadians(36));
        layer2.add(layer2Head);
        for (int i = 1; i < 5; i++) {
            Vertex next = layer2Head.rotateZ(Angle.toRadians(i * 72));
            layer2.add(next);
        }
        layer2.add(layer2Head);

        for (int i = 0; i < layer1.size() - 1; i++) {
            mesh.add(new Polygon(north, layer1.get(i), layer1.get(i + 1)));
        }

        for (int i = 0; i < layer1.size() - 1; i++) {
            mesh.add(new Polygon(layer1.get(i +1), layer1.get(i), layer2.get(i)));
        }

        for (int i = 0; i < layer2.size() - 1; i++) {
            mesh.add(new Polygon(layer2.get(i), layer2.get(i + 1), layer1.get(i + 1)));
        }

        for (int i = 0; i < layer2.size() - 1; i++) {
            mesh.add(new Polygon(south, layer2.get(i + 1), layer2.get(i)));
        }


        return mesh;

    }
}
