package com.wildstar.core;

import java.io.BufferedInputStream;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class Sound
{
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private Clip clip;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    Sound(URL url)
    {
        try
        {
            AudioInputStream sound = AudioSystem.getAudioInputStream(new BufferedInputStream(url.openStream()));
            DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
            this.clip = (Clip) AudioSystem.getLine(info);
            this.clip.open(sound);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void playSound()
    {
        this.clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void stopSound()
    {
        this.clip.stop();
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void playSoundOnce()
    {
        this.clip.loop(1);
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public void reallyPlaySoundOnce()
    {
        this.clip.setFramePosition(0);
        this.clip.start();
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}
