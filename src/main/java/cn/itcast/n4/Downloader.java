package cn.itcast.n4;

public class Downloader {

    public static Integer download(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 100;
    }
}
