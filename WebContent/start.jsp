<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link href="css/iago.css" rel="stylesheet">

<script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
<script src="https://code.jquery.com/ui/1.11.1/jquery-ui.min.js"></script>
<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.1/themes/smoothness/jquery-ui.css" />

<script src="js/start.js" type="text/javascript"></script>

<title>ICT Experiment - Start</title>
</head>
<body>
	<div class="welcome instructions">
		Welcome to the AuctionWars demo!  In this game, based on the TV show, you'll be negotiating over the contents of an abandoned storage locker.  You and your partner have to decide how to divide up a number of valuable items.  These items are worth different points based on how much you think you can sell them for!
	</div>
	<div class="reminder instructions">
		<p/>
		Please read the instructions in front of you. <strong>Pay special attention to the bold sections!</strong>
		<p>
		Press the continue button when you're finished reading.<br><br>
		<p>
	</div>
	<div class="reminder instructions">
		<div>
			You'll be playing a negotiation game with an computer AI (artificial intelligence).  Your goal is to get the MOST points.<br>
			Each point you have at the end of the game will give you one entry into a lottery on MTurk for one of several $10 bonus prizes.  <strong>So, the more points you have, the more likely you are to win!</strong><br>
			<br>
			You will have <strong>5 minutes</strong> to come to an agreement.  You will be warned when you have 60 seconds left.  If you and your partner don't agree by the end, you will receive 30 points.  Your opponent may also get some amount of points.<br>
			If you DO agree, you'll receive whatever points were allocated on your side when you agreed.<br>
			<br>
			The game consists of 3 issues: crates of records, some antique lamps, and an Art Deco painting.  You are trying to negotiate which items you will receive, and which your partner will. <br>
			<br>
			You get <strong>10 points for each crate of records, 30 points for each of the lamps, and no points at all for the painting</strong>.  This means that the <strong>lamps are worth the most to you!  Make sure to try and get as many as you can!</strong>  Your opponent may want the same items you do, <strong>or they may not</strong>.  Talking to your partner can help reveal what items they may want.<br>
			<br>
			In the game, you can send messages to your opponent and ask him questions. You can also move items around on the game board, and send offers. Everything you do will appear in the chat log on the right side of the screen so you can look it over.<br>
		</div>
		<div>
			<img class="instruction-pic" id="instr_whole" alt="Picture of the game board" src="img/instr_whole.PNG" width="590" height="426" />
		</div>
		<div>
			Here is a picture of the game board.  You can see the chat log on the right, and a picture of your partner on the left. In the bottom half, there is the trade table and buttons.<br><br>
		</div>
		<div>
			<img class="instruction-pic" id="instr_table" alt="Picture of the table" src="img/instr_table.PNG" width="543" height="347" />
		</div>
			Here is the trade table.  It is how you <strong>send offers to your partner</strong>.  It will start greyed out.  Click "Start Offer" to enable it.  You can click any item to pick it up, then click again to place it.<br>
			For example, you can click one of the bananas in the middle and then click it to your side.  You can click multiple times to pick up more than one item.  Nothing is sent until you click "Send Offer".<br><br>
			You can also accept or reject offers that your partner sends you, but these aren't binding.<br>
			Pressing "Formal Accept" is only possible if ALL items are either on your side or your partner's (nothing in the middle).  If you both agree, the game is finished!<br><br>
		<div>
			<img class="instruction-pic" id="instr_emo" alt="Picture of the emotion buttons" src="img/instr_emo.PNG" width="316" height="90" />
		</div>
		<div>
			Click these buttons to send emoticons in chat!<br><br>
		</div>
		<div>
			<img class="instruction-pic" id="instr_relation" alt="Picture of the relation options" src="img/instr_relation.PNG" width="560" height="276" />
		</div>
		<div>
			Clicking either of the first two buttons on the right side will let you <strong>express your preferences for items</strong> by talking to your partner about which items you each like best.  Just click the item you want to talk about once,
			then click again in one of the boxes.  Here, you can see that you're about to say that you like "iron" "less than" "gold".  You can also click the "less than" symbol to turn it into different
			options, like "equal" or "best".<br><br>
			
		</div>
		<div>
			<strong>IMPORTANT: The ONLY way to finish the game is to press "Formal Accept" and have your partner also press it, or for time to run out.  Pressing "Accept (non-binding)" will not work.</strong><br><br>
			<strong>NOTE: If your screen has a low resolution or you're playing this game on Safari for Mac, some lines may not display correctly.  We recommend using a different browser or adjusting your screen resolution.</strong><br><br><br>
			
		</div>
	</div>
	<input class="nomargin" type="button" id="butContinue" value="Continue" />
	<div class="questions hidden instructions">
		<br><br>Now for a few questions!<br><br>
		What item is worth the most to you?
		<form action="">
			<input id="ans1" type="radio" name="item" value="wrong"> records<br>
			<input type="radio" name="item" value="right"> lamps<br>
			<input type="radio" name="item" value="wrong"> painting<br>
		</form>
		<div class="hidden wrong" id="wrong1"><em>Oops!  Scroll back up and check the bold sections!</em></div>
		<br><br>How do you express your preferences for items?
		<form action="">
			<input type="radio" name="pref" value="wrong"> Clicking the image.<br>
			<input id="ans2" type="radio" name="pref" value="right"> Using the buttons on the right side of the screen, and by clicking items and boxes.<br>
			<input type="radio" name="pref" value="wrong"> Using the table in the bottom left.<br>
			<input type="radio" name="pref" value="wrong"> You can't, it's impossible.
		</form>
		<div class="hidden wrong" id="wrong2"><em>Oops!  Scroll back up and check the bold sections!</em></div>
		<br><br>How do you send an offer to your partner?
		<form action="">
			<input type="radio" name="offer" value="wrong"> Click the table while it's grey, then wait.<br>
			<input type="radio" name="offer" value="wrong"> You can't, it's impossible.<br>
			<input type="radio" name="offer" value="wrong"> Drag the items onto you partner's picture.<br>
			<input id="ans3" type="radio" name="offer" value="right"> Click "Start Offer", then click the boxes in the table to move, then click "Send Offer".
		</form>
		<div class="hidden wrong" id="wrong3"><em>Oops!  Scroll back up and check the bold sections!</em></div>
	</div>
	<div class="post instructions hidden">
			<br><br><strong>Good job!  Now, just enter your MTurkID (all caps, no additional spaces!) and press "Start!" to begin.  These instructions will stay open if you need reference.<br><br><br></strong>
			<form target="_blank" action="game" method="POST">
				MTurkID: <input type="text" name="id" required>
				<input type="submit" value="Start!" />
			</form>
	</div>
	
</body>
</html>