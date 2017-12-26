/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.filter;

import com.xemantic.tadedon.servlet.CacheForcingFilter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Forcing caching for the given URL resource patterns.
 *
 * @author Max Shaposhnik
 */
public class CheCacheForcingFilter extends CacheForcingFilter {

  //  @Inject
  //  @Named("wide.endpoint")
  //  public String checkTokenEndPoint;

  private Set<Pattern> actionPatterns = new HashSet<>();

  @Override
  public void init(FilterConfig filterConfig) {
    Enumeration<String> names = filterConfig.getInitParameterNames();
    while (names.hasMoreElements()) {
      String name = names.nextElement();
      if (name.startsWith("pattern")) {
        actionPatterns.add(Pattern.compile(filterConfig.getInitParameter(name)));
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    for (Pattern pattern : actionPatterns) {

      Cookie[] cookies = ((HttpServletRequest) request).getCookies();
      boolean tokenInvalid = false;
      if (cookies != null) {
        String token = "";
        for (Cookie cookie : cookies) {
          if (cookie.getName().equals("U-TOKEN")) {
            token = cookie.getValue();
            return;
          }
        }

        String checkTokenEndPoint = "http://211.239.163.237/comm-api/api/portal/v1/check_token";
        tokenInvalid = checkToken(checkTokenEndPoint, token);
      }
      if (!tokenInvalid) {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println(
            "<script>alert('잘못된 접근입니다. 포털 시스템을 통하여 접근하시기 바랍니다.'); window.close();</script>");
        out.flush();
        return;
      }
    }
    chain.doFilter(request, response);
  }

  private boolean checkToken(String checkTokenEndPoint, String token) {
    try {
      URL obj = new URL(checkTokenEndPoint);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      con.setRequestMethod("POST");
      con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      con.setDoOutput(true);
      DataOutputStream wr = new DataOutputStream(con.getOutputStream());
      wr.writeBytes("token=" + token);
      wr.flush();
      wr.close();

      int responseCode = con.getResponseCode();

      return (responseCode == 200);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
