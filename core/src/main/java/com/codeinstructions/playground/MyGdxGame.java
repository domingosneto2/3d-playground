package com.codeinstructions.playground;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Matrix4;
import com.codeinstructions.playground.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import static com.badlogic.gdx.math.MathUtils.clamp;

public class MyGdxGame extends ApplicationAdapter {

	ImmediateModeRenderer20 renderer;

	Matrix4 identity;

	Matrix projectionMatrix;

	int width;

	int height;

	double dx = 0;
	double dy = 0;

	double fov = 60;

	double zNear = 1;

	double zFar = 20;

	double dz = 4;

	Matrix rotation = Transform.identity();

	int polygonCount = Integer.MAX_VALUE;

	private static final int CUBE = 0;
	private static final int TETRAHEDRON = 1;
	private static final int SPHERE = 2;
	private static final int TORUS = 3;
	private static final int ICOSAHEDRON = 4;
	private static final int GEODESIC = 5;

	private static final int FIRST_MODEL = 0;
	private static final int LAST_MODEL = GEODESIC;

	private static final List<Model> models = new ArrayList<>();

	static {
		models.add(Spiker.model(Cube.model(), 0));
		models.add(FractalTetrahedron.model(0));
		models.add(Spiker.model(Sphere.model(10, 10), 0));
		models.add(Spiker.model(Torus.model(20, 20, 0.3), 0));
		models.add(Icosahedron.model());
		models.add(Spiker.model(Geodesic.model(2), 0));
	}

	private Mesh mesh = models.get(0).mesh();

	Matrix objectTransform = Transform.identity();

	Vertex light = new Vertex(0, 5, -5);

	int model = CUBE;

	boolean wireframe = true;

	boolean waswireframe = true;

	boolean smoothLighting = false;

	boolean drawNormals = false;

	boolean drawLightRays = false;




