package cn.tedu.store.service;

import java.util.List;

import cn.tedu.store.entity.CartVO;
import cn.tedu.store.service.ex.AccessDeniedException;
import cn.tedu.store.service.ex.CartNotFoundException;
import cn.tedu.store.service.ex.DeleteException;
import cn.tedu.store.service.ex.InsertException;
import cn.tedu.store.service.ex.UpdateException;

/**
 * 购物车的业务层接口
 */
public interface ICartService {
	
	/**
	 * 创建购物车记录
	 * @param num 商品数量
	 * @param uid 用户id
	 * @param pid 商品id
	 * @param username 创建人姓名
	 * @throws UpdateException
	 * @throws InsertException
	 */
	void createCart(
			Integer num,
			Integer uid, 
			Integer pid,
			String username)
					throws UpdateException,
					InsertException;
	
	/**
	 *  增加购物车中的商品的数量
	 * @param cid 商品的id
	 * @param num 数量的增量
	 * @param uid 用户的id
	 * @param username 最后修改人姓名
	 */
	void addNum(Integer cid, Integer num, 
			Integer uid, String username)throws CartNotFoundException,
			AccessDeniedException, UpdateException;
	
	/**
	 *  减少购物车中的商品的数量
	 * @param cid 商品的id
	 * @param num 减少的数量
	 * @param uid 用户的id
	 * @param username 最后修改人姓名
	 */
	void reduceNum(Integer cid, Integer num, 
			Integer uid, String username)throws CartNotFoundException,
			AccessDeniedException, UpdateException;
	
	
	/**
	 * 删除一条购物车记录
	 * @param cid 购物车记录id
	 * @param uid 用户id
	 * @throws CartNotFoundException
	 * @throws AccessDeniedException
	 * @throws DeleteException
	 */
	void removeCart(Integer cid,Integer uid)throws CartNotFoundException,
	AccessDeniedException, DeleteException;
	
	/**
	 * 根据一组cid删除对应的购物车记录
	 * @param cids 一组购物车id
	 * @param uid 用户id
	 */
	void removeByCids(Integer[] cids, Integer uid);
	
	
	/**
	 *  获取一个用户的所有购物车记录
	 * @param uid 用户id
	 * @return 所有购物车记录
	 */
	List<CartVO> getCartList(Integer uid);
	
	/**
	 * 根据一组cid查询对应的订单数据
	 * @param cids 一组订单id
	 * @param uid 用户id
	 * @return 一组订单数据
	 */
	List<CartVO> getByCids(Integer[] cids,Integer uid);

}
