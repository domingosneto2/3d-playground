package com.codeinstructions.playground;

import com.codeinstructions.playground.models.Polygon;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Mesh {
    private final List<MeshPolygon> polygons;

    private final List<Vertex> vertices;

    private final List<Vertex> vertexNormals;

    private boolean concave = false;

    private boolean packed = true;

    private Object meshContext = new Object();

    public Mesh(boolean concave) {
        this.concave = concave;
        this.polygons = new ArrayList<>();
        vertices = new ArrayList<>();
        vertexNormals = new ArrayList<>();
    }

    public Mesh(boolean concave, int capacity) {
        this.concave = concave;
        this.polygons = new ArrayList<>(capacity);
        vertices = new ArrayList<>();
        vertexNormals = new ArrayList<>();
    }

    public Mesh() {
        this.polygons = new ArrayList<>();
        vertices = new ArrayList<>();
        vertexNormals = new ArrayList<>();
    }

    public boolean isConcave() {
        return concave;
    }

    public void add(Polygon polygon) {
//        if (polygon.getNumVertices() > 3) {
//            Polygon[] children = polygon.split();
//            for (Polygon child : children) {
//                add(child);
//            }
//            return;
//        }
        MeshPolygon meshPolygon = new MeshPolygon(polygon.getNumVertices(), polygon.getNumNormals());
        meshPolygon.setMaterial(polygon.getMaterial());
        List<Vertex> polygonVertices = polygon.getVertices();
        Vertex v0 = polygonVertices.get(0);
        vertices.add(v0);
        int id = vertices.size();
        meshPolygon.setP0(id);

        Vertex v1 = polygonVertices.get(1);
        vertices.add(v1);
        id = vertices.size();
        meshPolygon.setP1(id);

        Vertex v2 = polygonVertices.get(2);
        vertices.add(v2);
        id = vertices.size();
        meshPolygon.setP2(id);

        if (polygonVertices.size() > 3) {
            Vertex v3 = polygonVertices.get(3);
            vertices.add(v3);
            id = vertices.size();
            meshPolygon.setP3(id);
        }

        List<Vertex> polygonNormals = polygon.getVertexNormals();
        if (!polygonNormals.isEmpty()) {
            Vertex n0 = polygonNormals.get(0);
            vertexNormals.add(n0);
            id = vertexNormals.size();
            meshPolygon.setN0(id);

            Vertex n1 = polygonNormals.get(1);
            vertexNormals.add(n1);
            id = vertexNormals.size();
            meshPolygon.setN1(id);

            Vertex n2 = polygonNormals.get(2);
            vertexNormals.add(n2);
            id = vertexNormals.size();
            meshPolygon.setN2(id);

            if (polygonNormals.size() > 3) {
                Vertex n3 = polygonNormals.get(3);
                vertexNormals.add(n3);
                id = vertexNormals.size();
                meshPolygon.setN3(id);
            }
        }


        polygons.add(meshPolygon);


        packed = false;
    }

    public void remove(int index) {
        polygons.remove(index);
        packed = false;
    }

    public void packNoDedupe() {
        List<Vertex> newVertices = new ArrayList<>(vertices.size());
        List<Vertex> newNormals = new ArrayList<>(vertexNormals.size());

        int[] vertexFromTo = new int[vertices.size()];
        int[] normalFromTo = new int[vertexNormals.size()];

        for (MeshPolygon polygon : polygons) {

            for (int i = 0; i < polygon.getNumVertices(); i++) {
                int existingId = polygon.getVertexId(i);
                int newId = remapVertex(vertices, existingId, newVertices, vertexFromTo);
                polygon.setVertexId(i, newId);
            }

            for (int i = 0; i < polygon.getNumNormals(); i++) {
                int existingId = polygon.getNormalId(i);
                int newId = remapVertex(vertexNormals, existingId, newNormals, normalFromTo);
                polygon.setNormalId(i, newId);
            }
        }

        vertices.clear();
        vertexNormals.clear();
        vertices.addAll(newVertices);
        vertexNormals.addAll(newNormals);

        packed = true;
    }

    public void pack() {
        Map<Vertex, Integer> vertexMap = new HashMap<>(vertices.size());
        Map<Vertex, Integer> normalMap = new HashMap<>(vertexNormals.size());


        List<Vertex> newVertices = new ArrayList<>(vertices.size());
        List<Vertex> newNormals = new ArrayList<>(vertexNormals.size());

        int[] vertexFromTo = new int[vertices.size()];
        int[] normalFromTo = new int[vertexNormals.size()];

        for (MeshPolygon polygon : polygons) {
            for (int i = 0; i < polygon.getNumVertices(); i++) {
                int existingId = polygon.getVertexId(i);
                int newId = remapVertex(vertices, existingId, newVertices, vertexMap, vertexFromTo);
                polygon.setVertexId(i, newId);
            }

            for (int i = 0; i < polygon.getNumNormals(); i++) {
                int existingId = polygon.getNormalId(i);
                int newId = remapVertex(vertexNormals, existingId, newNormals, normalMap, normalFromTo);
                polygon.setNormalId(i, newId);
            }
        }

        vertices.clear();
        vertexNormals.clear();
        vertices.addAll(newVertices);
        vertexNormals.addAll(newNormals);

        packed = true;
    }

    private int remapVertex(List<Vertex> vertices, int oldId, List<Vertex> newVertices, int[] lookup) {
        int newId = lookup[oldId - 1];
        if (newId == 0) {
            newVertices.add(vertices.get(oldId - 1));
            newId = newVertices.size();
        }
        lookup[oldId - 1] = newId;
        return newId;
    }

    private int remapVertex(List<Vertex> vertices, int oldId, List<Vertex> newVertices, Map<Vertex, Integer> map, int[] lookup) {
        int newId = lookup[oldId - 1];
        Vertex vertex = vertices.get(oldId - 1);
        if (newId == 0 && map != null) {
            newId = map.getOrDefault(vertex, 0);
        }
        if (newId == 0) {
            newVertices.add(vertex);
            newId = newVertices.size();
        }
        lookup[oldId - 1] = newId;
        if (map != null) {
            map.put(vertex, newId);
        }
        return newId;
    }

    public Mesh translate(Vertex offset) {
        return translate(offset.x, offset.y, offset.z);
    }

    public Mesh translate(double x, double y, double z) {
        Mesh newMesh = new Mesh(concave, polygons.size());

        for (Vertex vertex : vertices) {
            newMesh.vertices.add(vertex.translate(x, y , z));
        }

        newMesh.polygons.addAll(polygons);

        return newMesh;
    }


    public Mesh transform(Matrix transform) {
        Mesh newMesh = new Mesh(concave, polygons.size());

        for (Vertex vertex : vertices) {
            newMesh.vertices.add(transform.mul(vertex));
        }

        Vertex transformedOrigin = transform.mul(Vertex.ORIGIN);
        for (Vertex vertexNormal : vertexNormals) {
            newMesh.vertexNormals.add(transform.mul(vertexNormal).subtract(transformedOrigin));
        }

        newMesh.polygons.addAll(polygons);

        return newMesh;
    }

    public void transformInPlace(Matrix transform) {
        for (Vertex vertex : vertices) {
            transform.mul(vertex, vertex);
        }

        Vertex transformedOrigin = transform.mul(Vertex.ORIGIN);
        for (Vertex vertexNormal : vertexNormals) {
            transform.mul(vertexNormal, vertexNormal);
            vertexNormal.subtractInPlace(transformedOrigin);
        }

        meshContext = new Object();
    }

    long timerStart;

    private void startTimer() {
        timerStart = System.currentTimeMillis();
    }
    private void time(String message, boolean print) {
        long end = System.currentTimeMillis();
        if (print) {
            long duration = end - timerStart;
            System.out.println(message + " " + duration);
        }
        timerStart = end;
    }

    public Mesh copyIncludingPolygons() {
        Mesh copy = copy(false);
        for (int i = 0; i < polygons.size(); i++) {
            MeshPolygon MeshPolygon = polygons.get(i);
            copy.polygons.set(i, MeshPolygon.copy());
        }
        return copy;
    }

    public Mesh copy(boolean print) {
        startTimer();
        Mesh newMesh = new Mesh(concave, polygons.size());
        time("new Mesh", print);
        for (Vertex vertex : vertices) {
            newMesh.vertices.add(new Vertex(vertex.x, vertex.y, vertex.z));
        }
        time("new vertices", print);
        for (Vertex vertexNormal : vertexNormals) {
            newMesh.vertexNormals.add(new Vertex(vertexNormal.x, vertexNormal.y, vertexNormal.z));
        }
        time("new normals", print);
        newMesh.polygons.addAll(polygons);
        time("polygons", print);
        return newMesh;
    }

    public Vertex getVertexById(int vertexId) {
        return vertices.get(vertexId - 1);
    }

    public Vertex getNormalById(int normalId) {
        int index = normalId - 1;
        if (index >= vertexNormals.size()) {
            System.out.println("Found our guy");
        }
        return vertexNormals.get(normalId - 1);
    }

    public int getNumVertices() {
        return vertices.size();
    }

    public int getNumNormals() {
        return vertexNormals.size();
    }

    public final int getNumPolygons() {
        return polygons.size();
    }

    public final int addVertex(Vertex vertex) {
        vertices.add(vertex);
        return vertices.size();
    }

    public int addNormal(Vertex normalVertex) {
        vertexNormals.add(normalVertex);
        return vertexNormals.size();
    }

    public final List<MeshPolygon> getPolygons() {
        return polygons;
    }

    public void add(Mesh mesh) {
        int prevVertexCount = vertices.size();
        int prevNormalCount = vertexNormals.size();

        vertices.addAll(mesh.vertices);
        vertexNormals.addAll(mesh.vertexNormals);

        for (MeshPolygon polygon : mesh.polygons) {
            MeshPolygon newPolygon = polygon.copy();
            newPolygon.remapVertices(prevVertexCount);
            newPolygon.remapNormals(prevNormalCount);
            polygons.add(newPolygon);
        }
    }

    public Object getMeshContext() {
        return meshContext;
    }

    private class Worker extends RecursiveAction {

        int start;
        int vertexSize;

        int normalSize;

        Vertex[] vertexDest;

        Vertex[] normalDest;
        List<Vertex> vertices;
        List<Vertex> normals;
        Matrix transform;

        public Worker(int start, int vertexSize, int normalSize, Vertex[] vertexDest, Vertex[] normalDest, List<Vertex> vertices, List<Vertex> normals, Matrix transform) {
            this.start = start;
            this.vertexSize = vertexSize;
            this.normalSize = normalSize;
            this.vertexDest = vertexDest;
            this.normalDest = normalDest;
            this.vertices = vertices;
            this.normals = normals;
            this.transform = transform;
        }

        @Override
        protected void compute() {
            if (vertexSize != 0 && normalSize != 0) {
                invokeAll(new Worker(start, vertexSize, 0, vertexDest, normalDest, vertices, normals, transform),
                        new Worker(start, 0, normalSize, vertexDest, normalDest, vertices, normals, transform));
                return;
            }

            int size = vertexSize > 0 ? vertexSize : normalSize;
            if (size < 5000) {
                computeDirectly();
            }
            else {
                int split = size / 2;
                int vSize1 = 0, vSize2 = 0;
                int nSize1 = 0, nSize2 = 0;
                if (vertexSize > 0) {
                    vSize1 = split;
                    vSize2 = size - split;
                } else {
                    nSize1 = split;
                    nSize2 = size - split;
                }
                invokeAll(new Worker(start, vSize1, nSize1, vertexDest, normalDest, vertices, normals, transform),
                        new Worker(start + split, vSize2, nSize2, vertexDest, normalDest, vertices, normals, transform));
            }
        }



        private void computeDirectly() {
            for (int i = 0; i < vertexSize; i++) {
                Vertex vertex = vertices.get(i + start);
                vertexDest[start + i] = transform.mul(vertex);
            }

            for (int i = 0; i < normalSize; i++) {
                Vertex vertex = normals.get(i + start);
                normalDest[start + i] = transform.mul(vertex);
            }
        }
    }

//    private class PolygonCopyWorker extends RecursiveAction {
//
//        private int start;
//        private int len;
//
//        private Polygon[] polygonDest;
//
//        private List<Polygon> polygons;
//
//        private Mesh newMesh;
//
//        public PolygonCopyWorker(int start, int len, Polygon[] polygonDest, List<Polygon> polygons, Mesh newMesh) {
//            this.start = start;
//            this.len = len;
//            this.polygonDest = polygonDest;
//            this.polygons = polygons;
//            this.newMesh = newMesh;
//        }
//
//        @Override
//        protected void compute() {
//            if (len < 5000) {
//                computeDirectly();
//            } else {
//                int split = len / 2;
//                invokeAll(new PolygonCopyWorker(start, split, polygonDest, polygons, newMesh),
//                        new PolygonCopyWorker(start + split, len - split, polygonDest, polygons, newMesh));
//            }
//        }
//
//        private void computeDirectly() {
//            for ( int i = 0; i < len; i++) {
//                Polygon polygon = polygons.get(start + i);
//                polygonDest[start + i] = polygon.copyToMesh(newMesh);
//            }
//        }
//    }

    public Mesh transform(Matrix transform, ForkJoinPool forkJoinPool) {

        Vertex[] vertexDest = new Vertex[vertices.size()];
        Vertex[] normalDest = new Vertex[vertexNormals.size()];

        forkJoinPool.invoke(new Worker(0, vertices.size(), vertexNormals.size(), vertexDest, normalDest, vertices, vertexNormals, transform));

        Mesh newMesh = new Mesh(concave);

        newMesh.vertices.addAll(Arrays.asList(vertexDest));
        newMesh.vertexNormals.addAll(Arrays.asList(normalDest));

        //Polygon[] polygonDest = new Polygon[polygons.size()];

        //forkJoinPool.invoke(new PolygonCopyWorker(0, polygons.size(), polygonDest, polygons, newMesh));

        //newMesh.polygons.addAll(Arrays.asList(polygonDest));
        newMesh.polygons.addAll(polygons);

        return newMesh;
    }

    public void sort(Comparator<? super MeshPolygon> c) {
        polygons.sort(c);
    }

    public void replacePolygons(List<MeshPolygon> newList) {
        polygons.clear();
        polygons.addAll(newList);
        packed = false;
    }
}
