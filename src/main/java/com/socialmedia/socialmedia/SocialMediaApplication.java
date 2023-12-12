package com.socialmedia.socialmedia;

import com.socialmedia.socialmedia.dao.impl.FriendRequestDAOImpl;
import com.socialmedia.socialmedia.dao.impl.FriendshipDAOImpl;
import com.socialmedia.socialmedia.dao.impl.UserDAOImpl;
import com.socialmedia.socialmedia.entities.FriendRequest;
import com.socialmedia.socialmedia.entities.Friendship;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.services.impl.FriendServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableScheduling
public class SocialMediaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialMediaApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(UserDAOImpl dao, FriendRequestDAOImpl fdao, FriendshipDAOImpl friendDAO, FriendServiceImpl fService) {
		return runner -> {
//			createFriendRequest(fdao, dao);
//			deleteUserById(dao);
//			deleteRequest(fdao, dao);
//			deleteRequest(fdao, dao);
//			testFriendReq(dao);
//			testRoles(dao);
//			removeRole(dao);
//			makeFriends(dao, friendDAO);
//			deleteFriend(dao, friendDAO);
//			testFriendCreation(fService);
//			removeRoles(dao);
		};
	}

	private void removeRoles(UserDAOImpl dao){
		List<User> users = dao.findAll();
		for (User user: users) {
			dao.removeRole(user.getId(), "ROLE_TEMP");
		}
	}

	private void testFriendCreation(FriendServiceImpl fService){
//		fService.createRequest(1, 5);
//		fService.acceptRequest(10);
//		fService.removeFriend(4);
	}

	private void deleteFriend(UserDAOImpl dao, FriendshipDAOImpl friendDAO){
		User user1 = dao.getUserWithFriends(1);
		System.out.println(user1.getFriends());
		friendDAO.delete(3);
		System.out.println(user1.getFriends());
	}

	private void makeFriends(UserDAOImpl dao, FriendshipDAOImpl friendDAO){
		User user1 = dao.getUserWithFriends(1);
		User user2 = dao.getUserWithFriends(5);
		Friendship friendship = new Friendship(user1, user2);
		friendDAO.save(friendship);
	}

	private void createFriendRequest(FriendRequestDAOImpl fdao, UserDAOImpl dao){
		User sender = dao.findUserById(1);
		User receiver = dao.findUserById(61);
		FriendRequest req = new FriendRequest(sender, receiver);
		fdao.save(req);
	}

	private void testRoles(UserDAOImpl dao){
		User user = dao.findUserById(61);
		dao.addRole(61, "ROLE_ADMIN");
	}



	public void deleteRequest(FriendRequestDAOImpl fdao, UserDAOImpl dao){
		dao.removeFriendRequest(9);
	}

	private void testFriendReq(UserDAOImpl dao){
		User sender = dao.getUserWithSentRequests(1);
		User receiver = dao.getUserWithReceivedRequests(61);
		System.out.println(sender.getSentFriendRequests());
		System.out.println(receiver.getReceivedFriendRequests().size());
	}

	private void deleteUserById(UserDAOImpl dao) {
		dao.delete(6);
	}

	private void createUsers(UserDAOImpl dao) {
		dao.save(new User("test", "test@123.test", "test"));
		dao.save(new User("test2", "test2@123.text", "test2"));
		dao.save(new User("test3", "test3@123.test", "test3"));
	}

	private void addBasicRoles(UserDAOImpl dao) {
		Set<User> users = new HashSet<>(dao.findAll());
		for (User user: users) {
			dao.update(user);
		}
	}

	private void removeRole(UserDAOImpl dao) {
		User user = dao.findUserById(5);
		dao.removeRole(user.getId(),"ROLE_ADMIN");
	}
}
