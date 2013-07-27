package com.wildstar.core;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ResourceLoader
{
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static Sound sound_ball;
    public static Sound sound_mine;
    public static Sound sound_explosion;
    public static Sound sound_destroy;
    public static Sound sound_destroy2;
    public static Sound sound_laser;
    public static Sound sound_track;
    public static Sound sound_powerup;
    public static Sound sound_spawn;
    public static Sound sound_cd1;
    public static Sound sound_cd2;
    public static Sound sound_pause;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static BufferedImage img_player;
    public static BufferedImage img_enemy;
    public static BufferedImage img_bg;
    public static BufferedImage img_powerup;
    public static BufferedImage img_icon;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    static
    {
        Class<ResourceLoader> class1 = ResourceLoader.class;
        try
        {
            sound_ball = new Sound(class1.getResource("/ball.wav"));
            sound_mine = new Sound(class1.getResource("/mine.wav"));
            sound_explosion = new Sound(class1.getResource("/explosion.wav"));
            sound_destroy = new Sound(class1.getResource("/destroy.wav"));
            sound_destroy2 = new Sound(class1.getResource("/destroy2.wav"));
            sound_laser = new Sound(class1.getResource("/laser.wav"));
            sound_track = new Sound(class1.getResource("/track.wav"));
            sound_powerup = new Sound(class1.getResource("/powerup.wav"));
            sound_spawn = new Sound(class1.getResource("/spawn.wav"));
            sound_cd1 = new Sound(class1.getResource("/cd1.wav"));
            sound_cd2 = new Sound(class1.getResource("/cd2.wav"));
            sound_pause = new Sound(class1.getResource("/pause.wav"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        try
        {
            img_player = ImageIO.read(class1.getResource("/player.png"));
            img_enemy = ImageIO.read(class1.getResource("/enemy.png"));
            img_bg = ImageIO.read(class1.getResource("/bg.png"));
            img_powerup = ImageIO.read(class1.getResource("/powerup.png"));
            img_icon = ImageIO.read(class1.getResource("/icon.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}
