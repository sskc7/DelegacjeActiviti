<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Nowa Delegacja</title>
</head>
<body>
<form action="nowaWeryfikacja" method="post">
<h3>Podaj szczegóły delegacji</h3>
<label for="miejsce">Miejsce</label><input type="text"name="miejsce" /></br>
<label for="dataRozpoczecia">Data Rozpoczęcia</label><input type="text"name="dataRozpoczecia" /></br>
<label for="dataZakonczenia">Data Zakończenia</label><input type="text"name="dataZakonczenia" /></br>
<label for="transport">Transport</label><input type="text"name="transport" /></br>
<label for="hotel">Hotel</label><input type="text"name="hotel" /></br>
<label for="cel">Cel</label><input type="text"name="cel" /></br>
<label for="projekt">Projekt</label><input type="text"name="projekt" /></br></br>

<input type="submit" value="Submit" />
</form>
</body>
</html>