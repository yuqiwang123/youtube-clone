package com.programming.youtubeclone.service;

import com.programming.youtubeclone.model.User;
import com.programming.youtubeclone.model.Video;
import com.programming.youtubeclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        String sub = ((Jwt)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getClaim("sub");

        return userRepository.findBySub(sub)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with sub - " + sub));
    }

    public void addToLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToLikedVideos(videoId);
        userRepository.save(currentUser);
    }
    public boolean ifLikedVideos(String videoId) {
        return getCurrentUser().getLikedVideos().stream().anyMatch(likedVideo -> likedVideo.equals(videoId));
    }

    public boolean ifDisLikedVideos(String videoId) {
        return getCurrentUser().getDislikedVideos().stream().anyMatch(likedVideo -> likedVideo.equals(videoId));
    }

    public void removeFromLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromLikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void removeFromDislikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromDislikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void addToDisLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToDislikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void addVideoToHistory(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToVideoHistory(videoId);
        userRepository.save(currentUser);
    }

    public void subscribeUser(String userId) {
        User currentUser = getCurrentUser();
        currentUser.addToSubscribedToUsers(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with userId " + userId));
        user.addToSubscribers(currentUser.getId());

        userRepository.save(currentUser);
        userRepository.save(user);
    }

    public void unSubscribeUser(String userId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromSubscribedToUsers(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with userId " + userId));
        user.removeFromSubscribers(currentUser.getId());

        userRepository.save(currentUser);
        userRepository.save(user);
    }

    public Set<String> userHistory(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with userId " + userId));
        return user.getVideoHistory();
    }
}
