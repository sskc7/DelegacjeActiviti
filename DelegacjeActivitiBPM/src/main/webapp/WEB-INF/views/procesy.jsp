<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Moje Procesy</title>
</head>
<body>
	<h2>Moje Procesy</h2>

	<table>
		<c:forEach items="${zadania}" var="item">
			<tr>
				<td><a href="proces?del=${item.id}">Delegacja nr<c:out
							value="${item.id}" /></a></td>

			</tr>
		</c:forEach>

	</table>

</body>
</html>