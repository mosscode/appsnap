/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of appsnap.
 *
 * appsnap is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * appsnap is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with appsnap; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package com.moss.appsnap.keeper;

import java.io.File;

import com.moss.appsnap.api.AppsnapServiceId;
import com.moss.appsnap.keeper.data.InstallationInfo;
import com.moss.appsnap.keeper.socketapi.ApiMessageConnection;
import com.moss.appsnap.keeper.socketapi.ApiMessageHandler;

/**
 * <h2>Overview</h2>
 * <p>
 * This is the abstraction through which all
 * the os-specific stuff is handled.  
 * </p>
 * <h2>Lifecycle</h2>
 * <p>
 * On keeper/installer startup, an implementation of this is
 * chosen & instantiated.  The resulting instance is kept as a singleton
 * for the life of the keeper/installer.
 * </p>
 * <p>
 * 	Implementations can expect to always have keeperDataDir() called before
 *  anything else, followed by noLongerGutless().  If running in a Keeper 
 *  instance, these calls will then be followed by a call to thePollerWasStartedAndHereItIs().
 * </p>
 * <p>
 *  Once all those setup calls have been made, the instance is essentially treated as
 *  an on-demand factory for creating DesktopAppHandler instances (via appHandlerFor()).
 * </p>
 * 
 * <h2>Responsibilities</h2>
 * <p>
 * Implementations are responsible for exposing a means of launching the managed installations.
 * The resulting launch mechanisms must honor the following contract:
 * </p>
 * 
 * <ul>
 *   <li>When a user initiates a launch, a new call to Poller.poll() must be initiated & completed before the app is actually executed.  <i>NOTE: DesktopAppHandler integrations should be prepared to receive app maintenance callbacks while poll() is executing.</i></li>
 * </ul>
 */
public interface DesktopIntegrationStrategy {
	
	void thePollerWasStartedAndHereItIs(Poller poller);
	
	void noLongerGutless(Guts guts);
	
	DesktopAppHandler appHandlerFor(InstallationInfo info);
	File keeperDataDir(AppsnapServiceId serviceId);
	
	void startLocalApiServer(ApiMessageHandler handler);
	
	ApiMessageConnection newMessageToHost();

	void restart();
	
	boolean keeperIsRunning();
	
	void installControlPanel();
	
}
