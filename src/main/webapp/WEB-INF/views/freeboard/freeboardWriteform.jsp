<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<!DOCTYPE html">
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<br>
	<form action="freeboardWriteResult" method="post">
	<input type="text" name="member_no" value="<sec:authentication property="principal.member.member_no"/>">
		<table>
			<tr>
				<td>제목</td>
				<td><input type="text" name="title" required="required"></td>
			</tr>
			<tr>
				<td>글쓴이</td>
				<td><input type="text" name="writer" value='<sec:authentication property="principal.member.user_id"/>'
					readonly="readonly"></td>
			</tr>
			<tr>
				<td>내용</td>
				<td><textarea rows="30" cols="50" name="content"
						required="required"></textarea></td>
			</tr>
		</table>
		<input type="button" value="목록으로"
			onclick="location.href='freeboardList'">
		<button type="submit">작성완료</button>
	</form>
</body>
</html>