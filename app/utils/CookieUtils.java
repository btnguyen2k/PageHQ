package utils;

import play.mvc.Http;

/**
 * Cookies-related utilities.
 * 
 * @author Thanh.Nguyen <btnguyen2k@gmail.com>
 */
public class CookieUtils {
	/**
	 * Gets a cookie by name.
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	public static Http.Cookie getCookie(Http.Request request, String name) {
		return request.cookies().get(name);
	}
}
