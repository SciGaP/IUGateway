/**
 * 
 */
package org.scigap.iucig.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.scigap.iucig.model.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Viknes
 * 
 */
@Repository
public class ModuleRepository {

	SimpleJdbcTemplate jdbc;

	@Autowired
	public void setTemplate(DataSource dataSource) {
		jdbc = new SimpleJdbcTemplate(dataSource);
	}

	/*public List<Module> getAllModulesSQLite() {
		List<Map<String,Object>> table = jdbc.queryForList("select * from ModulesInfo");
		List<Module> modulesList = getModulesList(table);
        Collections.sort(modulesList, new NameComparator());
		return modulesList;
	}*/
	
	public List<Module> getAllModules() {
		List<Map<String,Object>> table = jdbc.queryForList("select m.name as Name, v.name as Version, v.description as Description, c.name as Cluster "
				+ "from stats.cluster_modules m inner join stats.cluster_module_versions v on m.id = v.cluster_module_id "
				+ "inner join stats.clusters c on m.cluster_id = c.id order by m.name");
		List<Module> modulesList = getModulesList(table);
		return modulesList;
	}
	
	public List<Module> getAllModuleNames() {
		List<Map<String,Object>> table = jdbc.queryForList("select distinct(m.name) as Name from stats.cluster_modules m");
		List<Module> modulesList = getModulesList(table);
		return modulesList;
	}
	
	/*public List<Module> getMasonModules() {
		List<Map<String,Object>> table = jdbc.queryForList("select * from ModulesInfo where Cluster='Mason'");
		List<Module> modulesList = getModulesList(table);
        Collections.sort(modulesList, new NameComparator());
		return modulesList;
	}
	
	public List<Module> getQuarryModules() {
		List<Map<String,Object>> table = jdbc.queryForList("select * from ModulesInfo where Cluster='Quarry'");
		List<Module> modulesList = getModulesList(table);
        Collections.sort(modulesList, new NameComparator());
		return modulesList;
	}

    public List<Module> getBR2Modules() {
        List<Map<String,Object>> table = jdbc.queryForList("select * from ModulesInfo where Cluster='Bigred2'");
        List<Module> modulesList = getModulesList(table);
        Collections.sort(modulesList, new NameComparator());
        return modulesList;
    }
    
    public List<Module> searchModules(String machine, String name, String version, String category, String description) {
    	String query = "select * from ModulesInfo where Cluster like (CASE WHEN '"+machine+"' !='null' THEN '%"+machine+"%' ELSE Cluster END)"
        		+ " AND Name like (CASE WHEN '"+name+"' !='null' THEN '%"+name+"%' ELSE Name END)"
        		+ " AND Version like (CASE WHEN '"+version+"' !='null' THEN '%"+version+"%' ELSE Version END)"
        		+ " AND Category like (CASE WHEN '"+category+"' !='null' THEN '%"+category+"%' ELSE Category END)"
        		+ " AND Description like (CASE WHEN '"+description+"' !='null' THEN '%"+description+"%' ELSE Description END)";
        List<Map<String,Object>> table = jdbc.queryForList(query);
        List<Module> modulesList = getModulesList(table);
        return modulesList;
    }*/
    
    public List<Module> getModuleDetails(String moduleName) {
    	List<Map<String,Object>> table = jdbc.queryForList("select v.name as Version, v.description as Description, c.name as Cluster "
				+ "from stats.cluster_modules m inner join stats.cluster_module_versions v on m.id = v.cluster_module_id "
				+ "inner join stats.clusters c on m.cluster_id = c.id where m.name = ?",moduleName);
		List<Module> modulesList = getModulesList(table);
		return modulesList;
	}
    
	private List<Module> getModulesList(List<Map<String, Object>> table) {
		List<Module> modulesList = new ArrayList<Module>();
		for(Map<String,Object> row : table) {
			Module module = new Module();
			module.setName((String) row.get("Name"));
			module.setVersion((String) row.get("Version"));
			module.setCluster((String) row.get("Cluster"));
			module.setCategory((String) row.get("Category"));
			module.setDescription((String) row.get("Description"));
			modulesList.add(module);
		}
		return modulesList;
	}

}
