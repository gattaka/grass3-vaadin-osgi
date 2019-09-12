package cz.gattserver.grass3.model.repositories;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.QueryResults;

import cz.gattserver.grass3.interfaces.ContentNodeOverviewTO;

public interface ContentNodeRepositoryCustom {

	QueryResults<ContentNodeOverviewTO> findByUserAccess(Long userId, boolean admin, int offset, int limit,
			String sortProperty);

	QueryResults<ContentNodeOverviewTO> findByTagAndUserAccess(Long tagId, Long userId, boolean admin,
			Pageable pageable);

	QueryResults<ContentNodeOverviewTO> findByUserFavouritesAndUserAccess(Long favouritesUserId, Long userId,
			boolean admin, Pageable pageable);

	QueryResults<ContentNodeOverviewTO> findByNodeAndUserAccess(Long nodeId, Long userId, boolean admin,
			Pageable pageable);

	QueryResults<ContentNodeOverviewTO> findByNameAndUserAccess(String name, Long userId, boolean admin,
			Pageable pageable);

	QueryResults<ContentNodeOverviewTO> findByNameAndContentReaderAndUserAccess(String name, String contentReader,
			Long userId, boolean admin, Pageable pageable);

}
