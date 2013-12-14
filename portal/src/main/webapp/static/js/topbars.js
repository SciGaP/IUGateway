$(document).ready(function() {
	
	// Custom Google Search
	(function() {
	    var cx = '001303010279189643848:_pem515tnrk';
	    var gcse = document.createElement('script');
	    gcse.type = 'text/javascript';
	    gcse.async = true;
	    gcse.src = (document.location.protocol == 'https:' ? 'https:' : 'http:') +
	        '//www.google.com/cse/cse.js?cx=' + cx;
	    var s = document.getElementsByTagName('script')[0];
	    s.parentNode.insertBefore(gcse, s);
	})();
});
$("#logoutButton").on("click",function(event) {
	window.location.href = "https://cas.iu.edu/cas/logout";
});
