angular.module("newsApp",["user"]);

$(document).ready(function() {
	/*	// Time out of >10ms is required for the topbar html to be loaded*/
	setTimeout(function() {
		$("#newsLink").parent().addClass("active");
		
		// Affix side bar if window size is greater than 980px
		if($(window).width()>980) {
			$('.bs-docs-sidenav').affix({
		        offset: {
		          top: 0
		        }
			});
		}
		
		// PTI
		$.ajax({
			url: document.location.protocol + "//ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&callback=?&q=" + encodeURIComponent("http://internal.pti.iu.edu/feeds/news/all/rss.xml"),
			dataType: "json",
			async : false,
			success: function(data) {
				var feed = data.responseData.feed.entries;
				var html="";
				if(feed!=null && feed.length!=0) {
					html=displayPTIFeed(feed,"pti");
				} else {
					html="No recent updates from PTI";
				}
				$('#ptiFeed').html(html);
			}
		});
		
		// UITS
		$.ajax({
			url: document.location.protocol + "//ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&callback=?&q=" + encodeURIComponent("http://uitsnews.iu.edu/feed"),
			dataType: "json",
			async : false,
			success: function(data) {
				var feed = data.responseData.feed.entries;
				var html="";
				if(feed!=null && feed.length!=0) {
					html=displayFeed(feed,"uits");
				} else {
					html="No recent updates from UITS";
				}
				$('#uitsFeed').html(html);
			}
		});
		
	},50);
});


function displayFeed(feed,feedname) {
	var html="";
	for(var item in feed) {
		html+=  //Heading
				"<div class='accordion-heading'><a class='accordion-toggle' data-toggle='collapse' href='#collapse"+feedname+item+"'><strong>" + 
				feed[item].title+"</strong></a></div>";
				// Content and external link for more reading
				if(item<3)
					html+="<div id='collapse"+feedname+item+"' class='accordion-body collapse in'><div class='accordion-inner'>"+feed[item].content;
				else
					html+="<div id='collapse"+feedname+item+"' class='accordion-body collapse'><div class='accordion-inner'>"+feed[item].content;
				html+="<a href='"+feed[item].link+"' target='_blank'><strong>...Read More</strong></a></div></div>";
	}
	return html;
}
// Separate method for PTI feeds as it already contains read more link embedded in the feed
function displayPTIFeed(feed,feedname) {
	var html="";
	for(var item in feed) {
		html+=  //Heading
				"<div class='accordion-heading'><a class='accordion-toggle' data-toggle='collapse' href='#collapse"+feedname+item+"'><strong>" + 
				feed[item].title+"</strong></a></div>";
				// Content and external link for more reading
				if(item<3)
					html+="<div id='collapse"+feedname+item+"' class='accordion-body collapse in'><div class='accordion-inner'>"+feed[item].content;
				else
					html+="<div id='collapse"+feedname+item+"' class='accordion-body collapse'><div class='accordion-inner'>"+feed[item].content;
				html+="</div></div>";
	}
	return html;
}