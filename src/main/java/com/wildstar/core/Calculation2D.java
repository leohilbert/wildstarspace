package com.wildstar.core;


import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import com.wildstar.gameobjects.impl.Ball;

public class Calculation2D
{
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static double calcAngle(Point2D.Float p1, Point2D.Float p2)
    {
        return getDegreesByCoord2f( p1, p2 )+90;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public final static Point2D.Float getPointByAngleAndDegrees(Point2D.Float origin, float distance, float orgAngle)
    {
        return getCoordByRangeEllipse(origin, distance, orgAngle);
    }
    // -----------------------------------------------------------------------------------------------------------
    public static float getDegreesByCoord2f(Point2D.Float k1, Point2D.Float k2)
    {
        float w, x, y;
        y = k1.x - k2.x;
        x = k1.y - k2.y;
        if(x!=0) w = (float)Math.abs(Math.atan( (y/x) )* 180 / Math.PI); else w = 0;
        
        int qu = 0;
        
        if(x>0) qu+= 1;
        else if(x==0) qu+= 2;
        else qu+= 4;
        
        if(y<0) qu+= 2;
        else if(y==0) qu+= 4;
        else qu+= 8;
        
        switch( qu )
        {
            case 5 : w = 0; break;
            case 3 : break;
            case 4 : w = 90; break;
            case 6 : w = 180 - w; break;
            case 8 : w = 180; break;
            case 12 : w = 180 + w; break;
            case 10 : w = 270; break;
            case 9 : w = 360 - w; break;
        }
        return (w+270);
    }
    // -----------------------------------------------------------------------------------------------------------
    public static Point2D.Float getCoordByRangeEllipse( Point2D.Float p1, double range, double angle )
    {
        Point2D.Float back = new Point2D.Float();
        back.x = (float)( p1.x + ( sin( angle ) * range ) );
        back.y = (float)( p1.y - ( cos( angle ) * range ) );
        return back;
    }
    // -----------------------------------------------------------------------------------------------------------
    public static double cos( double angle )
    {
        return Math.cos( Math.PI * angle / 180 );
    }
    // -----------------------------------------------------------------------------------------------------------
    public static double sin( double angle )
    {
        return Math.sin( Math.PI * angle / 180 );
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static float qdrt(float x)
    {
        return x*x;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static float pythagoras(Point2D.Float p1, Point2D.Float p2)
    {
        return (float) Math.sqrt(qdrt(p2.x-p1.x) + qdrt(p2.y-p1.y));
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static double getNormalDegrees(double angle)
    {
        if(angle > 360f)
        {
            return getNormalDegrees(angle-360);
        }
        else if(angle < 0)
        {
            return getNormalDegrees(angle+360);
        }
        return angle;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
    public static Set<Ball> explode(Point2D.Float pos)
    {
        Set<Ball> back = new HashSet<Ball>();
        for(int i=0;i<36;i++)
        {
            Point2D.Float f2 = Calculation2D.getPointByAngleAndDegrees(pos, 1, i*10);
            double CalcAngle = Calculation2D.calcAngle(f2, pos);
            back.add(new Ball(pos.x, pos.y, 6, CalcAngle));
        }
        return back;
    }
    // ----------------------------------------------------------------------------------------------------------------------------------------------
}







