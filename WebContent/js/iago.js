////////// FOLLOWING RUNS ASAP \\\\\\\\\\



//globals
var neutralTimeout;
var artChar = "Brad";
var timer = 0;
var timerID;
var angerGlow = false;
var happyGlow = false;
var sadGlow = false;
var surprisedGlow = false;
var neutralGlow = true;
var webSocket;
var maxTime = 0;
var closeSafe = false;
var readyToRestart = false;
var holdingForUser = false;

//called when data comes into the WebSocket
function requestAsyncInfo(incomingEvent, socket)
{
	var event = $.parseJSON(incomingEvent);
	
	if (event.tag == "GAME_END")
	{
		timer = 0;
		neutralGlow = true;
		angerGlow = false;
		heppyGlow = false
		sadGlow = false
		surprisedGlow = false;
		
		$(".issueButton").prop("disabled", true);
		$("#gridOverlay").removeClass("hidden");
		$("#gridOverlay").css("height", $("#exchange-grid").height() * 0.95);//leave some leeway
		$("#gridOverlay").css("width", $("#exchange-grid").width() * 0.95);//leave some leeway
		
		$(".messageHistory").empty();
		
		$("#butFormalAccept").prop("disabled", true);
		$("#butFormalAccept").addClass("hidden");
		
		var newGamePing = new Object();
		newGamePing.tag = "ngPing";
		newGamePing.data = "ngPing";
    	webSocket.send(JSON.stringify(newGamePing));
    	
    	startup();
	}
	if (event.tag == "OFFER_IN_PROGRESS")
	{
		$(".messageHistory").append("<div class='waitingImage'><img class='waitGif' src='img/thinking.gif' alt='Opponent is crafting an offer...'></img></div>");	
		$('.messageHistory').scrollTop($('.messageHistory').prop("scrollHeight"));
		$("#butSendOffer").addClass("hidden");
		$("#butStartOffer").removeClass("hidden");
		$("#butStartOffer").prop("disabled", true);
		$(".itemButton").prop("disabled", true);
		$("#gridOverlay").removeClass("hidden");
		$("#gridOverlay").css("height", $("#exchange-grid").height() * 0.95);//leave some leeway
		$("#gridOverlay").css("width", $("#exchange-grid").width() * 0.95);//leave some leeway
		//$(".overlay").removeClass("hidden");
	}
	else if (event.tag == "SEND_MESSAGE")
	{
		$(".waitingImage").remove();
		//$(".overlay").addClass("hidden");
		//ShowMessage(event.data.message);
		$(".messageHistory").append("<div class='message triangle-isosceles left'>" + event.data.message + "</div>");
		$('.messageHistory').scrollTop($('.messageHistory').prop("scrollHeight"));
		//$(".vhMessageLabel").text("Brad: " + event.data.message);
		
	}
	else if (event.tag == "SEND_OFFER" || event.tag == "objectLocs")//objectLocs is when the user does something, Event occurs when VH does somethign
	{		
		
		$(".waitingImage").remove();
		//$(".overlay").addClass("hidden");
		var formalLegal = true;
		var data = event.tag == "SEND_OFFER" ? event.data.offer.offer : event.data; //objectLocs is the raw array, offer is an Event
		
		$(".itemClass").addClass("hidden"); //remove unused columns
		for (var col = 0; col < data.length; col++)
		{
			$("#col" + col).removeClass("hidden");
			if(data[col][1] != 0)
				formalLegal = false;
			for (var row = 0; row < data[col].length; row++)
				$("#itemLabelCol" + col + "Row" + row).text(data[col][row]);
		}
		
		
		
		var obj = new Object();
		obj.tag = "request-points";
		obj.data = "";
		socket.send(JSON.stringify(obj));
		
		var obj2 = new Object();
		obj2.tag = "request-points-vh";
		obj2.data = "";
		socket.send(JSON.stringify(obj2));
		
		if(event.tag == "SEND_OFFER")
		{
			if(formalLegal)
			{
				$("#butFormalAccept").prop("disabled", false);
				$("#butFormalAccept").removeClass("hidden");
				$("#butFormalAccept").prop("value", "Send Formal Acceptance Proposal");
				$("#butAccept").addClass("hidden");
			}
			else
			{
				$("#butFormalAccept").prop("disabled", true);
				$("#butFormalAccept").addClass("hidden");
				$("#butAccept").removeClass("hidden");
			}
			
			$("#butReject").removeClass("hidden");
			$(".itemButton").fadeOut(200).fadeIn(200).fadeOut(200).fadeIn(200);
			$(".messageHistory").append("<div class='message triangle-isosceles left'>" + event.data.message + "</div>");
			$('.messageHistory').scrollTop($('.messageHistory').prop("scrollHeight"));
			
			$("#butSendOffer").addClass("hidden");
			$("#butStartOffer").removeClass("hidden");
			$("#butStartOffer").prop("disabled", false);
			$("#gridOverlay").removeClass("hidden");
			$("#gridOverlay").css("height", $("#exchange-grid").height() * 0.95);//leave some leeway
			$("#gridOverlay").css("width", $("#exchange-grid").width() * 0.95);//leave some leeway
		}

	}
	else if (event.tag == "SEND_EXPRESSION")
	{
		clearTimeout(neutralTimeout);
		$(".waitingImage").remove();
		//$(".overlay").addClass("hidden");
		$("#vhImg").css("box-shadow", "0 0 30px DarkRed");
		if(event.data.message == "angry")
		{
			$(".messageHistory").append("<div class='message triangle-isosceles left'><img class='butEmotion' src='img/angerface.png' alt=''></img></div>");
			if (artChar == "Brad")
				$("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Angry.png");
			else if (artChar == "Ellie")
				$("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Angry.png");
			else if  (artChar == "Rens")
				$("#vhImg").attr('src', "img/ChrRens/ChrRens_Angry.jpg");
			else if  (artChar == "Laura")
				$("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Angry.jpg");
		}
		if(event.data.message == "happy")
		{
			$(".messageHistory").append("<div class='message triangle-isosceles left'><img class='butEmotion' src='img/happyface.png' alt=''></img></div>");
			if (artChar == "Brad")
				$("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Smile.png");
			else if (artChar == "Ellie")
				$("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Smile.png");
			else if  (artChar == "Rens")
				$("#vhImg").attr('src', "img/ChrRens/ChrRens_Smile.jpg");
			else if  (artChar == "Laura")
				$("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Smile.jpg");
		}
		if(event.data.message == "surprised")
		{
			$(".messageHistory").append("<div class='message triangle-isosceles left'><img class='butEmotion' src='img/shockface.png' alt=''></img></div>");
			if (artChar == "Brad")
				$("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Surprise.png");
			else if (artChar == "Ellie")
				$("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Surprise.png");
			else if  (artChar == "Rens")
				$("#vhImg").attr('src', "img/ChrRens/ChrRens_Suprise.jpg");
			else if  (artChar == "Laura")
				$("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Surprise.jpg");
		}
		if(event.data.message == "disgusted")
		{
			$(".messageHistory").append("<div class='message triangle-isosceles left'><img class='butEmotion' src='img/shockface.png' alt=''></img></div>");
			if (artChar == "Brad")
				$("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Disgust.png");
			else if (artChar == "Ellie")
				$("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Disgust.png");
			else if  (artChar == "Rens")
				$("#vhImg").attr('src', "img/ChrRens/ChrRens_Disgust.jpg");
			else if  (artChar == "Laura")
				$("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Disgust.jpg");
		}
		if(event.data.message == "afraid")
		{
			$(".messageHistory").append("<div class='message triangle-isosceles left'><img class='butEmotion' src='img/shockface.png' alt=''></img></div>");
			if (artChar == "Brad")
				$("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Fear.png");
			else if (artChar == "Ellie")
				$("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Fear.png");
			else if  (artChar == "Rens")
				$("#vhImg").attr('src', "img/ChrRens/ChrRens_Fear.jpg");
			else if  (artChar == "Laura")
				$("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Fear.jpg");
		}
		if(event.data.message == "sad")
		{
			$(".messageHistory").append("<div class='message triangle-isosceles left'><img class='butEmotion' src='img/sadface.png' alt=''></img></div>");
			if (artChar == "Brad")
				$("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Sad.png");
			else if (artChar == "Ellie")
				$("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Sad.png");
			else if  (artChar == "Rens")
				$("#vhImg").attr('src', "img/ChrRens/ChrRens_Sad.jpg");
			else if  (artChar == "Laura")
				$("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Sad.jpg");
		}
		if(event.data.message == "insincereSmile")
		{
			$(".messageHistory").append("<div class='message triangle-isosceles left'><img class='butEmotion' src='img/happyface.png' alt=''></img></div>");
			if (artChar == "Brad")
				$("#vhImg").attr('src', "img/ChrBrad/ChrBrad_SmallSmile.png");
			else if (artChar == "Ellie")
				$("#vhImg").attr('src', "img/ChrEllie/ChrEllie_SmallSmile.png");
			else if  (artChar == "Rens")
				$("#vhImg").attr('src', "img/ChrRens/ChrRens_SmallSmile.jpg");
			else if  (artChar == "Laura")
				$("#vhImg").attr('src', "img/ChrLaura/ChrLaura_SmallSmile.jpg");
		}
		if(event.data.message == "neutral")
		{
			$(".messageHistory").append("<div class='message triangle-isosceles left'><img class='butEmotion' src='img/neutralface.png' alt=''></img></div>");
			if (artChar == "Brad")
				$("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Neutral.png");
			else if (artChar == "Ellie")
				$("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Neutral.png");
			else if  (artChar == "Rens")
				$("#vhImg").attr('src', "img/ChrRens/ChrRens_Neutral.jpg");
			else if  (artChar == "Laura")
				$("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Neutral.jpg");
		}
		neutralTimeout = setTimeout(function(){ 
			if (artChar == "Brad")
				$("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Neutral.png");
			else if (artChar == "Ellie")
				$("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Neutral.png");
			else if  (artChar == "Rens")
				$("#vhImg").attr('src', "img/ChrRens/ChrRens_Neutral.jpg");
			else if  (artChar == "Laura")
				$("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Neutral.jpg");
			$("#vhImg").css("box-shadow", "");
		  
		}, event.data.duration);
		
		$('.messageHistory').scrollTop($('.messageHistory').prop("scrollHeight"));
	}
	else if (event.tag == "FORMAL_ACCEPT")
	{
		$("#butFormalAccept").prop("disabled", false);
		$("#butFormalAccept").removeClass("hidden");
		$("#butFormalAccept").prop("value", "Accept Formally (WILL END GAME)");
		$("#butAccept").addClass("hidden");
	}
	else if (event.tag == "menu")
	{
		printMenu(event.data);
	}
	else if (event.tag == "cursorStatus")
	{
		console.log("Cursor data: " + event.data);
		if(event.data == "default")
		{
			$("body").css("cursor", "default");
			$(".button").css("cursor", "pointer");
		}
		else
			$("body, .button").css("cursor", "url(" + event.data + "),crosshair");

	}
	else if (event.tag == "points")
	{
		total = 0;
		for (i = 0; i < event.data.length; i++)
		{
			$("#labelPoints" + i).text(event.data[i]);
			total += event.data[i];
		}
		$("#labelPointsTotal").text("Total: " + total);
	}
	else if (event.tag == "points-vh")
	{
		total = 0;
		for (i = 0; i < event.data.length; i++)
		{
			$("#labelVHPoints" + i).text(event.data[i]);
			total += event.data[i];
		}
		$("#labelVHPointsTotal").text("Total: " + total);
	}
	else if (event.tag == "history")
	{
		//alert(event.data);
		$("#dialog-message").text(event.data)
		dialog();
	}
	else if (event.tag == "playerPointStruct")
	{
		$("#dialog-message").text(event.data)
		dialog();
	}
	else if (event.tag == "playerPointStructAdv")
	{
		$("#dialog-message").text(event.data)
		dialog();
	}
	else if (event.tag == "compareFirstItem")
	{
		if(event.data == -1)
			$("#butItemFirst").attr('src', "img/white.png");
		else if(event.data == 0)
			$("#butItemFirst").attr('src', "img/item0.png" + "?" + Math.random());
		else if(event.data == 1)
			$("#butItemFirst").attr('src', "img/item1.png" + "?" + Math.random());
		else if(event.data == 2)
			$("#butItemFirst").attr('src', "img/item2.png" + "?" + Math.random());
		else if(event.data == 3)
			$("#butItemFirst").attr('src', "img/item3.png" + "?" + Math.random());
		else if(event.data == 4)
			$("#butItemFirst").attr('src', "img/item4.png" + "?" + Math.random());
		else
			$("#butItemFirst").attr('src', "img/item0.png" + "?" + Math.random());
	}
	else if (event.tag == "compareSecondItem")
	{
		if(event.data == -1)
			$("#butItemSecond").attr('src', "img/white.png");
		else if(event.data == 0)
			$("#butItemSecond").attr('src', "img/item0.png" + "?" + Math.random());
		else if(event.data == 1)
			$("#butItemSecond").attr('src', "img/item1.png" + "?" + Math.random());
		else if(event.data == 2)
			$("#butItemSecond").attr('src', "img/item2.png" + "?" + Math.random());
		else if(event.data == 3)
			$("#butItemSecond").attr('src', "img/item3.png" + "?" + Math.random());
		else if(event.data == 4)
			$("#butItemSecond").attr('src', "img/item4.png" + "?" + Math.random());
		else
			$("#butItemSecond").attr('src', "img/item0.png" + "?" + Math.random());
	}
	else if(event.tag == "chatTextTemp")
	{
		$(".messageLabel").removeClass("hidden");
		$(".messageLabel").text(event.data);
	}
	else if(event.tag == "chatTextFinalized")
	{
		$(".messageHistory").append("<div class='message triangle-isosceles right'>" + event.data + "</div>");
		$('.messageHistory').scrollTop($('.messageHistory').prop("scrollHeight"));
	}
	else if(event.tag == "emoteMessage")
	{
		if(event.data == "neutral")
			$(".messageHistory").append("<div class='message triangle-isosceles right'><img class='butEmotion' src='img/neutralface.png' alt=''></img></div>");
		if(event.data == "angry")
			$(".messageHistory").append("<div class='message triangle-isosceles right'><img class='butEmotion' src='img/angerface.png' alt=''></img></div>");
		if(event.data == "happy")
			$(".messageHistory").append("<div class='message triangle-isosceles right'><img class='butEmotion' src='img/happyface.png' alt=''></img></div>");
		if(event.data == "surprised")
			$(".messageHistory").append("<div class='message triangle-isosceles right'><img class='butEmotion' src='img/shockface.png' alt=''></img></div>");
		if(event.data == "sad")
			$(".messageHistory").append("<div class='message triangle-isosceles right'><img class='butEmotion' src='img/sadface.png' alt=''></img></div>");
		$('.messageHistory').scrollTop($('.messageHistory').prop("scrollHeight"));
	}
	else if(event.tag == "offerFinalized")
	{
		$(".waitingImage").remove();
		$("#dialog-message").html(event.data);		
		dialogWaitForEnd();
	}
	else if(event.tag == "intermediate")
	{
		$("#dialog-message").html(event.data);	
		dialog(); 
	}
	else if(event.tag == "trueEnd")
	{
		$("#dialog-message").html(event.data);		
		dialogRedirect();
		closeSocket();
	}
	else if(event.tag == "negotiationEnd")
	{
		$(".waitingImage").remove();
		$("#dialog-message").html(event.data);
		dialogWaitForEnd();
	}
	else if(event.tag == "negotiationWarn")
	{
		$(".waitingImage").remove();
		$("#dialog-message").text("Only one minute remains in this negotiation!");
		dialog();
	}
	else if(event.tag == "visibility-points-vh")
	{
		if(event.data == true)
			$(".labelVHPoints").removeClass("hidden");//insecure
		else
			$(".labelVHPoints").addClass("hidden");
	}
	else if(event.tag == "agent-description")
	{
		if(event.data != null)
		{
			$("#vhDescription").html(event.data)
			$("#vhDescription").removeClass("hidden");
		}
	}
	else if(event.tag == "visibility-timer")
	{
		if(event.data == true)
			$("#negoTimer").removeClass("hidden");
		else
			$("#negoTimer").addClass("hidden");
	}
	else if(event.tag == "max-time")
	{
		maxTime = event.data;
	}
	else if (event.tag == "cursors-ready")
	{
		$("#butCol0Row0").attr('src', "img/item0.png" + "?" + Math.random()); //force a recache.  Ew, I know.
		$("#butCol0Row1").attr('src', "img/item0.png" + "?" + Math.random());
		$("#butCol0Row2").attr('src', "img/item0.png" + "?" + Math.random());
		
		$("#butCol1Row0").attr('src', "img/item1.png" + "?" + Math.random());
		$("#butCol1Row1").attr('src', "img/item1.png" + "?" + Math.random());
		$("#butCol1Row2").attr('src', "img/item1.png" + "?" + Math.random());
		
		$("#butCol2Row0").attr('src', "img/item2.png" + "?" + Math.random());
		$("#butCol2Row1").attr('src', "img/item2.png" + "?" + Math.random());
		$("#butCol2Row2").attr('src', "img/item2.png" + "?" + Math.random());
		
		$("#butCol3Row0").attr('src', "img/item3.png" + "?" + Math.random());
		$("#butCol3Row1").attr('src', "img/item3.png" + "?" + Math.random());
		$("#butCol3Row2").attr('src', "img/item3.png" + "?" + Math.random());
		
		$("#butCol4Row0").attr('src', "img/item4.png" + "?" + Math.random());
		$("#butCol4Row1").attr('src', "img/item4.png" + "?" + Math.random());
		$("#butCol4Row2").attr('src', "img/item4.png" + "?" + Math.random());
		
		$("#butItem0").attr('src', "img/item0.png" + "?" + Math.random());
		$("#butItem1").attr('src', "img/item1.png" + "?" + Math.random());
		$("#butItem2").attr('src', "img/item2.png" + "?" + Math.random());
		$("#butItem3").attr('src', "img/item3.png" + "?" + Math.random());
		$("#butItem4").attr('src', "img/item4.png" + "?" + Math.random());
		
		$("#butItemFirst").attr('src', "img/white.png");
		$("#butItemSecond").attr('src', "img/white.png");
	}
	else if(event.tag =="config-character-art")
	{
		if(event.data == "Brad")
		{
			artChar = "Brad";
		}
		else if(event.data == "Ellie")
		{
			artChar = "Ellie";
		}
		else if (event.data == "Rens")
		{
			artChar = "Rens";
		}
		else
		{
			artChar = "Laura";
		}
		if (artChar == "Brad")
			$("#vhImg").attr('src', "img/ChrBrad/ChrBrad_Neutral.png");
		else if (artChar == "Ellie")
			$("#vhImg").attr('src', "img/ChrEllie/ChrEllie_Neutral.png");
		else if  (artChar == "Rens")
			$("#vhImg").attr('src', "img/ChrRens/ChrRens_Neutral.jpg");
		else
			$("#vhImg").attr('src', "img/ChrLaura/ChrLaura_Neutral.jpg");
	}
	
			
}

