/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.olingo.odata2.jpa.processor.core;

import java.io.InputStream;
import java.util.List;

import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityLinkUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetCountUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetLinksUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntityUriInfo;
import org.apache.olingo.odata2.api.uri.info.GetFunctionImportUriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.apache.olingo.odata2.api.uri.info.PutMergePatchUriInfo;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAProcessor;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPAException;

public class ODataJPAProcessorDefault extends ODataJPAProcessor {

  public ODataJPAProcessorDefault(final ODataJPAContext oDataJPAContext) {
    super(oDataJPAContext);
    if (oDataJPAContext == null) {
      throw new IllegalArgumentException(ODataJPAException.ODATA_JPACTX_NULL);
    }
  }

  @Override
  public ODataResponse readEntitySet(final GetEntitySetUriInfo uriParserResultView, final String contentType)
      throws ODataException {

    List<Object> jpaEntities = jpaProcessor.process(uriParserResultView);

    ODataResponse oDataResponse =
        responseBuilder.build(uriParserResultView, jpaEntities, contentType);

    close();

    return oDataResponse;
  }

  @Override
  public ODataResponse readEntity(final GetEntityUriInfo uriParserResultView, final String contentType)
      throws ODataException {

    Object jpaEntity = jpaProcessor.process(uriParserResultView);

    ODataResponse oDataResponse =
        responseBuilder.build(uriParserResultView, jpaEntity, contentType);

    return oDataResponse;
  }

  @Override
  public ODataResponse countEntitySet(final GetEntitySetCountUriInfo uriParserResultView, final String contentType)
      throws ODataException {

    long jpaEntityCount = jpaProcessor.process(uriParserResultView);

    ODataResponse oDataResponse = responseBuilder.build(jpaEntityCount);

    return oDataResponse;
  }

  @Override
  public ODataResponse existsEntity(final GetEntityCountUriInfo uriInfo, final String contentType)
      throws ODataException {

    long jpaEntityCount = jpaProcessor.process(uriInfo);

    ODataResponse oDataResponse = responseBuilder.build(jpaEntityCount);

    return oDataResponse;
  }

  @Override
  public ODataResponse createEntity(final PostUriInfo uriParserResultView, final InputStream content,
      final String requestContentType, final String contentType) throws ODataException {

    Object createdJpaEntity = jpaProcessor.process(uriParserResultView, content, requestContentType);

    ODataResponse oDataResponse =
        responseBuilder.build(uriParserResultView, createdJpaEntity, contentType);

    return oDataResponse;
  }

  @Override
  public ODataResponse updateEntity(final PutMergePatchUriInfo uriParserResultView, final InputStream content,
      final String requestContentType, final boolean merge, final String contentType) throws ODataException {

    Object jpaEntity = jpaProcessor.process(uriParserResultView, content, requestContentType);

    ODataResponse oDataResponse = responseBuilder.build(uriParserResultView, jpaEntity);

    return oDataResponse;
  }

  @Override
  public ODataResponse deleteEntity(final DeleteUriInfo uriParserResultView, final String contentType)
      throws ODataException {

    Object deletedObj = jpaProcessor.process(uriParserResultView, contentType);

    ODataResponse oDataResponse = responseBuilder.build(uriParserResultView, deletedObj);
    return oDataResponse;
  }

  @Override
  public ODataResponse executeFunctionImport(final GetFunctionImportUriInfo uriParserResultView,
      final String contentType) throws ODataException {

    List<Object> resultEntity = jpaProcessor.process(uriParserResultView);

    ODataResponse oDataResponse =
        responseBuilder.build(uriParserResultView, resultEntity, contentType);

    return oDataResponse;
  }

  @Override
  public ODataResponse executeFunctionImportValue(final GetFunctionImportUriInfo uriParserResultView,
      final String contentType) throws ODataException {

    List<Object> result = jpaProcessor.process(uriParserResultView);

    ODataResponse oDataResponse =
        responseBuilder.build(uriParserResultView, result, contentType);

    return oDataResponse;
  }

  @Override
  public ODataResponse readEntityLink(final GetEntityLinkUriInfo uriParserResultView, final String contentType)
      throws ODataException {

    Object jpaEntity = jpaProcessor.process(uriParserResultView);

    ODataResponse oDataResponse =
        responseBuilder.build(uriParserResultView, jpaEntity, contentType);

    return oDataResponse;
  }

  @Override
  public ODataResponse readEntityLinks(final GetEntitySetLinksUriInfo uriParserResultView, final String contentType)
      throws ODataException {

    List<Object> jpaEntity = jpaProcessor.process(uriParserResultView);

    ODataResponse oDataResponse =
        responseBuilder.build(uriParserResultView, jpaEntity, contentType);

    return oDataResponse;
  }

  @Override
  public ODataResponse createEntityLink(final PostUriInfo uriParserResultView, final InputStream content,
      final String requestContentType, final String contentType) throws ODataException {

    jpaProcessor.process(uriParserResultView, content, requestContentType, contentType);

    return ODataResponse.newBuilder().build();
  }

  @Override
  public ODataResponse updateEntityLink(final PutMergePatchUriInfo uriParserResultView, final InputStream content,
      final String requestContentType, final String contentType) throws ODataException {

    jpaProcessor.process(uriParserResultView, content, requestContentType, contentType);

    return ODataResponse.newBuilder().build();
  }

  @Override
  public ODataResponse deleteEntityLink(final DeleteUriInfo uriParserResultView, final String contentType)
      throws ODataException {

    jpaProcessor.process(uriParserResultView, contentType);
    return ODataResponse.newBuilder().build();

  }
}
