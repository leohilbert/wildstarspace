package com.wildstar.gameobjects.impl;

import java.awt.geom.Point2D.Float;

import com.wildstar.core.Calculation2D;
import com.wildstar.gameobjects.Gameobject;

public class Ball extends Gameobject
{
	// ----------------------------------------------------------------------------------------------------------------------------------------------
    public Ball(float x, float y, int radius, double angle)
    {
        super(x, y, radius, radius);
        this.angle = angle;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void move()
    {
        Float tar = Calculation2D.getPointByAngleAndDegrees(this.pos, this.speed, (float) this.angle);
        this.pos = tar;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean collidesX(float checkX)
    {
        return this.pos.x - this.width <= checkX && this.pos.x + this.width >= checkX;
    }
	// ----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean collidesY(float checkY)
    {
        return this.pos.y - this.height <= checkY && this.pos.y + this.height >= checkY;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}