function printMenu(json) {
	
	// First hide the <div>'s elements completely 
	$(".butText").addClass("hidden");
	$(".butContainer").addClass("hidden");
	$(".compareButton").addClass("hidden");
	$(".messageLabel").addClass("hidden");
	$(".buffer").addClass("hidden");
	//$(".messageLabel").text("");
	
	minHeight = 0;
	// Then add every name contained in the list.	
	$.each(json, function(id, text) {
		minHeight += 30;
		$("#" + id).toggleClass("hidden");
		if($("#" + id).hasClass("butText"))
			$("#" + id).val(text);
	});
	
	if ($(".messages").height() < minHeight)
		$("#messageBuffer").css("padding-top", minHeight - $(".messages").height());
}

function dialog(){
	$( "#dialog-message" ).dialog({
	      modal: true,
	      height: 450,
	      width: 600,
	      buttons: {
	        Ok: function() {
	          $( this ).dialog( "close" );
	        }
	      }
	    });
	
}

function dialogWaitForEnd(){
	holdingForUser = true;
	$( "#dialog-message" ).dialog({
	      modal: true,
	      height: 450,
	      width: 600,
	      buttons: {
	        Ok: function() {        
	          readyToRestart = true;
	          holdingForUser = false;
	          $( this ).dialog( "close" );	          
	        }
	      }
	    });
	
}

