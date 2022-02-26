package com.programming.youtubeclone.repository;

import com.programming.youtubeclone.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository extends MongoRepository<Video, String> {
}
