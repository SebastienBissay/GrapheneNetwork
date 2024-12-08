package graph;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
    private static int indexCounter = 0;
    private final List<Vertex> linked = new ArrayList<>();
    private final int index;
    private PVector position;
    private PVector speed = new PVector(0, 0);

    public Vertex(PVector position) {
        this.position = position;
        this.index = indexCounter++;
    }

    public boolean isLinked() {
        return !linked.isEmpty();
    }

    public PVector getPosition() {
        return position;
    }

    public void setPosition(PVector position) {
        this.position = position;
    }

    public PVector getSpeed() {
        return speed;
    }

    public void setSpeed(PVector speed) {
        this.speed = speed;
    }

    public List<Vertex> getLinked() {
        return linked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return index == vertex.index;
    }

    public int compare(Vertex vertex) {
        return Integer.compare(index, vertex.index);
    }
}
