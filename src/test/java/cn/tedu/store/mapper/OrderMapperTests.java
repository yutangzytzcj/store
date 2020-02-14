package cn.tedu.store.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tedu.store.entity.District;
import cn.tedu.store.entity.Order;
import cn.tedu.store.entity.OrderItem;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderMapperTests {
	
	@Autowired
	OrderMapper mapper;

	@Test
	public void saveOrder() {
		Order order=new Order();
		order.setPrice(1000L);
		order.setRecvName("Tom");
		System.err.println("before:"+order.getId());
		Integer row=mapper.saveOrder(order);
		System.err.println("row="+row);
		System.err.println("after:"+order.getId());
		
	}
	
	@Test
	public void saveOrderItem() {
		OrderItem item=new OrderItem();
		item.setPid(111);
		item.setOid(2);
		item.setPrice(2000L);
		Integer row=mapper.saveOrderItem(item);
		System.err.println("row="+row);
	}
	
	@Test
	public void saveOrderItems() {
		List<OrderItem> list=new ArrayList<OrderItem>();
		for(int i=1;i<=5;i++) {
			OrderItem item=new OrderItem();
			item.setPid(i);
			item.setOid(2+i);
			item.setPrice(2000L+i);
			list.add(item);
		}
		Integer row=mapper.saveOrderItems(list);
		System.err.println("row="+row);
	}
	
	@Test
	public void updateStatus() {
		Integer row=mapper.updateStatus(5, 500, "Tom", new Date());
		System.err.println(row);
	}
	
	@Test
	public void findById() {
		Order order=mapper.findById(5);
		System.err.println(order);
	}
	

}
