package org.example.Food;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.Random;
public class Food {

    private double xx;
    private double yy;
    private double nutrition = 2;
    private Circle circle;
    private Random random;

    public Food(double x, double y, Color color)
    {
        this.xx = x;
        this.yy = y;

        this.circle = new Circle(5,color);
        circle.setCenterX(xx);
        circle.setCenterY(yy);
    }
    public Circle getCircle() {
        return circle;
    }
    public double getNutrition()
    {
        return nutrition;
    }

}