function dialogRedirect(){
	$( "#dialog-message" ).dialog({
	      modal: true,
	      height: 450,
	      width: 600,
	      buttons: {
	        Ok: function() {
	          window.location.href = "finish";
	        }
	      }
	    });
	
	
}




function openSocket(){
    // Ensures only one connection is open at a time
    if(webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED){
       writeResponse("WebSocket is already opened.");
        return;
    }
    
    var loc = window.location, new_uri;
    if (loc.protocol === "https:") {
        new_uri = "wss:";
    } else {
        new_uri = "ws:";
    }
    new_uri += "//" + loc.host;
    new_uri += loc.pathname + "/ws";
    // Create a new instance of the websocket
    webSocket = new WebSocket(new_uri);
     
    /**
     * Binds functions to the listeners for the websocket.
     */
    webSocket.onopen = function(event){
    	
    	startup()
    };

    webSocket.onmessage = function(event){
    	requestAsyncInfo(event.data, webSocket);
    };

    webSocket.onclose = function(event)
    {
    	clearInterval(timerID);
    	if(closeSafe == false)
    	{
    		$("#dialog-message").text("The connection was terminated early.  Your opponent may have left.")
    		dialog();
    	}
    	console.log('Onclose called' + JSON.stringify(event));
    	console.log('code is' + event.code);
    	console.log('reason is ' + event.reason);
    	console.log('wasClean  is' + event.wasClean);
    	$("input").off("click");
    	$(".tableSlot").off("click");
    };
}


