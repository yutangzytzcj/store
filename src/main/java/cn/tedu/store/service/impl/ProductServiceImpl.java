package cn.tedu.store.service.impl;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tedu.store.entity.Product;
import cn.tedu.store.mapper.ProductMapper;
import cn.tedu.store.service.IProductService;
import cn.tedu.store.service.ex.ProductNotFoundException;
import cn.tedu.store.service.ex.ProductOutOfStockException;
import cn.tedu.store.service.ex.UpdateException;

@Service
public class ProductServiceImpl implements IProductService {

	@Autowired
	ProductMapper mapper;
	
	

	@Override
	public void reduceNum(Integer pid, Integer num) {
		// 根据pid查询商品数据
		Product product=findById(pid);
		// 判断结果是否为null
		if(product==null) {
			// 是：ProductNotFoundException
			throw new ProductNotFoundException("减少商品库存数量异常！商品数据不存在！");
		}
		
		// 忽略商品下架

		// 从查询结果中获取当前库存量
		Integer oldNum=product.getNum();
		// 计算库存减少后的结果
		Integer newNum=oldNum-num;
		// 判断该结果是否小于0
		if(newNum<0) {
			// 是：ProductOutOfStockException
			throw new ProductOutOfStockException("减少商品库存数量异常！商品库存不足！");
		}

		// 执行更新操作
		updateNum(newNum, pid);
	}
	
	@Override
	public void addNum(Integer pid, Integer num) {
		// 根据pid查询商品数据
		Product product=findById(pid);
		// 判断结果是否为null
		if(product==null) {
			// 是：ProductNotFoundException
			throw new ProductNotFoundException("减少商品库存数量异常！商品数据不存在！");
		}
		
		// 忽略商品下架

		// 从查询结果中获取当前库存量
		Integer oldNum=product.getNum();
		// 计算库存减少后的结果
		Integer newNum=oldNum+num;
		
		// 执行更新操作
		updateNum(newNum, pid);
	}
	
	
	
	@Override
	public List<Product> getHotList() {
		return findHotList();
	}
	

	@Override
	public Product getById(Integer id) {
		// 使用id查商品数据
		Product product=findById(id);
		if(product==null) {
			throw new ProductNotFoundException("显示商品详情异常！未找到商品数据");
		}
		
		// 将不需要给用户的数据设为null
		product.setPriority(null);
		product.setStatus(null);
		product.setCreatedUser(null);
		product.setCreatedTime(null);
		product.setModifiedUser(null);
		product.setModifiedTime(null);
		
		return product;
	}
	
	
	/**
	 *  更新一个商品的库存数量
	 * @param num 新的库存数量
	 * @param id 商品的id
	 * @return
	 */
	private void updateNum(Integer num,Integer id) throws UpdateException {
		Integer row=mapper.updateNum(num, id);
		if(row!=1) {
			throw new UpdateException("更新商品库存数量失败！更新异常！");
		}
	}
	
	
	/**
	 * 查询优先级排行前4位的商品数据
	 * @return 优先级排行前4位的商品数据
	 */
	private List<Product> findHotList(){
		return mapper.findHotList();
	}
	
	private Product findById(Integer id) {
		return mapper.findById(id);
	}


}
