package graph;

public class Structure {
    private float distance;
    private Vertex parent, child;

    public Structure(Vertex child, float distance) {
        this.child = child;
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public Vertex getParent() {
        return parent;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }

    public Vertex getChild() {
        return child;
    }

    public void setChild(Vertex child) {
        this.child = child;
    }
}
