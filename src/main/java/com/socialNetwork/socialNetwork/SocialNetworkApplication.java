package com.socialNetwork.socialNetwork;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialNetwork.socialNetwork.DAO.Chat.ChatService;
import com.socialNetwork.socialNetwork.DAO.Comment.CommentService;
import com.socialNetwork.socialNetwork.DAO.Message.MessageService;
import com.socialNetwork.socialNetwork.DAO.Profile.ProfileService;
import com.socialNetwork.socialNetwork.DAO.Topic.TopicService;
import com.socialNetwork.socialNetwork.Entity.Chat;
import com.socialNetwork.socialNetwork.Entity.Comment;
import com.socialNetwork.socialNetwork.Entity.Message;
import com.socialNetwork.socialNetwork.Entity.Profile;
import com.socialNetwork.socialNetwork.Entity.Topic;
import com.socialNetwork.socialNetwork.Jackson.Serializer.ProfileForFullSerialization;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SocialNetworkApplication implements CommandLineRunner{
    
    @Autowired 
    public MessageService messageService;
    @Autowired
    public ChatService chatService;
    @Autowired
    public ProfileService profileService;
    @Autowired
    public TopicService topicService;
    @Autowired
    public CommentService commentService;

	public static void main(String[] args) {
		SpringApplication.run(SocialNetworkApplication.class, args);
	}

    @Override
    public void run(String... args) throws Exception {
//        testMessageRepository();
//        testChatRepository();
//        testTopicRepository();
//          testInitialization();
//        testJackson();
//        testChatController();
    }
    
    private void testChatController(){
        List<String> cookies=testProfileControllerLogin();
        Chat chat=testChatControllerGet(cookies);
        chat.setTitle("Tested");
        Set<Profile> profiles=new HashSet<>();
        profiles.add(profileService.findById(Long.valueOf(2)));
        List<Message> messages=Arrays.asList(messageService.findById(Long.valueOf(1)),messageService.findById(Long.valueOf(2)));
        
        Chat newChat=new Chat("TestChatController");
        newChat=testChatControllerPOST(newChat,profiles,messages,cookies);
        //Profile profile=profileService.findById(Long.valueOf(3));
//        testChatControllerPatch(chat,cookies);
//        testChatControllerDelete(newChat, profiles.iterator().next(), messages.get(1), cookies);
        testProfileControllerLogout(cookies);
    }
    
    private void testProfileController(){
         List<String> cookies=testProfileControllerLogin();
        ProfileForFullSerialization profile=new ProfileForFullSerialization(testProfileControllerGet(cookies));
        profile.setName("Updated");
        testProfileControllerPut(profile,cookies);
        testProfileControllerLogout(cookies);
    }
    
    private void testMessageRepository(){
        List<Message> list=messageService.findall();
        System.out.println("");
        Message message=messageService.findById(Long.valueOf(1));
        messageService.delete(message);
        message=messageService.save(message);
        List<Message> messages=messageService.findBySenderIdOrderByDate(Long.valueOf(1));
        messages=messageService.findByChatIdOrderByDate(Long.valueOf(1));
        System.out.println("");
    }
    
    private void testChatRepository(){
        List<Chat> list=chatService.findAll();
        System.out.println("");
        Chat chat=chatService.findById(Long.valueOf(1));
        chatService.delete(chat);
        chat=chatService.save(chat);
        List<Profile>profList=profileService.findAll();
        for (Profile p:profList)
        {
            p.getChats().add(chat);
            profileService.save(p);
        }
        chatService.save(chat);
        Chat newChat=new Chat("new Chat");
        Profile newProfile=new Profile("Bob", "Marley", null, new Date(1976, 11, 23), "SomeLog", "SomePAss");
        newProfile=profileService.save(newProfile);
        Profile profile2=profileService.findById(Long.valueOf(3));
        newChat.getProfiles().add(profile2);
        newChat.getProfiles().add(newProfile);
        newProfile.getChats().add(newChat);
        profile2.getChats().add(newChat);
        chatService.save(newChat);
        profileService.save(profile2);
        profileService.save(newProfile);
        System.out.println("");
    }
    
    public void testTopicRepository(){
        List<Topic> topics=topicService.findAll();
        topics=topicService.findByProfileId(Long.valueOf(1));
        Topic newTopic=new Topic("SomeText");
        List<Profile> profiles=profileService.findAll();
        Profile profile=profiles.get(0);
        profiles.get(0).getChats().size();
        Set<Chat> chats=profiles.get(0).getChats();
        Comment comment=new Comment("Great post!", new Date(2017, 10, 4));
        newTopic.getComments().add(comment);
        newTopic.setProfile(profiles.get(0));
        comment.setTopic(newTopic);
        comment.setProfile(profiles.get(1));
        topicService.save(newTopic);
        System.out.println("");
    }

    public void testInitialization() { 
        Profile profile=profileService.findById(Long.valueOf(2));
        profileService.initializeChats(profile);
        profileService.initializeComment(profile);
        profileService.initializeMessage(profile);
        profileService.initializeTopics(profile);
        Chat chat=new Chat("jghdfghdf");
        chat=chatService.save(chat);
        chat.addProfile(profile);
        profile=profileService.save(profile);
        chat=chatService.findById(Long.valueOf(1));
        chatService.initializeMessages(chat);
        chatService.initializeProfile(chat);
        Message message=messageService.findById(Long.valueOf(1));
        Topic topic=topicService.findById(Long.valueOf(1));
        topicService.initializeComments(topic);
        Comment comment=commentService.findById(Long.valueOf(1));
        System.out.println("");
    }

    private void testJackson() {
        try {
            Profile profile=profileService.findById(Long.valueOf(1));
            profileService.initializeChats(profile);
            profileService.initializeComment(profile);
            profileService.initializeMessage(profile);
            profileService.initializeTopics(profile);
            ObjectMapper mapper=new ObjectMapper();
            String json=mapper.writeValueAsString(profile);
            System.out.println(json);
            profile=mapper.readValue(json,new TypeReference<Profile>(){});
            System.out.println("");
        } catch (JsonProcessingException ex) {
            Logger.getLogger(SocialNetworkApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SocialNetworkApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<String> testProfileControllerLogin() {
        RestTemplate restTemplate=new RestTemplate();
        String LOGIN_REQUEST="http://localhost:8080/profile/login";
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        headers.add("Authentication", "user:user");
        HttpEntity<Profile> httpEntity=new HttpEntity<Profile>(headers);
        ResponseEntity<Profile> responseEntity=restTemplate.postForEntity(LOGIN_REQUEST, httpEntity, Profile.class);
        List<String> cookie =responseEntity.getHeaders().get("Set-Cookie");
        System.out.println("");
        return cookie;
    }
    
    private void testProfileControllerLogout(List<String> cookies) {
        RestTemplate restTemplate=new RestTemplate();
        String LOGOUT_REQUEST="http://localhost:8080/profile/logout";
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Cookie",cookies.stream().collect(Collectors.joining(";")));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<Profile> httpEntity=new HttpEntity<Profile>(headers);
        ResponseEntity<Profile> responseEntity=restTemplate.postForEntity(LOGOUT_REQUEST, httpEntity, Profile.class);
        System.out.println("");
    }

    private Profile testProfileControllerGet(List<String> cookies) {
        RestTemplate restTemplate=new RestTemplate();
        String GET_REQUEST="http://localhost:8080/profile/2";
        HttpHeaders headers=new HttpHeaders();
        headers.set("Cookie",cookies.stream().collect(Collectors.joining(";")));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<Profile> httpEntity=new HttpEntity<Profile>(headers);
        ResponseEntity<Profile> response=restTemplate.exchange(GET_REQUEST, HttpMethod.GET, httpEntity, Profile.class);
        return response.getBody();
    }
    
    private void testProfileControllerPut(ProfileForFullSerialization profile,List<String> cookies) {
        RestTemplate restTemplate=new RestTemplate();
        String PUT_REQUEST="http://localhost:8080/profile";
        HttpHeaders headers=new HttpHeaders();
        headers.set("Cookie",cookies.stream().collect(Collectors.joining(";")));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<ProfileForFullSerialization> httpEntity=new HttpEntity<ProfileForFullSerialization>(profile,headers);
        ResponseEntity<ProfileForFullSerialization> response=restTemplate.exchange(PUT_REQUEST, HttpMethod.PUT, httpEntity, ProfileForFullSerialization.class);
        System.out.println("");
    }
    
    private Chat testChatControllerGet(List<String> cookies) {
        RestTemplate restTemplate=new RestTemplate();
        String GET_REQUEST="http://localhost:8080/chat/1";
        HttpHeaders headers=new HttpHeaders();
        headers.set("Cookie",cookies.stream().collect(Collectors.joining(";")));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<Chat> httpEntity=new HttpEntity<Chat>(headers);
        ResponseEntity<Chat> response=restTemplate.exchange(GET_REQUEST, HttpMethod.GET, httpEntity, Chat.class);
        return response.getBody();
    }
    
    private void testChatControllerPatch(Chat chat,List<String> cookies) {
        RestTemplate restTemplate=new RestTemplate();
        String PATCH_REQUEST="http://localhost:8080/chat";
        HttpHeaders headers=new HttpHeaders();
        headers.set("Cookie",cookies.stream().collect(Collectors.joining(";")));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<Chat> httpEntity=new HttpEntity<Chat>(chat,headers);
        ResponseEntity<Chat> response=restTemplate.exchange(PATCH_REQUEST, HttpMethod.PUT, httpEntity, Chat.class);
        System.out.println("");
    }
    
    private Chat testChatControllerPOST(Chat chat,Set<Profile> profiles,List<Message> messages,List<String> cookies) {
        RestTemplate restTemplate=new RestTemplate();
        String POST_REQUEST="http://localhost:8080/chat";
        HttpHeaders headers=new HttpHeaders();
        headers.set("Cookie",cookies.stream().collect(Collectors.joining(";")));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<Chat> httpEntity=new HttpEntity<Chat>(chat,headers);
        ResponseEntity<Chat> response=restTemplate.exchange(POST_REQUEST, HttpMethod.POST, httpEntity, Chat.class);
        chat=response.getBody();
        //
//        HttpEntity<Profile> testEntity=new HttpEntity<Profile>(profileService.findById(Long.valueOf(2)),headers);
//        response=restTemplate.exchange("http://localhost:8080/chat/"+chat.getId()+"/profiles", HttpMethod.POST, testEntity, Chat.class);
        //
        System.out.println("");
        for (Profile profile:profiles)
        {
            chat.getProfiles().add(profile);
            HttpEntity<Profile> httpEntity2=new HttpEntity<Profile>(profile,headers);
            ResponseEntity<Chat> response2=restTemplate.exchange(POST_REQUEST+"/"+chat.getId()+"/profiles", HttpMethod.POST, httpEntity2, Chat.class);
            System.out.println("");
        }
        for (Message message:messages)
        {
            chat.getMessages().add(message);
            HttpEntity<Message> httpEntity2=new HttpEntity<Message>(message,headers);
            ResponseEntity<Chat> response2=restTemplate.exchange(POST_REQUEST+"/"+chat.getId()+"/messages", HttpMethod.POST, httpEntity2, Chat.class);
            System.out.println("");
        }
        return chatService.findById(chat.getId());
    }
    
    private void testChatControllerDelete(Chat chat,Profile profile,Message message,List<String> cookies) {
        RestTemplate restTemplate=new RestTemplate();
        String DELETE_REQUEST="http://localhost:8080/chat";
        HttpHeaders headers=new HttpHeaders();
        headers.set("Cookie",cookies.stream().collect(Collectors.joining(";")));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<Profile> profileEntity=new HttpEntity<Profile>(profile,headers);
        HttpEntity<Message> messageEntity=new HttpEntity<Message>(message,headers);
        ResponseEntity<Chat> responseChat2=restTemplate.exchange(DELETE_REQUEST+"/"+chat.getId()+"/messages", HttpMethod.DELETE, messageEntity, Chat.class);
        chat=responseChat2.getBody();
        ResponseEntity<Chat> responseChat1=restTemplate.exchange(DELETE_REQUEST+"/"+chat.getId()+"/profiles", HttpMethod.DELETE, profileEntity, Chat.class);
        chat=responseChat1.getBody();
        HttpEntity<Chat> httpEntity=new HttpEntity<Chat>(chat,headers);
        //ResponseEntity<Chat> response3=restTemplate.exchange(DELETE_REQUEST, HttpMethod.DELETE, httpEntity, Chat.class);
    }
    
}
