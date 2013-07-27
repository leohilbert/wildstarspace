package com.wildstar.gameobjects.impl;

import com.wildstar.core.Weapon;
import com.wildstar.gameobjects.Gameobject;

public class Powerup extends Gameobject
{
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static final int diameter = 50;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public Weapon type;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public Powerup(float x, float y)
    {
        super(x, y, diameter, diameter);
        Weapon[] values = Weapon.values();
        this.type = values[(int) Math.floor(Math.random()*(values.length-1))+1];
        System.out.println("POWERUP SPAWNED: " + this.type);
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
    @Override
    public void move() {}
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}
