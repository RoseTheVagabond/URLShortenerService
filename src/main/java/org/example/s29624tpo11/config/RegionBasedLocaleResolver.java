package org.example.s29624tpo11.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

public class RegionBasedLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String langParam = request.getParameter("lang");
        if (langParam != null) {
            return getLocaleFromLangParam(langParam);
        }

        String langHeader = request.getHeader("lang");
        if (langHeader != null) {
            return getLocaleFromLangParam(langHeader);
        }

        Locale sessionLocale = (Locale) request.getSession().getAttribute("locale");
        if (sessionLocale != null) {
            return sessionLocale;
        }

        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage != null) {
            String[] languages = acceptLanguage.split(",");
            for (String lang : languages) {
                String langCode = lang.trim().split(";")[0].toLowerCase();

                if (langCode.startsWith("pl")) {
                    return new Locale("pl");
                } else if (langCode.startsWith("de")) {
                    return new Locale("de");
                }
            }
        }
        return Locale.ENGLISH;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        if (locale != null) {
            request.getSession().setAttribute("locale", locale);
        }
    }

    private Locale getLocaleFromLangParam(String langParam) {
        return switch (langParam.toLowerCase()) {
            case "pl" -> new Locale("pl");
            case "de" -> new Locale("de");
            default -> Locale.ENGLISH;
        };
    }
}