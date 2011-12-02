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

package org.apache.struts2.views.xslt;

import java.util.List;

import org.apache.struts2.xwork2.util.logging.Logger;
import org.apache.struts2.xwork2.util.logging.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleNodeList implements NodeList {
    private Logger log = LoggerFactory.getLogger(SimpleNodeList.class);

    private List<Node> nodes;

    public SimpleNodeList(List<Node> nodes) {
        this.nodes = nodes;
    }

    public int getLength() {
        if (log.isTraceEnabled())
            log.trace("getLength: " + nodes.size());
        return nodes.size();
    }

    public Node item(int i) {
        log.trace("getItem: " + i);
        return nodes.get(i);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SimpleNodeList: [");
        for (int i = 0; i < getLength(); i++)
            sb.append(item(i).getNodeName()).append(',');
        sb.append("]");
        return sb.toString();
    }
}
