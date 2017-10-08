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
<h2>Moje Delegacja</h2>
      <h5>Delegacja utworzona  <c:out value="${trainingDate}" /> </h5>

	  <h4>Dane Szczegółowe</h4>
      <h5>Projekt:   <c:out value="${projekt}" /></h5>
      <h5>Miejsce:   <c:out value="${miejsce}" /></h5>
      <h5>Data Rozpoczęcia:   <c:out value="${dataRozpoczecia}" /></h5>
      <h5>Data Zakończenia:   <c:out value="${dataZakonczenia}" /></h5>
      <h5>Transport:   <c:out value="${transport}" /></h5>
      <h5>Hotel:   <c:out value="${hotel}" /></h5>
      <h5>Cel:   <c:out value="${cel}" /></h5>
	  <h5>Email test <c:out value="${customerEamil}" /></h5>
       <c:forEach items="${events}" var="item">
    
      <h5><c:out value="${item.step}" />//<c:out value="${item.user}" />//<c:out value="${item.date}" /></h5>
    
    
  </c:forEach>   
  <table>
  <c:forEach items="${procesDane}" var="item">
    <tr>
      <td><c:out value="${item.name}" />   <c:out value="${item.textValue}" /></td>
    
    </tr>
  </c:forEach>
  </table> 
    <h2>Historia</h2>

 <c:forEach items="${historia}" var="item2">
    <tr>
      <td><c:out value="${item2.name}" />   <c:out value="${item2.assignee}" /><c:out value="${item2.startTime}" /><c:out value="${item2.endTime}" /></td></br>
    
    </tr>
  </c:forEach>
          
<td><a href="akceptacja?del=${param.del}">Zatwierdz</a></td>
    
   

</body>
</html>