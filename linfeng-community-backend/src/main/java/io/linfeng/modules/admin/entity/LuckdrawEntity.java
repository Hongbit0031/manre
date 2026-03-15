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
@TableName("lf_luckdraw")
public class LuckdrawEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Integer id;
	/**
	 * 奖品类型[1积分2谢谢惠顾3红包4自定义物品]
	 */
	private Integer prizeType;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 图片
	 */
	private String image;
	/**
	 * 奖品数量
	 */
	private Integer number;
	/**
	 * 抽奖几率
	 */
	private Double probability;
	/**
	 * 排序
	 */
	private Integer sort;
	/**
	 * 抽奖状态[0禁用1使用]
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
