package org.example.Player;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.example.Food.Food;

public class Player {
    private String name;
    private Double xx;
    private Double yy;
    private Color color;
    private double size = 25;
    private Integer eatenFood = 0;
    private  Integer maxSize = 150;

    private Circle circle;

    public Player(Double x, Double y,Color color,String name)
    {
        this.xx = x;
        this.yy = y;
        this.color = color;
        this.name = name;

        this.circle = new Circle(size,color);
        circle.setCenterX(xx);
        circle.setCenterY(yy);
    }

    public Circle getCircle()
    {
        return circle;
    }

    public void move(double dx, double dy)
    {
        xx+=dx;
        yy+=dy;

        circle.setCenterX(xx);
        circle.setCenterY(yy);
    }
    public void eatFood(Food food)
    {
        size += food.getNutrition();
        circle.setRadius(size);
        eatenFood++;
    }

    public Double getXx() {
        return xx;
    }

    public void setXx(Double xx) {
        this.xx = xx;
    }

    public Double getYy() {
        return yy;
    }

    public void setYy(Double yy) {
        this.yy = yy;
    }

    public void setSize(double size)
    {
        this.size = size;
    }
    public double getSize()
    {
        return size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Integer getEatenFood()
    {
        return eatenFood;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }
}

