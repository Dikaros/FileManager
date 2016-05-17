package com.dikaros.diskmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.dikaros.diskmanager.adapter.HexAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HexActivity extends AppCompatActivity {

    TextView tvHexFileName;
    ListView lvHexList;
    List<String[]> fileList;
    String path;
    HexAdapter adapter;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hex);
        path = getIntent().getStringExtra("path");
        try {
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initViews();
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        tvHexFileName = (TextView) findViewById(R.id.tv_hex_file_name);
        lvHexList = (ListView) findViewById(R.id.lv_hex);
        lvHexList.setAdapter(adapter);
        tvHexFileName.setText(path);
    }

    /**
     * 初始化数据
     */
    private void initData() throws IOException {
        fileList = new ArrayList<>();
        file = new File(path);
        byte[] data = new byte[8];
        FileInputStream is = new FileInputStream(file);
        int len = -1;
        int count = 0;
        while ((len = is.read(data)) != -1) {
            String[] s = new String[len+2];
            s[0] = formatHex(Integer.toHexString(count));
            for (int i = 0; i < len; i++) {
                s[i+1] = byteToHex(data[i]);
            }
            s[s.length - 1] = new String(data);
            fileList.add(s);
            count+=len;
        }

        adapter = new HexAdapter(this, fileList);
    }


    private String byteToHex(byte b) {

        String s = Integer.toHexString(b&0xff).toUpperCase();
        return s.length()==1?"0"+s:s;
    }

    private String formatHex(String hex) {
        String newHex = hex;
        while (newHex.length() < 8) {
            newHex = "0" + newHex;
        }
        return newHex.toUpperCase();
    }
}
