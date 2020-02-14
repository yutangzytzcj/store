package cn.tedu.store.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.tedu.store.entity.Order;
import cn.tedu.store.entity.OrderItem;

/**
 * 订单数据和订单项数据的持久层接口
 */
public interface OrderMapper {
	
	/**
	 * 保存一条订单数据
	 * @param order 订单数据
	 * @return 受影响的行数
	 */
	Integer saveOrder(Order order);

	/**
	 * 保存一条订单项数据
	 * @param item 订单项数据
	 * @return 受影响的行数
	 */
	Integer saveOrderItem(OrderItem item);
	
	
	/**
	 * 保存多条订单项数据
	 * @param list 订单项数据的集合
	 * @return 受影响的行数
	 */
	Integer saveOrderItems(List<OrderItem> list);
	
	/**
	 * 更新订单状态
	 * @param id 订单id
	 * @param status 状态 0-未支付 1-已支付 2-取消 3-关闭
	 * @param username 操作人姓名
	 * @param modifiedTime 最后修改时间
	 * @return
	 */
	Integer updateStatus(
			@Param("id") Integer id,
			@Param("status") Integer status,
			@Param("username") String username,
			@Param("modifiedTime") Date modifiedTime);

	
	/**
	 * 根据id查询订单数据
	 * @param id 订单id
	 * @return 订单数据
	 */
	Order findById(Integer id);
	

}
