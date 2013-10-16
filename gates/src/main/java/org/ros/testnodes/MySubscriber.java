/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.testnodes;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

/**
 * A simple {@link Subscriber} {@link NodeMain}.
 * 
 * @author damonkohler@google.com (Damon Kohler)
 * @author j (also)
 */
public class MySubscriber extends AbstractNodeMain {

  /**
   * Define the default name for this node
   */
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("mySubscriber");
  }

  // define the topic of messages
  public final String topic = "chatterTopic";
  
  /**
   * after start of this node:
   * note: ConnectedNode serves as a factory for everything for communication etc..
   */
  @Override
  public void onStart(ConnectedNode connectedNode) {
    final Log log = connectedNode.getLog();
    // subscribe to given topic
    Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber(topic, std_msgs.String._TYPE);
    subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
    	// print messages to console
      @Override
      public void onNewMessage(std_msgs.String message) {
        log.info("I heard message: \"" + message.getData() + 
        		"\" published on topic: '"+topic+"'");
      }
    });
  }
}
