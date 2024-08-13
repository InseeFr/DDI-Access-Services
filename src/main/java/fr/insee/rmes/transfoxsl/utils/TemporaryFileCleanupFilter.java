package fr.insee.rmes.transfoxsl.utils;

import jakarta.servlet.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TemporaryFileCleanupFilter implements Filter {

    private static final List<File> tempFiles = new ArrayList<>();

    public static void addTempFile(File file) {
        tempFiles.add(file);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } finally {
            for (File file : tempFiles) {
                file.delete();
            }
            tempFiles.clear();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Do nothing
    }

    @Override
    public void destroy() {
        // Do nothing
    }
}