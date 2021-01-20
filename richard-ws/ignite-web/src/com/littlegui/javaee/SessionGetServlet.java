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
 * Servlet implementation class SessionGetFromServlet
 */
//@WebServlet("/SessionGetServlet")
public class SessionGetServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SessionGetServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();

		Object localIpPortPrevReqObj = session.getAttribute("local-ip-port");
		String localIpPortPrevReq = localIpPortPrevReqObj != null ? localIpPortPrevReqObj.toString() : "N/A";

		String localIpPortCurReq = req.getLocalAddr() + ':' + req.getLocalPort();

		Object valueObj = session.getAttribute("value");
		String value = valueObj != null ? valueObj.toString() : "N/A";

		String sessionId = session.getId();

		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("<h1>Hello Servlet Distributed</h1>");
		out.println("<br>");
		out.println("<p>Processed server ip and port previous request: " + localIpPortPrevReq);
		out.println("<p>Processed server ip and port current request: " + localIpPortCurReq);
		out.println("<p>User session ID: " + sessionId + "</p>");
		out.println("<p>Session value: " + value + "</p>");
		out.println("</body>");
		out.println("</html>");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}
}
