package com.bitcamp.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bitcamp.DTO.admin.MainViewDTO;
import com.bitcamp.DTO.member.MemberDTO;
import com.bitcamp.comm.DBConfigration;
import com.bitcamp.mapper.AdminMapper;
import com.bitcamp.mapper.TestMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DBConfigration.class})
public class JunitTest {
	
	@Autowired
	private SqlSessionTemplate sqlsession;
	@Autowired
	private AdminMapper tempmapper;
	
//	@Test
//	public void test() {
///*		
// 		MainViewDTO mainview = new MainViewDTO();
//		mainview.setMain_view_no(2);
//		mainview.setMain_view_name("test");
//		mainview.setMain_view_use(1);
//		mainview.setMain_view_product("products");
//		int result = mapper.insertTest(mainview);
//		assertEquals(1, result);
//*/
//		List<MainViewDTO> list = mapper.selectTest();
//		assertEquals("products", list.get(0).getMain_view_product());
//	}
	@Test
	public void sqlTest() {
		assertNotNull(sqlsession);
	}
	@Test
	public void temptest() {
		HashMap<String, Object> test = new HashMap<>();
		test.put("user_name", "요");
		test.put("search_date", "2019-08");
		test.put("startrow", 1);
		test.put("endrow", 10);
		List<MemberDTO> dto = tempmapper.getMemberList(test);
		System.out.println(dto.toString());
	}
}
