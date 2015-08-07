<%@ page errorPage="../../ErrorPage.jsp"%>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="helpfilling" scope="session"
	class="fr.paris.lutece.plugins.workflow.modules.fillingdirectory.web.HelpFillingJspBean" />
<%
helpfilling.init(request,fr.paris.lutece.plugins.directory.web.ManageDirectoryJspBean.RIGHT_MANAGE_DIRECTORY);
%>
<%=helpfilling.getModifyDirectoryRecordPopup(request)%>

<%@ include file="../../AdminFooter.jsp"%>


