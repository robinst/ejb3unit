package com.bm.ejb3metadata.annotations.helper.bean;

import org.objectweb.asm.Type;

import com.bm.ejb3metadata.annotations.JMethod;
import com.bm.ejb3metadata.annotations.exceptions.ResolverException;
import com.bm.ejb3metadata.annotations.metadata.ClassAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.EjbJarAnnotationMetadata;
import com.bm.ejb3metadata.annotations.metadata.MethodAnnotationMetadata;

/**
 * This class adds method meta data to bean class from the super class.<br>
 * TODO: Try to analyze super class from a super classloader if not found in the
 * current ejb-jar
 * @see <a href="http://www.jcp.org/en/jsr/detail?id=220">EJB 3.0 Spec ?4.6.2</a><br><p>
 *      A super class can't be a bean class (stateless, stateful, etc) so the
 *      method metadata don't need to be cloned</p>
 * @author Florent Benoit
 */
public final class InheritanceMethodResolver {

    /**
     * java.lang.object internal name.
     */
    private static final String JAVA_LANG_OBJECT = Type.getInternalName(Object.class);

    /**
     * Helper class, no public constructor.
     */
    private InheritanceMethodResolver() {

    }

    /**
     * Found all method meta data of the super class and adds them to the class
     * being analyzed.
     * @param classAnnotationMetadata class to analyze
     * @throws ResolverException if the super class in not in the given ejb-jar
     */
    public static void resolve(final ClassAnnotationMetadata classAnnotationMetadata) throws ResolverException {
        addMethodMetadata(classAnnotationMetadata, classAnnotationMetadata);
    }

    /**
     * Adds method meta data on the first class by iterating on the second given
     * class.
     * @param beanclassAnnotationMetadata class where to add method metadata
     * @param visitingClassAnnotationMetadata takes method metadata from super
     *        class of the given class
     * @throws ResolverException if a super class metadata is not found from
     *         ejb-jar
     */
    private static void addMethodMetadata(final ClassAnnotationMetadata beanclassAnnotationMetadata,
            final ClassAnnotationMetadata visitingClassAnnotationMetadata) throws ResolverException {

        // Analyze super classes of the given class
        String superClass = visitingClassAnnotationMetadata.getSuperName();

        if (superClass != null) {

            // If super class is java.lang.Object, break the loop
            if (superClass.equals(JAVA_LANG_OBJECT)) {
                return;
            }

            // Get parent meta-data
            EjbJarAnnotationMetadata ejbJarAnnotationMetadata = beanclassAnnotationMetadata.getEjbJarAnnotationMetadata();

            // Get meta data of the super class
            ClassAnnotationMetadata superClassMetadata = ejbJarAnnotationMetadata.getClassAnnotationMetadata(superClass);

            if (superClassMetadata == null) {
                // TODO : I18n
                throw new ResolverException("The class " + beanclassAnnotationMetadata + " extends the class " + superClass
                        + "but this class seems to be outside of the ejb-jar");
            }

            // Takes method metadata of the super class and adds them to the
            // bean class
            // Note : the flag inherited is set to true
            for (MethodAnnotationMetadata methodAnnotationMetadata : superClassMetadata.getMethodAnnotationMetadataCollection()) {
                // check that the method has not be redefined
                JMethod method = methodAnnotationMetadata.getJMethod();

                // Add only if it is not present.
                if (beanclassAnnotationMetadata
                        .getMethodAnnotationMetadata(method) == null) {
                    // Add a clone of the method to bean class
                    MethodAnnotationMetadata clonedMethodAnnotationMetadata = (MethodAnnotationMetadata) methodAnnotationMetadata
                            .clone();
                    // set new class linked to this method metadata
                    clonedMethodAnnotationMetadata
                            .setClassAnnotationMetadata(beanclassAnnotationMetadata);
                    beanclassAnnotationMetadata
                            .addMethodAnnotationMetadata(clonedMethodAnnotationMetadata);

                    // method is inherited
                    clonedMethodAnnotationMetadata.setInherited(true, superClassMetadata);

                    // lifecycle / aroundInvoke
                    if (clonedMethodAnnotationMetadata.isPostConstruct()) {
                        beanclassAnnotationMetadata.addPostConstructMethodMetadata(clonedMethodAnnotationMetadata);
                    }
                    if (clonedMethodAnnotationMetadata.isPreDestroy()) {
                        beanclassAnnotationMetadata.addPreDestroyMethodMetadata(clonedMethodAnnotationMetadata);
                    }
                    if (clonedMethodAnnotationMetadata.isPostActivate()) {
                        beanclassAnnotationMetadata.addPostActivateMethodMetadata(clonedMethodAnnotationMetadata);
                    }
                    if (clonedMethodAnnotationMetadata.isPrePassivate()) {
                        beanclassAnnotationMetadata.addPrePassivateMethodMetadata(clonedMethodAnnotationMetadata);
                    }
                    if (clonedMethodAnnotationMetadata.isAroundInvoke()) {
                        beanclassAnnotationMetadata.addAroundInvokeMethodMetadata(clonedMethodAnnotationMetadata);
                    }
                }
            }

            // Loop again
            addMethodMetadata(beanclassAnnotationMetadata, superClassMetadata);

        }
    }

}
