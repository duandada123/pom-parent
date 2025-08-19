package com.sinogale.codegen.updater;

import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.sinogale.codegen.BaseGenProcessor;
import com.squareup.javapoet.*;
import com.squareup.javapoet.TypeSpec.Builder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@AutoService(Processor.class)
public class GenUpdaterProcessor extends BaseGenProcessor<GenUpdater> {
    private static final String PREFIX = "Base";
    private static final String SUFFIX = "Updater";

    public GenUpdaterProcessor() {
        super(GenUpdater.class);
    }

    @Override
    protected void genCode(TypeElement e, RoundEnvironment roundEnvironment) {
        Set<VariableElement> variableElements = this.filterFields(e.getEnclosedElements(), (p) -> {
            return Objects.isNull(p.getAnnotation(IgnoreUpdater.class));
        });
        String packageName = e.getAnnotation(GenUpdater.class).pkgName();
        String pathStr = e.getAnnotation(GenUpdater.class).sourcePath();
        String className = "Base" + e.getSimpleName() + "Updater";
        String sourceName = e.getSimpleName() + "Updater";
        boolean override = e.getAnnotation(GenUpdater.class).overrideSource();
        Builder typeSpecBuilder = TypeSpec.classBuilder(className).addModifiers(new Modifier[]{Modifier.PUBLIC}).addAnnotation(ApiModel.class).addAnnotation(Data.class);
        Iterator var10 = variableElements.iterator();

        while(var10.hasNext()) {
            VariableElement ve = (VariableElement)var10.next();
            if (ve.asType().getKind().isPrimitive()) {
                this.messager.printMessage(Kind.ERROR, "原始类型不能生成updater请使用包装类", e);
                return;
            }

            FieldSpec.Builder fieldSpec = FieldSpec.builder(TypeName.get(ve.asType()), ve.getSimpleName().toString(), new Modifier[]{Modifier.PRIVATE}).addAnnotation(AnnotationSpec.builder(ApiModelProperty.class).addMember("value", "$S", new Object[]{this.getFieldDesc(ve)}).build());
            String fieldName = ve.getSimpleName().toString().substring(0, 1).toUpperCase() + ve.getSimpleName().toString().substring(1);
            MethodSpec.Builder getMethod = MethodSpec.methodBuilder("get" + fieldName).returns(TypeName.get(ve.asType())).addModifiers(new Modifier[]{Modifier.PUBLIC}).addStatement("return $L", new Object[]{ve.getSimpleName().toString()});
            MethodSpec.Builder setMethod = MethodSpec.methodBuilder("set" + fieldName).returns(Void.TYPE).addModifiers(new Modifier[]{Modifier.PUBLIC}).addParameter(TypeName.get(ve.asType()), ve.getSimpleName().toString(), new Modifier[0]).addStatement("this.$L = $L", new Object[]{ve.getSimpleName().toString(), ve.getSimpleName().toString()});
            typeSpecBuilder.addMethod(setMethod.build());
            typeSpecBuilder.addMethod(getMethod.build());
            typeSpecBuilder.addField(fieldSpec.build());
        }

        CodeBlock.Builder builder = CodeBlock.builder();
        Iterator var17 = variableElements.iterator();

        while(var17.hasNext()) {
            VariableElement ve = (VariableElement)var17.next();
            builder.addStatement("$T.ofNullable($L()).ifPresent(v -> param.$L(v))", new Object[]{Optional.class, "get" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, ve.getSimpleName().toString()), "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, ve.getSimpleName().toString())});
        }

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("update" + e.getSimpleName()).addModifiers(new Modifier[]{Modifier.PUBLIC}).addParameter(TypeName.get(e.asType()), "param", new Modifier[0]).addCode(builder.build()).returns(Void.TYPE);
        typeSpecBuilder.addMethod(methodBuilder.build());
        typeSpecBuilder.addField(FieldSpec.builder(ClassName.get(Long.class), "id", new Modifier[]{Modifier.PRIVATE}).build());
        Builder source = TypeSpec.classBuilder(sourceName).superclass(ClassName.get(packageName, className, new String[0])).addModifiers(new Modifier[]{Modifier.PUBLIC}).addAnnotation(ApiModel.class).addAnnotation(Data.class);
        this.genJavaFileToTarget(packageName, typeSpecBuilder);
        this.genJavaFile(packageName, pathStr, source, override);
    }
}
