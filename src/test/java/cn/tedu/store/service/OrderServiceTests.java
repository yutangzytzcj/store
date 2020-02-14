package cn.tedu.store.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tedu.store.service.IOrderService.Status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTests {
	
	@Autowired
	IOrderService service;
	
	@Test
	public void createOrder() {
		try {
			Integer[] cids= {18,19};
			service.createOrder(2, cids, 1, "管理员");
		} catch (Exception e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
		try {
			Thread.sleep(32*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void changeStatus() {
		try {
			service.changeStatus(8, Status.CLOSED, "管理员");
		} catch (Exception e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}

}





