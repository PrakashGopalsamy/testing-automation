package com.infosys.test.automation.utils;


import com.infosys.test.automation.reportgenerator.ReportGenerator;

import java.lang.reflect.InvocationTargetException;
import java.util.ServiceLoader;

public class ReportGeneratorUtils {
    public static Class<?> getGeneratorClass(String reportFormat) throws ClassNotFoundException {
        Class<?> generatorClass = null;
        ServiceLoader<ReportGenerator> reportGenerators=ServiceLoader.load(ReportGenerator.class, ReportGeneratorUtils.class.getClass().getClassLoader());
        for(ReportGenerator reportGenerator: reportGenerators){
//            System.out.println("Service Name : "+service.providerName());
            if (reportFormat.equalsIgnoreCase(reportGenerator.reportFormat())){
                generatorClass = reportGenerator.getClass();
                break;
            }
        }
        if (generatorClass == null){
            generatorClass = Class.forName(reportFormat);
        }
        return generatorClass;
    }

    public static ReportGenerator createReportGenerator(Class<?> generatorClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (ReportGenerator) generatorClass.getConstructor().newInstance();
    }

    public static ReportGenerator createReportGenerator(String reportFormat) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> generatorClass = getGeneratorClass(reportFormat);
        ReportGenerator generator = createReportGenerator(generatorClass);
        return generator;
    }
}
