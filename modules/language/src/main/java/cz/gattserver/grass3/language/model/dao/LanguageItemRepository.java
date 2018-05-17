package cz.gattserver.grass3.language.model.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.language.model.domain.ItemType;
import cz.gattserver.grass3.language.model.domain.LanguageItem;

public interface LanguageItemRepository extends JpaRepository<LanguageItem, Long>, LanguageItemRepositoryCustom {

	@Query("select i.id from LANGUAGEITEM i where i.language.id = ?1 and i.type = ?2 and i.successRate >= ?3 and i.successRate < ?4 order by content asc")
	List<Long> findIdsByLanguageAndSuccessRateRangeSortByContent(long languageId, ItemType type, double minRate,
			double maxRate);

	@Query("select i.id from LANGUAGEITEM i where i.language.id = ?1 and i.successRate >= ?2 and i.successRate < ?3 order by content asc")
	List<Long> findIdsByLanguageAndSuccessRateRangeSortByContent(long languageId, double minRate, double maxRate);

	@Query("select i from LANGUAGEITEM i where i.id in ?1")
	List<LanguageItem> findByIds(Set<Long> ids);

	@Query("select i from LANGUAGEITEM i where i.language.id = ?1 and i.content = ?2")
	LanguageItem findLanguageItemByContent(long languageId, String content);

	@Modifying
	@Query("update LANGUAGEITEM i set i.tested = ?2, i.successRate = ?3, i.lastTested = ?4 where i.id = ?1")
	void updateItem(Long id, int newCount, double newRate, LocalDateTime now);

}
