(function() {
	$(function() {
		$("#butContinue").click(function(e) {
			$('.questions').removeClass("hidden");
			$('html, body').animate({
		        scrollTop: $('.questions').offset().top - 20
		    }, 'slow');
		});
	});
	
	$(function() {
		$("input").click(function(e) {
			if($("input[name=item]:checked").val() == "right" && $("input[name=pref]:checked").val() == "right" && $("input[name=offer]:checked").val() == "right")
			{
				$('.post').removeClass("hidden");
				$('html, body').animate({
			        scrollTop: $('.post').offset().top - 20
			    }, 'slow');
			}
			if($("input[name=item]:checked").val() == "wrong")
				$("#wrong1").removeClass("hidden");
			else
				$("#wrong1").addClass("hidden");
			if($("input[name=pref]:checked").val() == "wrong")
				$("#wrong2").removeClass("hidden");
			else
				$("#wrong2").addClass("hidden");
			if($("input[name=offer]:checked").val() == "wrong")
				$("#wrong3").removeClass("hidden");
			else
				$("#wrong3").addClass("hidden");
		});
	});
}).call(this);