package com.codeinstructions.playground;

public class Transform {
	public static Matrix identity() {
		return new Matrix(1, 0, 0, 0,
				0, 1, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}
    public static Matrix projection(int w, int h, double fov, double zFar, double zNear) {
        double fovRadians = Math.PI * fov / 180;
		double a = (double) h / w;
		double f = 1 / Math.tan(fovRadians / 2);
		double q = zFar / (zFar - zNear);
		return new Matrix(a * f, 0, 0, 0,
				0, f, 0, 0,
				0, 0, q, zNear * q,
				0, 0, 1, 0);
    }

	public static Matrix rotationX(double rad) {
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);
		return new Matrix(1, 0, 0, 0,
				0, cos, -sin, 0,
				0, sin, cos, 0,
				0, 0, 0, 1);
	}

	public static Matrix rotationY(double rad) {
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);
		return new Matrix(cos, 0, sin, 0,
				0, 1, 0, 0,
				-sin, 0, cos, 0,
				0, 0, 0, 1);
	}

	public static Matrix rotationZ(double rad) {
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);
		return new Matrix(cos, -sin, 0, 0,
				sin, cos, 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	public static Matrix translate(double dx, double dy, double dz) {
		return new Matrix(1, 0, 0, dx,
				0, 1, 0, dy,
				0, 0, 1, dz,
				0, 0, 0, 1);
	}

	public static Matrix translate(Vertex delta) {
		return translate(delta.x, delta.y, delta.z);
	}

	public static Matrix scale(double v) {
		return new Matrix(v, 0, 0, 0,
				0, v, 0, 0,
				0, 0, v, 0,
				0, 0, 0, 1);
	}

	public static Matrix scale(double x, double y, double z) {
		return new Matrix(x, 0, 0, 0,
				0, y, 0, 0,
				0, 0, z, 0,
				0, 0, 0, 1);
	}

	public static Matrix scale(Vertex scale) {
		return new Matrix(scale.x, 0, 0, 0,
				0, scale.y, 0, 0,
				0, 0, scale.z, 0,
				0, 0, 0, 1);
	}
}
