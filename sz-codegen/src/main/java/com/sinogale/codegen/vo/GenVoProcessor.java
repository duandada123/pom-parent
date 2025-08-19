package com.sinogale.codegen.vo;

import com.google.auto.service.AutoService;
import com.sinogale.codegen.BaseGenProcessor;
import com.sinogale.common.annotation.TypeConverter;
import com.squareup.javapoet.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@AutoService(Processor.class)
public class GenVoProcessor extends BaseGenProcessor<GenVo> {
    private static final String PREFIX = "Base";
    private static final String SUFFIX = "Vo";

    public GenVoProcessor() {
        super(GenVo.class);
    }

    @Override
    protected void genCode(TypeElement e, RoundEnvironment roundEnvironment) {
        Set<VariableElement> variableElements = this.filterFields(e.getEnclosedElements(), (p) -> {
            return Objects.isNull(p.getAnnotation(IgnoreVo.class)) && !this.voIgnore(p);
        });
        String packageName = e.getAnnotation(GenVo.class).pkgName();
        String pathStr = e.getAnnotation(GenVo.class).sourcePath();
        boolean override = e.getAnnotation(GenVo.class).overrideSource();
        boolean isJpa = e.getAnnotation(GenVo.class).jpa();
        String className = "Base" + e.getSimpleName() + "Vo";
        String sourceName = e.getSimpleName() + "Vo";
        TypeSpec.Builder typeSpecBuilder;
        if (isJpa) {
            typeSpecBuilder = TypeSpec.classBuilder(className).superclass(AbstractBaseJpaVo.class).addModifiers(new Modifier[]{Modifier.PUBLIC}).addAnnotation(ApiModel.class).addAnnotation(Data.class);
        } else {
            typeSpecBuilder = TypeSpec.classBuilder(className).superclass(AbstractBaseJdbcVo.class).addModifiers(new Modifier[]{Modifier.PUBLIC}).addAnnotation(ApiModel.class).addAnnotation(Data.class);
        }

        MethodSpec.Builder constructorSpecBuilder = MethodSpec.constructorBuilder().addParameter(TypeName.get(e.asType()), "source", new Modifier[0]).addModifiers(new Modifier[]{Modifier.PUBLIC});
        constructorSpecBuilder.addStatement("super(source)", new Object[0]);
        TypeSpec.Builder source = TypeSpec.classBuilder(sourceName).superclass(ClassName.get(packageName, className, new String[0])).addModifiers(new Modifier[]{Modifier.PUBLIC}).addMethod(MethodSpec.constructorBuilder().addModifiers(new Modifier[]{Modifier.PUBLIC}).build()).addMethod(constructorSpecBuilder.build()).addAnnotation(ApiModel.class).addAnnotation(Data.class);
        Iterator var13 = variableElements.iterator();

        while(var13.hasNext()) {
            VariableElement ve = (VariableElement)var13.next();
            TypeName typeName;
            if (Objects.nonNull(ve.getAnnotation(TypeConverter.class))) {
                typeName = ClassName.bestGuess(ve.getAnnotation(TypeConverter.class).toTypeFullName());
            } else {
                typeName = TypeName.get(ve.asType());
            }

            FieldSpec.Builder fieldSpec = FieldSpec.builder(typeName, ve.getSimpleName().toString(), new Modifier[]{Modifier.PRIVATE}).addAnnotation(AnnotationSpec.builder(ApiModelProperty.class).addMember("value", "$S", new Object[]{this.getFieldDesc(ve)}).build());
            typeSpecBuilder.addField(fieldSpec.build());
            String fieldName = ve.getSimpleName().toString().substring(0, 1).toUpperCase() + ve.getSimpleName().toString().substring(1);
            MethodSpec.Builder getMethod = MethodSpec.methodBuilder("get" + fieldName).returns(TypeName.get(ve.asType())).addModifiers(new Modifier[]{Modifier.PUBLIC}).addStatement("return $L", new Object[]{ve.getSimpleName().toString()});
            MethodSpec.Builder setMethod = MethodSpec.methodBuilder("set" + fieldName).returns(Void.TYPE).addModifiers(new Modifier[]{Modifier.PUBLIC}).addParameter(TypeName.get(ve.asType()), ve.getSimpleName().toString(), new Modifier[0]).addStatement("this.$L = $L", new Object[]{ve.getSimpleName().toString(), ve.getSimpleName().toString()});
            constructorSpecBuilder.addStatement("this.set$L(source.get$L())", new Object[]{fieldName, fieldName});
            typeSpecBuilder.addMethod(getMethod.build());
            typeSpecBuilder.addMethod(setMethod.build());
        }

        typeSpecBuilder.addMethod(MethodSpec.constructorBuilder().addModifiers(new Modifier[]{Modifier.PROTECTED}).build());
        typeSpecBuilder.addMethod(constructorSpecBuilder.build());
        this.genJavaFileToTarget(packageName, typeSpecBuilder);
        this.genJavaFile(packageName, pathStr, source, override);
    }

    private boolean voIgnore(Element p) {
        return false;
    }
}