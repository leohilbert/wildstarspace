package com.wildstar.core;


import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.wildstar.gameobjects.impl.Ball;
import com.wildstar.gameobjects.impl.Enemy;
import com.wildstar.gameobjects.impl.Mine;
import com.wildstar.gameobjects.impl.MissleBall;
import com.wildstar.gameobjects.impl.Player;
import com.wildstar.gameobjects.impl.Powerup;

public class Start extends JFrame implements KeyListener, MouseListener
{
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private static final Cursor DEFAULTCURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
    private static final Cursor EMPTYCURSOR = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(1, 1), "");
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private static final long serialVersionUID = 1L;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private Thread threadCalculation;
    private Thread threadMouse;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    static long started;
    static boolean gameStarted = false;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    long lastSpawn;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private Thread keyThread;
    private HashSet<Integer> keys = new HashSet<Integer>();
    private HashSet<Integer> clicked = new HashSet<Integer>();
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private View view;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    Field field;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static boolean pause = false;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public Start() throws Exception
    {
        this.setTitle("Wildstar Space Simulator");

        this.addKeyListener(this);
        int width = 1500;
        int height = 1000;
        this.setSize(width, height);
        this.setVisible(true);
        
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                e.getWindow().dispose();
                System.exit(0);
            }
        });
        this.setCursor(EMPTYCURSOR); //$NON-NLS-1$
        this.field = Field.getInstance();
        this.field.player = new Player((width/2)-10, height/2 + 80);
        this.setIconImage(ResourceLoader.img_icon);
        
        this.view = new View(width, height, this);
        this.view.setBounds(0, 0, width, height);
        this.add(this.view);
        
        started = System.currentTimeMillis();

        this.threadCalculation = new Thread("Calculation")
        {
            @Override
            public void run()
            {
                while (this.isAlive())
                {
                    try
                    {
                        if(!Start.pause) onTimerCalculation();
                        Thread.sleep(25);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.threadCalculation.start();
        
        this.threadMouse = new Thread()
        {
            @Override
            public void run()
            {
                while (this.isAlive())
                {
                    try
                    {
                        if(!Start.pause) onMouseThread();
                        Thread.sleep(200);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.threadMouse.start();
        
        this.keyThread = new Thread()
        {
            @Override
            public void run()
            {
                while (this.isAlive())
                {
                    try
                    {
                        Thread.sleep(30);
                        onKeyThread();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.keyThread.start();
        
        this.revalidate();
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("unused")
    public static void main(String[] args) throws Exception
    {
        PrintStream printStream = new PrintStream(new BufferedOutputStream(new FileOutputStream("out.log")), true);
        System.setOut(printStream);
        System.setErr(printStream);
        new Start();
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    protected void onTimerCalculation()
    {
        Set<Enemy> deleteEnemy = new HashSet<Enemy>();
        Set<Ball> deleteBall = new HashSet<Ball>();
        
        update(deleteEnemy, deleteBall);
        
        long currTime = System.currentTimeMillis();
        long l = 500 - (currTime - Start.started)/1000;
        if(gameStarted && currTime - this.lastSpawn > (Configuration.FREEMODE ? Configuration.SPAWN_TIMER:Math.max(l, 1)))
        {
            this.lastSpawn = currTime;
            spawn();
        }

        this.field.enemys.removeAll(deleteEnemy);
        this.field.balls.removeAll(deleteBall);
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private void update(Set<Enemy> deleteEnemy, Set<Ball> deleteBall)
    {
        try
        {
            this.field.player.update(this.getWidth(),this.getHeight());
            if(this.field.player.collides(this.field.powerup))
            {
                this.field.player.setPowerUp(this.field.powerup.type);
                this.field.powerup = null;
            }
            
            for(Enemy enemy:this.field.enemys)
            {
                calcEnemy(enemy, deleteEnemy, deleteBall);
            }
            
            this.calcBallOOB(deleteBall);
        }
        catch (ConcurrentModificationException e)
        {
//            e.printStackTrace();
            // Mir egal!
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private void calcEnemy(Enemy enemy, Set<Enemy> deleteEnemy, Set<Ball> deleteBall)
    {
        enemy.move();
        Float ePos = enemy.pos;
        
        //Ball-Kollision
        for(Ball ball:this.field.balls)
        {
            Float bPos = ball.pos;
            if((enemy.collidesX(bPos.x) || ball.collidesX(ePos.x))
            && (enemy.collidesY(bPos.y) || ball.collidesY(ePos.y)))
            {
                deleteBall.add(ball);
                if(ball instanceof Mine || (ball instanceof MissleBall && Configuration.MISSLE_EXPLODES))
                {
                    ball.explode(false);
                }
                this.field.player.score++;

                ResourceLoader.sound_destroy.playSoundOnce();
                deleteEnemy.add(enemy);
            }
        }
        
        //Spieler-Kollision
        Float pPos = this.field.player.pos;
        if ((enemy.collidesX(pPos.x) || this.field.player.collidesX(ePos.x)) && (enemy.collidesY(pPos.y) || this.field.player.collidesY(ePos.y)))
        {
            ResourceLoader.sound_track.stopSound();
            ResourceLoader.sound_destroy2.reallyPlaySoundOnce();
            Start.pause = true;
            JOptionPane.showMessageDialog(this, "You died.", "GAME OVER", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        if (this.field.player.laser.isActive())
        {
            Point location = MouseInfo.getPointerInfo().getLocation();
            Point2D.Float mouse = new Point2D.Float(location.x, location.y);
            
            if ((ePos.x <= pPos.x && mouse.x <= pPos.x || ePos.x >= pPos.x && mouse.x >= pPos.x)
            && ((ePos.y <= pPos.y && mouse.y <= pPos.y || ePos.y >= pPos.y && mouse.y >= pPos.y)))
            {
                float pWidth = this.field.player.width;
                float pHeight = this.field.player.height;
                
                AffineTransform aff = new AffineTransform(AffineTransform.getTranslateInstance(pPos.x - pWidth / 2, pPos.y - pHeight / 2));
                
                aff.rotate(Math.toRadians(Calculation2D.calcAngle(pPos, mouse) - 180), pWidth / 2, pHeight / 2);
                Area ar = new Area(aff.createTransformedShape(new Rectangle2D.Float(pWidth / 2 - 1, pHeight / 2, 2, Calculation2D.pythagoras(pPos, mouse))));
                
                ar.intersect(new Area(new Rectangle2D.Double(ePos.x, ePos.y, enemy.width, enemy.height)));
                if (!ar.isEmpty())
                {
                    deleteEnemy.add(enemy);
                    this.field.player.score++;
                    ResourceLoader.sound_destroy.playSoundOnce();
                }
            }
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private void calcBallOOB(Set<Ball> deleteBall)
    {
        for(Ball ball:this.field.balls)
        {
            ball.move();
            
            Float pos = ball.pos;
            if(pos.x >= this.getWidth() || pos.y >= this.getHeight()
            || pos.x <= 0 || pos.y <= 0)
            {
                deleteBall.add(ball);
            }
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private void spawn()
    {
        Random rnd = new Random();
        int side = rnd.nextInt(4);
		if(side==0 || side==1) // X
        {
		    int where = rnd.nextInt(this.getWidth())+1;
            if(side==0) // Oben
            {
                this.field.enemys.add(new Enemy(where, -Enemy.imgheight));
            }
            else // Unten
            {
                this.field.enemys.add(new Enemy(where, this.getHeight()));
            }
        }
        else // Y
        {
            int where = rnd.nextInt(this.getHeight())+1;
            if(side==2) // Links
            {
                this.field.enemys.add(new Enemy(-Enemy.imgheight, where));
            }
            else // Rechts
            {
                this.field.enemys.add(new Enemy(this.getWidth(), where));
            }
        }
		
		if(this.field.player.getWeapon() == Weapon.NORMAL && this.field.powerup == null && Math.floor(Math.random()*100) == 0)
		{
	        this.field.powerup = new Powerup((int)Math.floor(Math.random()*(this.getWidth()-Powerup.diameter*2)),
	                                         (int)Math.floor(Math.random()*(this.getHeight()-Powerup.diameter*2)));
	        ResourceLoader.sound_spawn.reallyPlaySoundOnce();
		}
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    protected void onMouseThread()
    {
        if(this.clicked.contains(MouseEvent.BUTTON1))
        {
            if(this.field.player.getWeapon() == Weapon.LASER)
            {
                this.field.player.laser.setActive(true);
            }
            else
            {
                this.field.player.shootBall();
                this.field.player.laser.setActive(false);
            }
        }
        else
        {
            this.field.player.laser.setActive(false);
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    protected void onKeyThread()
    {
        if(!Start.pause)
        {
            if(this.keys.contains(KeyEvent.VK_D) && !this.keys.contains(KeyEvent.VK_A))
            {
                this.field.player.queryX(this.field.player.speed);
            }
            else if(this.keys.contains(KeyEvent.VK_A) && !this.keys.contains(KeyEvent.VK_D))
            {
                this.field.player.queryX(-this.field.player.speed);
            }
            if(this.keys.contains(KeyEvent.VK_S) && !this.keys.contains(KeyEvent.VK_W))
            {
                this.field.player.queryY(this.field.player.speed);
            }
            else if(this.keys.contains(KeyEvent.VK_W) && !this.keys.contains(KeyEvent.VK_S))
            {
                this.field.player.queryY(-this.field.player.speed);
            }
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void keyReleased(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE && gameStarted)
        {
            pause = !pause;
            if(pause)
            {
                this.setCursor(DEFAULTCURSOR);
                ResourceLoader.sound_track.stopSound();
            }
            else
            {
                this.setCursor(EMPTYCURSOR);
                ResourceLoader.sound_track.playSound();
            }
            ResourceLoader.sound_pause.reallyPlaySoundOnce();
        }
        if (Configuration.FREEMODE)
        {
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_1:
                    this.field.player.setPowerUp(Weapon.NORMAL);
                    break;
                case KeyEvent.VK_2:
                    this.field.player.setPowerUp(Weapon.MISSLE);
                    break;
                case KeyEvent.VK_3:
                    this.field.player.setPowerUp(Weapon.MINE);
                    break;
                case KeyEvent.VK_4:
                    this.field.player.setPowerUp(Weapon.BOMB);
                    break;
                case KeyEvent.VK_5:
                    this.field.player.setPowerUp(Weapon.LASER);
                    break;
            }
        }
        this.keys.remove(e.getKeyCode());
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void keyPressed(KeyEvent e)
    {
        this.keys.add(e.getKeyCode());
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void mousePressed(MouseEvent e)
    {
        this.clicked.add(e.getButton());
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void mouseReleased(MouseEvent e)
    {
        this.clicked.remove(e.getButton());
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void keyTyped(KeyEvent e) {}
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void mouseClicked(MouseEvent e) {}
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void mouseEntered(MouseEvent e) {}
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void mouseExited(MouseEvent e) {}
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}