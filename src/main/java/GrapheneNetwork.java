import graph.Collection;
import graph.Graph;
import graph.Vertex;
import processing.core.PApplet;
import processing.core.PVector;

import static parameters.Parameters.*;
import static save.SaveUtil.saveSketch;

public class GrapheneNetwork extends PApplet {

    private Graph g;

    public static void main(String[] args) {
        PApplet.main(GrapheneNetwork.class);
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
        randomSeed(SEED);
        noiseSeed(floor(random(MAX_INT)));
    }

    @Override
    public void setup() {
        background(BACKGROUND_COLOR.red(), BACKGROUND_COLOR.green(), BACKGROUND_COLOR.blue());
        stroke(STROKE_COLOR.red(), STROKE_COLOR.green(), STROKE_COLOR.blue(), STROKE_COLOR.alpha());
        fill(FILL_COLOR.red(), FILL_COLOR.green(), FILL_COLOR.blue(), FILL_COLOR.alpha());
        frameRate(-1);

        g = new Graph(this);
        for (int k = 0; k < NUMBER_OF_VERTICES; k++) {
            Vertex v = new Vertex(PVector.random2D(this)
                    .mult(pow(random(1), RADIUS_POWER) * INITIAL_MAXIMUM_RADIUS)
                    .add(width / 2f, height / 2f));
            g.add(v);
        }
        Collection c = new Collection(CELL_SIZE);
        c.addAll(g.getVertices());
        g.createLinks(c);
        g.addLinks(g.getVertices().size());
    }

    @Override
    public void draw() {
        g.render();
        float m = g.move();
        if (m < MAXIMUM_SPEED || frameCount >= MAXIMUM_ITERATIONS) {
            noLoop();
            saveSketch(this);
        }
    }
}
