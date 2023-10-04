package com.codeinstructions.playground;

public class Triangle {
    Vertex v1;
    Vertex v2;
    Vertex v3;

    public Triangle(Vertex v1, Vertex v2, Vertex v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public Triangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
        this.v1 = new Vertex(x1, y1, z1);
        this.v2 = new Vertex(x2, y2, z2);
        this.v3 = new Vertex(x3, y3, z3);
    }

    public Triangle translate(Vertex offset) {
        return new Triangle(v1.translate(offset), v2.translate(offset), v3.translate(offset));
    }

    public Triangle translate(double x, double y, double z) {
        return new Triangle(v1.translate(x, y, z), v2.translate(x, y, z), v3.translate(x, y, z));
    }
}
