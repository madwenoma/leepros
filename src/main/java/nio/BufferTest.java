package nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Administrator on 2018/3/22.
 */
public class BufferTest {

    private static void nio() {
        try {
            FileInputStream fis = new FileInputStream(new File("d:\\jdk-8u51-windows-x64.exe"));
//            RandomAccessFile file = new RandomAccessFile("E:\\input-file.txt", "rw");
            FileChannel channel = fis.getChannel();
            ByteBuffer header = ByteBuffer.allocate(1024);
            long date1 = System.currentTimeMillis();
            int count = channel.read(header);
            while (count != -1) {
                header.flip();
                header.clear();
                count = channel.read(header);
            }
            long date2 = System.currentTimeMillis();
            System.out.println(date2 - date1);


            channel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void io() {
        File file = new File("d:\\jdk-8u51-windows-x64.exe");
        try {
            FileInputStream fis = new FileInputStream(file);
            long date1 = System.currentTimeMillis();
            int count = fis.read();
            while (count != -1) {
                count = fis.read();
            }
            long date2 = System.currentTimeMillis();
            System.out.println(date2 - date1);
            fis.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }


    private static void ioBuffer() {
        File file = new File("d:\\jdk-8u51-windows-x64.exe");
        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            long date1 = System.currentTimeMillis();
            int count = bis.read();
            byte[] bytes = new byte[100 * 1024 * 1024];
            while (count != -1) {
                count = bis.read(bytes, 0, count);
            }
            long date2 = System.currentTimeMillis();
            System.out.println(date2 - date1);
            fis.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
    }


    public static void main(String[] args) {
        ioBuffer();
        nio();//
        io();//

    }

}
