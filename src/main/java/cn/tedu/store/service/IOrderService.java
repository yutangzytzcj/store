package cn.tedu.store.service;

import java.util.List;

import cn.tedu.store.entity.OrderItem;

/**
 * 订单及订单项的业务层接口
 */
public interface IOrderService {
	
	static interface Status{
		int UNPAID=0;
		int PAID=1;
		int CANCLED=2;
		int CLOSED=3;
	}
	
	/**
	 * 创建一个订单及相应的订单项数据的方法
	 * @param aid 收货地址id
	 * @param cids 一组购物车记录id
	 * @param uid 用户id
	 * @param username 操作人名称
	 */
	void createOrder(Integer aid,
			Integer[] cids, 
			Integer uid,
			String username);
	
	/**
	 * 更新一个订单的状态
	 * @param oid 订单的id
	 * @param status 新的订单状态{@link Status}
	 * @param username 操作者名称
	 */
	void changeStatus(Integer oid, 
			Integer status,
			String username);
	
	/**
	 *  关闭订单
	 * @param oid 订单的id
	 * @param orderItems 该订单的订单项集合
	 * @param username 操作者名称
	 */
	void close(Integer oid,
			List<OrderItem> orderItems,
			String username);
	

}




