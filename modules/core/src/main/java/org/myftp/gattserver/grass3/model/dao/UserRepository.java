package org.myftp.gattserver.grass3.model.dao;

import java.util.List;

import org.myftp.gattserver.grass3.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	public List<User> findByName(String name);

	public List<User> findByNameAndPass(String name, String passwordHash);

	public User findByIdAndFavouritesId(Long userId, Long contentNodeId);

	public List<User> findByFavouritesId(Long contentNodeId);

}
