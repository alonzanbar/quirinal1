package edu.usc.ict.iago.views;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Finish
 */
@WebServlet(name="Finish", loadOnStartup=1, description = "Starting servlet for opening screen.", urlPatterns = { "/finish" })
public class Intermediate extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public Intermediate() {
		//DatabaseUtils.connect();
	}
//	boolean special;
	boolean vh;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		request.getRequestDispatcher("finish.jsp").forward(request, response);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
	}
	
}
