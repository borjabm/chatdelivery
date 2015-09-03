package com.borjabonilla.chat.server.listeners;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.spdy.api.DataInfo;
import org.eclipse.jetty.spdy.api.GoAwayResultInfo;
import org.eclipse.jetty.spdy.api.PingResultInfo;
import org.eclipse.jetty.spdy.api.ReplyInfo;
import org.eclipse.jetty.spdy.api.Session;
import org.eclipse.jetty.spdy.api.Stream;
import org.eclipse.jetty.spdy.api.StreamFrameListener;
import org.eclipse.jetty.spdy.api.SynInfo;
import org.eclipse.jetty.spdy.api.server.ServerSessionFrameListener;

import com.borjabonilla.chat.server.AbstractMessageProcessorServer;
import com.borjabonilla.chat.server.impl.MessageProcessorServerImpl;

/**
 * Custom Listener for ServerSessionFrameListener.Adapter
 * 
 * @author Borja Bonilla
 * 
 */
public class SpeedyServerListener extends ServerSessionFrameListener.Adapter {

	// Any exceptions will be logged in a log file.
	private static final Logger logger = Logger.getLogger(SpeedyServerListener.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jetty.spdy.api.server.ServerSessionFrameListener.Adapter# onConnect(org.eclipse.jetty.spdy.api.Session)
	 */
	@Override
	public void onConnect(Session session) {
		System.out.println("onConnect--> session:" + session);
		super.onConnect(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jetty.spdy.api.server.ServerSessionFrameListener.Adapter# onGoAway(org.eclipse.jetty.spdy.api.Session)
	 */
	@Override
	public void onGoAway(Session session, GoAwayResultInfo goAwayResultInfo) {
		System.out.println("onGoAway--> session:" + session);
		super.onGoAway(session, goAwayResultInfo);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jetty.spdy.api.SessionFrameListener.Adapter#onSyn(org.eclipse .jetty.spdy.api.Stream,
	 * org.eclipse.jetty.spdy.api.SynInfo)
	 */
	@Override
	public StreamFrameListener onSyn(Stream stream, SynInfo synInfo) {
		/**
		 * The listener will return the handler for the stream on this session as soon as synInfo is received.
		 */
		try {
			// Send a reply to this message and keep stream openned.
			stream.reply(new ReplyInfo(false));

			// Next step is to create an adapter to handle the inputs of the
			// client from a specific stream.
			return new StreamFrameListener.Adapter() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.jetty.spdy.api.StreamFrameListener .Adapter #onFailure(org.eclipse.jetty.spdy.api.Stream,
				 * java.lang.Throwable)
				 */
				@Override
				public void onFailure(Stream stream, Throwable x) {
					System.out.println("onFailure->" + stream);
					super.onFailure(stream, x);
				}

				/**
				 * Let's treat and process the data when received.
				 */
				public void onData(Stream stream, DataInfo dataInfo) {
					String clientData = dataInfo.asString("UTF-8", false);
					System.out.println("Received the following client data: " + clientData);

					AbstractMessageProcessorServer processor = new MessageProcessorServerImpl();
					processor.process(stream, clientData);
				}
			};

		} catch (InterruptedException e) {
			logger.error(SpeedyServerListener.class, e);
		} catch (ExecutionException e) {
			logger.error(SpeedyServerListener.class, e);
		} catch (TimeoutException e) {
			logger.error(SpeedyServerListener.class, e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jetty.spdy.api.SessionFrameListener.Adapter#onPing(org.eclipse .jetty.spdy.api.Session,
	 * org.eclipse.jetty.spdy.api.PingResultInfo)
	 */
	@Override
	public void onPing(Session session, PingResultInfo pingResultInfo) {
		super.onPing(session, pingResultInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jetty.spdy.api.SessionFrameListener.Adapter#onFailure(org .eclipse.jetty.spdy.api.Session, java.lang.Throwable)
	 */
	@Override
	public void onFailure(Session session, Throwable x) {
		
		System.out.println("onFailure--> session: " + session);
		super.onFailure(session, x);
	}
}
