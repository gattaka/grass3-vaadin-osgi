package cz.gattserver.grass3.articles.code.service.impl;

import org.springframework.stereotype.Component;

import cz.gattserver.grass3.articles.code.AbstractCodePlugin;

@Component
public class RubyCodePlugin extends AbstractCodePlugin {

	public RubyCodePlugin() {
		super("RUBY", "Ruby", "ruby.png", "ruby", "ruby");
	}

}
