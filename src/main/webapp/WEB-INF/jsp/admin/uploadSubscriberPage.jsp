<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="org.springframework.ui.Model"%>
  <%@taglib uri="http://www.springframework.org/tags/form" prefix="s"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
 
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Payment</title>
<script type="text/javascript">
function goBack(){
	window.location.href = "/SmartCable/home"
}
function deleteEntry(id){
	if(confirm("Are you sure to delete the record")){
		window.location.href = "/SmartCable/admin/paymentDef/delete?boId="+id;
	}
}
function editEntry(id){
		window.location.href = "/SmartCable/admin/paymentDef/edit?boId="+id;
}
</script>
</head>
<body style="">
<s:form action="/SmartCable/admin/uploadSubscriberFile" enctype="multipart/form-data" method="POST">

<h1>File Upload</h1>
<table width="100%">

<tr>
<td>
<table>
						<tr>
							<td>Upload the file</td>
							<td> <input type="file" name="file"></td>
								
						</tr>
						<tr>
							<td colspan="2"><input type="submit" value="Upload"> Press here to upload the file!</td>
								
						</tr>
						
						
						
					</table>
</td>
</tr>
</table>
</s:form>

</body>
</html>