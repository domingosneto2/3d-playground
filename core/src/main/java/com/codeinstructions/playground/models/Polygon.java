package com.codeinstructions.playground.models;

import com.badlogic.gdx.graphics.Color;
import com.codeinstructions.playground.Matrix;
import com.codeinstructions.playground.Mesh;
import com.codeinstructions.playground.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Polygon {
    private final List<Vertex> vertices;

    private final List<Vertex> vertexNormals;

    private Color material;

    private Vertex normal;
    private Vertex center;
    private float color;
    private List<Float> colors;

    public Polygon(Vertex ... vertices) {
        this.vertices = new ArrayList<>(Arrays.asList(vertices));
        vertexNormals = new ArrayList<>();
        this.material = Color.WHITE;
    }

    public Polygon(List<Vertex> vertices) {
        this.vertices = new ArrayList<>(vertices);
        vertexNormals = new ArrayList<>();
        this.material = Color.WHITE;
    }

    public Polygon(ArrayList<Vertex> vertices) {
        this.vertices = vertices;
        vertexNormals = new ArrayList<>();
        this.material = Color.WHITE;
    }

    public Polygon(List<Vertex> vertices, List<Vertex> normals) {
        this.vertices = new ArrayList<>(vertices);
        this.vertexNormals = new ArrayList<>(normals);
        this.material = Color.WHITE;
    }

    public Polygon(ArrayList<Vertex> vertices, ArrayList<Vertex> normals) {
        this.vertices = vertices;
        this.vertexNormals = normals;
        this.material = Color.WHITE;
    }

    public Polygon(Mesh mesh, int[] vertexIds, int[] normalIds) {
        this.vertices = null;
        this.vertexNormals = null;
        this.material = Color.WHITE;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Vertex> getVertexNormals() {
        return vertexNormals;
    }

    public final Polygon transform(Matrix transformation) {
        ArrayList<Vertex> newVertices = new ArrayList<>(vertices.size());
        for (Vertex vertex : vertices) {
            Vertex transformed = transformation.mul(vertex);
            newVertices.add(transformed);
        }
        if (vertexNormals.isEmpty()) {
            Polygon polygon = new Polygon(newVertices);
            polygon.setColor(color);
            if (colors != null) {
                polygon.setColors(colors);
            }
            return polygon;
        } else {
            // This looks wrong, revise.
            Vertex transformedOrigin = transformation.mul(Vertex.ORIGIN);
            ArrayList<Vertex> newNormals = new ArrayList<>(vertexNormals.size());
            for (Vertex normal : vertexNormals) {
                newNormals.add(transformation.mul(normal).subtract(transformedOrigin));
            }
            Polygon polygon =  new Polygon(newVertices, newNormals);
            polygon.setColor(color);
            if (colors != null) {
                polygon.setColors(colors);
            }
            return polygon;
        }
    }


    public final Polygon copy() {
        Polygon polygon = new Polygon();
        for (Vertex vertex : vertices) {
            polygon.vertices.add(new Vertex(vertex));
        }
        polygon.setColor(color);
        if (colors != null) {
            polygon.setColors(colors);
        }
        return polygon;
    }

    private Polygon copyWithVertices(ArrayList<Vertex> newVertices) {
        Polygon polygon;
        if (vertexNormals == null) {
            polygon = new Polygon(newVertices);
        } else {
            ArrayList<Vertex> normalsList = new ArrayList<>(vertexNormals.size());
            normalsList.addAll(vertexNormals);
            polygon = new Polygon(newVertices, normalsList);
        }
        polygon.setColor(color);
        if (colors != null) {
            polygon.setColors(colors);
        }
        return polygon;
    }

    public final Polygon translate(double x, double y, double z) {
        ArrayList<Vertex> newVertices = new ArrayList<>(vertices.size());
        for (Vertex vertex : vertices) {
            newVertices.add(vertex.translate(x, y, z));
        }
        return copyWithVertices(newVertices);
    }

    public final Vertex normal() {
        if (normal == null) {
            Vertex v0 = vertices.get(0);
            Vertex v1 = vertices.get(1);
            Vertex v2 = vertices.get(2);
            Vertex u = v1.subtract(v0);
            Vertex v = v2.subtract(v0);
            normal = new Vertex(u.y * v.z - u.z * v.y, u.z * v.x - u.x * v.z, u.x * v.y - u.y * v.x);
        }
        return normal;
    }

    public final Vertex center() {
        if (center == null) {
            double x = 0, y = 0, z = 0;
            int numVertices = getNumVertices();
            for (int i = 0; i < numVertices; i++) {
                Vertex v = vertices.get(i);
                x += v.x;
                y += v.y;
                z += v.z;
            }
            center = new Vertex(x/numVertices, y/numVertices, z/numVertices);
        }
        return center;
    }

    public final void setColor(float color) {
        this.color = color;
    }

    public final void setColors(List<Float> colors) {
        this.colors = colors;
    }


    public final void setVertex(int i, Vertex newVertex) {
        vertices.set(i, newVertex);
    }

    public final void setVertexNormal(int i, Vertex newVertex) {
        vertexNormals.set(i, newVertex);
    }

    public int getNumVertices() {
        return vertices.size();
    }

    public int getNumNormals() {
        return vertexNormals.size();
    }

    public Color getMaterial() {
        return material;
    }

    public void setMaterial(Color material) {
        this.material = material;
    }

    public Polygon[] split() {
        if (getNumVertices() == 3) {
            return new Polygon[] {this};
        } else {
            Vertex center = center();
            Vertex normal = normal();
            Polygon[] split = new Polygon[getNumVertices()];
            for (int i = 0; i < getNumVertices(); i++) {
                Vertex v1 = vertices.get(i);
                Vertex v2;
                if (i == getNumVertices() - 1) {
                    v2 = vertices.get(0);
                } else {
                    v2 = vertices.get(i + 1);
                }

                if (getNumNormals() > 0) {
                    Vertex n1 = vertexNormals.get(i);
                    Vertex n2;
                    if (i == getNumVertices() - 1) {
                        n2 = vertexNormals.get(0);
                    } else {
                        n2 = vertexNormals.get(i + 1);
                    }

                    Polygon child = new Polygon(Arrays.asList(center, v1, v2), Arrays.asList(normal, n1, n2));
                    child.setMaterial(material);
                    split[i] = child;
                } else {
                    Polygon child = new Polygon(center, v1, v2);
                    child.setMaterial(material);
                    split[i] = child;
                }
            }
            return split;
        }
    }

    public Vertex getVertex(int i) {
        return vertices.get(i);
    }
}
