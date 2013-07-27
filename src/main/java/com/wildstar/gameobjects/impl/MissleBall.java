package com.wildstar.gameobjects.impl;

import java.awt.geom.Point2D;
import java.util.Random;

import com.wildstar.core.Calculation2D;
import com.wildstar.core.Configuration;



public class MissleBall extends Ball
{
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    Enemy enemy;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public MissleBall(float x, float y, int radius, double angle)
    {
        super(x, y, radius, angle);
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void move()
    {
        if(this.enemy == null || !this.field.enemys.contains(this.enemy))
        {
            if(Configuration.MISSLE_TARGETS_NEAREST_ENEMY)
                this.enemy = this.getNearestEnemy();
            else
            {
                try
                {
                    this.enemy = this.getRandomEnemy();
                }
                catch (Exception e)
                {
                    // Keine Gegner zum anvisieren.
                }
            }
            return;
        }
        Point2D.Float enemyPos = this.enemy.pos;
        
        double nangle = Calculation2D.getNormalDegrees(this.angle);
        double ncalc = Calculation2D.getNormalDegrees(Calculation2D.calcAngle(this.pos, enemyPos));
//        System.out.println(nangle + " -- " + ncalc);
        
        if(nangle < ncalc)
            this.angle += Math.toDegrees(Configuration.MISSLE_TURN_SPEED);
        else if(nangle > ncalc)
            this.angle -= Math.toDegrees(Configuration.MISSLE_TURN_SPEED);
        
        Point2D.Float f2 = Calculation2D.getPointByAngleAndDegrees(this.pos, this.speed, (float)this.angle);
        this.pos.setLocation(f2);
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private Enemy getNearestEnemy()
    {
        Enemy target = null;
        float currMin = 0f;
        for(Enemy en:this.field.enemys)
        {
            float pythagoras = Calculation2D.pythagoras(this.pos, en.pos);

            if(currMin > pythagoras || currMin == 0f)
            {
                currMin = pythagoras;
                target = en;
            }
        }
        return target;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private Enemy getRandomEnemy()
    {
        int item = new Random().nextInt(this.field.enemys.size());
        int i = 0;
        for(Enemy obj : this.field.enemys)
        {
            if (i++ == item)
                return obj;
        }
        return null;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}
