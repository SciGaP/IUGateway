/**
 * 
 */
package org.scigap.iucig.repository;

import org.scigap.iucig.model.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
							   + "inner join stats.clusters c on m.cluster_id = c.id "
                               + "where (select count(1) from stats.cluster_module_versions where retired is null and cluster_module_id = m.id) > 0 "
                               + "and (hidden is null or hidden = 0) "
							   + "order by m.name");
	List<Module> modulesList = getModulesList(table);
	return modulesList;
    }
    
    public List<Module> getAllModuleNames() {
	List<Map<String,Object>> table = jdbc.queryForList("select distinct(m.name) as Name from stats.cluster_modules m " +
							   "where (select count(1) from stats.cluster_module_versions where retired is null and cluster_module_id = m.id) > 0" +
                               "and (hidden is null or hidden = 0)");

	List<Module> modulesList = getModulesList(table);
	return modulesList;
    }
    
    public List<Module> getMasonModules() {
	List<Map<String,Object>> table = jdbc.queryForList("select distinct(m.name) as Name from " +
							   "stats.cluster_modules m inner join stats.cluster_module_versions v on m.id = v.cluster_module_id " +
							   "inner join stats.clusters c on m.cluster_id = c.id where c.code = 'mason' " +
							   "and (select count(1) from stats.cluster_module_versions where retired is null and cluster_module_id = m.id) > 0" +
                               "and (hidden is null or hidden = 0)");
	List<Module> modulesList = getModulesList(table);
        Collections.sort(modulesList, new NameComparator());
	return modulesList;
    }

    public List<Module> getCarbonateModules() {
	List<Map<String,Object>> table = jdbc.queryForList("select distinct(m.name) as Name from " +
							   "stats.cluster_modules m inner join stats.cluster_module_versions v on m.id = v.cluster_module_id " +
							   "inner join stats.clusters c on m.cluster_id = c.id where c.code = 'carbonate' " + 
							   "and (select count(1) from stats.cluster_module_versions where retired is null and cluster_module_id = m.id) > 0" +
                               "and (hidden is null or hidden = 0)");
	List<Module> modulesList = getModulesList(table);
        Collections.sort(modulesList, new NameComparator());
	return modulesList;
    }
    
    public List<Module> getKarstModules() {
	List<Map<String,Object>> table = jdbc.queryForList("select distinct(m.name) as Name from " +
							   "stats.cluster_modules m inner join stats.cluster_module_versions v on m.id = v.cluster_module_id " +
							   "inner join stats.clusters c on m.cluster_id = c.id where c.code = 'karst' " +
							   "and (select count(1) from stats.cluster_module_versions where retired is null and cluster_module_id = m.id) > 0" +
                               "and (hidden is null or hidden = 0)");
	List<Module> modulesList = getModulesList(table);
        Collections.sort(modulesList, new NameComparator());
	return modulesList;
    }

        public List<Module> getQuarryModules() {
	List<Map<String,Object>> table = jdbc.queryForList("select distinct(m.name) as Name from " +
							   "stats.cluster_modules m inner join stats.cluster_module_versions v on m.id = v.cluster_module_id " +
							   "inner join stats.clusters c on m.cluster_id = c.id where c.code = 'quarry'" +
							   "and (select count(1) from stats.cluster_module_versions where retired is null and cluster_module_id = m.id) > 0" +
                               "and (hidden is null or hidden = 0)");
	List<Module> modulesList = getModulesList(table);
        Collections.sort(modulesList, new NameComparator());
	return modulesList;
    }

    public List<Module> getBR2Modules() {
        List<Map<String,Object>> table = jdbc.queryForList("select distinct(m.name) as Name from " +
							   "stats.cluster_modules m inner join stats.cluster_module_versions v on m.id = v.cluster_module_id " +
							   "inner join stats.clusters c on m.cluster_id = c.id where c.code = 'bigred2' " +
							   "and (select count(1) from stats.cluster_module_versions where retired is null and cluster_module_id = m.id) > 0" +
                               "and (hidden is null or hidden = 0)");
        List<Module> modulesList = getModulesList(table);
        Collections.sort(modulesList, new NameComparator());
        return modulesList;
    }
    
    
    public List<Module> getModuleDetails(String moduleName) {
    	List<Map<String,Object>> table = jdbc.queryForList("select v.name as Version, v.description as Description, c.name as Cluster "
							   + "from stats.cluster_modules m inner join stats.cluster_module_versions v on m.id = v.cluster_module_id "
							   + "inner join stats.clusters c on m.cluster_id = c.id where c.name != 'Quarry' and m.name = ?",moduleName);
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
