package cn.tedu.store.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tedu.store.service.IOrderService;
import cn.tedu.store.util.JsonResult;

@RestController
@RequestMapping("orders")
public class OrderController extends BaseController {

	@Autowired
	IOrderService service;

	@PostMapping("create")
	public JsonResult<Void> createOrder(Integer aid, Integer[] cids,HttpSession session){
		Integer uid=getUidFromSession(session);
		String username=getUsernameFromSession(session);
		service.createOrder(aid,cids,uid,username);
		return new JsonResult<>(SUCCESS);
	}
	
}
