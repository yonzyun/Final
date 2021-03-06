package com.bitcamp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bitcamp.DTO.Product.ListDTO;
import com.bitcamp.DTO.member.MemberDTO;
import com.bitcamp.DTO.order.OrderDTO;
import com.bitcamp.DTO.productdetail.OrderResultDTO;
import com.bitcamp.DTO.productdetail.QABoardDTO;
import com.bitcamp.service.ProductDetailService;

@Controller
public class ProductDetailController {
	
	@Autowired
	private ProductDetailService service;

	@RequestMapping("/productDetail/{list_no}")
	public String productDetail(@PathVariable int list_no ,HttpSession session ,Model model) {

		Map<String, Object> map = service.productDetailService(list_no);
		
		//만약 상품이 내려졋다면
		ListDTO listdto = (ListDTO) map.get("productDetail");
		if(listdto.getList_status() == 0) {
			Object objdto = session.getAttribute("member");
			
			//로그인이 안되어 있다면
			if(objdto != null) {
				MemberDTO memberdto = (MemberDTO) objdto;
				//관리자 계정이나 판매자계정이 아니라면
				if(memberdto.getUser_authority().equals("ROLE_ADMIN") && memberdto.getUser_authority().equals("ROLE_SELLER")) {
					return "redirect:/";				
				}
			}
			else {
				return "redirect:/";
			}
		}
		
		Object tmp_list_order_member_no = session.getAttribute("list_order_member_no");
		
		//고객 주문리스트가 있다면
		if(tmp_list_order_member_no != null) {
			session.removeAttribute("list_order_member_no");
			List<Integer> list_order_member_no = (List<Integer>)tmp_list_order_member_no;
			model.addAttribute("orderList", service.productDetailOrderService(list_order_member_no));
		}
		
		//로그인한사람이 해당 판매자거나 관리자 라면
		boolean productRemoveDecision = false;
		Object objmdto = session.getAttribute("member");
		if(objmdto != null) {
			MemberDTO mdto = (MemberDTO) objmdto;
			if(mdto.getUser_authority().equals("ROLE_ADMIN")) {
				productRemoveDecision = true;
			}
			int memberNo = mdto.getMember_no();
			Object objArtistInfo = map.get("productDetailArtistInfo");
			if(objArtistInfo != null) {
				MemberDTO adto = (MemberDTO) objArtistInfo;
				int artistMemberNo = adto.getMember_no();
				if(memberNo == artistMemberNo) {
					productRemoveDecision = true;
				}
			}
		}
		
		int artist_no = (int) map.get("productDetailArtistBoardNo");
	
		model.addAttribute("listDTO", map.get("productDetail"));
		model.addAttribute("imgList", map.get("productDetailImg"));
		model.addAttribute("optionList", map.get("productDetailOption"));
		model.addAttribute("qaBoardList", map.get("productDetailQABoardList"));
		model.addAttribute("artistInfo", map.get("productDetailArtistInfo"));
		model.addAttribute("artistBoardNo", artist_no);
		model.addAttribute("productRemoveDecision", productRemoveDecision);
		model.addAttribute("QACurrentPage", 1);
		model.addAttribute("buyReviewCurrentPage", 1);
		return "productdetail/productdetail.mall";
	}
	
	@ResponseBody
	@RequestMapping(value="/productDetail/ajaxProductToggle", produces="application/text; charset=utf-8")
	public String ajaxProductDelete(@RequestBody HashMap<String, Object> hashmap, HttpSession session) {
		String resultMessage = "로그인부터 하세요";
		Object objdto = session.getAttribute("member");
		if(objdto != null) {
			resultMessage = service.productActiveToggle(hashmap);
		}
		return resultMessage;
	}
	
	@ResponseBody
	@RequestMapping(value="ajaxListOrderMemberNoDelete", produces="application/text; charset=utf-8")
	public String ajaxListOrderMemberNoDelete(@RequestBody HashMap<String, Object> hashmap) {
		return service.listOrderMemberNoDeleteService(hashmap);
	}
	
	@RequestMapping("/ajaxqaboardinsert")
	public @ResponseBody String ajaxqaboardinsert(@RequestBody Map<String, Object> map, HttpSession session) {
		String insertResult = "-1";
		Object objmember = session.getAttribute("member");
		if(objmember != null) {
			MemberDTO memberdto = (MemberDTO)session.getAttribute("member");	
			
			int list_no = Integer.parseInt((String)map.get("list_no"));
			int member_no = memberdto.getMember_no();
			String qa_board_content =  (String) map.get("qa_content");
			int qa_board_secret = 0;
			if(map.get("qa_secret").equals(true)) {
				qa_board_secret = 1;
			}
			
			QABoardDTO dto = new QABoardDTO();
			dto.setList_no(list_no);
			dto.setMember_no(member_no);
			dto.setQa_board_content(qa_board_content);
			dto.setQa_board_secret(qa_board_secret);
			
			insertResult = Integer.toString(service.productDetailQandAInsertService(dto));
		}
		
		return insertResult;
	}
	
