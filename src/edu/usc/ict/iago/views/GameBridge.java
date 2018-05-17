package edu.usc.ict.iago.views;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;

import edu.usc.ict.iago.utils.*;

/**
 * This the GameBridge, the connector between the web hosting front end of IAGO and the back end.
 * You probably don't want to modify anything here.  Perhaps you should make your changes in config.txt?
 * Note that if you are not developing your own experiment (e.g., you're competing in ANAC), your config.txt
 * will NOT be saved.
 * @author Johnathan Mell
 *
 */
@ServerEndpoint(value = "/game/ws", configurator = GetHttpSessionConfigurator.class)
@WebServlet(name="IagoGame", loadOnStartup=1, description = "This servlet loads the Iago platform.", urlPatterns = { "/game" })
public class GameBridge extends HttpServlet  {

	private static final long serialVersionUID = 2669425387121437625L;
		
	private GameBridgeUtils u;
	private String vhQualifiedName;
	private ArrayList<String> allGameSpecNames; 
	private int currentGame = 0;
	private GeneralVH storedVH;

	public GameBridge() throws Exception 
	{
		
		String email_username = "";
		String email_pass = "";
		String email_sender_name = "";
		String email_smtpAuth = "";
		String email_smtpHost = "";
		String email_smtpPort = "";
		String gameSpecMultiName = "";
		boolean dataMode_log = false;
		boolean dataMode_email = false;
		boolean dataMode_db = false;
		ServletUtils.DebugLevels debug = ServletUtils.DebugLevels.ERROR;
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if(classLoader == null)
		{
			ServletUtils.log("Context class loader is null--resources may be improperly loaded", ServletUtils.DebugLevels.WARN);
			classLoader = ClassLoader.getSystemClassLoader();
			if (classLoader == null)
				ServletUtils.log("System class laoder is null--cannot load properly.", ServletUtils.DebugLevels.ERROR);
		}
		InputStream input = classLoader.getResourceAsStream("config.txt");
		if(input == null)
			ServletUtils.log("Configuration file not found!", ServletUtils.DebugLevels.ERROR);
		Properties properties = new Properties();
		try {
			properties.load(input);
			
			vhQualifiedName = properties.getProperty("agent");
			gameSpecMultiName = properties.getProperty("gamespec");
			email_username = properties.getProperty("email_username");
			email_pass = properties.getProperty("email_pass");
			email_sender_name = properties.getProperty("email_sender_name");
			email_smtpAuth = properties.getProperty("email_smtpAuth");
			email_smtpHost = properties.getProperty("email_smtpHost");
			email_smtpPort = properties.getProperty("email_smtpPort");
			
			dataMode_log = properties.getProperty("dataMode_log").equals("enabled");
			dataMode_email = properties.getProperty("dataMode_email").equals("enabled");
			dataMode_db = properties.getProperty("dataMode_db").equals("enabled");
			
			debug = ServletUtils.DebugLevels.valueOf(properties.getProperty("debugLevel"));
			
		} catch (IOException e) {
		}
		
		ServletUtils.setDebug(debug);
		ServletUtils.setDataModeDb(dataMode_db);
		ServletUtils.setDataModeEmail(dataMode_email);
		ServletUtils.setDataModeLog(dataMode_log);
		ServletUtils.setCredentials(email_username, email_pass, email_sender_name, email_smtpAuth, email_smtpHost, email_smtpPort);
		
		
		if (vhQualifiedName == null || vhQualifiedName.equals(""))
			vhQualifiedName =  "edu.usc.ict.iago.agent.IAGOPinocchioVH";
		if (gameSpecMultiName == null || gameSpecMultiName.equals(""))
			gameSpecMultiName =  "edu.usc.ict.iago.views.ResourceGameSpec";
		
		//account for multiple games
		allGameSpecNames = new ArrayList<String> (Arrays.asList(gameSpecMultiName.split("\\s*,\\s*")));
		ServletUtils.log("We found the following GameSpecs: " + allGameSpecNames.toString(), ServletUtils.DebugLevels.DEBUG);
	
		GameSpec gs = null;
		try {
			@SuppressWarnings("unchecked")
			Class<? extends GameSpec> gsclass = (Class<? extends GameSpec>) Class.forName(allGameSpecNames.get(currentGame));
			Constructor<? extends GameSpec> ctor = gsclass.getDeclaredConstructor(boolean.class);
			ctor.setAccessible(true);
			gs = ctor.newInstance(new Object [] {true});
		} catch (ClassNotFoundException e2) {
			System.err.println("We were unable to load the primary GameSpec file from the class name provided in the configuration file.");
			e2.printStackTrace();
		}
		catch (NoSuchMethodException | SecurityException e1) {
			System.err.println("You have not provided a constructor that meets the requirements! Make sure your extension of GameSpec has"
					+ " a matching set of arguments.");
			e1.printStackTrace();
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		if (gs == null)
			throw new Exception("Unknown problem creating GameSpec class from configuration.");
		
		u = new GameBridgeUtils(gs);
	}
	
	@OnOpen
	public void onOpen(javax.websocket.Session session, EndpointConfig config) 
	{	

		GameSpec gs = null;
		try {
			@SuppressWarnings("unchecked")
			Class<? extends GameSpec> gsclass = (Class<? extends GameSpec>) Class.forName(allGameSpecNames.get(currentGame));
			Constructor<? extends GameSpec> ctor = gsclass.getDeclaredConstructor(boolean.class);
			ctor.setAccessible(true);
			gs = ctor.newInstance(new Object [] {false});
		} catch (ClassNotFoundException e2) {
			System.err.println("We were unable to load the primary GameSpec file from the class name provided in the configuration file.");
			e2.printStackTrace();
		}
		catch (NoSuchMethodException | SecurityException e1) {
			System.err.println("You have not provided a constructor that meets the requirements! Make sure your extension of GameSpec has"
					+ " a matching set of arguments.");
			e1.printStackTrace();
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		if (gs == null)
			try {
				throw new Exception("Unknown problem creating GameSpec class from configuration.");
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		
		try {
			@SuppressWarnings("unchecked")
			Class<? extends GeneralVH> vhclass = (Class<? extends GeneralVH>) Class.forName(vhQualifiedName);
			Constructor<? extends GeneralVH> ctor = vhclass.getConstructor(String.class, GameSpec.class, javax.websocket.Session.class); 
			GeneralVH vh = ctor.newInstance(new Object [] {"defaultAgent", gs, session});
			u.onOpenHelper(session,  config, vh);
			storedVH = vh;
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("We were unable to load the primary VH file from the class name provided in the configuration file.");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.err.println("You have not provided a constructor that meets the requirements!");
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			System.err.println("Your VH did not cast properly.  Did it extend GeneralVH?");
			e.printStackTrace();
		}
		

	}
	
	
	@OnClose
	public void onClose(javax.websocket.Session session, javax.websocket.CloseReason cr) {
		u.onCloseHelper(session, cr);
	}
	
	@OnMessage
	public void onMessage(javax.websocket.Session session, final String msg, boolean last) 
	{
		WebSocketUtils.JsonObject joIn = (WebSocketUtils.JsonObject)AccessController.doPrivileged(new PrivilegedAction<Object>()
		{
			public Object run() {
				return new Gson().fromJson(msg, WebSocketUtils.JsonObject.class);
			}
			
		});
		if(joIn.tag.equals("ngPing"))
		{
			ServletUtils.log("Ping received for new game", ServletUtils.DebugLevels.DEBUG);
			currentGame++;	
			if(currentGame > allGameSpecNames.size() - 1)
			{
				WebSocketUtils.send(new Gson().toJson(new WebSocketUtils(). new JsonObject("trueEnd", "The negotiation has ended.  You will now be redirected.")), session);
				WebSocketUtils.close(session);
			}
			else
			{
				try {
					buildUnprivilegedGameSpec();
					buildPrivilegedGameSpec();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else
			u.onMessageHelper(session, msg, last);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{		
		u.doPostHelper(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{		
		u.doGetHelper(request, response);
	}
	
	private void buildPrivilegedGameSpec() throws Exception
	{
		GameSpec gs = null;
		try {
			@SuppressWarnings("unchecked")
			Class<? extends GameSpec> gsclass = (Class<? extends GameSpec>) Class.forName(allGameSpecNames.get(currentGame));
			Constructor<? extends GameSpec> ctor = gsclass.getDeclaredConstructor(boolean.class);
			ctor.setAccessible(true);
			gs = ctor.newInstance(new Object [] {true});
		} catch (ClassNotFoundException e2) {
			System.err.println("We were unable to load the primary GameSpec file from the class name provided in the configuration file.");
			e2.printStackTrace();
		}
		catch (NoSuchMethodException | SecurityException e1) {
			System.err.println("You have not provided a constructor that meets the requirements! Make sure your extension of GameSpec has"
					+ " a matching set of arguments.");
			e1.printStackTrace();
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		if (gs == null)
			throw new Exception("Unknown problem creating GameSpec class from configuration.");
		
		u.setGameSpec(gs);
	}
	
	private void buildUnprivilegedGameSpec() throws Exception
	{
		GameSpec gs = null;
		try {
			@SuppressWarnings("unchecked")
			Class<? extends GameSpec> gsclass = (Class<? extends GameSpec>) Class.forName(allGameSpecNames.get(currentGame));
			Constructor<? extends GameSpec> ctor = gsclass.getDeclaredConstructor(boolean.class);
			ctor.setAccessible(true);
			gs = ctor.newInstance(new Object [] {true});
		} catch (ClassNotFoundException e2) {
			System.err.println("We were unable to load the primary GameSpec file from the class name provided in the configuration file.");
			e2.printStackTrace();
		}
		catch (NoSuchMethodException | SecurityException e1) {
			System.err.println("You have not provided a constructor that meets the requirements! Make sure your extension of GameSpec has"
					+ " a matching set of arguments.");
			e1.printStackTrace();
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		if (gs == null)
			try {
				throw new Exception("Unknown problem creating GameSpec class from configuration.");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		
		storedVH.setGameSpec(gs);
		
		
	}
}
