package com.infosys.test.automation.utils;

import com.infosys.test.automation.connectors.Connector;
import com.infosys.test.automation.connectors.ConnectorProvider;
import com.infosys.test.automation.dto.CondElement;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

public class ConnectorUtils {

    public static Class<?> getConnectorClass(String connectorType) throws ClassNotFoundException {
        Class<?> connectorClass = null;
        ServiceLoader<ConnectorProvider> services=ServiceLoader.load(ConnectorProvider.class, ConnectorUtils.class.getClass().getClassLoader());
        for(ConnectorProvider service: services){
//            System.out.println("Service Name : "+service.providerName());
            if (connectorType.equalsIgnoreCase(service.providerName())){
                connectorClass = service.getClass();
                break;
            }
        }
        if (connectorClass == null){
            connectorClass = Class.forName(connectorType);
        }
        return connectorClass;
    }

    public static Connector createConnector(Class<?> connectorClass,
                                            String connectorName,
                                            Properties connectorProperties,
                                            List<String> parentRecords,
                                            CondElement filterCond,
                                            CondElement joinCond) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
       return (Connector) connectorClass.getConstructor(String.class,Properties.class,List.class,CondElement.class,CondElement.class).newInstance(connectorName,connectorProperties,parentRecords,filterCond,joinCond);
    }

    public static Connector createConnetor(String connectorType,
                                           String connectorName,
                                           Properties connectorProperties,
                                           List<String> parentRecords,
                                           CondElement filterCond,
                                           CondElement joinCond) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> connectorClass = getConnectorClass(connectorType);
        Connector connector = createConnector(connectorClass,connectorName,connectorProperties,parentRecords,filterCond,joinCond);
        return connector;
    }
}
