package com.dikaros.diskmanager.view;

import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dikaros.diskmanager.R;
import com.dikaros.diskmanager.adapter.FileAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 16/4/19.
 */
public class NewFolderDialog {
    Context context;
    AlertDialog dialog;
    Button btnCreat;
    Button btnCancle;
    EditText etFileName;
    String path;
    FileAdapter adapter;

    public NewFolderDialog(Context context,String path,FileAdapter adapter) {
        this.context = context;
        this.path = path;
        this.adapter = adapter;
        initDialog();
    }

    OnFolderCreatedListener onFolderCreatedListener;

    public OnFolderCreatedListener getOnFolderCreatedListener() {
        return onFolderCreatedListener;
    }

    public void setOnFolderCreatedListener(OnFolderCreatedListener onFolderCreatedListener) {
        this.onFolderCreatedListener = onFolderCreatedListener;
    }

    public interface  OnFolderCreatedListener{
        public void onFolderCreated();
    }

    /**
     * 初始化对话框
     */
    private void initDialog() {
        dialog = new AlertDialog.Builder(context).create();
    }

    private void creatNewFolder() {
        String dirName = etFileName.getText().toString();
        if (dirName.equals("")) {
            dirName = renameDefaultFolder();
        }

        File file = new File(path+"/"+dirName);
        file.mkdir();
        adapter.addToFiles(file);
        adapter.notifyDataSetChanged();
        dialog.dismiss();
        if (onFolderCreatedListener!=null){
            onFolderCreatedListener.onFolderCreated();
        }

    }

    private String renameDefaultFolder(){
        int i = 1;
        String result = "新建文件夹";
        List<String> tempList = new ArrayList<>();
        for (File webFile : adapter.getFiles()) {
            if (!webFile.isFile()) {
                tempList.add(webFile.getName());
            }
        }
        while (tempList.contains(result)) {
            result="新建文件夹("+i+")";
            i++;
        }
        return result;
    }

    public void show(){
        if (dialog==null) {
            return;
        }
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_new_folder);
        btnCreat = (Button) dialog.getWindow().findViewById(
                R.id.btn_dlg_new_folder_creat);
        btnCancle = (Button) dialog.getWindow().findViewById(
                R.id.btn_dlg_new_folder_cancle);
        etFileName = (EditText) dialog.getWindow().findViewById(
                R.id.et_dlg_new_forder_name);
        btnCreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatNewFolder();
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }




}
