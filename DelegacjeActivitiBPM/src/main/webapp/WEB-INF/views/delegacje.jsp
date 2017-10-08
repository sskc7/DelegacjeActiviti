<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Moje Delegacje</title>
</head>
<body>
<h2>Moje Delegacje</h2>

<table>
  <c:forEach items="${zadania}" var="item">
    <tr>
      <td><a href="delegacja?del=${item.id}">Delegacja nr<c:out value="${item.id}" /></a></td>
    
    </tr>
  </c:forEach>
    
</table>

</body>
</html>