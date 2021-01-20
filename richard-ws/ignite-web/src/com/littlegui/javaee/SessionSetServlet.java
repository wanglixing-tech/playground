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
 * Servlet implementation class SessionSetServlet
 */
//@WebServlet("/SessionSetServlet")
public class SessionSetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SessionSetServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        Object localIpPortPrevReqObj = session.getAttribute("local-ip-port");
        String localIpPortPrevReq = localIpPortPrevReqObj != null ? localIpPortPrevReqObj.toString() : "N/A";

        String localIpPortCurReq = req.getLocalAddr() + ':' + req.getLocalPort();

        String sessionId = session.getId();

        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("<body>");
        out.println("<h1>Hello Servlet Distributed</h1>");
        out.println("<br>");
        out.println("<p>Processed server ip and port previous request: " + localIpPortPrevReq);
        out.println("<p>Processed server ip and port current request: " + localIpPortCurReq);
        out.println("<p>User session ID: " + sessionId + "</p>");
        out.println("<p>Entered value has been saved in the session. To view saved value click on this link...</p>");
        out.println("<a href='/ignite-web/SessionGetServlet'>Get session value</a>");
        out.println("</body>");
        out.println("</html>");

        String valueReceived = req.getParameter("text-val");
        session.setAttribute("value", valueReceived);

        session.setAttribute("local-ip-port", localIpPortCurReq);
    }
}
