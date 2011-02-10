<%@page import="fr.paris.lutece.plugins.workflow.modules.fillingdirectory.web.DoDownloadHistoryFile"%>
<% 
	 String strResult =  DoDownloadHistoryFile.doDownloadFile(request,response);
 	 if (!response.isCommitted())
	{
		  response.sendRedirect(strResult);
	}
%>
