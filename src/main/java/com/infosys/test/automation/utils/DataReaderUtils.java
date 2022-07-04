package com.infosys.test.automation.utils;

import com.infosys.test.automation.connectors.DataReader;
import com.infosys.test.automation.connectors.ReaderProvider;
import com.infosys.test.automation.dto.CondConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

public class DataReaderUtils {

    public static Class<?> getConnectorClass(String readerType) throws ClassNotFoundException {
        Class<?> readerClass = null;
        ServiceLoader<ReaderProvider> readers=ServiceLoader.load(ReaderProvider.class, DataReaderUtils.class.getClass().getClassLoader());
        for(ReaderProvider reader: readers){
//            System.out.println("Service Name : "+service.providerName());
            if (readerType.equalsIgnoreCase(reader.readerName())){
                readerClass = reader.getClass();
                break;
            }
        }
        if (readerClass == null){
            readerClass = Class.forName(readerType);
        }
        return readerClass;
    }

    public static DataReader createReader(Class<?> readerClass,
                                          String readerName,
                                          Properties readerProperties,
                                          List<String> parentRecords,
                                          CondConfig filterCond,
                                          CondConfig joinCond) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
       return (DataReader) readerClass.getConstructor(String.class,Properties.class,List.class, CondConfig.class, CondConfig.class).newInstance(readerName,readerProperties,parentRecords,filterCond,joinCond);
    }

    public static DataReader createReader(String readerType,
                                         String readerName,
                                         Properties readerProperties,
                                         List<String> parentRecords,
                                         CondConfig filterCond,
                                         CondConfig joinCond) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> readerClass = getConnectorClass(readerType);
        DataReader reader = createReader(readerClass,readerName,readerProperties,parentRecords,filterCond,joinCond);
        return reader;
    }
}
