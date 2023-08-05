package com.example.imagegallery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ImageDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private View footerBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.image_detail);

        String imagePath = getIntent().getStringExtra("imagePath");
        Glide.with(this).load(imagePath).into(imageView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView shareIcon = findViewById(R.id.share_icon);
        ImageView deleteIcon = findViewById(R.id.delete_icon);
        ImageView moreIcon = findViewById(R.id.more_icon);

        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ImageDetailActivity.this, "Share icon clicked", Toast.LENGTH_SHORT).show();
            }
        });

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ImageDetailActivity.this, "Delete icon clicked", Toast.LENGTH_SHORT).show();
            }
        });

        moreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ImageDetailActivity.this, "More icon clicked", Toast.LENGTH_SHORT).show();
            }
        });

        footerBar = findViewById(R.id.footer_bar);  // Assuming you have given this id to your footer bar in XML

        ImageView imageView = findViewById(R.id.image_detail);  // Replace with your ImageView ID
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleBarsVisibility();
            }
        });
    }

    private void toggleBarsVisibility() {
        if (getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
            footerBar.setVisibility(View.GONE);
        } else {
            getSupportActionBar().show();
            footerBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
                finish();
                return true;}
            else if( android.R.id.home==R.id.action_info){
                Toast.makeText(this, "Info icon clicked", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

