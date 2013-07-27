package com.wildstar.gameobjects;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import com.wildstar.core.Calculation2D;
import com.wildstar.core.Field;
import com.wildstar.core.ResourceLoader;
import com.wildstar.gameobjects.impl.Ball;
import com.wildstar.gameobjects.impl.MissleBall;


public abstract class Gameobject implements LineListener
{
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public Point2D.Float pos;
    public int width;
    public int height;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public float speed;
    public double angle;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static Set<Clip> sounds = new HashSet<Clip>();
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    protected Field field;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public Gameobject(float x, float y, int width, int height)
    {
        this(x, y);
        this.width = width;
        this.height = height;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public Gameobject(float x, float y)
    {
        this.pos = new Point2D.Float(x, y);
        this.speed = 3f;
        this.angle = 10d;
        this.field = Field.getInstance();
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public abstract void move();
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public abstract boolean collidesX(float checkX);
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public abstract boolean collidesY(float checkY);
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void update(LineEvent e)
    {
        Clip source = (Clip) e.getSource();
        if (e.getFramePosition() == source.getFrameLength())
        {
            source.close();
            sounds.remove(source);
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void explode(boolean missle)
    {
        for(int i=0;i<36;i++)
        {
            Point2D.Float f2 = Calculation2D.getPointByAngleAndDegrees(this.pos, 1, i*10);
            double CalcAngle = Calculation2D.calcAngle(f2, this.pos);
            if(missle) this.field.balls.add(new MissleBall(this.pos.x, this.pos.y, 6, CalcAngle));
            else this.field.balls.add(new Ball(this.pos.x, this.pos.y, 6, CalcAngle));
        }
        ResourceLoader.sound_explosion.playSoundOnce();
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public boolean collides(Gameobject go)
    {
        return go != null && ((this.collidesX(go.pos.x) || go.collidesX(this.pos.x)) && (this.collidesY(go.pos.y) || go.collidesY(this.pos.y)));
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}
