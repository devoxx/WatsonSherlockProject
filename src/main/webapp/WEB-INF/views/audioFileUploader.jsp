<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Upload a Devoxx OGG Audio File to Watson</title>
	<link href="<c:url value='/static/css/bootstrap.css' />"  rel="stylesheet" type="text/css"/>
	<link href="<c:url value='/static/css/app.css' />" rel="stylesheet" type="text/css"/>
</head>
<body> 

	<div class="form-container">
		<h1>Devoxx Watson Audio File Upload</h1>
		<form:form method="POST" modelAttribute="fileBucket" enctype="multipart/form-data" class="form-horizontal">
		
			<div class="row">
				<div class="form-group col-md-12">
                    <label class="col-md-3 control-lable" for="file">Presentation name</label>
                    <div class="col-md-7">
                        <form:input type="text" path="docName" id="name" class="form-control input-sm"/>
                        <div class="has-error">
                            <form:errors path="docName" class="help-inline"/>
                        </div>
                    </div>
                    <label class="col-md-3 control-lable" for="file">Speaker(s)</label>
					<div class="col-md-7">
						<form:input type="text" path="speakers" id="name" class="form-control input-sm"/>
						<div class="has-error">
							<form:errors path="speakers" class="help-inline"/>
						</div>
					</div>
					<label class="col-md-3 control-lable" for="file">YouTube link</label>
                    <div class="col-md-7">
                        <form:input type="text" path="link" id="name" class="form-control input-sm"/>
                        <div class="has-error">
                            <form:errors path="link" class="help-inline"/>
                        </div>
                    </div>
					<label class="col-md-3 control-lable" for="file">Abstract</label>
					<div class="col-md-7">
						<form:input type="text" path="audioAbstract" id="name" class="form-control input-sm"/>
						<div class="has-error">
							<form:errors path="audioAbstract" class="help-inline"/>
						</div>
					</div>
					<label class="col-md-3 control-lable" for="file">Upload an OGG audio file</label>
					<div class="col-md-7">
						<form:input type="file" path="file" id="file" class="form-control input-sm"/>
						<div class="has-error">
							<form:errors path="file" class="help-inline"/>
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
