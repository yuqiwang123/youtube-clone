import { Component, OnInit } from '@angular/core';
import {VideoService} from "../video.service";
import {VideoDto} from "../video-dto";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  videos: VideoDto[] = [];
  constructor(private videoService: VideoService) { }

  ngOnInit(): void {
    this.videoService.getAllVideos().subscribe(res => {
      this.videos = res;
      console.log(this.videos);
    });
  }

}
