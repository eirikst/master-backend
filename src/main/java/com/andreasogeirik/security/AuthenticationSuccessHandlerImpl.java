package com.andreasogeirik.security;

import com.andreasogeirik.model.dto.outgoing.UserDtoOut;
import com.andreasogeirik.service.dao.interfaces.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by eirikstadheim on 07/02/16.
 */
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    @Autowired
    UserDao userDao;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {
        int userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        com.andreasogeirik.model.entities.User user = userDao.findById(userId);

        httpServletResponse.getWriter().write(new UserDtoOut(user).toJson().toString());
        httpServletResponse.getWriter().flush();
        httpServletResponse.getWriter().close();
    }
}
