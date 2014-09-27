package cz.gattserver.grass3.medic.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.medic.domain.MedicalRecord;

public interface MedicalRecordRepository extends
		JpaRepository<MedicalRecord, Long> {

}
