package com.codeinstructions.playground;

import java.util.List;
import java.util.Objects;

public class Vertex {
    public double x;
    public double y;
    public double z;

    public static final Vertex ORIGIN = new Vertex(0, 0, 0);

    public static final Vertex I = new Vertex(1, 0, 0);
    public static final Vertex J = new Vertex(0, 1, 0);
    public static final Vertex K = new Vertex(0, 0, 1);


    public Vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vertex(Vertex vertex) {
        this.x = vertex.x;
        this.y = vertex.y;
        this.z = vertex.z;
    }

    public static Vertex sum(Vertex ... vertices) {
        double x = 0, y = 0, z = 0;

        for (Vertex vertex : vertices) {
            x += vertex.x;
            y += vertex.y;
            z += vertex.z;
        }

        return new Vertex(x, y, z);
    }

    public static Vertex avg(Vertex ... vertices) {
        double x = 0, y = 0, z = 0;

        for (Vertex vertex : vertices) {
            x += vertex.x;
            y += vertex.y;
            z += vertex.z;
        }

        int count = vertices.length;

        return new Vertex(x/count, y/count, z/count);
    }

    public static Vertex sum(List<Vertex> vertices) {
        double x = 0, y = 0, z = 0;

        for (Vertex vertex : vertices) {
            x += vertex.x;
            y += vertex.y;
            z += vertex.z;
        }

        return new Vertex(x, y, z);
    }

    public static Vertex avg(List<Vertex> vertices) {
        double x = 0, y = 0, z = 0;

        for (Vertex vertex : vertices) {
            x += vertex.x;
            y += vertex.y;
            z += vertex.z;
        }

        int count = vertices.size();

        return new Vertex(x/count, y/count, z/count);
    }

    public double dot(Vertex v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vertex add(Vertex vertex) {
        return new Vertex(x + vertex.x, y + vertex.y, z + vertex.z);
    }

    public Vertex subtract(Vertex vertex) {
        return new Vertex(x - vertex.x, y - vertex.y, z - vertex.z);
    }

    public Vertex mul(double scalar) {
        return new Vertex(x * scalar, y * scalar, z * scalar);
    }

    public double norm() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vertex rotateX(double theta) {
        return Transform.rotationX(theta).mul(this);
    }

    public Vertex rotateY(double theta) {
        return Transform.rotationY(theta).mul(this);
    }

    public Vertex rotateZ(double theta) {
        return Transform.rotationZ(theta).mul(this);
    }

    public Vertex translate(double dx, double dy, double dz) {
        return Transform.translate(dx, dy, dz).mul(this);
    }

    public Vertex translate(Vertex offset) {
        return add(offset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Double.compare(x, vertex.x) == 0 && Double.compare(y, vertex.y) == 0 && Double.compare(z, vertex.z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public Vertex normalize() {
        double norm = norm();
        return new Vertex(x / norm, y / norm, z / norm);
    }

    public Vertex mean(Vertex p2) {
        return new Vertex((x + p2.x)/2, (y + p2.y)/2, (z + p2.z)/2);
    }

    public void subtractInPlace(Vertex other) {
        x -= other.x;
        y -= other.y;
        z -= other.z;
    }

    public void addInPlace(Vertex v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }

    public void mulInPlace(double v) {
        x *= v;
        y *= v;
        z *= v;
    }
}
