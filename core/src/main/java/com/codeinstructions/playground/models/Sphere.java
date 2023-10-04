package com.codeinstructions.playground.models;

import com.codeinstructions.playground.Mesh;
import com.codeinstructions.playground.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.PI;

public class Sphere extends BaseModel {

    private int latSteps;
    private int lonSteps;

    public static Sphere model(int latSteps, int lonSteps) {
        return new Sphere(latSteps, lonSteps);
    }

    public Sphere(int latSteps, int lonSteps) {
        this.latSteps = latSteps;
        this.lonSteps = lonSteps;
    }

    @Override
    protected PolygonMesh computeMesh() {
        boolean concave = true;
        PolygonMesh mesh = new PolygonMesh(concave);

        Vertex north = new Vertex(0, 0, 1);

        Vertex lon0 = north.rotateX(PI / latSteps);
        Vertex lonp = lon0;

        List<Vertex> previousVertices = new ArrayList<>();
        previousVertices.add(lon0);

        // Build the first layer
        for (int j = 0; j < lonSteps; j++) {
            Vertex lon1 = j == lonSteps - 1 ? lon0 : lon0.rotateZ((j + 1) * 2 * PI / lonSteps);
            spherePolygon(mesh, north, lonp, lon1);
            previousVertices.add(lon1);
            lonp = lon1;
        }

        // Build all intermediate layers
        for (int i = 0; i < latSteps - 2; i++) {

            List<Vertex> currentVertices = new ArrayList<>();
            lon0 = north.rotateX((i + 2) * PI / latSteps);
            currentVertices.add(lon0);
            lonp = lon0;

            for (int j = 0; j < lonSteps; j++) {
                Vertex lon1 = j == lonSteps - 1 ? lon0 : lonp.rotateZ(2 * PI / lonSteps);
                Vertex north1 = previousVertices.get(j);
                Vertex north2 = previousVertices.get(j + 1);


                spherePolygon(mesh, north1, lonp, lon1, north2);

                currentVertices.add(lon1);
                lonp = lon1;
            }

            previousVertices = currentVertices;
        }

        Vertex south = new Vertex(0, 0, -1);


        // Build the final layer
        for (int i = 0; i < previousVertices.size() - 1; i++) {
            spherePolygon(mesh, previousVertices.get(i + 1), previousVertices.get(i), south);
        }

        return mesh;
    }

    private static void spherePolygon(PolygonMesh mesh, Vertex ... vertices) {
        List<Vertex> vertexList = Arrays.asList(vertices);
        mesh.add(new Polygon(vertexList, vertexList));
    }

    @Override
    public void decreaseDetail() {
        int prevLatSteps = latSteps;
        int prevLonSteps = lonSteps;
        latSteps--;
        lonSteps--;

        if (latSteps < 3) {
            latSteps = 3;
        }

        if (lonSteps < 3) {
            lonSteps = 3;
        }

        attributeSet(prevLonSteps, lonSteps);
        attributeSet(prevLatSteps, latSteps);
    }

    @Override
    public void increaseDetail() {
        latSteps++;
        lonSteps++;
        attributeSet(latSteps - 1, latSteps);
    }
}
