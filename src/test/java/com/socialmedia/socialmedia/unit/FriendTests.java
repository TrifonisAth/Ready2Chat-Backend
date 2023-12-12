package com.socialmedia.socialmedia.unit;

import com.socialmedia.socialmedia.dao.impl.FriendRequestDAOImpl;
import com.socialmedia.socialmedia.dao.impl.FriendshipDAOImpl;
import com.socialmedia.socialmedia.dao.impl.UserDAOImpl;
import com.socialmedia.socialmedia.entities.FriendRequest;
import com.socialmedia.socialmedia.entities.Friendship;
import com.socialmedia.socialmedia.entities.Role;
import com.socialmedia.socialmedia.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FriendTests {
    @Autowired
    private UserDAOImpl userDAO;
    @Autowired
    private FriendRequestDAOImpl reqDAO;
    @Autowired
    private FriendshipDAOImpl friendDAO;






@Test
void testFriend(){
    User a = userDAO.findUserById(61);
    User b = userDAO.findUserById(62);
    Friendship f = new Friendship(a,b);
    friendDAO.save(f);
}




//    @Test
            void testFriendRequest1() {
        FriendRequest req = reqDAO.findById(3);
        reqDAO.delete(req.getId());
//        User sender = req.getSender();
//        User receiver = req.getReceiver();
//        assertNotNull(sender);
//        assertNotNull(receiver);
//        assertEquals(sender.getSentFriendRequests().size(), 0);
//        assertEquals(receiver.getReceivedFriendRequests().size(), 0);
//        sender = userDAO.findUserById(sender.getId());
//        receiver = userDAO.findUserById(receiver.getId());
//        assertEquals(sender.getSentFriendRequests().size(), 0);
//        assertEquals(receiver.getReceivedFriendRequests().size(), 0);
    }

//    @Test
    void testFriendRequest() {
        FriendRequest req = reqDAO.findById(3);
        assertNotNull(req);
        User sender = req.getSender();
        User receiver = req.getReceiver();
        assertNotNull(sender);
        assertNotNull(receiver);
        assertEquals(sender.getSentFriendRequests().size(), 1);
        assertEquals(receiver.getReceivedFriendRequests().size(), 1);
        sender = userDAO.findUserById(sender.getId());
        receiver = userDAO.findUserById(receiver.getId());
        assertEquals(sender.getSentFriendRequests().size(), 1);
        assertEquals(receiver.getReceivedFriendRequests().size(), 1);
    }

//    @Test
    void testUserCRUD(){
        userDAO.delete(55);
        List<User> userList = userDAO.findAll();
        int size = userList.size();
        User a = new User("a", "aa@a.com", "a");
        User b = new User("b", "bb@b.com", "b");
        User c = new User("c", "cc@c.com", "c");
        userDAO.save(a);
        userDAO.save(b);
        userDAO.save(c);
        assertEquals(userDAO.findAll().size(), size + 3);
        assertFalse(userList.contains(a));
        assertTrue(!userList.contains(b));
        assertTrue(!userList.contains(c));
        userDAO.delete(a.getId());
        userDAO.delete(b.getId());
        userDAO.delete(c.getId());
        assertEquals(userDAO.findAll().size(), size);
        User d = new User("dsadad", "ddsadd@d.com", "d");
        userDAO.save(d);
        assertEquals(userDAO.findAll().size(), size + 1);
        d.setOnline(true);
        userDAO.update(d);
        d.addRole(new Role(d,"ROLE_TEMP"));
        userDAO.update(d);
        assertEquals(userDAO.findAll().size(), size + 1);
        assertTrue(userDAO.findUserById(d.getId()).isOnline());
        assertEquals(userDAO.findUserById(d.getId()).getRoles().size(), 1);
        assertEquals(userDAO.findUserById(d.getId()).getRoles().iterator().next().getRole(), "ROLE_TEMP");
        userDAO.update(d);
        userDAO.delete(d.getId());
        assertEquals(userDAO.findAll().size(), size);
    }

}
