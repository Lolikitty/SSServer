package server;

import server.http.HttpServer;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import server.config.Config;
import server.tcp.SynchronousPlayback;
import server.ui.UIRoot;

public class Main {

    public static String PATH = System.getProperty("user.dir");

    public static ArrayList a = new ArrayList();

    public static void main(String[] args) throws Exception {
        System.out.println(PATH);
        Init();
        new Thread(new HttpServer()).start();
        new Thread(new SynchronousPlayback()).start();

//        test();

        UIRoot r = new UIRoot();
        r.display(args);
    }

    static void test() {
        new Thread(() -> {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
            }

            while (true) {
                boolean canPlay = true;
                ArrayList a3 = (ArrayList) SynchronousPlayback.LOCATIONS.get("台北");
                
                if(a3.isEmpty()){
                    canPlay = false;
                }
                
                for (int v = 0; v < a3.size(); v++) {                    
                    ArrayList b = (ArrayList) a3.get(v);
                    for (int j = 0; j < b.size(); j++) {
                        HashMap h = (HashMap) b.get(j);
                        for (Object key : h.keySet()) {
//                            System.out.println(key + "     " + h.get(key));
                            if (!(boolean) h.get(key)) {
                                canPlay = false;
                                break;
                            }
                        }
                    }
                }

                System.out.println(a3.size());

                if (canPlay) {
                    System.out.println("play ===================================================");
                } else {
                    System.out.println("wait");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            }
        }).start();
    }

    static void Init() {
        new Config();

        File dir = new File("Data");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File dir2 = new File("Data/Facebook");
        if (!dir2.exists()) {
            dir2.mkdir();
        }
        File dir3 = new File("Data/iRecki");
        if (!dir3.exists()) {
            dir3.mkdir();
        }

    }

}
