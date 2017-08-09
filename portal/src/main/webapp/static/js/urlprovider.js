angular.module("urlprovider",[]).
    factory("UrlProvider", function() {
	
	function details() {
	    bigred2 = {name:"Big Red II", id:"bigred2", url:"/bigred2"};
	    mason = {name:"Mason", id:"mason", url:"/mason"};
	    quarry = {name:"Quarry", id:"quarry", url:"/quarry"};
	    karst = {name:"Karst", id:"karst", url:"/karst"};
	    carbonate = {name:"Carbonate", id:"carbonate", url:"/carbonate"};
	    module = {name:"Module", id:"module", url:"name"};
	    CIBFeedUrl = "http://internal.pti.iu.edu/feeds/news/all/rss.xml";
	    noticesFeedUrl = "http://itnotices.iu.edu/rss.aspx";
	    feedLoaderUrl = "//ajax.googleapis.com/ajax/services/feed/load?v=1.0&num=10&callback=JSON_CALLBACK&q=";
	    
	    this.getUrlForMachine = function(machine) {
		if(machine.toUpperCase() == bigred2.name.toUpperCase() || 
		   machine.toUpperCase() ==  bigred2.id.toUpperCase()) {
		    return bigred2.url;
		} else if(machine.toUpperCase() == mason.name.toUpperCase() || 
			  machine.toUpperCase() == mason.id.toUpperCase()) {
		    return mason.url;
		} else if(machine.toUpperCase() == quarry.name.toUpperCase() || 
			  machine.toUpperCase() == quarry.id.toUpperCase()) {
		    return quarry.url;
		}else if(machine.toUpperCase() == karst.name.toUpperCase() ||
			 machine.toUpperCase() == karst.id.toUpperCase()) {
		    return karst.url;
		}else if(machine.toUpperCase() == carbonate.name.toUpperCase() ||
			 machine.toUpperCase() == carbonate.id.toUpperCase()) {
		    return carbonate.url;
		}
		
	    };
	    
	    this.getNameForMachine = function(machine) {
		if(machine.toUpperCase() == bigred2.name.toUpperCase() || 
		   machine.toUpperCase() ==  bigred2.id.toUpperCase()) {
		    return bigred2.name;
		} else if(machine.toUpperCase() == mason.name.toUpperCase() || 
			  machine.toUpperCase() == mason.id.toUpperCase()) {
		    return mason.name;
		} else if(machine.toUpperCase() == quarry.name.toUpperCase() || 
			  machine.toUpperCase() == quarry.id.toUpperCase()) {
		    return quarry.name;
		}else if(machine.toUpperCase() == karst.name.toUpperCase() ||
			 machine.toUpperCase() == karst.id.toUpperCase()) {
		    return karst.name;
		}else if(machine.toUpperCase() == carbonate.name.toUpperCase() ||
			 machine.toUpperCase() == carbonate.id.toUpperCase()) {
		    return carbonate.name;
		}
		
	    };
	    
	    this.getFeedLoaderUrl = function() {
		return feedLoaderUrl;
	    };
	    
	    this.getCIBFeedUrl = function() {
		return CIBFeedUrl;
	    };
	    
	    this.getNoticesFeedUrl = function() {
		return noticesFeedUrl;
	    };
            this.getModuleUrl = function() {
		return module.url;
            };
	    
	};
	
	return new details();
    });

