<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Voxxed Article Upload to Watson</title>
    <link href="<c:url value='/static/css/bootstrap.css' />"  rel="stylesheet" type="text/css"/>
    <link href="<c:url value='/static/css/app.css' />" rel="stylesheet" type="text/css"/>
</head>
<body>

<div class="form-container">
    <h1>Voxxed Watson Article Upload</h1>
    <form:form method="POST" modelAttribute="article" class="form-horizontal">

        <div class="row">
            <div class="form-group col-md-12">
                <label class="col-md-3 control-lable" for="name">Voxxed article link</label>
                <div class="col-md-7">
                    <form:input type="text" path="link" id="name" class="form-control input-sm"/>
                    <div class="has-error">
                        <form:errors path="link" class="help-inline"/>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="form-actions floatRight">
                <input type="submit" value="Upload" class="btn btn-primary btn-sm">
            </div>
        </div>
    </form:form>
    <a href="<c:url value='/welcome' />">Home</a>
</div>
</body>
</html>
