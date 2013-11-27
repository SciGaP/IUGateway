<%--
  User: Jeff Gaynor
  Date: 9/27/11
  Time: 4:58 PM
  Modified by Viknes Balasubramanee to post the certificate to IUGateway portal
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>CILogon 2 success page.</title>
    <link rel="stylesheet"
          type="text/css"
          media="all"
          href="static/cilogon.css"/>
</head>
<body onload="document.certForm.submit()">
<div id="topimgfill">
    <div id="topimg"></div>
</div>

<br clear="all"/>

<div class="main">
    <p><b>Success!</b><br><br> The subject of the cert is<br><br> ${certSubject}
        <br><br>and the user id found for this request was <b>${username}</b>
        <br>The certificate is ${cert}

<%--     <form name="input" action="${action}/" method="get"/>
    <input type="submit" value="Return to client"/>
    </form> --%>
    <!--  Got the certificate from CILogon. Posting it to the iugateway portal -->
    <form name="certForm" action="../iugateway/receiveCert" method="POST">
    	<input type="hidden" name="certSubject" value="${certSubject}"/>
    	<input type="hidden" name="cert" value="${cert}"/>
    	<input type="hidden" name="username" value="${username}"/>
    	<input type="submit" value="Return to Homepage"/>
    </form>
    <div class="footer">

        <p>
            For questions about this site, please see the
            <a target="_blank" href="http://www.cilogon.org/portal-delegation">Portal
                Delegation FAQ</a> or send email to <a
                href="mailto:help@cilogon.org">help&nbsp;@&nbsp;cilogon.org</a>.
        </p>

        <p>
            This material is based upon work supported by the <a target="_blank"
                                                                 href="http://www.nsf.gov/">National Science
            Foundation</a> under grant
            number <a target="_blank"
                      href="http://www.nsf.gov/awardsearch/showAward.do?AwardNumber=0943633">0943633</a>.
        </p>

        <p>
            Any opinions, findings, and conclusions or recommendations expressed in this
            material are those of the authors and do not necessarily reflect the views
            of the National Science Foundation.
        </p>
    </div>
</div>
</body>
</html>