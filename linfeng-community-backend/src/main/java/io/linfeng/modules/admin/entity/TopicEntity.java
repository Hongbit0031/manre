package io.linfeng.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 圈子实体类
 * 
 * @author linfeng
 * @email 2445465217@qq.com
 * @date 2022-01-21 17:01:12
 */
@Data
@TableName("lf_topic")
public class TopicEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 圈子id
	 */
	@TableId(value = "id",type = IdType.AUTO)
	private Integer id;
	/**
	 * 创建用户id
	 */
	private Integer uid;
	/**
	 * 分类id
	 */
	private Integer cateId;
	/**
	 * 圈子名称
	 */
	private String topicName;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * logo
	 */
	private String coverImage;
	/**
	 * 背景图
	 */
	private String bgImage;
	/**
	 * 推荐类型：0 不推荐， 1首页推荐， 2圈子页推荐 
	 */
	private Integer topType;
	/**
	 * 圈子状态：0 正常 ，1禁用  2待审核
	 */
	private Integer status;
	/**
	 * 是否首页推荐圈子内容
	 */
	private Integer indexRecommend;
	/**
	 * 加入人数
	 */
	private Integer userNum;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 进圈条件0无限制1答题并审核
	 */
	private Integer rest;
	/**
	 * 问题内容设置
	 */
	private String question;
	/**
	 * 是否私密：0公开 1私密
	 */
	private Integer isPrivacy;

}
