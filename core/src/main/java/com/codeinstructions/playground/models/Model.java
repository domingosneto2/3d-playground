package com.codeinstructions.playground.models;

import com.codeinstructions.playground.Mesh;

public interface Model {

    boolean isModified();

    PolygonMesh polygonMesh();

    public Mesh mesh();

    public void increaseDetail();

    public void decreaseDetail();
}
