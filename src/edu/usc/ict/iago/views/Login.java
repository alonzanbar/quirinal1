package edu.usc.ict.iago.views;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.usc.ict.iago.utils.DatabaseUtils;
import edu.usc.ict.iago.utils.ServletUtils;

/**
 * Servlet implementation class Login
 * Does a lot of things involving logging in, sending emails when you forget your password, managing session data.
 * Plays back and forth with its friend, login.jsp, which contains an ungodly amount of scriptlets. Sorry.
 */
@WebServlet(name="Login", loadOnStartup=1, description = "Starting servlet for opening screen.", urlPatterns = { "/login" })
public class Login extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	//private String sessionid;//deprecated
	private String user = null;
	private int id = 0;
	
	public Login() 
	{
		//DatabaseUtils.connect();
	}
	
	private String shaEncode(String password)
	{	 
        MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        md.update(password.getBytes());
 
        byte byteData[] = md.digest();
 
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		//really not using ServletUtils version of this for much atm
		/**
		sessionid = ServletUtils.getSessionId(request);
		
	    // If the session ID wasn't sent, generate one.
	    // Then be sure to send it to the client with the response.
	    if (sessionid == null) {
	      sessionid = ServletUtils.generateSessionId();
	      response.addCookie(new Cookie("sessionid", sessionid));
	      
	    }
	    **/
	    
	    if(request.getParameter("logoff") != null)
	    {
			user = null;
			id = 0;
	    }
	    
	    //reset session variables, but keep logged in unless logoff
	    request.getSession().setAttribute("user", user);
	    request.getSession().setAttribute("id", id);
	    request.getSession().setAttribute("success_login", null);
	    request.getSession().setAttribute("success_forgot", null);
	    request.getSession().setAttribute("success_resetForm", null);
	    request.getSession().setAttribute("success_register", null);
	    request.getSession().setAttribute("error_login", null);
	    request.getSession().setAttribute("error_forgot", null);
	    request.getSession().setAttribute("error_resetForm", null);
	    request.getSession().setAttribute("error_register", null);
	    request.getSession().setAttribute("forgotForm", false);
	    request.getSession().setAttribute("resetForm", false);
	    
	    if(request.getParameter("forgot") != null)
	    	request.getSession().setAttribute("forgotForm", true);
	    
	    if(request.getParameter("reset") != null)
	    {
	    	String query = "SELECT usr,unique_forgot,unique_forgot_expiry FROM users WHERE unique_forgot=?";
	    	
	    	//encoding the parameter to follow percentage encoding would be the tidy answer here, since JavaMail already automatically (and forcefully)
	    	//encoded the URL it send to the user already
	    	//however, Java's URI class doesn't seem to adhere to the standard in query encoding the colon character to allow for full URLs to be passed as queries
	    	//as such, enjoy the following bs:
	    	//String rawParameter = request.getParameter("reset").replaceAll(":", "%3A");
	    	/**
			URI unique = null;
			try {
				unique = new URI("http", "ictstudies2-dev.ict.usc.edu", "/IAGO/login", "reset=" + request.getParameter("reset"), null);
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			String rawParameter = unique.toString().substring(unique.toString().indexOf("reset=") + 6);//all those percent-encodings
			**/
			ResultSet row = DatabaseUtils.query(query, request.getParameter("reset"));
	    	System.out.println("Reset parameter: " + request.getParameter("reset"));
	    	
	    	String err = null;
			try {
				if(!row.next())
				{
					System.err.println("User not found!");
					err = "User not found!";
				}
				else
				{
					java.util.Date date = new Date();
					long timestamp = date.getTime();
					if (timestamp > row.getTimestamp("unique_forgot_expiry").getTime())
					{
						err = "Link expired!";
						System.out.println("Link expired!");
						request.getSession().setAttribute("forgotForm", true);//redirect back so that the link shows
					}
					else
						request.getSession().setAttribute("resetForm", true);
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			request.getSession().setAttribute("error_forgot", err);
			
	    }
 	    
	    //System.out.println(sessionid + " playing against VH");
	    
		// load page without changing URL
	    System.out.println("Get User: " + user + "\nID: " + id);
		request.getRequestDispatcher("login.jsp").forward(request, response);
	}
	
	/***
	 * Handles the logic for validating and processing user input on the changing password/email page
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void postReset(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String err = null;
		
		//validation
		Pattern p = Pattern.compile("[\\w\\p{Punct}]+");
		if(request.getParameter("password") == "" || request.getParameter("passwordconf") == "" || request.getParameter("email") == "")
			err = "All the fields must be filled in!";			
		else if(request.getParameter("password").length() < 4 || request.getParameter("password").length() > 32)
		{
			err = "Your password must be between 3 and 32 characters!";
		}
		else if(!request.getParameter("password").equals(request.getParameter("passwordconf")))
		{
			err = "Your passwords do not match!";
		}
		else if(!(p.matcher(request.getParameter("password")).matches()))
		{
			err = "Your password contains invalid characters!";
		}
		
		if(err == null)
		{
			// If there are no errors
			
			String hexedpass = shaEncode(request.getParameter("password"));
			// encode their password so we don't have access to it like sneaky sneaks
			
			
			//do they exist?
			String query = "SELECT usr,unique_forgot,unique_forgot_expiry FROM users WHERE unique_forgot=?";
			ResultSet row = DatabaseUtils.query(query, request.getParameter("reset"));
			try {
				if(!row.next())
				{
					err = "User not found!";
				}
				else
				{
					String query2 = "UPDATE users SET pass=?, email=?, regIp=? WHERE usr=?";
					
					String ipAddress = request.getHeader("X-FORWARDED-FOR");
					if (ipAddress == null) {
					    ipAddress = request.getRemoteAddr();
					}
					
					DatabaseUtils.update(query2,  hexedpass, request.getParameter("email"), ipAddress, row.getString("usr"));	
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		request.getSession().setAttribute("error_resetForm", err);
		if (err == null)
			request.getSession().setAttribute("success_resetForm", "Success!");
		else
			request.getSession().setAttribute("success_resetForm", null);
	}
	
	/***
	 * Handles the logic for validating and processing user input when requesting a password reset.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void postForgot(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String err = null;
		if(request.getParameter("username") == null)
			err = "All the fields must be filled in!";
		
		//do they exist?
		String query = "SELECT id,usr,email,unique_forgot,unique_forgot_expiry FROM users WHERE usr=?";
		ResultSet row = DatabaseUtils.query(query, request.getParameter("username"));
		
		try {
			if(!row.next())
			{
				err ="User not found";
			}
			else
			{
				//can maybe do this without the generic command from DatabaseUtils
				String query2 = "UPDATE users SET unique_forgot=?, unique_forgot_expiry=? WHERE usr=?";
				
				java.util.Date date = new Date();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.HOUR, 24);//you get an extra day
				Object timestamp = new java.sql.Timestamp(cal.getTime().getTime());
				//URL unique = new URL("http://ictstudies2-dev.ict.usc.edu/IAGO/login?reset=" + ServletUtils.getSessionId(request));
				String url = "http://ictstudies2-dev.ict.usc.edu/IAGO/login?reset=";
				String unique = request.getSession().getId();
				System.out.println("URL encoding: " + unique);
				//System.out.println("URL database encoding: " + unique.toString().substring(unique.toString().indexOf("reset=") + 6));
				DatabaseUtils.update(query2, unique, timestamp, row.getString("usr"));//both the SQL classes AND JavaMail automatically encode colons.  Unfortunately.
				ServletUtils.sendMail(row.getString("email"), "Whoops! Forgot your password?", "If you didn't request a password change, ignore this message.\n" + 
						"If you'd like to reset your password on IAGO, please click the unique link to be redirected to a secure webform: " + url + unique);		
				System.out.println("Sent mail to " +row.getString(1));
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		catch(MessagingException e)
		{
			e.printStackTrace();
		}
		System.out.println("Queried for forgot password");
		request.getSession().setAttribute("error_forgot", err);
		if (err == null)
			request.getSession().setAttribute("success_forgot", "Success!");
		else
			request.getSession().setAttribute("success_forgot", null);
	}
	
	/***
	 * Handles the logic for validating and processing user input when logging in.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void postLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// Checking whether the Login form has been submitted
		String err = null;

		if(request.getParameter("username") == null || request.getParameter("password") == null)
			err = "All the fields must be filled in!";

		if(err == null)
		{
			//safe querying				
			String query = "SELECT id,usr FROM users WHERE usr=? AND pass=?";

			String hexedpass = shaEncode(request.getParameter("password"));
			ResultSet row = DatabaseUtils.query(query, request.getParameter("username"), hexedpass);

			try {
				if(row.next())
				{
					// If everything is OK login

					user = row.getString("usr");
					id = row.getInt("id");

					response.addCookie(new Cookie("remember", request.getParameter("rememberMe").equals(1) ? user : null));
				}
				else 
					err ="Wrong username and/or password!";
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		request.getSession().setAttribute("error_login", err);
		if (err == null)
			request.getSession().setAttribute("success_login", "Success!");
		else
			request.getSession().setAttribute("success_login", null);
	}
	
	/***
	 * Handles the logic for validating and processing user input when registering for a new account.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void postRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String err = null;
		
		//validation
		Pattern p = Pattern.compile("[\\w\\p{Punct}]+");
		if(request.getParameter("username") == "" || request.getParameter("password") == "" || request.getParameter("passwordconf") == "" || request.getParameter("email") == "")
			err = "All the fields must be filled in!";		
		else if(request.getParameter("username").length() < 4 || request.getParameter("username").length() > 32)
		{
			err = "Your username must be between 3 and 32 characters!";
		}	
		else if(request.getParameter("password").length() < 4 || request.getParameter("password").length() > 32)
		{
			err = "Your password must be between 3 and 32 characters!";
		}
		else if(!request.getParameter("password").equals(request.getParameter("passwordconf")))
		{
			err = "Your passwords do not match!";
		}
		else if(!(p.matcher(request.getParameter("username")).matches()))
		{
			err = "Your username contains invalid characters!";
		}
		else if(!(p.matcher(request.getParameter("password")).matches()))
		{
			err = "Your password contains invalid characters!";
		}
		
		if(err == null)
		{
			// If there are no errors
			
			String hexedpass = shaEncode(request.getParameter("password"));
			// encode their password so we don't have access to it like sneaky sneaks
			
			
			//do they exist?
			String query = "SELECT id,usr FROM users WHERE usr=?";
			ResultSet row = DatabaseUtils.query(query, request.getParameter("username"));
			
			try {
				if(row.next())
				{
					err ="This username is already taken!";
				}
				else
				{
					
					//can maybe do this without the generic command from DatabaseUtils
					String query2 = "INSERT INTO users (usr,pass,email,regIP,dt) VALUES(?, ?, ?, ?, ?)";
					
					String ipAddress = request.getHeader("X-FORWARDED-FOR");
					if (ipAddress == null) {
					    ipAddress = request.getRemoteAddr();
					}
					
					java.util.Date date = new Date();
					Object timestamp = new java.sql.Timestamp(date.getTime());
					DatabaseUtils.update(query2, request.getParameter("username"), hexedpass, request.getParameter("email"), ipAddress, timestamp);		
				}
			} catch (SQLException e) {
				e.printStackTrace();
				err = "SQL Exception, please try again.";
			}
		}
		request.getSession().setAttribute("error_register", err);
		if (err == null)
			request.getSession().setAttribute("success_register", "Success!");
		else
			request.getSession().setAttribute("success_register", null);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		if(request.getParameter("submit").equals("Reset"))
		{
			postReset(request, response);
		}
		else if (request.getParameter("submit").equals("Forgot"))
		{
			postForgot(request, response);
		}
		else if(request.getParameter("submit").equals("Login"))
		{
			postLogin(request, response);
		}
		else if(request.getParameter("submit").equals("Register"))
		{
			postRegister(request, response);
		}
		System.out.println("Post User: " + user + "\nID: " + id);
		request.getSession().setAttribute("user", user);
	    request.getSession().setAttribute("id", id);
		request.getRequestDispatcher("login.jsp").forward(request, response);
	}
	
}
