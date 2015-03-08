package server.http.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import server.database.SQL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import server.tcp.SynchronousPlayback;
import server.ui.Log;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 500, // 10MB
        maxRequestSize = 1024 * 1024 * 500)   // 50MB

public class UploadFile extends HttpServlet {

    // getParameter ( )
    String isFBUser;
    String id;
    String location;
    String playMode;
    String year;
    String month;
    String day;
    String startHour;
    String endHour;
    String chooseTableID;
    String type;
    int loopTime;
    int times;
    JSONObject obj = new JSONObject();
    JSONArray list = new JSONArray();
    String jstring;

    // InitPath ( )
    String userDir;
    String path;
    String fileDir;

    @Override

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try (PrintWriter out = resp.getWriter()) {
            try {
                getParameter(req);
                InitPath();
                if (type.equals("Image")) {
                    uploadImage(req, resp);
                } else if (type.equals("Video")) {
                    uploadVideo(req, resp);
                }
                setPoint();
                Refresh();
                out.print("pass");
            } catch (ServletException | IOException | NumberFormatException | ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
                out.print(e.getMessage());
            }
            System.out.println("Upload  Finish !!");
        }

    }

    void Refresh() {
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
                SynchronousPlayback.LOCATIONS.put(name, tablesGroup);
            }
        } catch (Exception ex) {
            Log.printError("UploadFile / Refresh", ex.getMessage());
        }
    }

    void getParameter(HttpServletRequest req) {
        isFBUser = req.getParameter("isFBUser");
        id = req.getParameter("id");

        jstring = req.getParameter("data");
        
        System.out.println(jstring);
        
        type = req.getParameter("type");
        try {
            jstring = java.net.URLDecoder.decode(jstring, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(UploadFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        obj = new JSONObject(jstring);

    }

    void InitPath() {
        // 未來要處理相同時間相同ID或某個平板ID存在的情況
        userDir = "";

        if (isFBUser.equals("true")) {
            userDir = "Facebook";
        } else {
            userDir = "iRecki";
        }

        path = "Data/" + userDir + "/" + id;

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }

        int i = 1;
        File f;
        while (true) {
            f = new File(path + "/" + i);
            if (!f.exists()) {
                path = path + "/" + i;
                f.mkdir();
                break;
            }
            i++;
        }
        fileDir = "Data/" + userDir + "/" + id + "/" + i;
    }

    void uploadImage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException,
            NumberFormatException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, SQLException {

        HttpServletRequest r = (HttpServletRequest) req;
        String fileNames = "";
        String fileExtension = "";
        String fileExtensions = "";
        int i = 1;

        for (Part p : r.getParts()) {
            String urlEncoderFileName = extractFileName(p);
            String fileName = java.net.URLDecoder.decode(urlEncoderFileName, "UTF-8");
            fileNames += fileName + ",";
            fileExtension = getExtension(fileName);
            fileExtensions += fileExtension + ",";
            p.write(path + "/" + i + "." + fileExtension);
            i++;
        }

        // 刪除後面的逗號
        fileNames = fileNames.substring(0, fileNames.length() - 1);
        fileExtensions = fileExtensions.substring(0, fileExtensions.length() - 1);

        saveToDataBase(fileNames, fileExtensions);
    }

    void uploadVideo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NumberFormatException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, SQLException {

        HttpServletRequest r = (HttpServletRequest) req;
        Part p = r.getPart("file");
        String fileName = extractFileName(p);

        // 未來要處理圖片延遲幾秒的情況、最多撥放幾張圖片
        p.write(path + "/" + "1." + getExtension(fileName));

        saveToDataBase(fileName, getExtension(fileName));
    }

    void saveToDataBase(String fileName, String fileExtension) throws NumberFormatException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, SQLException {

        JSONArray list_in = obj.getJSONArray("JURL");

        times = list_in.length();
        for (int i = 0; i < list_in.length(); i++) {

            JSONObject list_one = new JSONObject();
            list_one = list_in.getJSONObject(i);
            list_one = list_one.getJSONObject("o" + i);

            location = list_one.getString("location");
            playMode = list_one.getString("playMode");
            year = list_one.getString("year");
            month = list_one.getString("month");
            day = list_one.getString("day");
            startHour = list_one.getString("startHour");
            endHour = list_one.getString("endHour");
            chooseTableID = list_one.getString("chooseTableID");
            loopTime = list_one.getInt("loopTime");

            String sql = "INSERT INTO data ("
                    + "id, "
                    + "is_facebook_user, "
                    + "location, "
                    + "playmode, "
                    + "start_play_time, "
                    + "end_play_time, "
                    + "file_name, "
                    + "file_target_encod, "
                    + "file_dir, "
                    + "loop_time, "
                    + chooseTableID
                    + ") VALUES ("
                    + "'" + id + "', "
                    + "" + isFBUser + ", "
                    + "'" + location + "', "
                    + "'" + playMode + "', "
                    + "timestamp '" + year + "-" + month + "-" + day + " " + startHour + ":00:00" + " ', "
                    + "timestamp '" + year + "-" + month + "-" + day + " " + endHour + ":00:00" + " ', "
                    + "'" + fileName + "', "
                    + "'" + fileExtension + "', "
                    + "'" + fileDir + "', "
                    + "" + loopTime + ", "
                    + TableIDSQL()
                    + ");";

            new SQL().setData(sql);
        }

    }

    String TableIDSQL() {

        String[] ctid = chooseTableID.split(",");

        String s = "";

        for (int i = 0; i < ctid.length; i++) {
            s += "true, ";
        }

        // 刪除後面的逗號
        s = s.substring(0, s.length() - 2);

        return s;
    }

    // 未來要處理沒有附檔名的情況
    // 未來要處理未支援格式的情況
    public static String getExtension(String fileName) {
        int startIndex = fileName.lastIndexOf(46) + 1;
        int endIndex = fileName.length();
        return fileName.substring(startIndex, endIndex);
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }

    void setPoint() throws NumberFormatException, ClassNotFoundException,
            InstantiationException, IllegalAccessException, SQLException {
        String sql = "UPDATE users SET point = point - " + times + " WHERE id = '" + id + "' AND is_facebook_user = " + isFBUser + ";";
        new SQL().setData(sql);
    }

}
