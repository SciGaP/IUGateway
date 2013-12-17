package org.scigap.iucig.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.scigap.iucig.model.Module;
import org.scigap.iucig.service.ModuleService;
import org.scigap.iucig.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/modules/")
public class ModuleController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	ModuleService moduleService;
	
	private final Logger logger = Logger.getLogger(getClass());
	
	/** Returns the module software available on all systems */
	@ResponseBody
	@RequestMapping(value="/all", method = RequestMethod.GET)
	public List<Module> getAllModuleNames() {
		logger.debug("Getting all modules info");
		return moduleService.getAllModuleNames();
	}
	
	@ResponseBody
	@RequestMapping(value="/{moduleName}", method = RequestMethod.GET)
	public List<Module> getModuleDetails(@PathVariable String moduleName) {
		logger.debug("Getting all modules info");
		return moduleService.getModuleDetails(moduleName);
	}
	
/*	*//** Returns the module software available on Quarry *//*
	@ResponseBody
	@RequestMapping(value="/quarry", method = RequestMethod.GET)
	public List<Module> getQuarryModules() {
		logger.debug("Getting quarry modules info");
		return moduleService.getQuarryModules();
	}
	
	*//** Returns the module software available on Mason *//*
	@ResponseBody
	@RequestMapping(value="/mason", method = RequestMethod.GET)
	public List<Module> getMasonModules() {
		logger.debug("Getting mason modules info");
		return moduleService.getMasonModules();
	}

    *//** Returns the module software available on Mason *//*
    @ResponseBody
    @RequestMapping(value="/bigred2", method = RequestMethod.GET)
    public List<Module> getBR2Modules() {
        logger.debug("Getting BR2 modules info");
        return moduleService.getBR2Modules();
    }
    
    @ResponseBody
    @RequestMapping(value="/search", method = RequestMethod.GET)
    public List<Module> searchModules(@RequestParam(value="machine", required=false) String machine,
    		@RequestParam(value="name", required=false) String name,
    		@RequestParam(value="version", required=false) String version,
    		@RequestParam(value="category", required=false) String category,
    		@RequestParam(value="description", required=false) String description) {
        logger.debug("Searching modules info");
        return moduleService.searchModules(machine,name,version,category,description);
    }*/
	
}
