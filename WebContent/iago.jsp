<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<link href="css/iago.css" rel="stylesheet">

<script src="https://code.jquery.com/jquery-1.11.1.min.js"></script>
<script src="https://code.jquery.com/ui/1.11.1/jquery-ui.min.js"></script>
<%--<link rel="stylesheet" href="https://code.jquery.com/ui/1.11.1/themes/smoothness/jquery-ui.css" />--%>
<%--<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> --%>
<link rel="stylesheet" href="css/bootstrap_jquery_ui_theme/jquery-ui-1.10.0.custom.css" />

<script src="js/utils.js" type="text/javascript"></script>
<script src="js/iago.js" type="text/javascript"></script>

<title>IAGO</title>
</head>
<body>
	<div class="gameWrapper centered">
		<div class="flex centered" id="gameContainer">
			<div class="vh-side flex-child flex-vert-space-between">
				<div class="flex flex-child-no-grow rounded-box" id="vhContainer">
						<img class="staticPhoto" id="vhImg" alt="Avatar of other player" src="img/unset_vh.png" width="512" height="512" />
					<div class = "flex-child flex-vert" id="descriptionContainer">
						<div class="flex-child hidden" id="negoTimer">
							10:00
						</div>
						<div class="flex-child hidden" id="vhDescription">
							Description unset
						</div>
					</div>
				</div>
				<div class="exchange-grid flex-child flex rounded-box" id="exchange-grid">
					<div class="hidden overlay" id="gridOverlay"></div>
					<div class="gridLabels flex-child flex-vert">
						<div class="tableSlot-noBox flex-table labelVHPoints">
							<div class="labelVHPoints">Opponent Points:</div>
						</div>
						<div class="tableSlot-noBox flex-table">
							<div>Opponent's</div>
						</div>
						<div class="tableSlot-noBox flex-table">
							<div>Undecided</div>
						</div>
						<div class="tableSlot-noBox flex-table">
							<div>Yours</div>
						</div>
						<div class="tableSlot-noBox flex-table">
							<div>Your Points:</div>
						</div>
					</div>
					<div class="itemClass flex-child flex-vert" id="col0">
						<div class="tableSlot-noBox flex-table labelVHPoints">
							<div class="labelVHPoints" id="labelVHPoints0">0</div>
						</div>
						<div class="tableSlot box flex-table" id="divCol0Row0">
							<div class="flex-child" id="itemLabelCol0Row0">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol0Row0" src="img/white.png">
						</div>
						<div class="tableSlot box flex-table" id="divCol0Row1">
							<div class="flex-child" id="itemLabelCol0Row1">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol0Row1" src="img/white.png">
						</div>
						<div class="tableSlot box flex-table" id="divCol0Row2">
							<div class="flex-child" id="itemLabelCol0Row2">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol0Row2" src="img/white.png">
						</div>
						<div class="tableSlot-noBox flex-table">
							<div class="labelPoints" id="labelPoints0">0</div>
						</div>
					</div>
					<div class="itemClass flex-child flex-vert" id="col1">
						<div class="tableSlot-noBox flex-table labelVHPoints">
							<div class="labelVHPoints" id="labelVHPoints1">0</div>
						</div>
						<div class="tableSlot box flex-table" id="divCol1Row0">
							<div class="flex-child" id="itemLabelCol1Row0">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol1Row0" src="img/white.png">
						</div>
						<div class="tableSlot box flex-table" id="divCol1Row1">
							<div class="flex-child" id="itemLabelCol1Row1">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol1Row1" src="img/white.png">
						</div>
						<div class="tableSlot box flex-table" id="divCol1Row2">
							<div class="flex-child" id="itemLabelCol1Row2">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol1Row2" src="img/white.png">
						</div>
						<div class="tableSlot-noBox flex-table">
							<div class="labelPoints" id="labelPoints1">0</div>
						</div>
					</div>
					<div class="itemClass flex-child flex-vert" id="col2">
						<div class="tableSlot-noBox flex-table labelVHPoints">
							<div class="labelVHPoints" id="labelVHPoints2">0</div>
						</div>
						<div class="tableSlot box flex-table" id="divCol2Row0">
							<div class="flex-child" id="itemLabelCol2Row0">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol2Row0" src="img/white.png">
						</div>
						<div class="tableSlot box flex-table" id="divCol2Row1">
							<div class="flex-child" id="itemLabelCol2Row1">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol2Row1" src="img/white.png">
						</div>
						<div class="tableSlot box flex-table" id="divCol2Row2">
							<div class="flex-child" id="itemLabelCol2Row2">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol2Row2" src="img/white.png">
						</div>
						<div class="tableSlot-noBox flex-table">
							<div class="labelPoints" id="labelPoints2">0</div>
						</div>
					</div>
					<div class="itemClass flex-child flex-vert" id="col3">
						<div class="tableSlot-noBox flex-table labelVHPoints">
							<div class="labelVHPoints" id="labelVHPoints3">0</div>
						</div>
						<div class="tableSlot box flex-table" id="divCol3Row0">
							<div class="flex-child" id="itemLabelCol3Row0">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol3Row0" src="img/white.png">
						</div>
						<div class="tableSlot box flex-table" id="divCol3Row1">
							<div class="flex-child" id="itemLabelCol3Row1">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol3Row1" src="img/white.png">
						</div>
						<div class="tableSlot box flex-table" id="divCol3Row2">
							<div class="flex-child" id="itemLabelCol3Row2">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol3Row2" src="img/white.png">
						</div>
						<div class="tableSlot-noBox flex-table">
							<div class="labelPoints" id="labelPoints3">0</div>
						</div>
					</div>
					<div class="itemClass flex-child flex-vert" id="col4">
						<div class="tableSlot-noBox flex-table labelVHPoints">
							<div class="labelVHPoints" id="labelVHPoints4">0</div>
						</div>
						<div class="tableSlot box flex-table" id="divCol4Row0">
							<div class="flex-child" id="itemLabelCol4Row0">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol4Row0" src="img/white.png">
						</div>
						<div class="tableSlot box flex-table" id="divCol4Row1">
							<div class="flex-child" id="itemLabelCol4Row1">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol4Row1" src="img/white.png">
						</div>
						<div class="tableSlot box flex-table" id="divCol4Row2">
							<div class="flex-child" id="itemLabelCol4Row2">foo</div>
							<input class="itemButton flex-child button" type="image" id = "butCol4Row2" src="img/white.png">
						</div>
						<div class="tableSlot-noBox flex-table">
							<div class="labelPoints" id="labelPoints4">0</div>
						</div>
					</div>
					<div class="gridLabels flex-child flex-vert">
						<div class="tableSlot flex-table labelVHPoints">
							<div class="labelVHPoints" id="labelVHPointsTotal">Total: 0</div>
						</div>
						<div class="tableSlot-noBox flex-table">
							<div></div>
						</div>
						<div class="tableSlot-noBox flex-table">
							<div></div>
						</div>
						<div class="tableSlot-noBox flex-table">
							<div></div>
						</div>
						<div class="tableSlot-noBox flex-table">
							<div class="labelPoints" id="labelPointsTotal">Total: 0</div>
						</div>
					</div>
				</div>
				<div class="buttons flex-child flex-vert rounded-box">
					<input class="butPanel flex-child button" type="button" id="butStartOffer" title="Lets you move items on the table" value="Start an offer"></input>
					<input class="butPanel flex-child button hidden" type="button" id="butSendOffer" title="Sends the current offer" value="Send your offer"></input>
					<input class="butPanel flex-child button hidden" type="button" id="butFormalAccept" title="If opponent accepts, game will end" value="Formally accept offer"></input>
					<input class="butPanel flex-child button hidden" type="button" id="butAccept" title="Accepts an offer, but isn't binding." value="Accept offer (non-binding)"></input>
					<input class="butPanel flex-child button hidden" type="button" id="butReject" title="Rejects an offer, but isn't binding." value="Reject offer (non-binding)"></input>
					<input class="butPanel flex-child button" type="button" id="butViewPayoffs" value="View Payoffs"></input>
				</div>
			</div>
			<div class="menu-side flex-child flex-vert rounded-box">
				<div class="messageHistory flex-double-child scrolling">
				</div>
				<div class="flex-child" id="tips">
					Tip: The glowing icon is the one you're currently showing!	
				</div>
				<div class="emotion-grid flex-child-no-grow flex">
					<input class="butEmotion button" type="image" id="butAnger" src="img/angerface.png"></input>
					<input class="butEmotion button" type="image" id="butSad" src="img/sadface.png"></input>
					<input class="butEmotion button" type="image" id="butNeutral" src="img/neutralface.png"></input>
					<input class="butEmotion button" type="image" id="butSurprised" src="img/shockface.png"></input>
					<input class="butEmotion button" type="image" id="butHappy" src="img/happyface.png"></input>
				</div>
				<div class="messages flex-child flex-vert scrolling" id="messages">
					<div class = "buffer flex-child" id="messageBuffer"></div>
					<div class = "messageLabel flex-child" id = "craftingMessageString">Move the items into the boxes to describe what you want to say!</div>
					<input class="butText flex-child button" type="button" id="butYouLike" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butILike" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butNoBut" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butCustom1" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butCustom2" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butCustom3" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butCustom1_1" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butCustom2_1" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butCustom3_1" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butCustom1_2" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butCustom2_2" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butCustom3_2" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butCustom1_3" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butCustom2_3" value="foo" ></input>
					<input class="butText flex-child button" type="button" id="butCustom3_3" value="foo" ></input>
					
					<div class="butContainer flex-child hidden" id="butItemsDiv">
						<input class="compareButton flex-child button" type="image" id = "butItem0" src="img/white.png">
						<input class="compareButton flex-child button" type="image" id = "butItem1" src="img/white.png">
						<input class="compareButton flex-child button" type="image" id = "butItem2" src="img/white.png">
						<input class="compareButton flex-child button" type="image" id = "butItem3" src="img/white.png">
						<input class="compareButton flex-child button" type="image" id = "butItem4" src="img/white.png">
					</div>
					<div class="butContainer flex-child hidden" id="butItemsComparison">
						<input class="compareButton flex-child button box" type="image" id = "butItemFirst" src="img/white.png">
						<input class="compareButton flex-child button box" type="image" id = "butItemComparison" src="img/gtsymbol.jpg" alt="best?">
						<input class="compareButton flex-child button box" type="image" id = "butItemSecond" src="img/white.png">
					</div>
					<%--<div class="butContainer flex-child flex-vert scrolling" id="butExplDiv"> --%>
						<input class="butText flex-child button hidden" type="button" id="butExpl0" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl1" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl2" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl3" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl4" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl5" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl6" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl7" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl8" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl9" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl10" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl11" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl12" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl13" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl14" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl15" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl16" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl17" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl18" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl19" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butExpl20" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butConfirm" value="foo" ></input>
						<input class="butText flex-child button hidden" type="button" id="butBack" value="foo" ></input>
					<%--</div> --%>
				</div>

			</div>
		</div>
	</div>
	<div id="debug"></div>
	<div id="dialog-message" title="Game Message">
  		This is the default dialog which is useful for displaying information. The dialog window can be moved, resized and closed with the 'x' icon.
	</div>
</body>
</html>