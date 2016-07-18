package org.stepic.plugin.java.auth;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.http.client.utils.URIBuilder;
import org.apache.sanselan.util.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;


public class StepicUserAuthorizer {
  private static final int ourPort = 36656;
  private static volatile StepicUserAuthorizer ourAuthorizer;
  private static final Properties ourProperties = new Properties();
  private static final String REDIRECT_URI = "http://localhost:" + ourPort;
  private static final Logger LOG = Logger.getInstance(StepicUserAuthorizer.class.getName());

  private Server myServer;
  private String myAccessToken;
  private String myRefreshToken;

  private StepicUserAuthorizer() {
//    loadProperties();
  }

  public static StepicUserAuthorizer getInstance() {
    StepicUserAuthorizer authorizer = ourAuthorizer;
    if (authorizer == null) {
//      synchronized (CheckIOMissionGetter.class) {
        authorizer = ourAuthorizer;
        if (authorizer == null) {
          ourAuthorizer = authorizer = new StepicUserAuthorizer();
        }
//      }
    }
    return authorizer;
  }

  public String authorizeAndGetUser() {
    try {
      if (getServer() == null || !getServer().isRunning()) {
        startServer();
        LOG.info("Server started");
      }
      openAuthorizationPage();
      LOG.info("Authorization page opened");
      getServer().join();
    }
    catch (InterruptedException e) {
      LOG.warn(e.getMessage());
    }

    return getAccessToken();
  }

//  public void setTokensFromRefreshToken(@NotNull final String refreshToken) throws IOException {
//    final HttpPost request = makeRefreshTokenRequest(refreshToken);
//    getAndSetTokens(request);
//  }

//  private void setTokensFirstTime(@Nullable final String code) throws IOException {
//    if (code != null) {
//      final HttpPost request = makeAccessTokenRequest(code);
//      getAndSetTokens(request);
//    }
//    else {
//      throw new IOException("Code is null");
//    }
//  }

  private void startServer() {
    myServer = new Server(ourPort);
    MyContextHandler contextHandler = new MyContextHandler();
    getServer().setHandler(contextHandler);
    try {
      getServer().start();
    }
    catch (Exception e) {
      LOG.warn(e.getMessage());
    }
  }

  private static void openAuthorizationPage() {
    try {
//      final URI url = new URIBuilder(CheckIOConnectorBundle.message("authorization.url",
//                                                                    CheckIOConnectorBundle.message("checkio.url")))
//        .addParameter(CheckIOConnectorBundle.message("redirect.uri.parameter"), REDIRECT_URI)
//        .addParameter(CheckIOConnectorBundle.message("response.type.parameter"), CheckIOConnectorBundle.message("code.parameter"))
//        .addParameter(CheckIOConnectorBundle.message("client.id.parameter"),
//                      ourProperties.getProperty(CheckIOConnectorBundle.message("client.id.property.value")))
//        .build();
      final URI url = new URIBuilder("https://stepic.org/oauth2/authorize/")
              .addParameter("grant_type", "authorization-code")
              .addParameter("client_id", "NHpcRZlHp9PC6tsycZkYF6VL4dxsN8ik1rQlXtjK")
              .addParameter("redirect_uri", "http://localhost:36656")
              .build();
      LOG.info("Auth url created");
      BrowserUtil.browse(url);
      LOG.info("Url browsed");
    }
    catch (URISyntaxException e) {
      LOG.warn(e.getMessage());
    }
  }


//  public StepicUser getUser( @NotNull final String accessToken) {
//    StepicUser user = new StepicUser();
//    try {
//
//      final URI uri = new URIBuilder(CheckIOConnectorBundle.message("userinfo.url", CheckIOConnectorBundle.message("checkio.url")))
//        .addParameter(CheckIOConnectorBundle.message("access.token.parameter"), accessToken)
//        .build();
//      final HttpGet request = new HttpGet(uri);
//      final HttpResponse response = executeRequest(request);
//      if (response != null) {
//        final HttpEntity entity = response.getEntity();
//        final String userInfo = EntityUtils.toString(entity);
//        final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
//        user = gson.fromJson(userInfo, StepicUser.class);
//      }
//    }
//    catch (URISyntaxException | IOException e) {
//      LOG.warn(e.getMessage());
//    }
//    return user;
//  }

