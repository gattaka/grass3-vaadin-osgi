package cz.gattserver.grass3.ui.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@Route(value = "test")
@Theme(value = Lumo.class)
public class TestPage extends Div {

	private static final long serialVersionUID = -8238445448784431524L;

	public TestPage() {
		add(createTreeGrid());
	}

	private TreeGrid<Project> createTreeGrid() {
		TreeGrid<Project> treeGrid = new TreeGrid<>();
		final List<Project> generateProjectsForYears = generateProjectsForYears(2010, 2016);
		treeGrid.setItems(generateProjectsForYears, Project::getSubProjects);

		treeGrid.addHierarchyColumn(Project::getName).setHeader("Project Name").setId("name-column");

		treeGrid.expand(generateProjectsForYears.get(1)); // works!

		return treeGrid;
	}

	private List<Project> generateProjectsForYears(int startYear, int endYear) {
		List<Project> projects = new ArrayList<>();

		for (int year = startYear; year <= endYear; year++) {
			Project yearProject = new Project("Year " + year);

			Random random = new Random();

			for (int i = 1; i < 2 + random.nextInt(5); i++) {
				Project customerProject = new Project("Customer Project " + i);
				customerProject.setSubProjects(Arrays.asList(new Project("Implementation")));
				yearProject.addSubProject(customerProject);
			}
			projects.add(yearProject);
		}
		return projects;
	}

	private class Project {

		private List<Project> subProjects = new ArrayList<>();
		private String name;

		public Project(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public List<Project> getSubProjects() {
			return subProjects;
		}

		public void setSubProjects(List<Project> subProjects) {
			this.subProjects = subProjects;
		}

		public void addSubProject(Project subProject) {
			subProjects.add(subProject);
		}

	}

}
