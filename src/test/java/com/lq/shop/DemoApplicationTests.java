package com.lq.shop;

import com.google.common.collect.Lists;
import com.lq.shop.entity.UserEntity;
import com.lq.shop.vo.OrderItemVO;
import com.lq.shop.vo.OrderVO;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

	@Test
	public void contextLoads() {
	}

//	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(
		StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@Test
	public void test(){
		stringRedisTemplate.opsForValue().set("a","b",20000L);
		System.out.println(stringRedisTemplate.opsForValue().get("a"));
	}


	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RedisTemplate redisTemplate;

	@Test
	public void tests() throws Exception {
		stringRedisTemplate.opsForValue().set("aaa", "111");
		Assert.assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
	}

	@Test
	public void testObj() throws Exception {
		UserEntity user=new UserEntity();
		user.setId(123);
		user.setPassword("456");
		user.setEmail("123456");
//		ValueOperations<String, UserEntity> operations=redisTemplate.opsForValue();
		ValueOperations<String, String> operations=stringRedisTemplate.opsForValue();
//		operations.set("com.neox", user);

		operations.set("com.neo.f", "123",1000, TimeUnit.SECONDS);

//		Thread.sleep(1000);
//		com.lq.shop.UserEntity userEntity = (com.lq.shop.UserEntity) redisTemplate.opsForValue().get("com.neox");
		System.out.println(stringRedisTemplate.opsForValue().get("com.neo.f"));
//		System.out.println(userEntity);
		//redisTemplate.delete("com.neo.f");
		boolean exists=stringRedisTemplate.hasKey("com.neo.f");
		if(exists){
			System.out.println("exists is true");
		}else{
			System.out.println("exists is false");
		}
		// Assert.assertEquals("aa", operations.get("com.neo.f").getUserName());
	}


	@Test
	public void test2() throws Exception {
		OrderVO orderVO=new OrderVO();
		OrderItemVO orderItemVO = new OrderItemVO();
		List<OrderItemVO> objects = Lists.newArrayList();
		objects.add(orderItemVO);
		orderVO.setOrderItemVOList(objects);

//		ValueOperations<String, OrderVO> operations=redisTemplate.opsForValue();
		ValueOperations<String, OrderVO> operations=redisTemplate.opsForValue();
		operations.set("com.neox", orderVO);

		operations.set("com.neo.f", orderVO,1000, TimeUnit.SECONDS);

		System.out.println(stringRedisTemplate.opsForValue().get("com.neox"));

//		Thread.sleep(1000);
//		OrderVO orderVO1 = operations.get("com.neox");
//		System.out.println(orderVO1);
//		stringRedisTemplate.opsForValue().set("com.neo.d",JsonUtil.obj2String(orderVO));
//
//		OrderVO orderVO1 = JsonUtil
//			.string2Obj(stringRedisTemplate.opsForValue().get("com.neo.d"), OrderVO.class);

//		System.out.println(orderVO1);
//		System.out.println(stringRedisTemplate.opsForValue().get("com.neo.d"));
//		System.out.println(stringRedisTemplate.opsForValue().get("com.neox"));
		//redisTemplate.delete("com.neo.f");
		boolean exists=redisTemplate.hasKey("com.neo.f");
		if(exists){
			System.out.println("exists is true");
		}else{
			System.out.println("exists is false");
		}
		// Assert.assertEquals("aa", operations.get("com.neo.f").getUserName());
	}

}
