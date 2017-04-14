package cz.gattserver.grass3.medic.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.medic.domain.Physician;

public interface PhysicianRepository extends JpaRepository<Physician, Long> {

}
