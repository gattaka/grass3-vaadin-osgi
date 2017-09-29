package cz.gattserver.grass3.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass3.model.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public List<User> findByName(String name);

	public List<User> findByNameAndPassword(String name, String passwordHash);

	public User findByIdAndFavouritesId(Long userId, Long contentNodeId);

	public List<User> findByFavouritesId(Long contentNodeId);

}
