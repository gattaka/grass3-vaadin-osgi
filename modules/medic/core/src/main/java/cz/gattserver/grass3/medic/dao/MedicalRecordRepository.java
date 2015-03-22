package cz.gattserver.grass3.medic.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.medic.domain.MedicalRecord;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

	@Query("select m from MedicalRecord m order by m.date desc")
	List<MedicalRecord> findOrderByDateDesc();

}
