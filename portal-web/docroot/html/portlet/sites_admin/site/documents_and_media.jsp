<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/html/portlet/sites_admin/init.jsp" %>

<%
Group group = (Group)request.getAttribute("site.group");
Group liveGroup = (Group)request.getAttribute("site.liveGroup");
%>

<aui:fieldset label="documents-and-media">
	<c:if test="<%= (group != null) && !group.isCompany() %>">

		<%
		UnicodeProperties typeSettingsProperties = null;

		if (liveGroup != null) {
			typeSettingsProperties = liveGroup.getTypeSettingsProperties();
		}
		else {
			typeSettingsProperties = group.getTypeSettingsProperties();
		}

		boolean directoryIndexingEnabled = PropertiesParamUtil.getBoolean(typeSettingsProperties, request, "directoryIndexingEnabled");
		%>

		<aui:input helpMessage='<%= LanguageUtil.format(request, "directory-indexing-help", new Object[] {HtmlUtil.escape(group.getDescriptiveName(themeDisplay.getLocale())), themeDisplay.getPortalURL() + "/documents" + group.getFriendlyURL()}, false) %>' label="directory-indexing-enabled" name="TypeSettingsProperties--directoryIndexingEnabled--" type="checkbox" value="<%= directoryIndexingEnabled %>" />
	</c:if>
</aui:fieldset>