package cn.tedu.store.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.tedu.store.entity.Address;
import cn.tedu.store.entity.CartVO;
import cn.tedu.store.entity.District;
import cn.tedu.store.entity.User;
import cn.tedu.store.service.ex.ServiceException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartServiceTests {
	
	@Autowired
	ICartService service;
	
	@Test
	public void createCart() {
		try {
			service.createCart(33, 10, 10000017, "令狐冲");
		}catch(ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void addNum() {
		try {
			service.addNum(6, 100, 10, "林平之");
		}catch(ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void reduceNum() {
		try {
			service.reduceNum(5, 5, 1, "林平之");
		}catch(ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	
	@Test
	public void removeCart() {
		try {
			service.removeCart(2, 10);
		}catch(ServiceException e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void removeByCids() {
		try {
			Integer[] cids= {14,15};
			service.removeByCids(cids, 2);
		}catch(Exception e) {
			System.err.println(e.getClass().getName());
			System.err.println(e.getMessage());
		}
	}
	
	
	
	@Test
	public void getByCids() {
		Integer[] cids= {9,10,11};
		List<CartVO> list=service.getByCids(cids, 1);
		for(CartVO vo:list) {
			System.err.println(vo);
		}
	}
	
	
	@Test
	public void a() {
		List<Integer> list=new ArrayList<Integer>();
		list.add(1);
		list.add(3);
		list.add(5);
		list.add(6);
		Integer[] arr=list.toArray(new Integer[list.size()]);
		for(Integer i:arr) {
			System.err.println(i);
		}
	}
	
}