	@Override
	public void create () {
		renderer = new ImmediateModeRenderer20(false, true, 0);
		identity = new Matrix4();
		identity.idt();

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Input.Keys.L) {
					return toggleLightRays();
				}
				if (keycode == Input.Keys.N) {
					return toggleNormals();
				}
				if (keycode == Input.Keys.Y) {
					return toggleSmoothLighting();
				}
				if (keycode == Input.Keys.I) {
					return resetSpikeLength();
				}
				if (keycode == Input.Keys.TAB) {
					return changeModel();
				}
				if (keycode == Input.Keys.NUMPAD_ADD || keycode == Input.Keys.LEFT_BRACKET) {
					return increaseDetail();
				}
				if (keycode == Input.Keys.NUMPAD_SUBTRACT || keycode == Input.Keys.RIGHT_BRACKET) {
					return decreaseDetail();
				}
				if (keycode == Input.Keys.NUMPAD_0 || keycode == Input.Keys.NUM_0) {
					return togglePolygonCount();
				}
				if (keycode == Input.Keys.ENTER) {
					return addOnePolygon();
				}
				if (keycode == Input.Keys.DEL) {
					return removeOnePolygon();
				}
				if (keycode == Input.Keys.F) {
					return toggleWireframe();
				}
				return false;
			}
		});
	}

	private boolean toggleWireframe() {
		wireframe = !wireframe;
		return true;
	}

	private boolean removeOnePolygon() {
		if (polygonCount != 0) {
			polygonCount--;
		}
		return true;
	}

	private boolean addOnePolygon() {
		if (polygonCount < mesh.getNumPolygons()) {
			polygonCount++;
		}
		return true;
	}

	private boolean togglePolygonCount() {
		if (polygonCount == 0) {
			polygonCount = mesh.getNumPolygons();
		} else {
			polygonCount = 0;
		}
		return true;
	}

	private boolean decreaseDetail() {
		models.get(model).decreaseDetail();
		mesh = models.get(model).mesh();
		polygonCount = mesh.getNumPolygons();

		return true;
	}

	private boolean increaseDetail() {
		models.get(model).increaseDetail();
		mesh = models.get(model).mesh();
		polygonCount = mesh.getNumPolygons();
		return true;
	}

	private boolean changeModel() {
		int curModel = model;
		curModel++;
		if (curModel > LAST_MODEL) {
			curModel = FIRST_MODEL;
		}
		model = curModel;
		mesh = models.get(model).mesh();
		polygonCount = mesh.getNumPolygons();

		return true;
	}

	private boolean resetSpikeLength() {
		Model curModel = models.get(model);
		if (curModel instanceof Spiker<?> spiker) {
			spiker.resetSpikeLength();
		}
		return true;
	}

	private boolean toggleSmoothLighting() {
		smoothLighting = !smoothLighting;
		return true;
	}

	private boolean toggleNormals() {
		drawNormals = !drawNormals;
		return true;
	}

	private boolean toggleLightRays() {
		drawLightRays = !drawLightRays;
		return true;
	}

	long lastfps = 0;
	int frameCounter = 0;

	@Override
	public void render () {
		startTimer();
		double thetaX = 0;
		double thetaY = 0;
		double thetaZ = 0;

		double spikeDelta = 0;

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) dx -= 0.2 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dx += 0.2 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.UP)) dy += 0.2 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) dy -= 0.2 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.W)) dz += 0.2 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.S)) dz -= 0.2 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)) fov -= 10 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) fov += 10* Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.Q)) thetaX = 0.5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.E)) thetaX = -0.5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.A)) thetaY = 0.5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.D)) thetaY = -0.5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.Z)) thetaZ = 0.5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.C)) thetaZ = -0.5 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.P)) spikeDelta += 0.1 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.O)) spikeDelta -= 0.1 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.V)) zNear -= 0.1 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.B)) zNear += 0.1 * Gdx.graphics.getDeltaTime();

		int prevMeshSize = mesh.getNumPolygons();

		Model curModel = models.get(model);
		if (curModel instanceof Spiker<?> currentSpiker) {
			currentSpiker.setSpikeDelta(spikeDelta);
			mesh = currentSpiker.mesh();
		}

		if (polygonCount == prevMeshSize) {
			polygonCount = mesh.getNumPolygons();
		}

		// Rotate the light
		light = light.rotateY(Gdx.graphics.getDeltaTime() / 2);

		Mesh currentMesh = mesh;

		if (polygonCount > currentMesh.getNumPolygons()) {
			polygonCount = currentMesh.getNumPolygons();
		}

		rotation = rotation
				.rotationX(thetaX)
				.rotationY(thetaY)
				.rotationZ(thetaZ);

		objectTransform = rotation.translation(dx, dy, dz);

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);

		height = Gdx.graphics.getHeight();
		width = Gdx.graphics.getWidth();

		projectionMatrix = Transform.projection(width, height, fov, 100, 0.2);


		long currentTimestamp = System.currentTimeMillis();
		boolean printLog = false;
		if (lastfps == 0) {
			lastfps = System.currentTimeMillis();
		} else if (currentTimestamp - lastfps > 1000) {
			printLog = true;
			System.out.println("FPS: " + frameCounter + " Mesh: " + currentMesh.getNumPolygons());
			lastfps = currentTimestamp;
			frameCounter = 0;
		}
		//printLog = false;

		frameCounter++;
		ForkJoinPool pool = new ForkJoinPool(8);

		time("housekeeping", printLog);
		Mesh transformedMesh = currentMesh.copy(printLog);
		time("copy", printLog);
		transformedMesh.transformInPlace(objectTransform);
		time("objectTransform", printLog);

		Mesh floor = new Mesh();

		int numTiles = 20;
		float tileSize = 1;

		for (int i = 0; i < numTiles; i++) {
			for (int j = 0; j < numTiles; j++) {
				Vertex v1 = new Vertex(-tileSize * numTiles / 2 + i * tileSize, -2, 1 + j * tileSize);
				Vertex v2 = v1.add(Vertex.I.mul(tileSize));
				Vertex v3 = v1.add(Vertex.K.mul(tileSize));
				Vertex v4 = v2.add(Vertex.K.mul(tileSize));

				Polygon tile = new Polygon(v1, v3, v4, v2);
				tile.setMaterial((i + j) % 2 == 0 ? Color.WHITE : Color.GRAY);
				floor.add(tile);
			}
		}

		if (!wireframe) {
			backFaceCulling(transformedMesh, pool);
			time("backFaceCulling", printLog);
			computeLighting(transformedMesh);
			computeLighting(floor);
			time("computeLighting", printLog);
		}

		if (wireframe && !waswireframe) {
			resetBackFaceCulling(transformedMesh);
		}



		waswireframe = wireframe;

		transformedMesh.transformInPlace(projectionMatrix);
		floor.transformInPlace(projectionMatrix);
		Mesh projectedMesh = transformedMesh;
		time("projectionMatrix", printLog);

		if (!projectedMesh.isConcave() && !wireframe) {
			sortPolygons(projectedMesh, printLog);
			time("sortPolygons", printLog);
		}

		drawPolygons(floor, false);
		drawPolygons(projectedMesh, true);
		time("drawPolygons", printLog);

		if (drawNormals || drawLightRays) {
			Mesh transformedMesh2 = mesh.copyIncludingPolygons().transform(objectTransform);

			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendEquationSeparate(GL20.GL_FUNC_ADD, GL20.GL_FUNC_ADD);
			Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ZERO);
			for (int i = 0; i < transformedMesh2.getNumPolygons() && i < polygonCount; i++) {

				MeshPolygon polygon = transformedMesh2.getPolygons().get(i);
				drawLightRays(transformedMesh2, polygon);
			}
		}
	}

	private void resetBackFaceCulling(Mesh transformedMesh) {
		for (MeshPolygon polygon : transformedMesh.getPolygons()) {
			polygon.setBackFaceCulling(false);
		}
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

	private class CullingWorker extends RecursiveAction {

		private List<MeshPolygon> polygonList;

		private Mesh mesh;

		int start;

		int len;

		public CullingWorker(int start, int len, List<MeshPolygon> polygonList, Mesh mesh) {
			this.polygonList = polygonList;
			this.start = start;
			this.len = len;
			this.mesh = mesh;
		}

		@Override
		protected void compute() {
			if (len > 5000) {
				int split = len / 2;
				invokeAll(new CullingWorker(start, split, polygonList, mesh), new CullingWorker(start + split, len - split, polygonList, mesh));
			} else {
				for (int i = start; i < start + len; i++) {
					MeshPolygon polygon = polygonList.get(i);
					Vertex normal = polygon.normal(mesh);
					Vertex center = polygon.center(mesh);

					if (normal.dot(center) >= 0) {
						polygon.setBackFaceCulling(true);
					} else {
						polygon.setBackFaceCulling(false);
					}
				}
			}
		}
	}

	private void backFaceCulling(Mesh transformedMesh, ForkJoinPool pool) {
		pool.invoke(new CullingWorker(0, transformedMesh.getNumPolygons(), transformedMesh.getPolygons(), transformedMesh));
		List<MeshPolygon> newList = new ArrayList<>(transformedMesh.getPolygons().size());
		for (MeshPolygon polygon : transformedMesh.getPolygons()) {
			if (!polygon.isBackFaceCulling()) {
				newList.add(polygon);
			}
		}
		transformedMesh.replacePolygons(newList);
	}

	private void drawPolygons(Mesh projectedMesh, boolean countPolygons) {
		int count = 0;
		if (!wireframe) {
			renderer.begin(identity, GL20.GL_TRIANGLES);
		} else {
			renderer.begin(identity, GL20.GL_LINES);
		}
		for (MeshPolygon polygon : projectedMesh.getPolygons()) {
			if (count >= polygonCount && countPolygons) {
				break;
			}
			count++;

			if (polygon.isBackFaceCulling()) {
				if (!wireframe && !projectedMesh.isConcave()) {
					// In this case we sorted the polygons earlier, and the culled ones are in the end of the list,
					// so we can break out of the loop now
					break;
				}
			} else {
				drawPolygon2(polygon, projectedMesh);
			}
		}

		renderer.end();
	}

	private void sortPolygons(Mesh projectedMesh, boolean logSortingDuration) {
		long sortStart = System.currentTimeMillis();
		projectedMesh.sort((p1, p2) -> {
			int cullCompare = Boolean.compare(p1.isBackFaceCulling(), p2.isBackFaceCulling());
			if (cullCompare != 0) {
				return cullCompare;
			}
			Vertex c1 = p1.center(projectedMesh);
			Vertex c2 = p2.center(projectedMesh);
			return Double.compare(c1.z, c2.z);
		});
		long sortEnd = System.currentTimeMillis();

		if (logSortingDuration)
			System.out.println("Sort duration: " + (sortEnd - sortStart));
	}

	private void computeLighting(Mesh transformedMesh) {
		Color c = new Color();
		Color c0 = new Color();
		Color c1 = new Color();
		Color c2 = new Color();
		Color c3 = new Color();
		for (MeshPolygon polygon : transformedMesh.getPolygons()) {
			if (polygon.isBackFaceCulling()) {
				continue;
			}

			Vertex normal = polygon.normal(transformedMesh);
			Vertex center = polygon.center(transformedMesh);

			Color material = polygon.material;

			if (polygon.getNumNormals() == 0 || !smoothLighting) {
				computeColor(center, normal, material, c);
				polygon.setColor(c.r, c.g, c.b);
				polygon.setHasVertexColors(false);
			} else {
				computeColor(polygon.getP0(), polygon.getN0(), material, c0, transformedMesh);
				computeColor(polygon.getP1(), polygon.getN1(), material, c1, transformedMesh);
				computeColor(polygon.getP2(), polygon.getN2(), material, c2, transformedMesh);

				computeColor(center, normal, material, c);
				polygon.setColor(c.r, c.g, c.b);

				polygon.setC0(c0.r, c0.g, c0.b);
				polygon.setC1(c1.r, c1.g, c1.b);
				polygon.setC2(c2.r, c2.g, c2.b);
				if (polygon.getNumNormals() > 3) {
					computeColor(polygon.getP3(), polygon.getN3(), material, c3, transformedMesh);
					polygon.setC3(c3.r, c3.g, c3.b);
				}
				polygon.setHasVertexColors(true);
			}
		}
	}

	private void drawLightRays(Mesh transformedMesh, MeshPolygon polygon) {

		Vertex normal = polygon.normal(transformedMesh);
		Vertex center = polygon.center(transformedMesh);

		Vertex lightRay = center.subtract(light).normalize();

		renderer.begin(identity, GL20.GL_LINES);

		if (normal.dot(center) <= 0) {
			if (drawLightRays) {
				float dot = (float) normal.normalize().dot(lightRay);
				if (dot < 0) {
					Vertex clippedLight = light;
					if (light.z < zNear) {
						double dz = center.z - light.z;
						double clippingRatio = center.z - zNear;

						clippedLight = light.subtract(center).mul(clippingRatio / dz).add(center);
					}
					drawLine(projectionMatrix.mul(clippedLight), projectionMatrix.mul(center), Color.YELLOW.toFloatBits());
				}
			}
		}

		if (normal.dot(center) > 0 && !wireframe) {
			return;
		}

		if (drawNormals) {
			drawLine(projectionMatrix.mul(center), projectionMatrix.mul(center.add(normal.normalize().mul(1 / 5.))), Color.RED.toFloatBits());


			if (polygon.getNumNormals() > 0) {
				Vertex v0 = polygon.getV0(transformedMesh);
				Vertex n0 = transformedMesh.getNormalById(polygon.getN0());
				drawLine(projectionMatrix.mul(v0), projectionMatrix.mul(v0.add(n0.normalize().mul(1 / 5.))), Color.BLUE.toFloatBits());
				Vertex v1 = polygon.getV1(transformedMesh);
				Vertex n1 = transformedMesh.getNormalById(polygon.getN1());
				drawLine(projectionMatrix.mul(v1), projectionMatrix.mul(v1.add(n1.normalize().mul(1 / 5.))), Color.BLUE.toFloatBits());
				Vertex v2 = polygon.getV2(transformedMesh);
				Vertex n2 = transformedMesh.getNormalById(polygon.getN2());
				drawLine(projectionMatrix.mul(v2), projectionMatrix.mul(v2.add(n2.normalize().mul(1 / 5.))), Color.BLUE.toFloatBits());
				if (polygon.getNumNormals() > 3) {
					Vertex v3 = polygon.getV3(transformedMesh);
					Vertex n3 = transformedMesh.getNormalById(polygon.getN3());
					drawLine(projectionMatrix.mul(v3), projectionMatrix.mul(v3.add(n3.normalize().mul(1 / 5.))), Color.BLUE.toFloatBits());
				}
			}
		}
		renderer.end();
	}

	private void drawPolygon2(MeshPolygon polygon, Mesh mesh) {
		if (wireframe) {
			Vertex v0 = polygon.getV0(mesh);
			Vertex v1 = polygon.getV1(mesh);
			Vertex v2 = polygon.getV2(mesh);
			drawLine(v0, v1);
			drawLine(v1, v2);
			if (polygon.getNumVertices() == 3) {
				drawLine(v2, v0);
			} else {
				Vertex v3 = polygon.getV3(mesh);
				drawLine(v2, v3);
				drawLine(v3, v0);
			}
		} else {
			if (!polygon.isHasVertexColors()) {
				float color = Color.toFloatBits(polygon.r, polygon.g, polygon.b, 1);
				Vertex v0 = polygon.getV0(mesh);
				Vertex v1 = polygon.getV1(mesh);
				Vertex v2 = polygon.getV2(mesh);
				triangle(v0, v1, v2, color);

				if (polygon.getNumVertices() > 3) {
					v1 = v2;
					v2 = polygon.getV3(mesh);
					triangle(v0, v1, v2, color);
				}
			} else {
				Vertex v0 = polygon.getV0(mesh);
				Vertex v1 = polygon.getV1(mesh);
				Vertex v2 = polygon.getV2(mesh);
				float c0 = Color.toFloatBits(polygon.r0, polygon.g0, polygon.b0, 1);
				float c1 = Color.toFloatBits(polygon.r1, polygon.g1, polygon.b1, 1);
				float c2 = Color.toFloatBits(polygon.r2, polygon.g2, polygon.b2, 1);

				if (polygon.getNumVertices() == 3) {
					triangle(v0, v1, v2, c0, c1, c2);
				}
				if (polygon.getNumVertices() > 3) {
					Vertex v3 = polygon.getV3(mesh);
					float c3 = Color.toFloatBits(polygon.r3, polygon.g3, polygon.b3, 1);
					Vertex center = polygon.center(mesh);
					float cc = Color.toFloatBits(polygon.r, polygon.g, polygon.b, 1);
					triangle(v0, v1, center, c0, c1, cc);
					triangle(v1, v2, center, c1, c2, cc);
					triangle(v2, v3, center, c2, c3, cc);
					triangle(v3, v0, center, c3, c0, cc);
				}
			}
		}
	}

	private void triangle(Vertex v0, Vertex v1, Vertex v2, float color) {
		triangle(v0, v1, v2, color, color, color);
	}
	private void triangle(Vertex v0, Vertex v1, Vertex v2, float c0, float c1, float c2) {
		if (renderer.getNumVertices() > renderer.getMaxVertices() - 100) {
			renderer.flush();
		}

		renderer.color(c0);
		renderer.vertex((float) v0.x, (float) v0.y, (float) v0.z);
		renderer.color(c1);
		renderer.vertex((float) v1.x, (float) v1.y, (float) v1.z);
		renderer.color(c2);
		renderer.vertex((float) v2.x, (float) v2.y, (float) v2.z);
	}

	private void computeColor(int vertexId, int normalId, Color material, Color result, Mesh mesh) {
		Vertex vertex = mesh.getVertexById(vertexId);
		Vertex normal = mesh.getNormalById(normalId);
		computeColor(vertex, normal, material, result);
	}

	private void computeColor(Vertex vertex, Vertex normal, Color material, Color result) {
		Vertex lightDistance = vertex.subtract(light);
		Vertex lightRay = lightDistance.normalize();
		float dot = (float) normal.normalize().dot(lightRay);

		// Make so light shines with strength = 1 at the origin and then falls off with the inverse square law
		double lightIntensity = light.norm() / lightDistance.norm();
		lightIntensity *= lightIntensity;

		double light = -dot * lightIntensity;
		light = light < 0 ? 0 : light;
		double ambient = 0.2f;
		float lightValue = (float)(light / 2 + ambient);
		result.r = clamp(material.r * lightValue, 0, 1);
		result.g = clamp(material.g * lightValue, 0, 1);
		result.b = clamp(material.b * lightValue, 0, 1);

	}

	private void drawLine(Vertex v1, Vertex v2, float color) {
		if (renderer.getNumVertices() > renderer.getMaxVertices() - 100) {
			renderer.flush();
		}
		renderer.color(color);
		renderer.vertex((float)v1.x, (float)v1.y, (float)v1.z);
		renderer.color(color);
		renderer.vertex((float)v2.x, (float)v2.y, (float)v2.z);
	}

	private void drawLine(Vertex v1, Vertex v2) {
		drawLine(v1, v2, Color.WHITE_FLOAT_BITS);
	}
}