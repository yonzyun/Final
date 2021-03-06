<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Insert title here</title>
<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
<!-- 합쳐지고 최소화된 최신 CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
<!-- 부가적인 테마 -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css">
<!-- 합쳐지고 최소화된 최신 자바스크립트 -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
<link
	href="https://fonts.googleapis.com/css?family=Comfortaa&display=swap"
	rel="stylesheet">
<style>
* {
	font-family: 'Comfortaa', '맑은 고딕', cursive;
}

form {
	padding: 1%;
}

form * {
	margin: 5px;
}

.row {
	display: flex;
	justify-content: center;
}

.btn {
	width: 100px;
}
</style>
</head>
<body>
	<form method="post"
		action="/updateQa_board_contentResult/${qABoardDTO.getQa_board_no() }">
		<input type="hidden" name="qAndA" value="${qAndA }">
		<textarea id="qa_board_content" name="qa_board_content" rows="10"
			class="form-control">${qABoardDTO.getQa_board_content() }</textarea>
		<div class="row">
			<input type="submit" value="수정" class="btn btn-default">
		</div>
	</form>
</body>
</html>