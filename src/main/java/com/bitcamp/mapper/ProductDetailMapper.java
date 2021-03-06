package com.bitcamp.mapper;

import java.util.HashMap;
import java.util.List;

import com.bitcamp.DTO.Product.ListDTO;
import com.bitcamp.DTO.Product.OptionDTO;
import com.bitcamp.DTO.member.MemberDTO;
import com.bitcamp.DTO.productdetail.BuyReviewDTO;
import com.bitcamp.DTO.productdetail.OrderResultDTO;
import com.bitcamp.DTO.productdetail.ProductDetailOptionListDTO;
import com.bitcamp.DTO.productdetail.QABoardDTO;

public interface ProductDetailMapper {
	public ListDTO productDetailGet(int list_no);
	public List<String> productDetailImgGet(int list_no);
	public List<ProductDetailOptionListDTO> productDetailOptionGet(int list_no);
	public MemberDTO productDetailArtistGet(String user_id);
	public Integer productDetailArtistBoardGet(int member_no);
	//
	public void productDelete(HashMap<String, Object> hashmap);
	public int productDeleteCheck(int list_no);
	//
	public void listOrderMemberNoDelete(int list_order_member_no);
	public int listOrderMemberNoDeleteCheck(int list_order_member_no);
	//
	public int productDetailQandAInsertSeq();
	public int productDetailQandAInsert(QABoardDTO qaboarddto);
	public int productDetailQandAInsertCheck(int qa_board_no);
	public int productDetailQandAListMaxCount(int list_no);
	public List<QABoardDTO> productDetailQandAList(QABoardDTO dto);
	//
	public List<OrderResultDTO> productDetailOrderResultList(List<Integer> list_order_member_no);
	//
	public int productDetailBuyReviewListMaxCount(int list_no);
	public List<BuyReviewDTO> productDetailBuyReviewList(BuyReviewDTO buyreviewdto);
	//
	public List<Integer> ScheduledListOrderMemberNoList();
	public List<Integer> ScheduledOrderMadeNoList();
	public void ScheduledListOrderMemberNoDelete(int list_order_member_no);
}
