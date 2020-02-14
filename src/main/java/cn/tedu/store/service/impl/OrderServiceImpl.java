package cn.tedu.store.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tedu.store.entity.Address;
import cn.tedu.store.entity.CartVO;
import cn.tedu.store.entity.Order;
import cn.tedu.store.entity.OrderItem;
import cn.tedu.store.mapper.OrderMapper;
import cn.tedu.store.service.IAddressService;
import cn.tedu.store.service.ICartService;
import cn.tedu.store.service.IOrderService;
import cn.tedu.store.service.IProductService;
import cn.tedu.store.service.ex.AddressNotFoundException;
import cn.tedu.store.service.ex.CartNotFoundException;
import cn.tedu.store.service.ex.InsertException;
import cn.tedu.store.service.ex.OrderNotFoundException;
import cn.tedu.store.service.ex.ServiceException;
import cn.tedu.store.service.ex.UpdateException;

@Service
public class OrderServiceImpl implements IOrderService {

	@Autowired
	OrderMapper mapper;
	
	@Autowired
	IAddressService addressService;
	
	@Autowired
	ICartService cartService;
	
	@Autowired
	IProductService productService;
	
	@Override
	public void createOrder(Integer aid, Integer[] cids, Integer uid, String username) 
		throws ServiceException{
		// 创建当前时间的对象 now
		Date now=new Date();

		// 根据cids查询对应的CartVO的集合
		List<CartVO> cartVOs=cartService.getByCids(cids, uid);
		// 判断结果集合的长度是否为0
		if(cartVOs.size()==0) {
			// 是：CartNotFoundException
			throw new CartNotFoundException("创建订单记录异常！未找到相关购物车记录");
		}

		// 计算totalPrice
		Long totalPrice=0L;
		for(CartVO vo:cartVOs) {
			totalPrice+=(vo.getRealPrice()*vo.getNum());
		}

		// 创建一个Order对象
		Order order=new Order();
		// 补充uid
		order.setUid(uid);
		// 根据aid查询收获地址数据
		Address address=addressService.getByAid(aid);
		// 判断结果是否为null
		if(address==null) {
			// 是：AddressNotFoundException
			throw new AddressNotFoundException("创建订单记录异常！未查到对应的收货地址！");
		}

		// 补充recv*数据
		order.setRecvName(address.getName());
		order.setRecvPhone(address.getPhone());
		order.setRecvProvince(address.getProvinceName());
		order.setRecvCity(address.getCityName());
		order.setRecvArea(address.getAreaName());
		order.setRecvAddress(address.getAddress());
		// 补充status -> 0 未支付 1 已支付 2已取消 
		order.setStatus(0);
		// 补充price -> 总价，在上面已经计算过
		order.setPrice(totalPrice);
		// 补充orderTime -> now
		order.setOrderTime(now);
		// 补充4项日志数据
		order.setCreatedUser(username);
		order.setCreatedTime(now);
		order.setModifiedUser(username);
		order.setModifiedTime(now);
		// 添加订单数据
		saveOrder(order);
		
		
		List<OrderItem> orderItems=new ArrayList<OrderItem>();
		// 遍历上面查到的CartVO的集合
		for(CartVO vo:cartVOs) {
			//-- 创建一个OrderItem对象
			OrderItem item=new OrderItem();
			//-- 补充 oid
			item.setOid(order.getId());
			//-- 补充 pid,num,price,image,titel
			item.setPid(vo.getPid());
			item.setNum(vo.getNum());
			item.setPrice(vo.getRealPrice());
			item.setImage(vo.getImage());
			item.setTitle(vo.getTitle());
			//-- 补充 4项日志数据
			item.setCreatedUser(username);
			item.setCreatedTime(now);
			item.setModifiedUser(username);
			item.setModifiedTime(now);
			//-- 添加一个OrderItem数据
			saveOrderItem(item);
			orderItems.add(item);
			// 销库存
			productService.reduceNum(vo.getPid(), vo.getNum());
		}

		// 未完，待续
		// 删除订单项对应的购物车记录
		cartService.removeByCids(cids, uid);
		
		// 处理超时未支付		
		// 启动子线程，休眠15分钟
		// 在子线程醒来之后，执行关闭订单的操作
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.err.println("子线程准备休眠...");
				try {
					Thread.sleep(30*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.err.println("子线程启动，执行关闭订单操作...");
				// 调用关闭订单的方法
				close(order.getId(), orderItems, username);
			}
		}).start();
		
		
	}
	
	@Override
	public void changeStatus(Integer oid, Integer status, String username) {
		// 使用oid查询订单数据
		Order order=findById(oid);
		// 判断结果是否为Null
		if(order==null) {
			// 是： OrderNotFoundException
			throw new OrderNotFoundException("更新订单状态异常！订单数据不存在!");
		}
	
		// 更新订单状态
		updateStatus(oid, status, username, new Date());
	}
	
	@Override
	public void close(Integer oid, List<OrderItem> orderItems, String username) {
		// 使用oid查询订单数据
		Order order=findById(oid);
		// 判断结果是否为Null
		if(order==null) {
			// 是： OrderNotFoundException
			throw new OrderNotFoundException("关闭订单异常！订单数据不存在!");
		}

		// 判断订单状态是否不为0
		if(!order.getStatus().equals(0)) {
			// 是：return;
			return;
		}

		// 修改订单状态 status->3
		changeStatus(oid, Status.CLOSED, username);

		// 归还库存
		// 遍历orderItems
		for(OrderItem item:orderItems) {
			// -- 调用增加库存的方法 addNum(pid,num);
			productService.addNum(item.getPid(), item.getNum());
		}
	};
	
	
	
	/**
	 * 保存一条订单数据
	 * @param order 订单数据
	 * @return 受影响的行数
	 */
	private void saveOrder(Order order) throws InsertException {
		Integer row=mapper.saveOrder(order);
		if(row!=1) {
			throw new InsertException("创建订单记录异常！插入订单数据异常！");
		}
	}

	/**
	 * 保存一条订单项数据
	 * @param item 订单项数据
	 * @return 受影响的行数
	 */
	private void saveOrderItem(OrderItem item) throws InsertException{
		Integer row=mapper.saveOrderItem(item);
		if(row!=1) {
			throw new InsertException("创建订单记录异常！插入订单项数据异常！");
		}
	}
	
	/**
	 * 更新订单状态
	 * @param id 订单id
	 * @param status 状态 0-未支付 1-已支付 2-取消 3-关闭
	 * @param username 操作人姓名
	 * @param modifiedTime 最后修改时间
	 * @return
	 */
	private void updateStatus(
			Integer id,Integer status,
			String username,Date modifiedTime) throws UpdateException{
		Integer row=mapper.updateStatus(id, status, username, modifiedTime);
		if(row!=1) {
			throw new UpdateException("更新订单状态异常！");
		}
	}

	
	/**
	 * 根据id查询订单数据
	 * @param id 订单id
	 * @return 订单数据
	 */
	private Order findById(Integer id) {
		return mapper.findById(id);
	}


}
