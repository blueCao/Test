package mmap;

public class ReadWriteThreadTest {
    public static void main(String []args) throws Exception{
        if (System.getProperty("write") != null) {
            write(args);
        }
        if (System.getProperty("read") != null) {
            read(args);
        }
    }

    public static void write(String []args)throws Exception{
        // 默认写入 5 g, 20线程
        int writeSize = 100;
        String writeSizeString = System.getProperty("writeSize");
        if(writeSizeString != null && Integer.valueOf(writeSizeString) > 0){
            writeSize = Integer.valueOf(writeSizeString);
        }
        int writeThread = 20;
        String writeThreadString = System.getProperty("writeThread");
        if(writeThreadString != null && Integer.valueOf(writeThreadString) > 0){
            writeThread = Integer.valueOf(writeThreadString);
        }
        Thread[] writePools = new Thread[writeThread];
        long startTimestamp = System.currentTimeMillis();
        for(int i=0; i<writePools.length; i++){
            writePools[i] = new ReadWriteThread.WriteThread(writeSize,"data/block"+i);
            writePools[i].start();
        }
        for(int i=0; i<writePools.length; i++){
            writePools[i].join();
        }
        long endTimestamp = System.currentTimeMillis();
        System.out.println("finished write "+ writeSize +"M in "+(endTimestamp-startTimestamp) +"ms. Avg: " + writeSize * 1024 / (endTimestamp-startTimestamp)+"M/s");

    }


    public static void read(String []args)throws Exception{
        // 默认读入 5g
        int readSize = 100;
        String readSizeString = System.getProperty("readSize");

        if(readSizeString != null && Integer.valueOf(readSizeString) > 0){
            readSize = Integer.valueOf(readSizeString);
        }

        int readThread = 20;
        String readThreadString = System.getProperty("readThread");
        if(readThreadString != null && Integer.valueOf(readThreadString) > 0){
            readThread = Integer.valueOf(readThreadString);
        }

        int offset = 20;
        String offsetString = System.getProperty("offset");
        if(offsetString != null && Integer.valueOf(offsetString) > 0){
            offset = Integer.valueOf(offsetString);
        }

        Thread[] readPools = new Thread[readThread];
        long startTimestamp = System.currentTimeMillis();
        for(int i=0; i<readPools.length; i++){
            readPools[i] = new ReadWriteThread.ReadThread("data/block"+i,0,readSize);
            readPools[i].start();
        }
        for(int i=0; i<readPools.length; i++){
            readPools[i].join();
        }
        long endTimestamp = System.currentTimeMillis();
        System.out.println("finished read "+ readSize +" in "+(endTimestamp-startTimestamp) +"ms. Avg: " + readSize * 1024 / (endTimestamp-startTimestamp)+"M/s");
    }

}