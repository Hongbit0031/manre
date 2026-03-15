package io.linfeng.common.xss;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XSS过滤
 *
 */
public class XssFilter implements Filter {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private List<String> excludes = new ArrayList<>();
    private boolean isIncludeRichText = false;

    @Override
    public void init(FilterConfig config) {
        String excludeParam = config.getInitParameter("excludes");
        if (StrUtil.isNotBlank(excludeParam)) {
            excludes = Arrays.stream(excludeParam.split(","))
                    .map(String::trim)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
        }
        String richTextParam = config.getInitParameter("isIncludeRichText");
        if (StrUtil.isNotBlank(richTextParam)) {
            isIncludeRichText = Boolean.parseBoolean(richTextParam);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (isExcluded(httpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(
                httpServletRequest, isIncludeRichText);
        chain.doFilter(xssRequest, response);
    }

    @Override
    public void destroy() {
    }

    private boolean isExcluded(HttpServletRequest request) {
        if (excludes.isEmpty()) {
            return false;
        }
        String servletPath = request.getServletPath();
        for (String pattern : excludes) {
            if (antPathMatcher.match(pattern, servletPath)) {
                return true;
            }
        }
        return false;
    }

}