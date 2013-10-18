package com.paulhammant.greyangular.servlet;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.paulhammant.greyangular.Constants;
import com.paulhammant.greyangular.StringResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class EnsureHasAGreyhoundSessionFilter extends AbstractFilter {

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) servletRequest).getSession();
        if (session.getAttribute(Constants.SESSION_KEY) == null) {
            session.setAttribute(Constants.SESSION_KEY, getSessionFromGreyhound());
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public String getSessionFromGreyhound() throws IOException {
        HttpRequest httpRequest = new NetHttpTransport()
                .createRequestFactory(new StringResponse())
                .buildGetRequest(new GenericUrl("https://www.greyhound.com"));
        httpRequest.getHeaders().setUserAgent(Constants.USER_AGENT);
        return getCookie(httpRequest.execute());
    }

    private String getCookie(HttpResponse response) throws IOException {
        String cookies = response.getHeaders().get("set-cookie").toString();
        int start = cookies.indexOf("ASP.NET_SessionId=") + "ASP.NET_SessionId=".length();
        int end = cookies.indexOf("; path=/;", start);
        return cookies.substring(start, end);
    }

}
