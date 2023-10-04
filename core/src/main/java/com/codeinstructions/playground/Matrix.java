package com.codeinstructions.playground;

import java.util.Arrays;

public class Matrix {
    private final double[] data;

    public Matrix() {
        this.data = new double[16];
    }

    public Matrix(double ... values) {
        this.data = new double[16];

        System.arraycopy(values, 0, data, 0, values.length);
    }

    public int w() {
        return 4;
    }

    public int h() {
        return 4;
    }

    public Matrix mul(Matrix other) {
        Matrix result = new Matrix();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double val = 0;
                for (int k = 0; k < 4; k++) {
                    val += get(k, j) * other.get(i, k);
                }
                result.set(i, j, val);
            }
        }
        return result;
    }

    public Matrix rotationX(double theta) {
        return Transform.rotationX(theta).mul(this);
    }

    public Matrix rotationY(double theta) {
        return Transform.rotationY(theta).mul(this);
    }

    public Matrix rotationZ(double theta) {
        return Transform.rotationZ(theta).mul(this);
    }

    public Matrix translation(double dx, double dy, double dz) {
        return Transform.translate(dx, dy, dz).mul(this);
    }

    public Matrix translation(Vertex delta) {
        return Transform.translate(delta).mul(this);
    }

    public Vertex mul(Vertex v) {
//        double x = v.x * get(0, 0) + v.y * get(1, 0) + v.z * get(2, 0) + get(3, 0);
//        double y = v.x * get(0, 1) + v.y * get(1, 1) + v.z * get(2, 1) + get(3, 1);
//        double z = v.x * get(0, 2) + v.y * get(1, 2) + v.z * get(2, 2) + get(3, 2);
//        double w = v.x * get(0, 3) + v.y * get(1, 3) + v.z * get(2, 3) + get(3, 3);

        double x = v.x * data[0] + v.y * data[1] + v.z * data[2] + data[3];
        double y = v.x * data[4] + v.y * data[5] + v.z * data[6] + data[7];
        double z = v.x * data[8] + v.y * data[9] + v.z * data[10] + data[11];
        double w = v.x * data[12] + v.y * data[13] + v.z * data[14] + data[15];

        if (w != 0) {
            x = x / w;
            y = y / w;
            z = z / w;
        }

        return new Vertex(x, y, z);
    }

    public void mul(Vertex v, Vertex dest) {

        double x = v.x * data[0] + v.y * data[1] + v.z * data[2] + data[3];
        double y = v.x * data[4] + v.y * data[5] + v.z * data[6] + data[7];
        double z = v.x * data[8] + v.y * data[9] + v.z * data[10] + data[11];
        double w = v.x * data[12] + v.y * data[13] + v.z * data[14] + data[15];

        if (w != 0) {
            x = x / w;
            y = y / w;
            z = z / w;
        }

        dest.x = x;
        dest.y = y;
        dest.z = z;
    }

    public double get(int x, int y) {
        return data[y * 4 + x];
    }

    public void set(int x, int y, double val) {
        data[y * 4 + x] = val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        return Arrays.equals(data, matrix.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return "Matrix{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}
