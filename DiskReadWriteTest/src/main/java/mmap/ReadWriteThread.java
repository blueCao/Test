package mmap;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ReadWriteThread {
    static class WriteThread extends Thread{
        //单位G
        static byte[] gitbytes = new byte[1024 * 1024];
        int size;
        String filePath;
        public WriteThread(int size, String filePath) throws Exception{
            // 检查路径是否存在
            if(new File(filePath).exists()){
                throw new Exception("文件: "+filePath+" 已存在，请重新命名");
            }
            // 检查目录是否存在
            int lastIndex = filePath.lastIndexOf("/");
            if(lastIndex >= 0){
                File dirPath = new File(filePath.substring(0,lastIndex+1));
                if (!dirPath.exists() && !dirPath.isDirectory()){
                    throw new Exception("文件目录: "+dirPath.getName()+" 不存在请重新命名");
                }
            }

            this.size = size;
            this.filePath = filePath;
        }

        // 在指定路径写入指定大小的数据
        @Override
        public void run(){
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(new File(filePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            long writtenLength = 0;
            long startTime = System.currentTimeMillis();
            for(int i=0; i<size;i++){
                try {
                    output.write(gitbytes);
                    writtenLength += gitbytes.length;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            long endTime = System.currentTimeMillis();
            System.out.println("fininished wirte :"+ writtenLength+" bytes" + ". In "+(endTime - startTime) + "ms. "+ writtenLength / (endTime - startTime) / 1024 +"M/s");
        }
    }
    static class ReadThread extends Thread{
        //单位G
        static byte[] gitbytes = new byte[1024*1024];
        String filePath;
        int offset;
        int size;
        public ReadThread(String filePath, int offset,int size) throws Exception{
            // 检查路径是否存在
            if(!new File(filePath).exists()){
                throw new Exception("文件: "+filePath+" 不存在，请重新命名");
            }
            this.filePath = filePath;
            this.size = size;
            this.offset = offset;
        }

        // 在指定路径读入指定位置大小的数据
        @Override
        public void run() {
            MappedByteBuffer input = null;
            FileChannel channel = null;
            try {
                channel = new RandomAccessFile(new File(filePath), "r").getChannel();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (long j = 0; j < size; j = j + 1024) {
                try {
                    input = channel.map(FileChannel.MapMode.READ_ONLY, (offset+j)*1024*1024, 1024 * 1024 * 1024);
                    System.out.println(input.isDirect());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long readLength = 0;
                long startTime = System.currentTimeMillis();
                for (int i = 0; i < 1024; i++) {
                    int start = i * gitbytes.length;
                    int length = gitbytes.length;
//                if ((size * 1024 * 1024 - start) % gitbytes.length == 0){
//                    length = gitbytes.length;
//                }else{
//                    length = (size * 1024 * 1024 - start) % gitbytes.length;
//                }
                    input.get(gitbytes, 0, length);
                    readLength += length;
                }
                long endTime = System.currentTimeMillis();
                System.out.println("fininished read :" + readLength + " bytes" + ". In " + (endTime - startTime) + "ms. " + readLength / (endTime - startTime) / 1024 + "M/s");
            }
        }
    }
}
