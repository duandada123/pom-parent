package com.sinogale.jpa.support;

import com.sinogale.common.exception.ValidationException;
import com.sinogale.common.model.ValidateResult;
import com.sinogale.common.validator.CreateGroup;
import com.sinogale.common.validator.UpdateGroup;
import com.sinogale.common.validator.ValidateGroup;
import com.google.common.base.Preconditions;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class BaseJpaService {
    private static final Logger log = LoggerFactory.getLogger(BaseJpaService.class);
    static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public BaseJpaService(){

    }

    public <T, ID> Updater<T, ID> doUpdate(CrudRepository<T, ID> repository) {
        return new Updater(repository);
    }

    public <T, ID> Creator<T, ID> doCreate(CrudRepository<T, ID> repository) {
        return new Creator(repository);
    }

    private <T> void doValidate(T t, Class<? extends ValidateGroup> group) {
        Set<ConstraintViolation<T>> constraintViolations = VALIDATOR.validate(t, new Class[]{group, Default.class});
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            List<ValidateResult> results = (List)constraintViolations.stream().map((cv) -> {
                return new ValidateResult(cv.getPropertyPath().toString(), cv.getMessage());
            }).collect(Collectors.toList());
            throw new ValidationException(results);
        }
    }
    public class Creator<T,ID>{
        private final CrudRepository<T,ID> repository;
        private T t;

        private Consumer<T>  successCallback = (t)->{
            BaseJpaService.log.info("save success");
        };

        public Creator(CrudRepository<T, ID> repository) {
            this.repository = repository;
        }

        private Consumer<? super Throwable> errorCallback = (e) -> {
            e.printStackTrace();
        };

        public Creator<T,ID> create(Supplier<T> supplier){
            this.t = supplier.get();
            return this;
        }

        public Creator<T,ID> init(Consumer<T> t){
            Preconditions.checkArgument(Objects.nonNull(t), "entity must supply");
            t.accept(this.t);
            return this;
        }
        public Creator<T, ID> sucCallback(Consumer<T> consumer) {
            this.successCallback = consumer;
            return this;
        }

        public Creator<T, ID> errorCallback(Consumer<? super Throwable> consumer) {
            this.errorCallback = consumer;
            return this;
        }

        public Optional<T> execute(){
            BaseJpaService.this.doValidate(this.t, CreateGroup.class);
            T save = Try.of(()->{
                return this.repository.save(this.t);
            }).onSuccess(this.successCallback).onFailure(this.errorCallback).getOrNull();
            return Optional.ofNullable(save);
        }
    }

    public class Updater<T, ID> {
        private final CrudRepository<T, ID> repository;
        private T entity;
        private Consumer<T> successCallback;
        private Consumer<? super Throwable> errorCallback;

        private Updater(CrudRepository<T, ID> repository) {
            this.successCallback = (t) -> {
                BaseJpaService.log.info(t.toString());
            };
            this.errorCallback = (e) -> {
                BaseJpaService.log.error(e.getMessage());
            };
            this.repository = repository;
        }

        public Updater<T, ID> loadById(ID id) {
            Preconditions.checkArgument(Objects.nonNull(id), "id is null");
            Optional<T> loadEntity = this.repository.findById(id);
            this.entity = loadEntity.orElse(this.entity);
            return this;
        }

        public Updater<T, ID> load(Supplier<T> supplier) {
            this.entity = supplier.get();
            return this;
        }

        public Updater<T, ID> update(Consumer<T> consumer) {
            Preconditions.checkArgument(Objects.nonNull(this.entity), "entity is null");
            consumer.accept(this.entity);
            return this;
        }

        public Updater<T, ID> sucCallback(Consumer<T> consumer) {
            this.successCallback = consumer;
            return this;
        }

        public Updater<T, ID> errorCallback(Consumer<? super Throwable> consumer) {
            this.errorCallback = consumer;
            return this;
        }

        public Optional<T> execute() {
            BaseJpaService.this.doValidate(this.entity, UpdateGroup.class);
            T save = Try.of(() -> {
                return this.repository.save(this.entity);
            }).onSuccess(this.successCallback).onFailure(this.errorCallback).getOrNull();
            return Optional.ofNullable(save);
        }
    }
}
