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
package com.moss.appsnap.keeper.posix;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moss.appsnap.keeper.AbstractDesktopIntegrationStrategy;
import com.moss.appsnap.keeper.socketapi.ApiMessageConnection;
import com.moss.appsnap.keeper.socketapi.ApiMessageHandler;
import com.moss.posixfifosockets.PosixFifoSocket;
import com.moss.posixfifosockets.PosixFifoSocketAddress;
import com.moss.posixfifosockets.PosixFifoSocketHandler;
import com.moss.posixfifosockets.PosixFifoSocketServer;

/**
 * Encapsulates the use of the named pipes IPC mechanism for handling socket API stuff in posix systems
 */
public abstract class AbstractPosixDesktopIntegrationStrategy extends AbstractDesktopIntegrationStrategy {
	private Log log = LogFactory.getLog(getClass());

	class SocketAdapter implements PosixFifoSocketHandler {
		private final ApiMessageHandler handler;

		public SocketAdapter(ApiMessageHandler handler) {
			super();
			this.handler = handler;
		}

		public void handle(PosixFifoSocket socket) {
			log.info("Handling posix fifo socket connection: " + socket);
			handler.handle(new ApiMessageConnectionAdapter(socket));
		}
	};
	
	@Override
	public boolean keeperIsRunning() {
		return localApiServerSocketAddress().controlPipe().exists();
	}

	
	@Override
	public void startLocalApiServer(ApiMessageHandler handler) {
		try {
			localApiServerSocketAddress().mkdirs();
			
			final PosixFifoSocketServer fifoSocketServer = new PosixFifoSocketServer(localApiServerSocketAddress(), new SocketAdapter(handler));
			fifoSocketServer.start();
			
			Runtime.getRuntime().addShutdownHook(new Thread(){
				@Override
				public void run() {
					log.info("Stopping fifo socket server");
					fifoSocketServer.stop();
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract PosixFifoSocketAddress localApiServerSocketAddress();

	@Override
	public ApiMessageConnection newMessageToHost() {
		try {
			PosixFifoSocket socket = PosixFifoSocket.newClientConnection(localApiServerSocketAddress(), 5000);

			return new ApiMessageConnectionAdapter(socket);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
