package graph;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static parameters.Parameters.HEIGHT;
import static parameters.Parameters.WIDTH;
import static processing.core.PApplet.*;

public class Collection {
    private final List<List<Vertex>> cells;
    private final float cellSize;
    private final int xSize;
    private final int ySize;

    public Collection(float cellSize) {
        this.cellSize = cellSize;
        xSize = ceil(WIDTH / this.cellSize);
        ySize = ceil(HEIGHT / this.cellSize);
        cells = new ArrayList<>();
        for (int k = 0; k < xSize * ySize; k++) {
            cells.add(new ArrayList<>());
        }
    }

    public void add(Vertex vertex) {
        int x = constrain(floor(vertex.getPosition().x / cellSize), 0, xSize - 1);
        int y = constrain(floor(vertex.getPosition().y / cellSize), 0, ySize - 1);
        cells.get(x + xSize * y).add(vertex);
    }

    public void addAll(List<Vertex> vertices) {
        vertices.forEach(this::add);
    }

    public Structure getClosest(Vertex v) {
        int offset = 0;
        int x = floor(v.getPosition().x / cellSize);
        int y = floor(v.getPosition().y / cellSize);
        boolean found = false;
        float d = MAX_FLOAT;
        Vertex closest = null;
        while (!found) {
            for (int i = max(0, x - offset); i < min(xSize, x + offset + 1); i++) {
                for (int j = max(0, y - offset); j < min(ySize, y + offset + 1); j++) {
                    if (i == x - offset || i == x + offset || j == y - offset || j == y + offset) {
                        for (Vertex w : cells.get(i + xSize * j)) {
                            if (!w.isLinked() && !v.equals(w)) {
                                if (!found) {
                                    found = true;
                                    d = PVector.sub(v.getPosition(), w.getPosition()).magSq();
                                    closest = w;
                                } else {
                                    float tmp = PVector.sub(v.getPosition(), w.getPosition()).magSq();
                                    if (tmp < d) {
                                        d = tmp;
                                        closest = w;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (x - offset < 0 && x + offset > xSize && y - offset < 0 && y + offset > ySize) {
                break;
            } else {
                offset++;
            }
        }
        return new Structure(closest, d);
    }

    public Structure getClosest(List<Vertex> vertices) {
        Structure closest = new Structure(null, MAX_FLOAT);

        for (Vertex v : vertices) {
            Structure s = getClosest(v);
            if (s.getChild() != null && (closest.getChild() == null || s.getDistance() < closest.getDistance())) {
                closest.setChild(s.getChild());
                closest.setDistance(s.getDistance());
                closest.setParent(v);
            }
        }
        return closest;
    }
}