function closeSocket(){
	closeSafe = true;
    webSocket.close();
}

function startup(){
	var debugdata = 'Onopen called' + JSON.stringify(event) + '\ncode is' + event.code + '\nreason is ' + event.reason + '\nwasClean  is' + event.wasClean;
	console.log(debugdata);
	var debug = new Object();
	debug.tag = "debug";
	debug.data = debugdata;
	webSocket.send(JSON.stringify(debug));
	var obj = new Object();
	obj.tag = "button";
	obj.data = "root";
    webSocket.send(JSON.stringify(obj));
    var obj2 = new Object();
	obj2.tag = "button";
	obj2.data = "butCol0Row0";
    webSocket.send(JSON.stringify(obj2));
    var obj3 = new Object();
	obj3.tag = "butCol0Row0";
	obj3.data = "root";
    webSocket.send(JSON.stringify(obj3));
    $(".messageHistory").append("<div class='message triangle-isosceles left'>" + "Hello!" + "</div>");//this forces IE to refresh the formatting
    var obj4 = new Object();
	obj4.tag = "request-visibility";
	obj4.data = "vh-points";
    webSocket.send(JSON.stringify(obj4));
    var obj5 = new Object();
	obj5.tag = "request-max-time";
	obj5.data = "";
    webSocket.send(JSON.stringify(obj5));
    var obj6 = new Object();
	obj6.tag = "request-visibility";
	obj6.data = "timer";
    webSocket.send(JSON.stringify(obj6));
    var obj7 = new Object();
	obj7.tag = "request-agent-description";
	obj7.data = "";
    webSocket.send(JSON.stringify(obj7));
    var obj8 = new Object();
	obj8.tag = "request-agent-art";
	obj8.data = "";
    webSocket.send(JSON.stringify(obj8));
    
}

