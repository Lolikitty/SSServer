/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import config.Config;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Loli
 */
public class CreateFile extends HttpServlet {

    String javaExample
            = "\r\n"
            + "public class JavaExample {\r\n"
            + "    public static void main(String[] args) {\r\n"
            + "        System.out.println(\"Hello world !\");\r\n"
            + "    }\r\n"
            + "}";

    String servletExample
            = "\r\n"
            + "import java.io.IOException;\r\n"
            + "import java.io.PrintWriter;\r\n"
            + "import javax.servlet.ServletException;\r\n"
            + "import javax.servlet.annotation.WebServlet;\r\n"
            + "import javax.servlet.http.HttpServlet;\r\n"
            + "import javax.servlet.http.HttpServletRequest;\r\n"
            + "import javax.servlet.http.HttpServletResponse;\r\n"
            + "\r\n"
            + "public class ServletExample extends HttpServlet {\r\n"
            + "\r\n"
            + "    @Override\r\n"
            + "    protected void doGet(HttpServletRequest request, HttpServletResponse response)\r\n"
            + "            throws ServletException, IOException {\r\n"
            + "        \r\n"
            + "    }\r\n"
            + "\r\n"
            + "    @Override\r\n"
            + "    protected void doPost(HttpServletRequest request, HttpServletResponse response)\r\n"
            + "            throws ServletException, IOException {\r\n"
            + "        \r\n"
            + "    }\r\n"
            + "}";

    String htmlExample
            = "<!DOCTYPE html>\r\n"
            + "\r\n"
            + "<html>\r\n"
            + "    <head>\r\n"
            + "        <title> Title : HtmlExample </title>\r\n"
            + "    </head>\r\n"
            + "    <body>\r\n"
            + "        <h1> Hello HtmlExample </h1>\r\n"
            + "    </body>\r\n"
            + "</html>";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=utf-8");
        try (PrintWriter out = response.getWriter()) {

            try {
                String format = request.getParameter("format");
                String name = request.getParameter("name");

                File f = new File(Config.DATA_PATH);
                if (!f.exists()) {
                    f.mkdirs();
                }

                String fileExtension = "";

                if (format.equals("java")) {
                    fileExtension = "java";
                } else if (format.equals("servlet")) {
                    fileExtension = "java";
                } else if (format.equals("html")) {
                    fileExtension = "html";
                }

                String filePath = Config.DATA_PATH + "/" + name + "." + fileExtension;
                File ff = new File(filePath);
                ff.createNewFile();

                try (BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ff, true), "UTF-8"))) {
                    if (format.equals("java")) {
                        fw.write(javaExample.replaceAll("JavaExample", name));
                    } else if (format.equals("servlet")) {
                        fw.write(servletExample.replaceAll("ServletExample", name));
                    } else if (format.equals("html")) {
                        fw.write(htmlExample.replaceAll("HtmlExample", name));
                    }
                    fw.newLine();
                    fw.flush();
                }

                out.println("{\"Status\":\"success\"}");
            } catch (Exception e) {
                out.println("{\"Status\":\"error\"}");
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
