package com.codeinstructions.playground.models;

import com.codeinstructions.playground.Vertex;

public class Spiker<T extends Model> extends BaseModel {

    private T delegate;
    private PolygonMesh myMesh;

    private double spikeLength;

    public static <T extends Model> Spiker<T> model(T delegate, double spikeLength) {
        return new Spiker<>(delegate, spikeLength);
    }

    public Spiker(T delegate, double spikeLength) {
        this.delegate = delegate;
        this.spikeLength = spikeLength;
    }


    public T getDelegate() {
        return delegate;
    }

    @Override
    protected PolygonMesh computeMesh() {

        if (myMesh == null || isModified() || delegate.isModified()) {
            PolygonMesh sourceMesh = delegate.polygonMesh();
            myMesh = applySpikes(sourceMesh);
        }
        return myMesh;
    }

    private PolygonMesh applySpikes(PolygonMesh sourceMesh) {
        if (spikeLength == 0) {
            return sourceMesh;
        }
        PolygonMesh mesh = new PolygonMesh(false);

        for (Polygon polygon : sourceMesh.getPolygons()) {
            Vertex center = polygon.center();
            Vertex normal = polygon.normal();

            Vertex v0 = polygon.getVertex(0);
            Vertex v1 = polygon.getVertex(1);
            Vertex v2 = polygon.getVertex(2);

            Vertex spike = center.add(normal.normalize().mul(spikeLength));

            mesh.polygon(polygon.getMaterial(), v0, v1, spike);
            mesh.polygon(polygon.getMaterial(), v1, v2, spike);

            if (polygon.getNumVertices() == 3) {
                mesh.polygon(polygon.getMaterial(), v2, v0, spike);
            } else {
                Vertex v3 = polygon.getVertex(3);
                mesh.polygon(polygon.getMaterial(), v2, v3, spike);
                mesh.polygon(polygon.getMaterial(), v3, v0, spike);
            }
        }

        return mesh;
    }

    public void setSpikeDelta(double spikeDelta) {
        attributeSet(0.0d, spikeDelta);
        spikeLength += spikeDelta;
    }

    public void resetSpikeLength() {
        attributeSet(spikeLength, 0);
        spikeLength = 0;
    }

    @Override
    public void increaseDetail() {
        delegate.increaseDetail();
        setModified();
    }

    @Override
    public void decreaseDetail() {
        delegate.decreaseDetail();
        setModified();
    }
}
