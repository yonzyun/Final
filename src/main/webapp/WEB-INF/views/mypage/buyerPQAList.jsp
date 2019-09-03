<%@page import="com.bitcamp.DTO.productdetail.QABoardDTO"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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

/* Style the buttons that are used to open and close the accordion panel */
.accordion {
	background-color: #eee;
	color: #444;
	cursor: pointer;
	padding: 18px;
	width: 100%;
	text-align: left;
	border: none;
	outline: none;
	transition: 0.4s;
}

/* Add a background color to the button if it is clicked on (add the .active class with JS), and when you move the mouse over it (hover) */
.active, .accordion:hover {
	background-color: #ccc;
}

/* Style the accordion panel. Note: hidden by default */
.panel {
	padding: 0 18px;
	background-color: white;
	display: none;
	overflow: hidden;
}
</style>
</head>
<body>
	<table>
		<thead>
			<tr>
				<th style="display: none;">번호</th>
				<th>상품명</th>
				<th>내용</th>
				<th>문의일</th>
				<th>상태</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="list" items="${buyerPQAList }" varStatus="status">
				<tr class="accordion">
					<td style="display: none;">${list.qa_board_no }</td>
					<td><a href="productDetail/${list.list_no }">${list_title_list[status.index]}</a></td>
					<td>${list.qa_board_content }</td>
					<td>${list.qa_board_date }</td>
					<td>${list.qa_board_status }</td>
					<td><c:if test="${list.qa_board_status == '미답변' }">
							<button
								class="btn btn-default btn-block answerpadding ifanswered"
								value="${list.qa_board_no }" data-toggle="modal"
								data-target="#myModal">수정</button>
						</c:if></td>
					<td><c:if test="${list.qa_board_status == '미답변' }">
							<button
								class="btn btn-default btn-block answerpadding delete_question_btn"
								value="${list.qa_board_no }">삭제</button>
						</c:if></td>
				</tr>
				<c:if test="${list.qa_board_status == '답변' }">
					<tr class="panel">
						<td colspan="4" id="answer${list.qa_board_no }"></td>
					</tr>
				</c:if>
			</c:forEach>
		</tbody>
	</table>
	<div id="divPage">
		<form class="formPage" action="/buyerPQAList" method="post">
			<c:if test="${paging.startblock > 1 }">
				<a href="#">◀</a>
			</c:if>
			<c:forEach var="i" begin="${paging.startblock }"
				end="${paging.endblock }">
				<c:if test="${i == currpage }">
					<c:out value="${i }"></c:out>
				</c:if>
				<c:if test="${i != currpage }">
					<input type="submit" class="btn btn-default" name="curr"
						value="${i }">
				</c:if>
			</c:forEach>
			<c:if test="${paging.endblock < paging.totalpage }">
				<a href="#">▶</a>
			</c:if>
		</form>
	</div>
	<div class="modal fade" id="myModal">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title">수정하기</h4>
				</div>
				<div class="modal-body" id="updateQa_board_content"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">닫기</button>
				</div>
			</div>
		</div>
	</div>
	<script>
		var acc = document.getElementsByClassName("accordion");
		var i;

		for (i = 0; i < acc.length; i++) {
			acc[i].addEventListener("click", function() {
				if ($(this).children().eq(4).text() == "답변") {
					/* Toggle between adding and removing the "active" class,
					to highlight the button that controls the panel */
					this.classList.toggle("active");

					/* Toggle between hiding and showing the active panel */
					var panel = this.nextElementSibling;
					if (panel.style.display === "block") {
						panel.style.display = "none";
					} else {
						panel.style.display = "block";
					}
				}
			});
		}

		for (i = 0; i < acc.length; i++) {
			acc[i].addEventListener("click", function() {
				if ($(this).children().eq(4).text() == "답변") {
					var d = $(this).find('td:eq(0)').text();
					var e = $(this).children().eq(0).text();
					$.ajax({
						url : "/findPA",
						data : {
							qa_board_no : d
						},
						dataType : "text",
						type : "post",
						success : function(data) {
							/* if (!data) { */
							/* $('#answer' + d).empty(); */
							$('#answer' + d).append(data);
							/* } */
						},
						error : function(data) {
							alert("error");
						}
					});
				}
			}, {
				once : true
			});
		}

		// '수정' 버튼
		$('.ifanswered').click(function() {
			$.ajax({
				url : "/updateQa_board_content/" + $(this).val(),
				type : "GET",
				dataType : "html",
				success : function(data) {
					$('#updateQa_board_content').empty();
					$('#updateQa_board_content').append(data);
				},
				error : function(data) {
					alert("error");
				}
			});
		});

		// '삭제' 버튼
		$('.delete_question_btn').click(
				function() {
					if (confirm('정말로 삭제하시겠습니까?')) {
						alert("삭제가 완료되었습니다.");
						location.href = "/updateQa_board_delete_status/"
								+ $(this).val();
					} else {
						alert("삭제를 취소하였습니다.");
					}
					return false;
				});
	</script>
</body>
</html>