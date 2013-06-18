package org.myftp.gattserver.grass3.medic.dao;

import org.myftp.gattserver.grass3.medic.domain.MedicalInstitution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalInstitutionRepository extends
		JpaRepository<MedicalInstitution, Long> {

}
