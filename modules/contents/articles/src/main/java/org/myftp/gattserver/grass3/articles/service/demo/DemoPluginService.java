package org.myftp.gattserver.grass3.articles.service.demo;

import org.myftp.gattserver.grass3.articles.editor.api.EditorButtonResources;
import org.myftp.gattserver.grass3.articles.lexer.Token;
import org.myftp.gattserver.grass3.articles.parser.PluginBag;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractElementTree;
import org.myftp.gattserver.grass3.articles.parser.interfaces.AbstractParserPlugin;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IContext;
import org.myftp.gattserver.grass3.articles.parser.interfaces.IPluginFactory;
import org.myftp.gattserver.grass3.articles.service.IPluginService;

public class DemoPluginService implements IPluginService {

	public IPluginFactory getPluginFactory() {
		return new IPluginFactory() {

			public String getTag() {
				return "DEMO";
			}

			public AbstractParserPlugin getPluginParser() {
				return new AbstractParserPlugin() {

					@Override
					public AbstractElementTree parse(PluginBag pluginBag) {

						while (pluginBag.getToken() != Token.END_TAG
								&& pluginBag.getToken() != Token.EOF) {
							pluginBag.nextToken();
						}
						pluginBag.nextToken();
						
						return new AbstractElementTree() {

							@Override
							protected void generateElement(IContext ctx) {
								ctx.print("DEMO");
							}
						};
					}

					@Override
					public boolean canHoldBreakline() {
						return false;
					}
				};
			}
		};
	}

	public EditorButtonResources getEditorButtonResources() {
		return new EditorButtonResources("DEMO");
	}

}