	@RequestMapping(value="/ajaxqaboardList", method= {RequestMethod.POST})
	public @ResponseBody Map<String, Object> ajaxqaboardList(@RequestBody Map<String, Object> map, HttpSession session) {
		Object objdto = session.getAttribute("member");
		if(objdto != null) {
			MemberDTO memberdto = (MemberDTO) objdto;
			map.put("member_no", memberdto.getMember_no());
		}
		
		return service.productDetailQandAListService(map);
	}
	
	@RequestMapping(value="/ajaxBuyReviewList", method= {RequestMethod.POST})
	public @ResponseBody Map<String, Object> ajaxBuyReviewList(@RequestBody Map<String, Object> map) {
		return service.productDetailBuyReviewListService(map);
	}
	
	@RequestMapping("/productDetailResult")
	public String productDetailResult(@RequestParam int list_no, @RequestParam int order_price, @RequestParam(defaultValue="0") List<Integer> order_add_option
			, @RequestParam(defaultValue="") List<String> option_name, @RequestParam(defaultValue="") List<Integer> order_amount, @RequestParam(defaultValue="") List<Integer> option_price
			, @RequestParam String list_title, @RequestParam(defaultValue="") List<Integer> list_order_member_no, HttpSession session) {
		OrderDTO orderdto = new OrderDTO();
		
		System.out.println("리스트 번호 : "+list_no);
		System.out.println("총 금액 : "+order_price);
		if(option_name.size() != 0) {
			System.out.print("옵션 번호 : ");
			for(int i=0; i<order_add_option.size(); i++) {
				System.out.print(order_add_option.get(i)+"/");
			}
			System.out.println();
			System.out.print("옵션 이름 : ");
			for(int i=0; i<option_name.size(); i++) {
				System.out.print(option_name.get(i)+"/");
			}
			System.out.println();
			System.out.print("옵션 수량 : ");
			for(int i=0; i<order_amount.size(); i++) {
				System.out.print(order_amount.get(i)+"/");
			}
			System.out.println();
			System.out.print("옵션 가격 : ");
			for(int i=0; i<option_price.size(); i++) {
				System.out.print(option_price.get(i)+"/");
			}
			
			//orderdto 1차대입
			orderdto.setOrder_add_option(order_add_option);
			orderdto.setOption_name(option_name);
			orderdto.setOrder_amount(order_amount);
			orderdto.setOrder_price(order_price);
		}
		
		System.out.println();
		System.out.print("작품 타이틀 : "+list_title);
		
		//orderdto 2차 대입
		orderdto.setList_no(list_no);
		orderdto.setOrder_price(order_price);
		orderdto.setList_title(list_title);
		
		List<Integer> list_order_no = new ArrayList<Integer>();
		List<String> order_value = new ArrayList<String>();
				
		//고객 주문리스트가 있다면
		if(list_order_member_no.size() != 0) {
			List<OrderResultDTO> orderresultdto = service.productDetailOrderService(list_order_member_no);
			for(int i=0; i<orderresultdto.size(); i++) {
				list_order_no.add(orderresultdto.get(i).getList_order_no());
				order_value.add(orderresultdto.get(i).getOrder_value());
			}
			
			System.out.println();
			System.out.print("고객주문 번호 : ");
			for(int i=0; i<list_order_member_no.size(); i++) {
				System.out.print(list_order_member_no.get(i)+"/");
			}
			System.out.println();
			System.out.print("고객값 번호 : ");
			for(int i=0; i<list_order_no.size(); i++) {
				System.out.print(list_order_no.get(i)+"/");
			}
			System.out.println();
			System.out.print("고객값 : ");
			for(int i=0; i<order_value.size(); i++) {
				System.out.print(order_value.get(i)+"/");
			}
			
			//orderdto 3차 대입
			orderdto.setOrdermade_no(list_order_member_no);
			orderdto.setList_order_no(list_order_no);
			orderdto.setOrder_value(order_value);
		}
		
		session.setAttribute("orderDTO", orderdto);
		
		return "redirect:/order/order/"+list_no;
	}
	
	@ResponseBody
	@RequestMapping(value="/ajaxCookieInsert", method=RequestMethod.POST)
	public String ajaxCookieInsert(@RequestBody Map<String, Object> map ) {
		String value = map.get("formData").toString();
		System.out.println("결과값 : "+value);
		
		String currentTime = Long.toString(System.currentTimeMillis());
		String CookieName = "cookie"+currentTime;
		Cookie cookieset = new Cookie(CookieName, value);
		cookieset.setMaxAge(60*60*24);
			
		return "성공";
	}
}
