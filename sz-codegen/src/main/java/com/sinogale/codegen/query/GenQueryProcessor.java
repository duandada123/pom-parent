package com.sinogale.codegen.query;

import com.google.auto.service.AutoService;
import com.sinogale.codegen.BaseGenProcessor;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@AutoService(Processor.class)
public class GenQueryProcessor extends BaseGenProcessor<GenQuery> {

    public GenQueryProcessor() {
        super(GenQuery.class);
    }

    @Override
    protected void genCode(TypeElement e, RoundEnvironment roundEnvironment) {
        Set<VariableElement> variableElements = this.filterFields(e.getEnclosedElements(), (p) -> {
            return Objects.nonNull(p.getAnnotation(QueryItem.class));
        });
        String packageName = e.getAnnotation(GenQuery.class).pkgName();
        String pathStr = e.getAnnotation(GenQuery.class).sourcePath();
        boolean override = e.getAnnotation(GenQuery.class).overrideSource();
        String className = e.getSimpleName() + "Query";
        Builder typeSpecBuilder = TypeSpec.classBuilder(className).addModifiers(new Modifier[]{Modifier.PUBLIC}).addAnnotation(ApiModel.class).addAnnotation(Data.class);
        Iterator var9 = variableElements.iterator();

        while (var9.hasNext()) {
            VariableElement ve = (VariableElement) var9.next();
            FieldSpec.Builder fieldSpec = FieldSpec.builder(TypeName.get(ve.asType()), ve.getSimpleName().toString(), new Modifier[]{Modifier.PRIVATE}).addAnnotation(AnnotationSpec.builder(ApiModelProperty.class).addMember("value", "$S", new Object[]{this.getFieldDesc(ve)}).build());
            typeSpecBuilder.addField(fieldSpec.build());
        }

        this.genJavaFile(packageName, pathStr, typeSpecBuilder, override);
    }
}
