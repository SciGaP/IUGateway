/**
 * 
 */
package org.scigap.iucig.model;

import java.io.Serializable;


/**
 * @author Viknes
 *
 */
public class Module implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2281483385281543104L;
	
	private String id;
	private String name;
	private String version;
	private String cluster;
	private String category;
	private String description;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	
}
