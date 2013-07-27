package com.wildstar.gameobjects.impl;

import com.wildstar.core.ResourceLoader;
import com.wildstar.gameobjects.Gameobject;

public class Laser extends Gameobject
{
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private boolean active = false;
    public Player parent;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public Laser(Player parent)
    {
        super(parent.pos.x, parent.pos.y, 0, 0);
        this.parent = parent;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean collidesX(float checkX)
    {
        return false;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean collidesY(float checkY)
    {
        return false;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    @Deprecated
    /**
     * Keine Funktion
     */
    public void move()
    {}
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public boolean isActive()
    {
        return this.active;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void setActive(boolean active)
    {
        if(this.active != active)
        {
            this.active = active;
            if(active)
            {
                try
                {
                    ResourceLoader.sound_laser.playSound();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else ResourceLoader.sound_laser.stopSound();
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}
