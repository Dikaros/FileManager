package com.dikaros.filemanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.dikaros.filemanager.adapter.FileAdapter;
import com.dikaros.filemanager.util.FileSortFactory;
import com.dikaros.filemanager.util.FileUtils;
import com.dikaros.filemanager.view.NewFolderDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    //文件集合
    ArrayList<File> files;

    //文件适配器
    FileAdapter fileAdapter;

    //主ListView
    ListView lvMain;

    //当前路径
    String currnetPath;

    //根路径
    String rootPath;

    long lastBackPressed = 0;

    Stack<Integer> lastPostion;

    TextView tvPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        //请求获取root权限
//        FileUtils.upgradeRootPermission(getPackageCodePath());
        /*
        1. 加载数据
        2. 初始化监听器
        3. 初始化并设置Views
         */
        lastPostion = new Stack<>();
        loadFile();
        initAdapter();
        initViews();
    }

    /**
     * 初始化Views
     */
    private void initViews() {
        setTitle("文件浏览");

        lvMain = (ListView) findViewById(R.id.lv_main);
        tvPath = (TextView) findViewById(R.id.tv_path);
        tvPath.setText(currnetPath);
        lvMain.setAdapter(fileAdapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //当前选中的文件
                File file = files.get(position);
                //如果是文件夹,点击后进入文件夹
                if (file.isDirectory()) {
                    currnetPath = file.getAbsolutePath();
                    Log.i("filePath", currnetPath);
                    //清空files
                    files.clear();
                    //将当前选中的目录里的元素添加到files里面
                    for (File tFile : file.listFiles()) {
                        files.add(tFile);
                    }
                    fileAdapter.notifyDataSetChanged();
                    tvPath.setText(currnetPath);
                    lastPostion.push(lvMain.getFirstVisiblePosition());
                } else {
                    //如果是文件,使用意图过滤器启动相应的程序
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtils.getTailName(file.getName()));
                        Uri data = Uri.fromFile(file);
                        intent.setDataAndType(data, type);
                        startActivity(intent);

                    }catch (Exception e){
                        Toast.makeText(MainActivity.this,"无法打开文件",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 按下返回键
     * 返回键按下后判断当前目录是否是跟目录,如果不是,则返回上一个目录
     */
    @Override
    public void onBackPressed() {
        //当前路径不是根路径
        if (!rootPath.equals(currnetPath)) {
            //当前文件夹
            File file = new File(currnetPath);
            File pFile = file.getParentFile();
            currnetPath = pFile.getAbsolutePath();
            files.clear();
            //将当前选中的目录里的元素添加到files里面
            for (File tFile : pFile.listFiles()) {
                files.add(tFile);
            }
            fileAdapter.notifyDataSetChanged();
            tvPath.setText(currnetPath);
            lvMain.setSelection(lastPostion.pop());
        } else {
            //当前时间
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastBackPressed < 2000) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            }
            lastBackPressed = currentTime;


        }
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        fileAdapter = new FileAdapter(this, files);
    }

    /**
     * 加载文件数据
     */
    private void loadFile() {
        files = new ArrayList<>();
        File sdPath = Environment.getRootDirectory();
        //设置当前路径以及根路径均为sd卡的根目录
        currnetPath = sdPath.getAbsolutePath();
        rootPath = currnetPath;
        File[] tempFiles = sdPath.listFiles();

        for (File file : tempFiles) {
            files.add(file);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                doSearch();
                break;
            case R.id.action_new_folder:
                doCreateNewFolder();
                break;
            case R.id.action_sort_date:
                fileAdapter.setSortWay(FileSortFactory.SORT_BY_FOLDER_AND_TIME);
                fileAdapter.notifyDataSetChanged();
                break;
            case R.id.action_sort_size:
                fileAdapter.setSortWay(FileSortFactory.SORT_BY_FOLDER_AND_SIZE);
                fileAdapter.notifyDataSetChanged();
                break;
            case R.id.action_sort_name:
                fileAdapter.setSortWay(FileSortFactory.SORT_BY_FOLDER_AND_NAME);
                fileAdapter.notifyDataSetChanged();
                break;
        }
        return true;
    }


    /**
     * 搜索
     */
    private void doSearch() {
    }

    /**
     * 新建文件夹
     */
    private void doCreateNewFolder() {
        NewFolderDialog dialog = new NewFolderDialog(this, currnetPath, fileAdapter);
        dialog.show();
        dialog.setOnFolderCreatedListener(new NewFolderDialog.OnFolderCreatedListener() {
            @Override
            public void onFolderCreated() {
                Snackbar.make(findViewById(R.id.rlayout_main), "文件夹创建成功", Snackbar.LENGTH_SHORT).show();
            }
        });

    }
}
