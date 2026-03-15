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
 * @email 3582996245@qq.com
 * @date 2022-08-14 14:28:48
 */
@Data
@TableName("lf_luckdraw_record")
public class LuckdrawRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Integer id;
	/**
	 * 用户ID
	 */
	private Integer userId;
	/**
	 * 奖品ID
	 */
	private Integer prizeId;
	/**
	 * 奖品类型
	 */
	private Integer prizeType;
	/**
	 * 奖品名称
	 */
	private String prizeName;
	/**
	 * 奖品图片
	 */
	private String prizeImage;
	/**
	 * 获得数量
	 */
	private Integer number;
	/**
	 * 抽奖时间
	 */
	private Date createTime;

}
