package org.myftp.gattserver.grass3.medic.dao;

import java.util.List;

import org.myftp.gattserver.grass3.medic.domain.ScheduledVisit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledVisitRepository extends
		JpaRepository<ScheduledVisit, Long> {

	public List<ScheduledVisit> findByPlanned(boolean planned);

}
