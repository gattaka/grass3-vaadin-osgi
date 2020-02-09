package cz.gattserver.grass3.print3d.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass3.print3d.interfaces.Print3dTO;
import cz.gattserver.grass3.print3d.model.domain.Print3d;
import cz.gattserver.grass3.services.CoreMapperService;

@Component
public class Print3dMapperImpl implements Print3dMapper {

	/**
	 * Core mapper
	 */
	@Autowired
	private CoreMapperService mapper;

	/**
	 * Převede {@link Print3d} na {@link Print3dTO}
	 */
	public Print3dTO mapProjectForDetail(Print3d project) {
		if (project == null)
			return null;

		Print3dTO print3dTO = new Print3dTO();
		print3dTO.setId(project.getId());
		print3dTO.setProjectPath(project.getProjectPath());
		print3dTO.setContentNode(mapper.mapContentNodeForDetail(project.getContentNode()));
		return print3dTO;
	}

}
