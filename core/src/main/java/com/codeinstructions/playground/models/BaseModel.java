package com.codeinstructions.playground.models;

import com.badlogic.gdx.graphics.Color;
import com.codeinstructions.playground.Mesh;
import com.codeinstructions.playground.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class BaseModel implements Model {

    private PolygonMesh polygonMesh;

    private Mesh mesh;

    private boolean modified = true;

    @Override
    public boolean isModified() {
        return modified;
    }

    void clearModified() {
        this.modified = false;
    }

    void setModified() {
        modified = true;
    }

    void attributeSet(Object oldValue, Object newValue) {
        this.modified = !Objects.equals(oldValue, newValue) || this.modified;
    }

    @Override
    public void increaseDetail() {

    }

    @Override
    public void decreaseDetail() {

    }

    @Override
    public Mesh mesh() {
        if (modified) {
            polygonMesh = computeMesh();
            mesh = new Mesh(polygonMesh.concave);
            for (Polygon polygon : polygonMesh.getPolygons()) {
                mesh.add(polygon);
            }
        }

        modified = false;
        return mesh;
    }

    @Override
    public PolygonMesh polygonMesh() {
        mesh();
        return polygonMesh;
    }

    abstract protected PolygonMesh computeMesh();
}
