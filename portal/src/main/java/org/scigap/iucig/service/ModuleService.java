/**
 * 
 */
package org.scigap.iucig.service;

import java.util.List;

import org.scigap.iucig.model.Module;
import org.scigap.iucig.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Viknes
 *
 */
@Service(value="moduleService")
public class ModuleService {

	@Autowired
	ModuleRepository repository;
	
	public List<Module> getAllModuleNames() {
		return repository.getAllModuleNames();
	}
	
/*	public List<Module> getQuarryModules() {
		return repository.getQuarryModules();
	}
	
	public List<Module> getMasonModules() {
		return repository.getMasonModules();
	}

    public List<Module> getBR2Modules() {
        return repository.getBR2Modules();
    }
    
    public List<Module> searchModules(String machine, String name, String version, String category, String description) {
        return repository.searchModules(machine,name,version,category,description);
    }*/

	public List<Module> getModuleDetails(String moduleName) {
		return repository.getModuleDetails(moduleName);
	}
	
}
