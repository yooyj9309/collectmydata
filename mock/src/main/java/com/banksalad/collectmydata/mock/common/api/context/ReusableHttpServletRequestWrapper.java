package com.banksalad.collectmydata.mock.common.api.context;

import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReusableHttpServletRequestWrapper extends HttpServletRequestWrapper {

  private final byte[] cachedBody;

  public ReusableHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
    super(request);
    InputStream requestInputStream = request.getInputStream();
    this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
  }

  @Override
  public ServletInputStream getInputStream() {
    return new CachedBodyServletInputStream(this.cachedBody);
  }

  @Override
  public BufferedReader getReader() {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
    return new BufferedReader(new InputStreamReader(byteArrayInputStream));
  }

  @Slf4j
  private static class CachedBodyServletInputStream extends ServletInputStream {

    private final InputStream cachedBodyInputStream;

    public CachedBodyServletInputStream(byte[] cachedBody) {
      this.cachedBodyInputStream = new ByteArrayInputStream(cachedBody);
    }

    @Override
    public boolean isFinished() {
      try {
        return cachedBodyInputStream.available() == 0;
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
      return false;
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int read() throws IOException {
      return cachedBodyInputStream.read();
    }
  }
}
