package com.wildstar.gameobjects.impl;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.geom.Point2D;

import com.wildstar.core.Calculation2D;
import com.wildstar.core.Configuration;
import com.wildstar.core.ResourceLoader;
import com.wildstar.core.Weapon;
import com.wildstar.gameobjects.Gameobject;

public class Player extends Gameobject
{
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    float movedX;
    float movedY;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private long pwTime;
    private Weapon weapon;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public int score;
    public Laser laser;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public Player(float x, float y)
    {
        super(x, y, ResourceLoader.img_player.getWidth(), ResourceLoader.img_player.getHeight());

        this.movedX = 0f;
        this.movedY = 0f;
        this.speed = 5f;
        this.laser = new Laser(this);
        this.score = 0;
        this.weapon = Weapon.NORMAL;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void queryMove(float x, float y)
    {
        this.movedX += x;
        this.movedY += y;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void queryX(float x)
    {
        this.movedX += x;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void queryY(float y)
    {
        this.movedY += y;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void update(int boundsX, int boundsY)
    {
        // --------------- X-Koordinate -----------------
        if (this.movedX >= this.speed)
        {
            float toMove = this.pos.x + this.speed;
            if (Math.round(toMove + 20) <= boundsX)
            {
                this.pos.x += this.speed;
            }
            this.movedX -= this.speed;
        }
        else if (this.movedX <= -this.speed)
        {
            float toMove = this.pos.x - this.speed;
            if (Math.round(toMove) > 0)
            {
                this.pos.x -= this.speed;
            }
            if (this.movedX > this.speed)
                this.movedX -= this.speed;
            else
                this.movedX = 0;
        }
        else
        {
            this.movedX = 0;
        }

        // --------------- Y-Koordinate -----------------
        if (this.movedY >= this.speed)
        {
            float toMove = this.pos.y + this.speed;
            if (Math.round(toMove + 40) <= boundsY)
            {
                this.pos.y += this.speed;
            }
            this.movedY -= this.speed;
        }
        else if (this.movedY <= -this.speed)
        {
            float toMove = this.pos.y - this.speed;
            if (Math.round(toMove) > 0)
            {
                this.pos.y -= this.speed;
            }
            if (this.movedY > this.speed)
                this.movedY -= this.speed;
            else
                this.movedY = 0;
        }
        else
        {
            this.movedY = 0;
        }
        
        if(this.weapon != Weapon.NORMAL && this.getPowerLeft() <= 0)
        {
            this.weapon = Weapon.NORMAL;
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public int getPowerLeft()
    {
        return 10 - (int)((System.currentTimeMillis()-this.pwTime)/1000);
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean collidesX(float checkX)
    {
        return this.pos.x - (this.width/2) <= checkX && this.pos.x + (this.width/2) >= checkX;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean collidesY(float checkY)
    {
        return this.pos.y - (this.height/2) <= checkY && this.pos.y + (this.height/2) >= checkY;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void shootBall()
    {
        Point location = MouseInfo.getPointerInfo().getLocation();
        Point2D.Float mouseposition = new Point2D.Float(location.x, location.y);

        double mouseAngle = Calculation2D.calcAngle(this.pos, mouseposition);

        switch(this.weapon)
        {
            case NORMAL:
                this.field.balls.add(new Ball(this.pos.x, this.pos.y, 6, mouseAngle));
                ResourceLoader.sound_ball.playSoundOnce();
                break;
            case MISSLE:
                this.field.balls.add(new MissleBall(this.pos.x, this.pos.y, 6, mouseAngle));
                ResourceLoader.sound_ball.playSoundOnce();
                break;
            case MINE:
                this.field.balls.add(new Mine(this.pos.x, this.pos.y, 6));
                ResourceLoader.sound_mine.playSoundOnce();
                break;
            case BOMB:
                this.explode(Configuration.SHOCKWAVE_WITH_MISSILE);
                ResourceLoader.sound_explosion.playSoundOnce();
                break;
            default:
                break;            
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void move() {}
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void setPowerUp(Weapon weapon)
    {
        this.pwTime = System.currentTimeMillis();
        this.weapon = weapon;
        ResourceLoader.sound_powerup.reallyPlaySoundOnce();
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public Weapon getWeapon()
    {
        return this.weapon;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}
