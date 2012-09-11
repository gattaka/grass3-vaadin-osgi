package org.myftp.gattserver.grass3.data;

import java.util.ArrayList;
import java.util.List;

public class SectionFacade {

	private static SectionFacade instance;
	private List<Section> sections = new ArrayList<SectionFacade.Section>();

	public static class Section {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public Section(String name) {
			Section.this.name = name;
		}
	} 

	private SectionFacade() {
		sections.add(new Section("Software"));
		sections.add(new Section("Hardware"));
		sections.add(new Section("Zábava"));
	}

	public static SectionFacade getInstance() {
		if (instance == null)
			instance = new SectionFacade();
		return instance;
	}

	public List<Section> getSections() {
		return sections;
	}

}
