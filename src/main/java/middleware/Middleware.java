package middleware;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import dto.GroupsDTO;
import dto.JunctionUsersGroupsDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import service.GroupsService;
import service.JunctionUsersGroupsService;

@Component
@Order(0)
@RequiredArgsConstructor
public class Middleware extends OncePerRequestFilter {
	final GroupsService groupsService;
	final JunctionUsersGroupsService junctionUsersGroupsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestPath = request.getRequestURI();
		Object userIdObject = request.getSession().getAttribute("userId");

		if (userIdObject == null) {
			List<String> pathsToRedirect = Arrays.asList("/groups", "/profile");
			for (String path : pathsToRedirect) {
				if (requestPath.startsWith(path)) {
					response.sendRedirect("/login");
					return;
				}
			}

			if (requestPath.startsWith("/api")) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("Unauthorized: No userId found in session.");
				response.getWriter().flush();
				response.getWriter().close();
				return;
			}
		}
		
		if (requestPath.startsWith("/group")) {
			int userId = userIdObject == null ? 0 : (int) userIdObject;
			if (requestPath.length() < 72) { // 8 + 64 = 72
				response.sendRedirect("/error/404");
				return;
			}
			
			int prefixNumber = 8; // "/groups/"
			String hashedId = requestPath.substring(prefixNumber, 72);
			GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
			if (group == null) {
				response.sendRedirect("/error/404");
				return;
			}
			
			JunctionUsersGroupsDTO junction = junctionUsersGroupsService.readJunctionUsersGroupsByUserIdAndGroupId(userId, group.getId());
			if (junction == null) {
				response.sendRedirect("/error/404");
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		List<String> pathsToFilter = Arrays.asList("/home", "/login");
		String path = request.getRequestURI();
		return pathsToFilter.stream().anyMatch(path::startsWith);
	}
}