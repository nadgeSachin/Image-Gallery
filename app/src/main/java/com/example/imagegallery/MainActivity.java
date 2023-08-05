package com.example.imagegallery;


import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.os.Environment.MEDIA_MOUNTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static int PERMISSION_REQUEST_CODE=100;
    RecyclerView recyclerView;
    ArrayList<String> images;
    GalleryAdapter adapter;
    GridLayoutManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.gallery_recycler);
        images=new ArrayList<>();
        adapter=new GalleryAdapter(this,images);
        manager=new GridLayoutManager(this,3);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        checkPermissions();


    }



    private void checkPermissions() {
        int result= ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED){
            loadImages();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            boolean accepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
            if(accepted){
                loadImages();
            }else{
                Toast.makeText(this,"You have dined the permission",Toast.LENGTH_LONG).show();
            }
        }else{

        }
    }

    private void loadImages() {
        boolean SDCard= Environment.getExternalStorageState().equals(MEDIA_MOUNTED);
        if(SDCard){
            final String[] colums={MediaStore.Images.Media.DATA,MediaStore.Images.Media._ID};
            final String order=MediaStore.Images.Media.DATE_TAKEN+" DESC";
            Cursor cursor=getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,colums,null,null,order);
            int count =cursor.getCount();
            for(int i=0;i<count;i++){
                cursor.moveToPosition(i);
                int colunmindex=cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                images.add(cursor.getString(colunmindex));
            }
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}