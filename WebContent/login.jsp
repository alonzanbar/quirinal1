<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">

<link rel="stylesheet" type="text/css" href="css/demo.css" media="screen" />
<link rel="stylesheet" type="text/css" href="login_panel/css/slide.css" media="screen" />

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>	    
<script src="login_panel/js/slide.js" type="text/javascript"></script>

<title>IAGO Login</title>
</head>
<body>
<!-- Panel -->
<div id="toppanel">
	<div id="panel">
		<div class="content clearfix">
			<div class="left">
				<h1>IAGO Account Management</h1>
				<h2>A negotiating AI platform</h2>		
			</div>
            
            <% if ((Boolean)session.getAttribute("resetForm") == true) { %>
            
            <div class="left">
			<!-- Reset Password Form -->
			<form class="clearfix" action="" method="post">
				<h1>Reset Password</h1>
				
				<%
					out.println("<script src=\"js/login.js\" type=\"text/javascript\"></script>"); //open the panel regardless
					if(session.getAttribute("error_resetForm") != null)
					{ 
						out.println("<div class=\"err\">" + session.getAttribute("error_resetForm") + "</div>");
					} 
					
					else if(session.getAttribute("success_resetForm") != null)
					{ 
						out.println("<div class=\"success\">" + session.getAttribute("success_resetForm") + "</div>");
					} 
				%>
				
    			<label class="grey" for="email">Email:</label>
				<input class="field" type="text" name="email" id="email" size="23" />
				<label class="grey" for="password">Password:</label>
				<input class="field" type="password" name="password" id="password" size="23" />
				<label class="grey" for="password">Confirm Password:</label>
				<input class="field" type="password" name="passwordconf" id="passwordconf" size="23" />
    			<div class="clear"></div>
				<input type="submit" name="submit" value="Reset" class="bt_login" />
			</form>
			<a href="http://ictstudies2-dev.ict.usc.edu/IAGO/login">Return Home</a>

		</div>
		
		<% } else if ((Boolean)session.getAttribute("forgotForm") == true) { %>
        
        <div class="left">
		<!-- Forgot Password Form -->
		<form class="clearfix" action="" method="post">
			<h1>Reset Password</h1>
			
			<%
				out.println("<script src=\"js/login.js\" type=\"text/javascript\"></script>"); //open the panel regardless
				if(session.getAttribute("error_forgot") != null)
				{ 
					out.println("<div class=\"err\">" + session.getAttribute("error_forgot") + "</div>");
				} 
				
				else if(session.getAttribute("success_forgot") != null)
				{ 
					out.println("<div class=\"success\">" + session.getAttribute("success_forgot") + "</div>");
				} 
			%>
			
			<label class="grey" for="username">Username:</label>
			<input class="field" type="text" name="username" id="username" value="" size="23" />
			<div class="clear"></div>
			<input type="submit" name="submit" value="Forgot" class="bt_login" />
		</form>
		<a href="http://ictstudies2-dev.ict.usc.edu/IAGO/login">Return Home</a>
		<p>We will send you an email to the registered address on file that will allow you to reset your password.</p>
	</div>
            
            <% } else if ((Integer)session.getAttribute("id") == 0) { %>
            
			<div class="left">
				<!-- Login Form -->
				<form class="clearfix" action="" method="post">
					<h1>Member Login</h1>
                    
                    <%
						if(session.getAttribute("error_login") != null)
						{ 
							//out.print(session.getAttribute("err_login"));
							out.println("<div class=\"err\">" + session.getAttribute("error_login") + "</div>");
							out.println("<script src=\"js/login.js\" type=\"text/javascript\"></script>"); //open the panel to display the error 
						} 
					%>
					
					<label class="grey" for="username">Username:</label>
					<input class="field" type="text" name="username" id="username" value="" size="23" />
					<label class="grey" for="password">Password:</label>
					<input class="field" type="password" name="password" id="password" size="23" />
	            	<label><input name="rememberMe" id="rememberMe" type="checkbox" checked="checked" value="1" /> &nbsp;Remember me</label>
        			<div class="clear"></div>
					<input type="submit" name="submit" value="Login" class="bt_login" />
				</form>
				<a href="?forgot">Forgot your password?</a>
			</div>
			<div class="left right">			
				<!-- Register Form -->
				<form action="" method="post">
					<h1>Not a member yet? Sign Up!</h1>		
                    
                    <%
						
	                    if(session.getAttribute("success_register") != null)
						{ 
	                    	//never actually gets shown do to page reload
							//out.print(session.getAttribute("success"));
							out.println("<div class=\"success\">" + session.getAttribute("success_register") + "</div>");
							out.println("<script src=\"js/login.js\" type=\"text/javascript\"></script>"); //open the panel to display the success 
						} 
	                    else if(session.getAttribute("error_register") != null)
						{ 
							//out.print(session.getAttribute("err_reg"));
							out.println("<div class=\"err\">" + session.getAttribute("error_register") + "</div>");
							out.println("<script src=\"js/login.js\" type=\"text/javascript\"></script>"); //open the panel to display the error 
						} 
					%>
                    		
					<label class="grey" for="username">Username:</label>
					<input class="field" type="text" name="username" id="username" value="" size="23" />
					<label class="grey" for="email">Email:</label>
					<input class="field" type="text" name="email" id="email" size="23" />
					<label class="grey" for="password">Password:</label>
					<input class="field" type="password" name="password" id="password" size="23" />
					<label class="grey" for="password">Confirm Password:</label>
					<input class="field" type="password" name="passwordconf" id="passwordconf" size="23" />
					<input type="submit" name="submit" value="Register" class="bt_register" />
				</form>
			</div>
         
            
            
            <% } else if ((Integer)session.getAttribute("id") != 0){ %>
            
            <div class="left">
            
            <h1>Members panel</h1>
            
            <p>Thank you for logging in.  Links to specific games may be listed here.</p>
            <a href="http://ictstudies2-dev.ict.usc.edu/IAGO/game">WebNegoDemo!</a>
            <a href="?logoff">Log off</a>
            
            </div>
            
            <div class="left right">
            </div>
            
            <!-- endif -->
            <% } %>
		</div>
	</div> <!-- /login -->	

    <!-- The tab on top -->	
	<div class="tab">
		<ul class="login">
	    	<li class="left">&nbsp;</li>
	        <li>Hello <% if (session.getAttribute("user") != null) { out.print(session.getAttribute("user")); } else { %> Guest <% } %>!</li>
			<li class="sep">|</li>
			<li id="toggle">
				<a id="open" class="open" href="#"><% if (session.getAttribute("id") != null) { %> 'Open Panel' <% } else { %> 'Log In | Register' <% } %></a>
				<a id="close" style="display: none;" class="close" href="#">Close Panel</a>			
			</li>
	    	<li class="right">&nbsp;</li>
		</ul> 
	</div> <!-- / top -->
	
</div> <!--panel -->

<div class="pageContent">
    <div id="main">
      <div class="container">
        <h1>IAGO Negotiation Platform</h1>
        <h2>Negotiation between Humans and AI</h2>
      </div>
        
      <div class="container">
        
        <p>IAGO is a system designed both to teach humans negotiating skills when expert partners aren't available, and also to help researchers develop more human-like artificial intelligences.
          By taking part in the IAGO project, you will help contribute to this knowledge.</p>
        <p>Participants will be required to view consent forms, as data collected while using this system may be used for research purposes.  We make every effort to anonymize your data so it can't
          be specifically identified to you.</p>
        <p>A fundamental component of this project is the data we collect about the relationship between you and the virtual characters that make up IAGO.  This data may be used to change the behavior
          of the AIs over time, so it's important that you complete all sessions if you are part of a formal research study.  If you're just here to see the system, you'll be able to take part in the demo games.  Have fun!</p>
        <div class="clear"></div>
      </div>
    </div>
</div>

</body>
</html>