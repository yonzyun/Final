package com.bitcamp.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bitcamp.DAO.CustomUser;
import com.bitcamp.DTO.Product.ListDTO;
import com.bitcamp.DTO.comm.PageDTO;
import com.bitcamp.DTO.customerqaboard.CustomerQABoardDTO;
import com.bitcamp.DTO.freeboard.FreeboardDTO;
import com.bitcamp.DTO.member.MemberDTO;
import com.bitcamp.DTO.mypage.OrderOrderDTO;
import com.bitcamp.DTO.order.OrderDTO;
import com.bitcamp.DTO.productdetail.BuyReviewDTO;
import com.bitcamp.DTO.productdetail.QABoardDTO;
import com.bitcamp.VO.file.FileVO;
import com.bitcamp.service.ArtistService;
import com.bitcamp.service.CustomUserDetailService;
import com.bitcamp.service.MyPageService;

import lombok.Setter;

@Controller
public class MyPageController {
	@Autowired
	private MyPageService service;

	@Resource
	private CustomUserDetailService userService;

	@Autowired
	private ArtistService artistService;

	@Setter(onMethod_ = @Autowired)
	private PasswordEncoder pwdEncoder;

	@RequestMapping("myPage")
	public String myPage(Principal prin, HttpSession session) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		if (memberDTO != null) {
			return "mypage/myPage.mall";
		} else {
			return "redirect:/login";
		}
	}

	@RequestMapping("pWCheck")
	public String pWCheck(HttpSession session) {
		return "mypage/pWCheck.mall";
	}

	@RequestMapping("pWCheckResult")
	public String pWCheckResult(Principal prin, HttpSession session, @RequestParam String password,
			RedirectAttributes redirectattributes) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		boolean result = pwdEncoder.matches(password, memberDTO.getUser_password());
		if (result) {
			return "redirect:/userInfo";
		} else {
			redirectattributes.addFlashAttribute("msg", "비밀번호를 잘못 입력하셨습니다.");
			return "redirect:/pWCheck";
		}
	}

	@RequestMapping("userInfo")
	public String userInfo(Principal prin, HttpSession session, Model model) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		model.addAttribute("memberDTO", memberDTO);
		return "mypage/userInfo.mall";
	}

	@RequestMapping("userInfoResult")
	public String userInfoResult(Principal prin, HttpSession session, @RequestParam(value = "password") String password,
			@RequestParam(value = "user_name") String user_name, @RequestParam(value = "user_nick") String user_nick,
			@RequestParam(value = "user_email") String user_email, @RequestParam String address1,
			@RequestParam String address2, @RequestParam(value = "user_call") String user_call) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		String user_address = address1 + " " + address2;
		memberDTO.setUser_name(user_name);
		memberDTO.setUser_nick(user_nick);
		memberDTO.setUser_email(user_email);
		memberDTO.setUser_address(user_address);
		memberDTO.setUser_call(user_call);
		System.out.println(password);
		if (password != "") {
			String newPwd = pwdEncoder.encode(password);
			System.out.println(newPwd);
			service.updateUser_password(memberDTO.getMember_no(), newPwd);
		}
		service.updateUserInfo(memberDTO);
		return "redirect:/myPage";
	}

	@RequestMapping("customerQA")
	public String customerQA(HttpSession session) {
		return "mypage/customerQA.mall";
	}

	@RequestMapping("customerQAResult")
	public String customerQAResult(Principal prin, HttpSession session, @RequestParam String question_type,
			@RequestParam String question_title, @RequestParam String question_content) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		service.insertCQA(memberDTO.getMember_no(), question_type, question_title, question_content);
		return "redirect:cQAList";
	}

	@RequestMapping("withdraw")
	public String withdraw(Principal prin, HttpSession session) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		service.withdraw(memberDTO.getUser_id());
		return "redirect:login/logout";
	}

	@RequestMapping("buyList")
	public String buyList(Principal prin, HttpSession session, Model model,
			@RequestParam(required = false) String curr) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();

		Map<String, Object> listMap = new HashMap<>();
		listMap.put("member_no", memberDTO.getMember_no());
		// 페이징
		int totalCount = service.getBuyCount(listMap);
		int currpage = 1;
		if (curr != null)
			currpage = Integer.parseInt(curr);
		int pagepercount = 10;
		int blockSize = 10;

		PageDTO page = new PageDTO(currpage, totalCount, pagepercount, blockSize);
		listMap.put("startrow", page.getStartrow());
		listMap.put("endrow", page.getEndrow());

		Map<String, Object> parameters = service.getBuyList(listMap);
		List<OrderDTO> buyList = (List<OrderDTO>) parameters.get("buyList");
		List<String> buyListImage_loc = (List<String>) parameters.get("buyListImage_loc");
		List<List<OrderOrderDTO>> orderOrderList2 = (List<List<OrderOrderDTO>>) parameters.get("orderOrderList2");
		//

		model.addAttribute("buyList", buyList);
		model.addAttribute("buyListImage_loc", buyListImage_loc);
		model.addAttribute("orderOrderList2", orderOrderList2);
		model.addAttribute("paging", page);
		System.out.println("a-" + orderOrderList2);
		return "mypage/buyList.mall";
	}

	@RequestMapping("cQAList")
	public String cQAList(Principal prin, HttpSession session, Model model,
			@RequestParam(required = false) String curr) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();

		Map<String, Object> listMap = new HashMap<>();
		listMap.put("member_no", memberDTO.getMember_no());
		// 페이징
		int totalCount = service.getCQACount(listMap);
		int currpage = 1;
		if (curr != null)
			currpage = Integer.parseInt(curr);
		int pagepercount = 10;
		int blockSize = 10;

		PageDTO page = new PageDTO(currpage, totalCount, pagepercount, blockSize);
		listMap.put("startrow", page.getStartrow());
		listMap.put("endrow", page.getEndrow());

		List<CustomerQABoardDTO> cQAList = service.getCQAList(listMap);
		//

		model.addAttribute("cQAList", cQAList);
		model.addAttribute("paging", page);
		return "mypage/cQAList.mall";
	}

	@RequestMapping("buyerPQAList")
	public String buyerPQAList(Principal prin, HttpSession session, Model model,
			@RequestParam(required = false) String curr) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();

		Map<String, Object> listMap = new HashMap<>();
		listMap.put("member_no", memberDTO.getMember_no());
		// 페이징
		int totalCount = service.getBuyerPQACount(listMap);
		int currpage = 1;
		if (curr != null)
			currpage = Integer.parseInt(curr);
		int pagepercount = 10;
		int blockSize = 10;

		PageDTO page = new PageDTO(currpage, totalCount, pagepercount, blockSize);
		listMap.put("startrow", page.getStartrow());
		listMap.put("endrow", page.getEndrow());

		Map<String, Object> parameters = service.getBuyerPQAList(listMap);
		List<QABoardDTO> buyerPQAList = (List<QABoardDTO>) parameters.get("buyerPQAList");
		List<String> list_title_list = (List<String>) parameters.get("list_title_list");
		//

		model.addAttribute("buyerPQAList", buyerPQAList);
		model.addAttribute("list_title_list", list_title_list);
		model.addAttribute("paging", page);
		return "mypage/buyerPQAList.mall";
	}

	@RequestMapping("buyerReviewList")
	public String buyerReviewList(Principal prin, HttpSession session, Model model,
			@RequestParam(required = false) String curr) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();

		Map<String, Object> listMap = new HashMap<>();
		listMap.put("member_no", memberDTO.getMember_no());
		// 페이징
		int totalCount = service.getBuyerReviewCount(listMap);
		int currpage = 1;
		if (curr != null)
			currpage = Integer.parseInt(curr);
		int pagepercount = 10;
		int blockSize = 10;

		PageDTO page = new PageDTO(currpage, totalCount, pagepercount, blockSize);
		listMap.put("startrow", page.getStartrow());
		listMap.put("endrow", page.getEndrow());

		Map<String, Object> parameters = service.getBuyerReviewList(listMap);
		List<BuyReviewDTO> buyerReviewList = (List<BuyReviewDTO>) parameters.get("buyerReviewList");
		List<String> list_title_list = (List<String>) parameters.get("list_title_list");
		//

		model.addAttribute("buyerReviewList", buyerReviewList);
		model.addAttribute("list_title_list", list_title_list);
		model.addAttribute("paging", page);
		return "mypage/buyerReviewList.mall";
	}

	@RequestMapping("registerList")
	public String registerList(Principal prin, HttpSession session, Model model,
			@RequestParam(required = false) String curr) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();

		Map<String, Object> listMap = new HashMap<>();
		listMap.put("user_id", memberDTO.getUser_id());
		// 페이징
		int totalCount = service.getRegisterCount(listMap);
		int currpage = 1;
		if (curr != null)
			currpage = Integer.parseInt(curr);
		int pagepercount = 10;
		int blockSize = 10;

		PageDTO page = new PageDTO(currpage, totalCount, pagepercount, blockSize);
		listMap.put("startrow", page.getStartrow());
		listMap.put("endrow", page.getEndrow());

		List<ListDTO> registerList = service.getRegisterList(listMap);
		//

		model.addAttribute("registerList", registerList);
		model.addAttribute("paging", page);
		return "mypage/registerList.mall";
	}

	@RequestMapping("sellList")
	public String sellList(Principal prin, HttpSession session, Model model,
			@RequestParam(required = false) String curr) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();

		Map<String, Object> listMap = new HashMap<>();
		listMap.put("user_id", memberDTO.getUser_id());
		// 페이징
		int totalCount = service.getSellCount(listMap);
		int currpage = 1;
		if (curr != null)
			currpage = Integer.parseInt(curr);
		int pagepercount = 10;
		int blockSize = 10;

		PageDTO page = new PageDTO(currpage, totalCount, pagepercount, blockSize);
		listMap.put("startrow", page.getStartrow());
		listMap.put("endrow", page.getEndrow());

		Map<String, Object> parameters = service.getSellList(listMap);
		List<OrderDTO> sellList = (List<OrderDTO>) parameters.get("sellList");
		List<MemberDTO> buyerList = (List<MemberDTO>) parameters.get("buyerList");
		List<List<OrderOrderDTO>> orderOrderList2 = (List<List<OrderOrderDTO>>) parameters.get("orderOrderList2");
		//

		model.addAttribute("sellList", sellList);
		model.addAttribute("buyerList", buyerList);
		model.addAttribute("orderOrderList2", orderOrderList2);
		model.addAttribute("paging", page);
		return "mypage/sellList.mall";
	}

	@RequestMapping("sellerPQAList")
	public String sellerPQAList(Principal prin, HttpSession session, Model model,
			@RequestParam(required = false) String curr) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();

		Map<String, Object> listMap = new HashMap<>();
		listMap.put("member_no", memberDTO.getMember_no());
		listMap.put("user_id", memberDTO.getUser_id());
		// 페이징
		int totalCount = service.getSellerPQACount(listMap);
		int currpage = 1;
		if (curr != null)
			currpage = Integer.parseInt(curr);
		int pagepercount = 10;
		int blockSize = 10;

		PageDTO page = new PageDTO(currpage, totalCount, pagepercount, blockSize);
		listMap.put("startrow", page.getStartrow());
		listMap.put("endrow", page.getEndrow());

		Map<String, Object> parameters = service.getSellerPQAList(listMap);
		List<QABoardDTO> sellerPQAList = (List<QABoardDTO>) parameters.get("sellerPQAList");
		List<String> list_title_list = (List<String>) parameters.get("list_title_list");
		//

		model.addAttribute("sellerPQAList", sellerPQAList);
		model.addAttribute("list_title_list", list_title_list);
		model.addAttribute("paging", page);
		return "mypage/sellerPQAList.mall";
	}

	@RequestMapping("sellerReviewList")
	public String sellerReviewList(Principal prin, HttpSession session, Model model,
			@RequestParam(required = false) String curr) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();

		Map<String, Object> listMap = new HashMap<>();
		listMap.put("user_id", memberDTO.getUser_id());
		// 페이징
		int totalCount = service.getSellerReviewCount(listMap);
		int currpage = 1;
		if (curr != null)
			currpage = Integer.parseInt(curr);
		int pagepercount = 10;
		int blockSize = 10;

		PageDTO page = new PageDTO(currpage, totalCount, pagepercount, blockSize);
		listMap.put("startrow", page.getStartrow());
		listMap.put("endrow", page.getEndrow());

		Map<String, Object> parameters = service.getSellerReviewList(listMap);
		List<BuyReviewDTO> sellerReviewList = (List<BuyReviewDTO>) parameters.get("sellerReviewList");
		List<String> list_title_list = (List<String>) parameters.get("list_title_list");
		//

		model.addAttribute("sellerReviewList", sellerReviewList);
		model.addAttribute("list_title_list", list_title_list);
		model.addAttribute("paging", page);
		return "mypage/sellerReviewList.mall";
	}

	// Completion of receipt
	@RequestMapping("cor/{order_no}")
	public String cor(Principal prin, HttpSession session, @PathVariable int order_no) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		service.cor(order_no);
		return "redirect:/buyList";
	}

	// Review writing
	@RequestMapping("rw")
	public String rw(Principal prin, HttpSession session, @RequestParam int order_no, Model model) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		// OrderDTO orderDTO = service.findOrderDTO(order_no);
		model.addAttribute("order_no", order_no);
		return "mypage/insertBuyReview";
	}

	@RequestMapping(value = "/ajaxBuyReviewImgUpload", method = { RequestMethod.POST })
	@ResponseBody
	public FileVO ajaxBuyReviewImgUpload(HttpSession session, MultipartFile[] uploadFile) {
		FileVO filevo = new FileVO();

		if (uploadFile.length != 0) {
			String buyReviewImgFolder = session.getServletContext().getRealPath("/resources/image/buyReviewImg");
			UUID uuid = UUID.randomUUID();
			String fileName = uuid.toString() + "-" + uploadFile[0].getOriginalFilename();
			filevo.setFileName(fileName);
			filevo.setUploadPath(buyReviewImgFolder);
			filevo.setUuid(uuid.toString());
			try {
				File file = new File(buyReviewImgFolder, fileName);
				uploadFile[0].transferTo(file);
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		return filevo;
	}

	@RequestMapping("/buyReviewResult")
	public String buyReviewResult(@RequestParam int BuyReviewScore, @RequestParam String BuyReviewContent,
			@RequestParam String BuyReviewImg, @RequestParam String order_no) {
		int order_noInt = Integer.parseInt(order_no);
		BuyReviewDTO buyreviewdto = new BuyReviewDTO();
		buyreviewdto.setOrder_no(order_noInt);
		buyreviewdto.setBuy_review_content(BuyReviewContent);
		buyreviewdto.setBuy_review_score(BuyReviewScore);
		if (BuyReviewImg != null) {
			buyreviewdto.setBuy_review_image_loc(BuyReviewImg);
		}
		int insertResult = service.buyReviewInsertService(buyreviewdto);
		if (insertResult == 1) {
			System.out.println("등록에 성공했습니다.");
			artistService.artistScoreCalculation(order_noInt, BuyReviewScore); // 등록성공시 작가페이지 별점계산
		} else {
			System.out.println("등록에 실패했습니다.");
		}
		return "redirect:/buyList";
	}

	@RequestMapping(value = "/findPA", produces = "application/json; charset=utf8")
	@ResponseBody
	public QABoardDTO findPA(Principal prin, HttpSession session, @RequestParam int qa_board_no, Model model) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		QABoardDTO qABoardDTO = service.findPA(qa_board_no);
		return qABoardDTO;
	}

	// @RequestMapping(value = "/findPAN", produces = "application/text;
	// charset=utf8")
	// @ResponseBody
	// public int findPAN(Principal prin, HttpSession session, @RequestParam int
	// qa_board_no, Model model) {
	// // MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
	// CustomUser user = (CustomUser)
	// userService.loadUserByUsername(prin.getName());
	// MemberDTO memberDTO = user.getMember();
	// QABoardDTO qABoardDTO = service.findPA(qa_board_no);
	// int newQa_board_no = qABoardDTO.getQa_board_no();
	// return newQa_board_no;
	// }

	@RequestMapping("/updateQa_board_content/{qa_board_no}")
	public String updateQa_board_content(Principal prin, HttpSession session, @PathVariable int qa_board_no,
			Model model) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		QABoardDTO qABoardDTO = service.findQABoardDTO(qa_board_no);
		if (qABoardDTO.getQa_board_parent_no() == 0) {
			model.addAttribute("qAndA", "question");
		} else {
			model.addAttribute("qAndA", "answer");
		}
		model.addAttribute("qABoardDTO", qABoardDTO);
		return "mypage/updateQa_board_content";
	}

	@RequestMapping("updateQa_board_contentResult/{qa_board_no}")
	public String updateQa_board_contentResult(Principal prin, HttpSession session, @PathVariable int qa_board_no,
			@RequestParam String qAndA, @RequestParam String qa_board_content, Model model) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		service.updateQa_board_content(qa_board_no, qa_board_content);
		String redirect;
		if ("question".equals(qAndA)) {
			redirect = "redirect:/buyerPQAList";
		} else {
			redirect = "redirect:/sellerPQAList";
		}
		return redirect;
	}

	@RequestMapping("/updateQa_board_delete_status/{qa_board_no}")
	public String updateQa_board_delete_status(Principal prin, HttpSession session, @PathVariable int qa_board_no,
			Model model) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		QABoardDTO qABoardDTO = service.findQABoardDTO(qa_board_no);
		QABoardDTO ParentQABoardDTO = service.findPA(qa_board_no);
		if (ParentQABoardDTO == null) {
			service.updateQa_board_delete_status(qa_board_no);
			return "redirect:/buyerPQAList";
		} else {
			service.updateQa_board_delete_status(ParentQABoardDTO.getQa_board_no());
			service.updateQa_board_statusU(qa_board_no);
			return "redirect:/sellerPQAList";
		}
	}

	@RequestMapping("/updateBuy_review_content/{buy_review_no}")
	public String updateBuy_review_content(Principal prin, HttpSession session, @PathVariable int buy_review_no,
			Model model) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		BuyReviewDTO buyReviewDTO = service.findBuyReviewDTO(buy_review_no);
		model.addAttribute("buyReviewDTO", buyReviewDTO);
		return "mypage/updateBuy_review_content";
	}

	@RequestMapping("updateBuy_review_contentResult/{buy_review_no}")
	public String updateBuy_review_contentResult(Principal prin, HttpSession session, @PathVariable int buy_review_no,
			@RequestParam String buy_review_content, Model model) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		service.updateBuy_review_content(buy_review_no, buy_review_content);
		return "redirect:/buyerReviewList";
	}

	@RequestMapping("/updateBuy_review_status/{buy_review_no}")
	public String updateBuy_review_status(Principal prin, HttpSession session, @PathVariable int buy_review_no,
			Model model) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		service.updateBuy_review_status(buy_review_no);
		return "redirect:/buyerReviewList";
	}

	@RequestMapping("/updateList_status/{list_no}")
	public String updateList_status(Principal prin, HttpSession session, @PathVariable int list_no, Model model) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		service.updateList_status(list_no);
		return "redirect:/registerList";
	}

	// Shipment processing
	@RequestMapping("sp/{order_no}")
	public String sp(Principal prin, HttpSession session, @PathVariable int order_no) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		service.sp(order_no);
		return "redirect:/sellList";
	}

	@RequestMapping("insertPQAResult")
	public String insertPQAResult(Principal prin, HttpSession session, @RequestParam String list_no,
			@RequestParam String qa_board_content, @RequestParam String qa_board_parent_no) {
		// MemberDTO memberDTO = (MemberDTO) session.getAttribute("member");
		CustomUser user = (CustomUser) userService.loadUserByUsername(prin.getName());
		MemberDTO memberDTO = user.getMember();
		int list_noInt = Integer.parseInt(list_no);
		int qa_board_parent_noInt = Integer.parseInt(qa_board_parent_no);
		service.insertPQA(list_noInt, memberDTO.getMember_no(), qa_board_content, qa_board_parent_noInt);
		return "redirect:/sellerPQAList";
	}
}
