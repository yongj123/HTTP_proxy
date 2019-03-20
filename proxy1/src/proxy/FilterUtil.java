package proxy;

//过滤器
public class FilterUtil {

	static String URLFilter = "";// 要拦截的URL
	static String SFilter = "";// 要过滤的字符串
	static String Rep = "";// 替换的字符串

	public static int getDelta() {
		if (SFilter != "") {
			return SFilter.getBytes().length - Rep.getBytes().length;
		}
		return 0;
	}

	public static String getURLFilter() {
		return URLFilter;
	}

	public static void setURLFilter(String uRLFilter) {
		URLFilter = uRLFilter;
	}

	public static String getSFilter() {
		return SFilter;
	}

	public static void setSFilter(String sFilter) {
		SFilter = sFilter;
	}

	public static String getRep() {
		return Rep;
	}

	public static void setRep(String rep) {
		Rep = rep;
	}

	// try {
	// SFilter = URLEncoder.encode(SFilter, "utf-8");
	// Rep = URLDecoder.decode(Rep, "utf-8");
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// }
}
