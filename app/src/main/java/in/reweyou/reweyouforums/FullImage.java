package in.reweyou.reweyouforums;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class FullImage extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private String imagepath;
    private ImageViewTouch imageView;
    private Toolbar toolbar;
    private String tag;
    private TextView headline;
    private String headlinetext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        initToolbar();
        headline = (TextView) findViewById(R.id.headline);


        Bundle bundle = getIntent().getExtras();
        imagepath = bundle.getString("image");
        imageView = (ImageViewTouch) findViewById(R.id.image);
        showimage(imagepath);


    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showimage(String i) {
        Glide.with(FullImage.this).load(i).diskCacheStrategy(DiskCacheStrategy.SOURCE).fitCenter().into(imageView);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
