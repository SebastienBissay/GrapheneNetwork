package graph;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import static parameters.Parameters.*;
import static processing.core.PApplet.*;

public class Graph {
    private static PApplet pApplet;
    List<Vertex> vertices;

    public Graph(PApplet pApplet) {
        vertices = new ArrayList<>();
        Graph.pApplet = pApplet;
    }

    public void add(Vertex v) {
        vertices.add(v);
    }

    public void createLinks(Collection c) {
        List<Vertex> linked = new ArrayList<>();
        linked.add(vertices.get(0));
        while (linked.size() < vertices.size()) {
            Structure s = c.getClosest(linked);
            if (s.getChild() == null) {
                break;
            }
            s.getParent().getLinked().add(s.getChild());
            s.getChild().getLinked().add(s.getParent());
            linked.add(s.getChild());
        }
    }

    public void addLinks(int n) {
        for (int k = 0; k < n; k++) {
            addLink();
        }
    }

    private void addLink() {
        List<Vertex> candidates = new ArrayList<>();
        int tries = 0;
        Vertex v;
        do {
            v = vertices.get(floor(pApplet.random(vertices.size())));
            tries++;
            if (tries > vertices.size()) {
                return;
            }
        }
        while (v.getLinked().size() >= MAXIMUM_LINKS);
        for (int l = 0; l < vertices.size() / TRIES_FACTOR; l++) {
            boolean done = false;
            tries = 0;
            while (!done && tries < TRIES_FACTOR * vertices.size()) {
                tries++;
                Vertex w = vertices.get(floor(pApplet.random(vertices.size())));
                if (!w.equals(v) && !v.getLinked().contains(w)) {
                    boolean isOk = true;
                    for (Vertex v1 : vertices) {
                        Vertex finalV = v;
                        if (v1.getLinked().stream()
                                .filter(li -> li.compare(v1) > 0)
                                .anyMatch(li -> doIntersect(finalV.getPosition(),
                                        w.getPosition(),
                                        v1.getPosition(),
                                        li.getPosition()))) {
                            isOk = false;
                            break;
                        }
                    }
                    if (isOk) {
                        candidates.add(w);
                        done = true;
                    }
                }
            }
        }
        float minDist = MAX_FLOAT;
        Vertex newLink = null;
        for (Vertex w : candidates) {
            if (PVector.sub(v.getPosition(), w.getPosition()).magSq() < minDist) {
                newLink = w;
                minDist = PVector.sub(v.getPosition(), w.getPosition()).magSq();
            }
        }
        if (newLink != null) {
            v.getLinked().add(newLink);
            newLink.getLinked().add(v);
        }
    }

    public void render() {
        for (Vertex v : vertices) {
            for (Vertex l : v.getLinked()) {
                if (l.compare(v) > 0) {
                    myLine(v.getPosition(), l.getPosition());
                }
            }
        }
        for (Vertex v : vertices) {
            float d = 0;
            for (Vertex l : v.getLinked()) {
                d += PVector.sub(v.getPosition(), l.getPosition()).mag() / v.getLinked().size();
            }
            myCircle(v.getPosition(), RADIUS_FACTOR * d);
        }
    }

    public float move() {
        float maxSpeed = 0;
        for (Vertex v : vertices) {
            v.setSpeed(new PVector(0, 0));
        }
        for (Vertex v : vertices) {
            float d = 0;
            for (Vertex l : v.getLinked()) {
                d += min(MAXIMUM_DISTANCE, PVector.sub(v.getPosition(), l.getPosition()).mag()) / v.getLinked().size();
            }
            for (Vertex l : v.getLinked()) {
                PVector f = PVector.sub(l.getPosition(), v.getPosition());
                f.setMag(f.mag() - d);
                v.setSpeed(v.getSpeed().add(f));
                l.setSpeed(l.getSpeed().sub(f));
            }
            for (Vertex w : vertices) {
                if (!w.equals(v) && !v.getLinked().contains(w)) {
                    PVector f = PVector.sub(v.getPosition(), w.getPosition());
                    if (f.magSq() < sq(MAXIMUM_DISTANCE / 2f)) {
                        v.setSpeed(v.getSpeed().add(f));
                    }
                }
            }
            if (v.getPosition().x < MARGIN) {
                v.getSpeed().x += MARGIN - v.getPosition().x;
            }
            if (v.getPosition().x > WIDTH - MARGIN) {
                v.getSpeed().x -= v.getPosition().x - WIDTH + MARGIN;
            }
            if (v.getPosition().y < MARGIN) {
                v.getSpeed().y += MARGIN - v.getPosition().y;
            }
            if (v.getPosition().y > HEIGHT - MARGIN) {
                v.getSpeed().y -= v.getPosition().y - HEIGHT + MARGIN;
            }
        }
        for (Vertex v : vertices) {
            maxSpeed = max(maxSpeed, v.getSpeed().magSq());
            v.setPosition(v.getPosition().add(PVector.div(v.getSpeed(), SPEED_REDUCTION)));
        }
        return maxSpeed;
    }

    private int orientation(PVector p, PVector q, PVector r) {
        float o = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        return Float.compare(o, 0);
    }

    private boolean doIntersect(PVector p1, PVector q1, PVector p2, PVector q2) {
        if (p1 == p2 || p1 == q2 || q1 == p2 || q1 == q2)
            return false;
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        return o1 != o2 && o3 != o4;
    }

    private void myLine(PVector A, PVector B) {
        pApplet.stroke(STROKE_COLOR.red(), STROKE_COLOR.green(), STROKE_COLOR.blue(), STROKE_COLOR.alpha());
        float d = PVector.sub(A, B).mag();
        for (int i = 0; i < d / 2; i++) {
            float t = pApplet.random(1);
            PVector p = PVector.lerp(A, B, t);
            pApplet.point(p.x, p.y);
        }
    }

    private void myCircle(PVector c, float r) {
        pApplet.stroke(STROKE_COLOR.red(), STROKE_COLOR.green(), STROKE_COLOR.blue(), STROKE_COLOR.alpha());
        for (int i = 0; i < TWO_PI * r; i++) {
            PVector p = PVector.fromAngle(pApplet.random(TWO_PI)).mult(r).add(c);
            pApplet.point(p.x, p.y);
        }
        pApplet.stroke(FILL_COLOR.red(), FILL_COLOR.green(), FILL_COLOR.blue(), FILL_COLOR.alpha());
        for (int i = 0; i < PI * sq(r); i++) {
            PVector p = PVector.fromAngle(pApplet.random(TWO_PI)).mult(sqrt(pApplet.random(1)) * r).add(c);
            pApplet.point(p.x, p.y);
        }
    }

    public List<Vertex> getVertices() {
        return vertices;
    }
}
