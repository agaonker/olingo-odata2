/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 *        or more contributor license agreements.  See the NOTICE file
 *        distributed with this work for additional information
 *        regarding copyright ownership.  The ASF licenses this file
 *        to you under the Apache License, Version 2.0 (the
 *        "License"); you may not use this file except in compliance
 *        with the License.  You may obtain a copy of the License at
 * 
 *          http://www.apache.org/licenses/LICENSE-2.0
 * 
 *        Unless required by applicable law or agreed to in writing,
 *        software distributed under the License is distributed on an
 *        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *        KIND, either express or implied.  See the License for the
 *        specific language governing permissions and limitations
 *        under the License.
 ******************************************************************************/
package org.apache.olingo.odata2.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.odata2.api.commons.HttpHeaders;
import org.apache.olingo.odata2.api.commons.ODataHttpMethod;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataNotAcceptableException;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.core.commons.ContentType;
import org.apache.olingo.odata2.core.uri.UriInfoImpl;
import org.apache.olingo.odata2.core.uri.UriType;
import org.junit.Test;
import org.mockito.Mockito;

/**
 *  
 */
public class ContentNegotiatorTest {
  
  private void negotiateContentType(final List<ContentType> contentTypes, final List<ContentType> supportedTypes, final String expected) throws ODataException {
    UriInfoImpl uriInfo = Mockito.mock(UriInfoImpl.class);
    ODataRequest request = Mockito.mock(ODataRequest.class);
    final ContentType contentType = new ContentNegotiator(request, uriInfo).contentNegotiation(contentTypes, supportedTypes);
    assertEquals(expected, contentType.toContentTypeString());
  }

  @Test(expected=IllegalArgumentException.class)
  public void invalidContentNegotiatorCreation() {
    final ContentNegotiator contentType = new ContentNegotiator(null, null);
    assertNull(contentType);
  }

  @Test(expected=IllegalArgumentException.class)
  public void invalidContentNegotiatorCreationNullRequest() {
    UriInfoImpl uriInfo = Mockito.mock(UriInfoImpl.class);
    final ContentNegotiator contentType = new ContentNegotiator(null, uriInfo);
    assertNull(contentType);
  }

  @Test(expected=IllegalArgumentException.class)
  public void invalidContentNegotiatorCreationNullUri() {
    ODataRequest request = Mockito.mock(ODataRequest.class);
    final ContentNegotiator contentType = new ContentNegotiator(request, null);
    assertNull(contentType);
  }

  @Test
  public void contentNegotiationEmptyRequest() throws Exception {
    negotiateContentType(
        contentTypes(),
        contentTypes("sup/111", "sup/222"),
        "sup/111");
  }

  @Test
  public void contentNegotiationConcreteRequest() throws Exception {
    negotiateContentType(
        contentTypes("sup/222"),
        contentTypes("sup/111", "sup/222"),
        "sup/222");
  }

  @Test(expected = ODataNotAcceptableException.class)
  public void contentNegotiationNotSupported() throws Exception {
    negotiateContentType(contentTypes("image/gif"), contentTypes("sup/111", "sup/222"), null);
  }

  @Test
  public void contentNegotiationSupportedWildcard() throws Exception {
    negotiateContentType(
        contentTypes("image/gif"),
        contentTypes("sup/111", "sup/222", "*/*"),
        "image/gif");
  }

  @Test
  public void contentNegotiationSupportedSubWildcard() throws Exception {
    negotiateContentType(
        contentTypes("image/gif"),
        contentTypes("sup/111", "sup/222", "image/*"),
        "image/gif");
  }

  @Test
  public void contentNegotiationRequestWildcard() throws Exception {
    negotiateContentType(
        contentTypes("*/*"),
        contentTypes("sup/111", "sup/222"),
        "sup/111");
  }

  @Test
  public void contentNegotiationRequestSubWildcard() throws Exception {
    negotiateContentType(
        contentTypes("sup/*", "*/*"),
        contentTypes("bla/111", "sup/222"),
        "sup/222");
  }

  @Test
  public void contentNegotiationRequestSubtypeWildcard() throws Exception {
    negotiateContentType(
        contentTypes("sup2/*"),
        contentTypes("sup1/111", "sup2/222", "sup2/333"),
        "sup2/222");
  }

  @Test
  public void contentNegotiationRequestResponseWildcard() throws Exception {
    negotiateContentType(contentTypes("*/*"), contentTypes("*/*"), "*/*");
  }

  @Test
  public void contentNegotiationManyRequests() throws Exception {
    negotiateContentType(
        contentTypes("bla/111", "bla/blub", "sub2/222"),
        contentTypes("sub1/666", "sub2/222", "sub3/333"),
        "sub2/222");
  }

  @Test(expected = ODataNotAcceptableException.class)
  public void contentNegotiationCharsetNotSupported() throws Exception {
    negotiateContentType(
        contentTypes("text/plain;charset=iso-8859-1"),
        contentTypes("sup/111", "sup/222"),
        "sup/222");
  }

  @Test
  public void contentNegotiationWithODataVerbose() throws Exception {
    negotiateContentType(
        contentTypes("text/plain;q=0.5", "application/json;odata=verbose;q=0.2", "*/*"),
        contentTypes("application/json;charset=utf-8", "sup/222"),
        "application/json;charset=utf-8");
  }

  @Test
  public void contentNegotiationDefaultCharset() throws Exception {
    negotiateContentTypeCharset("application/xml", "application/xml;charset=utf-8", false);
  }

  @Test
  public void contentNegotiationDefaultCharsetAsDollarFormat() throws Exception {
    negotiateContentTypeCharset("application/xml", "application/xml;charset=utf-8", true);
  }

  @Test
  public void contentNegotiationSupportedCharset() throws Exception {
    negotiateContentTypeCharset("application/xml; charset=utf-8", "application/xml;charset=utf-8", false);
  }

  @Test
  public void contentNegotiationSupportedCharsetAsDollarFormat() throws Exception {
    negotiateContentTypeCharset("application/xml; charset=utf-8", "application/xml;charset=utf-8", true);
  }

  private void negotiateContentTypeCharset(final String requestType, final String supportedType, final boolean asFormat) throws ODataException {
    UriInfoImpl uriInfo = Mockito.mock(UriInfoImpl.class);
    Mockito.when(uriInfo.getUriType()).thenReturn(UriType.URI1);
    if (asFormat) {
      Mockito.when(uriInfo.getFormat()).thenReturn(requestType);
    }

    List<String> acceptedContentTypes = Arrays.asList(requestType);
    List<String> supportedContentTypes = Arrays.asList(supportedType);

    ODataRequest request = Mockito.mock(ODataRequest.class);
    Mockito.when(request.getMethod()).thenReturn(ODataHttpMethod.GET);
    Mockito.when(request.getRequestHeaderValue(HttpHeaders.ACCEPT)).thenReturn(requestType);
    Mockito.when(request.getAcceptHeaders()).thenReturn(acceptedContentTypes);
    
    
    // perform
    ContentNegotiator negotiator = new ContentNegotiator(request, uriInfo);
    String negotiatedContentType = negotiator.doAcceptContentNegotiation(supportedContentTypes).toContentTypeString();

    // verify
    assertEquals(supportedType, negotiatedContentType);
  }

  private List<ContentType> contentTypes(final String... contentType) {
    List<ContentType> ctList = new ArrayList<ContentType>();
    for (String ct : contentType) {
      ctList.add(ContentType.create(ct));
    }
    return ctList;
  }
}
