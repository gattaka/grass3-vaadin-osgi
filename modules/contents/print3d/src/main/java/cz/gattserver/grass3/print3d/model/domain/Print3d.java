package cz.gattserver.grass3.print3d.model.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

import cz.gattserver.grass3.model.domain.ContentNode;

@Entity(name = "PRINT3D")
public class Print3d {

	/**
	 * Meta-informace o obsahu
	 */
	@OneToOne
	private ContentNode contentNode;

	/**
	 * Relativní cesta (od kořene 3d projektů) k adresáři s 3d projektem
	 */
	private String projectPath;

	/**
	 * DB identifikátor
	 */
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ContentNode getContentNode() {
		return contentNode;
	}

	public void setContentNode(ContentNode contentNode) {
		this.contentNode = contentNode;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

}
