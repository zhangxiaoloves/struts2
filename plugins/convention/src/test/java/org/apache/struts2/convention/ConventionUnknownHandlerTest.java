/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.convention;

import org.apache.struts2.xwork2.config.Configuration;
import org.apache.struts2.xwork2.config.entities.PackageConfig;
import org.apache.struts2.xwork2.config.entities.ResultTypeConfig;
import org.apache.struts2.xwork2.inject.Container;
import junit.framework.TestCase;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import javax.servlet.ServletContext;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

public class ConventionUnknownHandlerTest extends TestCase {
    public void testCanonicalizeShouldReturnNullWhenPathIsNull() throws Exception {
        final ConventionUnknownHandler handler = conventionUnknownHandler();

        assertEquals(null, handler.canonicalize(null));
    }

    public void testCanonicalizeShouldCollapseMultipleConsecutiveSlashesIntoJustOne() throws Exception {
        final ConventionUnknownHandler handler = conventionUnknownHandler();

        assertEquals("/should/condense/multiple/consecutive/slashes/into/just-one.ext",
                handler.canonicalize("//should///condense////multiple/////consecutive////slashes///into//just-one.ext"));
    }

    public void testCanonicalizeShouldNotModifySingleSlashes() throws Exception {
        final ConventionUnknownHandler handler = conventionUnknownHandler();

        assertEquals("/should/not/modify/single/slashes.ext",
                handler.canonicalize("/should/not/modify/single/slashes.ext"));
    }

    public void testFindResourceShouldReturnNullAfterTryingEveryExtensionWithoutSuccess() throws Exception {
        final ServletContext servletContext = createStrictMock(ServletContext.class);  // Verifies method call order

        expect(servletContext.getResource("/some/path/which/does/not/exist/for/any/file/with.default_extension"))
                .andReturn(null);
        expect(servletContext.getResource("/some/path/which/does/not/exist/for/any/file/with.non_default_extension"))
                .andReturn(null);
        expect(servletContext.getResource("/some/path/which/does/not/exist/for/any/file/with.some_other_extension"))
                .andReturn(null);

        replay(servletContext);

        final ConventionUnknownHandler handler = conventionUnknownHandler(servletContext);

        final ConventionUnknownHandler.Resource resource = handler.findResource(defaultResultsByExtension(),
                "/some/path/which/does/not/exist/for/any/file/with");

        verify(servletContext);

        assertNull(resource);
    }

    public void testFindResourceShouldLookupResourceWithCanonicalPath() throws Exception {
        final ServletContext servletContext = createStrictMock(ServletContext.class);  // Verifies method call order

        final URL url = new URL("http://localhost");
        expect(servletContext.getResource("/canonicalized/path.default_extension")).andReturn(url);

        replay(servletContext);

        final ConventionUnknownHandler handler = conventionUnknownHandler(servletContext);
        handler.findResource(defaultResultsByExtension(), "///canonicalized//path");

        verify(servletContext);
    }

    public void testFindResourceShouldSetCanonicalizedPathOnResource() throws Exception {
        final ServletContext servletContext = createMock(ServletContext.class);

        final URL url = new URL("http://localhost");
        expect(servletContext.getResource("/canonicalized/path.default_extension")).andReturn(url);

        replay(servletContext);

        final ConventionUnknownHandler handler = conventionUnknownHandler(servletContext);

        final ConventionUnknownHandler.Resource resource = handler.findResource(defaultResultsByExtension(),
                "///canonicalized//path");

        assertEquals("/canonicalized/path.default_extension", resource.path);
    }

    private Configuration configuration(final String packageName) {
        final Configuration mock = createNiceMock(Configuration.class);

        final PackageConfig packageConfiguration = packageConfiguration();
        expect(mock.getPackageConfig(packageName)).andStubReturn(packageConfiguration);

        replay(mock);

        return mock;
    }

    private Container container() {
        final Container mock = createNiceMock(Container.class);

        replay(mock);

        return mock;
    }

    private ConventionUnknownHandler conventionUnknownHandler() {
        return conventionUnknownHandler(null);
    }

    private ConventionUnknownHandler conventionUnknownHandler(final ServletContext servletContext) {
        final String defaultParentPackageName = "DEFAULT PARENT PACKAGE NAME";

        final Configuration configuration = configuration(defaultParentPackageName);
        final Container container = container();

        return new ConventionUnknownHandler(configuration, null, servletContext, container, defaultParentPackageName,
                null, null);
    }

    private Map<String, ResultTypeConfig> defaultResultsByExtension() {
        final Iterator<String> extensions = createMock(Iterator.class);
        final Set<String> keys = createMock(Set.class);
        final Map<String, ResultTypeConfig> mock = createMock(Map.class);

        expect(extensions.hasNext()).andReturn(true).times(3).andReturn(false);
        expect(extensions.next()).andReturn("default_extension").andReturn("non_default_extension")
                .andReturn("some_other_extension");

        expect(keys.iterator()).andReturn(extensions);

        expect(mock.keySet()).andReturn(keys);

        replay(extensions);
        replay(keys);
        replay(mock);

        return mock;
    }

    private PackageConfig packageConfiguration() {
        final PackageConfig mock = createNiceMock(PackageConfig.class);

        replay(mock);

        return mock;
    }
}
