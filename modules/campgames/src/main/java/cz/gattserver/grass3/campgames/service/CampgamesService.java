package cz.gattserver.grass3.campgames.service;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.OrderSpecifier;

import cz.gattserver.grass3.campgames.interfaces.CampgameFileTO;
import cz.gattserver.grass3.campgames.interfaces.CampgameFilterTO;
import cz.gattserver.grass3.campgames.interfaces.CampgameOverviewTO;
import cz.gattserver.grass3.campgames.interfaces.CampgameTO;
import cz.gattserver.grass3.campgames.interfaces.CampgameKeywordTO;

public interface CampgamesService {

	/*
	 * Images
	 */

	boolean saveImagesFile(InputStream in, String fileName, CampgameTO item);

	List<CampgameFileTO> getCampgameImagesFiles(Long id);

	long getCampgameImagesFilesCount(Long id);

	Path getCampgameImagesFilePath(Long id, String name);

	InputStream getCampgameImagesFileInputStream(Long id, String name);

	boolean deleteCampgameImagesFile(Long id, String name);

	/*
	 * Item types
	 */

	/**
	 * Uloží nebo aktualizuje typ hw položky
	 * 
	 * @param hwItemTypeTO
	 *            to položky
	 * @return id uložené položky
	 */
	Long saveCampgameKeyword(CampgameKeywordTO hwItemTypeTO);

	Set<CampgameKeywordTO> getAllCampgameKeywords();

	CampgameKeywordTO getCampgameKeyword(Long fixTypeId);

	void deleteCampgameKeyword(Long id);

	/*
	 * Items
	 */

	Long saveCampgame(CampgameTO hwItemDTO);

	int countCampgames(CampgameFilterTO filter);

	List<CampgameOverviewTO> getAllCampgames();

	List<CampgameOverviewTO> getCampgames(CampgameFilterTO filter, Pageable pageable, OrderSpecifier<?>[] order);

	List<CampgameOverviewTO> getCampgameByKeywords(Collection<String> types);

	CampgameTO getCampgame(Long itemId);

	void deleteCampgame(Long id);

}
