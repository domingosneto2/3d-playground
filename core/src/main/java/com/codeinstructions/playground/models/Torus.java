package com.codeinstructions.playground.models;

import com.codeinstructions.playground.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.PI;

public class Torus extends BaseModel{
    private int outerSteps;
    private int innerSteps;
    private double innerRadius;

    public static Torus model(int outerSteps, int innerSteps, double innerRadius) {
        return new Torus(outerSteps, innerSteps, innerRadius);
    }

    public Torus(int outerSteps, int innerSteps, double innerRadius) {
        this.outerSteps = outerSteps;
        this.innerSteps = innerSteps;
        this.innerRadius = innerRadius;
    }

    public void setOuterSteps(int outerSteps) {
        attributeSet(this.outerSteps, outerSteps);
        this.outerSteps = outerSteps;
    }

    public void setInnerSteps(int innerSteps) {
        attributeSet(this.innerSteps, innerSteps);
        this.innerSteps = innerSteps;
    }

    public void setInnerRadius(double innerRadius) {
        attributeSet(this.innerRadius, innerRadius);
        this.innerRadius = innerRadius;
    }

    @Override
    protected PolygonMesh computeMesh() {
        double outerRadius = 1 - innerRadius;

        Vertex outerStart = new Vertex(0, outerRadius, 0);
        List<Vertex> initialRing = new ArrayList<>();
        Vertex ringBuilder = new Vertex(0, 0, innerRadius);
        initialRing.add(ringBuilder.add(outerStart));
        for (int j = 1; j < innerSteps; j++) {
            Vertex ringNext = ringBuilder.rotateX(j * 2 * PI / innerSteps);
            initialRing.add(ringNext.add(outerStart));
        }
        initialRing.add(ringBuilder.add(outerStart));
        List<Vertex> previousRing = initialRing;

        PolygonMesh mesh = new PolygonMesh(false);
        Vertex prevCenter = outerStart;

        for (int i = 0; i < outerSteps; i++) {
            List<Vertex> currentRing;
            Vertex currentCenter;
            if (i == outerSteps - 1) {
                currentRing = initialRing;
                currentCenter = outerStart;
            } else {
                Matrix rotate = Transform.rotationZ(- (i + 1) * 2 * PI / outerSteps);
                currentRing = initialRing.stream().map(rotate::mul).toList();
                currentCenter = rotate.mul(outerStart);
            }
            for (int j = 0; j < currentRing.size() - 1; j++) {
                Vertex p1 = previousRing.get(j);
                Vertex p2 = previousRing.get(j + 1);
                Vertex c1 = currentRing.get(j);
                Vertex c2 = currentRing.get(j + 1);

                //polygon(mesh, p1, p2, c2, c1);
                List<Vertex> vertices = Arrays.asList(p1, p2, c2, c1);
                List<Vertex> normals = new ArrayList<>();

                normals.add(p1.subtract(prevCenter));
                normals.add(p2.subtract(prevCenter));
                normals.add(c2.subtract(currentCenter));
                normals.add(c1.subtract(currentCenter));

                mesh.add(new Polygon(vertices, normals));

            }
            previousRing = currentRing;
            prevCenter = currentCenter;
        }

        return mesh;
    }

    @Override
    public void decreaseDetail() {
        innerSteps--;
        outerSteps--;
        if (innerSteps < 4) {
            innerSteps = 4;
        }
        if (outerSteps < 4) {
            outerSteps = 4;
        }
        attributeSet(innerSteps, innerSteps - 1);
    }

    @Override
    public void increaseDetail() {
        innerSteps++;
        outerSteps++;
        attributeSet(innerSteps - 1, innerSteps);
    }
}
