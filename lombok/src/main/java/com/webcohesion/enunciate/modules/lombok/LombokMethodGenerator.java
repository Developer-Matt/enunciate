package com.webcohesion.enunciate.modules.lombok;

import com.webcohesion.enunciate.javac.decorations.DecoratedProcessingEnvironment;
import com.webcohesion.enunciate.javac.decorations.element.DecoratedExecutableElement;
import com.webcohesion.enunciate.javac.decorations.element.DecoratedTypeElement;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

/**
 * @author Tomasz Kalkosiński
 */
public class LombokMethodGenerator {

    private DecoratedTypeElement decoratedTypeElement;
    private DecoratedProcessingEnvironment env;

    public LombokMethodGenerator(DecoratedTypeElement decoratedTypeElement, DecoratedProcessingEnvironment env) {
        this.decoratedTypeElement = decoratedTypeElement;
        this.env = env;
    }

    public void generateLombokGettersAndSetters() {
        List<? extends VariableElement> fields = decoratedTypeElement.getFields();
        for (VariableElement field : fields) {
            System.out.println("Field " + field.getSimpleName() + " of class " + field.getClass().getSimpleName());

//            DecoratedVariableElement decoratedVariableElement = (DecoratedVariableElement) field;
            if (shouldGenerateGetter(field)) {
                System.out.println("Adding getter to " + decoratedTypeElement + " " + System.identityHashCode(decoratedTypeElement) + " for field " + field);
                decoratedTypeElement.getMethods().add(new LombokPropertyDecoratedExecutableElement(field, env, true));
            }
            if (shouldGenerateSetter(field)) {
                System.out.println("Adding setter to " + decoratedTypeElement + " " + System.identityHashCode(decoratedTypeElement) + " for field " + field);
                decoratedTypeElement.getMethods().add(new LombokPropertyDecoratedExecutableElement(field, env, false));
            }
        }
    }

    private boolean shouldGenerateGetter(Element field) {
        String fieldSimpleName = field.getSimpleName().toString();
        for (ExecutableElement method : decoratedTypeElement.getMethods()) {
            DecoratedExecutableElement decoratedMethod = (DecoratedExecutableElement) method;
            if (decoratedMethod.getPropertyName() != null && decoratedMethod.getPropertyName().equals(fieldSimpleName) && decoratedMethod.isGetter()) {
                return false;
            }
        }

        if (field.getAnnotation(Getter.class) != null) {
            return true;
        }

        if (decoratedTypeElement.getAnnotation(Data.class) != null) {
            return true;
        }

        return false;
    }

    private boolean shouldGenerateSetter(Element field) {
        String fieldSimpleName = field.getSimpleName().toString();
        for (ExecutableElement method : decoratedTypeElement.getMethods()) {
            DecoratedExecutableElement decoratedMethod = (DecoratedExecutableElement) method;
            if (decoratedMethod.getPropertyName() != null && decoratedMethod.getPropertyName().equals(fieldSimpleName) && decoratedMethod.isSetter()) {
                return false;
            }
        }

        if (field.getAnnotation(Setter.class) != null) {
            return true;
        }

        if (decoratedTypeElement.getAnnotation(Data.class) != null) {
            return true;
        }

        return false;
    }
}