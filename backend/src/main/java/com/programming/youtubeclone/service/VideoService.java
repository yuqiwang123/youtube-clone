package com.programming.youtubeclone.service;

import com.programming.youtubeclone.dto.CommentDto;
import com.programming.youtubeclone.dto.UploadVideoResponse;
import com.programming.youtubeclone.dto.VideoDto;
import com.programming.youtubeclone.model.Comment;
import com.programming.youtubeclone.model.Video;
import com.programming.youtubeclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final UserService userService;
    public UploadVideoResponse uploadVideo(MultipartFile multipartFile) {
        // Upload file to AWS S3
        // Save Video Data to Database
        String videoUrl = s3Service.uploadFile(multipartFile);
        var video = new Video();
        video.setVideoUrl(videoUrl);

        var savedVideo = videoRepository.save(video);
        return new UploadVideoResponse(savedVideo.getId(), savedVideo.getVideoUrl());
    }

    public VideoDto editVideo(VideoDto videoDto) {
        //Find the video by videoId
        var savedVideo = getVideoById(videoDto.getId());
        //Map the videoDto fields to video
        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());
        //save the video to the db
        videoRepository.save(savedVideo);
        return videoDto;
    }

    public String uploadThumbnail(MultipartFile file, String videoId) {
        var savedVideo = getVideoById(videoId);
        String thumbnailUrl = s3Service.uploadFile(file);
        savedVideo.setThumbnailUrl(thumbnailUrl);
        videoRepository.save(savedVideo);
        return thumbnailUrl;
    }

    Video getVideoById(String videoId) {
        //Find the video by videoId
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by - " + videoId));
    }

    public VideoDto getVideoDetails(String videoId) {
        Video savedVideo = getVideoById(videoId);

        increaseVideoCount(savedVideo);
        userService.addVideoToHistory(videoId);

        return mapToVideoDto(savedVideo);
    }

    private void increaseVideoCount(Video savedVideo) {
        savedVideo.incrementViewCount();
        videoRepository.save(savedVideo);
    }

    public VideoDto likeVideo(String videoId) {
        //Get video by id
        Video videoById = getVideoById(videoId);

        //Increment Like Count
        //If user already liked the video, then decrement like count
        //If user already disliked the video, then increment like count and decrement dislike count
        if(userService.ifLikedVideos(videoId)) {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
        }else if(userService.ifDisLikedVideos(videoId)) {
            videoById.decrementDisLikes();
            userService.removeFromDislikedVideos(videoId);
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }else {
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }
        videoRepository.save(videoById);

        return mapToVideoDto(videoById);
    }

    public VideoDto disLikeVideo(String videoId) {
        //Get video by id
        Video videoById = getVideoById(videoId);

        if(userService.ifDisLikedVideos(videoId)) {
            videoById.decrementDisLikes();
            userService.removeFromDislikedVideos(videoId);
        }else if(userService.ifLikedVideos(videoId)) {
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(videoId);
        }else {
            videoById.incrementDisLikes();
            userService.addToDisLikedVideos(videoId);
        }
        videoRepository.save(videoById);

        return mapToVideoDto(videoById);
    }

    private VideoDto mapToVideoDto(Video videoById) {
        VideoDto videoDto = new VideoDto();
        videoDto.setVideoUrl(videoById.getVideoUrl());
        videoDto.setThumbnailUrl(videoById.getThumbnailUrl());
        videoDto.setId(videoById.getId());
        videoDto.setTitle(videoById.getTitle());
        videoDto.setDescription(videoById.getDescription());
        videoDto.setTags(videoById.getTags());
        videoDto.setVideoStatus(videoById.getVideoStatus());
        videoDto.setLikeCount(videoById.getLikes().get());
        videoDto.setDislikeCount(videoById.getDisLikes().get());
        videoDto.setViewCount(videoById.getViewCount().get());
        return videoDto;
    }

    public void addComment(String videoId, CommentDto commentDto) {
        Video video = getVideoById(videoId);
        Comment comment = new Comment();
        comment.setText(commentDto.getCommentText());
        comment.setAuthorId(comment.getAuthorId());
        video.addComment(comment);
        videoRepository.save(video);
    }

    public List<CommentDto> getAllComments(String videoId) {
        Video video = getVideoById(videoId);
        List<Comment> commentList = video.getCommentList();
        return commentList.stream().map(this::mapToCommentDto).toList();
    }

    private CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentText(comment.getText());
        commentDto.setAuthorId(comment.getAuthorId());
        return commentDto;
    }

    public List<VideoDto> getAllVideos() {
        return videoRepository.findAll().stream().map(this::mapToVideoDto).toList();
    }
}
