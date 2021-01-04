package com.e.androidpuzzlegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class SelectActivity extends AppCompatActivity {
    String mCurrentPhotoPath;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3;
    static final int REQUEST_IMAGE_GALLERY = 4;

    private String roomName = "";
    private String nickname = "";
    private boolean online = false;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        roomName = extras.getString(OnlineActivity.TABLE_MESSAGE_KEY);
        nickname = extras.getString(OnlineActivity.NICKNAME_MESSAGE_KEY);
        online = extras.getBoolean(OnlineActivity.ONLINE_MESSAGE_KEY);

        AssetManager am = getAssets();
        try {
            final String[] files = am.list("img");

            GridView grid = findViewById(R.id.grid);
            grid.setAdapter(new ImageAdapter(this));
            grid.setOnItemClickListener((adapterView, view, i, l) -> {
                if (!online) {
                    Intent intent1 = new Intent(getApplicationContext(), PuzzleActivity.class);
                    assert files != null;
                    intent1.putExtra("assetName", files[i % files.length]);
                    startActivity(intent1);
                } else {
                    Intent intent1 = new Intent(getApplicationContext(), WaitingRoomActivity.class);
                    assert files != null;
                    intent1.putExtra("assetName", files[i % files.length]);
                    intent1.putExtra(OnlineActivity.TABLE_MESSAGE_KEY, roomName);
                    intent1.putExtra(OnlineActivity.NICKNAME_MESSAGE_KEY, nickname);
                    uploadImage(files[i % files.length],"assetName");
                    startActivity(intent1);
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
        }

    }

    @SuppressLint("ShowToast")
    public void onImageFromCameraClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            }

            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted, initiate request
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);

             ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);

        } else {
            // Create an image file name
            @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            mCurrentPhotoPath = image.getAbsolutePath(); // save this to use in the intent

            return image;
        }

        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onImageFromCameraClick(new View(this));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!online) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Intent intent = new Intent(this, PuzzleActivity.class);
                intent.putExtra("mCurrentPhotoPath", mCurrentPhotoPath);
                startActivity(intent);
            }
            if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Intent intent = new Intent(this, PuzzleActivity.class);
                assert uri != null;
                intent.putExtra("mCurrentPhotoUri", uri.toString());
                startActivity(intent);
            }
        } else {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Intent intent = new Intent(this, WaitingRoomActivity.class);
                intent.putExtra(OnlineActivity.TABLE_MESSAGE_KEY, roomName);
                intent.putExtra(OnlineActivity.NICKNAME_MESSAGE_KEY, nickname);

                uploadImage(mCurrentPhotoPath, "mCurrentPhotoPath");

                startActivity(intent);
            }
            if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Intent intent = new Intent(this, WaitingRoomActivity.class);
                assert uri != null;
                intent.putExtra(OnlineActivity.TABLE_MESSAGE_KEY, roomName);
                intent.putExtra(OnlineActivity.NICKNAME_MESSAGE_KEY, nickname);

                uploadImage(uri.toString(), "mCurrentPhotoUri");

                startActivity(intent);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (!online)
            inflater.inflate(R.menu.photo_gallery_settings_menu, menu);
        return true;
    }

    @SuppressLint({"NonConstantResourceId", "ShowToast"})
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {
                        Log.e("permission","denied");
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
                    }

                    if (photoFile != null) {
                        Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                }
                return true;
            case R.id.gallery:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                    intent2.setType("image/*");
                    startActivityForResult(intent2, REQUEST_IMAGE_GALLERY);
                }
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void uploadImage(String theImage, String theImageType) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("GameTable");
        query.whereEqualTo("name", roomName); // the room name

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects != null) {

                    objects.get(0).put("image", theImage);
                    objects.get(0).put("imageType", theImageType);

                    objects.get(0).saveInBackground();
                }
            }
        });
    }
    // TODO pick the photo url and put it in this
    // google on how to upload/retrieve images from Parse in android
}