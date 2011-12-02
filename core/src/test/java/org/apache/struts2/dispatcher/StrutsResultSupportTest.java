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

package org.apache.struts2.dispatcher;

import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.xwork2.ActionContext;
import org.apache.struts2.xwork2.ActionInvocation;
import org.apache.struts2.xwork2.ActionSupport;
import org.apache.struts2.xwork2.util.ValueStack;
import org.easymock.EasyMock;

/**
 * Test case for StrutsResultSupport.
 */
public class StrutsResultSupportTest extends StrutsTestCase {


    public void testParse() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new ActionSupport() {
            public String getMyLocation() {
                return "ThisIsMyLocation";
            }
        });

        ActionInvocation mockActionInvocation = EasyMock.createNiceMock(ActionInvocation.class);
        mockActionInvocation.getStack();
        EasyMock.expectLastCall().andReturn(stack);
        EasyMock.replay(mockActionInvocation);

        InternalStrutsResultSupport result = new InternalStrutsResultSupport();
        result.setParse(true);
        result.setEncode(false);
        result.setLocation("/pages/myJsp.jsp?location=${myLocation}");

        result.execute(mockActionInvocation);

        assertNotNull(result.getInternalLocation());
        assertEquals("/pages/myJsp.jsp?location=ThisIsMyLocation", result.getInternalLocation());
        EasyMock.verify(mockActionInvocation);
    }

    public void testParseAndEncode() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new ActionSupport() {
            public String getMyLocation() {
                return "/myPage?param=value&param1=value1";
            }
        });

        ActionInvocation mockActionInvocation = EasyMock.createNiceMock(ActionInvocation.class);
        mockActionInvocation.getStack();
        EasyMock.expectLastCall().andReturn(stack);
        EasyMock.replay(mockActionInvocation);

        InternalStrutsResultSupport result = new InternalStrutsResultSupport();
        result.setParse(true);
        result.setEncode(true);
        result.setLocation("/pages/myJsp.jsp?location=${myLocation}");

        result.execute(mockActionInvocation);

        assertNotNull(result.getInternalLocation());
        assertEquals("/pages/myJsp.jsp?location=%2FmyPage%3Fparam%3Dvalue%26param1%3Dvalue1", result.getInternalLocation());
        EasyMock.verify(mockActionInvocation);
    }


    public void testNoParseAndEncode() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(new ActionSupport() {
            public String getMyLocation() {
                return "myLocation.jsp";
            }
        });

        ActionInvocation mockActionInvocation = EasyMock.createNiceMock(ActionInvocation.class);
        EasyMock.replay(mockActionInvocation);

        InternalStrutsResultSupport result = new InternalStrutsResultSupport();
        result.setParse(false);
        result.setEncode(false); // don't really need this, as encode is only valid when parse is true.
        result.setLocation("/pages/myJsp.jsp?location=${myLocation}");

        result.execute(mockActionInvocation);

        assertNotNull(result.getInternalLocation());
        assertEquals("/pages/myJsp.jsp?location=${myLocation}", result.getInternalLocation());
        EasyMock.verify(mockActionInvocation);
    }


    public static class InternalStrutsResultSupport extends StrutsResultSupport {
        private String _internalLocation = null;

        protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
            _internalLocation = finalLocation;
        }

        public String getInternalLocation() {
            return _internalLocation;
        }
    }
}
