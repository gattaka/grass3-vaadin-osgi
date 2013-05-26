package org.myftp.gattserver.jpatest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	public List<User> findByName(String name);

	public List<User> findByNameAndPassword(String name, String passwordHash);

	public User findByIdAndFavouritesId(Long userId, Long contentNodeId);

}
