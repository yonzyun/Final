<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
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

.container {
	background-color: #F0E5DE;
	margin: 5%;
	padding: 5%;
	border-radius: 5%;
}

.row {
	display: flex;
	flex-direction: row;
	flex-wrap: wrap;
	align-items: stretch;
}

.left {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.left img {
	width: 75%;
}

.line {
	width: 5px;
	height: 100%;
	background-color: #D9D4CF;
	margin: 0 auto;
}

.right {
	display: flex;
	flex-direction: row;
	justify-content: center;
	padding: 5%;
}

.right ul {
	display: flex;
	flex-direction: column;
	justify-content: space-around;
	padding: 15px;
	list-style: none;
}

.right ul li span {
	margin: 5px;
}

.right ul li a {
	color: #7C7877 !important;
	font-size: 15px;
}
</style>
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="col-xs-6 col-xs-offset-3 col-md-4 col-md-offset-1 left">
				<sec:authorize access="!hasRole('ROLE_SELLER')">
					<h3>구매자</h3>
					<img alt="buyer" src="/resources/image/mypage/customer.png">
				</sec:authorize>
				<sec:authorize access="hasRole('ROLE_SELLER')">
					<h3>판매자</h3>
					<img alt="seller" src="/resources/image/mypage/craftsman.png">
				</sec:authorize>
				<sec:authentication property="principal.member.user_id"
					var="user_id" />
				<h3>${user_id }<small class="text-muted">고객님 환영합니다.</small>
				</h3>
			</div>
			<div class="col-md-2 visible-md visible-lg">
				<div class="line"></div>
			</div>
			<div class="col-xs-6 col-xs-offset-3 col-md-4 right col-md-offset-0">
				<ul>
					<li><span class="glyphicon glyphicon-pencil"></span><a
						href="pWCheck">회원 정보 수정</a></li>
					<li><span class="glyphicon glyphicon-pencil"></span><a
						href="buyList">나의 구매 내역</a></li>
					<li><span class="glyphicon glyphicon-pencil"></span><a
						href="cQAList">나의 고객 문의</a></li>
					<li><span class="glyphicon glyphicon-pencil"></span><a
						href="buyerPQAList">나의 상품 문의</a></li>
					<li><span class="glyphicon glyphicon-pencil"></span><a
						href="buyerReviewList">나의 상품 후기</a></li>
				</ul>
				<sec:authorize access="hasRole('ROLE_SELLER')">
					<ul>
						<li><span class="glyphicon glyphicon-barcode"></span><a
							href="registerList">나의 등록 내역</a></li>
						<li><span class="glyphicon glyphicon-barcode"></span><a
							href="sellList">나의 판매 내역</a></li>
						<li><span class="glyphicon glyphicon-barcode"></span><a
							href="sellerPQAList">상품 문의 관리</a></li>
						<li><span class="glyphicon glyphicon-barcode"></span><a
							href="sellerReviewList">상품 후기 관리</a></li>
					</ul>
				</sec:authorize>
			</div>
		</div>
	</div>
</body>
</html>