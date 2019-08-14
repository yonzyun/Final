package com.bitcamp.mapper;

import java.util.List;

import com.bitcamp.DTO.Product.ListDTO;
import com.bitcamp.DTO.Product.OptionDTO;
import com.bitcamp.DTO.member.MemberDTO;
import com.bitcamp.DTO.productdetail.BuyReviewDTO;
import com.bitcamp.DTO.productdetail.OrderResultDTO;
import com.bitcamp.DTO.productdetail.QABoardDTO;

public interface ProductDetailMapper {
	public ListDTO productDetailGet(int list_no);
	public List<String> productDetailImgGet(int list_no);
	public List<OptionDTO> productDetailOptionGet(int list_no);
	public MemberDTO productDetailArtistGet(String list_artist);
	public int productDetailQandAInsertSeq();
	public int productDetailQandAInsert(QABoardDTO qaboarddto);
	public int productDetailQandAInsertCheck(int qa_board_no);
	public List<QABoardDTO> productDetailQandAList(QABoardDTO dto);
	public List<OrderResultDTO> productDetailOrderResultList(List<Integer> list_order_member_no);
	public int buyReviewInsertSeq();
	public void buyReviewInsert(BuyReviewDTO buyreviewdto);
	public int buyReviewInsertCheck(int buy_review_no);
}
