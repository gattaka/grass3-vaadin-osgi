package cz.gattserver.grass3.hw.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.hw.model.domain.HWServiceNote;

public interface HWServiceNoteRepository extends JpaRepository<HWServiceNote, Long> {

}
