package com.example.walden.jbox2dtest;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

/**
 * Created by wangjt on 2017/7/24.
 */

public class JboxTestView extends View {
    public JboxTestView(Context context) {
        super(context);
    }

    public JboxTestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    void init() {
        AABB ab = new AABB();
        // ab.lowerBound = new Vec2(-100, -100);


        Vec2 gravity = new Vec2(0, 10);

        World world = new World(gravity, true);


    }
    private void create(){
        PolygonShape shape=new PolygonShape();
        Shape
    }
}
