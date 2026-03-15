package io.linfeng.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author linfeng
 * @email linfengtech001@163.com
 * @date 2022-06-28 13:56:51
 */
@Data
@TableName("lf_vote_result")
public class VoteResultEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Integer id;
	/**
	 * 用户id
	 */
	private Integer uid;
	/**
	 * 投票id
	 */
	private Integer voteId;
	/**
	 * 结果
	 */
	private String result;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
