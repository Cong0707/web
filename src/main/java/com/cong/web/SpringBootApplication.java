package com.cong.web;

import arc.util.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;

import static com.cong.map.Influte.Influte2;
import static mindustry.Vars.charset;

@Controller
@org.springframework.boot.autoconfigure.SpringBootApplication
@EnableAutoConfiguration //开启自动配置
public class SpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);
    }

    @RequestMapping("/")
    String home() {
        return "html/home.html";
    }

    @RequestMapping("/castleOnly")
    String newyear() {
        return "html/newYearOnly.html";
    }

    @RequestMapping("/happy")
    String happy() {
        return "html/newYear.html";
    }

    @RequestMapping("/uploadMap")
    String uploadMap() {
        return "html/uploadMap.html";
    }

    @RequestMapping("/maps")
    String maps() {
        return "html/maps.html";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("imgFile") MultipartFile file) throws Exception {
        File dir = new File("uploadFile");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String name = ConfigUtils.getString("mapID", "50000");
        File map = new File(dir.getAbsolutePath() + File.separator + name + ".msav");
        if (file != null) {
            //保存
            file.transferTo(map);
            //解压
            //Influte1(map);
            Influte2(map);
            //判断地图是否有效
            if (mapValid(new File(map + ".unziped"))) {
                //图号+1
                int updateMapID = Integer.parseInt(name) + 1;
                ConfigUtils.set("mapID", updateMapID);
                //康康地图
                Log.info(test(new File(map + ".unziped")));
                //删解压的地图
                new File(map + ".unziped").delete();
                return "html/successful.html";
            } else {
                //删解压的地图和本体
                new File(map + ".unziped").delete();
                map.delete();
                return "html/invalid.html";
            }
        } else {
            return "html/invalid.html";
        }
    }

    public static Boolean mapValid(File file) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String s;
        while ((s = bf.readLine()) != null) {//使用readLine方法，一次读一行
            buffer.append(s.trim());
        }
        String map = buffer.toString();
        return map.startsWith("MSAV");
    }
    public static String test(File file) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String s;
        while ((s = bf.readLine()) != null) {//使用readLine方法，一次读一行
            buffer.append(s.trim());
        }
        String str = buffer.toString();
        Log.info("开始转buffer");
        //ByteBuffer bb = StandardCharsets.UTF_8.encode(str);
        ByteBuffer bb2 = ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8));
        Log.info("开始阅读");
        var strmap=NetworkIO.readStringMap(bb2);
        Log.info("阅读完毕");
        Log.info(strmap);
        return null;
    }


}

class NetworkIO {
    public static Map<String, String> readStringMap(ByteBuffer buf) {
        Log.info("Map<String, String> map = new HashMap<>();");
        Map<String, String> map = new HashMap<>();
        Log.info("int size = buf.getShort();");
        int size = buf.getShort();
        Log.info("开始循环");
        for (int i = 1; i < size; i++) {
            Log.info(readString(buf) + "," + readString(buf) + " ");
            map.put(readString(buf) + " ", readString(buf) + " ");
        }
        return map;
    }

    public static String readString(ByteBuffer read){
        short slength = read.getShort();
        Log.info(slength+"");
        if(slength >= 0){
            byte[] bytes = new byte[slength];
            read.get(bytes);
            return new String(bytes, charset);
        }else{
            return "";
        }
    }
}

/*
//TODO 寄
class NetworkIO{
    public static Map<String, String> readStringMap(ByteBuffer buf){
        Map<String, String> map = new HashMap<String, String>();
        int size = buf.getShort();
        for(int i=0;i<size;i++){
            map.put(readString(buf), readString(buf));
        }
        return map;
    }

    public static String readString(ByteBuffer ByteBuffer) {
        short index = ByteBuffer.getShort(2);
        byte b = ByteBuffer.get(index);
        String str = (String.valueOf(b));
        return str;
    }
}

/*


public static String test(File file) throws IOException {
        File rawFile = new File("D:/Desktop/web/web/build/libs/uploadFile/50000.msav");
        FileInputStream rawFileStream = new FileInputStream(rawFile);
        InflaterInputStream inflaterInputStream = new InflaterInputStream(rawFileStream);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        if (inflaterInputStream.markSupported()) {
            inflaterInputStream.mark(0);
        }
        if (inflaterInputStream.markSupported()) {
            inflaterInputStream.reset();
        }
        while (inflaterInputStream.available() != 0) {
            buffer.write(inflaterInputStream.read());
        }
        inflaterInputStream.close();
        String message1 = buffer.toString();

        Log.info(message1);

        return null;
    }

public class ByteBuffer {



    private int pos = 0;
    private byte[] buf;
    private int lim;

    public ByteBuffer(int length) {
        this.buf = new byte[length];
        this.lim = length;
    }

    public static ByteBuffer from(byte[] buffer) {
        ByteBuffer obj = new ByteBuffer(buffer.length);
        obj.put(buffer);
        obj.position(0);
        return obj;
    }

    public int getInt() {
        int o = this.pos;
        this.pos = o + 4;
        //分两半输出
        int out1 = (buf[o] & 0xFF) << 24 | (buf[o + 1] & 0xFF) << 16;
        int out2 = (buf[o + 2] & 0xFF) << 8 | (buf[o + 3] & 0xFF);
        return out1 | out2;
    }

    public int getInt() {
        int o = this.pos ? this.pos : 0;
        this.pos = o + 4;
        return (buf[o] & 0xFF) << 24 | (buf[o + 1] & 0xFF) << 16 | (buf[o + 2] & 0xFF) << 8 | (buf[o + 3] & 0xFF);
    }

    public void position(int pos) {
        this.pos = pos;
    }

    public byte get(int bytes) {
        int o = this.pos;
        this.pos = o + (bytes > 0 ? bytes : 1);
        byte[] data1 = Arrays.copyOfRange(buf, o, (bytes / 2));
        byte[] data2 = Arrays.copyOfRange(buf, o + (bytes / 2), o + bytes);
        return bytes :return data1 | data2 :return buf[o];
    }

    public void put(byte[] data) {
        if (data != null) {
            int writeBytes = Math.min(this.remaining(), data.length);
            System.arraycopy(data, 0, buf, pos, writeBytes);
            this.pos += writeBytes;
        } else if (data instanceof String) {
            byte[] stringBuffer = data.getBytes();
            this.put(stringBuffer);
        } else if (data instanceof Array) {
            byte[] arrayBuffer = new byte[data.length];
            for (int i = 0; i < data.length; i++) {
                arrayBuffer[i] = (byte) data[i]
            }
            this.put(arrayBuffer);
        } else if (data instanceof ByteBuffer) {
            data.flip();
            this.put(data.getBuffer());
            data.clear();
        } else {
            this.buf[this.pos] = data;
            this.pos++;
        }
    }

    public int remaining() {
        return this.lim - this.pos;
    }

    public int getShort() {
        int o = this.pos;
        this.pos = o + 2;
        return (buf[o] & 0xFF) << 8 | (buf[o + 1] & 0xFF);
    }
}










*/
