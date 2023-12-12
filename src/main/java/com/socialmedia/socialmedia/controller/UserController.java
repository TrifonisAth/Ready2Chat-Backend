package com.socialmedia.socialmedia.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialmedia.socialmedia.dto.ChatMessageDTO;
import com.socialmedia.socialmedia.dto.PersonDTO;
import com.socialmedia.socialmedia.dto.user_credentials.LoginRequest;
import com.socialmedia.socialmedia.dto.user_credentials.RegistrationRequest;
import com.socialmedia.socialmedia.entities.Message;
import com.socialmedia.socialmedia.entities.User;
import com.socialmedia.socialmedia.entities.VerificationToken;
import com.socialmedia.socialmedia.model.SecurityUser;
import com.socialmedia.socialmedia.services.impl.*;
import com.socialmedia.socialmedia.services.interfaces.FriendService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController()
@RequestMapping("/api")
public class UserController {
    private final UserServiceImpl userService;
    private final VerificationTokenServiceImpl verificationTokenServiceImpl;
    private final EmailServiceImpl emailService;
    private final PasswordEncoderService passwordEncoderService;
    private final JWTService jwtService;
    private final FriendService friendService;
    private final MessageService messageService;

    public UserController(
            UserServiceImpl userService,
            VerificationTokenServiceImpl verificationTokenServiceImpl,
            EmailServiceImpl emailService,
            PasswordEncoderService passwordEncoderService,
                          JWTService jwtService,
                          FriendService friendService,
                            MessageService messageService
    ) {
        this.userService = userService;
        this.verificationTokenServiceImpl = verificationTokenServiceImpl;
        this.emailService = emailService;
        this.passwordEncoderService = passwordEncoderService;
        this.jwtService = jwtService;
        this.friendService = friendService;
        this.messageService = messageService;
    }

    // TODO: Implement this.
//    @PostMapping("/resetPassword")
//    public ResponseEntity<?> resetPassword(@RequestBody String email, HttpServletResponse res) {
//        res.setHeader("Content-Type", "application/json");
//        User u = userService.findUserByEmail(email).orElse(null);
//        if (u == null) {
//            return ResponseEntity.badRequest().body("Invalid email.");
//        }
//        VerificationToken token = verificationTokenServiceImpl.generateToken(u);
//        emailService.sendVerificationToken(token);
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Reset password code sent.");
//        return ResponseEntity.ok().body(response);
//    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest user, HttpServletResponse res, HttpServletRequest req) throws JsonProcessingException {
        setHeaders(res);
        User u =  userService.findUserByEmail(user.getEmail()).orElse(null);
        if (u == null) {
            return ResponseEntity.badRequest().body("Invalid email.");
        }
        if (!passwordEncoderService.matches(user.getPassword(), u.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password.");
        }
        jwtService.setBothCookies(res, u.getId());
        userService.login(u);
        Map<String, Object> response = new HashMap<>();
        response.put("friends", userService.getUserFriends(u.getId()));
        response.put("requests", userService.getFriendRequests(u.getId()));

        response.put("inbox", userService.getConversations(u.getId()));
        response.put("ticket", jwtService.generateTicket(req, u.getId()));
        // Get all the users.
        List<PersonDTO> allUsers = userService.findAll().stream().map(User::toPersonDTO).toList();
        response.put("all_users", allUsers);
        response.put("user", u.toUserDets());
        response.put("message", "Logged in.");
        return getStringResponseEntity(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout( HttpServletResponse res) {
        res.setHeader("Content-Type", "application/json");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAnonymous(auth)) {
            return ResponseEntity.badRequest().body("You are not logged in.");
        }
        SecurityUser sec = (SecurityUser) auth.getPrincipal();
        userService.logout(sec.user());
        jwtService.disableCookies(res);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out.");
        return getStringResponseEntity(response);
    }

    private ResponseEntity<String> getStringResponseEntity(Map<String, Object> response) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse;
        try {
            jsonResponse = mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            System.out.println("Error while processing the response.");
            return ResponseEntity.badRequest().body("Error while processing the response.");
        }
        return ResponseEntity.ok().body(jsonResponse);
    }

