/*
 * Copyright (C) 2015 Miquel Sas
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.qtplaf.library.app;

import java.util.Locale;

import com.qtplaf.library.util.TextServer;

/**
 * A session manages the featurs related to one user executing one instance of the system. The selected locale, the user
 * and its rights, all are features packaged in the session.
 * <p>
 * Wherever a locale, user rights of any other user feature, the session will be required.
 * <p>
 * In a swing application, one session will be created at start time after logon, and this will be the unique session
 * managed by the application.
 * <p>
 * In a <i>WEB</i> application, normally under the execution of an <i>HttpSevlet</i>, mainly using the session of an
 * <i>HttpServelRequest</i>, many sessions will be available, each one corresponding the every detached request.
 * 
 * @author Miquel Sas
 */
public class Session {
	
	/**
	 * Default session.
	 */
	public static Session UK = new Session(Locale.UK);

	/**
	 * The working locale.
	 */
	private Locale locale;
	/**
	 * The user logged in the session.
	 */
	private User user;
	/**
	 * Security manager.
	 */
	private Security security;

	/**
	 * Constructor.
	 */
	public Session() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param locale The session locale.
	 */
	public Session(Locale locale) {
		super();
		setLocale(locale);
	}

	/**
	 * Returns the session locale.
	 * 
	 * @return The session locale.
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Set the session locale. Must be set at start up and does not trigger any interface update.
	 * 
	 * @param locale The session locale.
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Returns the working user.
	 * 
	 * @return The working user.
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the working user.
	 * 
	 * @param user The working user.
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Returns the security manager.
	 * 
	 * @return The security manager.
	 */
	public Security getSecurity() {
		return security;
	}

	/**
	 * Sets the security manager.
	 * 
	 * @param security The security manager.
	 */
	public void setSecurity(Security security) {
		this.security = security;
	}

	/**
	 * Returns the access mode for the argument key.
	 * 
	 * @param accessKey The access key.
	 * @return The access mode or not defined.
	 */
	public AccessMode getAccessMode(String accessKey) {
		if (getUser() != null && getSecurity() != null && accessKey != null) {
			return getSecurity().getAccessMode(getUser(), accessKey);
		}
		return AccessMode.NOT_DEFINED;
	}

	/**
	 * Returns a keyed and localized string.
	 * 
	 * @param key The key.
	 * @return The localizedstring.
	 */
	public String getString(String key) {
		Locale locale = getLocale();
		if (locale == null) {
			locale = Locale.UK;
		}
		return TextServer.getString(key, locale);
	}
}
