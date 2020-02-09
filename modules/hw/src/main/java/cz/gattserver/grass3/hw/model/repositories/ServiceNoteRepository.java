package cz.gattserver.grass3.hw.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.hw.model.domain.ServiceNote;

public interface ServiceNoteRepository extends JpaRepository<ServiceNote, Long> {

}
