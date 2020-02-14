package cn.tedu.store.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.tedu.store.entity.Cart;
import cn.tedu.store.entity.CartVO;
import cn.tedu.store.mapper.CartMapper;
import cn.tedu.store.service.ICartService;
import cn.tedu.store.service.IProductService;
import cn.tedu.store.service.ex.AccessDeniedException;
import cn.tedu.store.service.ex.CartNotFoundException;
import cn.tedu.store.service.ex.DeleteException;
import cn.tedu.store.service.ex.InsertException;
import cn.tedu.store.service.ex.UpdateException;

@Service
public class CartServiceImpl implements ICartService {

	@Autowired
	CartMapper mapper;
	
	@Autowired
	IProductService productService;
	
	@Override
	public void createCart(Integer num, Integer uid, Integer pid, String username)
			throws UpdateException, InsertException {
		// 使用uid和pid查询是否有购物车数据
		Cart result=getByUidAndPid(uid, pid);
		// 没有：
		if(result==null) {
			// 创建一个Cart对象
			Cart cart=new Cart();
			// 手动添加pid,uid,num到cart
			cart.setPid(pid);
			cart.setUid(uid);
			cart.setNum(num);
			// 使用pid查询商品价格
			Long price=productService.getById(pid).getPrice();
			// 手动添加商品价格到cart
			cart.setPrice(price);
			// 手动添加4个日志数据到cart
			Date now=new Date();
			cart.setCreatedUser(username);
			cart.setCreatedTime(now);
			cart.setModifiedUser(username);
			cart.setModifiedTime(now);
			// 将cart添加到数据库
			saveCart(cart);
			return;
		}
		
		// 有：
		// 从查询结果中获取cid
		Integer cid=result.getCid();
		// 从查询结果中获取原购物车中的数量
		Integer oldNum=result.getNum();
		// 计算出新的商品数量=原数量+num
		int newNum=oldNum+num;
		// 执行更新操作
		updateNum(cid, newNum, username, new Date());
	}
	
	@Override
	public void addNum(Integer cid, Integer num, Integer uid, String username)
			throws CartNotFoundException, AccessDeniedException, UpdateException {
		// 使用cid查购物车记录
		Cart result=findByCid(cid);
		// 判断结果是否为null
		if(result==null) {
			// 是：CartNotFoundException
			throw new CartNotFoundException("增加购物车商品数量异常！记录不存在");
		}
		
		// 判断result中的uid和参数中的uid是否不一致
		if(!result.getUid().equals(uid)) {
			// 是：AccessDeniedException
			throw new AccessDeniedException("增加购物车商品数量异常！没有操作权限");
		}
		
		// 从result中获取之前的num
		Integer oldNum=result.getNum();
		// 计算生成新的num
		Integer newNum=oldNum+num;
		// 执行更新操作
		updateNum(cid, newNum, username, new Date());
	}
	
	
	@Override
	public void reduceNum(Integer cid, Integer num, Integer uid, String username)
			throws CartNotFoundException, AccessDeniedException, UpdateException {
		// 使用cid查购物车记录
		Cart result=findByCid(cid);
		// 判断结果是否为null
		if(result==null) {
			// 是：CartNotFoundException
			throw new CartNotFoundException("增加购物车商品数量异常！记录不存在");
		}
		
		// 判断result中的uid和参数中的uid是否不一致
		if(!result.getUid().equals(uid)) {
			// 是：AccessDeniedException
			throw new AccessDeniedException("增加购物车商品数量异常！没有操作权限");
		}
		
		// 从result中获取之前的num
		Integer oldNum=result.getNum();
		// 计算生成新的num
		Integer newNum=oldNum-num;
		
		// 判断剩余数量是否小于等于0
		if(newNum<=0) {
			// 1. 什么都不做
			return;
			
			// 2. 删除该购物车记录
			// deleteByCid(cid);
			// return;
		}
		
		updateNum(cid, newNum, username, new Date());
	}
	
	

	@Override
	public void removeCart(Integer cid, Integer uid)
			throws CartNotFoundException, AccessDeniedException, DeleteException {
		// 使用cid查购物车记录
		Cart result=findByCid(cid);
		// 判断结果是否为null
		if(result==null) {
			// 是：CartNotFoundException
			throw new CartNotFoundException("删除购物车记录异常！记录不存在");
		}
		
		// 判断result中的uid和参数中的uid是否不一致
		if(!result.getUid().equals(uid)) {
			// 是：AccessDeniedException
			throw new AccessDeniedException("删除购物车记录异常！没有操作权限");
		}
		
		// 执行删除操作
		deleteByCid(cid);
	}
	
