package cz.gattserver.grass3.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cz.gattserver.grass3.model.domain.User;
import cz.gattserver.grass3.security.Role;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByName(String name);

	User findByNameAndPassword(String name, String passwordHash);

	User findByIdAndFavouritesId(long userId, long contentNodeId);

	List<User> findByFavouritesId(long contentNodeId);

	@Query("select ?2 member of u.roles from USER_ACCOUNTS u where u.id = ?1")
	boolean hasRole(long userId, Role role);

	@Modifying
	@Query("update USER_ACCOUNTS u set u.confirmed = ?2 where u.id = ?1")
	void updateConfirmed(long userId, boolean b);


}
