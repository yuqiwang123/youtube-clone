export interface VideoDto {
  id: string;
  title: string;
  description: string;
  tags: Array<string>;
  videoUrl: string;
  videoStatus: string;
  thumbnailUrl: string;
  viewCount: number;
  disLikes: number;
  likes: number;
}
