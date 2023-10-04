package com.codeinstructions.playground.models;

import com.badlogic.gdx.graphics.Color;
import com.codeinstructions.playground.Vertex;

import java.util.Arrays;

public class Geodesic extends BaseModel {

    private int level;

    public static Geodesic model(int level) {
        return new Geodesic(level);
    }

    public Geodesic(int level) {
        this.level = level;
    }

    public void setLevel(int level) {
        attributeSet(this.level, level);
        this.level = level;
    }

    @Override
    public PolygonMesh computeMesh() {
        clearModified();
        PolygonMesh icosahedron = Icosahedron.model().polygonMesh();
        PolygonMesh geodesic = new PolygonMesh(true);

        for (Polygon polygon : icosahedron.getPolygons()) {
            splitGeodesicFace(polygon, geodesic, level);
        }

        return geodesic;

    }

    private static void splitGeodesicFace(Polygon triangle, PolygonMesh mesh, int levels) {
        if (levels < 0) {
            throw new IllegalArgumentException();
        }
        Vertex p1 = triangle.getVertex(0);
        Vertex p2 = triangle.getVertex(1);
        Vertex p3 = triangle.getVertex(2);

        splitGeodesicFace(p1, p2, p3, mesh, levels);
    }

    private static void splitGeodesicFace(Vertex p1, Vertex p2, Vertex p3, PolygonMesh mesh, int levels) {

        if (levels == 0) {
            Polygon polygon = new Polygon(Arrays.asList(p1, p2, p3), Arrays.asList(p1, p2, p3));
            polygon.setMaterial(new Color(0.5f, 0.5f, 1f, 1));
            mesh.add(polygon);
        } else {
            Vertex m1 = p1.mean(p2).normalize();
            Vertex m2 = p2.mean(p3).normalize();
            Vertex m3 = p3.mean(p1).normalize();
            splitGeodesicFace(p1, m1, m3, mesh, levels - 1);
            splitGeodesicFace(m1, p2, m2, mesh, levels - 1);
            splitGeodesicFace(m3, m2, p3, mesh, levels - 1);
            splitGeodesicFace(m3, m1, m2, mesh, levels - 1);
        }
    }

    @Override
    public void decreaseDetail() {
        level--;
        if (level < 0) {
            level = 0;
        }
        attributeSet(level, level - 1);
    }

    @Override
    public void increaseDetail() {
        level++;
        attributeSet(level - 1, level);
    }
}
