package org.myftp.gattserver.grass3.medic.dao;

import org.myftp.gattserver.grass3.medic.domain.Physician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhysicianRepository extends JpaRepository<Physician, Long> {

}
