<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Moje claim</title>
</head>
<body>
	<h2>Moje claim</h2>
	<h5>
		Delegacja utworzona
		<c:out value="${trainingDate}" />
	</h5>

	<h4>Dane Szczegółowe</h4>
	<h5>
		Projekt:
		<c:out value="${projekt}" />
	</h5>
	<h5>
		Miejsce:
		<c:out value="${miejsce}" />
	</h5>
	<h5>
		Data Rozpoczęcia:
		<c:out value="${dataRozpoczecia}" />
	</h5>
	<h5>
		Data Zakończenia:
		<c:out value="${dataZakonczenia}" />
	</h5>
	<h5>
		Transport:
		<c:out value="${transport}" />
	</h5>
	<h5>
		Hotel:
		<c:out value="${hotel}" />
	</h5>
	<h5>
		Cel:
		<c:out value="${cel}" />
	</h5>
	<h5>
		Email test
		<c:out value="${customerEamil}" />
	</h5>


	<td><a href="przypisz?del=${param.del}">Przypisz</a></td>



</body>
</html>