function buttonHandler(button)
{
	//some of this hiding does not conform to code-hiding standards/encapsulation.  Will be revised to server-side.
	if(button.data == "butStartOffer")
	{
		$(".itemButton").prop("disabled", false);
		$("#butStartOffer").addClass("hidden");
		$("#butSendOffer").removeClass("hidden");
		$("#butAccept").addClass("hidden");
		$("#butReject").addClass("hidden");
		
		$("#gridOverlay").addClass("hidden");
		$("#gridOverlay").css("height", $("#exchange-grid").height() * 0.95);//leave some leeway
		$("#gridOverlay").css("width", $("#exchange-grid").width() * 0.95);//leave some leeway
	}
	
	if(button.data == "butSendOffer")
	{
		$(".itemButton").prop("disabled", true);
		$("#butSendOffer").addClass("hidden");
		$("#butStartOffer").removeClass("hidden");
		
		$("#gridOverlay").removeClass("hidden");
		$("#gridOverlay").css("height", $("#exchange-grid").height() * 0.95);//leave some leeway
		$("#gridOverlay").css("width", $("#exchange-grid").width() * 0.95);//leave some leeway
	}
	
	if(button.data == "butAccept" || button.data == "butReject")
	{
		$("#butAccept").addClass("hidden");
		$("#butReject").addClass("hidden");
		$("#gridOverlay").css("height", $("#exchange-grid").height() * 0.95);//leave some leeway
		$("#gridOverlay").css("width", $("#exchange-grid").width() * 0.95);//leave some leeway
	}
	
	if(button.data == "butItemComparison")
	{
		if($("#butItemComparison").attr('src') == "img/ltsymbol.jpg")
		{
			$("#butItemComparison").attr('src', 'img/gtsymbol.jpg');
			$("#butItemSecond").removeClass("reallyHidden");
			button.data += "GT";
		}
		else if($("#butItemComparison").attr('src') == "img/gtsymbol.jpg")
		{
			$("#butItemComparison").attr('src', 'img/best.png');
			$("#butItemSecond").addClass("reallyHidden");
			button.data += "BEST";
		}
		else if($("#butItemComparison").attr('src') == "img/best.png")
		{
			$("#butItemComparison").attr('src', 'img/least.png');
			$("#butItemSecond").addClass("reallyHidden");
			button.data += "LEAST";
		}
		else if($("#butItemComparison").attr('src') == "img/least.png")
		{
			$("#butItemComparison").attr('src', 'img/eqsymbol.jpg');
			$("#butItemSecond").removeClass("reallyHidden");
			button.data += "EQUAL";
		}
		else if($("#butItemComparison").attr('src') == "img/eqsymbol.jpg")
		{
			$("#butItemComparison").attr('src', 'img/ltsymbol.jpg');
			$("#butItemSecond").removeClass("reallyHidden");
			button.data += "LT";
		}
	}
	
	if(button.data == "butNeutral")
	{
		neutralGlow = true;
    	angerGlow = false;
    	happyGlow = false;
    	sadGlow = false;
    	surprisedGlow = false;
	}
	
	if(button.data == "butAnger")
	{
		neutralGlow = false;
    	angerGlow = true;
    	happyGlow = false;
    	sadGlow = false;
    	surprisedGlow = false;
	}
	
	if(button.data == "butHappy")
	{
		neutralGlow = false;
    	angerGlow = false;
    	happyGlow = true;
    	sadGlow = false;
    	surprisedGlow = false;
	}
	
	if(button.data == "butSad")
	{
		neutralGlow = false;
    	angerGlow = false;
    	happyGlow = false;
    	sadGlow = true;
    	surprisedGlow = false;
	}
	
	if(button.data == "butSurprised")
	{
		neutralGlow = false;
    	angerGlow = false;
    	happyGlow = false;
    	sadGlow = false;
    	surprisedGlow = true;
	}
	
	webSocket.send(JSON.stringify(button));
	
	//this needs to be done immediately, as it is simply waiting for input from the servlet
	if(button.data.indexOf("butExpl") > -1)
	{
		$(".messageHistory").append("<div class='message triangle-isosceles right'>" + $("#" + button.data).val() + "</div>");
		$('.messageHistory').scrollTop($('.messageHistory').prop("scrollHeight"));
	}	
}


