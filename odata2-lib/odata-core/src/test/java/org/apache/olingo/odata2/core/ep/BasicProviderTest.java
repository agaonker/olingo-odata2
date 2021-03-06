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
package org.apache.olingo.odata2.core.ep;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.edm.provider.EdmProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.core.commons.ContentType;
import org.apache.olingo.odata2.testutil.helper.StringHelper;
import org.apache.olingo.odata2.testutil.mock.EdmTestProvider;
import org.apache.olingo.odata2.testutil.mock.MockFacade;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;

/**
 *  
 */
public class BasicProviderTest extends AbstractProviderTest {

  public BasicProviderTest(final StreamWriterImplType type) {
    super(type);
  }

  protected static BasicEntityProvider provider = new BasicEntityProvider();

  @Test
  public void writeMetadata() throws Exception {
    Map<String, String> predefinedNamespaces = new HashMap<String, String>();
    predefinedNamespaces.put("annoPrefix", "http://annoNamespace");
    predefinedNamespaces.put("foo", "http://foo");
    predefinedNamespaces.put("annoPrefix2", "http://annoNamespace");
    predefinedNamespaces.put("annoPrefix", "http://annoNamespace");

    ODataResponse response = provider.writeMetadata(null, predefinedNamespaces);
    assertNotNull(response);
    assertNotNull(response.getEntity());
    assertNull("BasicProvider should not set content header", response.getContentHeader());
    String metadata = StringHelper.inputStreamToString((InputStream) response.getEntity());
    assertTrue(metadata.contains("xmlns:foo=\"http://foo\""));
    assertTrue(metadata.contains("xmlns:annoPrefix=\"http://annoNamespace\""));
    assertTrue(metadata.contains("xmlns:annoPrefix2=\"http://annoNamespace\""));
  }

