package net.butfly.albacore.io;

import static net.butfly.albacore.paral.Task.waitSleep;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import net.butfly.albacore.base.Named;
import net.butfly.albacore.lambda.Runnable;
import net.butfly.albacore.utils.collection.Maps;
import net.butfly.albacore.utils.logger.Loggable;

public interface Openable extends AutoCloseable, Loggable, Named {
	enum Status {
		CLOSED, OPENING, OPENED, CLOSING
	}

	default boolean opened() {
		return Opened.status(this).get() == Status.OPENED;
	}

	default boolean closed() {
		return Opened.status(this).get() == Status.CLOSED;
	}

	/**
	 * Run the handler before self close handler.
	 * 
	 * @param handler
	 */
	default void opening(Runnable handler) {
		Opened.OPENING.compute(this, (self, orig) -> {
			return orig == null ? handler : Runnable.merge(orig, handler);
		});
	}

	/**
	 * Run the handler before self close handler.
	 * 
	 * @param handler
	 */
	default void closing(Runnable handler) {
		Opened.CLOSING.compute(this, (self, orig) -> {
			return orig == null ? handler : Runnable.merge(orig, handler);
		});
	}

	default void open() {
		AtomicReference<Status> s = Opened.STATUS.computeIfAbsent(this, o -> new AtomicReference<Status>(Status.CLOSED));
		if (s.compareAndSet(Status.CLOSED, Status.OPENING)) {
			logger().trace(name() + " opening...");
			Runnable h = Opened.OPENING.get(this);
			if (null != h) h.run();
			if (!s.compareAndSet(Status.OPENING, Status.OPENED)) //
				throw new RuntimeException("Opened failure since status [" + s.get() + "] not OPENING.");
		}
		if (s.get() != Status.OPENED) //
			throw new RuntimeException("Start failure since status [" + s.get() + "] not OPENED.");
		logger().trace(name() + " opened.");
	}

	@Override
	default void close() {
		AtomicReference<Status> s = Opened.status(this);
		if (s.compareAndSet(Status.OPENED, Status.CLOSING)) {
			logger().trace(name() + " closing...");
			Runnable h = Opened.CLOSING.get(this);
			if (null != h) h.run();
			s.compareAndSet(Status.CLOSING, Status.CLOSED);
		} // else logger().warn(name() + " closing again?");
		while (!closed())
			waitSleep(500, logger(), "Waiting for closing finished...");
		Opened.STATUS.remove(this);
		logger().trace(name() + " closed.");
	}

	class Opened {
		private final static Map<Openable, AtomicReference<Status>> STATUS = Maps.of();
		private final static Map<Openable, Runnable> OPENING = Maps.of(), CLOSING = Maps.of();

		private Opened() {}

		private static AtomicReference<Status> status(Openable inst) {
			return STATUS.getOrDefault(inst, new AtomicReference<>(Status.CLOSED));
		}
	}
}
