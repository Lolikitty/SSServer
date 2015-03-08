package server.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import server.config.Config;
import server.database.SQL;
import static server.tcp.Synchronous.LOCATION;
import server.ui.Log;
import server.ui.UIRoot;
import java.net.URLDecoder;
//import static server.tcp.Synchronous.hsinchu;

public class SynchronousPlayback implements Runnable {

    public static HashMap LOCATIONS = new HashMap();

    @Override
    public void run() {

        try {
            while (!UIRoot.IsUIReady()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException ex) {
        }

        ServerSocket ss = null;
        try {
            ss = new ServerSocket(Config.TCP_SERVER_PORT);
            Log.printInfo("SynchronousPlayback", "TCP 伺服器已啟動, Port : " + Config.TCP_SERVER_PORT);
        } catch (IOException ex) {
            Log.printError("SynchronousPlayback", ex.getMessage());
        }

        new Thread(new Synchronous(LOCATIONS)).start();

        // Config.SERVER_IS_RUN
        while (true) {
            try {
                Socket s = ss.accept();
                Client c = new Client();
                c.s = s;
                new Thread(c).start();

            } catch (IOException ex) {
                Log.printError("SynchronousPlayback", ex.getMessage());
            }
        }
    }

}

class Synchronous implements Runnable {

    public static HashMap LOCATION;
//    SynchronousPlayback.LOCATIONS = LOCATION;

    public Synchronous(HashMap hm) {
        LOCATION = hm;
    }

    int i;

    @Override
    public void run() {

        String nowTemp = getNow();

        while (true) {
            try (ResultSet rs = new SQL().getData("SELECT name FROM location;")) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    ArrayList<ArrayList> tablesGroup = new ArrayList<>();
                    try (ResultSet rs2 = new SQL().getData("SELECT * FROM data WHERE to_timestamp( to_char(now(), 'YYYY-MM-DD HH24'), 'YYYY-MM-DD HH24') >= start_play_time AND now() <= end_play_time AND location = '" + name + "';");) {
                        while (rs2.next()) {
                            ArrayList<HashMap> a = new ArrayList<>();
                            a.clear();
                            for (int y = 1; y <= 10; y++) {
                                for (int x = 1; x <= 10; x++) {
                                    if (rs2.getBoolean("ctid_" + y + "_" + x)) {
                                        HashMap m = new HashMap();
                                        m.put("ctid_" + y + "_" + x, false);
                                        a.add(m);
                                        Log.printDebug("SynchronousPlayback / Synchronous", "### " + "ctid_" + y + "_" + x);
                                    }
                                }
                            }
                            tablesGroup.add(a);
                        }
                    }
                    LOCATION.put(name, tablesGroup);
                }
                Log.printDebug("SynchronousPlayback / Synchronous", nowTemp + " ------------------------------");
            } catch (Exception ex) {
                Log.printError("SynchronousPlayback / Synchronous", ex.getMessage());
            }

            try {
                while (nowTemp.equals(getNow())) {
                    Thread.sleep(1000);
                }
                nowTemp = getNow();
            } catch (Exception ex) {
                Log.printError("SynchronousPlayback / Synchronous", ex.getMessage());
            }
        }
    }

    String getNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR);
        return year + "-" + month + "-" + day + " " + hour + ":00:00";
    }
}

class Client implements Runnable {

    public static HashMap TABLE_PRINT_WRITER = new HashMap();

    public Socket s;

    @Override
    public void run() {
        try {

            System.out.println("--------------------------------------------------------- 1");

            BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter w = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);

            System.out.println("--------------------------------------------------------- 1.1");

            String line = r.readLine();

            System.out.println("--------------------------------------------------------- 1.2");

            line = URLDecoder.decode(line, "UTF-8");

            String[] msg = line.split(",");

            String location = msg[0];
            String tableID = msg[1];

            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
            }

            System.out.println("--------------------------------------------------------- 1.5");

            TABLE_PRINT_WRITER.put(location + "-ctid_" + tableID, w);
            w.println("ok");
            w.flush();

            System.out.println("--------------------------------------------------------- 2");

            Log.printInfo("SynchronousPlayback / Client", "# TCP Init Finish : " + line);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }

            //----------------------------------------------------------------------------------
            ArrayList a = (ArrayList) LOCATION.get(location);
            for (int v = 0; v < a.size(); v++) {
                ArrayList b = (ArrayList) a.get(v);
                for (int j = 0; j < b.size(); j++) {
                    HashMap h = (HashMap) b.get(j);
                    for (Object key : h.keySet()) {
                        if (key.equals("ctid_" + tableID)) {
                            h.put("ctid_" + tableID, true);
                        }
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }

            //---------------------------------------------------------------------------
            boolean canPlay = true;

            ArrayList a2 = (ArrayList) LOCATION.get(location);

            if (a2.isEmpty()) {
                canPlay = false;
            }

            for (int v = 0; v < a2.size(); v++) {
                ArrayList b = (ArrayList) a2.get(v);
                if (b.isEmpty()) {
                    canPlay = false;
                }
                for (int j = 0; j < b.size(); j++) {
                    HashMap h = (HashMap) b.get(j);
                    if (h.isEmpty()) {
                        canPlay = false;
                    }
                    for (Object key : h.keySet()) {
                        if (!(boolean) h.get(key)) {
                            canPlay = false;
                            break;
                        }
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }

            Log.printDebug("SynchronousPlayback / Client", "Can Play ? " + canPlay);

            if (canPlay) {
                Log.printDebug("SynchronousPlayback / Client", "Ready Play...");

                ArrayList<PrintWriter> pwArray = new ArrayList<>();

                ArrayList a3 = (ArrayList) LOCATION.get(location);
                for (int v = 0; v < a3.size(); v++) {
                    ArrayList b = (ArrayList) a3.get(v);
                    for (int j = 0; j < b.size(); j++) {
                        HashMap h = (HashMap) b.get(j);
                        int x = 0;
                        Log.printDebug("SynchronousPlayback / Client", "-------------------------------------------------------------------------- " + h.size());
                        for (Object key : h.keySet()) {
                            h.put(key, false);
                            PrintWriter pw = (PrintWriter) TABLE_PRINT_WRITER.get(location + "-" + key);
                            pwArray.add(pw);
                        }
                    }
                }

                // 降低延遲所以分開寫 ------------------------------------------------------------------------------
                Log.printDebug("SynchronousPlayback / Client", "pwArray : " + pwArray.size());

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                }

                for (int i = 0; i < 10; i++) {
                    pwArray.forEach(pw -> {
                        pw.println("y");
                    });
                }

                Log.printInfo("SynchronousPlayback / Client", "Play !");

            }
            Log.printDebug("SynchronousPlayback / Client", "-------");
        } catch (Exception ex) {
            Log.printError("SynchronousPlayback / Client", ex.getMessage());
        }

    }

}
