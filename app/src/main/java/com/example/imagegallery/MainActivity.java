package com.example.imagegallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private ArrayList<AlbumModel> albumList;

    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_PERMISSION_MANAGE_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        // Set the toolbar as the action bar
        setSupportActionBar(myToolbar);

        recyclerView = findViewById(R.id.album_recycler_view);
        albumList = new ArrayList<>();
        albumAdapter = new AlbumAdapter(albumList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(albumAdapter);

        // Check if the app has READ_EXTERNAL_STORAGE permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // The permission is granted, proceed with album fetching
            fetchAlbums();
        } else {
            // Request READ_EXTERNAL_STORAGE permission at runtime
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        }

        // If targeting Android 11 or higher, check for MANAGE_EXTERNAL_STORAGE permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request MANAGE_EXTERNAL_STORAGE permission at runtime
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_MANAGE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_EXTERNAL_STORAGE permission granted, fetch albums
                fetchAlbums();
            } else {
                // Permission denied, handle it accordingly (show a message, disable features, etc.)
            }
        } else if (requestCode == REQUEST_PERMISSION_MANAGE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // MANAGE_EXTERNAL_STORAGE permission granted, if required, handle it here
            } else {
                // Permission denied, if required, handle it here
            }
        }
    }

    @SuppressLint("Range")
    private void fetchAlbums() {
        String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        if (cursor != null) {
            HashMap<String, ArrayList<String>> albumMap = new HashMap<>();

            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String albumName;
                albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

                if (!albumMap.containsKey(albumName)) {
                    albumMap.put(albumName, new ArrayList<>());
                }

                albumMap.get(albumName).add(imagePath);
            }

            cursor.close();

            for (String albumName : albumMap.keySet()) {
                albumList.add(new AlbumModel(albumName, albumMap.get(albumName)));
            }

            // Sort albumList in ascending order
            Collections.sort(albumList, new Comparator<AlbumModel>() {
                @Override
                public int compare(AlbumModel o1, AlbumModel o2) {
                    return o1.getAlbumName().compareTo(o2.getAlbumName());
                }
            });
            albumAdapter.notifyDataSetChanged();
        }
    }

    private class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

        private ArrayList<AlbumModel> albums;

        public AlbumAdapter(ArrayList<AlbumModel> albums) {
            this.albums = albums;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AlbumModel model = albums.get(position);
            holder.albumNameTextView.setText(model.getAlbumName());

            // Display the total number of images
            holder.totalImagesTextView.setText(String.valueOf(model.getPhotoPaths().size()));

            // Set the cover photo of the album using the first photo in the album
            if (!model.getPhotoPaths().isEmpty()) {
                String coverPhotoPath = model.getPhotoPaths().get(0);
                Glide.with(MainActivity.this)
                        .load(coverPhotoPath)
                        .override(64, 64)
                        .centerCrop()
                        .into(holder.albumCoverImageView);
            }
        }

        @Override
        public int getItemCount() {
            return albums.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView albumNameTextView;
            private final TextView totalImagesTextView;
            private final ImageView albumCoverImageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                albumNameTextView = itemView.findViewById(R.id.album_name);
                totalImagesTextView = itemView.findViewById(R.id.total_images);
                albumCoverImageView = itemView.findViewById(R.id.album_cover_image);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            String albumName = albums.get(position).getAlbumName();
                            ArrayList<String> photoPaths = albums.get(position).getPhotoPaths();
                            Intent intent = new Intent(v.getContext(), AlbumDetailActivity.class);
                            intent.putExtra("albumName", albumName);
                            intent.putExtra("photoPaths", photoPaths);
                            v.getContext().startActivity(intent);
                        }
                    }
                });
            }
        }
    }
}
