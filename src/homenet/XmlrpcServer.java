/*
 * Copyright (c) 2011 Matthew Doll <mdoll at homenet.me>.
 *
 * This file is part of HomeNet.
 *
 * HomeNet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HomeNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HomeNet.  If not, see <http://www.gnu.org/licenses/>.
 */


/*
XmlrpcServer - Basic xmlrpc server implementation

Copyright (c) 2006 Burak Arikan

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General
Public License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330,
Boston, MA  02111-1307  USA
 */
package homenet;

/**
 *
 * @author mdoll
 */
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet.*;
import org.apache.xmlrpc.server.*;
import org.apache.xmlrpc.*;
import org.apache.xmlrpc.common.*;
import org.apache.xmlrpc.webserver.*;
import  org.apache.xmlrpc.metadata.*;


import java.util.*;


public class XmlrpcServer {

    XmlRpcServlet servlet;
    ServletWebServer server;
    PropertyHandlerMapping mapping;
    int port;
    private  HashMap<String,Object> classes = new HashMap();
    
    public class MyServlet extends XmlRpcServlet {

          protected XmlRpcHandlerMapping newXmlRpcHandlerMapping() throws XmlRpcException {
//              PropertyHandlerMapping mapping
//                  = (PropertyHandlerMapping) super.newXmlRpcHandlerMapping();
//              AbstractReflectiveHandlerMapping.AuthenticationHandler handler =
//                  new AbstractReflectiveHandlerMapping.AuthenticationHandler(){
//                          public boolean isAuthorized(XmlRpcRequest pRequest){
//                              XmlRpcHttpRequestConfig config =
//                                  (XmlRpcHttpRequestConfig) pRequest.getConfig();
//                              return isAuthenticated(config.getBasicUserName(),
//                                  config.getBasicPassword());
//                          };
//                  };
//              mapping.setAuthenticationHandler(handler);
//              return mapping;
            // mapping = (PropertyHandlerMapping) newXmlRpcHandlerMapping();
             mapping = new PropertyHandlerMapping();

      mapping.setRequestProcessorFactoryFactory(new myRequestProcessorFactoryFactory());
      mapping.setVoidMethodEnabled(true);
      
      //phm.addHandler(EchoService.class.getName(), EchoService.class);
     // xmlRpcServer.setHandlerMapping(phm);

      //XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
     // serverConfig.setEnabledForExtensions(true);
     // serverConfig.setContentLengthOptional(false);
      
        final XmlRpcSystemImpl systemHandler = new XmlRpcSystemImpl(mapping);

        add("system", systemHandler);
      
      
              
          //   org.apache.xmlrpc.metadata.XmlRpcSystemImpl.addSystemHandler(mapping);
              
              return mapping;
          }
  }
    public class myRequestProcessorFactoryFactory implements RequestProcessorFactoryFactory {

        private final RequestProcessorFactory factory = new myRequestProcessorFactory();
        

        public myRequestProcessorFactoryFactory() {
            
        }
        
        public void addObject(String name,Object object){
            classes.put(name, object);
        }

        public RequestProcessorFactory getRequestProcessorFactory(Class aClass) throws XmlRpcException {
            return factory;
        }

        private class myRequestProcessorFactory implements RequestProcessorFactory {

            public Object getRequestProcessor(XmlRpcRequest xmlRpcRequest) throws XmlRpcException {
             System.out.println("XML Server calling "+  classes.size()); 
             // return new Object();
               // System.out.println("method requested: "+);
              //  xmlRpcRequest.getConfig().
                return classes.get(xmlRpcRequest.getMethodName().substring(0,xmlRpcRequest.getMethodName().indexOf(".")));
            }
        }
    }
    

    public XmlrpcServer(int port) throws IOException, ServletException {
        
        
        this.port = port;
        System.out.println("Starting Servlet");
        servlet = new MyServlet();
  //  System.out.println("Servlet name:"+servlet.getServletName()+" info:"+servlet.getServletInfo());
   

        server = new ServletWebServer(servlet, port);
      //  XmlRpcServer s = server.getXmlRpcServer();
     //   mapping = new PropertyHandlerMapping();
     //  s.setHandlerMapping(mapping);

//        server.start();
//         server.addHandler(name, object);
//         server.start();


//          XmlRpcServerConfigImpl serverConfig =
//                (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
//            serverConfig.setEnabledForExtensions(true);
//           serverConfig.setContentLengthOptional(false);
//server.
    //    object o = new org.apache.xmlrpc.metadata.XmlRpcSystemImpl();
        
 
        
       server.start();
        System.out.println("Starting XMLRPC Server");

    }

    /**
     * Add an object to the server.
     */
    public void add(String name, Object object) {
        System.err.println("Adding Handler: " + name);
        try {
         ((myRequestProcessorFactoryFactory)mapping.getRequestProcessorFactoryFactory()).addObject(name, object);
            mapping.addHandler(name, object.getClass());
          //  server.start();
        } catch (Exception e) {
            System.err.println("JavaServer: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Remove a handler object that was previously registered with this server.
     */
    public void remove(String name) {
        mapping.removeHandler(name);
    }

    /**
     * Add an IP address to the list of accepted clients.
     */
    public void accept(String ip) {
        server.acceptClient(ip);
    }

    /**
     * Add an IP address to the list of denied clients.
     */
    public void deny(String ip) {
        server.denyClient(ip);
    }

    /**
     * Switch client filtering on/off.
     */
    public void filter(boolean value) {
        server.setParanoid(value);
    }

    /**
     * Shut down the server. Stops listening the server port.
     */
    public void stop() {
        server.shutdown();
    }

    /**
     * Get the IP of the localhost.
     */
    public String ip() {
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
