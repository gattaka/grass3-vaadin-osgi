package org.myftp.gattserver.grass3.medic.dao;

import org.myftp.gattserver.grass3.medic.domain.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends
		JpaRepository<MedicalRecord, Long> {

}
