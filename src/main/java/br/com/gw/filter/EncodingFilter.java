package br.com.gw.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class EncodingFilter implements Filter {
    private static final String UTF_8 = "UTF-8";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        request.setCharacterEncoding(UTF_8);
        response.setCharacterEncoding(UTF_8);

        chain.doFilter(request, response);
    }
}
