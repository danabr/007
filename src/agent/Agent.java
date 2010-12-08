package agent;

import agent.discovery.Peer;

/**
 * Interface defining the minimal capabilities of an agent.
 * @author Daniel Abrahamsson
 */
public interface Agent
{
    /**
      * An agent server invokes this method. It is used to tell an agent
      * that he is successfully arrived at a server and is ready to run.
      * This method should invoke the method start of the object that
      * represents the agent.
      * @param srv reference to that server object that received the agent object
      * @param srvInetAddr the server's IP address
      * @param serverPort  the server's server port
     */
    public void agentArrived(AgentServer srv,
                             java.net.InetAddress srvInetAddr,
                             int serverPort);

    /**
     * @return The agent's home (where it was originally sent from)
     */
    public Peer getHome();
}