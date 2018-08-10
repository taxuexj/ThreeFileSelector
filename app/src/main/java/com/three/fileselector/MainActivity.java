package com.three.fileselector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.three.fileselector.adapter.FileListAdapter;
import com.three.fileselector.bean.FileInfoBean;
import com.three.fileselector.fragment.FileListFragment;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private TextView tvSelect;
    private RecyclerView recyclerView;
    private FileListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initView() {
        tvSelect = findViewById(R.id.tv_selector);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initListener() {
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FileSelectorActivity.class);
                startActivityForResult(intent, FileSelectorActivity.SELECTED_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case FileSelectorActivity.SELECTED_REQUEST_CODE:
                    Bundle bundle = data.getExtras();
                    ArrayList<FileInfoBean> results = bundle.getParcelableArrayList(FileSelectorActivity.SELECTED_RESULT);
                    adapter = new FileListAdapter(results,MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    break;
            }
        }
    }
}
