package org.example.s29624tpo11.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
public class InternationalizationConfig implements WebMvcConfigurer, LocaleResolver {

    @Bean
    public LocaleResolver localeResolver() {
        return this;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String langParam = request.getParameter("lang");
        if (langParam != null) {
            return parseLocale(langParam);
        }

        String langHeader = request.getHeader("lang");
        if (langHeader != null) {
            return parseLocale(langHeader);
        }

        Locale sessionLocale = (Locale) request.getSession().getAttribute("locale");
        if (sessionLocale != null) {
            return sessionLocale;
        }

        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage != null && !acceptLanguage.isEmpty()) {
            String langCode = acceptLanguage.split(",")[0].trim().split(";")[0].toLowerCase();
            if (langCode.startsWith("pl")) return new Locale("pl");
            if (langCode.startsWith("de")) return new Locale("de");
        }

        return Locale.ENGLISH;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        if (locale != null) {
            request.getSession().setAttribute("locale", locale);
        }
    }

    private Locale parseLocale(String lang) {
        return switch (lang.toLowerCase()) {
            case "pl" -> new Locale("pl");
            case "de" -> new Locale("de");
            default -> Locale.ENGLISH;
        };
    }
}