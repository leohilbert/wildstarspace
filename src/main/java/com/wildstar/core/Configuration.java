package com.wildstar.core;

import java.util.Properties;

public class Configuration
{
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static boolean FREEMODE;
    public static int SCOPESIZE = 5;
    public static int SPAWN_TIMER;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static float MISSLE_TURN_SPEED;
    public static boolean MISSLE_TARGETS_NEAREST_ENEMY;
    public static boolean MISSLE_EXPLODES;
    public static boolean SHOCKWAVE_WITH_MISSILE;
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    static
    {
        try
        {
            Properties p = new Properties();
            p.load(Configuration.class.getResource("/config.ini").openStream());
            FREEMODE = Boolean.valueOf((String) p.get("FREEMODE"));
            if(FREEMODE)
            {
                SHOCKWAVE_WITH_MISSILE = Boolean.valueOf((String) p.get("SHOCKWAVE_WITH_MISSILE"));
                MISSLE_TURN_SPEED = Float.valueOf((String) p.get("MISSLE_TURN_SPEED"));
                MISSLE_TARGETS_NEAREST_ENEMY = Boolean.valueOf((String) p.get("MISSLE_TARGETS_NEAREST_ENEMY"));
                MISSLE_EXPLODES = Boolean.valueOf((String) p.get("MISSLE_EXPLODES"));
                SPAWN_TIMER = Integer.valueOf((String) p.get("SPAWN_TIMER"));
            }
            else fillStandard();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            System.err.println("Great, you fucked it up! The ini-File is broken! I will take the default now. THANKS!");
            fillStandard();
        }
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    private static void fillStandard()
    {
        FREEMODE = false;
        SHOCKWAVE_WITH_MISSILE = false;
        MISSLE_TURN_SPEED = .05f;
        MISSLE_TARGETS_NEAREST_ENEMY = true;
        MISSLE_EXPLODES = false;
        SPAWN_TIMER = 300;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}