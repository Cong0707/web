package com.cong.map;

import java.io.*;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class Influte {
    public static void Influte1(File file) throws Exception {
        // 输入流
        InputStream is = new FileInputStream(file);
        // 字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 缓冲区 缓冲区越大，速度越快
        byte[] bu = new byte[8192];
        int count = 0;
        // 将流写到字节输出流中
        while ((count = is.read(bu)) > 0) {
            baos.write(bu, 0, count);
        }
        // 将字节输出流转为字节数组
        byte[] text = baos.toByteArray();
        // 如果要使用同一个流对象，一定要重设清空原有数据
        baos.reset();
        // 解压器
        Inflater decompresser = new Inflater();
        // 重置 inflater 以处理新的输入数据集
        decompresser.reset();
        // 为解压缩设置输入数据
        decompresser.setInput(text);
        // 用于接受压缩后的数据
        try {
            // 缓冲
            byte[] buf = new byte[8192];
            // 如果已到达压缩数据流的结尾，则返回 true
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                baos.write(buf, 0, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 关闭解压缩器并放弃所有未处理的输入
        decompresser.end();
        // 输出流
        FileOutputStream fos = new FileOutputStream(file+".unziped");
        // 将字节输出流写入到输出流中
        baos.writeTo(fos);
        fos.close();
        baos.close();
        is.close();
    }

    public static void Influte2(File file) throws IOException {
        var is=new InflaterInputStream(new java.io.FileInputStream(file));
        try {
            FileOutputStream fos = new FileOutputStream(file+".unziped");

            int b;
            while ((b = is.read()) != -1) {
                fos.write(b);
            }

            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
