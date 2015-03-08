/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import config.Config;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Loli
 */
public class Run extends HttpServlet {

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

            String file = request.getParameter("file");
            String className = file.substring(0, file.indexOf("."));

            try {
                
                JSONObject obj = new JSONObject();
                JSONArray errArray = new JSONArray();
                JSONArray outArray = new JSONArray();
                                
                for(String aError : execErroutput(Config.DATA_PATH, Config.CMD_ENCODE, className)){
                    errArray.put(aError);
                }
                for(String aOutput : execOutput(Config.DATA_PATH, Config.CMD_ENCODE, className)){
                    outArray.put(aOutput);
                }
                
                obj.put("Error", errArray);
                obj.put("Output", outArray);
                
                out.println(obj.toString(4));
                
            } catch (Exception e) {
                System.err.println(e);
            }

        }
    }

    ArrayList<String> execErroutput(String runPath, String encode, String className) throws Exception {
        Runtime rt = Runtime.getRuntime();

        Process proc = rt.exec("javac " + className + ".java", new String[]{}, new File(runPath));
        InputStream stderr = proc.getErrorStream();
        InputStreamReader isr = new InputStreamReader(stderr, encode);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        ArrayList<String> a = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            System.err.println(line);
            a.add(line);
        }
        return a;
    }

    ArrayList<String> execOutput(String runPath, String encode, String className) throws Exception {
        Runtime rt = Runtime.getRuntime();

        Process proc = rt.exec("java " + className, new String[]{}, new File(runPath));
        InputStream stderr = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stderr, encode);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        ArrayList<String> a = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            System.out.println(line);
            a.add(line);
        }
        return a;
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
