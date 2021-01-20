package com.littlegui.javaee;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SampleServlet
 */
//@WebServlet("/SampleServlet")
public class SampleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SampleServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        HttpSession session = req.getSession();

        String localIpPortCurReq = req.getLocalAddr() + ':' + req.getLocalPort();

        String sessionId = session.getId();

        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("<body>");
        out.println("<h1>Hello Servlet Distributed</h1>");
        out.println("<br>");
        out.println("<p>Processed server ip and port current request: " + localIpPortCurReq);
        out.println("<p>User session ID: " + sessionId + "</p>");
        out.println("<form action='/ignite-web/SessionSetServlet' method='post'>");
        out.println("<p>Enter value to be added to session:</p>");
        out.println("<input type='text' name='text-val'/>");
        out.println("<input type='submit' value='Set to session'/>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");

        session.setAttribute("local-ip-port", localIpPortCurReq);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
