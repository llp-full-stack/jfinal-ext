package com.jfinal.ext.interceptor;

import java.util.Locale;
import java.util.regex.Pattern;

import org.joor.Reflect;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.render.Render;

public class I18nInterceptor implements Interceptor {
    private String defaultLanguage = "zh";
    private String defaultCountry = "CN";
    private String languagePara = "language";
    private String countryPara = "country";
    private String localePara = "locale";

    private static String excludeViewRegex;

    private Pattern pattern;

    @Override
    public void intercept(ActionInvocation ai) {
        if (StringKit.notBlank(excludeViewRegex)) {
            pattern = Pattern.compile(excludeViewRegex);
        }
        Controller controller = ai.getController();
        String language = controller.getPara(languagePara, defaultLanguage);
        String country = controller.getPara(countryPara, defaultCountry);
        Locale locale = new Locale(language, country);
        controller.setLocaleToCookie(locale);
        controller.setAttr(localePara, locale);
        ai.invoke();
        Render render = controller.getRender();
        String view = Reflect.on(render).get("view");
        if (pattern != null && pattern.matcher(view).matches()) {
            return;
        }
        String prefix = language;
        if (StringKit.notBlank(country)) {
            prefix += "_" + country;
        }
        Reflect.on(render).set("view", prefix + "/" + view);
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getDefaultCountry() {
        return defaultCountry;
    }

    public void setDefaultCountry(String defaultCountry) {
        this.defaultCountry = defaultCountry;
    }

    public String getLanguagePara() {
        return languagePara;
    }

    public void setLanguagePara(String languagePara) {
        this.languagePara = languagePara;
    }

    public String getCountryPara() {
        return countryPara;
    }

    public void setCountryPara(String countryPara) {
        this.countryPara = countryPara;
    }

    public String getLocalePara() {
        return localePara;
    }

    public void setLocalePara(String localePara) {
        this.localePara = localePara;
    }

    public static String getExcludeViewRegex() {
        return excludeViewRegex;
    }

    public static void setExcludeViewRegex(String excludeViewRegex) {
        I18nInterceptor.excludeViewRegex = excludeViewRegex;
    }

}
