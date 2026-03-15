//铃声通知
export default{
	//播放系统提示音乐
	playSystemAudio(){
	    try {
	        let music = null;
	        music = uni.createInnerAudioContext();
	        //自己挑个好听的，这里我们为了减少前端压缩包大小共用一个声音资源
	        music.src = '/static/audio/message-notice.mp3';
	        music.play();
	        music.onEnded(()=>{music = null;})
	    } catch (error) {
	        // 静默处理浏览器自动播放限制错误
	        console.log('音频播放失败（可能是浏览器自动播放限制）:', error.message);
	    }
	},
	//播放提示音
	playMessageAudio(){
	    try {
	        let music = null;
	        music = uni.createInnerAudioContext();
	        music.src = '/static/audio/message-notice.mp3';
	        music.play();
	        music.onEnded(()=>{music = null;})
	    } catch (error) {
	        // 静默处理浏览器自动播放限制错误
	        console.log('音频播放失败（可能是浏览器自动播放限制）:', error.message);
	    }
	}
}