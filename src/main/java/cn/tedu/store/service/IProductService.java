package cn.tedu.store.service;

import java.util.List;

import cn.tedu.store.entity.Product;

public interface IProductService {
	
	/**
	 * 减少一个商品的库存
	 * @param pid 商品的id
	 * @param num 库存减少的数量
	 */
	void reduceNum(Integer pid, Integer num);
	
	/**
	 * 增加一个商品的库存
	 * @param pid 商品的id
	 * @param num 库存增加的数量
	 */
	void addNum(Integer pid, Integer num);
	
	
	/**
	 * 查询热销商品
	 * @return 热销商品数据
	 */
	List<Product> getHotList();
	
	/**
	 * 使用商品id查商品数据
	 * @param id 商品id
	 * @return 商品数据
	 */
	Product getById(Integer id);

}
