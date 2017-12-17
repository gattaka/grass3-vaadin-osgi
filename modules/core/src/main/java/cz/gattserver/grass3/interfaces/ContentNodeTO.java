package cz.gattserver.grass3.interfaces;

import java.util.Set;

public class ContentNodeTO extends ContentNodeOverviewTO {

	/**
	 * Jde o plnohodnotný článek, nebo jde o rozpracovaný obsah?
	 */
	private Boolean draft = false;

	/**
	 * Jde-li o draft upravovaného obsahu, jaké je jeho id
	 */
	private Long draftSourceId;

	/**
	 * Tagy
	 */
	private Set<ContentTagOverviewTO> contentTags;

	public Boolean getDraft() {
		return draft;
	}

	public boolean isDraft() {
		return Boolean.TRUE.equals(draft);
	}

	public void setDraft(Boolean draft) {
		this.draft = draft;
	}

	public Long getDraftSourceId() {
		return draftSourceId;
	}

	public void setDraftSourceId(Long draftSourceId) {
		this.draftSourceId = draftSourceId;
	}

	public Set<ContentTagOverviewTO> getContentTags() {
		return contentTags;
	}

	public void setContentTags(Set<ContentTagOverviewTO> contentTags) {
		this.contentTags = contentTags;
	}

}
