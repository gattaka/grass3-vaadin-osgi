package cz.gattserver.grass3.model.dao;

import java.util.List;

import com.querydsl.core.Tuple;

public interface ContentTagRepositoryCustom {

	int countContentTagContents(Long id);

	List<Tuple> countContentTagsContents();

}
