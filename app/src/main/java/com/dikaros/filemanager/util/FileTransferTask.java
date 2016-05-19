package com.dikaros.filemanager.util;

import android.os.AsyncTask;

import com.dikaros.filemanager.adapter.FileAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Dikaros on 2016/5/19.
 */
public class FileTransferTask extends AsyncTask<String,Integer,Boolean> {

    File oldFile;
    File newFile;

    public FileTransferTask(File oldFile, File newFile) {
        this.oldFile = oldFile;
        this.newFile = newFile;
    }

    OnFileTransferListener onFileTransferListener;

    public void setOnFileTransferListener(OnFileTransferListener onFileTransferListener) {
        this.onFileTransferListener = onFileTransferListener;
    }

    @Override
    protected void onCancelled() {
        if (onFileTransferListener!=null){
            onFileTransferListener.transferFinished(false);
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (onFileTransferListener!=null){
            onFileTransferListener.transferFinished(aBoolean);
        }
    }

    @Override
    protected void onPreExecute() {
        if (onFileTransferListener!=null){
            onFileTransferListener.beforeTransfer();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (onFileTransferListener!=null){
            onFileTransferListener.onProgress(values);
        }
    }

    public interface OnFileTransferListener{
        public void transferFinished(boolean success);
        public void beforeTransfer();
        public void onProgress(Integer...progress);
    }
    @Override
    protected Boolean doInBackground(String... params) {

        if (oldFile.isFile()&&oldFile.exists()){
            try {
                FileInputStream fis = new FileInputStream(oldFile);
                FileOutputStream fos = new FileOutputStream(newFile);
                int len = -1;
                long contentSize = oldFile.length();
                long readed = 0;
                byte[] buff = new byte[8192];
                while ((len=fis.read(buff))!=-1){
                    //写文件
                    fos.write(buff,0,len);
                    readed+=len;
                    //发布进度
                    publishProgress((int)(readed*100/contentSize));
                }
                fos.flush();
                fis.close();
                fos.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                cancel(true);
                return false;
            }

        }

        return false;
    }

}
