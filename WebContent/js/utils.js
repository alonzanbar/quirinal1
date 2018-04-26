$(document).ready(function() {
	
	var photoMaxHeight = $(".staticPhoto").height();
	var photoMaxWidth = $(".staticPhoto").width();
	
	var photoMaxHeight2 = $(".itemButton").height();
	var photoMaxWidth2 = $(".itemButton").width();
	
	var photoMaxHeight3 = $(".compareButton").height();
	var photoMaxWidth3 = $(".compareButton").width();
	
	var photoMaxHeight4 = $(".butEmotion").height();
	var photoMaxWidth4 = $(".butEmotion").width();
	
	window.onresize = function() {
		height = $(window).height() * 0.90;
		width = $(window).width() * 0.90;
		
		//floor the height and width:
		if (height < 650)
			height = 650;
		if (width < height * 1.5)
			width = height * 1.5;
		
		$("#gameContainer").css("height", height);
		$("#gameContainer").css("width", width);
		
		
		photoHeight = Math.min(photoMaxHeight, .3 * height); //vh height is 30%
		$(".staticPhoto").css("height", photoHeight);
		$(".staticPhoto").css("width", (photoHeight / photoMaxHeight).toPrecision(3) * photoMaxWidth);
		
		photoHeight2 = Math.min(photoMaxHeight2, .05 * height); //item button height is 5%
		$(".itemButton").css("height", photoHeight2);
		$(".itemButton").css("width", (photoHeight2 / photoMaxHeight2).toPrecision(3) * photoMaxWidth2);
		
		photoHeight3 = Math.min(photoMaxHeight3, .05 * height); //compare button height is 5%
		$(".compareButton").css("height", photoHeight3);
		$(".compareButton").css("width", (photoHeight3 / photoMaxHeight3).toPrecision(3) * photoMaxWidth3);
		
		photoHeight4 = Math.min(photoMaxHeight4, .05 * height); //emotion button height is 5%
		$(".butEmotion").css("height", photoHeight4);
		$(".butEmotion").css("width", (photoHeight4 / photoMaxHeight4).toPrecision(3) * photoMaxWidth4);
		
		$("#gridOverlay").css("height", $("#exchange-grid").height() * 0.95);//leave some leeway
		$("#gridOverlay").css("width", $("#exchange-grid").width() * 0.95);//leave some leeway
	}; 
	window.onresize();
	
	window.animateHold = function(jElement, cssTo, cssFrom, holdtime, animtime) {
		jElement.animate(cssTo, animtime, 'swing', function() {
			setTimeout(function() {
				jElement.animate(cssFrom, animtime);
			}, holdtime);
		});
	};
	
	window.identifyBrowser = function() {
        var regexps = {
                'Chrome': [ /Chrome\/(\S+)/ ],
                'Firefox': [ /Firefox\/(\S+)/ ],
                'Internet Explorer': [
                  	/MSIE (\S+);/,                  /*IE 10 and older */
                  	/Trident.*rv[ :]?(\S+)\)/       /*IE 11 */
              	],
                'Opera': [
                    /Opera\/.*?Version\/(\S+)/,     /* Opera 10 */
                    /Opera\/(\S+)/                  /* Opera 9 and older */
                ],
                'Safari': [ /Version\/(\S+).*?Safari\// ]
            },
            re, m, version;
     
        var elements = 1;
        var userAgent = navigator.userAgent;
        
        for (var browser in regexps)
            while (re = regexps[browser].shift())
                if (m = userAgent.match(re)) {
                    version = (m[1].match(new RegExp('[^.]+(?:\.[^.]+){0,' + --elements + '}')))[0];
                    return browser + ' ' + version;
                }
     
        return "UNKNOWN";
    };
	
	$('.currentBrowser').text(identifyBrowser());
	
	$('#username').on('input', function() {
		var j = $(this);
		j.val(j.val().replace(/ /g, ''));
	});
	
	
	$('body').keydown(function(e) {
		if(e.keyCode == 123)
			e.preventDefault();
		});
});

