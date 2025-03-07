package middleware;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(0)
public class Middleware extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Object userIdObject = request.getSession().getAttribute("userId");
		if (userIdObject == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Unauthorized: No userId found in session.");
			response.getWriter().flush();
			response.getWriter().close();
			return;
		}
		
//		int userId = userIdObject.toString().equals("") ? 0 : Integer.parseInt(userIdObject.toString());
//		System.out.println("User ID is: " + userId);
//		System.out.println("Request URI is: " + request.getRequestURI());
		filterChain.doFilter(request, response);

	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return !path.startsWith("/api");
	}
}