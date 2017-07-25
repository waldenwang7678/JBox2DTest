package com.example.walden.jbox2dtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private JboxTestView jBoxview;
    private int[] img = new int[]{R.mipmap.ic_launcher, R.mipmap.icon, R.mipmap.tile1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        jBoxview = (JboxTestView) findViewById(R.id.jboxview);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        for (int i = 0; i < img.length; i++) {
            ImageView image = new ImageView(this);
            image.setImageResource(img[i]);
            jBoxview.addView(image, params);
        }

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jBoxview.random();
            }
        });

    }
}
