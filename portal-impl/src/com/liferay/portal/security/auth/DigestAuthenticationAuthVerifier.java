/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.security.auth;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 */
public class DigestAuthenticationAuthVerifier implements AuthVerifier {
	public String getAuthType() {
		return AUTH_TYPE;
	}

	public AuthVerifierResult verify(
			AccessControlContext accessControlContext, Properties configuration)
		throws AuthException {

		try {
			AuthVerifierResult authVerifierResult = new AuthVerifierResult();

			HttpServletRequest request = accessControlContext.getRequest();

			long userId = PortalUtil.getDigestAuthUserId(request);

			if (userId == 0) {
				return authVerifierResult;
			}

			authVerifierResult.setState(AuthVerifierResult.State.SUCCESS);
			authVerifierResult.setUserId(userId);

			return authVerifierResult;
		}
		catch (PortalException e) {
			throw new AuthException(e);
		}
		catch (SystemException e) {
			throw new AuthException(e);
		}
	}

	public static final String AUTH_TYPE = HttpServletRequest.DIGEST_AUTH;
}