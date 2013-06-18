package org.myftp.gattserver.grass3.medic.dao;

import org.myftp.gattserver.grass3.medic.domain.ScheduledVisit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledVisitRepository extends
		JpaRepository<ScheduledVisit, Long> {

}
