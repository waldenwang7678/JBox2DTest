package com.example.walden.jbox2dtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

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
 * Created by wangjt on 2017/7/24.
 */

public class JboxTestView extends FrameLayout {

    private World world;
    private int mWidth;
    private int mHeight;
    private int ratio = 50;  //世界和屏幕的比例
    private Random random;
    private float dt = 1f / 60f;  // 模拟频率  大约16ms
    private float friction = 0.3f;  //摩擦系数
    private float density = 0.5f;  //密度
    private float restitution = 0.5f;  // 能量损失率
    public boolean enable = true;
    private Body topBody;
    private Body bottomBody;
    private Body leftBody;
    private Body rightBody;
    private Paint mPaint;

    public JboxTestView(Context context) {
        super(context);
        init();
    }

    public JboxTestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        setWillNotDraw(false);
        random = new Random();
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#f05050"));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mWidth = width;
        mHeight = height;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            width = Util.dp2px(getContext(), 300);  //像素
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            height = Util.dp2px(getContext(), 300);
        }
        int lastSize = Math.min(width, height);
        setMeasuredDimension(lastSize, lastSize);

        mWidth = lastSize;
        mHeight = lastSize;

    }

    // layout 时候创建世界
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        creatWorld();
    }


    //通过 View 创建刚体, 确定刚体的位置, view 绘制的时候, 获取刚体的位置 , 根据刚体位置变化不断更新 View 的位置,
    public void onDraw(Canvas canvas) {   //View绘制时候被调用    变化量 :
        int velocityIterations = 3;
        int positionIterations = 10;
        //频率 , 速度 , 位置 ,
        world.step(dt, velocityIterations, positionIterations);   //绘制变化的变量
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            Body body = (Body) view.getTag(com.mobike.library.R.id.mobike_body_tag);
            if (body != null) {
                view.setX(metersToPixels(body.getPosition().x) - view.getWidth() / 2);
                view.setY(metersToPixels(body.getPosition().y) - view.getHeight() / 2);
                view.setRotation(radiansToDegrees(body.getAngle() % 360));
            }
        }
        canvas.drawRect(0, 0, leftBody.getPosition().x, leftBody.getPosition().y, mPaint);
        canvas.drawRect(0, 0, topBody.getPosition().x, topBody.getPosition().y, mPaint);
//        canvas.drawRect(0, 0, rightBody.getPosition().x, rightBody.getPosition().y, mPaint);
//        canvas.drawRect(0, 0, bottomBody.getPosition().x, bottomBody.getPosition().y, mPaint);
       canvas.drawRect(100,100,200,200,mPaint);
        invalidate();   //不断绘制
    }

    private float radiansToDegrees(float radians) {
        return radians / 3.14f * 180f;
    }

    private void creatWorld() {
        if (world == null) {
            Vec2 gravity = new Vec2(0, 10); //重力向量
            world = new World(gravity);
            createTopAndBottomBounds();  //世界边界
            createLeftAndRightBounds();
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            Body body = (Body) view.getTag();
            if (body == null) {
                createBody(world, view);
            }
        }
    }

    private void createBody(World world, View view) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.setType(BodyType.DYNAMIC); //动态

        bodyDef.position.set(pixelsToMeters(view.getX() + view.getWidth() / 2),
                pixelsToMeters(view.getY() + view.getHeight() / 2));
        Shape shape = null;
        //
        Boolean isCircle = (Boolean) view.getTag(com.mobike.library.R.id.mobike_view_circle_tag);
        if (isCircle != null && isCircle) {
            shape = createCircleShape(view);  //圆形
        } else {
            shape = createPolygonShape(view);   //多边形
        }
        FixtureDef fixture = new FixtureDef();
        fixture.setShape(shape);
        fixture.friction = friction;
        fixture.restitution = restitution;
        fixture.density = density;

        Body body = world.createBody(bodyDef);
        body.createFixture(fixture);
        view.setTag(com.mobike.library.R.id.mobike_body_tag, body);
        body.setLinearVelocity(new Vec2(random.nextFloat(), random.nextFloat()));
    }

    private Shape createCircleShape(View view) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(pixelsToMeters(view.getWidth() / 2));
        return circleShape;
    }

    private Shape createPolygonShape(View view) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(pixelsToMeters(view.getWidth() / 2), pixelsToMeters(view.getHeight() / 2));
        return polygonShape;
    }

    public float metersToPixels(float meters) {  // m 转化成像素
        return meters * ratio;
    }

    public float pixelsToMeters(float pixels) {   //像素转化成 m
        return pixels / ratio;
    }

    //创建世界上下边界
    private void createTopAndBottomBounds() {
        BodyDef bodyDef = new BodyDef();   //存储刚体的描述信息
        bodyDef.type = BodyType.STATIC;   //世界边界 静态

        PolygonShape box = new PolygonShape();//形状  多边形
        float boxWidth = pixelsToMeters(mWidth);  //
        float boxHeight = 1;
        box.setAsBox(boxWidth, boxHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;

        //上边界
        bodyDef.position.set(0, 1);
        topBody = world.createBody(bodyDef);
        topBody.createFixture(fixtureDef);

        //下边界
        bodyDef.position.set(0, pixelsToMeters(mHeight) - 1);
        bottomBody = world.createBody(bodyDef);
        bottomBody.createFixture(fixtureDef);
    }

    //创建世界左右边界
    private void createLeftAndRightBounds() {
        BodyDef bodyDef = new BodyDef();// 创建一个描述刚体信息的类
        bodyDef.type = BodyType.STATIC;


        PolygonShape box = new PolygonShape();  //多边形
        float boxHeight = pixelsToMeters(mHeight);
        box.setAsBox(1, boxHeight); //左右边界 , 宽1 , 高与 view 高相同

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;


        bodyDef.position.set(1, boxHeight);  //左边界位置
        // 创建刚体
        leftBody = world.createBody(bodyDef);
        leftBody.createFixture(fixtureDef);     //设置属性


        bodyDef.position.set(pixelsToMeters(mWidth) - 1, 0); //右边界位置
        rightBody = world.createBody(bodyDef);
        rightBody.createFixture(fixtureDef);
    }

    public void random() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            Vec2 impulse = new Vec2(random.nextInt(1000) - 1000, random.nextInt(1000) - 1000);
            View view = getChildAt(i);
            Body body = (Body) view.getTag(com.mobike.library.R.id.mobike_body_tag);
            if (body != null) {
                body.applyLinearImpulse(impulse, body.getPosition(), true);
            }
        }
    }
}
