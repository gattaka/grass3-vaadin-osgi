package cz.gattserver.grass3.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.model.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public User findByName(String name);

	public User findByNameAndPassword(String name, String passwordHash);

	public User findByIdAndFavouritesId(Long userId, Long contentNodeId);

	public List<User> findByFavouritesId(Long contentNodeId);

	@Modifying
	@Query("update USER_ACCOUNTS u set u.confirmed = ?2 where u.id = ?1")
	public void updateConfirmed(Long userId, boolean b);

}
