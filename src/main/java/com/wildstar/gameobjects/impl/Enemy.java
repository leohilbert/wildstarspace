package com.wildstar.gameobjects.impl;

import java.awt.geom.Point2D;
import java.util.Random;

import com.wildstar.core.Calculation2D;
import com.wildstar.core.ResourceLoader;
import com.wildstar.gameobjects.Gameobject;



public class Enemy extends Gameobject
{
	// ----------------------------------------------------------------------------------------------------------------------------------------------
    public static int imgwidth = ResourceLoader.img_enemy.getWidth();
    public static int imgheight = ResourceLoader.img_enemy.getHeight();
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public Enemy(float x, float y)
    {
        super(x, y, imgwidth, imgheight);
        this.speed = new Random().nextInt(2)+1;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void move()
    {
        double calcAngle = Calculation2D.calcAngle(this.pos, this.field.player.pos);
        Point2D.Float f2 = Calculation2D.getPointByAngleAndDegrees(this.pos, this.speed, (float)calcAngle);
        this.pos.setLocation(f2);
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
	@Override
    public boolean collidesX(float checkX)
	{
		return this.pos.x <= checkX && this.pos.x + this.width >= checkX;
	}
	// ----------------------------------------------------------------------------------------------------------------------------------------------
	@Override
    public boolean collidesY(float checkY)
	{
		return this.pos.y <= checkY && this.pos.y + this.height >= checkY;
	}
	// ----------------------------------------------------------------------------------------------------------------------------------------------
}
