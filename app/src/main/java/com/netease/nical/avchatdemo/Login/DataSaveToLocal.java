package com.netease.nical.avchatdemo.Login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataSaveToLocal {

    /**
     * 将字节流写入到本地文件中
     * @param content
     * @param filepath
     */
    public void saveDataToLocal(String content,String filepath){

        File file=new File(filepath);

        if(fileIsExists(file)){
            file.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file,true);
            fos.write(content.getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取本地文件字符串
     * @param filepath
     * @return
     */
    public String readDataToLocal(String filepath) {
        File file = new File(filepath);
        String str = "";
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            str = br.readLine();
            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();

        }
        return str;
    }


    /**
     * 判断本地文件是否存在
     * @param f
     * @return
     */
    private boolean fileIsExists(File f) {
        try {
            if (!f.exists() || !f.isFile()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 根据文件路径删除文件
     * @param filepath
     * @return
     */
    public boolean deleteFile(String filepath){
        Boolean b = false;
        File f = new File(filepath);
        if (fileIsExists(f)){
            if(f.delete()){
                b = true;
            }else {
                b = false;
            }
        }
        return b;
    }

}
