package com.codeinstructions.playground;

public class OptimizedMesh {
    private double[] vertices;
    private boolean used;

    private OptimizedPolygon[] polygons;

    public OptimizedMesh(Mesh mesh) {
        polygons = new OptimizedPolygon[mesh.getPolygons().size()];

    }
}
