package cz.gattserver.grass3.hw.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.hw.domain.ServiceNote;

public interface ServiceNoteRepository extends JpaRepository<ServiceNote, Long> {

}
