package app.framework;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import java.lang.reflect.*;
import java.util.*;

public class ActionParamBinder {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Object[] bind(
            ActionMap handler,
            HttpServletRequest req,
            HttpServletResponse resp,
            Map<String, String> vars) throws Exception {

        Parameter[] params = handler.getMethod().getParameters();
        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Parameter p = params[i];

            // Inject servlet objects directly
            if (p.getType() == HttpServletRequest.class) {
                args[i] = req; continue;
            }
            if (p.getType() == HttpServletResponse.class) {
                args[i] = resp; continue;
            }

            // ?param=value from URL query string
            if (p.isAnnotationPresent(ActionQueryParam.class)) {
                String name = p.getAnnotation(ActionQueryParam.class).value();
                args[i] = convert(req.getParameter(name), p.getType());
                continue;
            }

            // /mentee/{id} path segments
            if (p.isAnnotationPresent(ActionPathParam.class)) {
                String name = p.getAnnotation(ActionPathParam.class).value();
                args[i] = convert(vars.get(name), p.getType());
                continue;
            }

            // POST body — JSON or HTML form
            if (p.isAnnotationPresent(ActionRequestBody.class)) {
                String ct = req.getContentType();
                if (ct != null && ct.contains("json")) {
                    args[i] = mapper.readValue(req.getInputStream(), p.getType());
                } else {
                    args[i] = bindForm(req, p.getType());
                }
                continue;
            }

            throw new RuntimeException("Unsupported param: " + p);
        }

        return args;
    }

    // Maps HTML form fields directly onto a model object by field name
    private static Object bindForm(HttpServletRequest req, Class<?> clazz) throws Exception {
        Object obj = clazz.getDeclaredConstructor().newInstance();
        for (Map.Entry<String, String[]> e : req.getParameterMap().entrySet()) {
            try {
                Field f = clazz.getDeclaredField(e.getKey());
                f.setAccessible(true);
                f.set(obj, convert(e.getValue()[0], f.getType()));
            } catch (NoSuchFieldException ignored) {}
        }
        return obj;
    }

    private static Object convert(String v, Class<?> t) {
        if (v == null) return null;
        if (t == String.class)                       return v;
        if (t == int.class || t == Integer.class)    return Integer.parseInt(v);
        if (t == long.class || t == Long.class)      return Long.parseLong(v);
        return v;
    }
}