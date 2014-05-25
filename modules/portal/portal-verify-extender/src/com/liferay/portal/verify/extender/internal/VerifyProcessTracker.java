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

package com.liferay.portal.verify.extender.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.verify.VerifyException;
import com.liferay.portal.verify.VerifyProcess;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Miguel Pastor
 */
public class VerifyProcessTracker
	extends ServiceTracker<VerifyProcess, VerifyProcess> {

	public VerifyProcessTracker(BundleContext bundleContext) {
		super(bundleContext, VerifyProcess.class, null);

		_bundleContext = bundleContext;
	}

	@Override
	public VerifyProcess addingService(
		ServiceReference<VerifyProcess> serviceReference) {

		VerifyProcess verifyProcess = null;

		try {
			verifyProcess = _bundleContext.getService(
				serviceReference);

			String verifyProcessName = getVerifyProcessName(serviceReference);
	
			_verifyProcesses.put(verifyProcessName, verifyProcess);
		}
		catch (IllegalArgumentException iae) {
			return null;
		}

		if (_log.isDebugEnabled()) {
			Bundle bundle = _bundleContext.getBundle();

			_log.debug(
				"Executing verify process " + bundle.getSymbolicName() +
					" with " + verifyProcess.getClass());
		}

		try {
			verifyProcess.verify();
		}
		catch (VerifyException ve) {
			_log.error(
				"Unexpected error while executing the verify " +
					verifyProcess.getClass(),
				ve);
		}

		return verifyProcess;
	}

	public void execute(String verifyProcessName) throws VerifyException {
		VerifyProcess verifyProcess = _verifyProcesses.get(verifyProcessName);

		if (verifyProcess == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to find a verify process " + verifyProcessName);
			}

			return;
		}

		verifyProcess.verify();
	}

	public void list() {
		for (Map.Entry<String, VerifyProcess> entry :
				_verifyProcesses.entrySet()) {

			if (_log.isInfoEnabled()) {
				_log.info(	
					"Verify process " + entry.getKey() + " has " +
						entry.getValue());
			}
		}
	}

	@Override
	public void modifiedService(
		ServiceReference<VerifyProcess> serviceReference,
		VerifyProcess verifyProcess) {

		addingService(serviceReference);
	}

	@Override
	public void removedService(
		ServiceReference<VerifyProcess> serviceReference,
		VerifyProcess verifyProcess) {

		String verifyProcessName = getVerifyProcessName(serviceReference);

		_verifyProcesses.remove(verifyProcessName);
	}

	public String getVerifyProcessName(
		ServiceReference<VerifyProcess> serviceReference) {

		String verifyProcessName = (String)serviceReference.getProperty(
			"verify.process.name");

		if ((verifyProcessName == null) || verifyProcessName.equals("")) {
			throw new IllegalArgumentException(
				"Verify processes must specify the property " +
					"\"verify.process.name\"");
		}

		return verifyProcessName;
	}

	private static Log _log = LogFactoryUtil.getLog(VerifyProcessTracker.class);

	private BundleContext _bundleContext;
	private Map<String, VerifyProcess> _verifyProcesses =
		new ConcurrentHashMap<String, VerifyProcess>();

}