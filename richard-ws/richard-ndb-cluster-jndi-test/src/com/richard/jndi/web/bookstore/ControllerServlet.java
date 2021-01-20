package com.richard.jndi.web.bookstore;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
 
/**
 * ControllerServlet.java
 * This servlet acts as a page controller for the application, handling all
 * requests from the user.
 */
public class ControllerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BookDAO bookDAO;
 
    public void init() {
        //String jdbcURL = getServletContext().getInitParameter("jdbcURL");
        //String jdbcUsername = getServletContext().getInitParameter("jdbcUsername");
        //String jdbcPassword = getServletContext().getInitParameter("jdbcPassword");
 
        //bookDAO = new BookDAO(jdbcURL, jdbcUsername, jdbcPassword);
		try {
			Context initialContext = new InitialContext();
			DataSource datasource = (DataSource)initialContext.lookup("MySqlDS");
	    	bookDAO = new BookDAO(datasource);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
 
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        System.out.println("Selected Action:" + action);
        try {
            switch (action) {
            case "/new":
                showNewForm(request, response);
                break;
            case "/insert":
                insertBook(request, response);
                break;
            case "/delete":
                deleteBook(request, response);
                break;
            case "/edit":
                showEditForm(request, response);
                break;
            case "/update":
                updateBook(request, response);
                break;
            default:
                listBook(request, response);
                break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }
 
    private void listBook(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
		try {
			Context initialContext = new InitialContext();
			DataSource datasource = (DataSource)initialContext.lookup("MySqlDS");
	    	bookDAO = new BookDAO(datasource);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        List<Book> listBook = bookDAO.listAllBooks();
        request.setAttribute("listBook", listBook);
        RequestDispatcher dispatcher = request.getRequestDispatcher("BookList.jsp");
        dispatcher.forward(request, response);
    }
 
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("BookForm.jsp");
        dispatcher.forward(request, response);
    }
 
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
		try {
			Context initialContext = new InitialContext();
			DataSource datasource = (DataSource)initialContext.lookup("MySqlDS");
	    	bookDAO = new BookDAO(datasource);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        int id = Integer.parseInt(request.getParameter("id"));
        Book existingBook = bookDAO.getBook(id);
        RequestDispatcher dispatcher = request.getRequestDispatcher("BookForm.jsp");
        request.setAttribute("book", existingBook);
        dispatcher.forward(request, response);
 
    }
 
    private void insertBook(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        float price = Float.parseFloat(request.getParameter("price"));
		try {
			Context initialContext = new InitialContext();
			DataSource datasource = (DataSource)initialContext.lookup("MySqlDS");
	    	bookDAO = new BookDAO(datasource);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Book newBook = new Book(title, author, price);
        bookDAO.insertBook(newBook);
        response.sendRedirect("list");
    }
 
    private void updateBook(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        float price = Float.parseFloat(request.getParameter("price"));
		try {
			Context initialContext = new InitialContext();
			DataSource datasource = (DataSource)initialContext.lookup("MySqlDS");
	    	bookDAO = new BookDAO(datasource);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
        Book book = new Book(id, title, author, price);
        bookDAO.updateBook(book);
        response.sendRedirect("list");
    }
 
    private void deleteBook(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
		try {
			Context initialContext = new InitialContext();
			DataSource datasource = (DataSource)initialContext.lookup("MySqlDS");
	    	bookDAO = new BookDAO(datasource);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
        Book book = new Book(id);
        bookDAO.deleteBook(book);
        response.sendRedirect("list");
 
    }
}
