package com.wildstar.core;


import java.util.HashSet;
import java.util.Set;

import com.wildstar.gameobjects.impl.Ball;
import com.wildstar.gameobjects.impl.Enemy;
import com.wildstar.gameobjects.impl.Player;
import com.wildstar.gameobjects.impl.Powerup;

public class Field
{
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private static Field field;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public Set<Enemy> enemys;
    public Set<Ball> balls;
    public Player player;
    public Powerup powerup;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private Field()
    {
        this.enemys = new HashSet<Enemy>();
        this.balls = new HashSet<Ball>();
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static Field getInstance()
    {
        if(field == null)
        {
            field = new Field();
        }
        return field;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}
