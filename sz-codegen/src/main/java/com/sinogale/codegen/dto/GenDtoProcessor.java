package com.sinogale.codegen.dto;

import com.google.auto.service.AutoService;
import com.sinogale.codegen.BaseGenProcessor;
import com.squareup.javapoet.*;
import com.squareup.javapoet.TypeSpec.Builder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.time.LocalDateTime;
import java.util.*;

@AutoService(Processor.class)
public class GenDtoProcessor extends BaseGenProcessor<GenDto> {

    private static final String PREFIX = "Base";

    private static final String SUFFIX = "Dto";

    static final List<TypeName> dtoIgnoreFieldTypes = new ArrayList();

    public GenDtoProcessor() {
        super(GenDto.class);
    }

    @Override
    protected void genCode(TypeElement e, RoundEnvironment roundEnvironment) {
        Set<VariableElement> variableElements = this.filterFields(e.getEnclosedElements(), (p) -> {
            return Objects.isNull(p.getAnnotation(IgnoreDto.class)) && !this.dtoIgnore(p);
        });
        String packageName = e.getAnnotation(GenDto.class).pkgName();
        String pathStr = e.getAnnotation(GenDto.class).sourcePath();
        boolean override = e.getAnnotation(GenDto.class).overrideSource();
        String className = "Base" + e.getSimpleName() + "Dto";
        String sourceName = e.getSimpleName() + "Dto";
        Builder typeSpecBuilder = TypeSpec.classBuilder(className).addModifiers(new Modifier[]{Modifier.PUBLIC}).addAnnotation(ApiModel.class).addAnnotation(Data.class);
        Iterator var10 = variableElements.iterator();

        while (var10.hasNext()) {
            VariableElement ve = (VariableElement) var10.next();
            FieldSpec.Builder fieldSpec = FieldSpec.builder(TypeName.get(ve.asType()), ve.getSimpleName().toString(), new Modifier[]{Modifier.PRIVATE}).addAnnotation(AnnotationSpec.builder(ApiModelProperty.class).addMember("value", "$S", new Object[]{this.getFieldDesc(ve)}).build());
            String fieldName = ve.getSimpleName().toString().substring(0, 1).toUpperCase() + ve.getSimpleName().toString().substring(1);
            MethodSpec.Builder getMethod = MethodSpec.methodBuilder("get" + fieldName).returns(TypeName.get(ve.asType())).addModifiers(new Modifier[]{Modifier.PUBLIC}).addStatement("return $L", new Object[]{ve.getSimpleName().toString()});
            MethodSpec.Builder setMethod = MethodSpec.methodBuilder("set" + fieldName).returns(Void.TYPE).addModifiers(new Modifier[]{Modifier.PUBLIC}).addParameter(TypeName.get(ve.asType()), ve.getSimpleName().toString(), new Modifier[0]).addStatement("this.$L = $L", new Object[]{ve.getSimpleName().toString(), ve.getSimpleName().toString()});
            typeSpecBuilder.addField(fieldSpec.build());
            typeSpecBuilder.addMethod(setMethod.build());
            typeSpecBuilder.addMethod(getMethod.build());
        }

        Builder source = TypeSpec.classBuilder(sourceName).superclass(ClassName.get(packageName, className, new String[0])).addModifiers(new Modifier[]{Modifier.PUBLIC}).addAnnotation(ApiModel.class).addAnnotation(Data.class);
        this.genJavaFileToTarget(packageName, typeSpecBuilder);
        this.genJavaFile(packageName, pathStr, source, override);
    }

    private boolean dtoIgnore(Element ve) {
        return dtoIgnoreFieldTypes.contains(TypeName.get(ve.asType())) || ve.getModifiers().contains(Modifier.STATIC);
    }

    static {
        dtoIgnoreFieldTypes.add(TypeName.get(Date.class));
        dtoIgnoreFieldTypes.add(TypeName.get(LocalDateTime.class));
    }
}
