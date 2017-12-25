package cz.gattserver.grass3.pg.ui.pages;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;

import cz.gattserver.web.common.ui.window.WebWindow;

public class ImageSlideshowWindow extends WebWindow {

	private static final long serialVersionUID = 4928404864735034779L;

	private int currentIndex;
	private List<Resource> list;
	private Embedded embedded;
	private Path[] miniatures;

	public ImageSlideshowWindow(Path[] miniatures, int index, Path slideshowDir) {
		super((index + 1) + "/" + miniatures.length + " " + miniatures[index].getFileName().toString());
		this.currentIndex = index;
		this.miniatures = miniatures;

		setResizable(false);
		center();

		addStyleName("grass-image-slideshow-window");

		list = new ArrayList<>();

		for (Path mini : miniatures) {

			Path image = slideshowDir.resolve(mini.getFileName().toString());
			if (!Files.exists(image))
				image = slideshowDir.getParent().resolve(mini.getFileName().toString());

			Resource resource = new FileResource(image.toFile());
			list.add(resource);
		}

		HorizontalLayout slideShow = new HorizontalLayout();
		slideShow.setSizeFull();
		layout.addComponent(slideShow);

		embedded = new Embedded(null, list.get(index));
		embedded.setSizeUndefined();
		slideShow.addComponent(embedded);
		slideShow.setComponentAlignment(embedded, Alignment.MIDDLE_CENTER);
		slideShow.setExpandRatio(embedded, 1);

		addAction(new ShortcutListener("Prev", KeyCode.ARROW_LEFT, null) {
			private static final long serialVersionUID = -6194478959368277077L;

			@Override
			public void handleAction(Object sender, Object target) {
				if (currentIndex > 0) {
					changeImage(currentIndex - 1);
				}
			}
		});

		addAction(new ShortcutListener("Next", KeyCode.ARROW_RIGHT, null) {
			private static final long serialVersionUID = -6194478959368277077L;

			@Override
			public void handleAction(Object sender, Object target) {
				if (currentIndex < list.size() - 1) {
					changeImage(currentIndex + 1);
				}
			}
		});

	}

	protected void changeImage(int index) {
		currentIndex = index;
		embedded.setSource(list.get(currentIndex));
		setCaption((index + 1) + "/" + miniatures.length + " " + miniatures[currentIndex].getFileName().toString());
		// center();
	}

	@Override
	public void attach() {
		focus();
		super.attach();
	}
}
