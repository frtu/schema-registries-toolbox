package com.github.frtu.spring.annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Search for all classes that has certain annotation at class level using {@link #findCandidateComponents(Class)}.
 * Can also limit research under a specific basePackage using {@link #findCandidateComponents(Class, String)}
 *
 * @author Frédéric TU
 * @see <a href="https://github.com/frtu/SimpleToolbox/blob/master/SimpleInfra/src/main/java/com/github/frtu/simple/infra/reflect/ClassPathScanningAnnotationProvider.java">Moved from old project SimpleToolbox</a>
 * @since 1.1.0
 */
public class ClassPathScanningAnnotationProvider extends ClassPathScanningCandidateComponentProvider {
    public ClassPathScanningAnnotationProvider() {
        super(false);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent();
    }

    public static Set<BeanDefinition> findCandidateComponents(Class<? extends Annotation> annotationType) {
        return findCandidateComponents(annotationType, "");
    }

    public static Set<BeanDefinition> findCandidateComponents(Class<? extends Annotation> annotationType, String basePackage) {
        ClassPathScanningAnnotationProvider classPathScanningCandidateComponentProvider = new ClassPathScanningAnnotationProvider();
        classPathScanningCandidateComponentProvider.addIncludeFilter(new AnnotationTypeFilter(annotationType));

        Set<BeanDefinition> findCandidateComponents = classPathScanningCandidateComponentProvider.findCandidateComponents(basePackage);
        return findCandidateComponents;
    }
}