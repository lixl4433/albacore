package net.butfly.albacore.utils.logger.log4j;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public class JsonLayout extends Layout {
	private boolean debug = false;// stacktrack outputting
	private String timeFormat = "yyyy-MM-dd hh:mm:ss,SSS";

	public boolean isDebug() { return debug; }

	public void setDebug(boolean debug) { this.debug = debug; }

	public String getTimeFormat() { return timeFormat; }

	public void setTimeFormat(String timeFormat) { this.timeFormat = timeFormat; }

	@Override
	public String getContentType() { return "application/json"; }

	@Override
	public void activateOptions() {}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String format(LoggingEvent event) {
		// Map<String, Object> v = new ConcurrentHashMap<>();
		// Hashtable mdc = MDC.getContext();
		// if (null != mdc) mdc.forEach((k, vv) -> {
		// if (null != vv) v.put(k.toString(), vv);
		// });
		// if (!v.containsKey("STATS_TABLE")) return "";
		// v.put("OP_TIME", Texts.formatDate(timeFormat, new Date(event.timeStamp)));
		// ThrowableInformation thi = event.getThrowableInformation();
		// if (null != thi) {
		// if (null != thi.getThrowable().getMessage()) v.put("ERR_MESSAGE", thi.getThrowable().getMessage());
		// if (debug && null != thi.getThrowableStrRep()) v.put("ERR_STACKTRACE", thi.getThrowableStrRep());
		// }
		Object m = event.getMessage();
		// if (null != m) v.put("LOG_MESSAGE", m);
		// return Jsonx.json(v) + "\n";
		return null == m ? "" : m.toString();
	}

	@Override
	public boolean ignoresThrowable() {
		return false;
	}
}
