package com.sinogale.codegen.mapper;

import com.google.auto.service.AutoService;
import com.sinogale.codegen.BaseGenProcessor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class GenMapperProcessor extends BaseGenProcessor<GenMapper> {

    private static final String SUFFIX = "Mapper";

    public GenMapperProcessor() {
        super(GenMapper.class);
    }

    @Override
    protected void genCode(TypeElement e, RoundEnvironment roundEnvironment) {
        String className = e.getSimpleName() + "Mapper";
        String packageName = e.getAnnotation(GenMapper.class).pkgName();
        String pathStr = e.getAnnotation(GenMapper.class).sourcePath();
        boolean override = e.getAnnotation(GenMapper.class).overrideSource();
        TypeSpec.Builder typeSpecBuilder = TypeSpec.interfaceBuilder(className).addAnnotation(Mapper.class).addModifiers(new Modifier[]{Modifier.PUBLIC});
        FieldSpec instance = FieldSpec.builder(ClassName.get(packageName, className, new String[0]), "INSTANCE", new Modifier[0]).addModifiers(new Modifier[]{Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL}).initializer("$T.getMapper($T.class)", new Object[]{Mappers.class, ClassName.get(packageName, className, new String[0])}).build();
        typeSpecBuilder.addField(instance);
        this.genJavaFile(packageName, pathStr, typeSpecBuilder, override);
    }
}