	@Override
	public void removeByCids(Integer[] cids, Integer uid) {
		// 验证cids的正确性
		if(cids==null) {
			throw new IllegalArgumentException("参数异常");
		}
		List<CartVO> cartVOs=findByCids(cids);
		if(cartVOs.size()==0) {
			throw new CartNotFoundException("删除购物车记录异常！未找到对应记录");
		}
		
		// 验证数据的归属性
		// 用于保存正确的cid的集合
		List<Integer> rightCids=new ArrayList<Integer>();
		
		// 遍历，筛选出当前用户有权限操作的cid
		for(CartVO vo:cartVOs) {
			if(vo.getUid().equals(uid)) {
				rightCids.add(vo.getCid());
			}
		}
		// 执行删除操作
		deleteByCids(rightCids.toArray(new Integer[rightCids.size()]));
	}

	
	
	@Override
	public List<CartVO> getCartList(Integer uid) {
		return findCartList(uid);
	}
	

	@Override
	public List<CartVO> getByCids(Integer[] cids, Integer uid) {
		// 参数判断
		if(cids==null) {
			return new ArrayList<CartVO>();
		}
		
		List<CartVO> result=findByCids(cids);
		// 仅返回参数uid对应的数据
		Iterator<CartVO> it=result.iterator();
		while(it.hasNext()) {
			CartVO vo=it.next();
			if(!vo.getUid().equals(uid)) {
				// 删除与参数uid不一致的购物车记录
				it.remove();
			}
		}
		
		return result;
	}
	
	
	
	/**
	 *   查询一个用户的所有购物车记录
	 * @param uid 用户id
	 * @return 购物车记录的集合
	 */
	private List<CartVO> findCartList(Integer uid){
		return mapper.findCartList(uid);
	}

	/**
	 *  添加一条购物车记录
	 * @param cart 购物车记录
	 * @return 受影响的行数
	 */
	private void saveCart(Cart cart) throws InsertException {
		Integer row=mapper.saveCart(cart);
		if(row!=1) {
			throw new InsertException("添加购物车异常！请联系管理员");
		}
	}

	/**
	 *  更新一条购物车记录中的商品数量
	 * @param cid 购物车记录id
	 * @param num 商品数量
	 * @param username 最后修改人姓名
	 * @param modifiedTime 最后修改时间
	 * @return 受影响的行数
	 */
	private void updateNum(
			Integer cid, Integer num, 
			String username,Date modifiedTime) throws UpdateException {
		Integer row=mapper.updateNum(cid, num, username, modifiedTime);
		if(row!=1) {
			throw new UpdateException("添加购物车异常！更新数量失败！");
		}
	};
	


	
	/**
	 * 根据用户id和商品id查购物车记录
	 * @param uid 用户id
	 * @param pid 商品id
	 * @return 购物车记录 或 null
	 */
	private Cart getByUidAndPid(
			Integer uid,Integer pid) {
		return mapper.getByUidAndPid(uid, pid);
	}
	
	
	/**
	 * 根据cid查购物车数据
	 * @param cid 购物车记录id
	 * @return 购物车数据 或 null
	 */
	private Cart findByCid(Integer cid) {
		return mapper.findByCid(cid);
	}

	
	
	/**
	 * 根据cid删除一条购物车记录
	 * @param cid 购物车id
	 * @return 受影响的行数
	 */
	private void deleteByCid(Integer cid) throws DeleteException  {
		Integer row=mapper.deleteByCid(cid);
		if(row!=1) {
			throw new DeleteException("删除购物车记录异常！请联系管理员");
		}
	}
	
	
	/**
	 * 根据一组cid删除对应的购物车记录
	 * @param cids 一组购物车id
	 * @return 受影响的行数
	 */
	private void deleteByCids(Integer[] cids)throws DeleteException {
		if(cids==null || cids.length==0) {
			throw new IllegalArgumentException("删除购物车记录异常！参数异常！");
		}
		
		Integer rows=mapper.deleteByCids(cids);
		if(rows<1) {
			throw new DeleteException("删除购物车记录异常！请联系管理员");
		}
	}

	/**
	 *  根据一组cid查询对应的购物车记录
	 * @param cids 一组购物车记录的id
	 * @return 购物车记录数据
	 */
	private List<CartVO> findByCids(Integer[] cids){
		return mapper.findByCids(cids);
	}


	

}





