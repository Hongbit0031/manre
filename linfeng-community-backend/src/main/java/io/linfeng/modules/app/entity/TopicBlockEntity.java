package io.linfeng.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 圈子用户拉黑
 * 
 * @author pity
 * @email linfengtech002@163.com
 * @date 2023-06-25 13:40:04
 */
@Data
@TableName("lf_topic_block")
public class TopicBlockEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Integer id;
	/**
	 * 用户id
	 */
	private Integer uid;
	/**
	 * 操作用户id
	 */
	private Integer operateId;
	/**
	 * 圈子id
	 */
	private Integer topicId;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
