<%--
  User: Jeff Gaynor
  Date: 9/27/11
  Time: 4:37 PM
  Modified by Viknes Balasubramanee to redirect to login failure page
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<html>
<head>
    <title>CILogon2's Service 404 Error Page.</title>
    <link rel="stylesheet" type="text/css" media="all" href="WEB-INF/static/cilogon.css"/>
    <link rel="icon" href="images/favicon.ico" type="image/x-icon"/>
    <link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon"/>
</head>

<body>
<div id="topimgfill">
    <div id="topimg"></div>
</div>
<br clear="all"/>

<div class="floatleftbox">
    <div class="boxheader">Oh dear!</div>
    <div class="textbox">
        There was a problem servicing your request.

        A description of the problem reads as follows:
        <br><br>

        <i>${exception.message}</i>
        <br><br>

        <form name="input" action="../iugateway/loginFailure" method="get">
            Click to go back to the main page.<br><br><input type="submit" value="Submit"/>
        </form>
    </div>
</div>

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


</body>
</html>