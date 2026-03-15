package io.linfeng.modules.job.task;

import io.linfeng.modules.admin.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 *机器人定时发帖
 */
@Component("robotTask")
public class RobotTask implements ITask {


	@Autowired
	private PostService postService;



	@Override
	public void run(String params){

		postService.getRobotPostContent();
	}
}
