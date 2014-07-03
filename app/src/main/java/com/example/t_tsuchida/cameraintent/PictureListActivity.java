package com.example.t_tsuchida.cameraintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiObject;
import com.kii.cloud.storage.query.KiiQuery;
import com.kii.cloud.storage.query.KiiQueryResult;

import java.util.ArrayList;
import java.util.List;

public class PictureListActivity extends Activity {

    Button backButton;
    ListView pictureListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list);
        pictureListView = (ListView)findViewById(R.id.pictureListView);
        backButton = (Button)findViewById(R.id.backButton);

        // listView初期表示の値の設定
        KiiQuery allQuery = new KiiQuery();
        //////////List<String> list = new ArrayList<String>();
        List<Picture> list = new ArrayList<Picture>();
        try {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
            KiiQueryResult<KiiObject> result = Kii.user().bucket("private").query(allQuery);
            List<KiiObject> objectList = result.getResult();
            for (KiiObject object : objectList) {
                list.add(new Picture(object.getString("title"), object.toUri().toString()));
                ////////////////list.add(object.toUri().toString());
            }

            if (!list.isEmpty()) {
                pictureListView.setAdapter(new PictureListAdapter(this, list));
                ////////////////ArrayAdapter<String> adpter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
                ////////////////pictureListView.setAdapter(adpter);
            }
        } catch (Exception e) {
            Log.d("Error", "1", e);
            showToast("Error : " + e.getLocalizedMessage());
        }

        // listViewの項目をクリック時に呼び出せれるコールバックの設定
        pictureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 選択されたItemの値を取得
                pictureListView = (ListView)parent;
                ////////String dispName = (String)pictureListView.getItemAtPosition(position);
                Picture item = (Picture) pictureListView.getAdapter().getItem(position);

                // 写真表示画面に遷移
                Intent intent = new Intent(PictureListActivity.this, PictureViewActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("uriName", item.getUri());
                //////////intent.putExtra("uriName", dispName);
                startActivity(intent);
            }
        });

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
        getMenuInflater().inflate(R.menu.picture_list, menu);
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

    /** リストView用Beanクラス */
    private class Picture {
        private String title;
        private String uri;

        public Picture (String title, String uri) {
            this.title = title;
            this.uri = uri;
        }

        public String getTitle() {
            return title;
        }

        public String getUri() {
            return uri;
        }
    }

    /** リストView用Adapterクラス */
    private class PictureListAdapter extends BaseAdapter {
        private Context context;
        private List<Picture> list;
        private LayoutInflater layoutInflater = null;

        public PictureListAdapter(Context context, List<Picture> list) {
            super();
            this.context = context;
            this.list = list;
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Picture picture = (Picture)getItem(position);
            convertView = layoutInflater.inflate(R.layout.picture_list_item, null);
            TextView tv = (TextView) convertView.findViewById(R.id.picture_list_item);
            tv.setText(picture.getTitle());
            return convertView;
        }
    }
}
