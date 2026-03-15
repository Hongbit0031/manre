package io.linfeng.modules.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 进圈申请
 * 
 * @author pity
 * @email linfengtech002@163.com
 * @date 2023-06-26 14:21:34
 */
@Data
@TableName("lf_topic_apply")
public class TopicApplyEntity implements Serializable {
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
	 * 圈子id
	 */
	private Integer topicId;
	/**
	 * 状态0待审核1已通过2已拒绝
	 */
	private Integer status;
	/**
	 * 回答
	 */
	private String answer;
	/**
	 * 问题
	 */
	private String question;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 审核用户id
	 */
	private Integer operateId;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
