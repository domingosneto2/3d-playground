package com.codeinstructions.playground.models;


import com.badlogic.gdx.graphics.Color;
import com.codeinstructions.playground.*;


import static java.lang.Math.PI;

public class FractalTetrahedron extends BaseModel {

    int numLevels;

    public static FractalTetrahedron model(int numLevels) {
        return new FractalTetrahedron(numLevels);
    }

    public FractalTetrahedron(int numLevels) {
        this.numLevels = numLevels;
    }

    public void setNumLevels(int numLevels) {
        attributeSet(numLevels, this.numLevels);
        this.numLevels = numLevels;
    }

    @Override
    protected PolygonMesh computeMesh() {
        clearModified();
        PolygonMesh mesh = tetrahedron(numLevels, 0);
        mesh.transformInPlace(Transform.translate(Tetrahedron.centroid().mul(-1)));
        return mesh;
    }

    private PolygonMesh tetrahedron(int levels, int depth) {
        if (levels < 0) {
            throw new IllegalArgumentException();
        }
        boolean concave = levels == 0;
        PolygonMesh mesh = new PolygonMesh(concave);
        PolygonMesh face = face(levels, depth);

        PolygonMesh face2 = face
                .transform(Transform.rotationX(PI))
                .transform(Transform.rotationY(PI))
                .transform(Transform.translate(1, 0, 0))
                .transform(Transform.rotationX(-Math.atan(2 * Math.sqrt(2))));
        PolygonMesh face3 = face2
                .transform(Transform.translate(-1, 0, 0))
                .transform(Transform.rotationY(Angle.toRadians(120)));
        PolygonMesh face4 = face2
                .transform(Transform.rotationY(Angle.toRadians(-120)))
                .translate(1, 0, 0);
        mesh.add(face);
        mesh.add(face2);
        mesh.add(face3);
        mesh.add(face4);


        return mesh;
    }

    private PolygonMesh face(int level, int depth) {
        Vertex v0 = new Vertex(0, 0, 0);
        Vertex v1 = new Vertex(1, 0, 0);
        Vertex v2 = v1.rotateY(Angle.toRadians(-60));
        PolygonMesh mesh = new PolygonMesh(false);
        if (level == 0) {
            Polygon polygon = new Polygon(v0, v1, v2);
            float colorFactor = 1 - 0.1f * depth;
            polygon.setMaterial(new Color(colorFactor, colorFactor, colorFactor, 1));
            mesh.add(polygon);
            return mesh;
        } else {
            Vertex mid1 = v0.add(v2).mul(0.5);
            PolygonMesh subface = face(0, depth).transform(Transform.scale(0.5));

            PolygonMesh center = tetrahedron(level - 1, depth + 1);
            center.remove(0);
            center = center
                    .transform(Transform.scale(0.5))
                    .transform(Transform.rotationX(PI))
                    .translate(mid1);


            PolygonMesh subface2 = subface.copy().translate(0.5, 0, 0);
            PolygonMesh subface3 = subface.copy().translate(mid1);

            PolygonMesh result = new PolygonMesh(false);
            result.add(subface);
            result.add(subface2);
            result.add(subface3);
            result.add(center);

            return result;
        }
    }

    @Override
    public void decreaseDetail() {
        attributeSet(numLevels, numLevels - 1);
        numLevels--;
        if (numLevels < 0) {
            numLevels = 0;
        }
    }

    @Override
    public void increaseDetail() {
        numLevels++;
        attributeSet(numLevels - 1, numLevels);
    }
}
