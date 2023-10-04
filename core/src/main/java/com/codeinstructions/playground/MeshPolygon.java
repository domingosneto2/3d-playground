package com.codeinstructions.playground;

import com.badlogic.gdx.graphics.Color;

public class MeshPolygon {
    int numVertices;

    int p0, p1, p2, p3;

    int n0, n1, n2, n3;

    int numNormals;

    Vertex normal;

    Vertex center;

    Color material = Color.WHITE;

    float r, g, b;

    float r0, g0, b0;
    float r1, g1, b1;
    float r2, g2, b2;
    float r3, g3, b3;



    boolean hasVertexColors = false;

    boolean backFaceCulling;

    Object centerContext = null;

    Object normalContext = null;

    public MeshPolygon(int numVertices, int numNormals) {
        this.numVertices = numVertices;
        this.numNormals = numNormals;
    }

    public MeshPolygon(MeshPolygon polygon) {
        this.numNormals = polygon.numNormals;
        this.numVertices = polygon.numVertices;
        this.p0 = polygon.p0;
        this.p1 = polygon.p1;
        this.p2 = polygon.p2;
        this.p3 = polygon.p3;

        this.n0 = polygon.n0;
        this.n1 = polygon.n1;
        this.n2 = polygon.n2;
        this.n3 = polygon.n3;

        this.r = polygon.r;
        this.g = polygon.g;
        this.b = polygon.b;

        this.hasVertexColors = polygon.hasVertexColors;
        this.backFaceCulling = polygon.backFaceCulling;

        this.r0 = polygon.r0;
        this.g0 = polygon.g0;
        this.b0 = polygon.b0;

        this.r1 = polygon.r1;
        this.g1 = polygon.g1;
        this.b1 = polygon.b1;

        this.r2 = polygon.r2;
        this.g2 = polygon.g2;
        this.b2 = polygon.b2;

        this.r3 = polygon.r3;
        this.g3 = polygon.g3;
        this.b3 = polygon.b3;

    }

    public boolean isHasVertexColors() {
        return hasVertexColors;
    }

    public void setHasVertexColors(boolean hasVertexColors) {
        this.hasVertexColors = hasVertexColors;
    }

    public Vertex normal(Mesh mesh) {
        if (normal == null || normalContext != mesh.getMeshContext()) {
            Vertex v0 = mesh.getVertexById(p0);
            Vertex v1 = mesh.getVertexById(p1);
            Vertex v2 = mesh.getVertexById(p2);
            Vertex u = v1.subtract(v0);
            Vertex v = v2.subtract(v0);
            normal = new Vertex(u.y * v.z - u.z * v.y, u.z * v.x - u.x * v.z, u.x * v.y - u.y * v.x);
            normalContext = mesh.getMeshContext();
        }
        return normal;
    }

    public Vertex center(Mesh mesh) {
        if (center == null || centerContext != mesh.getMeshContext()) {
            double x = 0, y = 0, z = 0;
            int numVertices = getNumVertices();
            for (int i = 0; i < numVertices; i++) {
                Vertex v = getVertex(i, mesh);
                x += v.x;
                y += v.y;
                z += v.z;
            }
            center = new Vertex(x / numVertices, y / numVertices, z / numVertices);
            centerContext = mesh.getMeshContext();
        }
        return center;
    }


    public void setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setC0(float r, float g, float b) {
        this.r0 = r;
        this.g0 = g;
        this.b0 = b;
    }

    public void setC1(float r, float g, float b) {
        this.r1 = r;
        this.g1 = g;
        this.b1 = b;
    }

    public void setC2(float r, float g, float b) {
        this.r2 = r;
        this.g2 = g;
        this.b2 = b;
    }

    public void setC3(float r, float g, float b) {
        this.r3 = r;
        this.g3 = g;
        this.b3 = b;
    }

    public Vertex getVertex(int index, Mesh mesh) {
        return mesh.getVertexById(getVertexId(index));
    }


    public boolean isBackFaceCulling() {
        return backFaceCulling;
    }

    public void setBackFaceCulling(boolean backFaceCulling) {
        this.backFaceCulling = backFaceCulling;
    }

    public int getNumVertices() {
        return numVertices;
    }

    public int getNumNormals() {
        return numNormals;
    }

    public int getP0() {
        return p0;
    }

    public void setP0(int p0) {
        this.p0 = p0;
    }

    public int getP1() {
        return p1;
    }

    public void setP1(int p1) {
        this.p1 = p1;
    }

    public int getP2() {
        return p2;
    }

    public void setP2(int p2) {
        this.p2 = p2;
    }

    public int getP3() {
        return p3;
    }

    public void setP3(int p3) {
        this.p3 = p3;
    }

    public int getN0() {
        return n0;
    }

    public void setN0(int n0) {
        this.n0 = n0;
    }

    public int getN1() {
        return n1;
    }

    public void setN1(int n1) {
        this.n1 = n1;
    }

    public int getN2() {
        return n2;
    }

    public void setN2(int n2) {
        this.n2 = n2;
    }

    public int getN3() {
        return n3;
    }

    public void setN3(int n3) {
        this.n3 = n3;
    }


    public int getVertexId(int i) {
        switch (i) {
            case 0 -> {
                return p0;
            }
            case 1 -> {
                return p1;
            }
            case 2 -> {
                return p2;
            }
            case 3 -> {
                return p3;
            }
        }
        return 0;
    }

    public void setVertexId(int i, int newId) {
        switch (i) {
            case 0 -> {
                p0 = newId;
            }

            case 1 -> {
                p1 = newId;
            }
            case 2 -> {
                p2 = newId;
            }
            case 3 -> {
                p3 = newId;
            }
        }
    }

    public int getNormalId(int i) {
        switch (i) {
            case 0 -> {
                return n0;
            }
            case 1 -> {
                return n1;
            }
            case 2 -> {
                return n2;
            }
            case 3 -> {
                return n3;
            }
        }
        return 0;
    }

    public void setNormalId(int i, int newId) {
        switch (i) {
            case 0 -> {
                n0 = newId;
            }
            case 1 -> {
                n1 = newId;
            }
            case 2 -> {
                n2 = newId;
            }
            case 3 -> {
                n3 = newId;
            }
        }
    }

    public final MeshPolygon copy() {
        return new MeshPolygon(this);
    }

    public final Vertex getV0(Mesh mesh) {
        return mesh.getVertexById(p0);
    }

    public final Vertex getV1(Mesh mesh) {
        return mesh.getVertexById(p1);
    }

    public final Vertex getV2(Mesh mesh) {
        return mesh.getVertexById(p2);
    }

    public final Vertex getV3(Mesh mesh) {
        return mesh.getVertexById(p3);
    }

    public void remapVertices(int newBase) {
        p0 += newBase;
        p1 += newBase;
        p2 += newBase;
        if (numVertices > 3) {
            p3 += newBase;
        }
    }

    public void remapNormals(int newBase) {
        if (numNormals > 0) {
            n0 += newBase;
            n1 += newBase;
            n2 += newBase;
            if (numNormals > 3) {
                n3 += newBase;
            }
        }
    }

    public Color getMaterial() {
        return material;
    }

    public void setMaterial(Color material) {
        this.material = material;
    }
}
