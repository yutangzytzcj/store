package cn.tedu.store.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.tedu.store.entity.Product;

/**
 * 商品数据的持久层接口
 */
public interface ProductMapper {
	
	/**
	 *  更新一个商品的库存数量
	 * @param num 新的库存数量
	 * @param id 商品的id
	 * @return
	 */
	Integer updateNum(
			@Param("num") Integer num,
			@Param("id") Integer id);
	
	/**
	 * 查询优先级排行前4位的商品数据
	 * @return 优先级排行前4位的商品数据
	 */
	List<Product> findHotList(); 
	
	/**
	 * 根据商品id查商品数据
	 * @param id 商品id
	 * @return 商品数据
	 */
	Product findById(Integer id);

}



