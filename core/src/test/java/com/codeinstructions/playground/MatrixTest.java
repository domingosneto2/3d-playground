package com.codeinstructions.playground;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatrixTest {

    @Test
    void mul() {
        Matrix m1 = new Matrix();
        Matrix m2 = new Matrix();

        Matrix res = m1.mul(m2);
        assertEquals(res.w(), m1.w());
    }

    @Test
    void testIdentity() {
        Matrix identity = new Matrix(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);

        Matrix other = new Matrix(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);

        Matrix result = identity.mul(other);
        assertEquals(other, result);
    }

    @Test
    void testProjection() {
        Matrix projectionMatrix = Transform.projection(1, 1, 90, 2, 1);
        Vertex v = new Vertex(0.5, 0.5, 1);
        Vertex t = projectionMatrix.mul(v);
        assertEquals(v, t);

        Vertex v2 = new Vertex(0.5, 0.5, 2);
        Vertex t2 = projectionMatrix.mul(v2);
        assertEquals(new Vertex(0.25, 0.25, 1), t2);
    }

    @Test
    void testTranslation() {
        Matrix translationMatrix = Transform.translate(0.5, 0.5, 0.5);

        Vertex v = new Vertex(1, 1, 1);
        Vertex t = translationMatrix.mul(v);
        Vertex expected = new Vertex(1.5 ,1.5, 1.5);
        assertEquals(expected, t);
    }
}