  private void loadProperties() {
    try {
      InputStream is = this.getClass().getResourceAsStream("/properties/oauthData.properties");
      if (is == null) {
        LOG.warn("Properties file not found.");
      }
      ourProperties.load(is);
    }
    catch (IOException e) {
      LOG.warn(e.getMessage());
    }
  }
//
//  private static HttpPost makeRefreshTokenRequest(@NotNull final String refreshToken) {
//    final HttpPost request = new HttpPost(CheckIOConnectorBundle.message("token.url", CheckIOConnectorBundle.message("checkio.url")));
//    try {
//      final List<NameValuePair> requestParameters = new ArrayList<>();
//      requestParameters.add(new BasicNameValuePair(CheckIOConnectorBundle.message("grant.type.parameter"),
//                                                   CheckIOConnectorBundle.message("refresh.token.parameter")));
//      requestParameters.add(new BasicNameValuePair(CheckIOConnectorBundle.message("client.id.parameter"), ourProperties.getProperty(
//        CheckIOConnectorBundle.message("client.id.property.value"))));
//      requestParameters.add(new BasicNameValuePair(CheckIOConnectorBundle.message("client.secret.parameter"), ourProperties.getProperty(
//        CheckIOConnectorBundle.message("client.secret.property.value"))));
//      requestParameters.add(new BasicNameValuePair(CheckIOConnectorBundle.message("refresh.token.parameter"), refreshToken));
//
//      request.addHeader(CheckIOConnectorBundle.message("content.type.parameter"), CheckIOConnectorBundle.message("content.type.value"));
//      request.addHeader(CheckIOConnectorBundle.message("accept.parameter"), ContentType.APPLICATION_JSON.getMimeType());
//      request.setEntity(new UrlEncodedFormEntity(requestParameters));
//    }
//    catch (UnsupportedEncodingException e) {
//      LOG.warn(e.getMessage());
//    }
//    return request;
//  }

//  private static HttpPost makeAccessTokenRequest(@NotNull final String code) {
//    final HttpPost request = new HttpPost(CheckIOConnectorBundle.message("token.url", CheckIOConnectorBundle.message("checkio.url")));
//    try {
//      final List<NameValuePair> requestParameters = new ArrayList<>();
//      requestParameters.add(new BasicNameValuePair(CheckIOConnectorBundle.message("code.parameter"), code));
//      requestParameters.add(new BasicNameValuePair(CheckIOConnectorBundle.message("client.secret.parameter"), ourProperties.getProperty(
//        CheckIOConnectorBundle.message("client.secret.property.value"))));
//      requestParameters.add(new BasicNameValuePair(CheckIOConnectorBundle.message("grant.type.parameter"),
//                                                   CheckIOConnectorBundle.message("grant.type.token")));
//      requestParameters.add(new BasicNameValuePair(CheckIOConnectorBundle.message("client.id.parameter"), ourProperties.getProperty(
//        CheckIOConnectorBundle.message("client.id.property.value"))));
//      requestParameters.add(new BasicNameValuePair(CheckIOConnectorBundle.message("redirect.uri.parameter"), REDIRECT_URI));
//
//      request.addHeader(CheckIOConnectorBundle.message("content.type.parameter"), CheckIOConnectorBundle.message("content.type.value"));
//      request.addHeader(CheckIOConnectorBundle.message("accept.parameter"), ContentType.APPLICATION_JSON.getMimeType());
//      request.setEntity(new UrlEncodedFormEntity(requestParameters));
//    }
//    catch (UnsupportedEncodingException e) {
//      LOG.warn(e.getMessage());
//    }
//    return request;
//  }
//
//  private void getAndSetTokens(@NotNull final HttpRequestBase request) throws IOException {
//    final HttpResponse response = executeRequest(request);
//
//    if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//      JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
//      myAccessToken = jsonObject.getString(CheckIOConnectorBundle.message("access.token.parameter"));
//      myRefreshToken = jsonObject.getString(CheckIOConnectorBundle.message("refresh.token.parameter"));
//    }
//  }

//  private CloseableHttpResponse executeRequest(@NotNull HttpRequestBase request) throws IOException {
//    CloseableHttpClient client = CheckIOConnectorsUtil.createClient();
//    if (CheckIOConnectorsUtil.isProxyUrl(request.getURI())) {
//      client = CheckIOConnectorsUtil.getConfiguredClient();
//    }
//    return client.execute(request);
//  }

  private Server getServer() {
    return myServer;
  }

  public String getAccessToken() {
    return myAccessToken;
  }

  public String getRefreshToken() {
    return myRefreshToken;
  }

  private class MyContextHandler extends AbstractHandler {

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
      LOG.info("Handling auth response");
      final String token = httpServletRequest.getParameter("access_token");
      LOG.info("token = " + token);
      myAccessToken = token;

      try {
        final OutputStream os = httpServletResponse.getOutputStream();

        os.write(IOUtils.getInputStreamBytes(getClass().getResourceAsStream("/style/authorizationPage.html")));
        os.close();
//        setTokensFirstTime(code);
      }
      catch (IOException e) {
        stopServerInNewThread();
        LOG.warn(e.getMessage());
      }

      stopServerInNewThread();
    }

    private void stopServerInNewThread() {
      new Thread() {
        @Override
        public void run() {
          try {
            LOG.info("Stopping server");
            getServer().stop();
            LOG.info("Server stopped");
          }
          catch (Exception e) {
            LOG.warn(e.getMessage());
          }
        }
      }.start();
    }
  }
}


