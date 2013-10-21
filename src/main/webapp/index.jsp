<%@ page import="org.zjor.OKConnector" %>
<%@ page import="org.zjor.OkUserDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="clientId" value="199160832"/>
<c:set var="appKey" value="CBAFPPNMABABABABA"/>
<c:set var="appSecret" value="3DAF73573B571CD3FD77F545"/>
<c:set var="scope" value="VALUABLE ACCESS"/>

<c:set var="baseURL" value="${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, pageContext.request.contextPath)}"/>

<%
    String code = request.getParameter("code");
    if (code != null) {
        String baseURL = (String) pageContext.getAttribute("baseURL");
        String clientId = (String) pageContext.getAttribute("clientId");
        String appKey = (String) pageContext.getAttribute("appKey");
        String appSecret = (String) pageContext.getAttribute("appSecret");

        OKConnector connector = new OKConnector(clientId, appKey, appSecret);

        String accessToken = connector.exchangeCode(code, baseURL);
        OkUserDTO user = connector.getCurrentUser(accessToken);

        session.setAttribute("user", user);

        // To cleanup address from messy code parameter
        response.sendRedirect(baseURL);
    }
%>

<c:set var="user" value="${sessionScope.user}"/>

<!DOCTYPE html>
<html>
<head>
    <title>OK Auth</title>
</head>
<body>
<c:choose>
    <c:when test="${user == null}">
        <h2>You are not authorized</h2>
        <a href="http://www.odnoklassniki.ru/oauth/authorize?client_id=${clientId}&scope=${scope}&response_type=code&redirect_uri=${baseURL}">
            Login
        </a>
    </c:when>
    <c:otherwise>
        <c:out value="${user.fullName}"/>
    </c:otherwise>
</c:choose>
</body>
</html>
