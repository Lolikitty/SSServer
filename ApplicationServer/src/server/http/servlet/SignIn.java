package server.http.servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import server.database.SQL;
import java.io.PrintWriter;

public class SignIn extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
       
        String id = req.getParameter("id");
        String password = req.getParameter("password");
        String IsFacebookUser = req.getParameter("IsFacebookUser");
        

   
         
        
       
        if (IsFacebookUser.equals("false")) {
            SQL sql = new SQL();
            try {
             //for start SignIn  
            /*    String s
                        = "SELECT id FROM users WHERE id = '" + id + "' AND password = '" + password + "';";
                ResultSet rs = sql.getData(s);
                
                if (rs.next()) {
                    res.getWriter().print("pass!!!");
                    System.out.println("pass!!!");
                } else {
                    res.getWriter().print("no_pass");
                    System.out.println("No Pass");
                }*/
             //end SignIn   
                
                
                
               
             //for start SingUp 
                String s
                        = "INSERT INTO users (id, password ,is_facebook_user) VALUES ('" + id + "','" + password + "','" + IsFacebookUser + "');";
                
                 sql.setData(s);
              //end Singup
               

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
                res.getWriter().print("error");
                System.out.println("Error : " + ex.getMessage());
            }
        }
    }

}
