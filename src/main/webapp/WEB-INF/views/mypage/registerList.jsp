<%@page import="com.bitcamp.DTO.Product.ListDTO"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<table>
		<thead>
			<tr>
				<th>상품번호</th>
				<th>상품명</th>
				<th>등록일자</th>
				<th>상태</th>
				<th>기본가</th>
				<th>주문수량</th>
			</tr>
		</thead>
		<tbody>
			<%
				List<ListDTO> registerList = (List<ListDTO>) request.getAttribute("registerList");
				for (int i = 0; i < registerList.size(); i++) {
			%>
			<tr>
				<td><%=registerList.get(i).getList_no()%></td>
				<td><%=registerList.get(i).getList_title()%></td>
				<td><%=registerList.get(i).getList_date()%></td>
				<td><%=registerList.get(i).getList_status()%></td>
				<td><%=registerList.get(i).getList_base_price()%></td>
				<td><%=registerList.get(i).getBuy_count()%></td>
			</tr>
			<%
				}
			%>
		</tbody>
	</table>
</body>
</html>