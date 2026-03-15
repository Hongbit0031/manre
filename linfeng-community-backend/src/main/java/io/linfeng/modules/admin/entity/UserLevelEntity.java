package io.linfeng.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户经验值设置
 *
 * @author pity
 * @email linfengtech002@163.com
 * @date 2023-08-02 15:05:25
 */
@Data
@TableName("lf_user_level")
public class UserLevelEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Integer id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 最小要求值
	 */
	private Integer minNum;
	/**
	 * 最大要求值
	 */
	private Integer maxNum;
	/**
	 * 用户等级id
	 */
	private Integer levelId;

}
