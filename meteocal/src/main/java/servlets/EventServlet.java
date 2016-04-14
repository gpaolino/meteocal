package servlets;

import it.polimi.se2.meteocal.boundary.EventFacade;
import it.polimi.se2.meteocal.boundary.UserManager;
import it.polimi.se2.meteocal.entity.Event;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
/**
 *
 * @author andreagulino
 */
@WebServlet(name = "EventServlet", urlPatterns = {"/EventServlet"})
public class EventServlet extends HttpServlet {
    
    @Inject
    private UserManager um;
    
    @Inject
    private EventFacade ef;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
           
           boolean itsme = false;
           List<Event> userEvents = null;
            
           if( null == request.getParameter("user")) {
               itsme = true;
               userEvents = ef.getCalendarEvents(um.getLoggedUser().getEmail());
           } else {
               String email = request.getParameter("user");
               
               //Check if user not exists
               //TODO: Error
               if( um.find(email) == null) {
                   out.println("ERROR: Request for not existing user");
                   return;
               }
               
               //Check if calendar is public
               if ( um.find(email).getHasPublicCalendar() == false ) {
                   out.println("SECURITY WARNING: trying to access private calendar");
                   return;
               }
               
               userEvents = ef.getCalendarEvents(email);
              
           }

           Long from =  Long.valueOf(request.getParameter("from"));
           Long to = Long.valueOf(request.getParameter("to"));
            
           JSONArray events = new JSONArray();
           
           for (Event e: userEvents) {
               Long eStart = e.getStartsAt().toInstant().getEpochSecond()*1000;
               Long eEnd = e.getEndsAt().toInstant().getEpochSecond()*1000;
               if ( eStart>=from && eStart<=to ||
                    eEnd >=from && eEnd<=to ||
                    eStart<=from && to<eEnd ) {
                   
                   //other and event private?
                   if(itsme == false && e.getIsPublic()==false) {
                       events.add(e.hide());
                   } else {
                       events.add(e);
                   }
               }
           }
            
           StringWriter sw = new StringWriter();
           events.writeJSONString(sw);
           
           out.println( "{\"success\": 1, \"result\":"); 
           out.println(events);
           out.println( "}");
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
