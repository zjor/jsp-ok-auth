<%@ page import="org.zjor.OKConnector" %>
<%@ page import="org.zjor.OkUserDTO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="clientId" value="<your_app_id>"/>
<c:set var="appKey" value="<your_app_public_key>"/>
<c:set var="appSecret" value="your_app_secret_key"/>
<c:set var="scope" value="VALUABLE ACCESS"/>

<c:set var="baseURL" value="${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, pageContext.request.contextPath)}"/>

<%
    String baseURL = (String) pageContext.getAttribute("baseURL");
    String code = request.getParameter("code");
    if (code != null) {

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

    if ("logout".equals(request.getParameter("action"))) {
        pageContext.removeAttribute("user");

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
        <a href="http://www.odnoklassniki.ru/oauth/authorize?client_id=${clientId}&scope=${scope}&response_type=code&redirect_uri=${baseURL}">Login via OK</a>
    </c:when>
    <c:otherwise>
        <p>
            You have successfully logged in as <strong><c:out value="${user.fullName}"/></strong>
        </p>
        <a href="${baseURL}?action=logout">Logout</a>
    </c:otherwise>
</c:choose>
</body>
</html>
