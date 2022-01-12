package com.hipcommerce.common.web.pebble;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hipcommerce.common.web.util.ServletUtils;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.Function;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.net.URI;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;

@RequiredArgsConstructor
public class PebbleViewExtension extends AbstractExtension {

  private final MessageSourceAccessor messageSourceAccessor;
  private final ObjectMapper objectMapper;

  public Map<String, Filter> getFilters() {
    Map<String, Filter> filters = new HashMap<>(2);
    filters.put("ts", new FilterTimestamp());
    filters.put("nf", new FilterNumbFormat());
    filters.put("toJSON", new FilterJson(objectMapper));
    filters.put("crlf", new FilterCrLf());
    return filters;
  }

  @Override
  public Map<String, Function> getFunctions() {
    Map<String, Function> functions = new HashMap<>(2);
    functions.put("url", new FunctionUrl());
    functions.put("lang", new FunctionLang(messageSourceAccessor));
    return functions;
  }

  private static class FunctionUrl implements Function {

    @Override
    public List<String> getArgumentNames() {
      return Arrays.asList(new String[]{"path", "param", "nocache", "addparam"});
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Map<String, Object> arg0, PebbleTemplate self, EvaluationContext context,
        int lineNumber) {
      String url = "";
      HttpServletRequest request = ServletUtils.getCurrentRequest();
      if (request != null) {

        String path = arg0.get("path").toString().trim();
        if (!path.startsWith("/")) {
          String suffix = request.getRequestURL().toString();
          suffix = suffix.substring(0, suffix.lastIndexOf("/") + 1);
          URI u = URI.create(suffix + path).normalize();
          url = u.toString();
        } else {
          url = request.getContextPath() + path;
        }

        if (arg0.containsKey("param") && arg0.get("param") != null
            && request.getQueryString() != null) {
          try {
            List<String> arr = (List<String>) arg0.get("param");

            StringBuilder params = new StringBuilder();
            String[] queries = request.getQueryString().split("&");
            for (String query : queries) {
              if (query.length() > 0 && arr != null && arr.size() > 0 && arr
                  .contains(query.substring(0, query.indexOf("=")))) {
                continue;
              }
              params.append("&" + query);
            }

            if (params.toString() != null && !params.toString().trim().isEmpty()) {
              String result = params.toString().substring(1);
              url += (url.contains("?") ? "&" : "?") + result;
            }
          } catch (Exception ex) {
          }
        }
      } else {
        url = arg0.get("path").toString();
      }

      if (!arg0.getOrDefault("addparam", "").toString().isEmpty()) {
        url += (url.contains("?") ? "&" : "?") + arg0.getOrDefault("addparam", "");
      }

      return url;
    }
  }

  private static class FilterCrLf implements Filter {

    @Override
    public List<String> getArgumentNames() {
      return null;
    }

    @Override
    public Object apply(Object input, Map<String, Object> arg1, PebbleTemplate self,
        EvaluationContext context, int lineNumber) throws PebbleException {
      if (input == null) {
        return null;
      }
      try {
        return input.toString().replaceAll("\n", "<br />");
      } catch (Exception ex) {
        return input;
      }
    }
  }

  @RequiredArgsConstructor
  private static class FilterJson implements Filter {

    private final ObjectMapper objectMapper;

    @Override
    public List<String> getArgumentNames() {
      return null;
    }

    @Override
    public Object apply(Object input, Map<String, Object> arg1, PebbleTemplate self,
        EvaluationContext context,
        int lineNumber) throws PebbleException {
      if (input == null) {
        return null;
      }
      try {
        return objectMapper.writeValueAsString(input);
      } catch (Exception ex) {
        // nothing
      }
      return null;
    }
  }

  private static class FilterNumbFormat implements Filter {

    @Override
    public List<String> getArgumentNames() {
      return Arrays.asList(new String[]{"size", "hour"});
    }

    @Override
    public Object apply(Object input, Map<String, Object> arg1, PebbleTemplate self,
        EvaluationContext context,
        int lineNumber) throws PebbleException {
      if (input == null) {
        return null;
      }
      Integer i = Integer.parseInt(input.toString());

      if (arg1.get("hour") != null && arg1.get("hour").equals(true)) {
        i = i % 24;
      }

      DecimalFormat df = new DecimalFormat("00");
      return df.format(i);
    }
  }

  private static class FilterTimestamp implements Filter {

    @Override
    public List<String> getArgumentNames() {
      return null;
    }

    @Override
    public Object apply(Object input, Map<String, Object> arg1, PebbleTemplate self,
        EvaluationContext context,
        int lineNumber) throws PebbleException {
      if (input == null) {
        return null;
      }
      return new Date(Long.parseLong(input.toString()) * 1000);
    }
  }

  private static class FunctionLang implements Function {

    private MessageSourceAccessor messageSource;

    /**
     * Instantiates a new Function lang.
     *
     * @param messageSource the message source
     */
    public FunctionLang(MessageSourceAccessor messageSource) {
      this.messageSource = messageSource;

    }

    @Override
    public List<String> getArgumentNames() {
      return Arrays.asList(new String[]{"arg0", "arg1", "arg2", "ignore"});
    }

    @Override
    public Object execute(Map<String, Object> arg0, PebbleTemplate self, EvaluationContext context,
        int lineNumber) {

      StringBuilder sb = new StringBuilder();
      sb.append(arg0.get("arg0"));
      if (arg0.get("arg1") != null) {
        sb.append((!sb.toString().isEmpty()) ? "." : "");
        sb.append(arg0.get("arg1"));
      }
      if (arg0.get("arg2") != null) {
        sb.append((!sb.toString().isEmpty()) ? "." : "");
        sb.append(arg0.get("arg2"));
      }

      try {
        return messageSource.getMessage(sb.toString());
      } catch (Exception ex) {
        if (arg0.get("ignore") != null
            && Boolean.parseBoolean(arg0.get("ignore").toString()) == true) {
          return "";
        }
        return arg0.get("arg1") != null ? arg0.get("arg1") : arg0.get("arg0");
      }
    }
  }
}