  private void setNamespaces() {
    Map<String, String> prefixMap = new HashMap<String, String>();
    prefixMap.put("edmx", Edm.NAMESPACE_EDMX_2007_06);
    prefixMap.put("m", Edm.NAMESPACE_M_2007_08);
    prefixMap.put("a", Edm.NAMESPACE_EDM_2008_09);
    prefixMap.put("annoPrefix", "http://annoNamespace");
    prefixMap.put("prefix", "namespace");
    prefixMap.put("b", "RefScenario");
    prefixMap.put("pre", "namespaceForAnno");
    XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(prefixMap));
  }

  @Test
  public void writeMetadata2() throws Exception {
    EdmProvider testProvider = new EdmTestProvider();

    Map<String, String> predefinedNamespaces = new HashMap<String, String>();
    predefinedNamespaces.put("annoPrefix", "http://annoNamespace");
    predefinedNamespaces.put("foo", "http://foo");
    predefinedNamespaces.put("annoPrefix2", "http://annoNamespace");
    predefinedNamespaces.put("annoPrefix", "http://annoNamespace");
    predefinedNamespaces.put("prefix", "namespace");
    predefinedNamespaces.put("pre", "namespaceForAnno");

    ODataResponse response = provider.writeMetadata(testProvider.getSchemas(), predefinedNamespaces);
    assertNotNull(response);
    assertNotNull(response.getEntity());
    assertNull("BasicProvider should not set content header", response.getContentHeader());
    String metadata = StringHelper.inputStreamToString((InputStream) response.getEntity());

    setNamespaces();
    assertXpathExists(
        "/edmx:Edmx/edmx:DataServices/a:Schema/a:EntityType/a:Property[@Name and @Type and @Nullable and " +
            "@annoPrefix:annoName]",
        metadata);
    assertXpathExists(
        "/edmx:Edmx/edmx:DataServices/a:Schema/a:EntityType/a:Property[@Name and @Type and @m:FC_TargetPath and " +
            "@annoPrefix:annoName]",
        metadata);
    assertXpathExists("/edmx:Edmx/edmx:DataServices/a:Schema/a:EntityType/a:Property[@Name=\"EmployeeName\"]",
        metadata);
    assertXpathExists(
        "/edmx:Edmx/edmx:DataServices/a:Schema/a:EntityType/a:Property[@Name=\"EmployeeName\"]/a:propertyAnnoElement",
        metadata);
    assertXpathExists(
        "/edmx:Edmx/edmx:DataServices/a:Schema/a:EntityType/a:Property[@Name=\"EmployeeName\"]/a:propertyAnnoElement2",
        metadata);

    assertXpathExists("/edmx:Edmx/edmx:DataServices/a:Schema/a:schemaElementTest1", metadata);
    assertXpathExists("/edmx:Edmx/edmx:DataServices/a:Schema/a:schemaElementTest1/b:schemaElementTest2", metadata);
    assertXpathExists("/edmx:Edmx/edmx:DataServices/a:Schema/a:schemaElementTest1/prefix:schemaElementTest3", metadata);
    assertXpathExists(
        "/edmx:Edmx/edmx:DataServices/a:Schema/a:schemaElementTest1/a:schemaElementTest4[@rel=\"self\" and " +
            "@pre:href=\"http://foo\"]",
        metadata);
  }

  @Test
  public void writeMetadata3() throws Exception {
    EdmProvider testProvider = new EdmTestProvider();

    ODataResponse response = provider.writeMetadata(testProvider.getSchemas(), null);
    assertNotNull(response);
    assertNotNull(response.getEntity());
    assertNull("BasicProvider should not set content header", response.getContentHeader());
    String metadata = StringHelper.inputStreamToString((InputStream) response.getEntity());

    setNamespaces();
    assertXpathExists(
        "/edmx:Edmx/edmx:DataServices/a:Schema/a:EntityType/a:Property[@Name and @Type and @Nullable and " +
            "@annoPrefix:annoName]",
        metadata);
    assertXpathExists(
        "/edmx:Edmx/edmx:DataServices/a:Schema/a:EntityType/a:Property[@Name and @Type and @m:FC_TargetPath and " +
            "@annoPrefix:annoName]",
        metadata);
    assertXpathExists("/edmx:Edmx/edmx:DataServices/a:Schema/a:EntityType/a:Property[@Name=\"EmployeeName\"]",
        metadata);
    assertXpathExists(
        "/edmx:Edmx/edmx:DataServices/a:Schema/a:EntityType/a:Property[@Name=\"EmployeeName\"]/a:propertyAnnoElement",
        metadata);
    assertXpathExists(
        "/edmx:Edmx/edmx:DataServices/a:Schema/a:EntityType/a:Property[@Name=\"EmployeeName\"]/a:propertyAnnoElement2",
        metadata);

    assertXpathExists("/edmx:Edmx/edmx:DataServices/a:Schema/a:schemaElementTest1", metadata);
    assertXpathExists("/edmx:Edmx/edmx:DataServices/a:Schema/a:schemaElementTest1/b:schemaElementTest2", metadata);
    assertXpathExists("/edmx:Edmx/edmx:DataServices/a:Schema/a:schemaElementTest1/prefix:schemaElementTest3", metadata);
    assertXpathExists(
        "/edmx:Edmx/edmx:DataServices/a:Schema/a:schemaElementTest1/a:schemaElementTest4[@rel=\"self\" and " +
            "@pre:href=\"http://foo\"]",
        metadata);
  }

  @Test
  public void writePropertyValue() throws Exception {
    EdmTyped edmTyped = MockFacade.getMockEdm().getEntityType("RefScenario", "Employee").getProperty("Age");
    EdmProperty edmProperty = (EdmProperty) edmTyped;

    ODataResponse response = provider.writePropertyValue(edmProperty, employeeData.get("Age"));
    assertNotNull(response);
    assertNotNull(response.getEntity());
    assertNull("BasicProvider should not set content header", response.getContentHeader());
    String value = StringHelper.inputStreamToString((InputStream) response.getEntity());
    assertEquals(employeeData.get("Age").toString(), value);
  }

  @Test
  public void readPropertyValue() throws Exception {
    final EdmProperty property =
        (EdmProperty) MockFacade.getMockEdm().getEntityType("RefScenario", "Employee").getProperty("Age");

    final Integer age =
        (Integer) provider.readPropertyValue(property, new ByteArrayInputStream("42".getBytes("UTF-8")), null);
    assertEquals(Integer.valueOf(42), age);
  }

  @Test
  public void readPropertyValueWithMapping() throws Exception {
    final EdmProperty property =
        (EdmProperty) MockFacade.getMockEdm().getEntityType("RefScenario", "Employee").getProperty("Age");

    final Long age =
        (Long) provider.readPropertyValue(property, new ByteArrayInputStream("42".getBytes("UTF-8")), Long.class);
    assertEquals(Long.valueOf(42), age);
  }

  @Test(expected = EntityProviderException.class)
  public void readPropertyValueWithInvalidMapping() throws Exception {
    final EdmProperty property =
        (EdmProperty) MockFacade.getMockEdm().getEntityType("RefScenario", "Employee").getProperty("Age");

    final Float age =
        (Float) provider.readPropertyValue(property, new ByteArrayInputStream("42".getBytes("UTF-8")), Float.class);
    assertEquals(Float.valueOf(42), age);
  }

  @Test
  public void readPropertyBinaryValue() throws Exception {
    final byte[] bytes = new byte[] { 1, 2, 3, 4, -128 };
    final EdmProperty property =
        (EdmProperty) MockFacade.getMockEdm().getEntityType("RefScenario2", "Photo").getProperty("Image");

    assertTrue(Arrays.equals(bytes, (byte[]) provider
        .readPropertyValue(property, new ByteArrayInputStream(bytes), null)));
  }

  @Test
  public void writeBinary() throws Exception {
    final byte[] bytes = new byte[] { 49, 50, 51, 52, 65 };
    final ODataResponse response = provider.writeBinary(ContentType.TEXT_PLAIN_CS_UTF_8.toString(), bytes);
    assertNotNull(response);
    assertNotNull(response.getEntity());
    assertEquals(ContentType.TEXT_PLAIN_CS_UTF_8.toString(), response.getContentHeader());
    final String value = StringHelper.inputStreamToString((InputStream) response.getEntity());
    assertEquals("1234A", value);
  }

  @Test
  public void readBinary() throws Exception {
    final byte[] bytes = new byte[] { 1, 2, 3, 4, -128 };
    assertTrue(Arrays.equals(bytes, provider.readBinary(new ByteArrayInputStream(bytes))));
  }
}
