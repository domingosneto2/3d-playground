package com.codeinstructions.playground.models;

import com.badlogic.gdx.graphics.Color;
import com.codeinstructions.playground.Matrix;
import com.codeinstructions.playground.Vertex;

import java.util.ArrayList;
import java.util.List;

public class PolygonMesh {
    private List<Polygon> polygons = new ArrayList<>();

    boolean concave;

    public PolygonMesh(boolean concave) {
        this.concave = concave;
    }


    public PolygonMesh transform(Matrix transformation) {
        PolygonMesh newMesh = new PolygonMesh(concave);
        for (Polygon polygon : polygons) {
            newMesh.polygons.add(polygon.transform(transformation));
        }
        return newMesh;
    }

    public void remove(int i) {
        polygons.remove(i);
    }

    public PolygonMesh translate(Vertex v) {
        PolygonMesh newMesh = new PolygonMesh(concave);
        for (Polygon polygon : polygons) {
            newMesh.polygons.add(polygon.translate(v.x, v.y, v.z));
        }
        return newMesh;
    }

    public PolygonMesh copy() {
        PolygonMesh newMesh = new PolygonMesh(concave);
        for (Polygon polygon : polygons) {
            newMesh.polygons.add(polygon.copy());
        }
        return newMesh;
    }

    public void transformInPlace(Matrix transform) {
        for (int i = 0; i < polygons.size(); i++) {
            Polygon polygon = polygons.get(i);
            polygon = polygon.transform(transform);
            polygons.set(i, polygon);
        }
    }

    public void add(Polygon polygon) {
        polygons.add(polygon);
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    public PolygonMesh translate(double x, double y, double z) {
        PolygonMesh newMesh = new PolygonMesh(concave);
        for (Polygon polygon : polygons) {
            newMesh.polygons.add(polygon.translate(x, y, z));
        }
        return newMesh;
    }

    public void add(PolygonMesh mesh) {
        for (Polygon polygon : mesh.getPolygons()) {
            add(polygon);
        }
    }

    public void polygon(Vertex ... vertices) {
        Polygon polygon = new Polygon(vertices);
        add(polygon);
    }

    public void polygon(Color material, Vertex ... vertices) {
        Polygon polygon = new Polygon(vertices);
        polygon.setMaterial(material);
        add(polygon);
    }

}
