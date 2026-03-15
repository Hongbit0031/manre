export default {
    // 存储类型: 本地存储或minio存储请填写：local, 阿里云存储请填写：aliyun, 七牛云存储请填写：qiniu
    storageType: 'qiniu',
    
    // 获取视频封面图
    getVideoThumb(url) {
      switch(this.storageType) {
        case 'aliyun':
          return url + '?x-oss-process=video/snapshot,t_0,f_jpg';
        case 'qiniu':
          return url + '?vframe/jpg/offset/1';
        case 'local':
        default:
          return '/static/videoBg.jpg';
      }
    }
  }