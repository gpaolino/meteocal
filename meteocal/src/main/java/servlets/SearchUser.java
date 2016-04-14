package servlets;

//IMPORTA!
import it.polimi.se2.meteocal.boundary.UserManager;
import it.polimi.se2.meteocal.entity.User;

import org.json.simple.JSONArray;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author andreagulino
 */

   

@WebServlet(name = "SearchUser", urlPatterns = {"/search"})
public class SearchUser extends HttpServlet {

    @EJB
    UserManager um;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            
            
            JSONArray results = new JSONArray();
            
            String q = request.getParameter("q");
            if (q==null) {
                out.println("[\"error\":\"noParameter\"]");
            } else {
                
                
                for(User el: um.searchUser(q)) {
                    //Not return self user
                    if(um.getLoggedUser().getEmail() != el.getEmail()) {
                        results.add(el);
                    }
                }
 
                StringWriter sw = new StringWriter();
                results.writeJSONString(sw);
                
                out.println(results);
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
