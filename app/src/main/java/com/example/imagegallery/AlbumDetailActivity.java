package com.example.imagegallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;

    private ArrayList<String> photoPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        String albumName = getIntent().getStringExtra("albumName");
        photoPaths = (ArrayList<String>) getIntent().getSerializableExtra("photoPaths");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(albumName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.photo_recycler_view);
        photoAdapter = new PhotoAdapter(photoPaths);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(photoAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
                finish();
                return true;}
        else if(item.getItemId()==R.id.action_more){
                // handle the "More" button click
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_detail_menu, menu);
        return true;
    }


    private class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

        private ArrayList<String> photoPaths;

        public PhotoAdapter(ArrayList<String> photoPaths) {
            this.photoPaths = photoPaths;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String photoPath = photoPaths.get(position);
            Glide.with(AlbumDetailActivity.this).load(photoPath).into(holder.photoImageView);
        }

        @Override
        public int getItemCount() {
            return photoPaths.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView photoImageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                photoImageView = itemView.findViewById(R.id.photo_image_view);
                photoImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            String photoPath = photoPaths.get(position);
                            Intent intent = new Intent(itemView.getContext(), ImageDetailActivity.class);
                            intent.putExtra("imagePath", photoPath);
                            itemView.getContext().startActivity(intent);
                        }
                    }
                });
            }
        }
    }
}
