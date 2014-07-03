package com.example.t_tsuchida.cameraintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t_tsuchida.cameraintent.R;
import com.kii.cloud.storage.KiiObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureViewActivity extends Activity {

    ImageView pictureView;
    Button backButton;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);
        pictureView = (ImageView)findViewById(R.id.pictureView);
        backButton = (Button)findViewById(R.id.backButton);
        //saveButton = (Button)findViewById(R.id.saveButton);

        // 前画面からの値を取得
        Bundle extras = getIntent().getExtras();
        String uriName = extras.getString("uriName");
        String title = extras.getString("title");

        // 写真表示の設定
        Uri objectUri = Uri.parse(uriName);
        KiiObject object = KiiObject.createByUri(objectUri);
        try {
            object.refresh();
        } catch (Exception e) {
            Log.d("Error", "1", e);
            showToast("Error : " + e.getLocalizedMessage());
        }

        Date mDate = new Date();
        SimpleDateFormat fileName = new SimpleDateFormat("yyyyMMddHHmmss");

        object.set("title", fileName.format(mDate));


        // ストレージに画像を保存
        //File localFile = new File(Environment.getExternalStorageDirectory(), fileName.format(mDate) + ".jpg");
        // 一時フォルダに画像を保存
        File localFile = new File(getCacheDir(), fileName.format(mDate) + ".jpg");

        try {
            object.downloadBody(localFile);
            pictureView.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath()));
        } catch (Exception e) {
            Log.d("Error", "1", e);
            showToast("Error : " + e.getLocalizedMessage());
        }

        // Backボタンクリック時コールバック設定
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.picture_view, menu);
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
}
