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
 <script src="<c:url value="/resources/jquery-1.11.3.min.js" />"></script>
 <script src="<c:url value="/resources/jquery-ui.js" />"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Payment</title>
<script type="text/javascript">
function goBack(){
	window.location.href = "userInfo"
}
function deleteEntry(id){
	if(confirm("Are you sure to delete the record")){
		window.location.href = "cancelMonthPayment?boId="+id;
	}
}
function calculateOnAmount(){
	$.ajax({
		  url: "http://localhost:8080/SpringMavenFinal/admin/calculateOnAmount?amountG="+$('#amountG').val(),
		  success: function(resp){
		    $('#partialId').html(resp.split('_')[0]);
		    $('#pendingTillMonth').val(resp.split('_')[1]);
		  }
		});
}
function autoFillAmount(index){
	if(document.getElementById('monthDetailsList'+index+'.completed1').checked){
		document.getElementById('monthDetailsList'+index+'.amount').value=document.getElementById('monthDetailsList'+index+'.reqAmount').value;
	}else{
		document.getElementById('monthDetailsList'+index+'.amount').value=document.getElementById('tAmount'+index).value;
	}
} 

</script>
</head>
<body style="">
<s:form action="addMonthlyPayment" modelAttribute="paymentDto">
<h2 align="center" style="color: #808080;">Monthly Payment</h1>
<table width="100%">

<tr><td style="color: red;"> <c:out value="${error}"></c:out></td></tr>
<tr><td style="color:green;"> <c:out value="${message}"></c:out></td></tr>
<tr>
<td colspan="2">
<table>
<tr>
<td>User Name</td>
<td colspan="2"><c:out value="${paymentDto.subscriberName}"></c:out> </td>
</tr>
<tr>
<td>Total Pending amount:</td>
<td colspan="2"><c:out value="${paymentDto.totalPending}"></c:out>
<%-- <s:hidden path="pendingTillMonthMap"/> --%>
<br>
</td>
</tr>
<c:if test="${paymentDto.totalPending==0}">
<td colspan="2"><div style="font-size: x-large;color: green; ">Payment is completed till date.</div> </td>
</c:if>
</table>
<c:if test="${not empty paymentDto.monthDetailsList }">
		<table border="1" cellpadding="5px" style="border-collapse: collapse;margin-top: 30px; border-radius:15px;border-width: thick;border-color: gray;" width="100%">
		<tr style="background-color: #C0C0C0">
			<th width="20%">Sl No</th>
			<th width="20%">Month</th>
			<th width="20%">Payment date</th>
			<th width="20%">Amount</th>
			<th width="20%">Completed</th>
		</tr>
		<c:forEach var="dto" items="${paymentDto.monthDetailsList }" varStatus="st">
		<tr id="r_${st.index}">
			<td><c:out value="${st.index+1}"/></td>
			<td><c:out value="${dto.paymentMonth}"/></td>
			<td><c:out value="${dto.paymentMadeDate}"/><input id="tAmount${st.index}" type="hidden" value="${dto.amount}"> </td>
			<td><s:input size="3"  path="monthDetailsList[${st.index}].amount"/><div style="color: gray;display: inline-block;">&nbsp;&nbsp;<c:out value="${dto.reqAmount}"/></div> </td>
			<td align="center"><s:checkbox path="monthDetailsList[${st.index}].completed" onclick="autoFillAmount(${st.index})" />  </td>
			<s:hidden path="monthDetailsList[${st.index}].reqAmount"/>
		</tr>
		<c:if test="${dto.completed==false}">
		<script>
			$('#r_'+${st.index}).css("background-color", "#ffcccc");
		</script>
		</c:if>
		</c:forEach>
		</table>
</c:if>
</td>
</tr>
<tr>
<td style="padding: 20px;" align="right"><input type="submit" value="Add Payment"> </td>
<td style="padding: 20px;"  align="left"><input type="button" onclick="goBack()" value="Close"> </td>
</tr>
</table>
</s:form>
</body>
</html>