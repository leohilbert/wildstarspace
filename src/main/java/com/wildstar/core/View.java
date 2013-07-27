package com.wildstar.core;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.image.VolatileImage;
import java.util.ConcurrentModificationException;
import java.util.HashSet;

import javax.swing.JPanel;

import com.wildstar.gameobjects.impl.Ball;
import com.wildstar.gameobjects.impl.Enemy;
import com.wildstar.gameobjects.impl.Mine;
import com.wildstar.gameobjects.impl.MissleBall;
import com.wildstar.gameobjects.impl.Powerup;

public class View extends JPanel
{
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    private JPanel drawingPanel;
    VolatileImage newDisplay;
    private Thread threadDisplay;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private Field field;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private int lastFps, frames, lastSecond;
    private long oldl;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public View(int width, int height, Start start)
    {
        this.setFocusTraversalKeysEnabled(true);
        this.setIgnoreRepaint(true);
        this.setBackground(Color.BLACK);
        this.setSize(width, height);
        this.field = Field.getInstance();
        this.setFocusable(true);
        
        this.addMouseListener(start);
        this.addKeyListener(start);
        
        this.drawingPanel = new JPanel(true)
        {
            private static final long serialVersionUID = 1L;
            
            @Override
            public void paint(Graphics g)
            {
                try
                {
                    g.drawImage(View.this.newDisplay, 0, 0, null);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        this.add(this.drawingPanel);
        
        this.threadDisplay = new Thread("Drawing")
        {
            @Override
            public void run()
            {
                while (this.isAlive())
                {
                    try
                    {
                        View.this.onTimerDisplay();
                        Thread.sleep(16);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.threadDisplay.start();
        
        this.setLayout(new GridLayout());
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    protected synchronized void onTimerDisplay()
    {
        int w = this.getWidth();
        int h = this.getHeight();
        
        if (w <= 0 || h <= 0)
            return;
        
        if (this.newDisplay == null || (this.newDisplay.getWidth(null) != w || this.newDisplay.getHeight(null) != h))
        {
            GraphicsConfiguration grconfig = this.getGraphicsConfiguration();
            if(grconfig == null) return;
            
            this.newDisplay = grconfig.createCompatibleVolatileImage(w, h);
        }
        
        Graphics2D g = this.newDisplay.createGraphics();
        if(!Start.pause)
        {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            this.drawOnImage(g);
        }
        else
        {
            g.setColor(Color.WHITE);
            String str = String.valueOf("PAUSED");
            Rectangle2D bound = g.getFontMetrics().getStringBounds(str, g);
            g.drawString(str, (int) (this.getWidth()/2 - bound.getWidth()/2), this.getHeight()/2);
        }
        g.dispose();        
        this.repaint();
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private void drawOnImage(Graphics2D g)
    {
        try
        {
            g.drawImage(ResourceLoader.img_bg, 0, 0, null);
            
            HashSet<Ball> balls = new HashSet<Ball>(this.field.balls);
            HashSet<Enemy> enemys = new HashSet<Enemy>(this.field.enemys);

            if(this.field.powerup != null)
            {
                g.setColor(Color.black);
                g.fill(new Ellipse2D.Float(this.field.powerup.pos.x, this.field.powerup.pos.y, Powerup.diameter, Powerup.diameter));
                g.drawImage(ResourceLoader.img_powerup, (int)this.field.powerup.pos.x, (int)this.field.powerup.pos.y, null);
            }

            for(Ball ball:balls)
            {
                if(ball instanceof MissleBall)
                    g.setColor(Color.RED);
                else if(ball instanceof Mine)
                    g.setColor(Color.DARK_GRAY);
                else
                    g.setColor(Color.CYAN);
                g.fill(new Ellipse2D.Float(ball.pos.x, ball.pos.y, ball.width, ball.height));
            }

            for(Enemy enemy:enemys)
            {
                AffineTransform aff = new AffineTransform(AffineTransform.getTranslateInstance(enemy.pos.x, enemy.pos.y));
                aff.rotate(Math.toRadians(Calculation2D.calcAngle(enemy.pos, this.field.player.pos)), enemy.width/2, enemy.height/2);
                g.drawImage(ResourceLoader.img_enemy, aff, null);
            }
            
            this.drawPlayer(g);
            this.drawUI(g);
        }
        catch (ConcurrentModificationException e)
        {
            // Mir egal!
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private void drawUI(Graphics2D g)
    {
        if(Start.gameStarted)
        {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 70, 15);
            g.setColor(Color.GREEN);
            String score = (Configuration.FREEMODE)?"FREEMODE":"Score: " + String.valueOf(this.field.player.score);
            g.drawString(score, 1, 11);
    
            g.setColor(Color.WHITE);
            g.drawString("by pFreak", this.getWidth()/2, this.getHeight() - 10);
            
            if(this.field.player.getWeapon() != Weapon.NORMAL)
                g.drawString("POWERTIME: " + this.field.player.getPowerLeft(), 10, this.getHeight() - 30);
            g.drawString("Weapon: " + this.field.player.getWeapon(), 10, this.getHeight() - 10);
        }
        else
        {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Lucida Sans Unicode", Font.BOLD, 50));
            long l = 3 - (System.currentTimeMillis() - Start.started)/1000;
            if(l > 0)
            {
                String str = "Game will start in";
                Rectangle2D titleBound = g.getFontMetrics().getStringBounds(str, g);
                String count = String.valueOf(l);
                Rectangle2D countBound = g.getFontMetrics().getStringBounds(count, g);
                g.drawString(str, (int) (this.getWidth()/2 - titleBound.getWidth()/2), this.getHeight()/2);
                g.drawString(count, (int) (this.getWidth()/2 - countBound.getWidth()/2), this.getHeight()/2 + 60);
                if(l!=this.oldl)
                {
                    this.oldl = l;
                    ResourceLoader.sound_cd1.playSoundOnce();
                }
            }
            else
            {
                Start.gameStarted = true;
                ResourceLoader.sound_cd2.reallyPlaySoundOnce();
                ResourceLoader.sound_track.playSound();
            }
        }
        
        this.drawFPS(g);

        // Maus
        Point location = MouseInfo.getPointerInfo().getLocation();
        Point2D.Float mouse = new Point2D.Float(location.x, location.y);
        g.draw(new Line2D.Float(mouse.x - Configuration.SCOPESIZE, mouse.y, mouse.x + Configuration.SCOPESIZE, mouse.y));
        g.draw(new Line2D.Float(mouse.x, mouse.y - Configuration.SCOPESIZE, mouse.x, mouse.y + Configuration.SCOPESIZE));
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private void drawFPS(Graphics2D g)
    {
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + this.lastFps, this.getWidth() - 55, 11);

        this.frames++;
        int nsec = (int) ((Start.started - System.currentTimeMillis())/1000);
        if (nsec != this.lastSecond)
        {
            this.lastFps = this.frames;
            this.frames = 0;
        }
        this.lastSecond = nsec;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private void drawPlayer(Graphics2D g)
    {
        Float pos = this.field.player.pos;

        Point location = MouseInfo.getPointerInfo().getLocation();
        Point2D.Float mouseposition = new Point2D.Float(location.x, location.y);

        AffineTransform aff = new AffineTransform(AffineTransform.getTranslateInstance(pos.x - this.field.player.width / 2, pos.y - this.field.player.height / 2));
        aff.rotate(Math.toRadians(Calculation2D.calcAngle(pos, mouseposition)), this.field.player.width  / 2, this.field.player.width  / 2);

        // Laser?
        AffineTransform orgAff = g.getTransform();
        g.setTransform(aff);
        if (this.field.player.laser.isActive())
        {
            g.setColor(Color.red);
            g.rotate(Math.toRadians(180), this.field.player.width/2, this.field.player.height/2);
            g.fill(new Rectangle2D.Float(this.field.player.width/2-1, this.field.player.height/2, 2, Calculation2D.pythagoras(this.field.player.pos, mouseposition)));
        }
        g.setTransform(orgAff);

        g.drawImage(ResourceLoader.img_player, aff, null);
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}
