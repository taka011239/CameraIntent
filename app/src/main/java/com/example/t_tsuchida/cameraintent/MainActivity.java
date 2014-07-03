package com.example.t_tsuchida.cameraintent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiBucket;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.callback.KiiObjectCallBack;
import com.kii.cloud.storage.exception.app.AppException;
import com.kii.cloud.storage.resumabletransfer.AlreadyStartedException;
import com.kii.cloud.storage.resumabletransfer.KiiUploader;
import com.kii.cloud.storage.resumabletransfer.StateStoreAccessException;
import com.kii.cloud.storage.resumabletransfer.SuspendedException;
import com.kii.cloud.storage.resumabletransfer.TerminatedException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    static final int REQUEST_CAPTURE_IMAGE = 100;
    Button captureButton;
    Button shareButton;
    Button uploadButton;
    Button viewButton;
    ImageView captureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captureView = (ImageView)findViewById(R.id.captureView);
        captureButton = (Button)findViewById(R.id.caputureButton);
        shareButton = (Button)findViewById(R.id.shareButton);
        uploadButton = (Button)findViewById(R.id.uploadButton);
        viewButton = (Button)findViewById(R.id.viewButton);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQUEST_CAPTURE_IMAGE);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable)captureView.getDrawable()).getBitmap();
                createPictureObject(bitmap);
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PictureListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CAPTURE_IMAGE){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            captureView.setImageBitmap(bitmap);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void createPictureObject(Bitmap bitmap) {
        if(saveBitmapToSd(bitmap)) {
            KiiObject object = Kii.user().bucket("private").object();

            Date mDate = new Date();
            SimpleDateFormat fileName = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");

            object.set("title", fileName.format(mDate));

            object.save(new KiiObjectCallBack() {

                @Override
                public void onSaveCompleted(int token, KiiObject object,
                                            Exception exception) {
                    if (exception == null) {
                        showToast("Object created!");

                        File localFile = new File(Environment.getExternalStorageDirectory(), "temp_upload.jpg");
                        KiiUploader uploader = object.uploader(getApplicationContext(), localFile);

                        try {
                            uploader.transfer(null);
                        } catch (AlreadyStartedException e) {
                            // Upload already in progress.
                        } catch (SuspendedException e) {
                            // Upload suspended (e.g. network error or user interruption).
                        } catch (TerminatedException e) {
                            // Upload terminated (e.g. file not found or user interruption).
                        } catch (StateStoreAccessException e) {
                            // Failed to access the local storage.
                        }
                    } else {
                        showToast("Error : " + exception.getLocalizedMessage());
                    }
                }

            });
        }
    }

    private boolean saveBitmapToSd(Bitmap bitmap){
        try {
            File root = Environment.getExternalStorageDirectory();
            FileOutputStream fos = null;
            fos = new FileOutputStream(new File(root, "temp_upload.jpg"));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.toString());
            return false;
        }
    }
}