////////// FOLLOWING RUNS AFTER ELEMENTS LOADED \\\\\\\\\\\\\\\\\\\\\\\\\\\\
$(document).ready(function() 
{
	openSocket();
	
	var tempID = "root";
	
	
	timerID = setInterval(function(){
		if(holdingForUser)
			return;
		
	    timeRemaining = maxTime - timer;
	    timer++;
	    
	    if(timeRemaining < 0)
	    	timeRemaining = 0;
	    
	    mins = Math.floor(timeRemaining / 60);
	    secs = Math.floor(timeRemaining % 60);
	    trueSecs = secs + "";
	    if(secs < 10)
	    	trueSecs = "0" + secs;
	    
	    	
	    $("#negoTimer").text("Time Remaining: " + mins + ":" + trueSecs);
	    
	    if(readyToRestart)
	    {
	    	var obj = new Object();
	    	obj.tag = "dialogClosed";
	    	obj.data = "";
	    	webSocket.send(JSON.stringify(obj));
	    	readyToRestart = false;
	    }
	    
	  if(timer % 5 == 0){
	    var timeUpdate = new Object();
	    timeUpdate.tag = "time";
	    timeUpdate.data = timer;
    	webSocket.send(JSON.stringify(timeUpdate));
    	
	   }
	  
	  if (timer % 2 == 0){
		  $("#butNeutral").css("box-shadow", "");
		  $("#butAnger").css("box-shadow", "");
		  $("#butHappy").css("box-shadow", "");
		  $("#butSad").css("box-shadow", "");
		  $("#butSurprised").css("box-shadow", "");
		  if(neutralGlow)
		  {
			  $("#butNeutral").css("box-shadow", "0 0 20px DarkRed");
		  }
		  if(angerGlow)
		  {
			  $("#butAnger").css("box-shadow", "0 0 20px DarkRed");
		  }
		  if(happyGlow)
		  {
			  $("#butHappy").css("box-shadow", "0 0 20px DarkRed");
		  }
		  if(sadGlow)
		  {
			  $("#butSad").css("box-shadow", "0 0 20px DarkRed");
		  }
		  if(surprisedGlow)
		  {
			  $("#butSurprised").css("box-shadow", "0 0 20px DarkRed");
		  }
	  }
	  else
	  {
		  $("#butNeutral").css("box-shadow", "");
		  $("#butAnger").css("box-shadow", "");
		  $("#butHappy").css("box-shadow", "");
		  $("#butSad").css("box-shadow", "");
		  $("#butSurprised").css("box-shadow", "");
	  }
	}, 1000);
	
	
	//disable the offer grid to start
	$(".issueButton").prop("disabled", true);
	$("#gridOverlay").removeClass("hidden");
	$("#gridOverlay").css("height", $("#exchange-grid").height() * 0.95);//leave some leeway
	$("#gridOverlay").css("width", $("#exchange-grid").width() * 0.95);//leave some leeway
	
	$("#dialog-message").text("Welcome to IAGO! Prepare to begin!");
	dialog(); 
	

	//speciality event to capture grid buttons with larger area
	$(".tableSlot").click(function(event)
	{
		var button = new Object();
		button.tag = "button";
		if(event.target.id.indexOf("divCol") >= 0)
		{
			button.data = "but" + event.target.id.substring(event.target.id.indexOf("Col"));
			buttonHandler(button);
		}
	});
		
	// Add an event that triggers when ANY button
	// on the page is clicked...
    $("input").click(function(event) 
    {
 
    	
    	//send a message in the socket
    	var button = new Object();
    	button.tag = "button";
    	button.data = event.target.id;
    	
    	buttonHandler(button);
    	
    });  
		
});