    @GetMapping("/reconnect")
    public ResponseEntity<String> reconnect(HttpServletResponse res, HttpServletRequest req) throws JsonProcessingException {
        setHeaders(res);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAnonymous(auth)) {
            return ResponseEntity.badRequest().body("You are not logged in.");
        }
        System.out.println(auth.getPrincipal());
        SecurityUser sec = (SecurityUser) auth.getPrincipal();
        User u = sec.user();
        userService.login(u);
        System.out.println("Reconnected user: " + u.getDisplayName());
        Map<String, Object> response = new HashMap<>();
        response.put("friends", userService.getUserFriends(u.getId()));
        response.put("requests", userService.getFriendRequests(u.getId()));
        response.put("inbox", userService.getConversations(u.getId()));
        response.put("ticket", jwtService.generateTicket(req, u.getId()));
        // Get all the users.
        List<PersonDTO> allUsers = userService.findAll().stream().map(User::toPersonDTO).toList();
        response.put("all_users", allUsers);
        response.put("user", u.toUserDets());
        response.put("message", "Logged in.");
        return getStringResponseEntity(response);
    }

    @PostMapping("/message/store")
    public ResponseEntity<?> storeMessage(@RequestBody ChatMessageDTO message, HttpServletResponse res) {
        setHeaders(res);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAnonymous(auth)) {
            return ResponseEntity.badRequest().body("You are not logged in.");
        }
        SecurityUser sec = (SecurityUser) auth.getPrincipal();
        User u = sec.user();
        System.out.println("Message: " + message);
        System.out.println("User: " + u.getDisplayName());
        User  recipient = userService.findUserById(message.recipient().id());
        long fId = message.friendshipId();
        // Define the formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        LocalDateTime timestamp = LocalDateTime.parse(message.timestamp(), formatter);
        String content = message.message();
        Message msg = new Message(fId, u, recipient, content);
        messageService.save(msg);
        return ResponseEntity.ok().body("Message received.");
    }



    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody String code, HttpServletResponse res) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAnonymous(auth)) {
            return ResponseEntity.badRequest().body("You are not logged in.");
        }
        SecurityUser sec = (SecurityUser) auth.getPrincipal();
        User user = sec.user();
        setHeaders(res);
        VerificationToken v  = user.getVerificationToken();
        System.out.println("Code: " + v.getToken());
        if (!Objects.equals(v.getToken(), code)){
            return ResponseEntity.badRequest().body("Invalid code.");
        }
        // Check if the token is expired.
        if (v.isExpired()) {
            verificationTokenServiceImpl.delete(v.getId());
            VerificationToken newToken = verificationTokenServiceImpl.generateToken(user);
            emailService.sendVerificationToken(newToken);
            return ResponseEntity.badRequest().body("Code expired. A new one has been sent to you.");
        }
        // Verify the user.
        userService.verify(user);
        // Delete the token. It is no longer needed.
        user.setVerificationToken(null);
        verificationTokenServiceImpl.delete(v.getId());
        userService.login(user);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User verified.");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest user, HttpServletResponse res, HttpServletRequest req){
        setHeaders(res);
        System.out.println("Received: " + user.toString());
        if (!user.isValid()) {
            return ResponseEntity.badRequest().body("Invalid data.");
        }
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists.");
        }
        System.out.println("Display name: " + user.getDisplayName());
        if (userService.existsByDisplayName(user.getDisplayName())) {
            return ResponseEntity.badRequest().body("Display name already exists.");
        }
        user.setPassword(passwordEncoderService.encode(user.getPassword()));
        User newUser = userService.register(user);
        VerificationToken token = verificationTokenServiceImpl.generateToken(newUser);
        emailService.sendVerificationToken(token);
        jwtService.setBothCookies(res, newUser.getId());
        String clientIp = req.getRemoteAddr();
        System.out.println("Client IP: " + clientIp);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered.");
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/resend")
    public ResponseEntity<?> resendVerificationToken(HttpServletResponse res) {
        setHeaders(res);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAnonymous(auth)) {
            return ResponseEntity.badRequest().body("You are not logged in.");
        }
        SecurityUser sec = (SecurityUser) auth.getPrincipal();
        User u = sec.user();
        // Delete the old token.
        VerificationToken oldToken = verificationTokenServiceImpl.findTokenById(u.getId());
        verificationTokenServiceImpl.delete(oldToken.getId());
        VerificationToken token = verificationTokenServiceImpl.generateToken(u);
        emailService.sendVerificationToken(token);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "New verification code sent.");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/friendRequest/create")
    public ResponseEntity<?> sendFriendRequest(@RequestBody Long friendId, HttpServletResponse res) {
        setHeaders(res);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAnonymous(auth)) {
            return ResponseEntity.badRequest().body("You are not logged in.");
        }
        SecurityUser sec = (SecurityUser) auth.getPrincipal();
        long id = sec.user().getId();
        try {
            friendService.createRequest(id, friendId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Request already exists.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("requests", userService.getFriendRequests(id));
        response.put("message", "Friend request sent.");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/friendRequest/delete")
    public ResponseEntity<?> cancelFriendRequest(@RequestBody Long reqId, HttpServletResponse res) {
        setHeaders(res);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAnonymous(auth)) {
            return ResponseEntity.badRequest().body("You are not logged in.");
        }
        SecurityUser sec = (SecurityUser) auth.getPrincipal();
        long id = sec.user().getId();
        try {
            friendService.removeRequest(reqId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Request does not exist.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("requests", userService.getFriendRequests(id));
        response.put("message", "Friend request cancelled.");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/friendRequest/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody Long reqId, HttpServletResponse res) {
        setHeaders(res);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAnonymous(auth)) {
            return ResponseEntity.badRequest().body("You are not logged in.");
        }
        SecurityUser sec = (SecurityUser) auth.getPrincipal();
        long id = sec.user().getId();
        try {
            friendService.acceptRequest(reqId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Request does not exist.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("requests", userService.getFriendRequests(id));
        response.put("message", "Friend request cancelled.");
        response.put("friends", userService.getUserFriends(id));
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/friend/delete")
    public ResponseEntity<?> deleteFriend(@RequestBody Long friendshipId, HttpServletResponse res) {
        setHeaders(res);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isAnonymous(auth)) {
            return ResponseEntity.badRequest().body("You are not logged in.");
        }
        SecurityUser sec = (SecurityUser) auth.getPrincipal();
        long id = sec.user().getId();
        try {
            friendService.removeFriend(friendshipId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Friend does not exist.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("friends", userService.getUserFriends(id));
        response.put("message", "Friend deleted.");
        return ResponseEntity.ok().body(response);
    }

    private boolean isAnonymous(Authentication auth) {
        return auth.getPrincipal().equals("anonymousUser");
    }

    private void setHeaders(HttpServletResponse res) {
        res.setHeader("Content-Type", "application/json");
        res.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        res.setHeader("Access-Control-Allow-Credentials", "true");
    }
}
