package org.myftp.gattserver.jpatest;

import java.util.Calendar;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class FacadeService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContentNodeRepository contentNodeRepository;

	public void testFunctions() {

		ContentNode node = new ContentNode();
		node.setContentReaderId("contentReaderId");
		node.setCreationDate(Calendar.getInstance().getTime());
		node.setName("name" + System.currentTimeMillis());
		System.out.println("Saved content node: "
				+ contentNodeRepository.save(node).getId());

		User user = new User();
		user.setFavourites(new HashSet<ContentNode>());
		user.getFavourites().add(node);
		user.setConfirmed(true);
		user.setEmail("email");
		user.setPassword("password");
		user.setRegistrationDate(Calendar.getInstance().getTime());
		user.setName("name" + System.currentTimeMillis());
		System.out.println("Saved user: " + userRepository.save(user).getId());

		System.out.println("Count: " + userRepository.count());

		System.out.println("FindByName: " + userRepository.findByName("name"));
		System.out.println("FindByNameAndPassword: "
				+ userRepository.findByNameAndPassword("name", "password"));

		User foundUser = userRepository.findByIdAndFavouritesId(7L, 322L);
		System.out.println("FindByIdAndFavouritesId: "
				+ (foundUser == null ? null : foundUser.getId()));
		if (foundUser != null) {
			for (ContentNode favourite : foundUser.getFavourites()) {
				System.out.println(favourite.getId());
			}

			foundUser.setName("new name !!");
			userRepository.save(foundUser);
		}

	}

}
