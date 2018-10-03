package cz.gattserver.grass3.campgames.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import cz.gattserver.grass3.campgames.interfaces.CampgameOverviewTO;
import cz.gattserver.grass3.campgames.interfaces.CampgameTO;
import cz.gattserver.grass3.campgames.interfaces.CampgameKeywordTO;
import cz.gattserver.grass3.campgames.model.domain.Campgame;
import cz.gattserver.grass3.campgames.model.domain.CampgameKeyword;

public interface CampgamesMapperService {

	public CampgameKeywordTO mapCampgameKeyword(CampgameKeyword e);

	public CampgameKeyword mapCampgameKeyword(CampgameKeywordTO dto);

	public Set<CampgameKeywordTO> mapCampgameKeywords(Collection<CampgameKeyword> list);

	public CampgameTO mapCampgame(Campgame e);

	public CampgameOverviewTO mapCampgameOverview(Campgame e);

	public List<CampgameOverviewTO> mapCampgames(Collection<Campgame> list);

}
