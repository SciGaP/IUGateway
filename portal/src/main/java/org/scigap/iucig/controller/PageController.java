package org.scigap.iucig.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.scigap.iucig.gateway.util.ViewNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Viknes
 *
 */

@Controller
public class PageController {
	
	private final Logger logger = Logger.getLogger(getClass());
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String home(HttpServletRequest request) {
		logger.debug("Loading index page");
		return ViewNames.INDEX_PAGE;
	}
	
	@RequestMapping(value = "/loginFailure", method = RequestMethod.GET)
	public String loginFailure() {
		logger.info("Login Failed !");
		return ViewNames.LOGIN_FAILURE_PAGE;
	}

	@RequestMapping(value = "/contactus", method = RequestMethod.GET)
	public String getContactUs() {
		logger.debug("Load contactus page");
		return ViewNames.CONTACT_US_PAGE;
	}
	
	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String getAboutGateway() {
		logger.debug("Load about gateway page");
		return ViewNames.ABOUT_GATEWAY_PAGE;
	}
	
	@RequestMapping(value = "/feedback", method = RequestMethod.GET)
	public String getFeedback() {
		logger.debug("Load feedback page");
		return ViewNames.FEEDBACK_PAGE;
	}
	
	@RequestMapping(value = "/globusOnline", method = RequestMethod.GET)
	public String getGlobusOnline() {
		logger.debug("Load globus online page");
		return ViewNames.GLOBUS_ONLINE_PAGE;
	}
	
	@RequestMapping(value = "/ubmod", method = RequestMethod.GET)
	public String getUBMoD() {
		logger.debug("Load UBMoD page");
		return ViewNames.UBMOD_PAGE;
	}
	
	@RequestMapping(value = "/news", method = RequestMethod.GET)
	public String getNews() {
		logger.debug("Load News page");
		return ViewNames.NEWS_PAGE;
	}
	
	@RequestMapping(value = "/modulesInfo", method = RequestMethod.GET)
	public String getModulesInfo() {
		// Temporarily redirecting to search software page before completely removing it.
		logger.debug("Redirecting to Search software info page page");
		return ViewNames.SEARCH_MODULES_INFO_PAGE;
	}

    @RequestMapping(value = "/scienceDiscipline", method = RequestMethod.GET)
    public String getScienceDisciplineInfo() {
        // Temporarily redirecting to search software page before completely removing it.
        logger.debug("Redirecting to science discipline page");
        return ViewNames.SCIENCE_DISCIPLINE_INFO_PAGE;
    }
	
	@RequestMapping(value = "/searchModulesInfo", method = RequestMethod.GET)
	public String getSoftwareSearchInfo() {
		logger.debug("Load Search Software Info page");
		return ViewNames.SEARCH_MODULES_INFO_PAGE;
	}
	
	@RequestMapping(value = "/charts", method = RequestMethod.GET)
	public String getCharts() {
		logger.debug("Load Charts Info page");
		return ViewNames.CHARTS_PAGE;
	}
	
	@RequestMapping(value = "/help", method = RequestMethod.GET)
	public String getHelp() {
		logger.debug("Load Quarry Info page");
		return ViewNames.HELP_PAGE;
	}
	
	@RequestMapping(value = "/jobs", method = RequestMethod.GET)
	public String getAllJobs() {
		logger.debug("Load jobs page");
		return ViewNames.JOBS_PAGE;
	}
	
	@RequestMapping(value = "/submitJobs", method = RequestMethod.GET)
	public String getSubmitJobs() {
		logger.debug("Load submit jobs page");
		return ViewNames.SUBMIT_JOBS_PAGE;
	}
	
	@RequestMapping(value = "/userJobs", method = RequestMethod.GET)
	public String getUserJobs() {
		logger.debug("Load user jobs page");
		return ViewNames.USER_JOBS_PAGE;
	}

    @RequestMapping(value = "/nodeManagement", method = RequestMethod.GET)
    public String getNodes() {
        logger.debug("Load node management page");
        return ViewNames.NODE_MGT_PAGE;
    }

    @RequestMapping(value = "/reservations", method = RequestMethod.GET)
    public String getReservations() {
        logger.debug("Load node reservations page");
        return ViewNames.NODE_RESERVATIONS_PAGE;
    }
    
    @RequestMapping(value = "/amber", method = RequestMethod.GET)
    public String getAmberApplicationPage() {
    	logger.debug("Load Amber Application page");
    	return ViewNames.AMBER_PAGE;
    }

    @RequestMapping(value = "/sda", method = RequestMethod.GET)
    public String getFileManagerPage() {
        logger.debug("Load IU SDA page");
        return ViewNames.FILE_MANAGER_PAGE;
    }

}
