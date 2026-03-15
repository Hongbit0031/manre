package io.linfeng.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("lf_vote_subject")
public class VoteSubjectEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 1单选2多选
	 */
	private Integer type;
	/**
	 * 到期时间
	 *
	 */
	private Date expireTime;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
