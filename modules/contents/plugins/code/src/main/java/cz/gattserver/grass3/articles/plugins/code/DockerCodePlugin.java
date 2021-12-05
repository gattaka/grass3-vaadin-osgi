package cz.gattserver.grass3.articles.plugins.code;

import org.springframework.stereotype.Component;

@Component
public class DockerCodePlugin extends AbstractCodePlugin {

	public DockerCodePlugin() {
		super("DOCKER", "Docker", "docker.png", "dockerfile", "text/x-dockerfile");
	}

}
