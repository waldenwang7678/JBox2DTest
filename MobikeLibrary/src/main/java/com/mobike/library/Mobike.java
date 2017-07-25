package com.mobike.library;


import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.Random;

/**
 * Created by kimi on 2017/7/8 0008.
 * Email: 24750@163.com
 */

public class Mobike {

    public static final String TAG = Mobike.class.getSimpleName();

    private World world;            //世界
    private float dt = 1f / 60f;  // 模拟频率  大约16ms
    private float friction = 0.3f;  //摩擦系数
    private float density = 0.5f;  //密度
    private float restitution = 0.3f;  // 能量损失率
    private float ratio = 50;  //世界与屏幕的比例
    private int width, height;   //view 尺寸
    private boolean enable = true;   //是否绘制
    private final Random random = new Random();   //物体随机运动

    private ViewGroup mViewgroup;   // 当前 View

    public Mobike(ViewGroup viewgroup) {
        this.mViewgroup = viewgroup;
        density = viewgroup.getContext().getResources().getDisplayMetrics().density;
    }

    public void onSizeChanged(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void onDraw(Canvas canvas) {   //View绘制时候被调用    变化量 :
        if (!enable) {
            return;
        }
        int velocityIterations = 3;
        int positionIterations = 10;
        //频率 , 速度 , 位置 ,
        world.step(dt, velocityIterations, positionIterations);   //绘制变化的变量
        int childCount = mViewgroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mViewgroup.getChildAt(i);
            Body body = (Body) view.getTag(R.id.mobike_body_tag);
            if (body != null) {
                view.setX(metersToPixels(body.getPosition().x) - view.getWidth() / 2);
                view.setY(metersToPixels(body.getPosition().y) - view.getHeight() / 2);
                view.setRotation(radiansToDegrees(body.getAngle() % 360));
            }
        }
        mViewgroup.invalidate();   //不断绘制
    }

    public void onLayout(boolean changed) {  //分布
        createWorld(changed);
    }

    public void onStart() {
        setEnable(true);
    }

    public void onStop() {
        setEnable(false);
    }

    public void update() {
        world = null;
        onLayout(true);
    }

    //创建世界
    private void createWorld(boolean changed) {
        if (world == null) {   //创建一个 world
            // vec 向量 x ,  y
            world = new World(new Vec2(0, 10.0f));   //    物体,形状,约束 - 相互作用的集合
            createTopAndBottomBounds();
            createLeftAndRightBounds();
        }
        int childCount = mViewgroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mViewgroup.getChildAt(i);
            Body body = (Body) view.getTag(R.id.mobike_body_tag);  //每个 View 指代一个 body
            if (body == null || changed) {
                createBody(world, view);
            }
        }
    }

    //创建子 View 的 body ,
    private void createBody(World world, View view) {
        BodyDef bodyDef = new BodyDef();   //刚体信息类
        bodyDef.setType(BodyType.DYNAMIC); //动态

        //位置
        bodyDef.position.set(pixelsToMeters(view.getX() + view.getWidth() / 2),
                pixelsToMeters(view.getY() + view.getHeight() / 2));
        Shape shape = null;
        //
        Boolean isCircle = (Boolean) view.getTag(R.id.mobike_view_circle_tag);
        if (isCircle != null && isCircle) {
            shape = createCircleShape(view);  //圆形,形状与 View 尺寸相关
        } else {
            shape = createPolygonShape(view);   //多边形
        }
        FixtureDef fixture = new FixtureDef();
        fixture.setShape(shape);
        fixture.friction = friction;
        fixture.restitution = restitution;
        fixture.density = density;

        Body body = world.createBody(bodyDef);  //物体只能通过世界创造
        body.createFixture(fixture);

        view.setTag(R.id.mobike_body_tag, body);
        body.setLinearVelocity(new Vec2(random.nextFloat(), random.nextFloat()));
    }

    //创建子 view 的形状
    private Shape createCircleShape(View view) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(pixelsToMeters(view.getWidth() / 2));
        return circleShape;
    }

    //创建子 view 的形状
    private Shape createPolygonShape(View view) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(pixelsToMeters(view.getWidth() / 2), pixelsToMeters(view.getHeight() / 2));
        return polygonShape;
    }

    private void createTopAndBottomBounds() {
        BodyDef bodyDef = new BodyDef();   // 存储刚体的描述信息
        bodyDef.type = BodyType.STATIC;

        PolygonShape box = new PolygonShape();  //多边形形状
        float boxWidth = pixelsToMeters(width);  //
        float boxHeight = pixelsToMeters(ratio);
        box.setAsBox(boxWidth, boxHeight);   //设置多边形尺寸 ,

        //物体参数信息 最终会添加到物体(通过世界创造)上
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;   // 形状->多边形
        fixtureDef.density = 0.5f; // 密度
        fixtureDef.friction = 0.3f; // 摩擦系数
        fixtureDef.restitution = 0.5f;// 能量返回率

        bodyDef.position.set(0, -boxHeight);

        Body topBody = world.createBody(bodyDef);   //
        topBody.createFixture(fixtureDef);

        bodyDef.position.set(0, pixelsToMeters(height) + boxHeight);
        Body bottomBody = world.createBody(bodyDef);
        bottomBody.createFixture(fixtureDef);
    }

    private void createLeftAndRightBounds() {
        float boxWidth = pixelsToMeters(ratio);
        float boxHeight = pixelsToMeters(height);

        BodyDef bodyDef = new BodyDef();// 创建一个描述刚体信息的类
        bodyDef.type = BodyType.STATIC;

        PolygonShape box = new PolygonShape();  //多边形
        box.setAsBox(boxWidth, boxHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;

        bodyDef.position.set(-boxWidth, boxHeight);  //刚体属性 设置

        Body leftBody = world.createBody(bodyDef);  // 创建刚体
        leftBody.createFixture(fixtureDef);     //设置属性

        bodyDef.position.set(pixelsToMeters(width) + boxWidth, 0);

        Body rightBody = world.createBody(bodyDef);
        rightBody.createFixture(fixtureDef);
    }

    private float radiansToDegrees(float radians) {
        return radians / 3.14f * 180f;
    }

    private float degreesToRadians(float degrees) {
        return (degrees / 180f) * 3.14f;
    }

    public float metersToPixels(float meters) {
        return meters * ratio;
    }

    public float pixelsToMeters(float pixels) {
        return pixels / ratio;
    }

    public void random() {
        int childCount = mViewgroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Vec2 impulse = new Vec2(random.nextInt(1000) - 1000, random.nextInt(1000) - 1000);
            View view = mViewgroup.getChildAt(i);
            Body body = (Body) view.getTag(R.id.mobike_body_tag);
            if (body != null) {
                body.applyLinearImpulse(impulse, body.getPosition(), true);
            }
        }
    }

    public void onSensorChanged(float x, float y) {
        int childCount = mViewgroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Vec2 impulse = new Vec2(x, y);
            View view = mViewgroup.getChildAt(i);
            Body body = (Body) view.getTag(R.id.mobike_body_tag);
            if (body != null) {
                body.applyLinearImpulse(impulse, body.getPosition(), true);
            }
        }
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        if (friction >= 0) {
            this.friction = friction;
        }
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        if (density >= 0) {
            this.density = density;
        }
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        if (restitution >= 0) {
            this.restitution = restitution;
        }
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        if (ratio >= 0) {
            this.ratio = ratio;
        }
    }

    public boolean getEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        mViewgroup.invalidate();
    }
}
