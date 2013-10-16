package org.ros.testnodes;

import org.apache.commons.logging.Log;
import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

/**
 * A simple {@link Publisher} {@link NodeMain}.
 * 
 * @author damonkohler@google.com (Damon Kohler)
 * @author j (also)
 */
public class MyPublisher extends AbstractNodeMain {

	/**
	 * default name of the Node
	 */
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("myPublisher");
  }

  // define the common topic
  public final String topic = "chatterTopic";
  
  @Override
  public void onStart(final ConnectedNode connectedNode) {
	  
	// get the ros console
    final Log log = connectedNode.getLog();
	  
	// Create new publisher (publishing messages to a given topic)
    final Publisher<std_msgs.String> publisher =
        connectedNode.newPublisher(topic, std_msgs.String._TYPE);
    
    // This CancellableLoop will be canceled automatically when the node shuts down.
    connectedNode.executeCancellableLoop(new CancellableLoop() {
      private int sequenceNumber;

      @Override
      protected void setup() {
        sequenceNumber = 0;
      }

      @Override
      protected void loop() throws InterruptedException {
        std_msgs.String str = publisher.newMessage();
        str.setData("Hello world! " + sequenceNumber);
        publisher.publish(str);
        
        log.info("I published this: \"" + str.getData() + 
        		"\" on topic: '"+topic+"'");
        
        sequenceNumber++;
        Thread.sleep(1000);
      }
    });
  }
}
